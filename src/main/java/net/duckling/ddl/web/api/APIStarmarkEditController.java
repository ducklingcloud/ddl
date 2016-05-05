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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.IStarmarkService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/editStarmark")
@RequirePermission(target="team", operation="view")
public class APIStarmarkEditController extends APIBaseController {
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IStarmarkService starmarkService;
    
    
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response){
		Site site = findSite(request);
		int tid = site.getId();
		int rid = getRid(request, site);
		String uid = findUser(request);
		String operate = request.getParameter("operate");
		if("add".equals(operate)) {
		    starmarkService.addStarmark(uid,rid,tid);
		} else if("remove".equals(operate)) {
		    starmarkService.removeStarmark(uid,rid,tid);
		}
		
		JSONObject json = new JSONObject();
		json.put("status", "success");
		json.put("rid", rid);
		JsonUtil.writeJSONObject(response, json);
	}
	
	private int getRid(HttpServletRequest request,Site site){
		String rid = request.getParameter("rid");
		if(StringUtils.isNotEmpty(rid)){
			try{
				return Integer.parseInt(rid);
			}catch(Exception e){
			}
		}
		String itemId = request.getParameter("itemId");
		String itemType = request.getParameter("itemType");
		if(StringUtils.isEmpty(itemId)||StringUtils.isEmpty(itemType)){
			throw new RuntimeException("缺乏需求参数");
		}
		int id = Integer.parseInt(itemId);
		Resource r = resourceService.getResource(id, site.getId());
		if(r!=null){
			return r.getRid();
		}else{
			throw new RuntimeException("需求参数错误itemId="+itemId+";itemType="+itemType);
		}
	}
}