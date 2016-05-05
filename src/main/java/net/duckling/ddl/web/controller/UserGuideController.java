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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.service.user.UserGuide;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/system/userguide")
@RequirePermission(authenticated = true)
public class UserGuideController extends BaseController{
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private URLGenerator urlGenerator;
	@RequestMapping(params="func=get")
	public void get(HttpServletRequest request, HttpServletResponse response, @RequestParam("module") String module){
		VWBContext context = getVWBContext(request, module);
		String uid = context.getCurrentUID();
		int step = aoneUserService.getUserGuideStep(uid, module);
		if(step < 0){
			step = 0;
			aoneUserService.createUserGuide(UserGuide.build(uid, module));
		}
		JSONObject obj = new JSONObject();
		obj.put("step", step);
		JsonUtil.writeJSONObject(response, obj);
	}
	
	@RequestMapping(params="func=update")
	public void update(HttpServletRequest request, HttpServletResponse response, @RequestParam("module") String module,
			@RequestParam("step") int step){
		VWBContext context = getVWBContext(request, module);
		String uid = context.getCurrentUID();
		aoneUserService.updateUserGuideStep(uid, module, step);
		JsonUtil.writeJSONObject(response, new JSONObject());
	}
	
	private VWBContext getVWBContext(HttpServletRequest request, String module){
		if(null!=module && !UserGuide.MODULE_TEAM.equals(module)){
			return VWBContext.createContext(request, UrlPatterns.DASHBOARD);
		}else{
			return VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
		}
	}
	@RequestMapping(params="func=redirect")
	public void redirect(@RequestParam("uid")String uid,HttpServletResponse resp) throws IOException{
		UserExt instance = aoneUserService.getUserExtInfo(uid);
		resp.sendRedirect(urlGenerator.getAbsoluteURL(UrlPatterns.USER, instance.getId()+"", null));
	}
}
