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
package net.duckling.ddl.service.mobile.android;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.mobile.impl.Notice2Message;
import net.duckling.ddl.service.mobile.impl.NoticeHandler;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.UserNoticeCount;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 动态持有用户通知信息
 * @author zhonghui
 *
 */
@Service("androidNoticeHandler")
public class AndroidNoticeHandler implements NoticeHandler{
	private  class UserNoticeHandle {
		private static final long EXPIRED = 1000 * 60 * 30;
		private long latestGetTime;
		private LinkedList<Notice> notices;
		private String sessionId;
		private String uid;

		public UserNoticeHandle(String uid, String sessionId) {
			this.uid = uid;
			this.sessionId = sessionId;
			latestGetTime = System.currentTimeMillis();
			notices = new LinkedList<Notice>();
		}

		private int getUserMessageCount() {
			UserNoticeCount u = teamPreferenceService.getUserNoticeCount(uid);
			if (u != null) {
				return u.getMonitorNoticeCount() + u.getPersonNoticeCount();
			}
			return 0;
		}

		private void refreshGetTime() {
			latestGetTime = System.currentTimeMillis();
		}

		public synchronized void addNotice(Notice n) {
			notices.add(n);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof UserNoticeHandle)) {
				return false;
			}
			UserNoticeHandle o = (UserNoticeHandle) obj;
			return StringUtils.equals(uid, o.uid)
					&& StringUtils.equals(sessionId, o.sessionId);
		}

		public synchronized AndroidMessageBean getNowMessage() {
			refreshGetTime();
			if (notices.isEmpty()) {
				return AndroidMessageBean.getEmptyMessage(uid);
			}
			int n = 0;
			boolean haveRemove = false;
			Notice lastNotice = null;
			Notice tmp = null;
			Set<Integer> tids = new HashSet<Integer>();
			while ((tmp = notices.poll()) != null) {
				tids.add(tmp.getTid());
				if ("noticeRemove".equals(tmp.getNoticeType())) {
					lastNotice = null;
					n = 0;
					haveRemove = true;
					continue;
				}
				n++;
				lastNotice = tmp;
			}
			AndroidMessageBean result = new AndroidMessageBean();
			result.setLatestCount(n);
			if (lastNotice != null) {
				result.setMessage(Notice2Message.convert(lastNotice));
				result.setLatestMessageTeamId(lastNotice.getTid());
			} else {
				Notice notice = noticeService.getUserLatestNotice(uid);
				if (notice != null) {
					result.setLatestMessageTeamId(notice.getTid());
				}
			}
			if (haveRemove) {
				result.setType(AndroidMessageBean.MESSAGE_TYPE_REFRESH);
				result.setMessageCount(getUserMessageCount());
			} else {
				result.setType(AndroidMessageBean.MESSAGE_TYPE_ADD);
			}
			result.setMoreTeamMessage(tids.size() > 1);
			return result;
		}

		public String getSessionId() {
			return sessionId;
		}

		public boolean isExpired() {
			return (latestGetTime + EXPIRED) < System.currentTimeMillis();
		}
	}
	
	
	private static long refreshTime = 0;
	@Autowired
	private INoticeService noticeService;
	@Autowired
	private TeamPreferenceService teamPreferenceService;

	private Map<String, Set<UserNoticeHandle>> userNoticeHandlers = new ConcurrentHashMap<String, Set<UserNoticeHandle>>();

	private void addUser(String uid, String sessionId) {
		UserNoticeHandle handle = new UserNoticeHandle(uid, sessionId);
		Set<UserNoticeHandle> users = userNoticeHandlers.get(uid);
		if (users == null || users.isEmpty()) {
			users = new LinkedHashSet<UserNoticeHandle>();
		}
		users.add(handle);
		userNoticeHandlers.put(uid, users);
	}

	private AndroidMessageBean getNowMessageFromDB(String uid) {
		AndroidMessageBean bean = new AndroidMessageBean();
		bean.setType(AndroidMessageBean.MESSAGE_TYPE_REFRESH);
		UserNoticeCount u = teamPreferenceService.getUserNoticeCount(uid);
		bean.setMessageCount(u.getMonitorNoticeCount()
				+ u.getPersonNoticeCount());
		bean.setUid(uid);
		return bean;
	}

	private UserNoticeHandle getUserNoticeHandle(String uid, String sessionId) {
		Set<UserNoticeHandle> users = userNoticeHandlers.get(uid);
		if (users == null || users.isEmpty()) {
			addUser(uid, sessionId);
			return null;
		}
		for (UserNoticeHandle u : users) {
			if (u.getSessionId().equals(sessionId)) {
				return u;
			}
		}
		addUser(uid, sessionId);
		return null;
	}

	private void refresh() {
		long d = System.currentTimeMillis();
		if ((refreshTime + 1000 * 60 * 15) < d) {
			refreshTime = d;
			Iterator<Set<UserNoticeHandle>> it = userNoticeHandlers.values()
					.iterator();
			while (it.hasNext()) {
				Set<UserNoticeHandle> handle = it.next();
				if (handle != null) {
					Iterator<UserNoticeHandle> i = handle.iterator();
					while (i.hasNext()) {
						UserNoticeHandle h = i.next();
						if (h.isExpired()) {
							i.remove();
						}
					}
				}
			}
		}
	}
	/**
	 * 添加一个用户通知
	 */
	public void addNotice(Notice notice) {
		Set<UserNoticeHandle> n = userNoticeHandlers.get(notice.getRecipient());
		if (n != null && !n.isEmpty()) {
			refresh();
			for (UserNoticeHandle handle : n) {
				handle.addNotice(notice);
			}
		}
	}
	
	/**
	 * 获取用户通知信息
	 * @param uid
	 * @param sessionId
	 * @return
	 */
	public AndroidMessageBean getUserMessage(String uid, String sessionId) {
		UserNoticeHandle users = getUserNoticeHandle(uid, sessionId);
		if (users == null) {
			return getNowMessageFromDB(uid);
		}
		return users.getNowMessage();
	}
}