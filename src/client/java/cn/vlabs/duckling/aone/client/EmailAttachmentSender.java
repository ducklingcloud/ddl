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
package cn.vlabs.duckling.aone.client;

import java.io.InputStream;

public final class EmailAttachmentSender {
	private EmailAttachmentSender(){}

	/**
	 * 让邮件系统利用当前接口将邮件中的附件推送到DDL（文档库）的指定账户中，
	 * 并返回该附件在DDL中的URL，
	 * 以供邮件系统维护附件在DDL中的映射关系。
	 * 接口中只有一个静态方法，以供外部调用。
	 * @param fileName 附件名称；
	 * @param email 当前邮件；
	 * @param attachmentStream 当前附件输入流；
	 * @param clbAddress clb的ip地址；
	 * @return
	 */
	public static AttachmentPushResult sendAttachToDDL(String fileName,String email,InputStream attachmentStream,String clbAddress){
		
		
		return null;
	}
}
