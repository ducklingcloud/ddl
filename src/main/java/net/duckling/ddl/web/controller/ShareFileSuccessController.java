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

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * @date 2011-8-17
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/shareFileSuccess")
public class ShareFileSuccessController extends BaseController {
	@Autowired
	private TeamService teamService;
	@Autowired
	private URLGenerator urlGenerator;
	
	@RequestMapping
	public ModelAndView prepare(HttpServletRequest request) throws UnsupportedEncodingException {
		VWBContext context = getVWBContext(request);
		String isFirst = request.getParameter("isFirst");
		Integer tid = Integer.parseInt(request.getParameter("tid"));
		VWBContext.setCurrentTid(tid);
		Team team = teamService.getTeamByID(tid);
		String collectionURL = urlGenerator.getURL(tid, UrlPatterns.T_TEAM_HOME, "1" ,null);
		ModelAndView mv = layout(".aone.portal",context, "/jsp/aone/team/share/shareFileSuccess.jsp");
		mv.addObject("isFirst", isFirst);
		mv.addObject("collectionURL",collectionURL);
		mv.addObject("teamName",team.getName());
		mv.addObject("fileURLs", request.getParameterValues("fileURLs"));
		String[] array = request.getParameterValues("fileNames");
		if(array!=null&&array.length!=0){
			for(int i=0;i<array.length;i++){
				array[i] = java.net.URLDecoder.decode(array[i], "UTF-8");
			}
		}
		mv.addObject("fileNames", array);
		return mv;
	}
	
	private VWBContext getVWBContext(HttpServletRequest request) {
		return VWBContext.createContext(request,UrlPatterns.ADMIN);
	}
	
}
