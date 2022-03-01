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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.ResourceUtils;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cnic.cerc.dlog.client.WebLog;


@Controller
@RequestMapping("/system/bookmark")
public class LynxBookmarkController {
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private TeamService teamService;
    private static final Logger LOG = Logger.getLogger(LynxBookmarkController.class);

    @RequestMapping(params="func=newPage")
    @WebLog(method = "bookmarkNewPage")
    public void createNewPage(HttpServletRequest request, HttpServletResponse response){
        String title=request.getParameter("title");
        String content=request.getParameter("content");
        String tidStr=request.getParameter("tid");
        if(null==title || null==content || "".equals(title) || "".equals(content)
           ||null==tidStr||"".equals(tidStr)){
            LOG.info("newPage failed:title=null/\"\" | content=null");
            String returnStr="<script>alert('保存失败！title或content为空！');</script>";
            JsonUtil.write(response, returnStr);
            return;
        }
        VWBSession session = VWBSession.findSession(request);
        if(null==session||!session.isAuthenticated()){
            LOG.info("newPage failed: session=null or user is guest!");
            String returnStr="<script>alert('保存失败！会话已过期，请重新登录！');</script>";
            JsonUtil.write(response, returnStr);
            return;
        }
        int tid = Integer.parseInt(tidStr);
        VWBContext context=VWBContext.createContext(tid, request, UrlPatterns.BOOKMARK, null);
        title = folderPathService.getResourceName(tid,0,LynxConstants.TYPE_PAGE,title);
        Resource resource = ResourceUtils.createDDoc(tid, 0, title, context.getCurrentUID());
        resourceOperateService.addResource(resource);
        resourceOperateService.createPageVersion(resource, content);
        LOG.info("newPage success: title="+title);
        String returnStr="<script>alert('保存成功！下次访问科研在线时即可查看更新！');</script>";
        JsonUtil.write(response, returnStr);
    }

    @RequestMapping(params="func=getPermission")
    @RequirePermission( authenticated = true)
    public void getPermission(HttpServletResponse response){
        String message="<html>" +
                "<head><title>登录成功！</title></head>"+
                "<body><h1>Success!</h1><p>登录科研在线成功！请继续使用协同数据采集器的功能！</p></body>" +
                "</html>";
        response.setContentType("text/html; charset=utf-8");
        ServletOutputStream os;
        try {
            os = response.getOutputStream();
            os.write(message.getBytes());
        } catch (IOException e) {
            LOG.info("IOException while response to /system/bookmark?func=getPermission!");
            LOG.error(e);
        }
    }

    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response){
        StringBuilder sb=new StringBuilder();
        VWBContext context=VWBContext.createContext(request, UrlPatterns.BOOKMARK);
        VWBSession session = VWBSession.findSession(request);
        if(null==session||!session.isAuthenticated()){
            sb.append("var logstatus=false;");
            JsonUtil.write(response, sb);
            return ;
        }
        String uid = context.getCurrentUID();
        if(null==uid || "".equals(uid)||"guest".equals(uid.toLowerCase())){
            sb.append("var logstatus=false;");
            JsonUtil.write(response, sb);
            return ;
        }
        sb.append("var logstatus=true;");
        getTeamAndDCollections(response,context,sb);
    }

    private void getTeamAndDCollections(HttpServletResponse response, VWBContext context,
                                        StringBuilder sb){
        String uid = context.getCurrentUID();
        List<Team> allTeams = teamService.getAllUserTeams(uid);
        Iterator<Team> teamItr = allTeams.iterator();
        JsonArray teamArray = new JsonArray();
        while(teamItr.hasNext()){
            Team team = teamItr.next();
            JsonObject tempTeam = new JsonObject();
            tempTeam.addProperty("tid", team.getId());
            tempTeam.addProperty("tname", team.getDisplayName());
            teamArray.add(tempTeam);
        }
        sb.append("var teamColls="+teamArray.toString()+";");
        JsonUtil.write(response, sb);
    }
}
