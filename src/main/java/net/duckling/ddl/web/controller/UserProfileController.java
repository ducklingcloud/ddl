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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.HTMLConvertUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PinyinUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.bean.NoAuthToViewException;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.vlabs.commons.principal.UserPrincipal;

/**
 * @date 2011-6-24
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/user/{uxid}")
@RequirePermission( authenticated = true)
public class UserProfileController extends BaseController {
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private DucklingProperties systemProperty;
    @Autowired
    private AoneUserService aoneUserService;
    private VWBContext getVWBContext(HttpServletRequest pRequest) {
        return VWBContext.createContext(pRequest,UrlPatterns.PLAIN);
    }
    private String getSiteURL(int tid, VWBContext context) {
        VWBContext.setCurrentTid(tid);
        Site s = context.getContainer().getSite(tid);
        String result = s.getFrontPage();
        s = null;
        return result;
    }


    @RequestMapping
    public ModelAndView view(HttpServletRequest request,HttpServletResponse response,
                             @PathVariable("uxid")Integer uxid){
        VWBContext context = getVWBContext(request);
        String user = context.getCurrentUID();
        ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/profile/viewProfile.jsp");
        aoneUserService.getUserNameByID(user);

        UserExt instance = aoneUserService.getUserExtByAutoID(uxid);
        if (instance!=null){
            boolean flag = teamMemberService.isAtTheSameTeam(instance.getUid(),user);
            if(!flag){
                int tid = teamService.getPersonalTeam(context.getCurrentUID(), context.getCurrentUserName());
                throw new NoAuthToViewException(getSiteURL(tid, context));
            }
            String changePassURL = systemProperty.getProperty("duckling.umt.change.password");
            mv.addObject("changePasswordURL",changePassURL);
            mv.addObject("user",instance);
            if(instance.getUid().equals(user)) {
                mv.addObject("isMyself", true);
            }
            return mv;
        }else{
            notFound(request, response, false);
            return null;
        }
    }

    @RequestMapping(params="func=editProfile")
    public ModelAndView editProfile(HttpServletRequest request,HttpServletResponse response,
                                    @PathVariable("uxid")Integer uxid) {
        VWBContext context = getVWBContext(request);
        String user = context.getCurrentUID();
        UserExt instance = aoneUserService.getUserExtInfo(user);
        ModelAndView mv = layout(".aone.portal", context, "/jsp/aone/profile/editProfile.jsp");
        if(instance.getUid().equals(user)) {
            mv.addObject("user",instance);
        }
        return mv;
    }

    @RequestMapping(params="func=updateProfile")
    public void updateProfile(HttpServletRequest request,HttpServletResponse response,
                              @PathVariable("uxid")Integer uxid) {
        if(StringUtil.illCharCheck(request, response, "name","department","mobile","address","orgnization","telephone","weibo","qq","email")){
            return;
        }
        UserExt ux = new UserExt();
        VWBContext context = getVWBContext(request);
        ux.setId(uxid);
        ux.setUid(request.getParameter("uid"));
        ux.setName(HTMLConvertUtil.replaceLtGt(request.getParameter("name")));
        ux.setDepartment(HTMLConvertUtil.replaceLtGt(request.getParameter("department")));
        ux.setMobile(HTMLConvertUtil.replaceLtGt(request.getParameter("mobile")));
        ux.setAddress(HTMLConvertUtil.replaceLtGt(request.getParameter("address")));
        ux.setOrgnization(HTMLConvertUtil.replaceLtGt(request.getParameter("orgnization")));
        ux.setTelephone(HTMLConvertUtil.replaceLtGt(request.getParameter("telephone")));
        ux.setWeibo(HTMLConvertUtil.replaceLtGt(request.getParameter("weibo")));
        ux.setQq(HTMLConvertUtil.replaceLtGt(request.getParameter("qq")));
        ux.setEmail(HTMLConvertUtil.replaceLtGt(request.getParameter("email")));
        ux.setPinyin(HTMLConvertUtil.replaceLtGt(PinyinUtil.getPinyin(ux.getName())));
        aoneUserService.modifyUserProfile(ux);
        UserPrincipal up = new UserPrincipal( ux.getUid(), ux.getName(), ux.getUid());
        List<Principal> principals = new ArrayList<Principal>();
        principals.add(up);
        context.getVWBSession().setPrincipals(principals);
        updatePersonalTeamName(request, context);
        JsonObject json = new JsonObject();
        json.addProperty("result", "success");
        JsonUtil.write(response, json);
    }

    private void updatePersonalTeamName(HttpServletRequest request, VWBContext context) {
        int tid = teamService.getPersonalTeamNoCreate(context.getCurrentUID());
        Team team = teamService.getTeamByID(tid);
        team.setDisplayName(request.getParameter("name")+"的个人空间");
        String accessType = Team.ACCESS_PRIVATE;
        String defaultMemberAuth = Team.AUTH_VIEW;
        if(!team.isPersonalTeam()){
            accessType = request.getParameter("accessType");
            defaultMemberAuth = request.getParameter("defaultMemberAuth");
        }
        teamService.updateBasicInfo(team.getName(),team.getDisplayName(),team.getDescription(), accessType, defaultMemberAuth,null);
    }
}
