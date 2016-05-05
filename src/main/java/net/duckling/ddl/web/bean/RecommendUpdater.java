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
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.user.SimpleUser;


/**
 * @date Mar 31, 2011
 * @author xiejj@cnic.cn
 */
public class RecommendUpdater extends MessageUpdater{
	private Map<String, SimpleUser> cacheUser;
	public RecommendUpdater(Map<String, SimpleUser> cacheUser){
		this.cacheUser = cacheUser;
	}
	public MessageDisplay create(Message message) {
		return RecommendDisplay.getInstance(message);
	}
	public void update(MessageDisplay display, Message instance) {
		RecommendDisplay currDisplay =(RecommendDisplay)display;
		//存推荐的评论
		String senderId = instance.getBody().getFrom();
		currDisplay.getSenderMap().put(senderId, cacheUser.get(senderId));
		if(instance.getBody().getRemark()!=null&&instance.getBody().getRemark().length()!=0) {
			currDisplay.getRemarkMap().add(currDisplay.getSenderMap().get(senderId).getName()+":"+instance.getBody().getRemark());
		}
	}
	public void afterMerge(List<MessageDisplay> results) {
		//DO Nothing
	}
	public List<Message> beforeMerge(List<Message> messages) {
		List<Message> recommends = new ArrayList<Message>();
		for (Message current : messages) {
			if(Publisher.isRecommend(current.getPublisher())){
				recommends.add(current);
			}
		}
		return recommends;
	}
}
