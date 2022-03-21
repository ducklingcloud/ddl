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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.task.SQLThreadLocal;
import net.duckling.ddl.service.task.Task;
import net.duckling.ddl.service.task.TaskService;
import net.duckling.ddl.service.task.TaskTaker;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.CommonUtil;
import net.duckling.ddl.util.DateUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PinyinUtil;
import net.duckling.ddl.web.bean.TaskWrapper;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.esac.clb.util.HttpStatus;

/**
 * 对任务操作的基本Controller
 *
 * @author lvly
 * @since 2012-6-8
 * */
@Controller
@RequestMapping("/{teamCode}/task")
@RequirePermission(target = "team", operation = "view")
public class TaskBaseController extends BaseController {
    @Autowired
    private IParamService paramService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    protected AoneUserService aoneUserService;
    @Autowired
    protected TaskService taskService;

    private void add2Array(JsonArray parent, JsonArray child, char alphabet) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", "" + alphabet);
        obj.add("value", child);
        parent.add(obj);
    }

    /**
     * 转化为Task规范名称
     *
     * @param userIds
     *            用户名称 xx1@xx.com,xx2@xx.com....
     * @param site
     *            Site
     * @return string xxName%xxEmail,...,...
     * */
    private String convert2TaskNameType(String uIds, Site site) {
        if (StringUtils.isEmpty(uIds)) {
            return "";
        }
        String emails[] = uIds.split(",");
        StringBuffer sb = new StringBuffer();
        for (String email : emails) {
            if (StringUtils.isEmpty(email)) {
                continue;
            }
            sb.append(aoneUserService.getUserNameByID(email)).append("%")
                    .append(email).append(",");
        }
        if (sb.indexOf(",") > -1) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }

    /**
     * 将按拼音排好序的用户列表转化为JSON字符串，JSON字符串格式为： users:[{ id: a （姓名首字母） value:[{ id:
     * abc@cnic.cn (UID) name: 姓名 },......] },.... ]
     *
     * @param list
     *            SimpleUser列表
     * @return
     */
    private JsonObject getAlphabetJSONFromSortedPinyin(List<SimpleUser> list) {
        JsonArray array = new JsonArray();
        if (null == list || list.isEmpty()) {
            return new JsonObject();
        }

        Map<Character, JsonArray> map = new TreeMap<Character, JsonArray>();
        for (SimpleUser user : list) {
            Character c = null;
            if (StringUtils.isEmpty(user.getPinyin())) {
                c = user.getUid().charAt(0);
            } else {
                c = user.getPinyin().charAt(0);
            }
            c = Character.toUpperCase(c);
            JsonArray child = map.get(c);
            if (child == null) {
                child = new JsonArray();
                map.put(c, child);
            }
            child.add(getJSONFromSimpleUser(user));
        }

        for (Entry<Character, JsonArray> entry : map.entrySet()) {
            add2Array(array, entry.getValue(), entry.getKey());
        }
        JsonObject obj = new JsonObject();
        obj.add("users", array);
        return obj;
    }

    private JsonObject getJSONFromSimpleUser(SimpleUser su) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", su.getUid());
        obj.addProperty("email", su.getEmail());
        if (StringUtils.isNotEmpty(su.getName())) {
            obj.addProperty("name", su.getName());
        } else {
            obj.addProperty("name", su.getUid());
        }
        return obj;
    }

    /**
     * 将按拼音排好序的用户列表转化为JSON字符串，JSON字符串格式为： users:[{ id: a （姓名首字母） value:[{ id:
     * abc@cnic.cn (UID) name: 姓名 },......] },.... ]
     *
     * @param list
     * @return
     */
    private JsonArray getRegularJSONFromSortedPinyin(List<SimpleUser> list) {
        JsonArray array = new JsonArray();
        if (null != list && !list.isEmpty()) {
            for (SimpleUser su : list) {
                JsonObject temp = new JsonObject();
                temp.addProperty("name", su.getName());
                temp.addProperty("id", su.getUid());
                temp.addProperty("email", su.getEmail());
                array.add(temp);
            }
        }
        return array;
    }
    /**往目标队列里面加一个新成员
     * @param dest 目标队列
     * @param userId 用户Id
     * @param taskId 任务Id，暂时无用
     * @return dest+user
     * */
    protected List<TaskTaker> addTaker(List<TaskTaker> dest,String userId,int taskId){
        TaskTaker taker=new TaskTaker();
        taker.setTaskId(taskId);
        taker.setUserId(userId);
        dest.add(taker);
        return dest;
    }
    // tool
    private boolean isChineseCharacter(String chars) {
        if (null == chars || chars.length() == 0) {
            return false;
        }
        char ch = chars.charAt(0);
        return (ch >= PinyinUtil.CH_START && ch <= PinyinUtil.CH_END);
    }

    protected String deleteSame(String str1, String str2) {
        String[] str1A = str1.split(",");
        String[] str2A = str2.split(",");
        Set<String> set = new HashSet<String>();
        if (!CommonUtil.isNullArray(str1A)) {
            for (String str : str1A) {
                if (StringUtils.isEmpty(str)) {
                    continue;
                }
                set.add(str);
            }
        }
        if (!CommonUtil.isNullArray(str2A)) {
            for (String str : str2A) {
                if (StringUtils.isEmpty(str)) {
                    continue;
                }
                set.add(str);
            }
        }
        return set.toString().replace("[", "").replace("]", "");
    }

    /** 删除任务 */
    protected ModelAndView doDeleteTask(HttpServletRequest request,
                                        HttpServletResponse response, String teamCode, String taskType) {
        // 参数获取
        String taskId = request.getParameter("taskId");
        String listType = request.getParameter("listType");

        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        context.getSite();
        TaskService service = taskService;
        if (TaskService.TYPE_SHARE.equals(taskType)) {
            service.deleteShareTask(taskId);
        } else if (TaskService.TYPE_INDEPENDENT.equals(taskType)) {
            service.deleteTask(taskId);
        }
        return getForward(teamCode, "?listType=" + listType);

    }

    protected ModelAndView doToDustbin(HttpServletRequest request,
                                       HttpServletResponse response, String teamCode, String taskType) {
        // 参数获取
        String taskId = request.getParameter("taskId");
        String listType = request.getParameter("listType");
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        context.getSite();
        Param param = paramService.get(ParamConstants.UserTaskType.TYPE,
                                       taskId, context.getCurrentUID());
        if (param == null) {
            param = new Param();
            param.setItemId(context.getCurrentUID());
            param.setKey(taskId);
            param.setType(ParamConstants.UserTaskType.TYPE);
            param.setValue(Task.DUSTBIN);
            paramService.addParam(param);
        }
        return getForward(teamCode, "?listType=" + listType);
    }

    protected ModelAndView getForward(String teamCode, String params) {
        return new ModelAndView("forward:" + StringUtils.trimToEmpty(params));
    }

    protected ModelAndView getRedirect(String teamCode) {
        return new ModelAndView("redirect:");
    }

    protected String[] getSplit(HttpServletRequest request, String paramName) {
        if (isNullParam(request, paramName)) {
            return new String[] {};
        }
        return request.getParameter(paramName).split(",");
    }

    protected boolean isNull(String param) {
        return param == null || "".equals(param.trim());
    }

    protected boolean isNullParam(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName) == null
                || "".equals(request.getParameter(paramName).trim());
    }

    /** 获取团队成员 */
    @RequestMapping(params = "func=getmembers")
    @RequirePermission(target = "team", operation = "edit")
    public void getTeamMembers(HttpServletRequest request,
                               HttpServletResponse response) {
        VWBContext.createContext(request, UrlPatterns.T_TASK_PATTERNS);
        int tid = VWBContext.getCurrentTid();
        String type = request.getParameter("type");
        if (null == type || "".equals(type) || !"map".equals(type)) {
            List<SimpleUser> members = teamMemberService
                    .getTeamMembersOrderByName(tid);
            JsonObject obj = getAlphabetJSONFromSortedPinyin(members);
            JsonUtil.write(response, obj);
        } else {
            String keyword = request.getParameter("searchParam");
            List<SimpleUser> members = new ArrayList<SimpleUser>();
            if (null != keyword && !"".equals(keyword)) {
                if (isChineseCharacter(keyword)) {
                    members = teamMemberService.getTeamMembersByName(tid,
                                                                     keyword);
                } else {
                    members = teamMemberService.getTeamMembersByPinyin(tid,
                                                                       keyword);
                }
            }
            JsonArray array = getRegularJSONFromSortedPinyin(members);
            JsonUtil.write(response, array);
        }

    }

    /** 列出所有的任务 */
    @RequestMapping
    public ModelAndView home(HttpServletRequest request,
                             @PathVariable("teamCode") String teamCode) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        TaskService service = taskService;
        // 参数获取
        String listType = request.getParameter("listType");
        String date = request.getParameter(SQLThreadLocal.CON_DATE);
        String taskType = request.getParameter(SQLThreadLocal.CON_TASK_TYPE);

        // 注入查询条件
        SQLThreadLocal.append(SQLThreadLocal.CON_DATE, date);
        SQLThreadLocal.append(SQLThreadLocal.CON_TASK_TYPE, taskType);

        // 如果是重定向过来的默认跳转到这里
        listType = listType == null ? "imTaker" : listType;

        List<Task> tasks = null;
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                 "/jsp/aone/task/task_items.jsp");
        String preOperCN = "";
        // 查看我发布的任务
        if ("createByMe".equals(listType)) {
            tasks = service.getTasksCreateByMe(context.getCurrentUID(),
                                               teamCode);
        }
        // 查看我接受的任务
        else if ("imTaker".equals(listType)) {
            tasks = service
                    .getTasksByImTaker(context.getCurrentUID(), teamCode);

        }// 已完成
        else if ("allHistory".equals(listType)) {
            tasks = service.getFinishedTasks(teamCode, context.getCurrentUID());
        }
        // 点击通知时候用
        mv.addObject("taskId", request.getParameter("taskId"));
        mv.addObject("taskType", request.getParameter("taskType"));

        mv.addObject("tasks", TaskWrapper.getView(tasks,
                                                  context.getCurrentUID(), aoneUserService, taskService));
        mv.addObject("preOper", listType);
        mv.addObject("dateCon", date);
        mv.addObject("preOperCN", preOperCN);
        mv.addObject("taskTypeCon", taskType);
        return mv;
    }

    /** 提交一个任务，并跳转到任务列表页面 */
    @RequestMapping(params = "func=modifySubmit")
    public ModelAndView modifySubmitTask(HttpServletRequest request,
                                         @PathVariable("teamCode") String teamCode) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        Site site = context.getSite();
        TaskService service = taskService;

        String taskId = StringUtils.trimToEmpty(request.getParameter("taskId"));
        String taskType = StringUtils.trimToEmpty(request
                                                  .getParameter("taskType"));
        String taskTitle = StringUtils.trimToEmpty(request
                                                   .getParameter("taskTitle"));
        String takerListStr = convert2TaskNameType(
            StringUtils.trimToEmpty(request.getParameter("takerList")),
            site);
        String takerFromPop = convert2TaskNameType(
            StringUtils.trimToEmpty(request.getParameter("takerList_pop")),
            site);
        takerListStr = deleteSame(takerListStr, takerFromPop);
        // 修改
        if (!isNull(taskId)) {
            String needDeleteItemsId = request
                    .getParameter("needDeleteItemsId");
            String needUpdateItemsId = request
                    .getParameter("needUpdateItemsId");
            String[] needAddItemsContents = request
                    .getParameterValues("newTaskContent");
            String[] modifyContents = request
                    .getParameterValues("modifyTaskContent");

            Task task = service.getShareTask(taskId);
            task.setTitle(taskTitle);
            service.updateTask(task);
            service.updateTakers(task, takerListStr, TaskService.TYPE_SHARE,
                                 DEntity.DTASK_SHARE);
            service.addShareItems(needAddItemsContents, task.getTaskId() + "");
            service.updateShareItems(needUpdateItemsId.split(","),
                                     modifyContents);
            service.deleteShareItems(needDeleteItemsId.split(","));
            // noticeS任务更新了给所有人发一下通知
            service.sendNotice(site.getId(), service.getTakers(taskId), task,
                               DEntity.DTASK_SHARE, DAction.UPDATE_TASK, task.getCreator());

        }// 增加
        else {
            String items[] = request.getParameterValues("newTaskContent");
            String title = request.getParameter("taskTitle");
            String[] takerArray = takerListStr.split(",");
            Task task = new Task();

            task.setTeamId(teamCode);
            task.setCreateTime(DateUtil.getCurrentTime());
            task.setCreator(context.getCurrentUID());
            task.setTaskType(taskType);
            task.setTitle(title);
            int id = service.addTask(task);
            task.setTaskId(id);
            service.addShareItems(items, id + "");
            service.addTakers(takerListStr.split(","), id + "");
            // noticeS 任务创建成功发送通知
            service.sendNotice(site.getId(),
                               getListFromArray(takerArray, id), task,
                               DEntity.DTASK_SHARE, DAction.CREATE, task.getCreator());
        }
        return getRedirect(teamCode);
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
    /**
     * 权限控制
     * */
    @SuppressWarnings({ "unchecked", "unused" })
    @OnDeny({ "getTeamMembers" })
    public void onDeny(String methodName, HttpServletRequest request,
                       HttpServletResponse response) {
        JsonObject obj = new JsonObject();
        obj.addProperty("status", false);
        obj.addProperty("result", "无权进行此操作！");
        response.setStatus(HttpStatus.AUTH_FAILED);
        JsonUtil.write(response, obj);
    }

    // tool end

    /** 获取任务进度 */
    public void process(HttpServletRequest request,
                        HttpServletResponse response, String type) {
        VWBContext.createContext(request, UrlPatterns.T_TASK_PATTERNS);
        TaskService service = taskService;
        // param list
        String taskId = request.getParameter("taskId");

        if (TaskService.TYPE_INDEPENDENT.equals(type)) {
            JsonUtil.write(response, JsonUtil
                           .getJSONArrayFromList(service.getAllUserProcess(taskId)));
        } else if (TaskService.TYPE_SHARE.equals(type)) {
            JsonUtil.write(response, JsonUtil
                           .getJSONObject(TaskWrapper.getShareProcess(service
                                                                      .getShareItems(taskId))));
        }
    }

    /***
     * 准备更改任务
     * */
    @RequestMapping(params = "func=readyModifyTask")
    public ModelAndView readyModifyTask(HttpServletRequest request,
                                        @PathVariable("teamCode") String teamCode, String taskType) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        context.getSite();
        Team instance = teamService.getTeamByName(teamCode);
        // 参数获取
        String taskId = request.getParameter("taskId");

        List<SimpleUser> members = teamMemberService
                .getTeamMembersOrderByName(instance.getId());
        TaskService service = taskService;
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                 "/jsp/aone/task/task_modify.jsp");
        // 修改
        if (!isNull(taskId)) {
            Task task = null;
            if (TaskService.TYPE_SHARE.equals(taskType)) {
                task = service.getShareTask(taskId);
            } else if (TaskService.TYPE_INDEPENDENT.equals(taskType)) {
                task = service.getTask(taskId);
            }

            mv.addObject("task", task);
        }
        // 增加

        mv.addObject("members", members);
        mv.addObject("user", context.getCurrentUID());

        return mv;
    }

    /** 查看任务详细信息 */
    @SuppressWarnings("unused")
    @Deprecated
    public ModelAndView viewTask(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @PathVariable("teamCode") String teamCode, String taskType) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        context.getSite();
        TaskService service = taskService;
        // 参数获取
        String taskId = request.getParameter("taskId");

        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                 "/jsp/aone/task/task_view.jsp");
        Task task = null;
        if (TaskService.TYPE_SHARE.equals(taskType)) {
            task = service.getShareTask(taskId);
        } else if (TaskService.TYPE_INDEPENDENT.equals(taskType)) {
            task = service.getTask(taskId);
            mv.addObject("items", task.getItems());
            mv.addObject("userProcess", service.getAllUserProcess(taskId));
        }

        mv.addObject("task", task);
        return mv;
    }
}
