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

package net.duckling.ddl.service.subscribe;




/**
 * @date 2011-2-28
 * @author Clive Lee
 */
public class Subscription {
    private int id;
	private String userId;
	private Publisher publisher;
	private NotifyPolicy notifyPolicy;
	private int tid;
	
	public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
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
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the publisher
	 */
	public Publisher getPublisher() {
		return publisher;
	}
	/**
	 * @param publisher
	 *            the publisher to set
	 */
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
	/**
	 * @return the notifyPolicy
	 */
	public NotifyPolicy getNotifyPolicy() {
		return notifyPolicy;
	}
	/**
	 * @param notifyPolicy
	 *            the notifyPolicy to set
	 */
	public void setNotifyPolicy(NotifyPolicy notifyPolicy) {
		this.notifyPolicy = notifyPolicy;
	}

}
