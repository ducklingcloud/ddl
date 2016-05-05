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
package net.duckling.ddl.service.mobile.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;

import org.apache.log4j.Logger;


public class MobileNoticeQueue {
	private static final Logger LOG = Logger.getLogger(MobileNoticeQueue.class);
	private static final BlockingQueue<Notice> queue = new LinkedBlockingQueue<Notice>();

	private static boolean filter(Notice notice){
		return isAcceptNoticeType(notice.getNoticeType());
	}
    private static boolean isAcceptNoticeType(String type){
		return NoticeRule.PERSON_NOTICE.equals(type)||NoticeRule.MONITOR_NOTICE.equals(type);
	}
	
	public static void add(List<Notice> notices){
		if(notices!=null){
			for(Notice n : notices){
				if(filter(n)){
					queue.add(n);
				}
			}
		}
	}
	public static void addRemoveNotice(String uid, int tid, String type, Set<Integer> eventIds){
		if(isAcceptNoticeType(type)){
			Notice e = new Notice();
			e.setRecipient(uid);
			e.setTid(tid);
			//可加入事件id等。
			e.setNoticeType("noticeRemove");
			queue.add(e);
		}
	}
	
	public static Notice take(){
		try {
			return queue.take();
		} catch (InterruptedException e) {
			LOG.info("", e);
			return null;
		}
	}
	
}
