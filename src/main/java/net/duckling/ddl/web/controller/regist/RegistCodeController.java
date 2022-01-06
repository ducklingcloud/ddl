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
package net.duckling.ddl.web.controller.regist;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.controller.BaseController;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.vlabs.duckling.common.util.Base64Util;

@Controller
@RequestMapping("/t/{code}")
public class RegistCodeController extends BaseController {
    @Autowired
    private DucklingProperties config;
    @Autowired
    private TeamService teamService;
    @Autowired
    private URLGenerator generator;
    @RequestMapping
    public ModelAndView registCode(HttpServletRequest request,@PathVariable("code")String code){
        if(VWBSession.findSession(request).isAuthenticated()){
            int teamId = getTeamId(code);
            String url = generator.getAbsoluteURL(UrlPatterns.JOIN_PUBLIC_TEAM, null, null)+"?func=join&teamId="+teamId;
            return new ModelAndView(new RedirectView(url));
        }else{
            VWBContext context = VWBContext.createContext(request,UrlPatterns.MYSPACE);
            ModelAndView mv = layout(".aone.portal", context,"/jsp/aone/regist/regist.jsp");
            mv.addObject("umtLoginURL", getOauthLogin(code));
            if(code!=null&&!"".equals(code)){
                mv.addObject("joinGroupName", code);
                mv.addObject("joinTeamName", getTeamName(code));
            }
            mv.addObject("registerForm",new RegisterForm());
            return mv;
        }

    }


    private String getOauthLogin(String checkCode){
        String loginUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY)+"/system/login";
        if(StringUtils.isNotEmpty(checkCode)){
            loginUrl+="?checkCode="+checkCode;
        }
        return loginUrl;
    }

    private String getTeamName(String groupName) {
        String teamCode = Base64Util.decodeBase64(groupName);
        Team team =  teamService.getTeamByName(teamCode);
        if(team!=null){
            return team.getDisplayName();
        }
        return "";
    }
    private int getTeamId(String groupName){
        String teamCode = Base64Util.decodeBase64(groupName);
        Team team =  teamService.getTeamByName(teamCode);
        if(team!=null){
            return team.getId();
        }
        return -1;
    }
}
