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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * API接口中访问页面接口
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/pageInfo")
@RequirePermission(target="team", operation="view")
public class APIPageInfoController extends APIBaseController {
    @Autowired
    private IResourceService resourceService;
    
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(@RequestParam("itemId")Integer itemId,@RequestParam("itemType")String itemType, 
			HttpServletRequest request, HttpServletResponse response){
		Site site = findSite(request);
		Resource meta = resourceService.getResource(itemId, site.getId());
		
		String uid = findUser(request);
		Set<String> userSet = meta.getMarkedUserSet();
		boolean marked = false;
		if(userSet != null && userSet.size() > 0) {
			for(String user : userSet) {
				if(uid.equals(user)) {
					marked = true;
				}
			}
		}
		
		JSONObject object= new JSONObject();
		object.put("marked", marked);
		object.put("fileType", meta.getFileType());
		object.put("rid", meta.getRid());
		object.put("size", meta.getSize());
		JsonUtil.writeJSONObject(response, object);
	}
	
}