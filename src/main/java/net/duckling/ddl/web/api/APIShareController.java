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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.IdentifyingCode;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequirePermission(target = "team", operation = "view")
@RequestMapping("/api/shareResource")
public class APIShareController {
	@Autowired
	private ShareResourceService shareResourceService;
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private AoneMailService aonemailService;
	@Autowired
	private IResourceService resourceService;
	
	/**
	 * 分享文件
	 * @param rid
	 * @param isCreateFetchCode
	 * @param request
	 * @return 分享url和提取码
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(params="func=share")
	public JSONObject share(@RequestParam("rid")int rid,
			@RequestParam(value="isCreateFetchCode", required=false) boolean isCreateFetchCode,
			HttpServletRequest request){
		ShareResource sr = shareResourceService.get(rid);
		JSONObject obj = new JSONObject();
		if(sr==null){
			Resource r = resourceService.getResource(rid, VWBContext.getCurrentTid());
			if(r!=null){
				sr = createShareResource(rid, request);
			}
		}
		if(isCreateFetchCode && StringUtils.isEmpty(sr.getPassword())){
			sr.setLastEditor(VWBSession.getCurrentUid(request));
			sr.setLastEditTime(new Date());
			sr.setPassword(IdentifyingCode.getLowCaseRandomCode(6));
			shareResourceService.update(sr);
		}
		obj.put("fetchCode", sr.getPassword());
		obj.put("url", sr.generateShareUrl(urlGenerator));
		return obj;
	}
	
	/**
	 * 返回已分享列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params="func=list")
	public void shareList(HttpServletRequest request, HttpServletResponse response){
		int tid = VWBContext.getCurrentTid();
		List<ShareResource> srList = shareResourceService.queryByTid(tid);
		setShareExt(srList, tid);
		Map<String,Object> model = new HashMap<String, Object>();
		model.put("list", srList);
		JsonUtil.writeJSONObject(response, JsonUtil.getJSONObject(model));
	}
	
	/**
	 * 删除分享
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params="func=delete")
	@SuppressWarnings("unchecked")
	public JSONObject deleteShareResource(HttpServletRequest request){
		String[] rs = request.getParameterValues("rids[]");
		int[] rids = new int[rs.length];
		for(int i = 0;i<rs.length;i++){
			rids[i] = Integer.parseInt(rs[i]);
		}
		for(int rid:rids){
			shareResourceService.delete(rid);
		}
		JSONObject obj = new JSONObject();
		obj.put("success", true);
		return obj;
	}
	
	@ResponseBody
	@RequestMapping(params="func=deleteFetchCode")
	@SuppressWarnings("unchecked")
	public JSONObject deleteFetchCode(@RequestParam("rid")int rid,HttpServletRequest request){
		ShareResource sr = shareResourceService.get(rid);
		if(sr!=null){
			sr.setPassword(null);
			shareResourceService.update(sr);
		}
		JSONObject obj = new JSONObject();
		obj.put("success", Boolean.TRUE);
		obj.put("url", sr.generateShareUrl(urlGenerator));
		return obj;
	}
	
	private ShareResource createShareResource(int rid, HttpServletRequest request) {
		ShareResource sr;
		sr = new ShareResource();
		sr.setRid(rid);
		sr.setTid(VWBContext.getCurrentTid());
		sr.setShareUid(VWBSession.getCurrentUid(request));
		sr.setCreateTime(new Date());
		sr.setLastEditor(VWBSession.getCurrentUid(request));
		sr.setLastEditTime(new Date());
		shareResourceService.add(sr);
		return sr;
	}

	private List<ShareResource> setShareExt(List<ShareResource> srList, int tid) {
		Map<Integer,Resource> resourceMap = getResourceMap(tid);
		for(ShareResource item : srList){
			Resource res = resourceMap.get(item.getRid());
			item.setValid((res==null || res.isDelete()) ? false : true);//是否有效
			item.setTitle(res.getTitle());
			item.setItemType(res.getItemType());
			item.setFileType(res.getFileType());
			item.setSize(res.getSize());
			item.setLastVersion(res.getLastVersion());
			item.setFolder(res.isFolder());
			//用户名
			SimpleUser user = aoneUserService.getSimpleUserByUid(item.getShareUid());
			item.setShareUserName(user.getName());
			
			//分享链接
			item.setShareUrl(item.generateShareUrl(urlGenerator));
		}
		return srList;
	}
	
	/**
	 * 获取以已分享的资源map
	 * @param tid
	 * @return
	 */
	private Map<Integer,Resource> getResourceMap(int tid){
		List<Resource> resList = shareResourceService.queryTeamShareResource(tid);
		Map<Integer,Resource> resMap = new HashMap<Integer,Resource>();
		for(Resource item : resList){
			resMap.put(item.getRid(), item);
		}
		return resMap;
	}
}
