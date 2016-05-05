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
package net.duckling.ddl.web.controller.pan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.util.JsonUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;

@Controller
@RequestMapping("/system/clientcode")
public class ClientCodeController {
	@Autowired
	private AuthorizationCodeService authorizationCodeService;
	@Autowired
	private TeamPreferenceService teamPreferenceService;
	@Autowired
	private TeamService teamService;
	
	private static Logger LOG=Logger.getLogger(ClientCodeController.class);

	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		boolean needTeams = request.getParameter("need_teams") == null ? false : true; //是否需要返回团队信息
		AccessToken token = (AccessToken) request.getAttribute("accessToken");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		if (token == null) {
			JsonUtil.writeJSONObject(response, getLoginFailed());
			return;
		}
		
		JsonUtil.writeJSONObject(response, getLoginSuccess(token,needTeams));
	}

	private String getLoginSuccess(AccessToken token, boolean needTeams) {
		Map<String,Object> map = new HashMap<String,Object>();
		String uid = token.getUserInfo().getCstnetId();
		map.put("Email", uid);
		map.put("AccessToken", token.getAccessToken());
		map.put("RefreshToken", token.getRefreshToken());
		map.put("DisplayName", token.getUserInfo().getTrueName()==null ? "" : token.getUserInfo().getTrueName());
		map.put("ExpiresIn", token.getExpiresIn());
		if(needTeams){
			map.put("Teams", getTeamsOrderSequence(uid));	
		}
		return JsonUtil.getJSONString(map);
	}
	
	private String getLoginFailed() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		return JsonUtil.getJSONString(map);
	}

	@RequestMapping(params = "func=refreshToken")
	public void refreshToken(@RequestParam("refreshtoken") String refreshToken, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		boolean needTeams = request.getParameter("need_teams") == null ? false : true;
		
		try {
			LOG.info("refreshToken:"+refreshToken);
			AccessToken token = authorizationCodeService.umtRefreshToken(refreshToken);
			JsonUtil.writeJSONObject(response, getLoginSuccess(token,needTeams));
		} catch (OAuthProblemException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			LOG.error("RefreshToken to AccessToken failed",e);
		}
	}
	
	
	/**
	 * 返回团队信息列表
	 * @param uid
	 * @return
	 */
	private List<Team> getTeamsOrderSequence(String uid){
		List<Team> teamList = new ArrayList<Team>();
		//个人团队
		String psname = teamService.getTeamNameFromEmail(uid);
		Team team = teamService.getTeamByName(psname);
		int tid = 0;
		if(team!=null){
			teamList.add(team);
			tid = team.getId();
		}
		List<TeamPreferences> prefList = teamPreferenceService.getTeamPrefWithoutTeam(uid, tid);
		if(prefList!=null){
			for (TeamPreferences p : prefList) {
				teamList.add(teamService.getTeamByID(p.getTid()));
			}
		}
		return teamList;
	}
}
