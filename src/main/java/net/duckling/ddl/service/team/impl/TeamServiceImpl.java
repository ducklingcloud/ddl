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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamCreateInfo;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.team.TeamState;
import net.duckling.ddl.service.team.dao.TeamCreateInfoDAOImpl;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.DateUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.SiteUtil;
import net.duckling.ddl.util.TeamInitUtil;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @date 2011-5-11
 * @author Clive Lee
 */

public class TeamServiceImpl implements TeamService, VmtUserManager{

	private static final String TEAM_NAME = "team-name";
	private static final String TEAM_ID = "team-id";
	private static final Logger LOG = Logger.getLogger(TeamServiceImpl.class);
	private AoneUserService aoneUserService;
	private AuthorityService authorityService;
	private ICacheService memcachedService;
	private TeamDAO teamDao;	
	private TeamMemberService teamMemberService;
	private TeamPreferenceService teamPreferenceService;
	private TeamCreateInfoDAOImpl teamCreateInfoDao;
	
	public void setTeamCreateInfoDao(TeamCreateInfoDAOImpl teamCreateInfoDao) {
		this.teamCreateInfoDao = teamCreateInfoDao;
	}

	public void setTeamPreferenceService(TeamPreferenceService teamPreferenceService){
		this.teamPreferenceService= teamPreferenceService;
	}
	private VMTTeamManager vmtTeamManager;

	private Team addTeamToCache(int tid) {
		Team team = getTeamFromCache(tid);
		if (team == null) {
			team = teamDao.getTeamById(tid);
			if (team == null) {
				return null;
			}
			setTeamCache(tid, team);
		}
		return team;
	}

	private int createUserDefaultTeam(String user, String userName) {
		Map<String, String> params = initTeamConfig(user, userName);
		LOG.info("createUserDefaultTeam user=" + user + ";userName=" + userName);
		int tid = createAndStartTeam(user, params);
		if (tid != -1) {
			addTeamMembers(tid, new String[] { user },
					new String[] { userName }, new String[]{"admin"});
		}
		return tid;
	}

	private Team getTeamFromCache(int tid) {
		return (Team) memcachedService.get(TEAM_ID + "." + tid);
	}
	private void updateTeamCache(Team team){
		memcachedService.remove(TEAM_ID + "." + team.getId());
		memcachedService.set(TEAM_ID + "." + team.getId(), team);
	}
	
	private void removeTeamCache(Team team){
		memcachedService.remove(TEAM_ID + "." + team.getId());
		memcachedService.remove(TEAM_NAME + "." + team.getName());
	}

	private Integer getTidFromCache(String name) {
		return (Integer) memcachedService.get(TEAM_NAME + "."
				+ name);
	}


	private void initTeamCache(int tid) {
		Team team = teamDao.getTeamById(tid);
		setTeamCache(tid, team);
	}

	/*---------------- Domain Methods End---------------------------*/
	private Map<String, String> initTeamConfig(String user, String userName) {
		Map<String, String> params = new HashMap<String, String>();
		String teamName = getTeamNameFromEmail(user);
		params.put(KeyConstants.TEAM_TYPE, "personal");
		if (!StringUtils.isEmpty(userName)) {
			params.put(KeyConstants.SITE_DISPLAY_NAME, userName + "的个人空间");
		} else {
			params.put(KeyConstants.SITE_DISPLAY_NAME, teamName);
		}
		params.put(KeyConstants.SITE_NAME_KEY, teamName);
		params.put(KeyConstants.SITE_CREATOR, user);
		params.put(KeyConstants.SITE_DESCRIPTION, "");
		params.put(KeyConstants.TEAM_ACCESS_TYPE, Team.ACCESS_PRIVATE);
		params.put(KeyConstants.TEAM_DEFAULT_MEMBER_AUTH, Team.AUTH_EDIT);
		return params;
	}

	private void initTeamVmtdn(Team team){
		if(StringUtils.isEmpty(team.getVmtdn())){
			bundlingTeamVmtdn(team);
			team = getTeamByID(team.getId());
			team.setVmtdn(team.getVmtdn());
		}
	}

	private void setTeamCache(Integer tid, Team t) {
		setTeamToCache(tid, t);
		setTidToCache(t.getName(), t.getId());
	}

	private void setTeamToCache(int tid, Team t) {
		memcachedService.set(TEAM_ID + "." + tid, t);
	}

	private void setTidToCache(String name, int tid) {
		memcachedService.set(TEAM_NAME + "." + name, tid);
	}

	private void updateTeam(Team team) {
		if (Team.COMMON_TEAM.equals(team.getType())) {
			vmtTeamManager.updateTeam(team);
		}
	}

	private boolean validateTeam(Team t){
		if(t==null||Team.PESONAL_TEAM.equals(t.getType())){
			return false;
		}
		return true;
	}

	@Override
	public boolean addAdminToVmt(int tid, String uid, boolean isExistUser) {
		Team team = getTeamByID(tid);
		if(!validateTeam(team)){
			return false;
		}
		initTeamVmtdn(team);
		return vmtTeamManager.addAdminToTeam(team.getVmtdn(), uid);
	}

	@Override
	public void addTeamMembers(int tid, String[] users, String[] usernames,
			String auth) {
		addTeamMembers(tid, users, usernames, new String[]{auth});
	}
	
	@Override
	public void addUserToDefaultMember(String[] users,String[] userNames){
		Team team = getDefaultTeam();
		if(team!=null){
			for(int i=0;i<users.length;i++){
				if(!teamMemberService.isUserInTeam(team.getId(), users[i])){
					addTeamMembers(team.getId(), new String[]{users[i]},new String[]{ userNames[i]}, team.getDefaultMemberAuth());
				}
			}
		}
		
	}
	
	private Team getDefaultTeam(){
		return getTeamByID(1);
	}
	public void addTeamMembers(int tid, String[] users, String[] usernames,
			String[] auth) {
		addTeamMembers(tid, users, usernames, auth, true);
	}
	
	@Override
	public void addTeamMembers(int tid, String[] users, String[] usernames, String[] auth, boolean noticeVmt) {
		// 预处理users是为了解决新注册用户多次加入同一团队的bug
		String[] newUsers = null; // 需要新添加的团队成员
		String[] newUsersAuth = null;// 新添加团队成员的权限
		String[] oldUsers = null; // 新添加成员中已经是团队成员的成员
		String[] newAuths = null; // 已存在的团队成员的新权限
		VmtUserUpdateMessageHandle handle = new VmtUserUpdateMessageHandle();
		handle.setAuthorityService(authorityService);
		handle.setVmtUserManager(this);
		
		if (null != users && users.length > 0) {
			List<String> userList = new ArrayList<String>();
			List<String> authList = new ArrayList<String>();
			List<String> oldUserList = new ArrayList<String>();
			List<String> newAuthList = new ArrayList<String>();
			for (int i = 0; i < users.length; i++) {
				if(noticeVmt){
					handle.addUpdateUser(tid, users[i], auth[i]);
				}
				
				if (!teamMemberService.isUserInTeam(tid, users[i])) {
					userList.add(users[i]);
					authList.add(auth[i]);
				} else {
					oldUserList.add(users[i]);
					newAuthList.add(auth[i]);
				}
			}
			newUsers = new String[userList.size()];
			newUsersAuth = new String[authList.size()];
			oldUsers = new String[oldUserList.size()];
			newAuths = new String[newAuthList.size()];
			userList.toArray(newUsers);
			authList.toArray(newUsersAuth);
			oldUserList.toArray(oldUsers);
			newAuthList.toArray(newAuths);
		}
		aoneUserService.addToBatchUsers(newUsers, usernames);
		teamMemberService.addTeamMembers(newUsers, tid);
		authorityService.addBatchTeamAcl(newUsers, tid, newUsersAuth);
		authorityService.updateMembersAuthority(tid, oldUsers, newAuths);// 更新已有成员的权限
		if(noticeVmt){
			handle.dealVmtUserMessage(tid);
		}
	}
	@Override
	public boolean addUserToVmt(int tid, String uid) {
		Team team = getTeamByID(tid);
		if(!validateTeam(team)){
			return false;
		}
		initTeamVmtdn(team);
		return vmtTeamManager.addUserToTeam(team.getVmtdn(), uid);
	}

	private boolean bundlingTeamVmtdn(Team team) {
		if (StringUtils.isNotEmpty(team.getVmtdn())
				|| Team.PESONAL_TEAM.equals(team.getType())) {
			return true;
		} else {
			String result = vmtTeamManager.getTeamVmtdn(team.getName());
			if (result != null) {
				updateTeamVmtDn(team, result);
			} else {
				LOG.error("team '" + team.getDisplayName()
						+ "' query vmtdn result is null;team info:" + team);
			}
		}
		return false;
	}
	
	private void updateTeamVmtDn(Team team,String dn){
		teamDao.updateTeamVmtDn(team.getId(), dn);
		initTeamCache(team.getId());
		team.setVmtdn(dn);
	}
	
	public int createAndStartTeam(String currentUser, Map<String, String> params) {
		return createAndStartTeam(currentUser, params, new Date(), false);
	}

	public int createAndStartTeam(String currentUser,
			Map<String, String> params, Date date, boolean fromVmt) {
		int teamId = creatTeam(currentUser, params, date, fromVmt);
		TeamState state = TeamState.WORK;
		Team team = teamDao.getTeamById(teamId);
		team.setState(state);
		setTeamToCache(teamId, team);
		teamDao.updateTeamState(teamId, state);
		TeamInitUtil.initTeam(teamId, team.getCreator());
		return teamId;
	}

	public int creatTeam(String siteAdmin, Map<String, String> params,
			Date date, boolean fromVmt) {
		String teamCode = params.get(KeyConstants.SITE_NAME_KEY);
		synchronized (teamCode) {
			Team t = getTeamByName(teamCode);
			if (t != null) {
				throw new RuntimeException("team already exists!" + t);
			}
			Team team = new Team();
			team.setState(TeamState.UNINIT);
			team.setDisplayName(params.get(KeyConstants.SITE_DISPLAY_NAME));
			team.setName(params.get(KeyConstants.SITE_NAME_KEY));
			team.setCreator(params.get(KeyConstants.SITE_CREATOR));
			team.setDescription(params.get(KeyConstants.SITE_DESCRIPTION));
			team.setType(params.get(KeyConstants.TEAM_TYPE));
			team.setAccessType(params.get(KeyConstants.TEAM_ACCESS_TYPE));
			team.setDefaultMemberAuth(params
					.get(KeyConstants.TEAM_DEFAULT_MEMBER_AUTH));
			team.setCreateTime(new Date());
			team.setTeamDefaultView(params.get(KeyConstants.TEAM_DEFAULT_VIEW));
			int tid = teamDao.createTeam(team);
			team.setId(tid);
			setTeamCache(tid, team);
			LOG.info("Create team " + team);
			if (!fromVmt && !Team.PESONAL_TEAM.equals(team.getType())) {
				String dn = vmtTeamManager.addTeam(team);
				if(dn!=null){
					updateTeamVmtDn(team, dn);
				}
			}
			return tid;
		}
	}

	@Override
	public List<Team> getAllPublicAndProtectedTeam(int offset, int size) {
		return teamDao.getAllPublicAndProtectedTeam(offset, size);
	}

	public List<Team> getAllTeams() {
		return teamDao.getAllTeams();
	}

	@Override
	public List<Team> getAllUserPublicAndProtectedTeam(String uid) {
		return teamDao.getAllUserPublicAndProtectedTeam(uid);
	}

	public List<Team> getAllUserTeams(String user) {
		return teamDao.getAllUserTeams(user);
	}

	public int getPersonalTeam(String uid, String userName) {
		Team team = getTeamByName(getTeamNameFromEmail(uid));
		return (team != null) ? team.getId() : createUserDefaultTeam(uid,
				userName);
	}

	public int getPersonalTeamNoCreate(String uid) {
		Team team = getTeamByName(getTeamNameFromEmail(uid));
		return (team != null) ? team.getId() : -1;
	}

	// Use Cache
	public Team getTeamByID(int tid) {
		Team t = getTeamFromCache(tid);
		if (t == null) {
			t = addTeamToCache(tid);
		}
		return t;
	}

	// Use Cache
	public Team getTeamByName(String name) {
		Integer tid = null;
		if (name != null) {
			tid = getTidFromCache(name);
		}
		if (tid == null) {
			Team t = teamDao.getTeamByName(name);
			if (t == null) {
				return null;
			}
			tid = t.getId();
			setTeamCache(tid, t);
		}
		return this.getTeamByID(tid);
	}

	public String getTeamNameFromEmail(String email) {
		String temp = email.replace("@", "-"); // 将Email地址转变为团队名称
		temp = temp.replace(".", "-");
		return temp;
	}

	public List<TeamPreferences> getTeamPrefWithoutPersonSpace(String uid) {
		String psname = this.getTeamNameFromEmail(uid);
		Team team = this.getTeamByName(psname);
		if (team != null) {
			return teamPreferenceService.getTeamPrefWithoutTeam(uid, team.getId());
		} else {
			return teamPreferenceService.getTeamPrefWithoutTeam(uid, 0);
		}
	}

	@Override
	public int getTotalTeamNumber() {
		return teamDao.getTotalTeamNumber();
	}

	public List<Team> getUserTeamOrderByUser(String uid) {
		List<TeamPreferences> prefList = this
				.getTeamPrefWithoutPersonSpace(uid);
		String psname = getTeamNameFromEmail(uid);
		Team personTeam = getTeamByName(psname);
		List<Team> teams = new ArrayList<Team>();
		if (personTeam != null) {
			teams.add(personTeam);
		}
		if (prefList != null) {
			for (TeamPreferences p : prefList) {
				teams.add(getTeamByID(p.getTid()));
			}
		}
		return teams;
	}


	public boolean isValidateNewTeamName(String teamName) {
		//是否是系统关键字
		if(!SiteUtil.isValidateTeamCode(teamName)){
			return false;
		}
		return isNotExistedTeamCode(teamName);
	}
	
	@Override
	public boolean isNotExistedTeamCode(String teamCode){
		if (getTeamByName(teamCode) != null) {
			return false;
		}
		if(vmtTeamManager.teamCodeExistInVmt(teamCode)){
			return false;
		}
		return true;
	}

	@Override
	public boolean removeAdminToVmt(int tid, String uid, boolean isRomoveUser) {
		Team team = getTeamByID(tid);
		if(!validateTeam(team)){
			return false;
		}
		initTeamVmtdn(team);
		return vmtTeamManager.removeAdminToTeam(team.getVmtdn(), uid);
	}
	@Override
	public boolean removeMembers(int tid, String[] uids, boolean noticeVMT) {
		try {
			synchronized (Integer.valueOf(tid)) {
				List<String> noticeList = new ArrayList<String>();
				if (noticeVMT) {
					for (String uid : uids) {
						if (teamMemberService.isUserInTeam(tid, uid)) {
							noticeList.add(uid);
						}
					}
				}
				authorityService.removeMemberAcls(tid, uids);
				teamMemberService.removeMembers(tid, uids);
				for(String uid : noticeList){
					removeUserToVmt(tid, uid);
				}
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean removeUserToVmt(int tid, String uid) {
		Team team = getTeamByID(tid);
		if(!validateTeam(team)){
			return false;
		}
		initTeamVmtdn(team);
		return vmtTeamManager.removeUserToTeam(team.getVmtdn(), uid);
	}

	public void setAoneUserService(AoneUserService aoneUserService) {
		this.aoneUserService = aoneUserService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}
	public void setMemcachedService(ICacheService memcacheService) {
		this.memcachedService = memcacheService;
	}
	
	public void setTeamDao(TeamDAO teamDao) {
		this.teamDao = teamDao;
	}
	public void setTeamMemberService(TeamMemberService teamMemberService) {
		this.teamMemberService = teamMemberService;
	}

	public void setVmtTeamManager(VMTTeamManager vmtTeamManager) {
		this.vmtTeamManager = vmtTeamManager;
	}
	
	public void updateBasicInfo(String teamName, String title,
			String description, String accessType, String defaultMemberAuth,String defaultView) {
		Team t = this.getTeamByName(teamName);
		if (t != null) {
			t.setDisplayName(title);
			t.setDescription(description);
			t.setAccessType(accessType);
			t.setDefaultMemberAuth(defaultMemberAuth);
			t.setTeamDefaultView(defaultView);
			bundlingTeamVmtdn(t);
			updateTeam(t);
			updateTeamCache(t);
			teamDao.updateBasicInfo(teamName, title, description, accessType,
					defaultMemberAuth, t.getCreateTime(),defaultView);
		}
	}
	@Override
	public void updateMembersAuthority(int tid, String[] uids, String[] auths,
			boolean isVmt) {
		authorityService.updateMembersAuthority(tid, uids, auths);
		if (!isVmt) {
			for (int i = 0; i < uids.length; i++) {
				String uid = uids[i];
				String auth = auths[i];
				if (Team.AUTH_ADMIN.equals(auth)) {
					addAdminToVmt(tid, uid, false);
				} else {
					removeAdminToVmt(tid, uid, false);
				}
			}
		}
	}

	public void updateTeamTitle(String teamName, String title, Date date) {
		Team t = this.getTeamByName(teamName);
		if (t != null) {
			t.setDisplayName(title);
			bundlingTeamVmtdn(t);
			if (title != null && !title.equals(t.getDisplayName())) {
				updateTeam(t);
			}
			setTeamToCache(t.getId(), t);
			if (date == null) {
				date = t.getCreateTime();
			}
			teamDao.updateBasicInfo(teamName, title, t.getDescription(),
					t.getAccessType(), t.getDefaultMemberAuth(), date,t.getTeamDefaultView());
		}
	}

	@Override
	public void addTeamCreateInfo(TeamCreateInfo info) {
		teamCreateInfoDao.create(info);
	}

	@Override
	public List<Team> getTeamByCreator(String uid) {
		return teamDao.getTeamByCreator(uid);
	}

	@Override
	public List<Team> queryByTeamCode(String queryWord) {
		return teamDao.queryByTeamCode(queryWord);
	}

	@Override
	public List<Team> queryByTeamName(String queryWord) {
		return teamDao.queryByTeamName(queryWord);
	}

	@Override
	public void updateTeamState(int tid,TeamState hangup) {
		teamDao.updateTeamState(tid, hangup);
	}
	
	@Override
	public void updateHangup(int tid) {
		Team team = getTeamByID(tid);
		teamDao.updateTeamState(tid, TeamState.HANGUP);
		removeTeamCache(team);
		//团队名称追加删除标记
		teamDao.updateTeamName(tid, createHangupName(team.getName()));
		
		vmtTeamManager.deleteTeam(team.getVmtdn());
	}
	
	@Override
	public List<Team> getTeamByType(String type) {
		return teamDao.getTeamByType(type);
	}

	@Override
	public PaginationBean<Team> queryByTeamName(String queryWord, int offset, int size) {
		return teamDao.queryByTeamName(queryWord,offset,size);
	}

	/**
	 * 格式为：原团队名称 + #hanup + 时间戳
	 * 如团队名称为myteam，返回结果是myteam#hanup20151010150021055
	 * @param name
	 * @return
	 */
	private String createHangupName(String name){
		String timestamp = DateUtil.getCurrentTime("yyyyMMddHHmmssSSS");
		return name + "#hangup" + timestamp;
	}
	
}
