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
package net.duckling.ddl.service.tobedelete;

import java.util.Date;

@Deprecated
public class Page implements Cloneable{
    public static final Integer INIT_VERSION = 0;
    public static final Integer FIRST_VERSION = 1;
    public static final int UNALLOCTION = 0;
    
	private int id;
	private int pid;
	private int tid;
	private String status;
	private String title;
	private Date createTime;
	private String creator;
	private String creatorName;
	private String lastEditor;
	private String lastEditorName;
	private Date lastEditTime;
	private int lastVersion;

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
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPid(int pid) {
		this.pid = pid;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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

	public Object clone(){
		Page page = new Page();
		page.setTid(tid);
		page.setStatus(status);
		page.setTitle(title);
		page.setCreateTime(createTime);
		page.setCreator(creator);
		page.setLastEditor(lastEditor);
		page.setLastEditTime(lastEditTime);
		page.setLastVersion(lastVersion);
		return page;
	}
	
	public static Page getNewPageMeta(int tid,String title,String uid){
		return buildPageMeta(UNALLOCTION,tid,title,uid,INIT_VERSION);
	}
	
	@SuppressWarnings("unused")
    public static Page buildPageMeta(int rid,int tid,String title,String uid,int version){
		Page m = new Page();
		m.setTid(tid);
		m.setTitle(title);
		m.setCreator(uid);
		m.setCreateTime(new Date());
		m.setLastEditor(uid);
		m.setLastEditTime(new Date());
		m.setLastVersion(version);
		return m;
	}
}
