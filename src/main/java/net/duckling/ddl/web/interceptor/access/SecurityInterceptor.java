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
package net.duckling.ddl.web.interceptor.access;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vlabs.duckling.common.util.Base64Util;

public class SecurityInterceptor extends HandlerInterceptorAdapter {
    private PermissionChecker checker = null;

    private PermissionDenyListener listener = null;
    private TeamService teamService;
    private String param;
    private URLGenerator urlGenerator;

    public void setUrlGenerator(URLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    private PermissionResolver resolver = new PermissionResolver();

    private boolean checkTeamNotFound(HttpServletRequest request, HttpServletResponse response,
            RequirePermission permission) throws IOException {
        Site site = VWBContext.findSite(request);
        if (isTeamTarget(permission.target()) && site == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        return true;
    }

    private String checkURLIfRegistCodeExist(HttpServletRequest request) {
        String viewUrl = request.getRequestURL().toString();
        VWBSession vwbsession = VWBSession.findSession(request);
        String redirectURL = null;
        if (viewUrl.contains("regist") && vwbsession.isAuthenticated()) {
            String teamCode = request.getQueryString();
            viewUrl += teamCode;
            Team team = getTeamFromRegistURL(viewUrl);
            if (null != team) {
                if (Team.ACCESS_PROTECTED.equals(team.getAccessType())
                        || Team.ACCESS_PUBLIC.equals(team.getAccessType())) {
                    redirectURL = urlGenerator.getURL(UrlPatterns.JOIN_PUBLIC_TEAM, null, "func=join&teamId="+team.getId());
                } else {
                    redirectURL = urlGenerator.getURL(UrlPatterns.T_TEAM_HOME, null,null);
                }
            } else {
                redirectURL = urlGenerator.getURL(UrlPatterns.DASHBOARD, null,null);
            }
        }
        return redirectURL;
    }

    private Team getTeamFromRegistURL(String viewUrl) {
        int codeIndex = viewUrl.indexOf("code");
        if (codeIndex >= 0) {
            String code = viewUrl.substring(codeIndex + "code=".length(), viewUrl.length());
            String tname = Base64Util.decodeBase64(code);
            return teamService.getTeamByName(tname);
        }
        return null;
    }

    private boolean isProcessed(Object returnValue) {
        if (returnValue == null) {
            return true;
        }
        return (Boolean) returnValue;
    }

    private boolean isTeamTarget(String target) {
        return ("team".equals(target) || "page".equals(target) || "collection".equals(target));
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String url = checkURLIfRegistCodeExist(request);
        if (null != url) {
            response.sendRedirect(url);
            return true;
        }
//        System.out.println("uri:" + request.getRequestURI());
        
        String methodName = request.getParameter(param);
        RequirePermission permission = resolver.findPermission(handlerMethod.getBean(), methodName);
        if (permission == null) {
            return true;
        }
        if(isPanFileQuery(request)){
        	return true;
        }
        if (!checkTeamNotFound(request, response, permission)) {
            return false;
        }
        try {
            if (checker == null || checker.hasAccess(request, permission)) {
                return true;
            } else {
                Method denyMethod = resolver.findDenyProcessor(handlerMethod.getBean(), methodName);
                if (denyMethod != null) {
                    Object returnValue = denyMethod.invoke(handlerMethod.getBean(), new Object[] { methodName, request, response });
                    if (isProcessed(returnValue)) {
                        return false;
                    }
                }

                listener.onDeny(request, response, permission);
                return false;
            }
        } catch (PageNotFound e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
    }
    public void setTeamService(TeamService teamService){
    	this.teamService= teamService;
    }
    public void setListener(PermissionDenyListener listener) {
        this.listener = listener;
    }
    public boolean isPanFileQuery(HttpServletRequest request){
    	if(request.getRequestURI().contains("/pan/fileManager")){
    		VWBSession vwbSession = VWBSession.findSession(request);
			return vwbSession.isAuthenticated();
    	}
    	return false;
    }
    public void setParam(String param) {
        this.param = param;
    }

    public void setPermissionChecker(PermissionChecker checker) {
        this.checker = checker;
    }
}
