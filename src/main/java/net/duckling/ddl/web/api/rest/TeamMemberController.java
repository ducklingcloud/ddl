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
package net.duckling.ddl.web.api.rest;


import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.ddl.web.vo.ErrorMsg;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 团队成员
 * @author Brett
 *
 */
@Controller
@RequestMapping("/api/v1/team")
@RequirePermission(target="team",operation="admin")
public class TeamMemberController extends AbstractController {
	private static final Logger LOG = Logger.getLogger(TeamMemberController.class);
	
	@RequestMapping(value="/memberBatchAdd", method = RequestMethod.POST)
	public void batchAdd(@RequestParam("uids") String [] uids, @RequestParam("names") String [] names,	
			 @RequestParam("auth") String auth,
			 HttpServletRequest request, HttpServletResponse response) throws IOException {
		int tid = getCurrentTid();
		
		//验证auth
		if(Team.AUTH_ADMIN.equals(auth)||Team.AUTH_EDIT.equals(auth)||Team.AUTH_VIEW.equals(auth)){
			//nothing
		}else{
			LOG.info("team auth wrong. {\"tid\":" + tid + ", \"auth\":" + auth +"}");
			writeError(ErrorMsg.TEAM_AUTH_WRONG, response);
			return;
		}
		
		//初始化权限数组，和uids等长
		String [] auths = new String[uids.length];
		for(int i=0; i< uids.length; i++){
			auths[i] = auth;
		}
		
		teamService.addTeamMembers(tid, uids, names, auths);
		
		LOG.info("Team members added successfully. {\"tid\":" + tid + ",\"uids\":" + Arrays.toString(uids) +
					",\"names\":" + Arrays.toString(names) + ", \"auth\":" + auth +"}");
		
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	
	@RequestMapping(value="/memberBatchDelete", method = RequestMethod.POST)
	public void batchDelete(@RequestParam("uids") String [] uids, 
			 HttpServletRequest request, HttpServletResponse response) throws IOException {
		int tid = getCurrentTid();
		teamService.removeMembers(tid, uids, true);
		LOG.info("Team members removed successfully. {\"tid\":" + tid + ",\"uids\":" + Arrays.toString(uids) +"}");
		
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	@Autowired
	private TeamService teamService;
}
