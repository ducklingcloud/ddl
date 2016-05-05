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

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.team.TeamSpaceApplication;
import net.duckling.ddl.service.team.TeamSpaceApplicationService;
import net.duckling.ddl.service.team.TeamSpaceConfig;
import net.duckling.ddl.service.team.TeamSpaceConfigService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;

@Service
public class TeamSpaceApplicationServiceImpl implements TeamSpaceApplicationService {

	private static final Logger LOGGER = Logger.getLogger(TeamSpaceApplicationService.class);
	
	@Override
	public int add(long newSize, long originalSize, String uid, int tid, String applicationType) {
		TeamSpaceConfig config = teamSpaceConfigService.getTeamSpaceConfig(tid);
		config.setSize(newSize);
		config.setTid(tid);
		config.setUpdateUid(uid);
		config.setUpdateTime(new Date());
		config.setDescription(applicationType);
		if (config.getId() <= 0) {
			teamSpaceConfigService.insert(config);
		} else {
			teamSpaceConfigService.update(config);
		}
		TeamSpaceApplication bean = new TeamSpaceApplication();
		bean.setApplicationTime(new Date());
		bean.setApplicationType(applicationType);
		bean.setApproveTime(new Date());
		bean.setNewSize(newSize);
		bean.setOriginalSize(originalSize);
		bean.setTid(tid);
		bean.setUid(uid);
		return teamSpaceApplicationDAO.create(bean);
	}
	
	@Override
	public long updateSpaceAllocate(String uid, long size, int tid) {
		addTeamSpaceConfig(uid, size, tid);
		
		UserExt older = aoneUserService.getUserExtInfo(uid);
		older.setUnallocatedSpace(older.getUnallocatedSpace() - size);
		aoneUserService.modifyUserProfile(older);
		
		LOGGER.info("allocated space. uid:" + uid + ", tid:" + tid + ", size:" + size);
		return older.getUnallocatedSpace();
	}
	
	@Override
	public int updateSpaceAllocateAll(){
		List<UserExt> allUserUnallocated = aoneUserService.searchByUnallocatedSpace();
		for(UserExt item : allUserUnallocated){
			int tid= teamService.getPersonalTeamNoCreate(item.getUid());
			long size = item.getUnallocatedSpace();
			addTeamSpaceConfig(item.getUid(), size, tid);
			item.setUnallocatedSpace(0);
			aoneUserService.modifyUserProfile(item);
			
			LOGGER.info("allocated space. uid:" + item.getUid() + ", tid:" + tid + ", size:" + size);
		}
		return allUserUnallocated.size();
	}
	
	@Override
	public void updateSpaceAllocateAllUser(){
		List<Team> pesonalTeamList = teamService.getTeamByType(Team.PESONAL_TEAM);
		for(Team item : pesonalTeamList){
			UserExt user = aoneUserService.getUserExtInfo(item.getCreator());
			long size = user.getUnallocatedSpace();
			updateSpaceAllocate(item.getCreator(), size, item.getId());
		}
	}

	@Override
	public List<TeamSpaceApplication> queryByTid(int tid) {
		return teamSpaceApplicationDAO.queryByTid(tid);
	}

	@Override
	public void delete(int id) {
		teamSpaceApplicationDAO.delete(id);
	}
	
	private void addTeamSpaceConfig(String uid, long size, int tid){
		TeamSpaceConfig config = teamSpaceConfigService.getTeamSpaceConfig(tid);
		this.add(size+config.getSize(), config.getSize(), uid, tid, TeamSpaceApplication.TYPE_ACTIVITY);
	}
	
	@Autowired
	private TeamSpaceConfigService teamSpaceConfigService;
	@Autowired
	private TeamSpaceApplicationDAO teamSpaceApplicationDAO;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private TeamService teamService;
}
