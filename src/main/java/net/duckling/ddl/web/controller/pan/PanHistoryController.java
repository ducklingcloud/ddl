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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

//import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.MeePoRevision;

@Controller
@RequirePermission(authenticated = true)
@RequestMapping("/pan/history")
public class PanHistoryController extends BaseController{
    private static final Logger LOGGER = Logger.getLogger(PanHistoryController.class);
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private IPanService panService;
    //@WebLog(method = "PanHistory", params = "path")
    @RequestMapping
    public ModelAndView display(HttpServletRequest request,HttpServletResponse response){
        String path = request.getParameter("path");
        //      String version = request.getParameter("version");
        List<PanResourceBean> result = new ArrayList<PanResourceBean>();
        PanResourceBean current = null;
        try {
            PanAcl acl = PanAclUtil.getInstance(request);
            MeePoMeta currentMeta = panService.ls(acl, path, false);
            SimpleUser user = aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request));
            current = MeePoMetaToPanBeanUtil.transfer(currentMeta,user);
            current.setVersion(1);
            MeePoRevision[] vers = panService.revisions(acl, path);
            if(vers!=null){
                int length = vers.length+1;
                current.setVersion(length-1);
                for(MeePoRevision re : vers){
                    PanResourceBean ver = MeePoMetaToPanBeanUtil.transfer(currentMeta, re,user);
                    ver.setVersion(--length);
                    result.add(ver);
                }
            }
        } catch (MeePoException e) {
            LOGGER.error("", e);
        }
        VWBContext context = VWBContext.createContext(request,UrlPatterns.T_TEAM_HOME);
        ModelAndView mv = layout(ELayout.LYNX_INFO, context,"/jsp/pan/pan_history_version.jsp");
        mv.addObject("history", result);
        mv.addObject("users", getEditorName(result));
        mv.addObject("oneVersion", result.size()==1);
        mv.addObject("currentRe", current);
        mv.addObject("teamType", "pan");
        return mv;
    }

    private Object getEditorName(List<PanResourceBean> beans) {
        Set<String> uids = new HashSet<String>();
        for(PanResourceBean fv : beans){
            uids.add(fv.getLastEditor());
        }
        List<UserExt> us = aoneUserService.getUserExtByUids(uids);
        Map<String,String> result = new HashMap<String,String>();
        for(UserExt ue : us){
            result.put(ue.getUid(), ue.getName());
        }
        return result;
    }
    //@WebLog(method = "PanRollback", params = "path,version")
    @RequestMapping(params="func=rollback")
    public void rollback(HttpServletRequest request,HttpServletResponse response,@RequestParam("path")String path,@RequestParam("version")int version) throws IOException{
        PanAcl acl = PanAclUtil.getInstance(request);
        try {
            MeePoMeta result = panService.rollback(acl, path, version);
            String url = urlGenerator.getAbsoluteURL(UrlPatterns.PAN_PREVIEW, URLEncoder.encode(result.restorePath, "utf-8"),null);
            response.sendRedirect(url);
        } catch (MeePoException e) {
            LOGGER.error("", e);
            throw new RuntimeException("无权限执行恢复操作");
        } catch (UnsupportedEncodingException e) {
        }
    }


}
