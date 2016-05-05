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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * API调用的Controller（查询集合页面）
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/myStarmark")
@RequirePermission(target="team", operation="view")
public class APIMyStarmarkController extends APIBaseResourceController {
    
    @Autowired
    private IResourceService resourceService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response){
		JSONObject object= new JSONObject();
		Site site =  findSite(request);
		String uid = findUser(request);
		int tid = site.getId();
		List<Resource> reslist = resourceService.getStarmarkResource(uid, tid);
		reslist = filterResult(reslist);
		JSONArray array = JsonUtil.getJSONArrayFromResourceList(reslist);
		object.put("records", array);
		String api = request.getParameter("api");
		object.put("api", api);
		JsonUtil.writeJSONObject(response, object);
	}
	
	private List<Resource> filterResult(List<Resource> resList) {
		List<Resource> result = new ArrayList<Resource>();
		Set<Integer> bids = new HashSet<Integer>();
		if(null == resList || resList.size() <= 0){
			return null;
		}
		for(Resource res : resList){
			int bid = res.getBid();
			if(bid == 0) {//不在bundle里边
				if(!res.isBundle()){
					result.add(res);
				} else if(!bids.contains(res.getRid())) {
					result.add(res);
					bids.add(res.getRid());
				}
			}
		}
		return result;
	}
	
}