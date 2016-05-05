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
package net.duckling.ddl.service.team.impl;

import java.util.List;
import java.util.Set;

import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.UserNoticeCount;

public interface TeamPreferenceDAO {
	TeamPreferences getTeamPreferences(String uid, int tid);
	/**
	 * @param tids
	 * @return
	 */
	List<TeamPreferences> getAllTeamPreferencesByTids(Set<Integer> tids);

	List<TeamPreferences> getTeamPreferencesByUID(String uid, int filterTeam);

	// get uid according tid
	List<TeamPreferences> getUidByTid(int tid);
	boolean updatePreferene(final TeamPreferences pre);
	/**
	 * 获取用户各个未读消息计数
	 * 
	 * @param uid
	 * @return
	 */
	UserNoticeCount getUserNoticeCount(String uid);
	/**
	 * 添加用户消息
	 * 
	 * @param uids
	 * @param tid
	 * @param type
	 * @param eventId
	 */
	void batchIncreaseNoticeCount(final String[] uids, final int tid,
			final String type, final int eventId);

	void updateNoticeAccessTime(String uid, int tid, String type);

	void updateAllMessageCount(String uid, String type);

}
