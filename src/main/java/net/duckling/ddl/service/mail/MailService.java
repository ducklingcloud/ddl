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

package net.duckling.ddl.service.mail;


/**
 * 邮件服务接口
 * @date Mar 15, 2010
 * @author xiejj@cnic.cn
 */
public interface MailService{
	/**
	 * 发送邮件
	 * @param mail 邮件接口
	 */
	void sendMail(Mail mail);
	
	/**
	 * 发送简单邮件
	 * @param address 接受地址，队列
	 * @param title 邮件标题，主题
	 * @param content 邮件内容
	 * */
	void sendSimpleMail(String[] address,String title,String content);
	
	/**
	 * 发送简单邮件
	 * @param address 接受地址，队列
	 * @param title 邮件标题，主题
	 * @param content 邮件内容
	 * */
	void sendSimpleMail(String[] address,String from,String title,String content);
}
