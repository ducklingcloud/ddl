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

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * 错误处理
 * 
 * @author xiejj@cnic.cn
 * @creation Jan 20, 2011 11:46:19 AM
 */
@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController {
    private static final Logger LOGGER = Logger.getLogger(ErrorController.class);
    
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private AuthorityService authorityService;

    @RequestMapping
    public ModelAndView service(HttpServletRequest request) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.ERROR);
        boolean teamFounded = Boolean.TRUE.equals(request.getAttribute("teamFounded"));
        if (teamFounded) {
            return layout(ELayout.LYNX_MAIN, context, findRendable("500"));
        } else {
            return layout(".aone.portal", context, findRendable("500"));
        }
    }

    @RequestMapping(params = "e=404")
    public ModelAndView onNotFound(HttpServletRequest request) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.ERROR);
        String returnPage;
        String frontPage = null;
        boolean teamFounded = Boolean.TRUE.equals(request.getAttribute("teamFounded"));
        if (teamFounded) {
            String referURL = request.getHeader("Referer");
            if (referURL != null && referURL.startsWith(context.getSite().getBaseURL())) {
                request.setAttribute("hasRefer", true);
                returnPage = referURL;
            } else {
                returnPage = context.getSite().getFrontPage();
            }
            frontPage = context.getSite().getFrontPage();
        } else {
            returnPage = urlGenerator.getURL(UrlPatterns.SWITCH_TEAM, null,null);
        }
        request.setAttribute("returnPage", returnPage);
        request.setAttribute("frontPage", frontPage);
        request.setAttribute("contextPath", request.getContextPath());
        if (teamFounded) {
            return layout(ELayout.LYNX_MAIN, context, findRendable("404"));
        } else {
            return layout(".aone.portal", context, findRendable("404"));
        }
    }

    @RequestMapping(params = "e=403")
    public ModelAndView onForbidden(HttpServletRequest request) {
        request.setAttribute("contextPath", request.getContextPath());
        VWBContext context = VWBContext.createContext(request, UrlPatterns.ERROR);
        boolean hasTeamAccess = Boolean.TRUE.equals(request.getAttribute("hasTeamAccess"));
        if(!hasTeamAccess){
        	hasTeamAccess=authorityService.teamAccessability(VWBContext.getCurrentTid(), VWBSession.findSession(request), "view");
        }
        String returnPage;
        if (hasTeamAccess) {
            String referURL = request.getHeader("Referer");
            if (referURL != null && referURL.startsWith(context.getSite().getBaseURL())) {
                request.setAttribute("hasRefer", true);
                returnPage = referURL;
            } else {
                returnPage = context.getSite().getFrontPage();
            }
        } else {
            returnPage = urlGenerator.getURL(UrlPatterns.SWITCH_TEAM, null,null);
        }
        String frontPage;
        if (context.getSite() != null&&hasTeamAccess) {
            frontPage = context.getSite().getFrontPage();
        } else {
            frontPage = urlGenerator.getURL(UrlPatterns.SWITCH_TEAM, null,null);
        }
        request.setAttribute("returnPage", returnPage);
        request.setAttribute("frontPage", frontPage);
        request.setAttribute("contextPath", request.getContextPath());

        if (hasTeamAccess) {
            return layout(ELayout.LYNX_MAIN, context, findRendable("403"));
        } else {
            return layout(".aone.portal", context, findRendable("403"));
        }
    }

    @ExceptionHandler(IOException.class)
    public void onException(IOException e) {
        LOGGER.debug("导向错误页面时发生异常", e);
    }

    private String findRendable(String error) {
        String jsp;
        if ("404".equals(error) || "403".equals(error)) {
            jsp = "/error/" + error + ".jsp";
        } else {
            jsp = "/error/500.jsp";
        }
        return jsp;
    }
}
