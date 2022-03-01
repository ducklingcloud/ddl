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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.team.TeamSpaceApplication;
import net.duckling.ddl.service.team.TeamSpaceApplicationService;
import net.duckling.ddl.service.team.TeamSpaceConfig;
import net.duckling.ddl.service.team.TeamSpaceConfigService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.AbstractSpaceController;
import net.duckling.ddl.web.bean.AdminHelper;
@Controller
@RequestMapping("/system/teamSpaceConfig")
public class TeamSpaceConfigController extends AbstractSpaceController{
    @Autowired
    private TeamSpaceConfigService teamSpaceConfigService;
    @Autowired
    private TeamSpaceApplicationService teamSpaceApplicationService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private DucklingProperties properties;
    @RequestMapping
    public ModelAndView teamSpaceConfig(HttpServletRequest request){
        if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
            throw new RuntimeException(VWBSession.getCurrentUid(request)+"用户类型不正确");
        }
        VWBContext context = VWBContext.createContext(request,UrlPatterns.ADMIN);
        List<TeamSpaceConfig> config = teamSpaceConfigService.getAllTeamSpaceConfig();
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/team/teamSpaceConfig.jsp");
        mv.addObject("configs", config);
        Map<Integer,Team> map = new HashMap<Integer,Team>();
        if(config!=null){
            for(TeamSpaceConfig c : config){
                Team t = teamService.getTeamByID(c.getTid());
                map.put(t.getId(), t);
            }
            mv.addObject("teamMap", map);
        }
        return mv;
    }

    @RequestMapping(params="func=queryTeam")
    public void queryTeam(HttpServletRequest request,HttpServletResponse response){
        if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
            JsonObject obj = new JsonObject();
            obj.addProperty("result", false);
            obj.addProperty("message", "没有权限");
            JsonUtil.write(response, obj);
        }
        String queryType = request.getParameter("queryType");
        String queryWord = request.getParameter("queryWord");
        List<Team> teams = null;
        if("teamCode".equals(queryType)){
            teams = teamService.queryByTeamCode(queryWord);
        }else if("teamName".equals(queryType)){
            teams = teamService.queryByTeamName(queryWord);
        }
        JsonArray config = new JsonArray();
        if(teams!=null){
            for(Team t : teams){
                JsonObject o = new JsonObject();
                o.addProperty("tid", t.getId());
                o.addProperty("teamCode", t.getName());
                o.addProperty("teamName", t.getDisplayName());
                TeamSpaceConfig c = teamSpaceConfigService.getTeamSpaceConfig(t.getId());
                o.addProperty("size", FileSizeUtils.getFileSize(c.getSize()));
                if(c.getId()==0){
                    o.addProperty("type", "default");
                }else{
                    o.addProperty("type", c.getUpdateUid()+"在"+getTime(c.getUpdateTime())+"设置");
                }
                config.add(o);
            }
        }
        JsonObject o = new JsonObject();
        o.add("config", config);
        o.addProperty("result", true);
        JsonUtil.write(response, o);
    }

    private String getTime(Date d){
        if(d==null){
            return "-";
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(d);
        }
    }

    @RequestMapping(params="func=configTeamSize")
    public void configTeamSize(HttpServletRequest request,HttpServletResponse response){
        JsonObject o = new JsonObject();
        if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
            o.addProperty("result", false);
            o.addProperty("message", "没有权限");
            JsonUtil.write(response, o);
        }
        String uid = VWBSession.getCurrentUid(request);
        int tid = Integer.parseInt(request.getParameter("tid"));
        long size = Integer.parseInt(request.getParameter("size"))*FileSizeUtils.ONE_GB;
        long orginalSize = 0;
        String description = request.getParameter("description");
        TeamSpaceConfig config = teamSpaceConfigService.getTeamSpaceConfig(tid);
        if(config.getId()>0){
            orginalSize = config.getSize();
            config.setSize(size);
            config.setUpdateTime(new Date());
            config.setUpdateUid(uid);
            config.setDescription(description);
            teamSpaceConfigService.update(config);
        }else{
            orginalSize = Long.parseLong(properties.getProperty("duckling.team.default.size"));
            config = new TeamSpaceConfig();
            config.setTid(tid);
            config.setSize(size);
            config.setUpdateTime(new Date());
            config.setUpdateUid(uid);
            config.setDescription(description);
            teamSpaceConfigService.insert(config);
        }
        addUpdateRecord(tid, uid, orginalSize, size);
        o.addProperty("result", true);
        JsonUtil.write(response, o);
    }

    private void addUpdateRecord(int tid,String uid,long originalSize,long newSize){
        teamSpaceApplicationService.add(newSize, originalSize, uid, tid, TeamSpaceApplication.TYPE_ADMIN);
    }

    @RequestMapping(params="func=queryTeamConfig")
    public void queryTeamConfig(HttpServletRequest request,HttpServletResponse response){
        JsonObject o = new JsonObject();
        if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
            o.addProperty("result", false);
            o.addProperty("message", "没有权限");
            JsonUtil.write(response, o);
        }
        int tid = Integer.parseInt(request.getParameter("tid"));
        TeamSpaceConfig config = teamSpaceConfigService.getTeamSpaceConfig(tid);
        Team t = teamService.getTeamByID(tid);
        o.addProperty("result", true);
        o.addProperty("tid", tid);
        o.addProperty("teamCode", t.getName());
        o.addProperty("teamName", t.getDisplayName());
        o.addProperty("size", config.getSize()/FileSizeUtils.ONE_GB);
        o.addProperty("description", config.getDescription());
        JsonUtil.write(response, o);
    }
    @RequestMapping(params="func=deleteConfig")
    public void deleteConfig(HttpServletRequest request,HttpServletResponse response){
        JsonObject o = new JsonObject();
        if(!AdminHelper.validateUser(VWBSession.getCurrentUid(request))){
            o.addProperty("result", false);
            o.addProperty("message", "没有权限");
            JsonUtil.write(response, o);
        }
        int id = Integer.parseInt(request.getParameter("id"));
        teamSpaceConfigService.delete(id);
        o.addProperty("result", true);
        JsonUtil.write(response, o);
    }

}
