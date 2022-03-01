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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserSortPreference;
import net.duckling.ddl.service.user.impl.UserSortPreferenceService;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoException.AccessDenied;
import com.meepotech.sdk.MeePoException.BadRequest;
import com.meepotech.sdk.MeePoException.InvalidAccessToken;
import com.meepotech.sdk.MeePoException.NetworkIO;
import com.meepotech.sdk.MeePoException.NotFound;
import com.meepotech.sdk.MeePoException.OperationNotAllowed;
import com.meepotech.sdk.MeePoException.QuotaOutage;
import com.meepotech.sdk.MeePoException.RetryLater;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.PanQueryResult;

@Controller
@RequestMapping("/pan/list")
@RequirePermission(authenticated = true)
public class LynxPanController extends BaseController {
    private static final Logger LOG = Logger.getLogger(LynxPanController.class);

    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private IPanService service;
    @Autowired
    private PanShareResourceService panShareResourceService;
    @Autowired
    private UserSortPreferenceService userSortPreferenceService;
    @Autowired
    private TeamService teamService;
    @RequestMapping
    public ModelAndView display(HttpServletRequest request) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        ModelAndView m = layout(ELayout.LYNX_MAIN, context, "/jsp/pan/pan_list.jsp");
        String baseUrl = request.getContextPath() + "/pan";
        m.addObject("teamUrl", baseUrl + "/list");
        m.addObject("teamHome", baseUrl);
        m.addObject("pageType", "list");
        m.addObject("teamType", "pan");
        int myTid = teamService.getPersonalTeamNoCreate(VWBSession.getCurrentUid(request));
        Team t = teamService.getTeamByID(myTid);
        m.addObject("myTeamCode", t.getName());
        m.addObject("myTeamId", myTid);
        m.addObject("sortType", userSortPreferenceService.getUidSortPreference(context.getCurrentUID(), null, UserSortPreference.WEB_TYPE));
        m.addObject(LynxConstants.TEAM_TITLE,"个人空间（同步版Beta）");
        return m;
    }

    @WebLog(method = "PanQueryList", params = "path,keyWord")
    @RequestMapping(params = "func=query")
    public void queryResource(HttpServletRequest request, HttpServletResponse response) {
        String keyword = request.getParameter("keyWord");
        String uid = VWBSession.getCurrentUid(request);
        if(StringUtils.isNotEmpty(keyword)){
            dealSearch(request,response);
            return;
        }
        JsonObject j = null;
        String path = getRequestPath(request);
        MeePoMeta meta = null;
        try {
            meta = queryMeta(request, path, true);
        } catch (MeePoException e) {
            j = new JsonObject();
            dealMetaException(e, j);
            LOG.error(e.getMessage()+e.getClass(),e);
            JsonUtil.write(response, j);
            return;
        }
        if (meta == null) {
            LOG.error("Path error");
            j =new  JsonObject();
            j.addProperty("success", "false");
            j.addProperty("message", "请求的文件夹不存在或已被删除");
            JsonUtil.write(response, j);
            return;
        }
        SimpleUser user = aoneUserService.getSimpleUserByUid(uid);
        List<PanResourceBean> result = conductShared(uid, adapterMeta(meta,user));

        filterByType(result,request.getParameter("type"));
        List<PanResourceBean> ancestors = getAncestors(meta);
        String tokenKey = request.getParameter("tokenKey");
        String order = getOrder(request);
        PanResourceBeanSort.sort(result, order);
        j = buildQueryResultJson(user, meta, result, ancestors);
        j.addProperty("tokenKey", tokenKey);
        j.addProperty("order", order);
        j.addProperty("showSearch", true);
        JsonUtil.write(response, j);
    }

    /**
     * 设置文件是否已分享
     * @param uid
     * @param resourceList
     * @return
     */
    private List<PanResourceBean> conductShared(String uid, List<PanResourceBean> resourceList){
        Map<String,PanShareResource> sharedMap = new HashMap<String,PanShareResource>();
        List<PanShareResource> list = panShareResourceService.getByUid(uid);
        for(PanShareResource item : list){
            sharedMap.put(item.getSharePath(), item);
        }
        for(PanResourceBean item : resourceList){
            if(sharedMap.containsKey(item.getPath())){
                item.setShared(true);
            }else{
                item.setShared(false);
            }
        }
        return resourceList;
    }

    /**
     *
     * @param request
     * @param response
     */
    private void dealSearch(HttpServletRequest request, HttpServletResponse response) {
        String path = getRequestPath(request);
        String keyword = request.getParameter("keyWord");
        String uid = VWBSession.getCurrentUid(request);
        try {
            MeePoMeta meta = queryMeta(request, path, false);
            PanQueryResult[] result = service.search(PanAclUtil.getInstance(request), path, keyword, 100);
            SimpleUser user = aoneUserService.getSimpleUserByUid(uid);
            List<PanResourceBean> pbs = conductShared(uid, adapterMeta(result,user));
            JsonObject j = new JsonObject();
            j.addProperty("total", pbs.size());
            j.add("currentResource",
                  LynxResourceUtils.getPanResourceJson(
                      MeePoMetaToPanBeanUtil.transfer(meta,user), user.getUid()));
            j.addProperty("nextBeginNum", "0");
            j.add("children",
                  LynxResourceUtils.getPanResourceJSON(pbs, user.getUid()));
            List<PanResourceBean> ancestors = getAncestors(meta);
            j.add("path", LynxResourceUtils.getPanResourceJSON(
                ancestors, user.getUid()));
            j.addProperty("size", pbs.size());
            j.addProperty("type", "search");
            String tokenKey = request.getParameter("tokenKey");
            j.addProperty("tokenKey", tokenKey);
            j.addProperty("order", "");
            j.addProperty("showSearch", true);
            j.addProperty("unshowSort", true);
            j.addProperty("isSearch", true);
            JsonUtil.write(response, j);
        } catch (MeePoException e) {
            JsonObject j = new JsonObject();
            dealMetaException(e, j);
            LOG.error(e.getMessage()+e.getClass(),e);
            JsonUtil.write(response, j);
        }
    }

    private List<PanResourceBean> adapterMeta(PanQueryResult[] result, SimpleUser user) {
        List<PanResourceBean> rs = new ArrayList<PanResourceBean>();
        if(result!=null){
            for(PanQueryResult r : result){
                rs.add(MeePoMetaToPanBeanUtil.transferSearchResoult(r,user));
            }
        }
        return rs;
    }

    private String getOrder(HttpServletRequest request){
        String order=request.getParameter("sortType");
        String uid = VWBSession.getCurrentUid(request);
        order = userSortPreferenceService.getUidSortPreference(uid, order, UserSortPreference.WEB_TYPE);
        return StringUtils.isEmpty(order)?"timeDesc":order;
    }
    private void filterByType(List<PanResourceBean> result, String type) {
        if("Picture".equals(type)){
            Iterator<PanResourceBean> it = result.iterator();
            while(it.hasNext()){
                PanResourceBean bean = it.next();
                if(!FileTypeUtils.isClbDealImage(bean.getTitle())){
                    it.remove();
                }
            }
        }
    }

    private List<PanResourceBean> getAncestors(MeePoMeta meta) {
        List<PanResourceBean> rs = new ArrayList<PanResourceBean>();
        String path = meta.restorePath;
        if (path != null) {
            String[] ps = path.split("/");
            StringBuilder tem = new StringBuilder();
            for (String p : ps) {
                if (StringUtils.isEmpty(p)) {
                    tem.append("/");
                    continue;
                }
                PanResourceBean bean = new PanResourceBean();
                tem.append(p);
                String rid = null;
                try {
                    rid = URLEncoder.encode(tem.toString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                }
                bean.setRid(rid);
                bean.setTitle(p);
                tem.append("/");
                rs.add(bean);
            }
        }
        return rs;
    }

    private List<PanResourceBean> adapterMeta(MeePoMeta meta,SimpleUser user) {
        List<PanResourceBean> result = new ArrayList<PanResourceBean>();
        if (meta == null || meta.contents == null || meta.contents.length == 0) {
            return result;
        }
        MeePoMeta[] ms = meta.contents;
        for (MeePoMeta m : ms) {
            result.add(MeePoMetaToPanBeanUtil.transfer(m,user));
        }
        return result;
    }

    private void dealMetaException(MeePoException e,JsonObject obj){
        obj.addProperty("success", "false");
        if(e instanceof NetworkIO||e instanceof RetryLater){
            obj.addProperty("message", "网络忙，请稍后再试！");
        }else if( e instanceof InvalidAccessToken){
            obj.addProperty("message", "您的登录出现异常，请重新登录！");
        }else if( e instanceof BadRequest){
            obj.addProperty("message", "服务器异常，请稍后再试！");
        }else if(e instanceof QuotaOutage){
            obj.addProperty("message", "空间已满，请核对后再试！");
        }else if(e instanceof OperationNotAllowed){
            obj.addProperty("message", "权限不足，请核对后再试！");
        }else if(e instanceof AccessDenied){
            obj.addProperty("message", "网络忙，请稍后再试！");
        }else if(e instanceof NotFound){
            obj.addProperty("message", "文件没找到，请核对后重试！");
        }else{
            obj.addProperty("message", "服务器异常，请稍后再试！");
        }
    }


    private MeePoMeta queryMeta(HttpServletRequest request, String path, boolean list) throws MeePoException {
        return service.ls(PanAclUtil.getInstance(request), path, list);
    }


    private String getRequestPath(HttpServletRequest request) {
        String path = request.getParameter("path");
        if (StringUtils.isEmpty(path)) {
            path = "/";
        } else {
            try {
                path = URLDecoder.decode(URLEncoder.encode(path, "UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    private JsonObject buildQueryResultJson( SimpleUser user, MeePoMeta meta, List<PanResourceBean> resources,
                                             List<PanResourceBean> ancestors) {
        if (resources == null) {
            resources = new ArrayList<PanResourceBean>();
        }
        JsonObject j = new JsonObject();
        j.addProperty("total", resources.size());
        j.add("currentResource",
              LynxResourceUtils.getPanResourceJson(
                  MeePoMetaToPanBeanUtil.transfer(meta,user), user.getUid()));
        j.addProperty("nextBeginNum", "0");
        j.add("children",
              LynxResourceUtils.getPanResourceJSON(resources, user.getUid()));
        j.add("path", LynxResourceUtils.getPanResourceJSON(
            ancestors, user.getUid()));
        j.addProperty("size", resources.size());
        return j;
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    @WebLog(method = "PanEditFileName", params = "rid,fileName")
    @RequestMapping(params = "func=editFileName")
    public void editeFileName(HttpServletRequest request, HttpServletResponse response) {
        String rid = decode(request.getParameter("rid"));
        String fileName = request.getParameter("fileName");
        String uid = VWBSession.getCurrentUid(request);
        String newRid = getNewPath(rid, fileName);
        JsonObject o = new JsonObject();
        boolean result = false;
        String message = null;
        PanAcl acl = PanAclUtil.getInstance(request);
        try {
            result = service.rename(acl, rid, newRid);
            if (!result) {
                message = "文件名已经存在";
            }
        } catch (MeePoException e) {
            JsonObject j = new JsonObject();
            dealMetaException(e, j);
            LOG.error(e.getMessage()+e.getClass(),e);
            JsonUtil.write(response, j);
            return;
        }
        if (result) {
            try {
                MeePoMeta meta = service.ls(acl, newRid, false);
                SimpleUser user = aoneUserService.getSimpleUserByUid(
                    VWBSession.getCurrentUid(request));
                String isSearchResultEditor = request.getParameter("isSeachResult");
                PanResourceBean bean = MeePoMetaToPanBeanUtil.transfer(meta,user);
                if(StringUtils.isNotEmpty(isSearchResultEditor)){
                    bean.setBeanType(PanResourceBean.BEAN_TYPE_SEARCH);
                }
                o.add("resource", LynxResourceUtils.getPanResourceJson(bean, uid));
            } catch (MeePoException e) {
            }
            LOG.info(uid +" rename " +rid+ " to "+ fileName);
        }
        o.addProperty("result", result);
        o.addProperty("message", message);
        JsonUtil.write(response, o);
    }

    private String getNewPath(String oldPath, String fileName) {
        if (StringUtils.isEmpty(oldPath) || "/".equals(oldPath)) {
            return "/" + fileName;
        } else {
            int index = oldPath.lastIndexOf("/");
            if (index == -1) {
                return "/" + fileName;
            }
            if (index == oldPath.length() - 1) {
                return oldPath + fileName;
            }
            return oldPath.substring(0, index) + "/" + fileName;
        }
    }

    @WebLog(method = "PanCreateFolder", params = "parentRid,fileName")
    @RequestMapping(params = "func=createFolder")
    public void createFolder(HttpServletRequest request, HttpServletResponse response) {
        String uid = VWBSession.getCurrentUid(request);
        String parentRid = decode(request.getParameter("parentRid"));
        if("-1".equals(parentRid)||"0".equals(parentRid)){
            parentRid="/";
        }
        if(StringUtil.illCharCheck(request, response, "fileName")){
            return;
        }
        String fileName = "/" + request.getParameter("fileName");
        if (!StringUtils.isEmpty(parentRid)) {
            fileName = parentRid + fileName;
        }
        PanAcl acl = PanAclUtil.getInstance(request);
        JsonObject o = new JsonObject();

        boolean result = false;
        String message = null;
        try {
            result = service.mkdir(acl, fileName);
            if (!result) {
                message = "文件夹名称已存在";
                o.addProperty("errorCode", "errorName");
            }
        } catch (MeePoException e) {
            JsonObject j = new JsonObject();
            dealMetaException(e, j);
            LOG.error(e.getMessage()+e.getClass(),e);
            JsonUtil.write(response, j);
            return;
        }
        if (result) {
            try {
                MeePoMeta meta = service.ls(acl, fileName, false);
                SimpleUser user = aoneUserService.getSimpleUserByUid(
                    VWBSession.getCurrentUid(request));
                PanResourceBean bean = MeePoMetaToPanBeanUtil.transfer(meta,user);
                String isSearchResultEditor = request.getParameter("isSeachResult");
                if(StringUtils.isNotEmpty(isSearchResultEditor)){
                    bean.setBeanType(PanResourceBean.BEAN_TYPE_SEARCH);
                }
                o.add("resource",
                      LynxResourceUtils.getPanResourceJson(bean, uid));
            } catch (MeePoException e) {
                JsonObject j = new JsonObject();
                dealMetaException(e, j);
                LOG.error(e.getMessage()+e.getClass(),e);
                JsonUtil.write(response, j);
                return;
            }
            LOG.info(uid+" create folder "+fileName);
        }
        o.addProperty("result", result);
        o.addProperty("message", message);
        JsonUtil.write(response, o);
    }

    @WebLog(method = "PandeleteResource", params = "rid")
    @RequestMapping(params = "func=deleteResource")
    public void deleteResource(HttpServletRequest request, HttpServletResponse response) {
        String rid = decode(request.getParameter("rid"));
        JsonObject o = new JsonObject();
        boolean result = false;
        String message = null;
        PanAcl acl = PanAclUtil.getInstance(request);
        try {
            result = service.rm(acl, rid);
        } catch (MeePoException e) {
            JsonObject j = new JsonObject();
            dealMetaException(e, j);
            LOG.error(e.getMessage()+e.getClass(),e);
            JsonUtil.write(response, j);
            return;
        }
        if(result){
            LOG.info(acl.getUid()+" delete "+rid);
        }
        o.addProperty("result", result);
        o.addProperty("message", message);
        JsonUtil.write(response, o);
    }

    @WebLog(method = "PandeleteResources", params = "rids[]")
    @RequestMapping(params="func=deleteResources")
    public void deleteResources(HttpServletRequest request,HttpServletResponse response){
        String[] ridStrs = request.getParameterValues("rids[]");
        List<String> rids = new ArrayList<String>();
        for(String r : ridStrs){
            rids.add(decode(r));
        }
        List<String> errorList = new ArrayList<String>();
        List<String> successList = new ArrayList<>();
        PanAcl acl = PanAclUtil.getInstance(request);
        for (String r : rids) {
            try {
                boolean result = service.rm(acl, r);
                if (!result) {
                    errorList.add(encode(r));
                }else{
                    successList.add(encode(r));
                }
            } catch (MeePoException e) {
                LOG.error("", e);
                errorList.add(encode(r));
            }
        }
        JsonObject o = new JsonObject();
        o.addProperty("result", errorList.isEmpty());
        if(!errorList.isEmpty()){
            JsonArray array = new JsonArray();
            for(String s : errorList){
                array.add(s);
            }
            o.add("errorRids", array);
            JsonArray suc = new JsonArray();
            for(String s : successList){
                suc.add(s);
            }
            o.add("sucRids", suc);
        }
        LOG.info(acl.getUid()+" delete "+successList+" ;delete error"+errorList);
        JsonUtil.write(response, o);
    }

    private String encode(String s){
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

}
