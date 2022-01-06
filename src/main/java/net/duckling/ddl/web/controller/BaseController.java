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
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.Constant;
import net.duckling.ddl.service.render.JSPRendable;
import net.duckling.ddl.service.render.Rendable;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.UserAgentUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * @date 2011-5-6
 * @author Clive Lee
 */
public class BaseController {
    private final static String VIEW_ROOT = "/WEB-INF/views/";
    protected static final Logger LOGGER = Logger.getLogger(BaseController.class);
    @Autowired
    private RenderingService farenderingService;
    protected ModelAndView layout(String template, VWBContext context, Rendable content) {
        ModelAndView mv = new ModelAndView(template);
        if (content == null) {
            content = farenderingService.createRendable(VWBContext.getCurrentTid(),context.getResource().getRid());
        }
        mv.addObject("content", content);
        if (context.getSite() != null) {
            mv.addObject("teamCode", context.getSite().getTeamContext());
        }
        return mv;
    }
    /**
     * 向系统报404错误
     * @param request   HttpRequest对象
     * @param response  HttpResponse对象
     * @param inTeam    true时显示团队的顶菜单。false不显示团队的顶菜单
     */
    protected void notFound(HttpServletRequest request, HttpServletResponse response, boolean inTeam){
        if (!inTeam){
            request.setAttribute("accessMain", Boolean.TRUE);
        }
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            LOGGER.debug("Error founded in not found :",e);
        }
    }

    protected ModelAndView layout(String template, VWBContext context,String jsp) {
        //FIXME:NO ID VALUE
        return layout(template, context, new JSPRendable(jsp, 65536));
    }

    /**
     * 根据客户端类型，返回相适应的视图模版
     * @param template
     * @param context
     * @param jsp
     * @return
     */
    protected ModelAndView layoutAdaptive(String template, VWBContext context,String jsp) {
        if(UserAgentUtil.isMobile(context.getHttpRequest())){
            template = UserAgentUtil.MOBILE + template;
            jsp = VIEW_ROOT + UserAgentUtil.MOBILE + jsp;
        }
        return layout(template, context, new JSPRendable(jsp, 65536));
    }

    protected ModelAndView layout(String template, VWBContext context,String jsp,Integer id) {
        return layout(template, context, new JSPRendable(jsp, id));
    }

    /**
     * 生成csrf token 格式:"CSRF"+DUCKLING_NAME+版本+用户ID的md5
     * @param request
     * @return
     */
    protected String getCsrfToken(HttpServletRequest request){
        String basePath = request.getSession().getServletContext().getRealPath("/");
        VWBContext context = VWBContext.createContext(request, UrlPatterns.PLAIN);
        return DigestUtils.md5Hex("CSRF" + Constant.DUCKLING_NAME + Constant.getVersion(basePath) + context.getCurrentUID());
    }

    /**
     * 验证csrf token
     * @param request
     * @return
     */
    protected boolean isWrongCsrfToken(HttpServletRequest request){
        String ctoken = request.getParameter("ctoken");
        if(ctoken==null || "".equals(ctoken)){
            return true;
        }
        String serverToken = getCsrfToken(request);
        if(serverToken.equals(ctoken)){
            return false;
        }
        return true;
    }

    /**
     * 权限不足
     * @param request
     * @param response
     */
    protected void sendForbidden(HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    public static String getLocaleMessage(HttpServletRequest request, String code){
        WebApplicationContext ac = RequestContextUtils.getWebApplicationContext(request);
        return ac.getMessage(code, null, RequestContextUtils.getLocale(request));
    }
}
