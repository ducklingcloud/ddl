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
/**
 *
 */
package net.duckling.ddl.service.copy;

import java.util.Date;

/**
 * copy记录的痕迹
 * @author lvly
 * @since 2012-11-13
 */
public class CopyLog {
    public static final String TYPE_UPDATE="update";
    public static final String TYPE_CREATE="create";

    /**
     * 主键
     * */
    private int id;

    /**
     *发送方teamId
     ***/
    private int fromTid;

    /**
     * 发送文件的rid
     * */
    private int fromRid;

    /**
     * 发送方的版本号
     * */
    private int fromVersion;
    /**
     *接受方teamId
     ***/
    private int toTid;

    /**
     * 接受文件的rid
     * */
    private int toRid;

    /**
     * 接收方的版本号
     * */
    private int toVersion;

    /**
     * 触发人
     * */
    private String uid;
    /**
     * 触发时间
     * */
    private Date copyTime;
    /**
     * 执行操作，是更新还是创建
     * */
    private String type;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getFromTid() {
        return fromTid;
    }
    public void setFromTid(int fromTid) {
        this.fromTid = fromTid;
    }
    public int getFromRid() {
        return fromRid;
    }
    public void setFromRid(int fromRid) {
        this.fromRid = fromRid;
    }
    public int getFromVersion() {
        return fromVersion;
    }
    public void setFromVersion(int fromVersion) {
        this.fromVersion = fromVersion;
    }
    public int getToTid() {
        return toTid;
    }
    public void setToTid(int toTid) {
        this.toTid = toTid;
    }
    public int getToRid() {
        return toRid;
    }
    public void setToRid(int toRid) {
        this.toRid = toRid;
    }
    public int getToVersion() {
        return toVersion;
    }
    public void setToVersion(int toVersion) {
        this.toVersion = toVersion;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public Date getCopyTime() {
        return copyTime;
    }
    public void setCopyTime(Date copyTime) {
        this.copyTime = copyTime;
    }


}
