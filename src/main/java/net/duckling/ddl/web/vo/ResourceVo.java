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
package net.duckling.ddl.web.vo;

import java.util.Date;

public class ResourceVo {
    private int tid;
    private String title;
    private String path;
    private long size;
    private int version;
    private boolean folder;
    private String lastEditor;
    private String lastEditorName;
    private Date lastEditTime;
    public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public boolean isFolder() {
        return folder;
    }
    public void setFolder(boolean folder) {
        this.folder = folder;
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
}
