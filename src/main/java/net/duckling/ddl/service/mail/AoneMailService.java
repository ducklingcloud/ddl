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

import java.util.List;

import net.duckling.ddl.service.devent.DEvent;
import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.SimpleUser;

public interface AoneMailService {

	/**
	 * 使用已存在的用户接受邀请的通知审核邮件
	 * 
	 * @param admin
	 * @param instance
	 * @param curUser
	 * @param teamName
	 */
	void sendInvitationExisterUser(List<String> admin, Invitation instance,
			String curUser, String teamName, String configURL);
    /**
     * 
     * 发送分享邮件
     * 
     * @param e
     * @param userIds
     */
	void sendEmail(DEvent e, String[] userIds);
	/**
	 * 使用别的用户接受邀请的通知审核邮件
	 */
	void sendInvitationChangeUser(List<String> admin, Invitation instance,
			String curUser, String teamName, String configURL);

	/**
	 * 发送用户申请加入团队的信息给团队管理员
	 * 
	 * @param admin
	 * @param curUser
	 * @param teamName
	 * @param url
	 */
	void sendApplyToTeamAdmin(List<String> admin, SimpleUser user,
			String teamName, String url);

	void sendApplyResultToUser(String uid, String teamName, String teamUrl,
			boolean flag);

	public void sendMentionEmail(DEvent e, String[] userIds);

	void sendInvitationMail(String teamName, int tid, String inviter,
			List<String> invitees, String message, String teamDispalyName);

	void sendActivationMail(Activation instance, String activationURL);

	/**
	 * 给自己发邮件，快速分享时候
	 * */
	void sendShareSuccessMailWithoutActivation(String email, String userName,
			String[] fileURL, String[] fileNames,String[]shareUser);

	void sendShareSuccessMail(Activation instance, String activationURL,
			String[] fileURL, String[] fileNames);

	/** 给享受者发邮件 */
	void sendAccessFileMail(String[] fileNames, String[] fileURLs,
			String fileOwner, String receiver, String message);
}