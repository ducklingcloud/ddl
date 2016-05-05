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

package net.duckling.ddl.web.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 查询用户参加的团队
 * 
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/teams")
@RequirePermission(authenticated=true)
public class APITeamsController extends APIBaseController {
    @Autowired
    private TeamPreferenceService teamPreferenceService;
	@Autowired
	private TeamService teamService;
	
	/*
	 * 获得当前用户所在的所有团队的代号,名称和tid
	 * 要求:已通过认证的用户
	 * 参数:当前登陆用户的uid
	 * url:/api/teams
	 * 
	 */
	@RequestMapping
	public void service(HttpServletRequest request,
			HttpServletResponse response) {
		String uid = findUser(request);
		List<Team> teams = teamService.getUserTeamOrderByUser(uid);
		List<TeamPreferences> p = teamPreferenceService.getAllTeamPrefs(uid);
		Map<Integer,TeamPreferences> map = new HashMap<Integer,TeamPreferences>();
		for(TeamPreferences tp : p){
			map.put(tp.getTid(), tp);
		}
		JSONArray array = new JSONArray();
		for(Team obj:teams){
			JSONObject o = JsonUtil.getJSONObject(obj);
			TeamPreferences pp = map.get(obj.getId());
			if(pp!=null&&!obj.isPersonalTeam()){
				o.put("teamNoticeCount", pp.getTeamNoticeCount());
				o.put("personNoticeCount", pp.getPersonNoticeCount());
				o.put("monitorNoticeCount", pp.getMonitorNoticeCount());
			}
			array.add(o);
		}
		JsonUtil.writeJSONObject(response, array);
	}
	
	
	
	public String getTeamNameFromEmail(String email) {
		String temp = email.replace("@", "-"); // 将Email地址转变为团队名称
		temp = temp.replace(".", "-");
		return temp;
	}
	
	/*
	 * 获得所有团队代号和tid
	 * 要求:已通过认证的用户
	 * url:/api/teams?func=allTeams
	 * 返回结果:json数组
	 * [{id:"1",name:"cerc",displayName:"xxx"},{id:"2",name:"xxx",displayName:"xxx"}]
	 */
	@RequestMapping(params="func=allTeams")
	public void allTeams(HttpServletResponse response){
		List<Team> teams = teamService.getAllTeams();
		
		writeTeamJSON(response, teams);
	}

	private void writeTeamJSON(HttpServletResponse response, List<Team> teams) {
		JSONArray array = JsonUtil.getJSONArrayFromList(teams);
		JsonUtil.writeJSONObject(response, array);
	}

}
