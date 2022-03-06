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

package net.duckling.ddl.web.controller.message;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.AbstractRecommendContrller;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//import cn.cnic.cerc.dlog.client.WebLog;

/**
 * @date 2011-7-8
 * @author Clive Lee
 */

@Controller
@RequestMapping("/{teamCode}/recommend")
@RequirePermission(target = "team", operation = "view")
public class RecommendController extends AbstractRecommendContrller {
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private IResourceService resourceService;
    private String combineRecipients(String[] userIds) {
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
    @SuppressWarnings("unchecked")
    private void getTeamAllUser(VWBContext ctx,HttpServletResponse response){
        List<SimpleUser> candidates = teamMemberService.getTeamMembersOrderByName(ctx.getTid());
        Collections.sort(candidates, comparator);
        JsonArray array = new JsonArray();
        for (SimpleUser current : candidates) {
            JsonObject temp = new JsonObject();
            temp.addProperty("uid", current.getUid());
            temp.addProperty("id", current.getId());
            temp.addProperty("email", current.getEmail());
            temp.addProperty("pinyin", current.getPinyin());
            if (StringUtils.isNotEmpty(current.getName())) {
                temp.addProperty("name", current.getName());
            } else {
                temp.addProperty("name", current.getUid());
            }
            array.add(temp);
        }
        JsonUtil.write(response, array);
    }
    @SuppressWarnings("unchecked")
    private void submitRecommend(EventDispatcher eventDispatcher, HttpServletRequest request, HttpServletResponse response) {
        VWBContext ctx = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String currUser = ctx.getCurrentUID();
        String remark = request.getParameter("remark");
        String sendType = request.getParameter("sendType");
        String[] userIds = request.getParameterValues("users");
        int rid = Integer.parseInt(request.getParameter("rid"));
        Resource res = resourceService.getResource(rid);
        if (res.isFile()) {
            eventDispatcher.sendFileRecommendEvent(ctx.getTid(), rid, res.getTitle(), currUser, res.getLastVersion(),
                                                   remark, combineRecipients(userIds),sendType);
        } else if(res.isPage()){
            eventDispatcher.sendPageRecommendEvent(ctx.getTid(), rid, res.getTitle(), currUser, res.getLastVersion(),
                                                   remark, combineRecipients(userIds),sendType);
        }else if(res.isFolder()){
            eventDispatcher.sendFolderRecommendEvent(ctx.getTid(), rid, res.getTitle(), currUser, res.getLastVersion(),
                                                     remark, combineRecipients(userIds),sendType);
        }

        JsonObject object = new JsonObject();
        object.addProperty("status", "success");
        object.addProperty("itemType", res.getItemType());
        JsonUtil.write(response, object);
    }
    private Resource getSavedViewPort(HttpServletRequest request, int rid,
                                      String itemType) {
        Site site = VWBContext.findSite(request);
        return resourceService.getResource(rid, site.getId());
    }

    private VWBContext getVWBContext(HttpServletRequest request, int rid,
                                     String itemType) {
        Resource res = getSavedViewPort(request, rid, itemType);
        return VWBContext.createContext(request, UrlPatterns.MYSPACE, res);
    }

    @RequestMapping(params = "func=prepareRecommend")
    //@WebLog(method = "prepareRecommend", params = "rid")
    public void prepareRecommend(HttpServletRequest request,
                                 HttpServletResponse response) {
        prepareRecommend(response);
    }

    @RequestMapping(params = "func=getTeamUser")
    public void getTeamUser(HttpServletRequest request,
                            HttpServletResponse response) {
        int rid = Integer.parseInt(request.getParameter("rid"));
        String itemType = request.getParameter("itemType");
        VWBContext context = getVWBContext(request, rid, itemType);
        getTeamAllUser(context, response);
    }

    @RequestMapping(params = "func=addRecommend")
    //@WebLog(method = "addRecommend", params = "rid")
    public void addRecommend(HttpServletRequest request,
                             HttpServletResponse response) {
        submitRecommend(eventDispatcher, request,
                        response);
    }
}
