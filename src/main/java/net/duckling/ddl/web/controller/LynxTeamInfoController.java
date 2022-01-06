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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 用于VMT获取ddl team和用户信息
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/system/teaminfo")
public class LynxTeamInfoController {
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AuthorityService authorityService;
    /**
     * 获取所有的team和team admin
     * @param req
     * @param resp
     */
    @RequestMapping(params="func=getAllTeam")
    public void getAllTeam(HttpServletRequest req,HttpServletResponse resp){
        VWBContainer container = VWBContainerImpl.findContainer();
        if(!validateRequest(req,container)){
            resp.setStatus(401);
            return;
        }
        List<Team> teams = teamService.getAllTeams();
        JSONArray teamArray = new JSONArray();
        for(Team team : teams){
            if(Team.PESONAL_TEAM.equals(team.getType())){
                continue;
            }
            teamArray.add(team.getName());
        }
        JsonUtil.writeJSONObject(resp, teamArray);
    }
    /**
     * 获得所有我能看到的团队teamCode
     * @param
     * */
    @RequestMapping(params="func=getMyTemCodes")
    public void getMyTemCodes(HttpServletRequest req,HttpServletResponse resp){
        VWBContainer container = VWBContainerImpl.findContainer();
        if(!validateRequest(req,container)){
            resp.setStatus(401);
            return;
        }
        String uid = req.getParameter("uid");
        if(StringUtils.isEmpty(uid)){
            resp.setStatus(400);
            return;
        }
        List<Team> myTeams=teamService.getAllUserTeams(uid);
        JSONArray teamArray = new JSONArray();
        for(Team team : myTeams){
            if(Team.PESONAL_TEAM.equals(team.getType())){
                continue;
            }
            teamArray.add(team.getName());
        }
        JsonUtil.writeJSONObject(resp, teamArray);

    }

    @RequestMapping(params="func=getUserByTeamCode")
    public void getUserByTeamCode(HttpServletRequest req,HttpServletResponse resp){
        VWBContainer container = VWBContainerImpl.findContainer();
        if(!validateRequest(req,container)){
            resp.setStatus(401);
            return;
        }
        String teamCode = req.getParameter("teamCode");
        if(StringUtils.isEmpty(teamCode)){
            resp.setStatus(400);
            return;
        }
        Site site = container.getSiteByName(teamCode);
        List<SimpleUser>  users= teamMemberService.getTeamMembersOrderByName(site.getId());
        JSONArray userArray = new JSONArray();
        for(SimpleUser user : users){
            userArray.add( user.getUid());
        }
        JSONObject result = new JSONObject();
        result.put("userInfo", userArray);
        Team team=teamService.getTeamByID(site.getId());
        JSONObject obj = new JSONObject();
        obj.put("teamCode", team.getName());
        obj.put("teamName", team.getDisplayName());
        List<TeamAcl> teamAcls = authorityService.getTeamAdminByTid(team.getId());
        JSONArray adminArrays = new JSONArray();
        for(TeamAcl teamAcl : teamAcls){
            adminArrays.add(teamAcl.getUid());
        }
        obj.put("admin", adminArrays);
        obj.put("accessType", team.getAccessType());
        result.put("teamInfo",obj);
        JsonUtil.writeJSONObject(resp, result);
    }

    private boolean validateRequest(HttpServletRequest request,VWBContainer container){
        String userName = request.getParameter("userName");
        String pp = request.getParameter("password");
        if(StringUtils.isEmpty(userName)||StringUtils.isEmpty(pp)){
            return false;
        }
        String usreName = container.getProperty("duckling.vmt.getteaminfo.user");
        String password = container.getProperty("duckling.vmt.getteaminfo.password");
        return (usreName.equals(userName)&&password.equals(pp));
    }

}
