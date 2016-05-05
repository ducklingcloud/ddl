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

package net.duckling.ddl.web.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.StatusUtil;
import net.duckling.ddl.util.UserAgentUtil;
import net.duckling.ddl.web.bean.ErrorMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.common.util.Base64Util;

/**
 * @date 2011-6-21
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/activation/{activationURL}")
public class ActivationController extends BaseController {
	private static final Logger LOG = Logger.getLogger(ActivationController.class);
	@Autowired
	private TeamService teamService;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private URLGenerator urlGenerator;
	
	private ModelAndView getErrorModelAndView(HttpServletRequest request) {
		VWBContext context = getVWBContext(request);
		ModelAndView mv = layout(".aone.portal", context,"/jsp/aone/errors/simpleError.jsp");;
		ErrorMessage message = new ErrorMessage();
		message.setMessage("输入的URL错误，请输入正确的URL!");
		message.setTitle("激活错误页面");
		mv.addObject("exception", message);
		return mv;
	}
	
	private VWBContext getVWBContext(HttpServletRequest pRequest) {
        return VWBContext.createContext(pRequest,UrlPatterns.ACTIVITION);
    }
	private ModelAndView redirect(Activation instance, HttpServletRequest request){
		ModelAndView mv = null;
		VWBContext context = getVWBContext(request);
		//激活链接中包含团队的情况
		if (instance.getTname() != null && !"".equals(instance.getTname())) {
			String tName = Base64Util.decodeBase64(instance.getTname());
			Team team =teamService.getTeamByName(tName);
			//此处增加AccessType判断是为了漏洞修复：若Tname的团队为private，则 通过推广链接注册的用户不能加入该团队
			if (team != null && !Team.ACCESS_PRIVATE.equals(team.getAccessType())) {
				if(Team.ACCESS_PUBLIC.equals(team.getAccessType())){
					teamService.addTeamMembers(team.getId(),
								new String[] { instance.getEmail() },
								new String[] { instance.getName() }, team.getDefaultMemberAuth());
					String teamHomeURL = urlGenerator.getURL(team.getId(),UrlPatterns.T_TEAM_HOME, null,null);
					mv = new ModelAndView(new RedirectView(teamHomeURL));
				}else{
					String teamHomeURL = urlGenerator.getURL(UrlPatterns.JOIN_PUBLIC_TEAM, null,"func=join&teamId="+team.getId());
					mv = new ModelAndView(new RedirectView(teamHomeURL));
				}
			}else{
				String switchTeamURL = urlGenerator.getAbsoluteURL(UrlPatterns.SWITCH_TEAM, "",null);
				ModelAndView mv1 =  new ModelAndView(new RedirectView(switchTeamURL));
				mv = mv1;
			}
		}else{
			String switchTeamURL = urlGenerator.getAbsoluteURL(UrlPatterns.SWITCH_TEAM, "",null);
			ModelAndView mv1 =  new ModelAndView(new RedirectView(switchTeamURL));
			mv = mv1;
		}
		return mv;
	}

	private boolean validateActivationURL(String[] keyAndId){
		if(keyAndId==null||keyAndId.length!=2){
			return false;
		}
		String id = keyAndId[1];
		if(id==null||"".equals(id)){
			return false;
		}
		try{
			Integer.valueOf(id);
		}catch(Exception e){
			return false;
		}
		return true;
	}
    private void loginWithoutRedirect(VWBContext context,String email,String displayName) {
		Collection<Principal> ps = new ArrayList<Principal>();
		Principal p = new UserPrincipal(email,displayName,email);
		ps.add(p);
		context.getVWBSession().setPrincipals(ps);
		teamService.getPersonalTeam(email, displayName);
		teamService.addTeamMembers(1, new String[]{email}, new String[]{displayName},Team.AUTH_EDIT);
	}
	@RequestMapping
	public ModelAndView acceptActivation(HttpServletRequest request,@PathVariable("activationURL")String activationURL) {
		String[] keyAndId = EncodeUtil.decodeKeyAndID(activationURL);
		if(!validateActivationURL(keyAndId)){
			LOG.warn("解析activationURL:["+activationURL+"]错误");
			return getErrorModelAndView(request);
		}
		VWBContext context = getVWBContext(request);
		Activation instance = aoneUserService.getActivationByKeyAndID(keyAndId[0],keyAndId[1]);
		instance.setEmail(instance.getEmail().toLowerCase());
		ModelAndView mv = null;
		if(StatusUtil.isWaiting(instance.getStatus())) {
			aoneUserService.createAccountInUMT(instance.getEmail(),instance.getName(),instance.getPassword());
			aoneUserService.addToBatchUsers(new String[] {instance.getEmail()},
					new String[] {instance.getName()});
			instance.setStatus(StatusUtil.ACCEPT);
			aoneUserService.updateActivationStatus(instance);
			
			if(UserAgentUtil.isMobile(request)){
				mv = layoutAdaptive(".aone.portal", context,"/jsp/aone/regist/activationSuccess.jsp");
			}else{
				loginWithoutRedirect(context, instance.getEmail(), instance.getName());
				mv = redirect(instance, request);
			}
			
		}else {
			//激活URL已经失效
			mv = layout(".aone.portal", context,"/jsp/aone/regist/activationError.jsp");
		}
		return mv;
	}
	
}
