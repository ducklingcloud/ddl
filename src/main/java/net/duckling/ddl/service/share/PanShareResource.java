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

import net.duckling.ddl.constant.LynxConstants;

public class PanShareResource {
	private int id;
	private String shareUid;
	private String panShareId;
	private String sharePath;
	private String password;
	private Date shareTime;
	private long expireMillis;
	private int downloadCount;
	private String status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getShareUid() {
		return shareUid;
	}
	public void setShareUid(String shareUid) {
		this.shareUid = shareUid;
	}
	public String getPanShareId() {
		return panShareId;
	}
	public void setPanShareId(String panShareId) {
		this.panShareId = panShareId;
	}
	public String getSharePath() {
		return sharePath;
	}
	public void setSharePath(String sharePath) {
		this.sharePath = sharePath;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Date getShareTime() {
		return shareTime;
	}
	public void setShareTime(Date shareTime) {
		this.shareTime = shareTime;
	}
	public long getExpireMillis() {
		return expireMillis;
	}
	public void setExpireMillis(long expireMillis) {
		this.expireMillis = expireMillis;
	}
	public int getDownloadCount() {
		return downloadCount;
	}
	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isDelete(){
		return LynxConstants.STATUS_DELETE.equals(getStatus());
	}
	public boolean isAvailable(){
		return LynxConstants.STATUS_AVAILABLE.equals(getStatus());
	}
}
