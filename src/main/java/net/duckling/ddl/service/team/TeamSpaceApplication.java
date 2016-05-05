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
package net.duckling.ddl.service.team;

import java.util.Date;

public class TeamSpaceApplication {
	public static final String TYPE_MANUAL = "manual";
	public static final String TYPE_ADMIN = "admin";
	public static final String TYPE_ACTIVITY = "activity";
	private int id;
	private int tid;
	private String uid;
	private Date applicationTime;
	private Date approveTime;
	private long originalSize;
	private long newSize;
	private String applicationType;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Date getApplicationTime() {
		return applicationTime;
	}
	public void setApplicationTime(Date applicationTime) {
		this.applicationTime = applicationTime;
	}
	public Date getApproveTime() {
		return approveTime;
	}
	public void setApproveTime(Date approveTime) {
		this.approveTime = approveTime;
	}
	public long getOriginalSize() {
		return originalSize;
	}
	public void setOriginalSize(long originalSize) {
		this.originalSize = originalSize;
	}
	public long getNewSize() {
		return newSize;
	}
	public void setNewSize(long newSize) {
		this.newSize = newSize;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public boolean isManual(){
		return TYPE_MANUAL.equals(getApplicationType());
	}
	public String getTypeDisplay(){
		switch (getApplicationType()) {
		case TYPE_MANUAL:
			return "手动扩容";
		case TYPE_ADMIN:
			return "管理员扩容";
		case TYPE_ACTIVITY:
			return "活动奖励";
		default:
			return "自动扩容";
		}
	}
}
