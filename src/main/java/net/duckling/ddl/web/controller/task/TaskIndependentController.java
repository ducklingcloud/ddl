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
package net.duckling.ddl.web.controller.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.task.ItemWrapper;
import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author lvly
 * @since 2012-6-8 对任务操作的基本Controller
 * */
@Controller
@RequestMapping("/{teamCode}/task/independent")
@RequirePermission(target = "team", operation = "view", authenticated = true)
public class TaskIndependentController extends TaskBaseController {

    @Autowired
    private TaskService taskService;

    /** 列出所有的任务 */
    @RequestMapping
    public ModelAndView home(HttpServletRequest request, @PathVariable("teamCode") String teamCode) {
        return super.home(request, teamCode);

    }

    /** 查看一个任务 */
    @RequestMapping(params = "func=view")
    public ModelAndView viewTask(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("teamCode") String teamCode) {
        return super.viewTask(request, response, teamCode, TaskService.TYPE_INDEPENDENT);
    }

    /** 获得进度，ajax */
    @RequestMapping(params = "func=process")
    public void process(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("teamCode") String teamCode) {
        super.process(request, response, TaskService.TYPE_INDEPENDENT);
    }

    /** 修改一个任务，准备页面 */
    @RequestMapping(params = "func=readyModify")
    public ModelAndView readyModifyTask(HttpServletRequest request, @PathVariable("teamCode") String teamCode) {
        return super.readyModifyTask(request, teamCode, TaskService.TYPE_INDEPENDENT);
    }

  
    @RequestMapping(params = "func=readyDeal")
    public void readyDealTask(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("teamCode") String teamCode) {
        // 参数获取
        String taskId = request.getParameter("taskId");

        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TASK_PATTERNS);
        context.getSite();
        TaskService service = taskService;

        service.setReadStatus(taskId, context.getCurrentUID());
        Task task = service.getTaskWithStatus(taskId, context.getCurrentUID());
        task.setItems(ItemWrapper.getProcessFromView(task.getItems(), service.getProcess(taskId)));
        JsonUtil.writeJSONObject(response, JsonUtil.getJSONArrayFromList(task.getItems()));
    }

    /** 处理任务 */
    @RequestMapping(params = "func=dealSubmit")
    public void dealSubmitTask(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("teamCode") String teamCode) {
        // 参数获取
        String[] undoItemIds = getSplit(request, "undoIds");
        String[] doingItemIds = getSplit(request, "doingIds");
        String[] finishItemIds = getSplit(request, "finishIds");
        String taskId = request.getParameter("taskId");

        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TASK_PATTERNS);
        Site site = context.getSite();
        TaskService service = taskService;
        boolean result = service.updateRefStatus(undoItemIds, doingItemIds, finishItemIds, context.getCurrentUID());
        if (!result) {
            JsonUtil.writeJSONObject(response, result);
            return;
        }
        boolean isOver = service.isIndependentTaskOver(taskId);
        Task task = service.getTask(taskId);
        if (isOver) {
            task.setValid(Task.OVER);
            service.updateTask(task);
            // noticeD 当任务完成时，给所有人发个通知
            service.sendNotice(site.getId(), addTaker(task.getTakers(), task.getCreator(), task.getTaskId()), task,
                    DEntity.DTASK_INDEPENDENT, DAction.OVER_TASK, context.getCurrentUID());
        } else {
            if (Task.OVER.equals(task.getValid())) {
                task.setValid(Task.VALID);
                service.updateTask(task);
            }
        }
        JsonUtil.writeJSONObject(response, result);
    }

    /** 删除任务 */
    @RequestMapping(params = "func=delete")
    public ModelAndView deleteTask(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("teamCode") String teamCode) {
        return super.doDeleteTask(request, response, teamCode, TaskService.TYPE_INDEPENDENT);
    }

    /** 归档任务 */
    @RequestMapping(params = "func=dustbin")
    public ModelAndView taskToDustbin(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("teamCode") String teamCode) {
        return super.doToDustbin(request, response, teamCode, TaskService.TYPE_INDEPENDENT);
    }
}
