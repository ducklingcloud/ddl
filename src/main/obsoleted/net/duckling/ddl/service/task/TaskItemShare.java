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


/**共享的items，这里就不存在taker的概念了
 * @author lvly
 * @since 2012-6-18
 * */
public class TaskItemShare extends TaskItem{
    /**接受者，执行者*/
    private String userId;
    /**状态执行状态*/
    private String status;
    /**修改时间*/
    private String editTime;

    //临时变量
    private boolean canEdit;
    //格式化输出BEGIN
    /**获得用户汉语名字*/
    public String getUserNameStr(){
        return TakerWrapper.getUserName(userId);
    }
    /**获得用户ID*/
    public String getUserIDStr(){
        return TakerWrapper.getUserID(userId);
    }
    //END

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEditTime() {
        return editTime;
    }

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }




}
