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

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * @author 吕龙云、
 * @since 2012-6-12
 * 任务的接受者实体类
 * */
public class TaskTaker extends UserIdFormatter{

    /**Id,主键*/
    private int takerId;
    /**关联的任务Id*/
    private int taskId;
    /**阅读状态*/;
    private String readStatus;

    public static final String READ_STATUS_READ="read";
    public static final String READ_STATUS_UNREAD="unread";


    public String getReadStatus() {
        return readStatus;
    }
    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }
    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)){
            return true;
        }
        if(obj==null||!(obj instanceof TaskTaker)){
            return false;
        }
        TaskTaker taker=(TaskTaker)obj;
        return this.taskId==taker.taskId&&super.getUserId().equals(taker.getUserId());
    }
    @Override
    public int hashCode() {
        return   new   HashCodeBuilder()
                .append(this.getTaskId())
                .append(this.getUserId())
                .append(this.getReadStatus())
                .hashCode();
    }

    /**未开始状态*/
    public static final String STATUS_UNDO="undo";
    /**正在做状态*/
    public static final String STATUS_DOING="doing";
    /**已经做完状态*/
    public static final String STATUS_FINISH="finish";
    public int getTakerId() {
        return takerId;
    }
    public void setTakerId(int takerId) {
        this.takerId = takerId;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String toString(){
        return super.getUserNameStr();
    }




}
