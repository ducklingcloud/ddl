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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.mobile.MobileVersionService;
import net.duckling.ddl.service.mobile.MobileVersion;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.bean.AdminHelper;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/system/mobileversion")
public class MobileVersionConfigController extends BaseController {
	@Autowired
	private MobileVersionService mobileVersionService;

	@RequestMapping
	public ModelAndView mobileVersion(HttpServletRequest request){
		VWBContext c = VWBContext.createContext(request,UrlPatterns.ADMIN);
		String uid = c.getCurrentUID();
		if(!AdminHelper.validateUser(uid)){
			throw new RuntimeException(uid+"用户类型不正确");
		}
		MobileVersion android = mobileVersionService.getLatestVersionByType(MobileVersion.TYPE_ANDROID);
		MobileVersion ios = mobileVersionService.getLatestVersionByType(MobileVersion.TYPE_IOS);
		ModelAndView mv = layout(ELayout.LYNX_MAIN, c,"/jsp/aone/mobile/mobileVersionConfig.jsp");
		mv.addObject("android", android);
		mv.addObject("ios", ios);
		return mv;
	}
	
	@RequestMapping(params="func=createNewVersion")
	public void createMobileVersion(HttpServletRequest request,HttpServletResponse resp) throws IOException{
		VWBContext c = VWBContext.createContext(request,UrlPatterns.ADMIN);
		String uid = c.getCurrentUID();
		if(!AdminHelper.validateUser(uid)){
			throw new RuntimeException(uid+"用户类型不正确");
		}
		String version = request.getParameter("mobileVersion");
		if(StringUtils.isNotEmpty(version)){
			MobileVersion m = new MobileVersion();
			m.setVersion(version);
			String type = request.getParameter("type");
			if("android".equals(type)){
				m.setType(MobileVersion.TYPE_ANDROID);
			}else{
				m.setType(MobileVersion.TYPE_IOS);
			}
			m.setCreator(uid);
			m.setDescription(request.getParameter("description"));
			mobileVersionService.create(m);
		}
		resp.sendRedirect("/system/mobileversion");
	}
}
