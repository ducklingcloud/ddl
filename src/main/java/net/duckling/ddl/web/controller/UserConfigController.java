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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserConfigService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.web.AbstractSpaceController;
import net.duckling.ddl.web.bean.AdminHelper;
import net.duckling.ddl.service.user.UserConfig;
import net.duckling.ddl.util.JsonUtil;
@Controller
@RequestMapping("/system/userConfig")
public class UserConfigController extends AbstractSpaceController{
	@Autowired
	private UserConfigService userConfigService;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private TeamService teamService;
	@RequestMapping
	public ModelAndView UserConfig(HttpServletRequest request){
		if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
			throw new RuntimeException(VWBSession.getCurrentUid(request)+"用户类型不正确");
		}
		VWBContext context = VWBContext.createContext(request,UrlPatterns.ADMIN);
		List<UserConfig> all = userConfigService.getAllConfig();
		ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/user/userConfig.jsp");
		if(all!=null){
			Map<String,Integer> map = new HashMap<String,Integer>();
			for(UserConfig u : all){
				List<Team> ts = teamService.getTeamByCreator(u.getUid());
				map.put(u.getUid(), ts.size());
			}
			mv.addObject("size", map);
		}
		mv.addObject("configs", all);
		return mv;
	}
	
	@RequestMapping(params="func=searchUser")
	public void searchUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject o = new JSONObject();
		if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
			o.put("result", false);
			o.put("message", "用户没有权限");
			JsonUtil.writeJSONObject(response, o);
			return;
		}
		String keyWord = request.getParameter("keyWord");
		List<UserExt> users = aoneUserService.searchUserByMail(keyWord);
		JSONArray array = new JSONArray();
		if(users!=null){
			for(UserExt u : users){
				JSONObject user = new JSONObject();
				user.put("uid", u.getUid());
				List<Team> ts = teamService.getTeamByCreator(u.getUid());
				user.put("teamSize", ts.size());
				UserConfig conf = userConfigService.getByUid(u.getUid());
				if(conf!=null){
					user.put("maxSize", conf.getMaxCreateTeam());
					user.put("description", conf.getDescription());
					user.put("configUid", conf.getConfigUid());
					user.put("configTime", conf.getConfigDate().toString());
				}else{
					user.put("maxSize", 0);
					user.put("description", "");
					user.put("configUid", "-");
					user.put("configTime", "-");
				}
				array.add(user);
			}
		}
		o.put("result", true);
		o.put("configs", array);
		JsonUtil.writeJSONObject(response, o);
	}
	
	
	@RequestMapping(params="func=queryUser")
	public void queryUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject o = new JSONObject();
		if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
			o.put("result", false);
			o.put("message", "用户没有权限");
			JsonUtil.writeJSONObject(response, o);
			return;
		}
		String uid = request.getParameter("uid");
		UserConfig u = userConfigService.getByUid(uid);
		o.put("uid", uid);
		if(u!=null){
			o.put("maxSize", u.getMaxCreateTeam());
			o.put("description", u.getDescription());
		}
		o.put("result", true);
		JsonUtil.writeJSONObject(response, o);
	}
	
	@RequestMapping(params="func=updateUserConfig")
	public void updateUserConfig(HttpServletRequest request,HttpServletResponse response){
		JSONObject o = new JSONObject();
		if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
			o.put("result", false);
			o.put("message", "用户没有权限");
			JsonUtil.writeJSONObject(response, o);
			return;
		}
		String uid = request.getParameter("uid");
		int size = Integer.parseInt(request.getParameter("size"));
		String description = request.getParameter("description");
		UserConfig u = userConfigService.getByUid(uid);
		if(u==null){
			u = new UserConfig();
			u.setConfigDate(new Date());
			u.setConfigUid(VWBSession.getCurrentUid(request));
			u.setDescription(description);
			u.setMaxCreateTeam(size);
			u.setUid(uid);
			userConfigService.insert(u);
		}else{
			u.setConfigDate(new Date());
			u.setConfigUid(VWBSession.getCurrentUid(request));
			u.setDescription(description);
			u.setMaxCreateTeam(size);
			userConfigService.update(u);
		}
		o.put("result", true);
		JsonUtil.writeJSONObject(response, o);
	}
	
	@RequestMapping(params="func=deleteUserConfig")
	public void deleteUserConfig(HttpServletRequest request,HttpServletResponse response){
		JSONObject o = new JSONObject();
		if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
			o.put("result", false);
			o.put("message", "用户没有权限");
			JsonUtil.writeJSONObject(response, o);
			return;
		}
		int id = Integer.parseInt(request.getParameter("id"));
		userConfigService.delete(id);
		o.put("result", true);
		JsonUtil.writeJSONObject(response, o);
	}
	
}
