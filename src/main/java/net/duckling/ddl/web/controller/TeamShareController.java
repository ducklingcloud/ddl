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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.IdentifyingCode;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequirePermission(target = "team", operation = "view")
@RequestMapping("{teamCode}/shareResource")
public class TeamShareController {
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

    @ResponseBody
    @RequestMapping(params="func=getFetchCode")
    public JsonObject getResourceShareCode(@RequestParam("rid")int rid,HttpServletRequest request){
        ShareResource sr = shareResourceService.get(rid);
        JsonObject obj = new JsonObject();
        if(sr==null){
            sr = createShareResource(rid, request);
        }else if(sr!=null&&StringUtils.isEmpty(sr.getPassword())){
            sr.setLastEditor(VWBSession.getCurrentUid(request));
            sr.setLastEditTime(new Date());
            sr.setPassword(IdentifyingCode.getLowCaseRandomCode(6));
            shareResourceService.update(sr);
        }
        obj.addProperty("fetchCode", sr.getPassword());
        obj.addProperty("url", urlGenerator.getAbsoluteURL(UrlPatterns.RESOURCE_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(rid));
        return obj;
    }
    @ResponseBody
    @RequestMapping(params="func=deleteFetchCode")
    public JsonObject deleteFetchCode(@RequestParam("rid")int rid,HttpServletRequest request){
        ShareResource sr = shareResourceService.get(rid);
        if(sr!=null){
            sr.setPassword(null);
            shareResourceService.update(sr);
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        obj.addProperty("url", urlGenerator.getAbsoluteURL(UrlPatterns.RESOURCE_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(rid));
        return obj;
    }

    public ShareResource createShareResource(int rid, HttpServletRequest request) {
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

    @ResponseBody
    @RequestMapping(params="func=getUserStatus")
    public JsonObject getUserStatus(HttpServletRequest request){
        UserExt ext = aoneUserService.getUserExtInfo(VWBSession.getCurrentUid(request));
        JsonObject obj = new JsonObject();
        if(StringUtils.isEmpty(ext.getConfirmStatus())){
            obj.addProperty("status", "false");
        }else if(ext.isConfStatusAvailable()){
            obj.addProperty("status", "true");
            int rid = Integer.parseInt(request.getParameter("rid"));
            setShareUrl(request, obj, rid);
        }else{
            obj.addProperty("status", "forbidden");
        }
        obj.addProperty("userName", VWBSession.getCurrentUidName(request));
        return obj;
    }

    @ResponseBody
    @RequestMapping(params="func=updateUserStatus")
    public JsonObject updateUserStatus(HttpServletRequest request){
        UserExt ext = aoneUserService.getUserExtInfo(VWBSession.getCurrentUid(request));
        ext.setConfirmStatus(UserExt.CONF_STATUS_AVA);
        aoneUserService.modifyUserProfile(ext);
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        obj.addProperty("status", "true");
        int rid = Integer.parseInt(request.getParameter("rid"));
        setShareUrl(request, obj, rid);
        return obj;
    }

    public void setShareUrl(HttpServletRequest request, JsonObject obj, int rid) {
        obj.addProperty("url", urlGenerator.getAbsoluteURL(UrlPatterns.RESOURCE_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(rid));
        ShareResource sr = shareResourceService.get(rid);
        if(sr==null){
            sr =createShareResource(rid, request);
        }
        obj.addProperty("fetchCode", sr.getPassword());
    }

    @ResponseBody
    @RequestMapping(params="func=getShareUrl")
    public JsonObject getShareUrl(HttpServletRequest request,@RequestParam("rid")int rid){
        JsonObject obj = new JsonObject();
        setShareUrl(request, obj, rid);
        return obj;
    }

    @ResponseBody
    @RequestMapping(params="func=sendShareResourceEmail")
    public JsonObject sendShareResourceEmail(HttpServletRequest request,@RequestParam("rid")int rid){
        String message = request.getParameter("message");
        String userName = VWBSession.getCurrentUidName(request);
        Resource resource=resourceService.getResource(rid);
        String fileNames=resource.getTitle();
        message = getMessage(rid, userName, fileNames, message);
        String fileURLs = urlGenerator.getAbsoluteURL(UrlPatterns.RESOURCE_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(rid);
        String friendEmails = request.getParameter("targetEmails");
        String[] shareMails = friendEmails.split(",");
        for (int i = 0; i < shareMails.length; i++) {
            aonemailService.sendAccessFileMail(new String[] { fileNames }, new String[] { fileURLs }, userName, shareMails[i],
                                               message);
        }
        JsonObject object = new JsonObject();
        object.addProperty("status", "success");
        object.addProperty("itemType", resource.getItemType());
        object.addProperty("fileURL", fileURLs);
        object.addProperty("friendEmails", friendEmails);
        object.addProperty("fileName", fileNames);
        return object;
    }

    private String getMessage(int rid,String userName,String title,String message){
        ShareResource sr = shareResourceService.get(rid);
        String url = urlGenerator.getAbsoluteURL(UrlPatterns.RESOURCE_SHARE, null, null)+"/"+ShareRidCodeUtil.encode(rid);
        String add = "";
        if(StringUtils.isNotEmpty(sr.getPassword())){
            add+="  提取码："+sr.getPassword();
        }
        String sb = "您的好友"+userName+"和您分享团队文档库的文件：<a href='"+url+"'>"+title+"</a><br/> 提取地址：<a href='"+url+"'>"+url+"</a>"+add+"<br/>";
        sb+=message;
        return sb;
    }
}
