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

import net.duckling.ddl.util.DateUtil;


/**
 * @author lvly
 * @since 2012-6-8 任务对象的实体类
 * */
public class Task {
    /** 主键Id */
    private int taskId;
    /** teamId */
    private String teamId;
    /** 创建者 */
    private String creator;
    /** 创建时间 */
    private String createTime;
    /** 任务名称 */
    private String title;
    /** 是否有效 */
    private String valid;
    /** 任务类型 独立或者共享 */
    private String taskType;

    // 临时变量
    private List<TaskTaker> takers;
    private List items;
    private List<UserProcess> userProcess;
    private String readStatus;
    private boolean userOver;



    public static final String VALID = "valid";
    public static final String INVALID = "invalid";
    public static final String OVER="over";
    public static final String DUSTBIN="dustbin";

    /** 未开始状态 */
    public static final String STATUS_UNDO = "undo";
    /** 正在做状态 */
    public static final String STATUS_DOING = "doing";
    /** 已经做完状态 */
    public static final String STATUS_FINISH = "finish";

    //格式化方法 BEGIN
    /**获得taskType的中文显示方式*/
    public String getTaskTypeCN() {
        return TaskService.TYPE_SHARE.equals(taskType)?"共享任务":"独立任务";
    }
    /**获得接受者列表 某某某| 某某某 | 某某某这种形式*/
    public String getTakersNameStr(){
        return TakerWrapper.toView(takers).toString();
    }
    /**获得姓名Id列表*/
    public String getTakersUIDStr(){
        return TakerWrapper.toUID(takers).toString();
    }
    /**短格式时间，精确到日*/
    public String getShortCreateTime(){
        return DateUtil.getShortTime(this.createTime);
    }
    /**获取是否是新任务，用来判定星标*/
    public boolean getNewTask(){
        return TaskTaker.READ_STATUS_UNREAD.equals(this.readStatus);
    }
    //END

    public List<TaskTaker> getTakers() {
        return takers;
    }

    public boolean getUserOver() {
        return userOver;
    }
    public void setUserOver(boolean isUserOver) {
        this.userOver = isUserOver;
    }
    public String getReadStatus() {
        return readStatus;
    }
    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }
    public List<UserProcess> getUserProcess() {
        return userProcess;
    }
    public void setUserProcess(List<UserProcess> userProcess) {
        this.userProcess = userProcess;
    }
    public void setTakers(List<TaskTaker> takers) {
        this.takers = takers;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

}
