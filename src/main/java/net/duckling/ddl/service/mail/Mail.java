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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Introduction Here.
 * 
 * @date Mar 15, 2010
 * @author xiejj@cnic.cn
 */
public class Mail {
	private String subject;

	private String template;

	private String recipient;

	private Properties props = new Properties();

	private String contentType;
	
	private List<String> attachments = new ArrayList<String>();

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getRecipient() {
		return recipient;
	}

	public void putParam(String paramName, String paramValue) {
		props.put(paramName, paramValue);
	}
	
	public void addAttachment(String fName) {
		attachments.add(fName);
	}
	
	public List<String> getAttachments(){
		return attachments;
	}

	public String getMessage() {
		if (template == null || props == null || props.size() == 0)
		{
			return template;
		}
		String message = template;
		Set<Object> keys = props.keySet();
		for (Object key : keys) {
			message = message.replaceAll((String) key, props
					.getProperty((String) key));
		}
		return message;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
