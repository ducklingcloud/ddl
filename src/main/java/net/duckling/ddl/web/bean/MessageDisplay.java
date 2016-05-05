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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.subscribe.Message;



/**
 * @date 2011-3-23
 * @author Clive Lee
 */
public abstract class MessageDisplay  {
	
	public static final String COMMENT = "comment";
	public static final String SEND_COMMENT = "send_comment";
	public static final String FEED_COMMENT = "feed_comment";
	public static final String RECOMMEND = "recommend";
	public static final String SUBSCRIPTION = "subscription";
	public static final String PAGE_FEED = "page_feed";
	public static final String PERSON_FEED = "person_feed";
	
	private Date createTime; 
	private int status;  //同种状态的合并到一起
	private String type;
	private String receiver;
	
	public static Comparator<MessageDisplay> comparator = new Comparator<MessageDisplay>() {
		public int compare(MessageDisplay m1,MessageDisplay m2) {
			return m1.getCreateTime().after(m2.getCreateTime())?-1:1;
		}
	};
	
	public static void sort(MessageDisplay[] messages) {
		Arrays.sort(messages,MessageDisplay.comparator);
	}
	
	public static MessageDisplay[] mergeAndSort(MessageDisplay[] list1,MessageDisplay[] list2) {
		List<MessageDisplay> tempList = new ArrayList<MessageDisplay>();
		for(MessageDisplay curr:list1){
			tempList.add(curr);
		}
		for(MessageDisplay curr:list2){
			tempList.add(curr);
		}
		MessageDisplay[] results = tempList.toArray(new MessageDisplay[0]);
		MessageDisplay.sort(results);
		return results;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	public boolean isNewMessage(){
		return status==Message.NEW_STATUS;
	}
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
}
