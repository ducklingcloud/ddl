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

package net.duckling.ddl.service.authenticate.impl;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.service.authenticate.AuthenticationService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.UrlUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.common.util.Base64Util;
import cn.vlabs.vwb.LoginContext;

/**
 * Manages authentication activities for a WikiEngine: user login, logout, and
 * credential refreshes. This class uses JAAS to determine how users log in.
 */
public final class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class);

    private AoneUserService aoneUserService;
    private ForwardPolicy policy = new ForwardPolicy();
    private LoginProvider provider;
    private TeamService teamService;
    private URLGenerator urlGenerator;

    private String getAcceptInputUrl() {
        return VWBContainerImpl.findContainer().getURL("login", null,
                                                       "action=saveprofile", true);
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

    private boolean isLocalLogin(LoginContext login) {
        return login != null && login.getUserName() != null;
    }

    private void redirect(HttpServletRequest request,
                          HttpServletResponse response, String viewUrl) throws IOException {
        VWBSession vwbsession = VWBSession.findSession(request);
        String redirectURL = viewUrl;
        if (vwbsession == null || StringUtils.isEmpty(viewUrl)) {
            redirectURL = urlGenerator.getURL(UrlPatterns.DASHBOARD, null,null);
        } else if (viewUrl.contains("regist") && vwbsession.isAuthenticated()) {
            Team team = getTeamFromRegistURL(viewUrl, request);
            if (null != team) {
                redirectURL = urlGenerator.getURL(UrlPatterns.JOIN_PUBLIC_TEAM, null, "func=join&teamId=" + team.getId());
            } else {
                redirectURL = urlGenerator.getURL(UrlPatterns.DASHBOARD, null,null);
            }
        }
        response.sendRedirect(redirectURL);
    }

    private void updateCurrentUserInformation(HttpServletRequest request) {
        VWBSession vwbsession = VWBSession.findSession(request);
        net.duckling.ddl.service.authenticate.UserPrincipal currUser = (net.duckling.ddl.service.authenticate.UserPrincipal) vwbsession
                .getCurrentUser();
        if (!aoneUserService.isExistAoneRegister(currUser.getName())) {
            aoneUserService.registInAONE(currUser.getName(),
                                         currUser.getFullName());
        }
    }

    public void commit(HttpServletRequest request, HttpServletResponse response) {
        String viewUrl = policy.getSavedSuccessURL(request);
        String failURL = policy.getSavedFailURL(request);

        try {
            Collection<Principal> principals = provider.commit(request);
            VWBSession vwbsession = VWBSession.findSession(request);
            if (principals == null || principals.size() == 0) {
                LOGGER.info("Failed to authenticate user.");
                vwbsession.setStatus(VWBSession.ANONYMOUS);
                if (failURL != null) {
                    response.sendRedirect(failURL);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                vwbsession.setPrincipals(principals);
                updateCurrentUserInformation(request);
                LOGGER.info("Successfully authenticated user.");
                // Redirect!
                LOGGER.info("Redirecting user to " + viewUrl);
                redirect(request, response, viewUrl);
            }
        } catch (IOException e) {
            LOGGER.error("Error ocurred while parse user's credential.");
            LOGGER.debug("Detail is:", e);
        } finally {
            policy.clearUrls(request);
        }
    }

    public void commitLogin(UserPrincipal up, HttpServletRequest request) {
        VWBSession vwbsession = VWBSession.findSession(request);
        if (up == null) {
            LOGGER.info("Failed to authenticate user.");
            vwbsession.setStatus(VWBSession.ANONYMOUS);
        } else {
            ArrayList<Principal> principals = new ArrayList<Principal>();
            principals.add(up);
            vwbsession.setPrincipals(principals);
            updateCurrentUserInformation(request);
        }
    }

    public void escienceRedirect(HttpServletRequest request,
                                 HttpServletResponse response) {
        String redirect = provider.makeUmtRegistUrl(request, VWBContainerImpl
                                                    .findContainer().getURL("switchTeam", null, null, true));
        try {
            response.sendRedirect(redirect);
        } catch (IOException e) {
        }
    }

    public void invalidateSession(HttpServletRequest request) {
        if (request == null) {
            LOGGER.error("No HTTP reqest provided; cannot log out.");
            return;
        }

        HttpSession session = request.getSession();
        String sid = (session == null) ? "(null)" : session.getId();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Invalidating WikiSession for session ID=" + sid);
        }
        VWBSession vwbsession = VWBSession.findSession(request);
        vwbsession.invalidate();
        session.invalidate();
    }

    public void login(HttpServletRequest request, HttpServletResponse response,
                      LoginContext login) {
        try {
            String acceptUrl = getAcceptInputUrl();
            if (login != null) {
                policy.saveSuccessURL(request, login.getSuccessURL());
                policy.saveFailURL(request, login.getFailURL());
            } else {
                policy.saveSuccessURL(request, null);
                policy.saveFailURL(request, null);
            }
            PageView pv;
            if (isLocalLogin(login)) {
                pv = provider.localLogin(request, acceptUrl,
                                         login.getUserName(), login.getPassword());
            } else {
                pv = provider.login(request, acceptUrl);
            }
            pv.forward(request, response);
        } catch (Exception e) {
            LOGGER.error("Local login failed:" + e.getMessage());
        }
    }

    public void login(HttpServletRequest request, HttpServletResponse response,
                      String successUrl, String failUrl) {
        try {
            String acceptUrl = getAcceptInputUrl();
            PageView pv = provider.login(request,
                                         UrlUtil.changeSchemeToHttps(acceptUrl, request));
            policy.saveSuccessURL(request,
                                  UrlUtil.changeSchemeToHttps(successUrl, request));
            policy.saveFailURL(request,
                               UrlUtil.changeSchemeToHttps(failUrl, request));
            pv.forward(request, response);
        } catch (Exception e) {
            LOGGER.error("Display login page failed:" + e.getMessage());
            LOGGER.debug("Detail is ", e);
        }
    }

    public UserPrincipal login(String userName, String password) {
        return provider.login(userName, password);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        VWBSession vwbsession = VWBSession.findSession(request);
        try {
            if (vwbsession.isAuthenticated()) {
                String localURL =getOutReturnUrl(request);
                PageView pv = provider.logout(request, localURL);
                invalidateSession(request);
                pv.forward(request, response);
            } else {
                LOGGER.info("User's session is invalid, just redirect to home page.");
                VWBContainer container = VWBContainerImpl.findContainer();
                PageView pv = new PageView(true, container.getURL("switchTeam",
                                                                  "", null, true));
                pv.forward(request, response);
            }
        } catch (Exception e) {
            LOGGER.error("Display login page failed:" + e.getMessage());
            LOGGER.debug("Detail is ", e);
        }
    }
    private String getOutReturnUrl(HttpServletRequest request){
        String url = (String)VWBSession.findSession(request).getAttribute(Attributes.REQUEST_URL);
        if(StringUtils.isNotEmpty(url)){
            return url;
        }else{
            VWBContainer container = VWBContainerImpl.findContainer();
            if ("true".equals(request.getParameter("embed"))){
                return container.getBaseURL()+"/system/clientcode";
            }else{
                return container.getURL("switchTeam", "", null, true);
            }
        }
    }
    public void saveSuccessURL(HttpServletRequest request, String url) {
        policy.saveSuccessURL(request, url);
    }

    // events processing .......................................................

    public void setAoneUserService(AoneUserService aoneUserService) {
        this.aoneUserService = aoneUserService;
    }

    public void setLoginProvider(LoginProvider provider) {
        this.provider = provider;
    }

    public void setProvider(LoginProvider provider) {
        this.provider = provider;
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public void setUrlGenerator(URLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

}
