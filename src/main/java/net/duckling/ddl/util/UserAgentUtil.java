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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class UserAgentUtil {

	public final static String MOBILE = "mobile";
	private final static String PC = "pc";
	
	protected static final Logger LOG = Logger.getLogger(UserAgentUtil.class);

	/**
	 * 判断是否是从手机或者PAD访问的
	 * 
	 * @param request
	 * @return true:为移动终端 false:为PC用户
	 */
	public static boolean isMobile(HttpServletRequest request) {
		// 请求强制PC端
		if (PC.equals(request.getParameter("adapter"))) {
			return false;
		}

		String userAgent = request.getHeader("User-Agent");
		String wapProfile = request.getHeader("x-wap-profile");
		// 在TD-LTE环境下发现请求包里会不携带User-Agent头，所以判断userAgent是否为null
		if (userAgent == null)
			return true;
		if (userAgent.contains("Android") || userAgent.contains("iPhone")
				|| (wapProfile != null && wapProfile.length() > 0)) {
			return true;
		}
		return false;
	}

}
