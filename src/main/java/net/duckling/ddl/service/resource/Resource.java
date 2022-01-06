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
package net.duckling.ddl.service.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import net.duckling.ddl.constant.LynxConstants;


public class Resource {
    /**
     * 文件夹类型，用于数据库显示排序；先排文件夹后排文件
     */
    public static final int FOLDER_ORDER_TYPE =1;
    /**
     * 非文件加类型，用于数据库显示排序
     */
    public static final int NO_FOLDER_ORDER_TYPE=2;

    //资源最小占用空间
    public static final long SIZE_MIN_OCCUPIED = 8192l;     //8kb
    public static final String DDOC = "ddoc"; //file_type协作文档扩展名
    private int tid;
    private String title;
    private Date createTime;
    private String creator;
    private String creatorName;
    private String lastEditor;
    private String lastEditorName;
    private Date lastEditTime;
    private int lastVersion;
    private String fileType;
    private int rid;
    private String itemType;
    private int bid;
    /**
     * file大小为lastVersion的值，page和folder为0
     */
    private long size;
    private boolean shared;
    private Map<Integer,String> tagMap;
    private Set<String> markedUserSet;
    /**
     * 用于文件夹的排序，文件加值为1，其余文件暂定为2
     */
    private int orderType;

    private String status;

    /** 为了排序加的列 **/
    private String orderTitle;//用于按资源标题排序
    private Date orderDate;//用于按资源最后修改时间排序

    /**
     * 资源完整路径,如/hello/abc.doc
     */
    private String path;

    private List<Resource> childrens = new ArrayList<Resource>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the bid
     */
    public int getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(int bid) {
        this.bid = bid;
    }

    /**
     * @return the markedUserSet
     */
    public Set<String> getMarkedUserSet() {
        if(markedUserSet==null){
            this.markedUserSet = new HashSet<String>();
        }
        return markedUserSet;
    }

    /**
     * @param markedUserSet the markedUserSet to set
     */
    public void setMarkedUserSet(Set<String> markedUserSet) {
        this.markedUserSet = markedUserSet;
    }

    /**
     * @return the rid
     */
    public int getRid() {
        return rid;
    }


    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    /**
     * @param rid the rid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
    }


    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType the itemType to set
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
        if("Folder".equalsIgnoreCase(itemType)){
            setOrderType(1);
        }else{
            setOrderType(2);
        }
    }

    /**
     * @return the creatorName
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * @param creatorName the creatorName to set
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    /**
     * @return the lastEditorName
     */
    public String getLastEditorName() {
        return lastEditorName;
    }

    /**
     * @param lastEditorName the lastEditorName to set
     */
    public void setLastEditorName(String lastEditorName) {
        this.lastEditorName = lastEditorName;
    }
    /**
     * @return the tagMap
     */
    public Map<Integer, String> getTagMap() {
        return tagMap;
    }

    /**
     * @param tagMap the tagMap to set
     */
    public void setTagMap(Map<Integer, String> tagMap) {
        this.tagMap = tagMap;
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
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return the lastEditor
     */
    public String getLastEditor() {
        return lastEditor;
    }

    /**
     * @param lastEditor the lastEditor to set
     */
    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }

    /**
     * @return the lastEditTime
     */
    public Date getLastEditTime() {
        return lastEditTime;
    }

    /**
     * @param lastEditTime the lastEditTime to set
     */
    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    /**
     * @return the lastVersion
     */
    public int getLastVersion() {
        return lastVersion;
    }

    /**
     * @param lastVersion the lastVersion to set
     */
    public void setLastVersion(int lastVersion) {
        this.lastVersion = lastVersion;
    }

    /**
     * @return the fileType
     */
    public String getFileType() {
        if(StringUtils.isEmpty(fileType)&&!isFolder()){
            fileType = title.substring(title.lastIndexOf('.')+1, title.length());
        }
        return fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public Date getOrderDate() {
        if(orderDate==null){
            orderDate=new Date();
        }
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public boolean isPage(){
        return LynxConstants.TYPE_PAGE.equals(getItemType());
    }

    public boolean isBundle(){
        return LynxConstants.TYPE_BUNDLE.equals(getItemType());
    }

    public boolean isFile(){
        return LynxConstants.TYPE_FILE.equals(getItemType());
    }

    public static boolean isSupportedType(String type){
        if(LynxConstants.TYPE_PAGE.equals(type)||
           LynxConstants.TYPE_BUNDLE.equals(type)||
           LynxConstants.TYPE_FILE.equals(type)){
            return true;
        }
        return false;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<Resource> getChildrens() {
        return childrens;
    }
    public void addChildren(Resource node){
        getChildrens().add(node);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Resource other = (Resource) obj;
        if (rid != other.rid)
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tid=").append(tid).append(";title=").append(title).append(";rid=").append(rid);
        sb.append(";fileType=").append(fileType).append(";itemType=").append(itemType).append(";bid=").append(bid);
        return sb.toString();
    }

    public boolean isFolder(){
        return LynxConstants.TYPE_FOLDER.equalsIgnoreCase(getItemType());
    }

    public boolean isAvailable(){
        return StringUtils.isEmpty(getStatus())||LynxConstants.STATUS_AVAILABLE.equals(getStatus());
    }
    public boolean isDelete(){
        return LynxConstants.STATUS_DELETE.equals(getStatus());
    }

    public boolean isUnpublish() {
        return LynxConstants.STATUS_UNPUBLISH.equals(getStatus());
    }

}
