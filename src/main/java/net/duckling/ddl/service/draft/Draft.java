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

package net.duckling.ddl.service.draft;

import java.util.Date;

/**
 * @date 2011-7-15
 * @author Clive Lee
 */
public class Draft {

    public static final String AUTO_TYPE = "auto";
    public static final String MANUAL_TYPE = "manual";

    private int id;
    private int tid;
    private int rid;
    private String uid;
    private String type;
    private String title;
    private String content;
    private Date modifyTime;


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return the pid
     */
    public int getRid() {
        return rid;
    }
    /**
     * @param pid the pid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
    }
    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }
    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * @return the modifyTime
     */
    public Date getModifyTime() {
        return modifyTime;
    }
    /**
     * @param modifyTime the modifyTime to set
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Draft() {}

    public Draft(int tid,int rid,String uid,String title,String content,String type) {
        this.content = content;
        this.title = title;
        this.tid = tid;
        this.uid = uid;
        this.rid = rid;
        this.type = type;
        this.modifyTime = new Date();
    }


}
