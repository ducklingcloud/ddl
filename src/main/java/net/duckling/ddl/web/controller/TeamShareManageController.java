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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequirePermission(target = "team", operation = "view")
@RequestMapping("{teamCode}/shareManage")
public class TeamShareManageController extends BaseController{
	@Autowired
	private ShareResourceService shareResourceService;
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private TeamService teamService;
	
	@RequestMapping
	public ModelAndView display(HttpServletRequest request){
		VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
		ModelAndView m = layout(ELayout.LYNX_MAIN, context,"/jsp/shareManage.jsp");
		int tid = VWBContext.getCurrentTid();
		List<ShareResource> srs = shareResourceService.queryByTid(tid);
		List<Resource> res = shareResourceService.queryTeamShareResource(tid);
		Map<Integer,ShareResource> srsMap = getShareResourceMap(srs);
		Map<String,String> userNames = getUserName(srs);
		m.addObject("res", res);
		m.addObject("users", userNames);
		m.addObject("srs", srsMap);
		m.addObject("shareUrl", getShareUrl(srs));
		m.addObject("reSize", getResourceSize(res));
		m.addObject("list", urlGenerator.getURL(tid, UrlPatterns.T_LIST, null, null));
		m.addObject("pageType", "list");
		addMyTeam(request, m);
		return m;
	}
	
	private ModelAndView addMyTeam(HttpServletRequest request, ModelAndView mv){
    	int myTeamId = teamService.getPersonalTeamNoCreate(VWBSession.getCurrentUid(request));
		String myTeamCode = teamService.getTeamNameFromEmail(VWBSession.getCurrentUid(request));
		mv.addObject("myTeamId",myTeamId);
		mv.addObject("myTeamCode",myTeamCode);
		return mv;
    }
	private Map<Integer,String> getResourceSize(List<Resource> res){
		Map<Integer,String> result = new HashMap<Integer,String>();
		for(Resource r : res){
			if(r.isFolder()){
				result.put(r.getRid(), "-");
			}else{
				String size = FileSizeUtils.getFileSize(r.getSize());
				result.put(r.getRid(), size);
			}
		}
		return result;
	}
	
	private Map<Integer,String> getShareUrl(List<ShareResource> srs){
		Map<Integer,String> re = new HashMap<Integer,String>();
		for(ShareResource sr: srs){
			String url = urlGenerator.getAbsoluteURL(UrlPatterns.RESOURCE_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(sr.getRid());
			re.put(sr.getRid(), url);
		}
		return re;
	}
	
	private Map<String, String> getUserName(List<ShareResource> srs) {
		Set<String> user = new HashSet<String>();
		for(ShareResource sr: srs){
			user.add(sr.getShareUid());
		}
		Map<String,String> re = new HashMap<String,String>();
		for(String uid : user){
			SimpleUser us = aoneUserService.getSimpleUserByUid(uid);
			re.put(us.getUid(), us.getName());
		}
		return re;
	}

	private Map<Integer,ShareResource> getShareResourceMap(List<ShareResource> srs){
		Map<Integer,ShareResource> re = new HashMap<Integer,ShareResource>();
		for(ShareResource sr: srs){
			re.put(sr.getRid(), sr);
		}
		return re;
	}
	
	@ResponseBody
	@RequestMapping(params="func=delete")
	public JSONObject deleteShareResource(HttpServletRequest request){
		String[] rs = request.getParameterValues("rids[]");
		int[] rids = new int[rs.length];
		for(int i = 0;i<rs.length;i++){
			rids[i] = Integer.parseInt(rs[i]);
		}
		for(int rid:rids){
			shareResourceService.delete(rid);
		}
		JSONObject obj = new JSONObject();
		obj.put("success", true);
		return obj;
	}
	
}
