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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.AbstractRecommendContrller;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;


@Controller
@RequestMapping("/api/pageRecommend")
@RequirePermission(target="team", operation="view")
public class APIPageRecommendController extends AbstractRecommendContrller {
	
    @Autowired
	private IResourceService resourceService;
    @Autowired
    private EventDispatcher eventDispatcher;
    
	@SuppressWarnings({ "unchecked"})
	@OnDeny("*")
	public void onDeny(String method, HttpServletRequest request, HttpServletResponse response){
		JSONObject object = new JSONObject();
		object.put("message", "Permission denied or session is time out.");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		JsonUtil.writeJSONObject(response, object);
	}
	/**
	 * 发送推荐，必选参数还有users,内容是逗号分割的字符串,格式如users=user1@cstet.cn,user2@cstnet.cn
	 * @param itemId 资源ID
	 * @param itemType 资源类型
	 * @param request
	 * @param response
	 */
	@WebLog(method = "apiAddRecommend", params = "itemId,itemType")
    @RequestMapping("/add")
	public void addRecommend(@RequestParam("itemId")Integer itemId,@RequestParam("itemType")String itemType, 
			HttpServletRequest request, HttpServletResponse response){
		VWBContext context=getVWBContext(request, itemId, itemType);
		this.submitRecommend(context,request, response);
	}
	
	@RequestMapping("/prepare")
	public void allRecommendUser(@RequestParam("itemId")Integer itemId,@RequestParam("itemType")String itemType, 
			HttpServletRequest request, HttpServletResponse response){
		prepareRecommend(response);
	}
	
	private VWBContext getVWBContext(HttpServletRequest request,int itemId, String itemType) {
		Site site = VWBContext.findSite(request);
		Resource res = resourceService.getResource(itemId,site.getId());
		return VWBContext.createContext(request,UrlPatterns.MYSPACE, res);
	}
	
	@SuppressWarnings("unchecked")
	private  void submitRecommend(VWBContext ctx, HttpServletRequest request, HttpServletResponse response) {
		String currUser = ctx.getCurrentUID();
		String remark = request.getParameter("remark");
		String itemType = request.getParameter("itemType");
		String users = request.getParameter("users");
		String[] userIds = users.split(",");
		userIds = APICommonUtil.jsonArrayStringReplace(userIds);
		int itemId = Integer.parseInt(request.getParameter("itemId"));
		Resource res = resourceService.getResource(itemId, VWBContext.getCurrentTid());
		
		if (res.isFile()) {
			eventDispatcher.sendFileRecommendEvent(ctx.getTid(), res.getRid(), res.getTitle(), currUser, res.getLastVersion(),
							remark, combineRecipients(userIds),null);
		} else if(res.isPage()){
			eventDispatcher.sendPageRecommendEvent(ctx.getTid(), res.getRid(), res.getTitle(), currUser, res.getLastVersion(),
							remark, combineRecipients(userIds),null);
		}else if(res.isFolder()){
			eventDispatcher.sendFolderRecommendEvent(ctx.getTid(), res.getRid(), res.getTitle(), currUser, res.getLastVersion(),
					remark, combineRecipients(userIds),null);
		}
		
		JSONObject object = new JSONObject();
		object.put("status", "success");
		object.put("itemType", itemType);
		JsonUtil.writeJSONObject(response, object);
	}
	
	private static String combineRecipients(String[] userIds) {
		if (userIds == null || userIds.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < userIds.length; i++) {
			sb.append(userIds[i]);
			if (i != (userIds.length - 1)) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
}