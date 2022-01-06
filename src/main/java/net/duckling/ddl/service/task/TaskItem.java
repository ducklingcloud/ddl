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

/***任务项 的实体Bean，跟任务Task是多对一的关系
 * @author lvly
 * @since 2012-06-14
 * */
public class TaskItem {

    /**主键*/
    private int itemId;
    /**任务项描述*/
    private String content;
    /**关联taskId*/
    private int taskId;
    /**是否有效？*/
    private String valid;

    //临时变量
    private String process;
    private String status;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getProcess() {
        return process;
    }
    public void setProcess(String process) {
        this.process = process;
    }
    public static final String VALID="valid";
    public static final String INVALID="invalid";

    public TaskItem(){

    }


    public String getValid() {
        return valid;
    }
    public void setValid(String valid) {
        this.valid = valid;
    }
    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)){
            return true;
        }
        if(obj==null||!(obj instanceof TaskItem)){
            return false;
        }
        TaskItem item=(TaskItem)obj;
        return
                item.taskId==this.taskId&&
                item.content.equals(this.content);
    }
    @Override
    public int hashCode() {
        return   new   HashCodeBuilder()
                .append(this.getItemId())
                .append(this.getTaskId())
                .append(this.getContent())
                .append(this.getValid())
                .hashCode();
    }

    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getTaskId() {
        return taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }


}
