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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.util.JsonUtil;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("/logBrowseTime")
public class LogBrowseTimeController {
	/**
	 * @param stayTime  页面停留时间
	 * @param currentUrl  用户访问的页面的url
	 */
	@RequestMapping(value = "/ajax", method = RequestMethod.POST)
	@ResponseBody
	@WebLog(method = "st.pv-timing", params = "stayTime,currentUrl")
	public void ajaxDatas(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("stayTime") String stayTime,
			@RequestParam("currentUrl") String currentUrl) {
		JSONObject retVal = new JSONObject();
		JsonUtil.writeJSONObject(response, retVal);
		//System.out.println(stayTime);
	}
}
