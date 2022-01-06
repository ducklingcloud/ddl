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
package net.duckling.ddl.service.sync.domain.resource;

import java.util.Date;
import java.util.Map;

public class Resource {
    // 资源最小占用空间
    public static final long SIZE_MIN = 8192l; // 8kb

    private int rid;
    private int tid;
    private int bid; // 文件夹ID
    private String itemType; // DFile、DPage、Folder
    private String title;
    private String creator;
    private Date createTime;
    private String lastEditor;
    private String lastEditorName;
    private Date lastEditTime;
    private int version;
    private Map<Integer, String> tags;
    private String fileType; // 文件扩展名
    private String status;
    private long size;

    public static enum ItemType {
        DPage("DPage"), DFile("DFile"), Folder("Folder");
        private String value;

        private ItemType(String val) {
            value = val;
        }

        public static ItemType get(String val) {
            if (DPage.toString().equals(val)) {
                return DPage;
            } else if (DFile.toString().equals(val)) {
                return DFile;
            } else if (Folder.toString().equals(val)) {
                return Folder;
            }
            return null;
        }

        public String toString() {
            return value;
        }
    }

    public boolean isFolder() {
        return ItemType.Folder.toString().equalsIgnoreCase(getItemType());
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }

    public String getLastEditorName() {
        return lastEditorName;
    }

    public void setLastEditorName(String lastEditorName) {
        this.lastEditorName = lastEditorName;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<Integer, String> getTags() {
        return tags;
    }

    public void setTags(Map<Integer, String> tags) {
        this.tags = tags;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
