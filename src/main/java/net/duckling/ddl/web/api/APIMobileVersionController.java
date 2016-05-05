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

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.service.mobile.MobileVersionService;
import net.duckling.ddl.service.mobile.MobileVersion;
import net.duckling.ddl.util.JsonUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("api/mobileVersion")
public class APIMobileVersionController extends APIBaseController{
	
	private static final Logger LOG=Logger.getLogger(APIMobileVersionController.class);
	
	@Autowired
	private MobileVersionService mobileVersionService;
	@Autowired
	private DucklingProperties config;
	@RequestMapping
	public void mobileVersion(HttpServletRequest request,HttpServletResponse resp){
		String type = request.getParameter("type");
		if(toDupdate()){
			formatDupdate(resp,type);
		}else{
			JSONObject jsonObj = new JSONObject();
			if("android".equals(type)){
				type = MobileVersion.TYPE_ANDROID;
			}else if("ios".equals(type)){
				type = MobileVersion.TYPE_IOS;
			}else{
				jsonObj.put("result", false);
				jsonObj.put("message", "type="+type+";不符合规定类型\"android\"和\"ios\"");
				JsonUtil.writeJSONObject(resp, jsonObj);
				return;
			}
			MobileVersion m = mobileVersionService.getLatestVersionByType(type);
			if(m==null){
				jsonObj.put("result", false);
				jsonObj.put("message", "系统没有获取到最新版本号");
				JsonUtil.writeJSONObject(resp, jsonObj);
				return;
			}
			jsonObj.put("updateMessage", m.getDescription());
			jsonObj.put("result", true);
			jsonObj.put("version", m.getVersion());
			jsonObj.put("downloadUrl", "http://www.escience.cn/apks/ddl-latest.apk");
			JsonUtil.writeJSONObject(resp, jsonObj);
		}
	}
	
	private boolean toDupdate(){
		String t = config.getProperty("ddl.mobile.version");
		return !StringUtils.isEmpty(t);
	}
	
	
	private void formatDupdate(HttpServletResponse resp,String type){
		HttpClient dClient = new HttpClient();
		PostMethod method = new PostMethod(getDupdateUrl());
		method.setParameter("type", type);
		method.setParameter("projectName", getProjectName());
		try {
			dClient.executeMethod(method);
			String response = method.getResponseBodyAsString();
			Map<String,Object> map = dealJsonResult(response);
			JSONObject obj = new JSONObject();
			obj.put("updateMessage", map.get("descreption"));
			obj.put("result", map.get("success"));
			obj.put("version", map.get("version"));
			String url = (String)map.get("downloadUrl");
			if(StringUtils.isEmpty(url)){
				url = "http://www.escience.cn/apks/ddl-latest.apk";
			}
			obj.put("downloadUrl", url);
			obj.put("isForce", map.get("forcedUpdate"));
			JsonUtil.writeJSONObject(resp, obj);
		} catch (HttpException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		} catch (ParseException e) {
			LOG.error("", e);
		}
		
	}


	private String getProjectName() {
		return "ddl";
	}

	private Map<String, Object> dealJsonResult(String response) throws ParseException {
		Map<String,Object> result = new HashMap<String,Object>();
		org.json.JSONObject obj =new org.json.JSONObject(response);
		Iterator<String> keys = obj.keys();
		String key = null;
		while(keys.hasNext()){
			key = keys.next();
			result.put(key,obj.get(key));
		}
		return result;
	}
	private String getDupdateUrl() {
		return config.getProperty("ddl.mobile.version.url");
	}
}
