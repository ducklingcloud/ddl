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
package net.duckling.ddl.web.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.exception.MessageException;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.service.version.IVersionService;
import net.duckling.ddl.service.version.Version;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.bean.Result;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;

@Controller
@RequestMapping("/system/syncclient")
public class SyncClientController {
	private static Logger LOG=Logger.getLogger(SyncClientController.class);
	
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws MessageException {
		AccessToken token = (AccessToken) request.getAttribute("accessToken");
		if (token == null) {
			JsonUtil.writeJSONObject(response,new Result<String>(Result.CODE_ERROR,Result.MESSAGE_PARAM_ERROR + " [accessToken]"));
        	return;
		}
		JsonUtil.writeJSONObject(response, loginResult(token));
	}

	@RequestMapping(params = "func=refreshToken")
	public void refreshToken(@RequestParam("refreshtoken") String refreshToken,
			HttpServletResponse response) throws IOException {
		try {
			LOG.info("refreshToken:"+refreshToken);
			AccessToken token = authorizationCodeService.umtRefreshToken(refreshToken);
			JsonUtil.writeJSONObject(response, loginResult(token));
		} catch (OAuthProblemException e) {
			JsonUtil.writeJSONObject(response,new Result<String>(Result.CODE_UNAUTHORIZED,Result.MESSAGE_UNAUTHORIZED));
			LOG.warn("RefreshToken to AccessToken failed.");
		}
	}
	
	/**
	 * 更新客户端
	 * @param request
	 * @param response
	 * @throws MessageException
	 */
	@RequestMapping(params = "func=update")
	public void update(@RequestParam String os, HttpServletRequest req, HttpServletResponse resp)
			throws MessageException {
		Version version = versionService.get(IVersionService.PROJECT_DDL_DRIVE, os);
		if(version==null || !version.isSuccess()){
			JsonUtil.writeJSONObject(resp, new Result<String>(Result.CODE_FILE_NOT_FOUND, Result.MESSAGE_FILE_NOT_FOUND));
			return;
		}
		JsonUtil.writeJSONObject(resp, new Result<Version>(version));
	}
	
	private Result<Map<String, Object>> loginResult(AccessToken token) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("email", token.getUserInfo().getCstnetId());
		map.put("accessToken", token.getAccessToken());
		map.put("refreshToken", token.getRefreshToken());
		map.put("displayName", token.getUserInfo().getTrueName()==null ? "" : token.getUserInfo().getTrueName());
		map.put("expiresIn", token.getExpiresIn());
		
		
		//用户所有的团队
		List<Team> teamList = getTeamsOrderSequence(token.getUserInfo().getCstnetId());
		map.put("teams", teamList);
		return new Result<Map<String,Object>>(map);
	}
	
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
	
	@Autowired
	private AuthorizationCodeService authorizationCodeService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private TeamSpaceSizeService teamSpaceSizeService;
	@Autowired
	private TeamPreferenceService teamPreferenceService;
	@Autowired
	private IVersionService versionService;
}
