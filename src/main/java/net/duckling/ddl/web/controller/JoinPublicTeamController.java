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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamApplicant;
import net.duckling.ddl.service.team.TeamApplicantService;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.web.bean.PageNum;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequestMapping("/system/joinPublicTeam")
public class JoinPublicTeamController extends BaseController {

    private static final String TEAM_MEMBER = "teamMember";
    private static final String OUTSIDE = "outside";
    @Autowired
    private AoneMailService aonemailService;
    @Autowired
    private TeamApplicantService teamApplicantService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private URLGenerator urlGenerator;

    @RequestMapping
    public ModelAndView display(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.JOIN_PUBLIC_TEAM);
        ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/team/joinPublicTeam.jsp");
        List<Team> teamList = teamService.getAllPublicAndProtectedTeam(0, 0);
        int totalNum=teamList.size();
        int pageSize=20;
        int currPageNum=Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter("pageNum"), "1"));
        PageNum pageNum=new PageNum(currPageNum,totalNum,pageSize);
        teamList = teamService.getAllPublicAndProtectedTeam(pageNum.getOffset(), pageSize);
        List<String> creatorNames = new ArrayList<String>();
        if(null!=teamList && teamList.size()>0){
            for(Team team : teamList){
                SimpleUser user = aoneUserService.getSimpleUserByUid(team.getCreator());
                String username = (null == user)?"无":user.getName();
                creatorNames.add(username);
            }
        }
        mv.addObject("teamList", teamList);
        mv.addObject("creatorNames", creatorNames);
        if(isAuthUser(request, context)){
            prepareAuthUserData(context, mv, teamList);
        }
        mv.addObject(LynxConstants.PAGE_TITLE, "加入公开团队");
        mv.addObject("teamUrl", generatTeamUrl(teamList));
        mv.addObject("pageNum",pageNum);
        return mv;
    }

    @RequestMapping(params="func=join")
    @RequirePermission(authenticated = true)
    public ModelAndView join(HttpServletRequest request, @RequestParam("teamId") int tid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.JOIN_PUBLIC_TEAM);
        String curUid = context.getCurrentUID();
        Team team = teamService.getTeamByID(tid);
        if(team.isHangup()){
            return null;
        }
        String redirectURL = null;
        boolean isInTeam = teamMemberService.isUserInTeam(tid, curUid);
        if(!isInTeam){
            addUserToTeam(curUid, team, tid);
        }else{
            return new ModelAndView(new RedirectView(urlGenerator.getURL(UrlPatterns.DASHBOARD, null,null)));
        }
        if(Team.ACCESS_PROTECTED.equals(team.getAccessType())){
            redirectURL = urlGenerator.getURL(UrlPatterns.DASHBOARD, null,null);
            List<TeamAcl> admins = authorityService.getTeamAdminByTid(team.getId());
            List<String> adminEmails = new ArrayList<String>();
            for(TeamAcl teamAcl : admins){
                SimpleUser u = aoneUserService.getSimpleUserByUid(teamAcl.getUid());
                adminEmails.add(u.getEmail());
            }
            String url = urlGenerator.getAbsoluteURL(UrlPatterns.CONFIG_TEAM, team.getName(), "func=adminApplicant");
            SimpleUser  user = aoneUserService.getSimpleUserByUid(curUid);
            aonemailService.sendApplyToTeamAdmin(adminEmails,user,team.getDisplayName(),url);
        }else if(Team.ACCESS_PUBLIC.equals(team.getAccessType())){
            redirectURL = urlGenerator.getURL(team.getId(), UrlPatterns.T_TEAM_HOME, null, null);
        }else{
            redirectURL = urlGenerator.getURL(UrlPatterns.JOIN_PUBLIC_TEAM, null, null);
        }
        return new ModelAndView(new RedirectView(redirectURL));
    }

    @RequestMapping(params="func=quit")
    @RequirePermission(authenticated = true)
    public void quit(HttpServletRequest request, HttpServletResponse response, @RequestParam("teamId") int tid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.JOIN_PUBLIC_TEAM);
        JsonObject obj = new JsonObject();
        obj.addProperty("tid", tid);
        String curUid = context.getCurrentUID();
        boolean isInTeam = teamMemberService.isUserInTeam(tid, curUid);
        if(!isInTeam){
            obj.addProperty("status", false);
            JsonUtil.write(response, obj);
            return;
        }
        teamService.removeMembers(tid, new String[]{curUid},true);
        obj.addProperty("status", true);
        JsonUtil.write(response, obj);
    }

    @RequestMapping(params="func=cancelApply")
    @RequirePermission(authenticated = true)
    public void cancelApply(HttpServletRequest request, HttpServletResponse response, @RequestParam("teamId") int tid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.JOIN_PUBLIC_TEAM);
        String curUid = context.getCurrentUID();
        teamApplicantService.cancelApply(tid, curUid);
        JsonObject obj = new JsonObject();
        obj.addProperty("tid", tid);
        obj.addProperty("status", true);
        JsonUtil.write(response, obj);
    }

    @RequestMapping(params="func=iknow")
    @RequirePermission(authenticated = true)
    public void iknow(HttpServletRequest request, HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.JOIN_PUBLIC_TEAM);
        String curUid = context.getCurrentUID();
        teamApplicantService.iknowAllTeamApplicantNotice(curUid);
        JsonObject obj = new JsonObject();
        obj.addProperty("status", true);
        JsonUtil.write(response, obj);
    }

    private boolean isAuthUser(HttpServletRequest request, VWBContext context){
        String curUid = context.getCurrentUID();
        VWBSession session = VWBSession.findSession(request);
        if(!StringUtils.isEmpty(curUid) && session.isAuthenticated()){
            return true;
        }
        return false;
    }

    private void prepareAuthUserData(VWBContext context, ModelAndView mv, List<Team> teamList){
        String curUid = context.getCurrentUID();
        List<Team> myTeamList = null;//已加入的团队
        List<TeamApplicant> myteamApplicantList = null;//待审核的团队
        if(null==curUid){
            myTeamList = new ArrayList<Team>();
            myteamApplicantList = new ArrayList<TeamApplicant>();
        }
        else{
            myTeamList = teamService.getAllUserPublicAndProtectedTeam(curUid);
            myteamApplicantList = teamApplicantService.getUserApplicant(curUid);
        }
        mv.addObject("teamStatus", checkStatus(teamList, myTeamList, myteamApplicantList));
        mv.addObject("isAuthenticated", true);
    }

    /**
     * 标记每个公开团队是否是用户加入过的
     * @param all 所有公开和待审核的团队集合
     * @param my 我加入的团队集合
     * @param myApplicant 我申请的团队集合
     * @return
     */
    private JsonArray checkStatus(List<Team> all, List<Team> my, List<TeamApplicant> myApplicant){
        if(isEmpty(all)){
            return null;
        }
        int size = all.size();
        JsonArray status = initStatusMap(size);
        if(isEmpty(my) && isEmpty(myApplicant)){
            return status;
        }
        for(int i=0; i<size; i++){
            int tid = all.get(i).getId();
            JsonObject obj = (JsonObject)status.get(i);
            boolean isTeamMember = amITeamMemberOfThisTeam(my, tid);
            obj.addProperty("status", (isTeamMember)?TEAM_MEMBER:OUTSIDE);
            if(!isTeamMember){
                updateStatusIfIApplied(myApplicant, tid, obj);
            }
        }
        return status;
    }

    /**
     * 判断我当前加入的团队是否包含该团队(tid)
     * @param team 我加入的团队列表
     * @param tid 目标团队ID
     * @return true or false
     */
    private boolean amITeamMemberOfThisTeam(List<Team> team, int tid){
        if(null != team && team.size()>0){
            for(Team temp : team){
                if(tid==temp.getId()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 如果我的申请记录表(ta)中,包含申请加入该团队(tid)的记录，则更新obj的status以及reason
     * @param ta 我的申请记录表
     * @param tid 目标团队
     * @param obj 返回给前台的信息
     */
    private void updateStatusIfIApplied(List<TeamApplicant> ta, int tid, JsonObject obj){
        if(null != ta && ta.size()>0){
            for(TeamApplicant temp : ta){
                if(tid==temp.getTid()){
                    obj.addProperty("status", temp.getStatus());
                    obj.addProperty("reason", temp.getReason());
                    return;
                }
            }
        }
    }

    /**
     * 初始化每个团队的状态为未加入
     * @param size
     * @return
     */
    private JsonArray initStatusMap(int size){
        JsonArray maps = new JsonArray();
        for(int i=0; i<size; i++){
            JsonObject obj = new JsonObject();
            obj.addProperty("status", OUTSIDE);
            obj.addProperty("reason", "");
            maps.add(obj);
        }
        return maps;
    }

    private boolean isEmpty(List<?> list){
        return (null == list || list.size()<=0);
    }

    private void addUserToTeam(String curUid, Team team, int tid){
        SimpleUser user = aoneUserService.getSimpleUserByUid(curUid);
        if(Team.ACCESS_PROTECTED.equals(team.getAccessType())){
            TeamApplicant ta = TeamApplicant.build(curUid, tid, TeamApplicant.STATUS_WAITING, "",false);
            teamApplicantService.create(ta);
        }else{
            teamService.addTeamMembers(tid, new String[]{curUid}, new String[]{user.getName()}, team.getDefaultMemberAuth());
        }
    }

    @RequestMapping(params="func=search")
    public ModelAndView search(HttpServletRequest request,@RequestParam("keyword")String keyword){
        int page = 1;
        try{
            page = Integer.parseInt(request.getParameter("pageNum"));
        }catch(Exception e){}
        PaginationBean<Team> result = teamService.queryByTeamName(keyword,(page-1)*20,20);
        List<String> creatorNames = new ArrayList<String>();
        List<Team> teamList = result.getData();
        if(null!=teamList && teamList.size()>0){
            for(Team team : teamList){
                SimpleUser user = aoneUserService.getSimpleUserByUid(team.getCreator());
                String username = (null == user)?"无":user.getName();
                creatorNames.add(username);
            }
        }
        VWBContext context = VWBContext.createContext(request, UrlPatterns.JOIN_PUBLIC_TEAM);
        ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/team/joinPublicTeam.jsp");
        mv.addObject("teamList", result.getData());
        mv.addObject("creatorNames", creatorNames);
        if(isAuthUser(request, context)){
            prepareAuthUserData(context, mv, teamList);
        }
        mv.addObject(LynxConstants.PAGE_TITLE, "加入公开团队");
        PageNum pageNum=new PageNum(page,result.getTotal(),20);
        mv.addObject("pageNum",pageNum);
        mv.addObject("keyword", keyword);
        mv.addObject("teamUrl", generatTeamUrl(teamList));
        return mv;
    }

    private List<String> generatTeamUrl(List<Team> teams){
        List<String> result = new ArrayList<String>();
        if(null!=teams && teams.size()>0){
            for(Team team : teams){
                String teamUrl = urlGenerator.getAbsoluteURL(team.getId(), UrlPatterns.T_TEAM_HOME, null, null);
                result.add(teamUrl);
            }
        }
        return result;
    }


}
