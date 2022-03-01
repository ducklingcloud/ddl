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

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamApplicantService;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StatusUtil;
import net.duckling.ddl.web.bean.UMTServerURLUtil;
import net.duckling.ddl.web.controller.BaseController;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


/**
 * @date 2011-6-20
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/invite/{inviteURL}")
public class InvitationValidateController extends BaseController{
    @Autowired
    private TeamApplicantService teamApplicantService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private URLGenerator urlGenerator;

    private ModelAndView joinTeamSuccess() {
        String switchTeamURL = urlGenerator.getURL(UrlPatterns.SWITCH_TEAM, "", null);
        ModelAndView mv = new ModelAndView(new RedirectView(switchTeamURL));
        return mv;
    }
    private void acceptInvitation(VWBContext context,Invitation instance,HttpServletResponse response) {
        UserExt ext = aoneUserService.getUserExtInfo(instance.getInvitee());
        Team team = teamService.getTeamByID(instance.getTeamId());
        boolean exist = teamMemberService.isUserInTeam(instance.getTeamId(), ext.getUid());
        if(!exist){
            String auth = team.getDefaultMemberAuth();
            if(null==auth || "".equals(auth)){
                auth = Team.AUTH_EDIT;
            }
            teamService.addTeamMembers(instance.getTeamId(),
                                       new String[]{ext.getUid()}, new String[] {ext.getName()}, auth);
        }
        JsonUtil.write(response, new Gson().toJsonTree(instance));
    }
    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request, UrlPatterns.INVITE);
    }

    @RequestMapping
    public ModelAndView init(HttpServletRequest request, @PathVariable("inviteURL")String inviteURL) {
        VWBContext context = getVWBContext(request);
        Invitation instance = getInvitationInstance(inviteURL, context);
        if(StatusUtil.isWaiting(instance.getStatus())) {
            ModelAndView mv = userUnmatched(context, instance);
            if(mv!=null){
                return mv;
            }
            boolean flag = aoneUserService.isExistUMTRegister(instance.getInvitee());
            if(flag) {
                //如果该用户已注册,则将该人添加为成员
                return joinTeamSuccess();
            }else {
                //如果该用户没有注册，则跳转到未注册页面
                return registBeforeJoin(context,instance);
            }
        }
        //用户邀请链接已过期
        return expired(context);
    }

    public ModelAndView registBeforeJoin(VWBContext context,Invitation instance) {
        ModelAndView mv = layout(".aone.portal", context,"/jsp/aone/regist/regist.jsp");
        instance.setDisplayURL(EncodeUtil.getDisplayURL(instance));
        mv.addObject("umtLoginURL", UMTServerURLUtil.getUMTLoginServerURL(context));
        mv.addObject("invitation", instance);
        mv.addObject("team",teamService.getTeamByID(instance.getTeamId()));
        mv.addObject("registerForm",new RegisterForm());
        return mv;
    }

    public ModelAndView userUnmatched(VWBContext context, Invitation instance) {
        String uid = context.getCurrentUID();
        if(context.getVWBSession().isAuthenticated()&&StringUtils.isNotEmpty(uid)&&!uid.equals(instance.getInvitee())){
            String logout = urlGenerator.getURL(UrlPatterns.LOGOUT, null,null);
            String message = "当前用户与激活用户存在冲突，请<a href='"+logout+"'>退出</a>后重试";
            ModelAndView mv = layout(".aone.portal", context,"/jsp/aone/regist/registError.jsp");
            mv.addObject("message", message);
            return mv;
        }
        return null;
    }

    @RequestMapping(params="func=accept")
    public void acceptInvitation(HttpServletRequest request,HttpServletResponse response,
                                 @PathVariable("inviteURL")String inviteURL) {
        VWBContext context = getVWBContext(request);
        Invitation instance = getInvitationInstance(inviteURL, context);
        if(StatusUtil.isWaiting(instance.getStatus())) {
            boolean flag = updateInvitationStatus(instance, context,StatusUtil.ACCEPT);
            if(flag){
                acceptInvitation(context, instance,response);
                //同步接受邀请审核
                teamApplicantService.batchDelete(instance.getTeamId(), new String[]{instance.getInvitee()});
            }
        }
    }

    public ModelAndView expired(VWBContext context) {
        return layout(".aone.portal", context,"/jsp/aone/regist/inviteExpired.jsp");
    }

    @RequestMapping(params="func=ignore")
    public void ignoreInvitation(HttpServletRequest request,HttpServletResponse response,
                                 @PathVariable("inviteURL")String inviteURL) {
        VWBContext context = getVWBContext(request);
        Invitation instance = getInvitationInstance(inviteURL, context);
        if(StatusUtil.isWaiting(instance.getStatus())) {
            boolean flag = updateInvitationStatus(instance, context,StatusUtil.IGNORE);
            if(flag)
            {
                JsonUtil.write(response, new Gson().toJsonTree(instance));
            }
        }
    }

    private Invitation getInvitationInstance(String inviteURL, VWBContext context) {
        String[] keyAndId = EncodeUtil.decodeKeyAndID(inviteURL);
        return invitationService.getInvitationInstance(keyAndId[0], keyAndId[1]);
    }

    private boolean updateInvitationStatus(Invitation instance,VWBContext context,String status) {
        boolean flag = invitationService.updateInviteStatus(instance.getEncode(),instance.getId()+"",status);
        if(flag)
        {
            instance.setStatus(status);
        }
        else
        {
            instance.setStatus(StatusUtil.INVALID);
        }
        return flag;
    }
}
