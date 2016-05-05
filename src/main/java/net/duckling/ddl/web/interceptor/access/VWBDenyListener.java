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
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.UrlUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * @date May 9, 2011
 * @author xiejj@cnic.cn
 */
public class VWBDenyListener implements PermissionDenyListener {
    private static final Logger LOGGER = Logger.getLogger(VWBDenyListener.class);

    private String getRequiredPermission(RequirePermission requirePermission) {
        return "Permission(\"" + requirePermission.target() + "\", \"view\"";
    }

    private String getRequestURL(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if (request.getQueryString() != null) {
            url = url + "?" + request.getQueryString();

        }
        return url;
    }

    public void onDeny(HttpServletRequest request, HttpServletResponse response, RequirePermission requirePermission)
            throws IOException {
        VWBSession m_session = VWBSession.findSession(request);
        VWBContainer container = VWBContainerImpl.findContainer();
        Principal currentUser = m_session.getCurrentUser();

        try {
            if (m_session.isAuthenticated()) {
                LOGGER.info("User " + currentUser.getName() + " has no access - forbidden (permission="
                        + getRequiredPermission(requirePermission) + ") URL:" + request.getRequestURI());
                response.setHeader("ddl-auth", "Permission dend");
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else {
                LOGGER.info("User " + currentUser.getName() + " has no access - redirecting (permission="
                        + getRequiredPermission(requirePermission) + ") URL:" + request.getRequestURI());

                String requesturl = (String) request.getAttribute(Attributes.REQUEST_URL);
                if (requesturl == null) {
                    requesturl = getRequestURL(request);
                }
                m_session.setAttribute(Attributes.REQUEST_URL, requesturl);
                m_session.setAttribute(Attributes.TEAM_ID_FOR_JOIN_PUBLIC_TEAM, request.getParameter("teamId"));
                if(isAjaxRequest(request)){
                	response.setStatus(450);
                }else{
                	if(isHashURL(requesturl)){
                		m_session.removeAttribute(Attributes.REQUEST_URL);
                		request.setAttribute("url", UrlUtil.changeSchemeToHttps(container.getURL(UrlPatterns.LOGIN, null, null, false), request));
                		request.getRequestDispatcher("/jsp/aone/hash/dealHashRequest.jsp").forward(request, response);
                	}else{
                		String redirect = UrlUtil.changeSchemeToHttps(container.getURL(UrlPatterns.LOGIN, null, null, false), request);
                		response.sendRedirect(redirect);
                	}
                }
            }
        } catch (IOException e) {
            LOGGER.error("Redirect failed for:" + e.getMessage(),e);
            throw new InternalVWBException(e.getMessage());
        } catch (ServletException e) {
        	 LOGGER.error("Redirect failed for:" + e.getMessage(),e);
        	 throw new InternalVWBException(e.getMessage());
		}
    }
    
    private boolean isHashURL(String url){
    	if(StringUtils.isNotEmpty(url)){
    		return url.endsWith("/list");
    	}
    	return false;
    }
    
    private boolean isAjaxRequest(HttpServletRequest request){
    	String ajaxFlag = request.getHeader("X-Requested-With");
    	if("XMLHttpRequest".equals(ajaxFlag)){
    		return true;
    	}
    	return false;
    }
}
