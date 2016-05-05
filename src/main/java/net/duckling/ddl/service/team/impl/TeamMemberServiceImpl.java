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

import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.falcon.api.cache.ICacheService;

/**
 * @date 2012-2-16
 * @author clive
 */

public class TeamMemberServiceImpl implements TeamMemberService {
	/**
	 * 团队成员数量缓存key前缀
	 */
	private static final String TEAM_MEMBER_AMOUNT = "team-member-amount";
	
	@Override
	public void addTeamMembers(String[] newUsers, int tid) {
		teamMemberDao.addTeamMembers(newUsers, tid);
		
		clearMemberAmountCache(tid);
	}
	@Override
	public boolean checkTeamValidity(String uid, int tid) {
		return isUserInTeam(tid, uid);
	}
	@Override
	public List<UserExt> getTeamContacts(int tid) {
		return teamMemberDao.getTeamContacts(tid);
	}

	@Override
	public List<SimpleUser> getTeamMembersByName(int tid, String name) {
		return teamMemberDao.getMembersByName(tid, name);
	}

	@Override
	public List<SimpleUser> getTeamMembersByPinyin(int tid, String pinyin) {
		return teamMemberDao.getMembersByPinyin(tid, pinyin);
	}
	@Override
	public List<SimpleUser> getTeamMembersOrderByName(int tid) {
		return teamMemberDao.getMembersOrderByName(tid);
	}
	@Override
	public List<UserExt> getUserExtContacts(int tid) {
		return teamMemberDao.getUserExtContacts(tid);
	}
	@Override
	public boolean isAtTheSameTeam(String user1, String user2) {
		return teamMemberDao.isAtTheSameTeam(user1, user2);
	}

	@Override
	public boolean isUserInTeam(int tid, String curUid) {
		Boolean[] result= teamMemberDao.isUsersInTeam(tid, new String[]{curUid});
		return result[0];
	}

	@Override
	public Boolean[] isUsersInTeam(int tid, String[] uids) {
		return teamMemberDao.isUsersInTeam(tid, uids);
	}

	@Override
	public int updateTeamsSequence(String uid, String[] ids) {
		return teamMemberDao.updateTeamSequence(uid, ids).length;
	}

	@Override
	public void removeMembers(int tid, String[] uids) {
		teamMemberDao.removeMembers(tid, uids);
		
		clearMemberAmountCache(tid);
	}
	
	@Override
	public int getMemberAmount(int tid) {
		Integer amount = getMemberAmountCache(tid);
		if(amount==null){
			amount = new Integer(teamMemberDao.getMemberAmount(tid));
			setMemberAmountCache(tid,amount);
		}
		return amount;
	}
	
	private void clearMemberAmountCache(int tid){
		memcachedService.remove(TEAM_MEMBER_AMOUNT + tid);
	}
	private Integer getMemberAmountCache(int tid){
		Object cache = memcachedService.get(TEAM_MEMBER_AMOUNT + tid);
		Integer amount = null;
		if(cache!=null){
			amount = (Integer)amount;
		}
		return amount;
	}
	private void setMemberAmountCache(int tid, Integer amount){
		memcachedService.set(TEAM_MEMBER_AMOUNT + tid, amount);
	}
	
	private TeamMemberDAO teamMemberDao;
	private ICacheService memcachedService;
	
	public void setTeamMemberDao(TeamMemberDAO teamMemberDao) {
		this.teamMemberDao = teamMemberDao;
	}
	public void setMemcachedService(ICacheService memcachedService) {
		this.memcachedService = memcachedService;
	}
}
