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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserPreferences;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

//import cn.cnic.cerc.dlog.client.WebLog;

/**
 * @date 2011-6-15
 * @author Clive Lee
 * @revised 去掉若干个response参数 clive-v.10525
 */
@Controller
@RequestMapping("/system/switch")
@RequirePermission(authenticated = true)
public class SwitchTeamController extends BaseController {

    private static final Logger LOG = Logger.getLogger(SwitchTeamController.class);
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private INoticeService noticeService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private URLGenerator urlGenerator;

    private String generate(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.SWITCH_TEAM);
        String uid = context.getCurrentUID();
        UserPreferences userPre = aoneUserService.getUserPreferences(uid);
        if (null != userPre && !UserPreferences.REFRESH_TEAM_MODE_DEFAULT.equals(userPre.getRefreshTeamMode())) {
            int defaultTeam = userPre.getDefaultTeam();
            Team team = null;
            // 由于DAO中采用queryForObject方法，可能查询不到数据而抛出异常
            try {
                team = teamService.getTeamByID(defaultTeam);
            } catch (EmptyResultDataAccessException e) {
                LOG.error("Team whose id=" + defaultTeam + " doesn't exist!", e);
            }
            // 若默认设置的团队被移除或者没有权限访问此团队，则进入DashBoard
            if (null != team) {
                boolean hasAuth = teamMemberService.checkTeamValidity(uid, team.getId());
                if (hasAuth) {
                    // 构造团队的Context
                    context = VWBContext.createContext(defaultTeam, request, null, null);
                    String teamName = team.getName();
                    //设置当前进入团队的tid，用于搜索时获取当前团队
                    String ahm = userPre.getAccessHomeMode();
                    if (UserPreferences.ACCESS_HOME_MODE_DYNAMIC.equals(ahm)) {
                        return urlGenerator.getURL(team.getId(),UrlPatterns.T_NOTICE, null,"func=teamNotice");
                    } else if (UserPreferences.ACCESS_HOME_MODE_TAGITEMS.equals(ahm)) {
                        return urlGenerator.getURL(team.getId(),UrlPatterns.T_LIST, null,null);
                    } else if (UserPreferences.ACCESS_HOME_MODE_STARMARK.equals(ahm)) {
                        return urlGenerator.getURL(team.getId(),UrlPatterns.T_LIST, null,null)+"#rid=0&queryType=myStarFiles";
                    } else if (UserPreferences.ACCESS_HOME_MODE_COMMON.equals(ahm)) {
                        return urlGenerator.getURL(team.getId(), UrlPatterns.T_LIST, null, null)+"#rid=0&queryType=myRecentFiles";
                    }
                }
            }
        }
        return urlGenerator.getURL(UrlPatterns.DASHBOARD, null,null);
    }

    private String getTeamSpaceHome(VWBContext context, String teamName) {
        Site site = context.getContainer().getSiteByName(teamName);
        return site.getFrontPage();
    }
    private Set<Integer> getEventIds(HttpServletRequest request,
                                     VWBContext context, int tid, String type) {
        Set<Integer> result = new HashSet<Integer>();
        int eventId = Integer.valueOf(request.getParameter("eventId"));
        result.add(eventId);
        try {
            int targetId = Integer.valueOf(request.getParameter("targetId"));
            if (targetId > 0) {
                List<Notice> notices = noticeService.getNoticeByTypeAndTargId(
                    type, targetId);
                if (notices != null) {
                    for (Notice n : notices) {
                        result.add(n.getEventId());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("去除消息提示错误", e);
        }
        return result;
    }

    private String getPersonalSpaceURL(VWBContext context) {
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        Site site = context.getContainer().getSite(tid);
        return site.getFrontPage();
    }

    private String getPersonSpaceHome(HttpServletRequest request) {
        VWBContext context = this.getVWBContext(request);
        return getPersonalSpaceURL(context);
    }

    private String getRedirectURL(HttpServletRequest request, int tid) {
        String noticeId = request.getParameter("noticeId");
        Notice notice = null;
        if (StringUtils.isNotEmpty(noticeId)) {
            int nId = Integer.parseInt(noticeId);
            notice = noticeService.getNoticeById(nId);
        } else {
            String redirect = request.getParameter("redirect");
            if (redirect != null && "true".equals(redirect)) {
                int eventId = Integer.valueOf(request.getParameter("eventId"));
                List<Notice> ntis = noticeService.getNoticeByEventId(eventId);
                if (ntis != null && !ntis.isEmpty()) {
                    notice = ntis.get(0);
                }
            }
        }
        if (notice != null) {
            return notice.getTarget().getUrl();
        } else {
            return null;
        }
    }

    private String getTeamSpaceHome(HttpServletRequest request, String teamName) {
        VWBContext context = this.getVWBContext(request);
        Site site = context.getContainer().getSiteByName(teamName);
        Team  t = teamService.getTeamByID(site.getId());
        String url = null;
        if(Team.DEFAULT_TEAM_VIEW_NOTIC.equals(t.getTeamDefaultView())){
            url = urlGenerator.getAbsoluteURL(site.getId(), UrlPatterns.T_NOTICE, null, null)+"?func=teamNotice";
        }else{
            url = urlGenerator.getAbsoluteURL(site.getId(), UrlPatterns.T_LIST, null, null);
        }
        return url;
    }

    private VWBContext getVWBContext(HttpServletRequest pRequest) {
        return VWBContext.createContext(pRequest, UrlPatterns.SWITCH_TEAM);
    }

    private void rememberLastEnter(HttpServletRequest request) {
        VWBContext context = getVWBContext(request);
        UserPreferences userPre = aoneUserService.getUserPreferences(context
                                                                     .getCurrentUID());
        if (null == userPre
            || !UserPreferences.REFRESH_TEAM_MODE_AUTO.equals(userPre
                                                              .getRefreshTeamMode())) {
            return;
        }
        String teamName = request.getParameter("team");
        if (null == teamName || "".equals(teamName)) {
            String func = request.getParameter("func");
            if (null != func && func.equals("person")) {
                teamName = teamService.getTeamNameFromEmail(context
                                                            .getCurrentUID());
            } else {
                return;
            }
        }
        Team team = teamService.getTeamByName(teamName);
        // 设置当前进入团队的tid，用于搜索时获取当前团队
        // request.getSession(true).setAttribute(CURRENT_TID, team.getId());
        userPre.setDefaultTeam(team.getId());
        aoneUserService.updateUserPreferences(userPre);
    }

    @RequestMapping(params = "func=forward")
    public ModelAndView forward(HttpServletRequest request) {
        VWBContext context = this.getVWBContext(request);
        Site site = context.getContainer().getSiteByName(request.getParameter("team"));
        String frontPage =urlGenerator.getAbsoluteURL(site.getId(), UrlPatterns.T_NOTICE, null, null)+ "?func=" + request.getParameter("tab");
        return new ModelAndView(new RedirectView(frontPage));
    }

    @RequestMapping(params = "func=home")
    public ModelAndView home(HttpServletRequest request) {
        return new ModelAndView(new RedirectView(getPersonSpaceHome(request)
                                                 + "?func=" + request.getParameter("tab")));
    }

    @RequestMapping
    public ModelAndView init(HttpServletRequest request) {
        String redirectUrl = generate(request);
        return new ModelAndView(new RedirectView(redirectUrl));
    }

    @RequestMapping(params = "func=jump")
    public ModelAndView jump(HttpServletRequest request) {
        rememberLastEnter(request);
        return new ModelAndView(new RedirectView(getTeamSpaceHome(request,
                                                                  request.getParameter("team"))));
    }

    //@WebLog(method = "personTeam", params = "from")
    @RequestMapping(params = "func=person")
    public ModelAndView person(HttpServletRequest request) {
        rememberLastEnter(request);
        return new ModelAndView(new RedirectView(getPersonSpaceHome(request)));
    }
    @RequestMapping(params="func=pan")
    public ModelAndView personSync(HttpServletRequest request){
        return new ModelAndView(new RedirectView("/pan/list"));
    }
    @RequestMapping(params = "func=switchTeam")
    public ModelAndView teamList(HttpServletRequest request) {
        return new ModelAndView(new RedirectView(getPersonSpaceHome(request)
                                                 + "?func=switchTeam"));
    }

    //@WebLog(method = "updateOneNoticeEmail", params = "messageType,teamId,uid,eventId,targetId")
    @RequestMapping(params="func=updateOneNoticeEmail")
    public void updateOneNoticeFromWeb(HttpServletRequest request,
                                       HttpServletResponse response){
        updateOneNotice(request, response);
    }
    /**
     * 点击链接，去掉一个notice
     */
    @RequestMapping(params = "func=updateOneNotice")
    public void updateOneNotice(HttpServletRequest request,
                                HttpServletResponse response) {
        String type = request.getParameter("messageType");
        int tid = Integer.parseInt(request.getParameter("teamId"));
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        Set<Integer> eventIds = getEventIds(request, context, tid, type);

        teamPreferenceService.updateOneNoticeAccessTime(uid, tid, type, eventIds);
        String redirectURL = getRedirectURL(request, tid);
        if (!CommonUtils.isNull(redirectURL)) {
            try {
                response.sendRedirect(redirectURL);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        } else {
            JsonObject json = new JsonObject();
            json.addProperty("status", "success");
            json.addProperty("tid", tid);
            JsonUtil.write(response, json);
        }
    }

    @RequestMapping(params = "func=updateAllNotice")
    public void updateAllTeam(HttpServletRequest request,
                              HttpServletResponse response) {
        String type = request.getParameter("messageType");
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        teamPreferenceService.updateAllMessage(uid, type);
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        JsonUtil.write(response, json);
    }

    @RequestMapping(params = "func=updateNotice")
    public void updateOneTeam(HttpServletRequest request,
                              HttpServletResponse response) {
        String type = request.getParameter("messageType");
        int tid = Integer.parseInt(request.getParameter("teamId"));
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        teamPreferenceService.updateNoticeAccessTime(uid, tid, type);
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("tid", tid);
        JsonUtil.write(response, json);
    }

}
