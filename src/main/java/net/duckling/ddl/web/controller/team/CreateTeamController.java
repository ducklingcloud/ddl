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

package net.duckling.ddl.web.controller.team;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamCreateInfo;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.UserConfig;
import net.duckling.ddl.service.user.UserConfigService;
import net.duckling.ddl.util.HTMLConvertUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @date 2011-5-26
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/createTeam")
@RequirePermission(authenticated = true)
public class CreateTeamController extends BaseController {
	private static final Logger LOG = Logger
			.getLogger(CreateTeamController.class);
	@Autowired
	private TeamService teamService;
	@Autowired
	private DucklingProperties config;
	@Autowired
	private UserConfigService userConfigService;
	@Autowired
	private URLGenerator urlGenerator;
	
	private int createTeam(HttpServletRequest request) {
		VWBContext context = VWBContext.createContext(request,
				UrlPatterns.ADMIN);
		Map<String, String> params = new HashMap<String, String>();
		int teamId = -1;
		boolean flag = teamService.isValidateNewTeamName(request
				.getParameter("teamId"));
		if (flag && setParamsAndValidateForCreateSiste(request, params)) {
			teamId = teamService.createAndStartTeam(context.getCurrentUID(),
					params);
			LOG.info("用户:" + context.getCurrentUID() + ";创建团队tid=" + teamId
					+ ";参数有" + params);
			addTeamCreateInfo(teamId,request);
		}
		return teamId;
	}

	private void addTeamCreateInfo(int teamId, HttpServletRequest request) {
		String[] as = request.getParameterValues("teamInfo");
		if(as!=null){
			for(String a:as){
				TeamCreateInfo info = new TeamCreateInfo();
				info.setTid(teamId);
				info.setParamKey(a);
				info.setParamValue(a);
				teamService.addTeamCreateInfo(info);
			}
		}
	}

	private VWBContext getVWBContext(HttpServletRequest pRequest) {
		return VWBContext.createContext(pRequest, UrlPatterns.ADMIN);
	}

	private boolean isValidTeamName(String teamName, VWBContext context) {
		return teamService.isValidateNewTeamName(teamName);
	}

	private boolean setParamsAndValidateForCreateSiste(
			HttpServletRequest request, Map<String, String> params) {
		VWBContext context = getVWBContext(request);
		String teamId = request.getParameter("teamId");
		String teamDisplayName = request.getParameter("teamName");
		if (StringUtils.isEmpty(teamId) || !isValidTeamName(teamId, context)||teamId.contains("<")||teamId.contains(">")) {
			return false;
		}
		String domain = context.getContainer().getDefaultDomain();
		String uid = context.getCurrentUID();
		params.put(KeyConstants.SITE_CREATOR, uid);
		params.put(KeyConstants.SITE_DESCRIPTION,
				HTMLConvertUtil.replaceLtGt(request.getParameter("teamDescription")));
		params.put(KeyConstants.SITE_NAME_KEY, teamId);
		params.put(KeyConstants.SITE_DISPLAY_NAME, HTMLConvertUtil.replaceLtGt(teamDisplayName));
		params.put(KeyConstants.TEAM_CLB_USERNAME, context.getContainer()
				.getProperty(KeyConstants.CONTAINER_CLB_USER));
		params.put(KeyConstants.TEAM_CLB_PASSWORD, context.getContainer()
				.getProperty(KeyConstants.CONTAINER_CLB_PASSWORD));
		params.put(KeyConstants.TEAM_TYPE, "common");
		String accessType = request.getParameter("accessType");
		accessType = accessType == null ? Team.ACCESS_PRIVATE : accessType;
		params.put(KeyConstants.TEAM_ACCESS_TYPE, accessType);
		String defaultMemberAuth = request.getParameter("defaultMemberAuth");
		defaultMemberAuth = defaultMemberAuth == null ? Team.AUTH_VIEW
				: defaultMemberAuth;
		params.put(KeyConstants.TEAM_DEFAULT_MEMBER_AUTH, defaultMemberAuth);
		params.put(KeyConstants.SITE_DOMAIN_KEY, domain);
		params.put(KeyConstants.TEAM_DEFAULT_VIEW, getTeamDefaultAccess(request));
		return true;
	}
	private String getTeamDefaultAccess(HttpServletRequest request){
		String[] a = request.getParameterValues("teamInfo");
		if(a==null||a.length==0){
			return Team.DEFAULT_TEAM_VIEW_LIST;
		}else if(a.length==1){
			if("teamcommunication".equals(a[0])){
				return Team.DEFAULT_TEAM_VIEW_NOTIC;
			}else{
				return Team.DEFAULT_TEAM_VIEW_LIST;
			}
		}else{
			return Team.DEFAULT_TEAM_VIEW_LIST;
		}
	}
	private ModelAndView validateCreateTeam(TeamForm teamForm,
			BindingResult br, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = null;
//		String checkCode = request.getParameter("checkCode");
//		boolean re = PictureCheckCodeUtil.checkCode(request, checkCode,
//				"teamType", true);
//		if (!re) {
//			result = init(request,teamForm);
//			result.addObject("checkError", "验证码错误！");
//		}
		if(StringUtil.illCharCheck(request, null, "teamDescription")){
			result = init(request,teamForm);
			result.addObject("teamDescriptionError", "请不要输入非法字符:?\\ /*<>|\":");
		}
		if (result == null && br.hasErrors()) {
			VWBContext context = this.getVWBContext(request);
			result = layout(".aone.portal", context,
					"/jsp/aone/team/createTeam.jsp");
			;
			result.getModel().putAll(br.getModel());
		}
		return result;
	}

	@RequestMapping(params = "func=error")
	public ModelAndView error(HttpServletRequest request) {
		VWBContext context = this.getVWBContext(request);
		ModelAndView mv = layout(".aone.portal", context,
				"/jsp/aone/errors/createTeamError.jsp");
		mv.addObject(LynxConstants.PAGE_TITLE, "消息");
		return mv;
	}

	@RequestMapping
	public ModelAndView init(HttpServletRequest request,TeamForm teamForm) {
		VWBContext context = this.getVWBContext(request);
		String uid = VWBSession.getCurrentUid(request);
		if(!validateUserCreateTeam(uid)){
			return layout(".aone.portal", context,
					"/jsp/aone/team/noEnoughSizeCreateTeam.jsp");
		}
		ModelAndView result = layout(".aone.portal", context,
				"/jsp/aone/team/createTeam.jsp");
		result.addObject("teamForm", teamForm);
		String baseUrl = urlGenerator.getBaseUrl();
		if(!baseUrl.endsWith("/")){
			baseUrl = baseUrl+"/";
		}
		result.addObject("baseUrl", baseUrl);
		result.addObject(LynxConstants.PAGE_TITLE, "创建团队");
		return result;
	}
	
	private boolean validateUserCreateTeam(String uid){
		List<Team> teams = teamService.getTeamByCreator(uid);
		
		int teamAmount = 0;
		if(teams!=null){
			for(Team t : teams){
				//会议类型团队不作数量限制.
				if(Team.CONFERENCE_TEAM.equals(t.getType())){
					continue;
				}
				teamAmount++;
			}
		}
		int maxSize = 10;
		try{
			UserConfig u = userConfigService.getByUid(uid);
			if(u!=null){
				maxSize = u.getMaxCreateTeam();
			}else{
				maxSize = Integer.parseInt(config.getProperty("duckling.user.default.team.size"));
			}
		}catch(Exception e){}
		return maxSize>=teamAmount;
	}

	@RequestMapping(params = "func=createTeam")
	public ModelAndView submitCreateTeam(
			@Valid @ModelAttribute("teamForm") TeamForm teamForm,
			BindingResult br, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView re = validateCreateTeam(teamForm, br, request, response);
		if (re != null) {
			return re;
		}
		VWBContext context = this.getVWBContext(request);
		String uid = VWBSession.getCurrentUid(request);
		if(!validateUserCreateTeam(uid)){
			return layout(".aone.portal", context,
					"/jsp/aone/team/noEnoughSizeCreateTeam.jsp");
		}
		int newTid = createTeam(request);
		if (newTid != -1) {
			VWBContext.setCurrentTid(newTid);
			teamService.addTeamMembers(newTid,
					new String[] { context.getCurrentUID() },
					new String[] { context.getCurrentUserName() },
					Team.AUTH_ADMIN);
			VWBContext.setCurrentTid(-1);
			return new ModelAndView(
					"redirect:/system/createTeam?func=success&teamId=" + newTid);
		}
		return new ModelAndView("redirect:/system/createTeam?func=error");
	}

	@RequestMapping(params = "func=success")
	public ModelAndView success(HttpServletRequest request) {
		VWBContext context = this.getVWBContext(request);
		int newTid = Integer.parseInt(request.getParameter("teamId"));
		Team instance = teamService.getTeamByID(newTid);
		ModelAndView mv = layout(".aone.portal", context,
				"/jsp/aone/team/createTeamSuccess.jsp");
		mv.addObject("teamId", newTid);
		mv.addObject("team", instance);
		String teamUrl = urlGenerator.getAbsoluteURL(newTid, UrlPatterns.T_TEAM, null, null);
		teamUrl = teamUrl.replace(":80/", "/");
		if(teamUrl.endsWith("/")){
			teamUrl = teamUrl.substring(0, teamUrl.length()-1);
		}
		mv.addObject("teamUrl", teamUrl);
		mv.addObject(LynxConstants.PAGE_TITLE, "创建团队");
		return mv;
	}

	@RequestMapping(params = "func=validateTeamId")
	public void validateTeamId(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String teamId = request.getParameter("teamId");
		boolean flag = teamService.isValidateNewTeamName(teamId);
		PrintWriter writer = response.getWriter();
		writer.print(flag);
		writer.flush();
		writer.close();
	}

}
