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

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.AbstractSpaceController;
import net.duckling.ddl.web.bean.AdminHelper;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/system/admin")
public class LynxSystemNavigationController extends AbstractSpaceController{
	
	@RequestMapping
	public ModelAndView init(HttpServletRequest request){
		boolean b = AdminHelper.validateUser(VWBSession.getCurrentUid(request));
		if(!b){
			throw new RuntimeException(VWBSession.getCurrentUid(request)+"用户类型不正确");
		}
		VWBContext context = VWBContext.createContext(request,UrlPatterns.ADMIN);
		return layout(ELayout.LYNX_MAIN, context, "/jsp/aone/navigation.jsp");
	}
}
