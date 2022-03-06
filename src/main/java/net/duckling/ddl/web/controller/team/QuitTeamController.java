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

package net.duckling.ddl.web.controller.team;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.subscribe.SubscriptionService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//import cn.cnic.cerc.dlog.client.WebLog;


/**
 * @date 2011-5-26
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/quitTeam")
@RequirePermission( authenticated = true)
public class QuitTeamController extends BaseController {
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private AoneUserService aoneUserService;
    private static final Logger LOG = Logger.getLogger(QuitTeamController.class);
    private VWBContext getVWBContext(HttpServletRequest pRequest) {
        return VWBContext.createContext(pRequest,UrlPatterns.ADMIN);
    }

    @SuppressWarnings("unchecked")
    //@WebLog(method = "quitTeam", params = "teamName")
    @RequestMapping
    public void quitTeam(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        String teamName = request.getParameter("teamName");
        int tid = teamService.getTeamByName(teamName).getId();
        boolean isUserInTeam = teamMemberService.checkTeamValidity(uid,tid);
        if( isUserInTeam){
            teamService.removeMembers(tid, new String[]{uid},true); //reuse code
            UserExt ext = aoneUserService.getUserExtInfo(uid);
            VWBContext.setCurrentTid(tid);
            Site site = context.getContainer().getSite(tid);
            subscriptionService.removePersonSubscription(site.getId(),uid,ext.getId()); //to test
            List<TeamPreferences> users = teamPreferenceService.getUidByTid(tid);
            if(users==null||users.isEmpty()){
                teamService.updateHangup(tid);
            }
            LOG.info("user "+uid+" quit team "+teamName);
        }
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("tid", tid);
        JsonUtil.write(response, json);
    }
    //@WebLog(method = "quitTeamValidate", params = "teamName")
    @RequestMapping(params="func=quitTeamValidate")
    public void quitTeamValidate(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        String teamName = request.getParameter("teamName");
        Team team = teamService.getTeamByName(teamName);
        List<TeamPreferences> users = teamPreferenceService.getUidByTid(team.getId());
        JsonObject json = new JsonObject();

        //会议团队不能退出
        if(Team.CONFERENCE_TEAM.equals(team.getType())){
            json.addProperty("type", "conferenceTeam");
            JsonUtil.write(response, json);
            return;
        }

        if(users.size()==1){
            json.addProperty("type", "onlyOneUser");
        }else{
            List<TeamAcl> s = authorityService.getTeamAdminByTid(team.getId());
            if(s.size()==1&&s.get(0).getUid().equals(uid)){
                json.addProperty("type", "onlyOneAdmin");
            }else{
                json.addProperty("type", "success");
            }
        }
        JsonUtil.write(response, json);
    }

}
