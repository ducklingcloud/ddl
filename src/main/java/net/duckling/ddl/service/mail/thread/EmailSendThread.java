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
/**
 * 
 */
package net.duckling.ddl.service.mail.thread;

import java.util.concurrent.LinkedBlockingQueue;

import net.duckling.ddl.service.mail.MailService;
import net.duckling.ddl.service.mail.impl.SimpleEmail;

import org.apache.log4j.Logger;


/**
 * 发送邮件队列 ,解决一下子发太多页面假死的问题
 * 
 * @author lvly
 * @since 2012-11-27
 */
public class EmailSendThread extends Thread {
	private static final Logger LOG = Logger.getLogger(EmailSendThread.class);
	private static LinkedBlockingQueue<SimpleEmail> emailQueue = new LinkedBlockingQueue<SimpleEmail>();
	private boolean flag = true;
	private MailService mailService;

	public static void addEmail(SimpleEmail email) {
		emailQueue.add(email);
	}
	public void setMailService(MailService mailService){
		this.mailService = mailService;
	}
	
	public EmailSendThread() {
		super("EmailSendThread");
		this.setDaemon(true);
	}
	public void shutdown() {
		this.flag = false;
		interrupt();
	}

	@Override
	public void run() {
		while (flag) {
			SimpleEmail email;
			try {
				email = emailQueue.take();
				if (email == null) {
					continue;
				}
				if(emailQueue.isEmpty()){
					Thread.sleep(10000);
				}
				mailService.sendSimpleMail(email.getEmail(),email.getFrom(), email.getTitle(),email.getContent());
			} catch (Throwable e){
				LOG.error(e.getMessage(), e);
				continue;
			}
		}
	}

}
