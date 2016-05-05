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
package net.duckling.ddl.service.mobile.android;

public class AndroidMessageBean {
	public static final String MESSAGE_TYPE_NOMESSAGE="no_message";
	public static final String MESSAGE_TYPE_REFRESH="refresh_message";
	public static final String MESSAGE_TYPE_ADD="add_message";
	private String uid;
	private String message;
	private int messageCount;
	private String type;
	private int latestCount;
	private boolean moreTeamMessage;
	private int latestMessageTeamId;
	
	
	public int getLatestMessageTeamId() {
		return latestMessageTeamId;
	}

	public void setLatestMessageTeamId(int latestMessageTeamId) {
		this.latestMessageTeamId = latestMessageTeamId;
	}

	public boolean isMoreTeamMessage() {
		return moreTeamMessage;
	}

	public void setMoreTeamMessage(boolean moreTeamMessage) {
		this.moreTeamMessage = moreTeamMessage;
	}

	public boolean isNoMessage(){
		return MESSAGE_TYPE_NOMESSAGE.equals(type);
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLatestCount() {
		return latestCount;
	}
	public void setLatestCount(int latestCount) {
		this.latestCount = latestCount;
	}
	
	public static AndroidMessageBean getEmptyMessage(String uid){
		AndroidMessageBean result = new AndroidMessageBean();
		result.setUid(uid);
		result.setType(MESSAGE_TYPE_NOMESSAGE);
		return result;
	}
}
