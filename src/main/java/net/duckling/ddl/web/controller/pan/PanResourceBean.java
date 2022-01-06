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
package net.duckling.ddl.web.controller.pan;

import java.util.Date;

import net.duckling.ddl.constant.LynxConstants;

public class PanResourceBean {
    public static final String BEAN_TYPE_SEARCH="search";

    private String rid;
    private String path;
    private String parentPath;
    private String title;
    private long size;
    private String sizeStr;
    private String type;
    private String creator;
    private Date createTime;
    private Date modifyTime;
    private String lastEditor;
    private String itemType;
    private String fileType;
    private int version;
    private int meePoVersion;
    private String beanType;
    private Boolean shared;

    public int getMeePoVersion() {
        return meePoVersion;
    }
    public void setMeePoVersion(int meePoVersion) {
        this.meePoVersion = meePoVersion;
    }
    public String getParentPath() {
        return parentPath;
    }
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
    public String getLastEditor() {
        return lastEditor;
    }
    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public String getRid() {
        return rid;
    }
    public void setRid(String rid) {
        this.rid = rid;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public String getSizeStr() {
        return sizeStr;
    }
    public void setSizeStr(String sizeStr) {
        this.sizeStr = sizeStr;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public boolean isFile() {
        return !LynxConstants.TYPE_FOLDER.equals(getItemType());
    }
    public boolean isFolder(){
        return LynxConstants.TYPE_FOLDER.equals(getItemType());
    }

    public String getBeanType() {
        return beanType;
    }
    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }
    public boolean isSearchResult(){
        return BEAN_TYPE_SEARCH.equals(beanType);
    }
    public Boolean getShared() {
        return shared;
    }
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

}
