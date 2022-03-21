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
package net.duckling.ddl.service.task;

import java.util.List;
import java.util.Map;



/**
 * @author lvly
 * @since  2012-6-8
 * */
public interface TaskService {
    String TYPE_SHARE="share";
    String TYPE_INDEPENDENT="independent";

    /**查询我创建的任务列表
     * @param currentUID 当前用户ID
     * @param teamCode   团队ID
     * */
    List<Task> getTasksCreateByMe(String currentUID, String teamCode);

    /**查询我接受到的任务列表
     *
     * @param currentUID 当前用户ID
     * @param teamCode   团队ID
     * */
    List<Task> getTasksByImTaker(String currentUID, String teamCode);

    /**查看所有的任务用于历史查看
     * @param teamCode   团队ID
     * @param userId    用户id
     * */
    List<Task> getFinishedTasks(String teamCode,String userId);

    /**获得任务，并自动装填item和taker到临时变量
     * @param taskId    任务ID
     * */
    Task getTask(String taskId);

    /**获得任务，并自动装填有状态item和taker到临时变量
     * @param taskId    任务ID
     * @param currentUID 当前用户ID
     * */
    Task getTaskWithStatus(String taskId, String currentUID);

    /**更新一个任务
     * @param task      查询用taskId，更改用属性
     * */
    void updateTask(Task task);

    /**
     * 更新接受者队列
     * @param type  任务类型
     * @param taskId  任务实例
     * @param takerStrNew 新的接受者队列userId,userId....
     * @param noticeType 任务类型
     * */
    void updateTakers(Task task, String takerListStrNew, String type,String noticeType);

    /**增加任务项
     * @param needAddItemContent 需要增加的任务想列表
     * @param taskId    关联任务ID
     * */
    void addItems(String[] needAddItemsContent, String taskId);

    /**
     *更新任务项
     *@param  ids 需要更新的itemId列表
     *@param  modifyContent 需要更新成的内容
     * */
    void updateItems(String[] ids, String[] modifyContent);

    /**删除任务项列表
     * @param ids 需要删除的item的id数组
     * */
    void deleteItems(String[] ids);
    /**增加一个任务
     * @param task实例 增加一个任务
     * @param 返回一个新生成的id
     * */
    int addTask(Task task);

    /**新增接受者队列
     * @param 新增的uid数组
     * @param 关联的任务ID
     * */
    void addTakers(String[] userIds, String taskId);

    /**
     *获得任务进度情况
     * @param taskId 关联任务编号
     * @return map<a,b> a-itemId,b-process[1/3]
     **/

    Map<Integer,String> getProcess(String taskId);

    /**修改taker的执行状态,用itemId和UID就能定位到一个ref的状态，并更改
     * @param undoIds 需要改成undo的Id数组
     * @param doingIds 需要改成undo的Id数组
     * @param finishIds 需要改成undo的Id数组
     * @param currentUID 当前用户ID
     * */
    boolean updateRefStatus(String[] undoIds, String[] doingIds, String[] finishIds, String currentUID);

    /**查询该任务是否已经完成
     * @param taskId 关联任务ID号
     * */
    boolean isIndependentTaskOver(String taskId);

    /**删除任务，并删除级联的items
     * @param taskId 关联任务ID号
     * */
    void deleteTask(String taskId);

    /**获取每个接受者的执行情况
     * @param taskId 任务ID
     * */
    List<UserProcess> getAllUserProcess(String taskId);

    /**判断用户是不是把自己的任务项全部完成了
     * @param task 任务实例
     * @param uid 用户ID
     * @return 返回是否已完成？
     * */
    boolean isUserDone(Task task,String uid);

    /**----------------------------------以下是share用的方法---------------------------------------**/
    /**获得ShareTask
     * @param taskId 关联任务号
     * */
    Task getShareTask(String taskId);

    /**
     *增加share任务项
     *@param needAddItemContents 需要增加的任务项描述
     *@param taskId 任务ID
     *
     * */
    void addShareItems(String[] needAddItemsContents, String taskId);

    /**
     * 更新任务项描述
     * @param ids 需更改的id号
     * @param modifyContents 需要改成的内容
     * */
    void updateShareItems(String[] ids, String[] modifyContents);

    /**
     *删除任务项目描述
     *@param ids 需要删除的任务项id
     * */
    void deleteShareItems(String[] ids);

    /**查询share任务项
     * @param taskId 任务ID
     * */
    List<TaskItemShare> getShareItems(String taskId);

    /**修改shareItems的执行状态,用itemId就能定位到一个的状态，并更改
     * @param undoIds 需要改成undo的Id数组
     * @param doingIds 需要改成undo的Id数组
     * @param finishIds 需要改成undo的Id数组
     * @param userid   当前用户Id
     * @return boolean 其他操作无所谓，返回有没有锁(undo->doing)成功
     * */
    boolean updateShareItemsStatus(String[] undoIds, String[] doingIds,
                                   String[] finishIds, String userid);

    /**查看共享任务是否已经完成
     * @param taskId 任务Id
     * */
    boolean isShareTaskOver(String taskId);

    /**删除共享任务
     * @param taskId 任务ID
     * */
    void deleteShareTask(String taskId);

    /**----------------------------------以下是notice用的方法---------------------------------------**/
    /**发送通知
     * @param site 当前site
     * @param takers 接受者队列
     * @param task   任务实例，主要是为了获取taskId和taskTitle这两个字段
     * @param noticeType 通知类型 DEntity.Task_SHARE,或者Task_INDEPENDENT
     * @param action 执行的动作，去DActon找
     * @param actor  动作的执行者，一般为creator
     * */
    void sendNotice(int tid,List<TaskTaker> takers,Task task,String noticeType,String action,String actor);


    /**根据taskId获取takers列表，share和independent 通用
     * @param taskId 任务ID
     * @return  takers 接受者队列
     * */
    List<TaskTaker> getTakers(String taskId);

    /**设置已阅状态，通用
     * @param taskId 任务ID
     * @param userId 当前用户ID
     * */
    void setReadStatus(String taskId,String userId);





}
