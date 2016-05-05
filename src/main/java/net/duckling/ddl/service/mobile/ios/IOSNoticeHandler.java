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
package net.duckling.ddl.service.mobile.ios;

import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.mobile.DeviceTokenService;
import net.duckling.ddl.service.mobile.IphoneDeviceToken;
import net.duckling.ddl.service.mobile.impl.Notice2Message;
import net.duckling.ddl.service.mobile.impl.NoticeHandler;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.UserNoticeCount;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iosNoticeHandler")
public class IOSNoticeHandler implements NoticeHandler {
	private final Logger LOG = Logger.getLogger(IOSNoticeHandler.class);
	@Autowired
	private DeviceTokenService deviceTokenService;
	@Autowired
	private IOSMessageSender sender;
	@Autowired
	private TeamPreferenceService teamPreferenceService;

	private IOSMessageBean getMessageBean(Notice notice, IphoneDeviceToken token) {
		UserNoticeCount count = teamPreferenceService.getUserNoticeCount(notice
				.getRecipient());
		if (count == null) {
			return null;
		}
		IOSMessageBean bean = new IOSMessageBean();
		bean.setNoticeCount(count.getMonitorNoticeCount()
				+ count.getPersonNoticeCount());
		bean.setDeviceToken(token.getDeviceToken());
		bean.setUid(notice.getRecipient());
		if ("noticeRemove".equals(notice.getNoticeType())) {
		} else {
			bean.setMessage(Notice2Message.convert(notice));
			bean.setSound("default");
		}
		return bean;
	}

	public void addNotice(Notice notice) {
		String uid = notice.getRecipient();
		IphoneDeviceToken token = deviceTokenService.getTokenByUid(uid);
		if (token != null) {
			try {
				sender.sendMessage(getMessageBean(notice, token));
			} catch (JSONException e) {
				LOG.error("", e);
			}
		}
	}
}
