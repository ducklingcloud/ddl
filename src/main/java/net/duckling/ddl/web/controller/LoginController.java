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
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.service.authenticate.impl.ForwardPolicy;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.common.util.Base64Util;
import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.Oauth;
import cn.vlabs.umt.oauth.UMTOauthConnectException;
import cn.vlabs.umt.oauth.UserInfo;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;


/**
 * 登录Servlet
 *
 * @date Mar 6, 2010
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/system/login")
public class LoginController{
    private static final Logger LOG = Logger.getLogger(LoginController.class);
    private ForwardPolicy policy = new ForwardPolicy();
    @Autowired
    private DucklingProperties config;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private TeamService teamService;

    /**
     * 登录页面主题
     */
    private final static String THEME_EMBED_PC = "embed_pc";
    private final static String THEME_EMBED_MOBILE = "embed_mobile";

    private DucklingProperties simpleTheme=null;
    public DucklingProperties getSimpleTheme(String themeName){
        if (simpleTheme==null){
            simpleTheme= new DucklingProperties();
            simpleTheme.putAll(config);
        }
        simpleTheme.setProperty("theme", themeName);
        return simpleTheme;
    }
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response) throws UMTOauthConnectException, IOException {
        String viewUrl =(String) VWBSession.findSession(request).getAttribute(Attributes.REQUEST_URL);
        if(StringUtils.isEmpty(viewUrl)){
            viewUrl = request.getParameter(Attributes.REQUEST_URL);
            if(StringUtils.isNotEmpty(viewUrl)){
                VWBSession.findSession(request).setAttribute(Attributes.REQUEST_URL,viewUrl);
            }else{
                viewUrl = "";
            }
        }
        //用于团队邀请code的回跳
        String checkCode= request.getParameter("checkCode");
        if(StringUtils.isNotEmpty(checkCode)){
            viewUrl = urlGenerator.getAbsoluteURL(UrlPatterns.REGIST, null, "")+"?code="+checkCode;
            VWBSession.findSession(request).setAttribute(Attributes.REQUEST_URL,viewUrl);
        }

        response.sendRedirect(getLoginUrl(request)+"&state="+URLEncoder.encode(viewUrl, "UTF-8"));
    }
    @RequestMapping("/embed")
    @WebLog(method = "moblieLogin")
    public void embedBrowser(HttpServletRequest request, HttpServletResponse response) throws IOException, UMTOauthConnectException {
        String theme = request.getParameter("theme");
        String needTeams = request.getParameter("need_teams"); //要求返回teams列表
        if(!THEME_EMBED_MOBILE.equals(theme)){
            theme = THEME_EMBED_PC;
        }
        String state = (needTeams==null|| "false".equals(needTeams)) ? "&state=clientcode" : "&state=clientcode_needteams";

        Oauth o = new Oauth(getSimpleTheme(theme));
        response.sendRedirect(o.getAuthorizeURL(request)+state);
    }

    private String getLoginUrl(HttpServletRequest request) throws UMTOauthConnectException{
        Oauth o = new Oauth(config);
        return o.getAuthorizeURL(request);
    }
    @WebLog(method = "loginToken")
    @RequestMapping("/token")
    public void dealToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        Oauth o = new Oauth(config);
        try {
            AccessToken t = o.getAccessTokenByRequest(request);
            String token = t.getAccessToken();
            UserInfo u = t.getUserInfo();
            VWBSession vwbsession = VWBSession.findSession(request);
            LOG.info("token:" + token);
            if (u == null) {
                LOG.info("Failed to authenticate user.");
                vwbsession.setStatus(VWBSession.ANONYMOUS);
                String failURL = policy.getSavedFailURL(request);
                if (failURL != null) {
                    response.sendRedirect(failURL);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                UserPrincipal user = new UserPrincipal(u.getCstnetId(),getUserName(u.getCstnetId(),u),u.getCstnetId(),u.getType());
                List<Principal> set = new ArrayList<Principal>();
                set.add(user);
                vwbsession.setPrincipals(set);
                updateCurrentUserInformation(request);

                request.getSession().setAttribute(Attributes.UMT_ACCESS_TOKEN, token);

                String state = request.getParameter("state");

                //手机端、meepo盘登录
                if ("clientcode".equals(state)) {
                    request.setAttribute("accessToken", t);
                    request.getRequestDispatcher("/system/clientcode").forward(request, response);
                }else if ("clientcode_needteams".equals(state)) { //要求返回团队信息
                    request.setAttribute("accessToken", t);
                    request.getRequestDispatcher("/system/clientcode?need_teams=true").forward(request, response);
                }else if("syncclient".equals(state)) { //同步电脑客户端登录
                    request.setAttribute("accessToken", t);
                    request.getRequestDispatcher("/system/syncclient").forward(request, response);
                }else{
                    String viewUrl =(String) VWBSession.findSession(request).removeAttribute(Attributes.REQUEST_URL);
                    if(StringUtils.isEmpty(viewUrl)){
                        viewUrl = state;
                    }
                    LOG.info("Successfully authenticated user:"+u.getCstnetId()+";access_token:"+token);
                    LOG.info("Redirecting user to " + viewUrl);
                    // Redirect!
                    redirect(request, response, viewUrl);
                }
            }
        } catch (UMTOauthConnectException e) {
            LOG.error("", e);
        } catch (OAuthProblemException e) {
            LOG.error("", e);
        }
    }

    private void updateCurrentUserInformation(HttpServletRequest request) {
        VWBSession vwbsession = VWBSession.findSession(request);
        net.duckling.ddl.service.authenticate.UserPrincipal currUser = (net.duckling.ddl.service.authenticate.UserPrincipal) vwbsession
                .getCurrentUser();
        if (!aoneUserService.isExistAoneRegister(currUser.getName())) {
            String fullName = currUser.getFullName();
            if(StringUtils.isEmpty(fullName)){
                fullName = getNameFromEmail(currUser.getName());
            }
            aoneUserService.registInAONE(currUser.getName(), fullName);
            teamService.getPersonalTeam(currUser.getName(), fullName);
            //将新注册的用户加入体验团队
            //          teamService.addUserToDefaultMember(new String[]{currUser.getName()}, new String[]{fullName});
        }
    }

    private String getUserName(String uid,UserInfo u){
        String name = aoneUserService.getUserNameByID(uid);
        if(StringUtils.isEmpty(name)){
            name = u.getTrueName();
        }
        if(StringUtils.isEmpty(name)){
            name = getNameFromEmail(uid);
        }
        return name;
    }
    private void redirect(HttpServletRequest request, HttpServletResponse response, String viewUrl) throws IOException {
        VWBSession vwbsession = VWBSession.findSession(request);
        String redirectURL = viewUrl;
        if (vwbsession == null || StringUtils.isEmpty(viewUrl)) {
            redirectURL = urlGenerator.getURL(UrlPatterns.DASHBOARD, null, null);
        } else if (viewUrl.contains("regist") && vwbsession.isAuthenticated()) {
            Team team = getTeamFromRegistURL(viewUrl, request);
            if (null != team) {
                redirectURL = urlGenerator.getURL(UrlPatterns.JOIN_PUBLIC_TEAM, null,
                                                  "func=join&teamId=" + team.getId());
            } else {
                redirectURL = urlGenerator.getURL(UrlPatterns.DASHBOARD, null, null);
            }
        }
        response.sendRedirect(redirectURL);
    }

    private String getNameFromEmail(String uid){
        int i = uid.indexOf("@");
        if(i>0){
            return uid.substring(0, i);
        }else{
            return uid;
        }
    }

    private Team getTeamFromRegistURL(String viewUrl, HttpServletRequest request) {
        int codeIndex = viewUrl.indexOf("code");
        if (codeIndex >= 0) {
            String code = viewUrl.substring(codeIndex + "code=".length(),viewUrl.length());
            String tname = Base64Util.decodeBase64(code);
            return teamService.getTeamByName(tname);
        }
        return null;
    }
}
