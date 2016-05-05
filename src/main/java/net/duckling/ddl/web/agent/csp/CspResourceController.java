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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
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
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/system/csp/resource")
public class CspResourceController  extends CspBaseController{
	
	private static final Logger LOG = Logger.getLogger(CspResourceController.class);
	private static final String INCLUDE_FILE = "file";
	private static final String ORDER_BY_TITLE = "title";
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public void search(
			@RequestParam(value="begin", required=false) Integer begin,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="includes", required=false) String includes,
			@RequestParam(value="q", required=false) String q,
			@RequestParam(value="orderBy", required=false) String orderBy,
			@RequestParam(value="order", required=false) String order,
			HttpServletRequest request,
			HttpServletResponse response) {
		String teamCode = request.getParameter("teamCode");
		String auth = request.getParameter("auth");
		String email = AuthUtil.getAuthEmail(auth);
		if(StringUtils.isEmpty(email)){
			LOG.error("need permission. {email:"+email+",q:"+ q +"\"teamCode\":" + teamCode +"}");
			writeError(ErrorMsg.NEED_PERMISSION, request, response);
			return;
		}
		Team team = getUserTeam(email, teamCode);
		if(team == null){
			LOG.error("team not found. {email:"+email+",q:"+ q +"\"teamCode\":" + teamCode+"}");
			writeError(ErrorMsg.NEED_PERMISSION, request, response);
			return;
		}
		q = UrlCoder.decode(q);
		begin = begin==null||begin<0 ? 0 : begin;
		limit = limit==null||limit<=0 ? 10 : limit;
		int tid = getCurrentTid();
		String type = LynxConstants.TYPE_FILE;
		int rootRid = 0;
		if(INCLUDE_FILE.equals(includes)){
			type = LynxConstants.TYPE_FILE;
		}
		
		String orderStr = "";
		if(ORDER_BY_TITLE.equals(orderBy)){
			orderStr = LynxConstants.ASC.equals(order) ? "title" : "titleDesc";
		}else{
			orderStr = LynxConstants.ASC.equals(order) ? "time" : "timeDesc";
		}

		PaginationBean<Resource> resources = folderPathService.getChildren(tid, rootRid, type, orderStr, begin, limit, q);
		//设置path属性
		folderPathService.setResourceListPath(resources.getData());
		
		JsonUtil.writeJSONP(request, response, VoUtil.getResourcePaginationVo(resources), null);
	}
	
	@Autowired
    private IResourceService resourceService;
	@Autowired
    private FolderPathService folderPathService;
}
