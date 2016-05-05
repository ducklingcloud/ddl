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
package net.duckling.ddl.service.mail.impl;

import java.util.List;

import net.duckling.ddl.service.mail.EmailAttachment;
import net.duckling.ddl.service.mail.EmailAttachmentService;
import net.duckling.ddl.service.resource.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailAttachmentServiceImpl implements EmailAttachmentService {
	@Autowired
	private EmailAttachmentDAO emailAttachmentDAO;
	public int createEmailAttach(EmailAttachment attachment) {
		return emailAttachmentDAO.create(attachment);
	}

	public List<EmailAttachment> getEmailAttachByUidAndTid(String uid, int tid) {
		return emailAttachmentDAO.findByUserAndTid(uid, tid);
	}

	public List<EmailAttachment> getEmailAttachByMid(String mid) {
		return emailAttachmentDAO.findByMid(mid);
	}

	public boolean updateEmailAttach(EmailAttachment attachment) {
		return emailAttachmentDAO.update(attachment);
	}

	public boolean deleteEmailAttach(int id) {
		return emailAttachmentDAO.delete(id);
	}

	@Override
	public List<Resource> getFileByEmailMidAndUid(String mid, int tid, String uid) {
		return emailAttachmentDAO.getFileByEmailMidAndUid(mid, tid, uid);
	}

	@Override
	public List<Resource> getFileByTid(int[] tids, int offset, int rows) {
		return emailAttachmentDAO.getFileByTid(tids, offset, rows);
	}

	@Override
	public int getTeamFileCount(int[] tids) {
		return emailAttachmentDAO.getTeamFileCount(tids);
	}

	@Override
	public List<Resource> getFileByTidAndTitle(String title, int tid) {
		return emailAttachmentDAO.getFileByTidAndTitle(title, tid);
	}

	

	

}
