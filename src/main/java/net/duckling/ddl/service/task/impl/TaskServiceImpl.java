/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package net.duckling.ddl.service.task.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskItem;
import net.duckling.ddl.service.task.TaskItemShare;
import net.duckling.ddl.service.task.TaskService;
import net.duckling.ddl.service.task.TaskTaker;
import net.duckling.ddl.service.task.UserProcess;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.CommonUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




/**@author lvly
 * @since 2012-6-8
 * 对任务进行操作的基本服务类
 * */
@Service("TaskService")
public class TaskServiceImpl implements TaskService {
    private static final Logger LOGGER = Logger.getLogger(TaskService.class);
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private TeamService teamService;

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }
	
	@Override
	public List<Task> getTasksCreateByMe(String currentUID, String teamCode) {
		List<Task> tasks=taskDAO.getTasksByUID(currentUID, teamCode);
		if(tasks==null){
			return null;
		}
		for(int i=0;i<tasks.size();i++){
			Task task=tasks.get(i);
			task.setTakers(taskDAO.getTakers(task.getTaskId()+""));
		}
		return tasks;
	}
	@Override
	public List<Task> getTasksByImTaker(String currentUID, String teamCode) {
		List<Task> tasks=taskDAO.getTasksByImTaker(currentUID, teamCode);
		if(tasks==null){
			return null;
		}
		for(int i=0;i<tasks.size();i++){
			Task task=tasks.get(i);
			task.setTakers(taskDAO.getTakers(task.getTaskId()+""));
		}
		return tasks;
	}
	@Override
	public List<Task> getFinishedTasks(String teamCode,String userId) {
		List<Task> tasks= taskDAO.getTasksByHistory(teamCode,userId);
		if(tasks==null){
			return null;
		}
		for(int i=0;i<tasks.size();i++){
			Task task=tasks.get(i);
			task.setTakers(taskDAO.getTakers(task.getTaskId()+""));
		}
		return tasks;
	}
	@Override
	public Task getTask(String taskId) {
		Task task=taskDAO.getTask(taskId);
		task.setTakers(taskDAO.getTakers(taskId));
		task.setItems(taskDAO.getItems(taskId));
		task.setUserProcess(getAllUserProcess(taskId));
		return task;
	}

    @Override
    public Task getTaskWithStatus(String taskId, String currentUID) {
        Task task = taskDAO.getTask(taskId);
        task.setTakers(taskDAO.getTakers(taskId));
        task.setItems(taskDAO.getItemsWithStatus(taskId, currentUID));
        task.setUserProcess(getAllUserProcess(taskId));
        return task;
    }

    @Override
    public void updateTask(Task task) {
        taskDAO.updateTask(task);

    }

    @Override
    public void updateTakers(Task task, String takerListStrNew, String taskType, String noticeType) {
        Team team = teamService.getTeamByName(task.getTeamId());
        int tid = team.getId();
        // 原来的接受者队列
        List<TaskTaker> takersOrg = task.getTakers();
        // 新传过来的接受者队列
        List<TaskTaker> takersDest = getListFromArray(takerListStrNew.split(","), task.getTaskId());
        List<TaskTaker> needDelete = new ArrayList<TaskTaker>();
        List<TaskTaker> needAdd = new ArrayList<TaskTaker>();
        if (takersDest.size() == 0 && takersOrg.size() != 0) {
            needDelete = takersOrg;
        } else if (takersOrg.size() == 0 && takersDest.size() != 0) {
            needAdd = takersDest;
        } else if (takersOrg.size() == 0 && takersDest.size() == 0) {
            return;
        } else {
            // 多的挑出来
            loop: for (TaskTaker org : takersOrg) {
                for (TaskTaker dest : takersDest) {
                    if (org.equals(dest)) {
                        continue loop;
                    }

                }
                needDelete.add(org);
                // noticeD&S 踢出去的人给下通知
                sendNotice(tid, org, task, noticeType, DAction.DELETE_TAKER, org.getUserIDStr());
            }
            // 少的挑出来
            loop: for (TaskTaker dest : takersDest) {
                for (TaskTaker org : takersOrg) {
                    if (org.equals(dest)) {
                        continue loop;
                    }
                }
                needAdd.add(dest);
                // noticeD&S 新加的人给下通知
                sendNotice(tid, dest, task, noticeType, DAction.PLUS_TAKER, dest.getUserIDStr());
            }
        }
        taskDAO.deleteTakers(getIds(needDelete));
        taskDAO.addTakers(needAdd);
        if (TYPE_INDEPENDENT.equals(taskType)) {
            taskDAO.deleteTakerItemRefByUID(getUIds(needDelete), task.getTaskId());
            taskDAO.addTakersItemRef(needAdd, getIdsFromItemList(task.getItems()), task.getTaskId());

        }
    }
	/**从队列中剥离出来userId
	 * @param takers 接受者队列
	 * @return id[]  taker_id
	 * */
	private String[] getUIds(List<TaskTaker> takers){
		String ids[]=new String[takers.size()];
		int index=0;
		for(TaskTaker taker:takers){
			ids[index++]=taker.getUserIDStr();
		}
		return ids;
	}

	/**从队列中剥离出来id
	 * @param takers 接受者队列
	 * @return id[]  taker_id
	 * */
	private int[] getIds(List<TaskTaker> takers){
		int ids[]=new int[takers.size()];
		int index=0;
		for(TaskTaker taker:takers){
			ids[index++]=taker.getTakerId();
		}
		return ids;
	}
    @Override
    public void addItems(String[] needAddItemsContent, String taskId) {
        if (CommonUtil.isNullArray(needAddItemsContent)) {
            return;
        }
        int[] ids = taskDAO.addItems(needAddItemsContent, taskId);
        Task task = getTask(taskId);

        taskDAO.addTakersItemRef(task.getTakers(), ids, task.getTaskId());

    }

    @Override
    public void updateItems(String[] ids, String[] modifyContent) {
        if (CommonUtil.isNullArray(modifyContent)) {
            return;
        }
        taskDAO.updateItems(ids, modifyContent);
    }

    @Override
    public void deleteItems(String[] ids) {
        if (CommonUtil.isNullArray(ids)) {
            return;
        }
        taskDAO.deleteItems(ids);
        taskDAO.deleteItemRefByItemId(ids);

    }

    @Override
    public int addTask(Task task) {
        return taskDAO.addTask(task);
    }
	/**只抽取ID，从taskItem列表中
	 * @param list TaskItem列表
	 * */
	private int[] getIdsFromItemList(List<TaskItem> list){
		int ids[]=new int[list.size()];
		int index=0;
		for(TaskItem item:list){
			ids[index]=item.getItemId();
			index++;
		}
		return ids;
	}
    @Override
    public void addTakers(String[] userIds, String taskId) {
        if (CommonUtil.isNullArray(userIds)) {
            return;
        }
        taskDAO.addTakers(getListFromArray(userIds, Integer.valueOf(taskId)));
        Task task = getTask(taskId);
        taskDAO.addTakersItemRef(task.getTakers(), getIdsFromItemList(task.getItems()), task.getTaskId());

    }
	/**通过用户Id和任务Id构造出需要持久化的taker队列
	 * @param userIds 用户id队列
	 * @param taskId  任务Id
	 * @return result List<TaskTaker>队列，不包含Id，待持久化
	 * */
	private List<TaskTaker> getListFromArray(String[] userIds,int taskId){
		List<TaskTaker> result=new ArrayList<TaskTaker>();
		if(CommonUtil.isNullArray(userIds)){
			return result;
		}
		for(String id:userIds){
			TaskTaker taker=new TaskTaker();
			taker.setTaskId(taskId);
			taker.setUserId(id);
			taker.setReadStatus(TaskTaker.READ_STATUS_UNREAD);
			result.add(taker);
		}
		return result;
	}
    @Override
    public Map<Integer, String> getProcess(String taskId) {
        return taskDAO.getProcess(taskId);
    }

    @Override
    public boolean updateRefStatus(String[] undoIds, String[] doingIds, String[] finishIds, String userId) {
        boolean flag = true;
        if (!CommonUtil.isNullArray(undoIds)) {
            flag &= taskDAO.updateRefStatus(undoIds, userId, Task.STATUS_UNDO);
        }
        if (!CommonUtil.isNullArray(doingIds)) {
            flag &= taskDAO.updateRefStatus(doingIds, userId, Task.STATUS_DOING);
        }
        if (!CommonUtil.isNullArray(finishIds)) {
            flag &= taskDAO.updateRefStatus(finishIds, userId, Task.STATUS_FINISH);
        }
        return flag;

    }

    @Override
    public boolean isIndependentTaskOver(String taskId) {
        List<String> statuss = taskDAO.getRefByTaskId(taskId);
        for (String status : statuss) {
            if (!TaskTaker.STATUS_FINISH.equals(status)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void deleteTask(String taskId) {
        taskDAO.deleteTask(taskId);
        taskDAO.deleteItemsByTaskId(taskId);
    }

    // share

    @Override
    public Task getShareTask(String taskId) {
        if (CommonUtil.isNullStr(taskId)) {
            LOGGER.error("why the taskId is Null!!!");
        }
        Task task = taskDAO.getTask(taskId);
        task.setItems(taskDAO.getShareItems(taskId));
        task.setTakers(taskDAO.getTakers(taskId));
        return task;
    }

    @Override
    public void addShareItems(String[] needAddItemsContents, String taskId) {
        taskDAO.addShareItems(needAddItemsContents, taskId);

    }

    @Override
    public void updateShareItems(String[] ids, String[] modifyContents) {
        taskDAO.updateShareItems(ids, modifyContents);

    }

    @Override
    public void deleteShareItems(String[] ids) {
        taskDAO.deleteShareItems(ids);
    }

    @Override
    public List<TaskItemShare> getShareItems(String taskId) {
        return taskDAO.getShareItems(taskId);
    }

    @Override
    public boolean updateShareItemsStatus(String[] undoIds, String[] doingIds, String[] finishIds, String userId) {
        boolean flag = true;
        if (!CommonUtil.isNullArray(undoIds)) {
            flag &= taskDAO.updateShareItemStatus(undoIds, Task.STATUS_UNDO, "");
        }
        if (!CommonUtil.isNullArray(doingIds)) {
            flag &= taskDAO.updateShareItemStatus(doingIds, Task.STATUS_DOING, userId);
        }
        if (!CommonUtil.isNullArray(finishIds)) {
            flag &= taskDAO.updateShareItemStatus(finishIds, Task.STATUS_FINISH, userId);
        }
        return flag;
    }

    @Override
    public boolean isShareTaskOver(String taskId) {
        List<TaskItemShare> items = taskDAO.getShareItems(taskId);
        for (TaskItemShare share : items) {
            if (!TaskTaker.STATUS_FINISH.equals(share.getStatus())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void deleteShareTask(String taskId) {
        taskDAO.deleteTask(taskId);
        taskDAO.deleteShareItemsByTaskId(taskId);
    }

    public void sendNotice(int tid, List<TaskTaker> takers, Task task, String noticeType, String action, String actor) {
        eventDispatcher.sendTaskActionEvent(tid, takers, task, action, actor, noticeType);
    }

    /** 重载方法，转换一下参数 */
    public void sendNotice(int tid, TaskTaker taker, Task task, String noticeType, String action, String actor) {
        eventDispatcher.sendTaskActionEvent(tid, CommonUtil.getList(taker), task, action, actor, noticeType);
    }

    @Override
    public List<TaskTaker> getTakers(String taskId) {
        return taskDAO.getTakers(taskId);
    }

    @Override
    public List<UserProcess> getAllUserProcess(String taskId) {
        return taskDAO.getAllUserProcess(taskId);
    }

    @Override
    public void setReadStatus(String taskId, String userId) {
        taskDAO.setReadStatus(taskId, userId);
    }

    @Override
    public boolean isUserDone(Task task, String uid) {
        if (task == null) {
            return false;
        }
        if (task.getTaskType().equals(TYPE_INDEPENDENT)) {
            return taskDAO.isUserDone(task.getTaskId(), uid);
        }
        return false;

    }
}
