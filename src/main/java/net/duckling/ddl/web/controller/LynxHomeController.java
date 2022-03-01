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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authenticate.UserPrincipal;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.JsonUtil;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/home")
public class LynxHomeController extends BaseController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private AoneUserService aoneUserService;
    @RequestMapping
    public void init(HttpServletResponse response){
        JsonUtil.write(response, teamUserTotal(response));
    }

    @RequestMapping(params="func=total")
    public void total(HttpServletRequest request, HttpServletResponse response){
        JsonUtil.writeJSONP(request, response, teamUserTotal(response));
    }

    @RequestMapping(params="func=getStatus")
    public void getStatus(HttpServletRequest request, HttpServletResponse response){
        JsonUtil.write(response, userStatus(request));
    }

    /**
     * jsonp方法请求
     * @param request
     * @param response
     */
    @RequestMapping(params="func=getStatusJp")
    public void getStatusJp(HttpServletRequest request, HttpServletResponse response){
        JsonUtil.writeJSONP(request, response, userStatus(request));
    }

    @RequestMapping(params="func=getMobile")
    public void getMobile(HttpServletRequest request,HttpServletResponse response) throws IOException{
        String u = request.getHeader("user-agent");
        u = u.toLowerCase();
        LOGGER.info("二维码扫描客户端记录："+u);
        if(StringUtils.isEmpty(u)){
            response.sendRedirect("/");
        }else if(u.contains("ios")||u.contains("iphone")||u.contains("ipad")){
            response.sendRedirect("http://itunes.apple.com/cn/app/ke-yan-zai-xian/id495109931");
        }else if(u.contains("android")){
            response.sendRedirect("http://www.escience.cn/apks/ddl-latest.apk");
        }else{
            response.sendRedirect("/");
        }
    }

    @SuppressWarnings("unchecked")
    private JsonObject teamUserTotal(HttpServletResponse response){
        int totalTeamNum = teamService.getTotalTeamNumber();
        int totalUserNum =aoneUserService.getTotalAoneUserNumber();
        JsonObject obj = new JsonObject();
        obj.addProperty("totalTeamNum", totalTeamNum);
        obj.addProperty("totalUserNum", totalUserNum);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private JsonObject userStatus(HttpServletRequest request){
        VWBSession session = VWBSession.findSession(request);
        JsonObject obj = new JsonObject();
        obj.addProperty("haveUmtId", false);
        if(session != null&&session.isAuthenticated()){
            obj.addProperty("status", true);
            obj.addProperty("userEmail", ((UserPrincipal)session.getCurrentUser()).getFullName());
        }else{
            Cookie[] cookies = request.getCookies();
            if(cookies!=null&&cookies.length>0){
                obj.addProperty("status", false);
                Integer i = (Integer)request.getSession().getAttribute("redirect_uri_count");
                if(i!=null&&i>2){
                    request.getSession().setAttribute("redirect_uri_count",0);
                }else{
                    for(Cookie cookie : cookies){
                        if("UMTID".equals(cookie.getName())){
                            obj.addProperty("haveUmtId", true);
                            break;
                        }
                    }
                }
            }else{
                obj.addProperty("status", false);
            }
        }
        return obj;
    }
}
