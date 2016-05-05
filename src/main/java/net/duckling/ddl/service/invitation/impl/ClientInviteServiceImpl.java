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
package net.duckling.ddl.service.invitation.impl;

import java.util.List;

import net.duckling.ddl.service.authenticate.UserPrincipal;
import net.duckling.ddl.service.invitation.ClientInviteService;
import net.duckling.ddl.service.mail.MailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientInviteServiceImpl implements ClientInviteService {
	@Autowired
	private ClientInviteDAO inviteDao;
	@Autowired
	private ClientMessageDAO messageDao;

	@Autowired
	private MailService mailService;

	private static final String INVITE_MESSAGE = "<body>尊敬的%s<br/>"
			+ "您好！<br/>" + "%s(%s)想给您发送加密文档。但您没有安装文档库加密版客户端。<br/>"
			+ "安装完后他就可以给您发送加密文档了。</body>";

	@Override
	public void invite(UserPrincipal invitor, String invitee) {
		inviteDao.save(invitor.getName(), invitee);
		String content = String.format(INVITE_MESSAGE, invitee,
				invitor.getFullName(), invitor.getName());
		mailService.sendSimpleMail(new String[] { invitee }, "分享邀请",
				content);
	}

	@Override
	public void accept(String invitee) {
		List<String> invitors = inviteDao.getInvitors(invitee);
		if (invitors != null && invitors.size() > 0) {
			inviteDao.markAccept(invitee);
			messageDao.saveMessage(invitors, "用户" + invitee
					+ "已经安装了团队文档库加密版，您现在可以给他（她）分享加密文件了。");
		}
	}

	@Override
	public List<String> readMessage(String username) {
		List<String> messages = messageDao.readMessage(username);
		messageDao.markMessageReaded(username);
		return messages;
	}
}
