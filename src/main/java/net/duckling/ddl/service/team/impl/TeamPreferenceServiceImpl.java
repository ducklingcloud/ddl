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

import net.duckling.ddl.service.mobile.impl.MobileNoticeQueue;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.UserNoticeCount;

public class TeamPreferenceServiceImpl implements TeamPreferenceService{
	private TeamPreferenceDAO teamPreferenceDAO;
	public void setTeamPreferenceDao(TeamPreferenceDAO teamPreferenceDAO){
		this.teamPreferenceDAO = teamPreferenceDAO;
	}
	@Override
	public List<TeamPreferences> getAllTeamPreferencesByTids(Set<Integer> tids) {
		return teamPreferenceDAO.getAllTeamPreferencesByTids(tids);
	}
	@Override
	public List<TeamPreferences> getAllTeamPrefs(String uid) {
		return teamPreferenceDAO.getTeamPreferencesByUID(uid, 0);
	}
	@Override
	public TeamPreferences getTeamPreferences(String uid, int tid) {
		return teamPreferenceDAO.getTeamPreferences(uid, tid);
	}
	@Override
	public List<TeamPreferences> getTeamPrefWithoutTeam(String uid, int tid) {
		return teamPreferenceDAO.getTeamPreferencesByUID(uid, tid);
	}
	@Override
	public List<TeamPreferences> getUidByTid(int tid) {
		return teamPreferenceDAO.getUidByTid(tid);
	}
	@Override
	public UserNoticeCount getUserNoticeCount(String uid) {
		return teamPreferenceDAO.getUserNoticeCount(uid);
	}
	@Override
	public void increaseNoticeCount(String[] uids, int tid, String type,
			int enventId) {
		teamPreferenceDAO.batchIncreaseNoticeCount(uids, tid, type, enventId);
	}
	@Override
	public void updateOneNoticeAccessTime(String uid, int tid, String type,
			Set<Integer> eventIds) {
		TeamPreferences pre = teamPreferenceDAO.getTeamPreferences(uid, tid);
		if (pre != null) {
			pre.removeOneNotice(eventIds, type);
			teamPreferenceDAO.updatePreferene(pre);
			MobileNoticeQueue.addRemoveNotice(uid, tid, type, eventIds);
		}
	}
	@Override
	public void updateAllMessage(String uid, String type) {
		teamPreferenceDAO.updateAllMessageCount(uid, type);
	}

	@Override
	public void updateNoticeAccessTime(String uid, int tid, String type) {
		teamPreferenceDAO.updateNoticeAccessTime(uid, tid, type);
		MobileNoticeQueue.addRemoveNotice(uid, tid, type, null);
	}
}