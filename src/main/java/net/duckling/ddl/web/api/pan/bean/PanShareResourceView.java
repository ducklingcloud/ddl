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
package net.duckling.ddl.web.api.pan.bean;

import java.util.Date;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.web.controller.pan.PanResourceBean;

/**
 * api 同步盘分享前端结果显示
 */
public class PanShareResourceView {
	private int id;
	private String shareUid;
	private String rid;
	private String password;
	private Date shareTime;
	private long expireMillis;
	private int downloadCount;
	private String status;
	
	private boolean valid;
	private String shareUrl;
	private String title;
	private String itemType;
	private String fileType;
	private boolean folder;
	private long size;
	private int lastVersion;
	private String shareUserName;
	
	public PanShareResourceView(PanShareResource psr, PanResourceBean pr){
		this.id = psr.getId();
		this.shareUid = psr.getShareUid();
		this.downloadCount = psr.getDownloadCount();
		this.expireMillis = psr.getExpireMillis();
		this.password = psr.getPassword();
		this.shareTime = psr.getShareTime();
		this.rid = pr.getRid();
		this.status = psr.getStatus();
		this.valid = (pr.getPath()==null ? false : true);
		this.title = pr.getTitle();
		this.itemType = pr.getItemType();
		this.fileType = pr.getFileType();
		this.folder = pr.isFolder();
		this.size = pr.getSize();
		this.lastVersion = pr.getVersion();
	}
	
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

	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isDelete(){
		return LynxConstants.STATUS_DELETE.equals(this.status);
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public boolean isFolder() {
		return folder;
	}
	public void setFolder(boolean folder) {
		this.folder = folder;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public int getLastVersion() {
		return lastVersion;
	}
	public void setLastVersion(int lastVersion) {
		this.lastVersion = lastVersion;
	}
	public String getShareUserName() {
		return shareUserName;
	}
	public void setShareUserName(String shareUserName) {
		this.shareUserName = shareUserName;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	
}
