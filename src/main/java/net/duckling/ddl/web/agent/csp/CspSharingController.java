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
package net.duckling.ddl.web.agent.csp;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PathName;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.UrlCoder;
import net.duckling.ddl.web.agent.util.AuthUtil;
import net.duckling.ddl.web.vo.ErrorMsg;
import net.duckling.ddl.web.vo.VoUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/system/csp/sharing")
public class CspSharingController extends CspBaseController {
	private static final Logger LOG = Logger.getLogger(CspSharingController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public void sharing(@RequestParam("path") String path,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		String teamCode = request.getParameter("teamCode");
		String auth = request.getParameter("auth");
		
		String uid = AuthUtil.getAuthEmail(auth);
		if(StringUtils.isEmpty(uid)){
			LOG.error("need permission. {uid:"+ uid +",path:"+ path +"\"teamCode\":" + teamCode + "}");
			writeError(ErrorMsg.NEED_PERMISSION, request, response);
			return;
		}
		Team team = getUserTeam(uid, teamCode);
		if(team == null){
			LOG.error("need permission. {uid:"+ uid +"path:"+ path +"\"teamCode\":" + teamCode + "}");
			writeError(ErrorMsg.NEED_PERMISSION, request, response);
			return;
		}
		
		path = StringUtils.defaultIfBlank(path, PathName.DELIMITER);
		path = UrlCoder.decode(path);
		Resource r = folderPathService.getResourceByPath(team.getId(), path);
		if(r == null || r.getRid() == 0){
			LOG.error("file not found. {path:"+ path +",\"teamCode\":" + teamCode + "}");
			writeError(ErrorMsg.NOT_FOUND, request,  response);
			return;
		}
		
		ShareResource sr = shareResourceService.get(r.getRid());
		if(sr==null){
			sr = createShareResource(r.getRid(), uid);
		}else{
//			sr.setLastEditor(uid);
//			sr.setLastEditTime(new Date());
//			shareResourceService.update(sr);
		}
		sr.setTitle(r.getTitle());
		sr.setShareUrl(sr.generateShareUrl(urlGenerator));
		
		LOG.info("file shared successfully. {teamCode:"+ teamCode +",path:" + path +",title:" + r.getTitle() +"}");
		JsonUtil.writeJSONP(request, response, VoUtil.getShareResourceVo(sr), null);
	}
	
	
	private ShareResource createShareResource(int rid, String uid) {
		ShareResource sr;
		sr = new ShareResource();
		sr.setRid(rid);
		sr.setTid(getCurrentTid());
		sr.setShareUid(uid);
		sr.setCreateTime(new Date());
		sr.setLastEditor(uid);
		sr.setLastEditTime(new Date());
		shareResourceService.add(sr);
		return sr;
	}
	
	@Autowired
	TeamService teamService;
	@Autowired
	private URLGenerator urlGenerator;
    @Autowired
	private ShareResourceService shareResourceService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
	
}
