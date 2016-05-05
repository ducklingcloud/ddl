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
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


/**
 * @date 2011-10-18
 * @author clive
 */

@Controller
@RequirePermission(authenticated=true)
public class CopyFileController extends BaseController {
    
    
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private FileVersionService fileVersionService ;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private IResourceService resourceService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private TeamSpaceSizeService teamSpaceSizeService;
	@RequestMapping("/copyfile/{downloadURL}")
	public ModelAndView copyFileToMySpace(HttpServletRequest request,
			@PathVariable("downloadURL") String downloadURL) throws UnsupportedEncodingException{
		String[] tempArray = EncodeUtil.getDecodeArray(downloadURL);
		int tid = Integer.parseInt(tempArray[0]);
		int rid = Integer.parseInt(tempArray[2]);
		VWBContext context = getVWBContext(request);
		String uid = context.getCurrentUID();
		String userName = aoneUserService.getUserNameByID(uid);
		int mySpaceId = teamService.getPersonalTeam(uid,userName);
		VWBContext.setCurrentTid(tid);
		FileVersion oldFileVersion = fileVersionService.getLatestFileVersion(rid, tid);
		if(!teamSpaceSizeService.validateTeamSize(mySpaceId, oldFileVersion.getSize())){
			ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/team/share/shareFileError.jsp");
			mv.addObject("message", "团队空间已满！");
			return mv;
		}
		Resource r = resourceService.getResource(oldFileVersion.getRid());
		VWBContext.setCurrentTid(mySpaceId);
		FileVersion newVersion = resourceOperateService.referExistFileByClbId(mySpaceId,0, uid, oldFileVersion.getClbId(),oldFileVersion.getClbVersion(), r.getTitle(), oldFileVersion.getSize());
		VWBContext.setCurrentTid(-1);
		String redirectURL = urlGenerator.getAbsoluteURL(UrlPatterns.COPYFILE, downloadURL,null)+"/success";
		ModelAndView mv = new ModelAndView(new RedirectView(redirectURL));
		mv.addObject("uid",uid);
		mv.addObject("tid", mySpaceId);
		mv.addObject("rid",newVersion.getRid());
		mv.addObject("fileName",URLEncoder.encode(oldFileVersion.getTitle(),"UTF-8"));
		return mv;
	}
	
	@RequestMapping("/copyfile/{downloadURL}/success")
	public ModelAndView copySuccess(HttpServletRequest request) throws UnsupportedEncodingException{
		int tid = Integer.parseInt(request.getParameter("tid"));
		int rid = Integer.parseInt(request.getParameter("rid"));
		VWBContext.setCurrentTid(tid);
		VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM);
		ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/team/share/copyFileSuccess.jsp");
		mv.addObject("tagURL", urlGenerator.getURL(tid,UrlPatterns.T_LIST, "",""));
		mv.addObject("teamURL", urlGenerator.getURL(tid,UrlPatterns.T_TEAM_HOME, tid+"",""));
		mv.addObject("fileURL",urlGenerator.getURL(tid, UrlPatterns.T_VIEW_R, rid+"",""));
		mv.addObject("fileName",URLDecoder.decode(request.getParameter("fileName"),"UTF-8"));
		return mv;
	}
	
	private VWBContext getVWBContext(HttpServletRequest request) {
		return VWBContext.createContext(request, "copyfile");
	}
	
}
