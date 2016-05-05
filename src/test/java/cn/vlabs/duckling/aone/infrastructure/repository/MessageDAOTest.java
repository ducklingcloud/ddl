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

package cn.vlabs.duckling.aone.infrastructure.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.MessageBody;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.impl.MessageDAO;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

/**
 * MessageDAO 的测试类
 * 
 * @date Mar 2, 2011
 * @author xiejj@cnic.cn
 */
public class MessageDAOTest extends BaseTest {
	private MessageDAO md;
	private int tid=1;
	@Before
	public void setUp() throws Exception {
		md = f.getBean(MessageDAO.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		md = null;
	}
	@Test
	public void testCreateMessage() {
		Date now = new Date();
		Date today = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
		Date tommrow = DateUtils.truncate(DateUtils.addDays(now, 1),
				Calendar.DAY_OF_MONTH);

		MessageBody body = createMessage(now);
		Publisher publisher = createPublisher(Publisher.PAGE_TYPE);
		
		int messageId = md.createMessage(tid,body, publisher,
				new String[] { "liji@cnic.cn","xiejj@cnic.cn" });
		try{
			
			List<Message> message = md.getMessage(tid,"xiejj@cnic.cn", today, tommrow);
			
			assertNotNull("Must be one message", message);
			assertEquals("Only one message", 1, message.size());
			assertEquals("Title expected","New message from column A", message.get(0).getBody().getTitle());
		}finally{
			md.removeMessage(messageId);
		}
	}

	@Test
	public void testGetMessageWithPublisher(){
		Date now = new Date();
		Date today = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
		Date tommrow = DateUtils.truncate(DateUtils.addDays(now, 1),
				Calendar.DAY_OF_MONTH);

		MessageBody body = createMessage(now);

		Publisher columnPublisher = createPublisher(Publisher.PAGE_TYPE);
		int columnMessage = md.createMessage(tid,body, columnPublisher,
				new String[] { "liji@cnic.cn","xiejj@cnic.cn" });
		
		Publisher pagePublisher = createPublisher(Publisher.PAGE_TYPE);
		int pageMessage = md.createMessage(tid,body, pagePublisher, new String[] {"xiejj@cnic.cn"});
		
		try{
			
			List<Message> allMessage = md.getMessage(tid,"xiejj@cnic.cn", today, tommrow);
			
			assertNotNull(allMessage);
			assertEquals(2, allMessage.size());
			assertEquals("New message from column A", allMessage.get(0).getBody().getTitle());
			
		}finally{
			md.removeMessage(pageMessage);
			md.removeMessage(columnMessage);
		}
	}

	private MessageBody createMessage(Date now) {
		MessageBody body = new MessageBody();
		body.setDigest("This is a digest.");
		body.setTitle("New message from column A");
		body.setRid(23);
		body.setFrom("admin@root.umt");
		body.setTime(now);
		body.setType("PageCreated");
		return body;
	}


	private Publisher createPublisher(String type) {
		Publisher publisher = new Publisher();
		publisher.setId(1);
		publisher.setType(type);
		return publisher;
	}
}
