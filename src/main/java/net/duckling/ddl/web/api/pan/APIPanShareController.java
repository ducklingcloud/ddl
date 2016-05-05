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
package net.duckling.ddl.web.api.pan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.IdentifyingCode;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.ddl.web.api.pan.bean.PanShareResourceView;
import net.duckling.ddl.web.controller.pan.MeePoMetaToPanBeanUtil;
import net.duckling.ddl.web.controller.pan.PanResourceBean;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.PanShareInfo;

@Controller
@RequirePermission(authenticated=true)
@RequestMapping("/api/pan/shareResource")
public class APIPanShareController {
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
	public JSONObject share(@RequestParam("rid")String rid,
			@RequestParam(value="isCreateFetchCode", required=false) boolean isCreateFetchCode,
			HttpServletRequest request){
		rid = decode(rid);
		String uid = VWBSession.getCurrentUid(request);
		PanShareResource sr = panShareResourceService.getByPath(uid, rid);
		JSONObject obj = new JSONObject();
		if(sr==null){
			sr = createShareResource(rid, request);
		}
		
		if(isCreateFetchCode && sr!=null && StringUtils.isEmpty(sr.getPassword())){
			sr.setPassword(IdentifyingCode.getLowCaseRandomCode(6));
			panShareResourceService.update(sr);
		}
		obj.put("fetchCode", sr.getPassword());
		obj.put("url", generateShareUrl(sr.getId()));
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
		List<PanShareResource> list = panShareResourceService.getByUid(VWBSession.getCurrentUid(request));
		
		Map<String,Object> model = new HashMap<String, Object>();
		model.put("list", transferList(list, PanAclUtil.getInstance(request)));
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
		String[] rs = request.getParameterValues("ids[]");
		int[] ids = new int[rs.length];
		for(int i = 0;i<rs.length;i++){
			ids[i] = Integer.parseInt(rs[i]);
		}
		for(int id:ids){
			panShareResourceService.delete(id);
		}
		JSONObject obj = new JSONObject();
		obj.put("success", true);
		return obj;
	}
	
	@ResponseBody
	@RequestMapping(params="func=deleteFetchCode")
	@SuppressWarnings("unchecked")
	public JSONObject deleteFetchCode(@RequestParam("rid")String rid,HttpServletRequest request){
		String uid = VWBSession.getCurrentUid(request);
		rid = decode(rid);
		PanShareResource sr = panShareResourceService.getByPath(uid, rid);
		if(sr!=null){
			sr.setPassword(null);
			panShareResourceService.update(sr);
		}
		JSONObject obj = new JSONObject();
		obj.put("success", Boolean.TRUE);
		obj.put("url", generateShareUrl(sr.getId()));
		return obj;
	}

	private List<PanShareResourceView> transferList(List<PanShareResource> list,PanAcl acl) {
		List<PanShareResourceView> listWarp = new ArrayList<PanShareResourceView>();
		for(PanShareResource p : list){
			try {
				MeePoMeta me = service.ls(acl, p.getSharePath(), false);
				PanResourceBean bean = (me==null) ? getDeleteBean(p) 
											: MeePoMetaToPanBeanUtil.transfer(me,null);
				
				PanShareResourceView view = new PanShareResourceView(p, bean);
				view.setShareUrl(generateShareUrl(view.getId()));
				SimpleUser user = aoneUserService.getSimpleUserByUid(p.getShareUid());
				view.setShareUserName(user.getName());
				listWarp.add(view);
			} catch (MeePoException e) {
				e.printStackTrace();
			}
		}
		return listWarp;
	}
	
	private PanResourceBean getDeleteBean(PanShareResource p){
		PanResourceBean bean = new PanResourceBean();
		bean.setTitle(getTitleFromPath(p.getSharePath()));
		bean.setPath(null);
		bean.setItemType(LynxConstants.TYPE_FILE);
		int index = p.getSharePath().lastIndexOf(".");
		if(index>=0){
			String pix = p.getSharePath().substring(index+1);
			bean.setFileType(pix.toLowerCase());
		}
		return bean;
	}
	
	private String getTitleFromPath(String sharePath) {
		int index = sharePath.lastIndexOf("/");
		if(index>=0){
			sharePath = sharePath.substring(index+1);
		}
		return sharePath;
	}
	
	
	private PanShareResource createShareResource(String rid, HttpServletRequest request) {
		String uid = VWBSession.getCurrentUid(request);
		PanShareResource ps = panShareResourceService.getAllByPath(uid, rid);
		if(ps!=null){
			if(ps.isDelete()){
				ps.setStatus(LynxConstants.STATUS_AVAILABLE);
				panShareResourceService.update(ps);
			}
			return ps;
		}else{
			try {
			    PanShareInfo info = service.shareFile(PanAclUtil.getInstance(request), rid, null, "3650d");
				ps = new PanShareResource();
				ps.setShareTime(new Date());
				ps.setShareUid(VWBSession.getCurrentUid(request));
				ps.setStatus(LynxConstants.STATUS_AVAILABLE);
				ps.setExpireMillis(1000l*3600*24*365*10);
				ps.setPanShareId(info.getShare_id());
				ps.setSharePath(rid);
				panShareResourceService.add(ps);
				return ps;
			} catch (MeePoException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

	/**
	 * 生成shareUrl
	 * @param urlGenerator
	 * @return
	 */
	private String generateShareUrl(int id) {
		return urlGenerator.getAbsoluteURL(UrlPatterns.PAN_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(id);
	}
	
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	private PanShareResourceService panShareResourceService;
	@Autowired
    private IPanService service;
	@Autowired
	private AoneUserService aoneUserService;
}
