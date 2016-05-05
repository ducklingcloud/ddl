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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.ClientValidator;
import net.duckling.ddl.util.HTMLConvertUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.SiteUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.ddl.web.vo.ErrorMsg;
import net.duckling.ddl.web.vo.VoUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 团队控制器
 * @author Brett
 *
 */
@Controller
@RequestMapping("/api/v1/teams")
@RequirePermission(authenticated = true)
public class TeamController extends AbstractController {
	private static final Logger LOG = Logger.getLogger(TeamController.class);
	
	@RequestMapping("/{teamCode}")
	@ResponseBody
	public Team getByName(@PathVariable String teamCode, HttpServletResponse response) {
		Team team = teamService.getTeamByName(teamCode);
		if(team == null){
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			JsonUtil.write(response, ErrorMsg.URI_NOT_FOUND);
		}
	    return team;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void create(@RequestParam("teamCode") String teamCode, @RequestParam("displayName") String displayName,	
			 @RequestParam("description") String description,
			 @RequestParam(value="accessType", required=false) String accessType,
			 @RequestParam(value="autoTeamCode", required=false) Boolean autoTeamCode,
			 @RequestParam(value="type", required=false) String type,
			 HttpServletRequest request, HttpServletResponse response) {
		String uid = getCurrentUid(request);
		accessType = StringUtils.defaultIfBlank(accessType, Team.ACCESS_PRIVATE);
		autoTeamCode = autoTeamCode==null ? false : autoTeamCode;
		teamCode = StringUtils.defaultString(teamCode).trim();
		
		LOG.info("create team start... {uid:" + uid +",teamCode:" + teamCode +",displayName:"+ displayName  +",accessType:" + accessType +",autoTeamCode:" + autoTeamCode +",type:" + type +"}");
		
		//团队只允许信任的客户端创建
		if(!ClientValidator.validate(request)){
			LOG.warn("client is not allowed. {teamCode:"+teamCode+",type:"+type+",host:"+ClientValidator.getRealIp(request)+",pattern:" 
					+ ClientValidator.getClientIpPattern(request) + "}");
			
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			JsonUtil.write(response, ErrorMsg.NEED_PERMISSION);
			return;
		}
		
		if(Team.CONFERENCE_TEAM.equals(type)){
			//nothing
		}else{
			//默认普通团队
			type = Team.COMMON_TEAM;
		}
		
		if(autoTeamCode){
			teamCode = tidyTeamCode(teamCode);
			if("".equals(teamCode)){
				teamCode = getRandomString(5);
			}
			teamCode = teamCodeIncrement(teamCode, 0);
		}
		if(!checkParams(teamCode, displayName, description, accessType,  response)){
			return;
		}
		
		Map<String, String> params = getParamsForCreate(uid, teamCode, displayName, description, accessType, type);
		int teamId = teamService.createAndStartTeam(uid, params);
		teamService.addTeamMembers(teamId,
				new String[] { uid },
				new String[] { super.getCurrentUsername(request)},
				Team.AUTH_ADMIN);
		LOG.info("create team success. {uid:" + uid + ", tid:" + teamId + ", parmas:" + params + "}");
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		JsonUtil.write(response, VoUtil.getTeamVo(teamService.getTeamByID(teamId)));
	}
	
	private boolean checkParams(String teamCode, String displayName, String description, String accessType, 
			HttpServletResponse response){
		if(!SiteUtil.isValidateTeamCode(teamCode)){
			writeError(ErrorMsg.TEAM_CODE_WRONG, response);
			return false;
		}
		if(!teamService.isNotExistedTeamCode(teamCode)){
			writeError(ErrorMsg.TEAM_CODE_ALREADY_EXIST, response);
			return false;
		}
		if(StringUtils.isBlank(displayName)){
			writeError(ErrorMsg.DISPLAY_NAME_WRONG, response);
			return false;
		}
		if(Team.ACCESS_PRIVATE.equals(accessType)||Team.ACCESS_PROTECTED.equals(accessType)||Team.ACCESS_PUBLIC.equals(accessType)){
			//nothing
		}else{
			writeError(ErrorMsg.ACCESS_TYPE_WRONG, response);
			return false;
		}
		return true;
	}
	
	private Map<String, String> getParamsForCreate(String uid, String name, String displayName, 
			String description, String accessType, String type) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(KeyConstants.SITE_CREATOR, uid);
		params.put(KeyConstants.SITE_NAME_KEY, name);
		params.put(KeyConstants.SITE_DISPLAY_NAME, HTMLConvertUtil.replaceLtGt(displayName));
		params.put(KeyConstants.SITE_DESCRIPTION, HTMLConvertUtil.replaceLtGt(description));
		params.put(KeyConstants.TEAM_TYPE, type);
		params.put(KeyConstants.TEAM_ACCESS_TYPE, accessType);
		params.put(KeyConstants.TEAM_DEFAULT_MEMBER_AUTH, Team.AUTH_ADMIN);
		params.put(KeyConstants.TEAM_DEFAULT_VIEW, Team.DEFAULT_TEAM_VIEW_LIST);
		return params;
	}
	
	/**
	 * 如果teamCode存在则返回一个自增可用的teamCode
	 * @param teamCode
	 * @param i
	 * @return
	 */
	private String teamCodeIncrement(String teamCode, int i){
		String newCode = teamCode;
		if(i>0){
			newCode = teamCode+i;
		}
		if(!teamService.isNotExistedTeamCode(newCode)){
			i++;
			return teamCodeIncrement(teamCode, i);
		}
		return newCode;
	}
	
	/**
	 * 整理成符合规范的teamCode
	 * @param code
	 * @return
	 */
	private String tidyTeamCode(String code){
		code = code.toLowerCase().trim();
		StringBuilder result = new StringBuilder();
		for(int i=0;i<code.length();i++){
			//是否是数字或小写字母
			if((code.charAt(i)>=97&&code.charAt(i)<=122) || 
				(code.charAt(i)>=48&&code.charAt(i)<=57) ){
				result.append(code.charAt(i));
			}
		}
		return result.toString();
	}
	
	private static String getRandomString(Integer length) {  
	    String str = "";  
	    Random random = new Random();  
	    for (int i = 0; i < length; i++) {  
	        boolean b = random.nextBoolean();  
	        if (b) {
	            str += (char) (97 + random.nextInt(26));// 取得小写字母  
	        } else {
	            str += String.valueOf(random.nextInt(10));
	        }
	    }
	    return str;  
	}
	
	@Autowired
	private TeamService teamService;
}
