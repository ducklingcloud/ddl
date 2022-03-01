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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.controller.pan.MeePoMetaToPanBeanUtil;
import net.duckling.ddl.web.controller.pan.PanResourceBean;
import net.duckling.ddl.web.controller.pan.PanResourceBeanSort;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequestMapping("/api/pan/list")
@RequirePermission(authenticated=true)
public class APIPanListController {
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private IPanService service;

    /**
     * 返回记录格式
     */
    public final static String RECORD_FORMAT="recordFormat";
    public final static String RECORD_FORMAT_LITE="lite"; //简版

    @RequestMapping
    public void list(HttpServletRequest request,HttpServletResponse response){
        //如果不在支持meePo盘那么开启下面注销
        //      JsonObject obj = new JsonObject();
        //      obj.put("success", false);
        //      obj.put("message", "此版本不再支持个人同步盘，请下载新的版本");
        //      JSONHelper.writeJSONObject(response, obj);return;

        String path = getRequestRid(request);
        String recordFormat = request.getParameter(RECORD_FORMAT);

        MeePoMeta meta = null;
        try {
            meta = queryMeta(request, path, true);
        } catch (MeePoException e) {
            e.printStackTrace();
        }
        SimpleUser user = aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request));
        List<PanResourceBean> result = adapterMeta(meta,user);
        filterByType(result,request.getParameter("type"));
        List<PanResourceBean> ancestors = getAncestors(meta);
        String tokenKey = request.getParameter("tokenKey");
        PanResourceBeanSort.sort(result, request.getParameter("sortType"));

        JsonObject j = null;
        if(RECORD_FORMAT_LITE.equals(recordFormat)){
            j = buildQueryResultLite(user, meta, result, ancestors);
        }else{
            j = buildQueryResult(user, meta, result, ancestors);
        }
        j.addProperty("tokenKey", tokenKey);

        JsonUtil.write(response, j);
    }

    @SuppressWarnings("unchecked")
    private JsonObject buildQueryResult(SimpleUser user, MeePoMeta meta, List<PanResourceBean> resources,
                                        List<PanResourceBean> ancestors) {
        JsonObject j = buildQueryResultCommon(user, meta, resources, ancestors);
        j.add("children", LynxResourceUtils.getPanResourceList(resources, user.getUid()));
        return j;
    }

    @SuppressWarnings("unchecked")
    private JsonObject buildQueryResultLite( SimpleUser user, MeePoMeta meta, List<PanResourceBean> resources,
                                             List<PanResourceBean> ancestors) {
        JsonObject j = buildQueryResultCommon(user, meta, resources, ancestors);
        j.add("children", LynxResourceUtils.getPanResourceListLite(resources, user.getUid()));
        return j;
    }

    @SuppressWarnings("unchecked")
    private JsonObject buildQueryResultCommon( SimpleUser user, MeePoMeta meta, List<PanResourceBean> resources,
                                               List<PanResourceBean> ancestors) {
        if (resources == null) {
            resources = new ArrayList<PanResourceBean>();
        }
        JsonObject j = new JsonObject();
        j.addProperty("total", resources.size());
        j.add("currentResource", LynxResourceUtils.getPanResource(user.getUid(), MeePoMetaToPanBeanUtil.transfer(meta,user)));
        j.addProperty("nextBeginNum", "0");
        j.add("path", LynxResourceUtils.getPanResourceList(ancestors, user.getUid()));
        j.addProperty("size", resources.size());
        return j;
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
    private MeePoMeta queryMeta(HttpServletRequest request, String path, boolean list) throws MeePoException {
        return service.ls(PanAclUtil.getInstance(request), path, list);
    }

    private String getRequestRid(HttpServletRequest request){
        String tagAll = request.getParameter("tagFilter");
        if("all".equals(tagAll)){
            return "/";
        }
        String rid = request.getParameter("rid");
        if(StringUtils.isEmpty(rid)){
            String path = request.getParameter("path");
            rid=decode(path);
        }else{
            rid=decode(rid);
        }
        if(StringUtils.isEmpty(rid)){
            return "/";
        }
        return rid;
    }

    private String decode(String rid){
        try {
            return URLDecoder.decode(rid, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return "";
    }
    @RequestMapping(params="func=getChildrenFolder")
    public void getFolder(HttpServletRequest request,HttpServletResponse response){
        String rid = decode(request.getParameter("rid"));
        if("0".equals(rid)){
            rid="/";
        }
        MeePoMeta meta = null;
        try {
            meta = queryMeta(request, rid, true);
        } catch (MeePoException e) {
            e.printStackTrace();
        }
        List<PanResourceBean> childrenList = getChildren(meta, aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request)));
        SimpleUser user = aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request));
        JsonArray result = LynxResourceUtils.getPanResourceList(childrenList,user.getUid());
        JsonObject o = new JsonObject();
        o.add("childrenFolder", result);
        o.addProperty("total", childrenList.size());
        JsonUtil.write(response, o);
    }

    private List<PanResourceBean> getChildren(MeePoMeta root ,SimpleUser user){
        List<PanResourceBean> result = new ArrayList<PanResourceBean>();
        if(root==null||root.contents==null){
            return result;
        }
        MeePoMeta[] ms = root.contents;
        for(MeePoMeta m : ms){
            result.add(MeePoMetaToPanBeanUtil.transfer(m, user));
        }
        filterFolder(result);
        PanResourceBeanSort.sort(result, PanResourceBeanSort.TYPE_TIME_DESC);
        return result;
    }

    private void filterFolder(List<PanResourceBean> beans){
        Iterator<PanResourceBean> it = beans.iterator();
        while(it.hasNext()){
            PanResourceBean bean = it.next();
            if(!LynxConstants.TYPE_FOLDER.equals(bean.getItemType())){
                it.remove();
            }
        }
    }
}
