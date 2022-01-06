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
package net.duckling.ddl.service.file;

import java.util.Date;

public class FileVersion  {
    private int id;
    private int tid;
    private int version;
    private int clbId;
    private long size;
    private String title;
    private String editor;
    private String status;
    private Date editTime;
    private int rid;
    //因为有copy，所有都变复杂
    private int clbVersion;
    /**
     * 上传文件设备号
     */
    private String device;
    /**
     * 文件校验和
     */
    private String checksum;

    public int getClbVersion() {
        return clbVersion;
    }
    public void setClbVersion(int clbVersion) {
        this.clbVersion = clbVersion;
    }
    /**
     * @return the rid
     */
    public int getRid() {
        return rid;
    }
    /**
     * @param rid the rid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the tid
     */
    public int getTid() {
        return tid;
    }
    /**
     * @param tid the tid to set
     */
    public void setTid(int tid) {
        this.tid = tid;
    }
    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }
    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }
    /**
     * @return the clbId
     */
    public int getClbId() {
        return clbId;
    }
    /**
     * @param clbId the clbId to set
     */
    public void setClbId(int clbId) {
        this.clbId = clbId;
    }
    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }
    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }
    /**
     * @return the editor
     */
    public String getEditor() {
        return editor;
    }
    /**
     * @param editor the editor to set
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the editTime
     */
    public Date getEditTime() {
        return editTime;
    }
    /**
     * @param editTime the editTime to set
     */
    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getChecksum() {
        return checksum;
    }
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(";rid=").append(rid);
        sb.append(";tid=").append(tid);
        sb.append(";title=").append(title);
        sb.append(";version=").append(version);
        sb.append(";clbid=").append(clbId);
        sb.append(";clbVersion=").append(clbVersion);
        sb.append(";size=").append(size);
        sb.append(";editor=").append(editor);
        sb.append(";editTime=").append(editTime);
        sb.append(";device=").append(device);
        return sb.toString();
    }
}
