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
package net.duckling.ddl.web.controller.pan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.IdentifyingCode;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;

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
@RequestMapping("pan/shareResource")
public class PanShareResourceController {

	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private PanShareResourceService panShareResourceService;
	@Autowired
    private IPanService service;
	@Autowired
	private AoneMailService aonemailService;
	
	
	@ResponseBody
	@RequestMapping(params="func=getFetchCode")
	public JSONObject getResourceShareCode(@RequestParam("rid")String rid,HttpServletRequest request){
		rid = decode(rid);
		String uid = VWBSession.getCurrentUid(request);
		PanShareResource sr = panShareResourceService.getByPath(uid, rid);
		JSONObject obj = new JSONObject();
		if(sr==null){
			sr = createShareResource(rid, request);
		}else if(sr!=null&&StringUtils.isEmpty(sr.getPassword())){
			sr.setPassword(IdentifyingCode.getLowCaseRandomCode(6));
			panShareResourceService.update(sr);
		}
		obj.put("fetchCode", sr.getPassword());
		obj.put("url", getShareURL(sr.getId()));
		return obj;
	}
	@ResponseBody
	@RequestMapping(params="func=deleteFetchCode")
	public JSONObject deleteFetchCode(@RequestParam("rid")String rid,HttpServletRequest request){
		String uid = VWBSession.getCurrentUid(request);
		rid = decode(rid);
		PanShareResource sr = panShareResourceService.getByPath(uid, rid);
		if(sr!=null){
			sr.setPassword(null);
			panShareResourceService.update(sr);
		}
		JSONObject obj = new JSONObject();
		obj.put("success", true);
		obj.put("url", getShareURL(sr.getId()));
		return obj;
	}
	
	@ResponseBody
	@RequestMapping(params="func=getUserStatus")
	public JSONObject getUserStatus(HttpServletRequest request){
		UserExt ext = aoneUserService.getUserExtInfo(VWBSession.getCurrentUid(request));
		JSONObject obj = new JSONObject();
		if(StringUtils.isEmpty(ext.getConfirmStatus())){
			obj.put("status", "false");
		}else if(ext.isConfStatusAvailable()){
			obj.put("status", "true");
			String rid = request.getParameter("rid");
			setShareUrl(request, rid, obj);
		}else{
			obj.put("status", "forbidden");
		}
		obj.put("userName", VWBSession.getCurrentUidName(request));
		return obj;
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
	
	
	@ResponseBody
	@RequestMapping(params="func=updateUserStatus")
	public JSONObject updateUserStatus(HttpServletRequest request){
		UserExt ext = aoneUserService.getUserExtInfo(VWBSession.getCurrentUid(request));
		ext.setConfirmStatus(UserExt.CONF_STATUS_AVA);
		aoneUserService.modifyUserProfile(ext);
		JSONObject obj = new JSONObject();
		obj.put("success", true);
		obj.put("status", "true");
		String rid = request.getParameter("rid");
		setShareUrl(request, rid, obj);
		return obj;
	}
	@ResponseBody
	@RequestMapping(params="func=getShareUrl")
	public JSONObject getShareUrl(HttpServletRequest request,@RequestParam("rid")String rid){
		JSONObject obj = new JSONObject();
		setShareUrl(request, rid, obj);
		return obj;
	}
	
	private void setShareUrl(HttpServletRequest request,String rids,JSONObject obj){
		String uid = VWBSession.getCurrentUid(request);
		String rid = decode(rids);
		PanShareResource sr = panShareResourceService.getByPath(uid, rid);
		if(sr==null){
			sr =createShareResource(rid, request);
		}
		obj.put("url", getShareURL(sr.getId()));
		obj.put("fetchCode", sr.getPassword());
	}
	
	@ResponseBody
	@RequestMapping(params="func=sendShareResourceEmail")
	public JSONObject sendShareResourceEmail(HttpServletRequest request,@RequestParam("rid")String rid){
		String message = request.getParameter("message");
		String uid = VWBSession.getCurrentUid(request);
		String userName = VWBSession.getCurrentUidName(request);
		MeePoMeta meta=null;
		rid = decode(rid);
		try {
			meta = service.ls(PanAclUtil.getInstance(request), rid, false);
		} catch (MeePoException e) {
			e.printStackTrace();
		}
		PanShareResource sr = panShareResourceService.getByPath(uid, rid);
        String fileNames=meta.name;
        message = getMessage(sr, userName, fileNames, message);
        String fileURLs = getShareURL(sr.getId());
		String friendEmails = request.getParameter("targetEmails");
        String[] shareMails = friendEmails.split(",");
        for (int i = 0; i < shareMails.length; i++) {
            aonemailService.sendAccessFileMail(new String[] { fileNames }, new String[] { fileURLs }, userName, shareMails[i],
                    message);
        }
        JSONObject object = new JSONObject();
		object.put("status", "success");
		object.put("itemType", getMeepMetaType(meta));
		object.put("fileURL", fileURLs);
		object.put("friendEmails", friendEmails);
		object.put("fileName", fileNames);
		return object;
	}
	
	private String getMeepMetaType(MeePoMeta meta){
		PanResourceBean bean = MeePoMetaToPanBeanUtil.transfer(meta,null);
		return bean.getItemType();
	}
	
	private String getShareURL(int id){
		return urlGenerator.getAbsoluteURL(UrlPatterns.PAN_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(id);
	}
	
	
	private String getMessage(PanShareResource sr,String userName,String title,String message){
		String url = getShareURL(sr.getId());
		String add = "";
		if(StringUtils.isNotEmpty(sr.getPassword())){
			add+="  提取码："+sr.getPassword();
		}
		String sb = "您的好友"+userName+"和您分享团队文档库的文件：<a href='"+url+"'>"+title+"</a><br/> 提取地址<a href='"+url+"'>"+url+"</a>"+add+"<br/>";
		sb+=message;
		return sb;
	}
	
	private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
