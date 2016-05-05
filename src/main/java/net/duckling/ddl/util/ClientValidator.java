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
package net.duckling.ddl.util;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.url.UrlPatterns;

import org.apache.commons.lang.StringUtils;

public class ClientValidator {

	private static Pattern ddlIpPattern;
	
	/**
	 * 校验是否是允许的客户端ip
	 * @param request
	 * @return
	 */
	public static boolean validate(HttpServletRequest request) {
		Pattern pattern =getClientIpPattern(request);
		if(pattern==null){
			return false;
		}
		String realIp = getRealIp(request);
		return pattern.matcher(realIp).matches();
	}
	
	public static String getRealIp(HttpServletRequest request){
		String nginxIp = request.getHeader("x-real-ip");
		if(StringUtils.isNotEmpty(nginxIp)){
			return nginxIp;
		}
		return request.getRemoteAddr();
	}
	
	public static Pattern getClientIpPattern(HttpServletRequest request){
		if(ddlIpPattern==null){
			initIpPattren(request);
		}
		return ddlIpPattern;
	}
	
	private static synchronized void initIpPattren(HttpServletRequest request) {
		if(ddlIpPattern==null){
			VWBContext context = VWBContext.createContext(request,UrlPatterns.ADMIN);
			String pattern =context.getContainer().getProperty(KeyConstants.DDL_CLIENT_ACCEPTED_IP);
			if(pattern==null||pattern.length()==0){
				return;
			}
			ddlIpPattern = Pattern.compile(pattern);
		}
	}
}
