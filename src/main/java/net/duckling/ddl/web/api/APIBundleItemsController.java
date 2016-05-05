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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/api/bundleItems")
@RequirePermission(target="team", operation="view")
public class APIBundleItemsController extends APIBaseResourceController {
    
	@Autowired
	private FolderPathService folderPathService;
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response){
		JSONObject object= new JSONObject();
		Site site =  findSite(request);
		List<Resource> elementList = null;
		
		String bidStr = request.getParameter("bid");
		if(StringUtils.isNotEmpty(bidStr)) {// 查找指定bundle下的所有文件
			int tid = site.getId();
			int bid = Integer.parseInt(bidStr);
			elementList = folderPathService.getChildren(tid, bid);
		}
		
		JSONArray array = JsonUtil.getJSONArrayFromResourceList(elementList);
		object.put("records", array);
		JsonUtil.writeJSONObject(response, object);
	}
	
}