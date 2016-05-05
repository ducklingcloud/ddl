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
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/crawler/page")
@RequirePermission(authenticated = true)
public class APIPageCrawlerController {

	@Autowired
    private IResourceService resourceService;
    
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response) {
		String teamCode = request.getParameter("team");
		Site site = VWBContainerImpl.findContainer().getSiteByName(teamCode);
		Resource render = findPageRenderByDifferentId(request,site);
		writePageRender(response,render);
	}
	
	private Resource findPageRenderByDifferentId(HttpServletRequest request,Site site) {
		Integer rid = getIntegerParamter(request,"rid");
		if(rid!=null){
			return extractFromPageId(rid,site);
		}
		return null;
	}
	
	private Resource extractFromPageId(Integer rid,Site site){
		Resource r = resourceService.getResource(rid);
		if(r!=null&&r.isPage()&&r.getTid()==VWBContext.getCurrentTid()){
			return r;
		}
		return null;
	}
	

	private Integer getIntegerParamter(HttpServletRequest request,String key){
		String idStr = request.getParameter(key);
		if (idStr != null) {
			return Integer.parseInt(idStr);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void writePageRender(HttpServletResponse response, Resource render) {
		JSONObject object = new JSONObject();
		if(render==null){
			object.put("error", "Can not find your target page, please check your request url.");
		}else{
			object.put("rid", render.getRid());
			object.put("title", render.getTitle());
		}
		JsonUtil.writeJSONObject(response, object);
	}

}
