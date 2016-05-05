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

package net.duckling.ddl.service.share;

import java.util.Date;

/**
 * @date 2011-10-10
 * @author clive
 */
public class ShareFileAccess {
	
	private int id;
	private int tid;
	private int fid;
	private String uid; //fileOwner
	private int clbId;
	private Date createTime;
	private String password;
	private int validOfDays;
	private int rid;
	
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	/**
	 * 文件提取码
	 */
	private String fetchFileCode;

	
	
	public String getFetchFileCode() {
		return fetchFileCode;
	}
	public void setFetchFileCode(String fetchFileCode) {
		this.fetchFileCode = fetchFileCode;
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
	 * @return the fid
	 */
	public int getFid() {
		return fid;
	}
	/**
	 * @param fid the fid to set
	 */
	public void setFid(int fid) {
		this.fid = fid;
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the validOfDays
	 */
	public int getValidOfDays() {
		return validOfDays;
	}
	/**
	 * @param validOfDays the validOfDays to set
	 */
	public void setValidOfDays(int validOfDays) {
		this.validOfDays = validOfDays;
	}
	
}
