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
package net.duckling.ddl.service.mobile;

import java.util.Date;

public class IphoneDeviceToken {
	
	private int id;
	private String uid;
	private String deviceToken;
	private Date lastLoginTime;
	
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof IphoneDeviceToken)) return false;
		IphoneDeviceToken iphoneDeviceToken = (IphoneDeviceToken)obj;
		boolean isEqual = true;
		if(!deviceToken.equals(iphoneDeviceToken.getDeviceToken())) {
			isEqual = false;
		} else if(!uid.equals(iphoneDeviceToken.getUid())) {
			isEqual = false;
		}
		return isEqual;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

}
