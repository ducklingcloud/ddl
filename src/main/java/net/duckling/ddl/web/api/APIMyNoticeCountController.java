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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.mobile.DeviceTokenService;
import net.duckling.ddl.service.mobile.IphoneDeviceToken;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/myNoticeCount")
@RequirePermission(target="team",operation="view")
public class APIMyNoticeCountController extends APIBaseNoticeController {
	@Autowired
	private DeviceTokenService deviceTokenService;
	@SuppressWarnings("unchecked")
	@RequestMapping
	public void service(HttpServletRequest request, HttpServletResponse response) {
		String api = request.getParameter("api");
		String deviceToken = request.getParameter("deviceToken");
		deviceToken = validDeviceToken(deviceToken);
		
		String uid = findUser(request);
		Site site = findSite(request);
		String messageType = request.getParameter("messageType");
		int totalCount = totalCount(site, messageType, uid);
		
		if(deviceToken != null) {
			IphoneDeviceToken idt = new IphoneDeviceToken();
			idt.setDeviceToken(deviceToken);
			idt.setUid(uid);
			idt.setLastLoginTime(new Date());
			deviceTokenService.insertOrUpdateDeviceToken(idt);
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("api", api);
		jsonObj.put("totalCount", totalCount);
		JsonUtil.writeJSONObject(response, jsonObj);
	}
	
	private String validDeviceToken(String deviceToken) {
		if(deviceToken == null) return null;
		deviceToken = deviceToken.replaceAll("<", "");
		deviceToken = deviceToken.replaceAll(">", "");
		deviceToken = deviceToken.replaceAll(" ", "");
		return deviceToken;
	}
	
}
