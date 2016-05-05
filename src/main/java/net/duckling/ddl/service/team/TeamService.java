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

package net.duckling.ddl.service.team;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.util.PaginationBean;


/**
 * @date 2012-2-17
 * @author clive
 */
public interface TeamService {
	/**
	 * 创建一个团队，指定创建时间
	 * 
	 * @param siteAdmin
	 * @param params
	 * @param date
	 *            团队创建时间
	 * @param fromVmt
	 *            是否是来自VMT的创建消息
	 * @return
	 */
	int creatTeam(String siteAdmin, Map<String, String> params, Date date,
			boolean fromVmt);

	int createAndStartTeam(String currentUser, Map<String, String> params,
			Date date, boolean fromVmt);

	int createAndStartTeam(String currentUser, Map<String, String> params);

	List<Team> getAllTeams();

	List<Team> getAllUserTeams(String user);
	void addTeamCreateInfo(TeamCreateInfo info);
	/**
	 * 获取uid的默认团队如果不存在就自动创建默认团队
	 * 
	 * @param uid
	 * @param userName
	 * @return
	 */
	int getPersonalTeam(String uid, String userName);

	/**
	 * 获取uid的默认团队如果不存在就返回-1
	 * 
	 * @param uid
	 * @param userName
	 * @return
	 */
	int getPersonalTeamNoCreate(String uid);

	Team getTeamByID(int tid);

	Team getTeamByName(String name);

	boolean isValidateNewTeamName(String teamName);
	boolean isNotExistedTeamCode(String teamCode);

	void updateBasicInfo(String teamName, String title, String description,
			String accessType, String defaultMemberAuth,String defaultView);

	String getTeamNameFromEmail(String email);

	List<Team> getAllPublicAndProtectedTeam(int offset, int size);

	List<Team> getAllUserPublicAndProtectedTeam(String uid);

	/**
	 * 获取当前系统内团队数量
	 * 
	 * @return 团队数量
	 */
	int getTotalTeamNumber();

	/**
	 * 获取用户的全部team，并按照用户设置的banner进行排序
	 * 
	 * @param uid
	 * @return
	 */
	List<Team> getUserTeamOrderByUser(String uid);

	/**
	 * 查询用户所在的团队的信息（不包括用户的个人团队）
	 * 
	 * @param uid
	 *            用户ID
	 * @return 所有匹配的记录
	 */
	List<TeamPreferences> getTeamPrefWithoutPersonSpace(String uid);
	/**
	 * 添加团队成员，并赋予不同的权限：若待添加的成员中存在已经是团队的成员的情况，则更新这些成员的权限
	 * @param tid 团队ID
	 * @param users 待添加团队成员的UID集合
	 * @param usernames 待添加团队成员的显示名称集合
	 * @param auth 权限
	 */
	void addTeamMembers(int tid, String[] users, String[] usernames, String[] auth);
	void addTeamMembers(int tid, String[] users, String[] usernames, String auth);
	void addTeamMembers(int tid, String[] users, String[] usernames, String[] auth,boolean noticeVmt);

	/**
	 * 批量删除团队成员
	 * @param tid 团队ID
	 * @param uids 用户ID
	 * @param flag 是否通知VMT
	 * @return
	 */
	boolean removeMembers(int tid, String[] uids,boolean noticeVMT);

	public void updateMembersAuthority(int tid, String[] uids, String[] auths,boolean isVmt);
	void updateTeamTitle(String teamName, String title, Date date);

	List<Team> getTeamByCreator(String uid);
	/**
	 * 将用户加入默认团队 如果用户存在那就只改变权限
	 * @param users
	 * @param userNames
	 */
	void addUserToDefaultMember(String[] users, String[] userNames);

	List<Team> queryByTeamCode(String queryWord);

	List<Team> queryByTeamName(String queryWord);
	PaginationBean<Team> queryByTeamName(String queryWord,int offset,int size );

	void updateTeamState(int tid,TeamState hangup);
	
	/**
	 * 更新团队为删除状态。团队名称追加了删除标记，以便其他人注册使用。
	 * @param tid
	 */
	void updateHangup(int tid);
	
	List<Team> getTeamByType(String type);
}