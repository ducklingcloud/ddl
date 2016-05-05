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
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.OnDeny;

import org.json.simple.JSONObject;


/**
 * 基础的Controller
 * @date 2011-9-2
 * @author xiejj@cnic.cn
 */
public class APIBaseController {
	
	@SuppressWarnings({ "unchecked"})
	@OnDeny("*")
	public void onDeny(String method, HttpServletRequest request, HttpServletResponse response){
		JSONObject object = new JSONObject();
		object.put("message", "Permission denied or session is time out.");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		JsonUtil.writeJSONObject(response, object);
	}
	
	public Site findSite(HttpServletRequest request) {
		return VWBContext.findSite(request);
	}
	
	public String findUser(HttpServletRequest request) {
		return VWBSession.findSession(request).getCurrentUser().getName();
	}
	
	public VWBContext findVWBContext(HttpServletRequest request, String requestContext) {
		return VWBContext.createContext(request, requestContext);
	}
	
}
