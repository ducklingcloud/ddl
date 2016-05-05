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
package net.duckling.ddl.web.bean;

import java.util.Date;

import org.springframework.util.StringUtils;
/**
 *accessToken 校验后生成临时code
 * @author zhonghui
 *
 */
public class AuthorizationCode {
	public static final String STATUS_USED = "used";
	public static final String STATUS_AVAILABLE = "available";
	public static final String STATUS_EXPIRED = "expired";
	
	
	private int id;
	private String code;
	private String accessToken;
	private Date createTime;
	private String uid;
	private String clientId;
	private String status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isAvailable(){
		return StringUtils.isEmpty(getStatus())&&!isExpired();
		
	}
	public boolean isUsed(){
		return STATUS_USED.equals(getStatus());
	}
	
	public boolean isExpired(){
		return (getCreateTime().getTime()+5*1000*60)<System.currentTimeMillis();
	}
}
