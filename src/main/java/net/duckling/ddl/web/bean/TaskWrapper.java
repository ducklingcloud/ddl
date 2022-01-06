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
package net.duckling.ddl.web.bean;

import java.util.List;

import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskItemShare;
import net.duckling.ddl.service.task.TaskService;
import net.duckling.ddl.service.task.UserProcess;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.CommonUtil;


/**
 * 任务工具类
 *
 * @author lvly
 * @since 2012-6-25
 * */
public final class TaskWrapper {

    /**
     * 获得展示队列
     *
     * @param tasks
     *            任务队列
     * @param context
     *            使用上下文环境
     * */
    public static List<Task> getView(List<Task> tasks, String uid, AoneUserService userSerivce, TaskService taskService) {
        if (CommonUtil.isNullArray(tasks)) {
            return null;
        }

        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            if (TaskService.TYPE_INDEPENDENT.equals(task.getTaskType())) {
                task.setUserOver(taskService.isUserDone(task, uid));
            } else {
                task.setUserOver(Task.OVER.equals(task.getValid()) || Task.DUSTBIN.equals(task.getValid()));
            }
            task.setCreator(userSerivce.getUserNameByID(task.getCreator()));
        }
        return tasks;
    }

    /** 获得共享任务进度 */
    public static UserProcess getShareProcess(List<TaskItemShare> shares) {
        if (CommonUtil.isNullArray(shares)) {
            return null;
        }
        int undoCount = 0;
        int doingCount = 0;
        int finishCount = 0;
        for (TaskItemShare share : shares) {
            switch (share.getStatus()) {
                case Task.STATUS_UNDO:
                    undoCount++;
                    break;
                case Task.STATUS_DOING:
                    doingCount++;
                    break;
                case Task.STATUS_FINISH:
                    finishCount++;
                    break;
                default:
                    break;
            }
        }
        return new UserProcess(undoCount, doingCount, finishCount, "进度%all");
    }
}
