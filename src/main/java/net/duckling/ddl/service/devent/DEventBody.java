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

package net.duckling.ddl.service.devent;

import java.util.Date;





/**
 * @date 2011-10-31
 * @author clive
 */
public class DEventBody {
	
	private int id;
	private int tid;
	private String actor;
	private String operation;
	private DEntity target;
	private String recipients;
	private String message;
	private Date occurTime;
	private int targetVersion;
	
	/**
	 * 用于控制邮件是否是群发，还是单个发送
	 */
	private String emailSendType;
	public String getEmailSendType() {
		return emailSendType;
	}
	public void setEmailSendType(String emailSendType) {
		this.emailSendType = emailSendType;
	}
	/**
	 * @return the targetVersion
	 */
	public int getTargetVersion() {
		return targetVersion;
	}
	/**
	 * @param targetVersion the targetVersion to set
	 */
	public void setTargetVersion(int targetVersion) {
		this.targetVersion = targetVersion;
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
	 * @return the actor
	 */
	public String getActor() {
		return actor;
	}
	/**
	 * @param actor the actor to set
	 */
	public void setActor(String actor) {
		this.actor = actor;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String action) {
		this.operation = action;
	}
	/**
	 * @return the target
	 */
	public DEntity getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(DEntity target) {
		this.target = target;
	}
	/**
	 * @return the recipients
	 */
	public String getRecipients() {
		return recipients;
	}
	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the occurTime
	 */
	public Date getOccurTime() {
		return occurTime;
	}
	/**
	 * @param occurTime the occurTime to set
	 */
	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}
}
