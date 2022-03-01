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

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamApplicant;
import net.duckling.ddl.service.team.TeamApplicantService;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PictureCheckCodeUtil;
import net.duckling.ddl.util.StatusUtil;
import net.duckling.ddl.web.controller.BaseController;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.common.util.Base64Util;

/**
 * @date 2011-6-21
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/regist")
public class RegistController extends BaseController {
    @Autowired
    private AoneMailService aonemailService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private TeamApplicantService teamApplicantService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AoneUserService aoneUserService ;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private DucklingProperties config;

    private ModelAndView gotoSwtichTeam() {
        String switchTeamURL = urlGenerator.getURL(UrlPatterns.SWITCH_TEAM, "", null);
        ModelAndView mv = new ModelAndView(new RedirectView(switchTeamURL));
        return mv;
    }
    /**
     * 如果team是公开的的就直接加入，不是公开的需提交审核信息
     * @param teamId
     * @param curUid
     */
    private void addTeamMember(int teamId,String curUid,Invitation invite,boolean useExsit){
        Team team = teamService.getTeamByID(teamId);
        if(team.isPublicTeam()){
            UserExt ext = aoneUserService.getUserExtInfo(curUid);
            teamService.addTeamMembers(teamId,
                                       new String[]{curUid}, new String[] {ext.getName()}, team.getDefaultMemberAuth());
        }else{
            TeamApplicant ta = TeamApplicant.build(curUid, teamId, TeamApplicant.STATUS_WAITING, getAppReason(invite.getInvitee()),false);
            teamApplicantService.create(ta);
            String teamName = teamService.getTeamByID(invite.getTeamId()).getDisplayName();
            String configURL = urlGenerator.getAbsoluteURL(UrlPatterns.CONFIG_TEAM, invite.getTeamName(), "func=adminApplicant");
            //+"?teamCode="+invite.getTeamName()+"&func=adminApplicant";
            if(useExsit){
                aonemailService.sendInvitationExisterUser(getAdminUid(invite.getTeamId()), invite, curUid, teamName,configURL);
            }else{
                aonemailService.sendInvitationChangeUser(getAdminUid( invite.getTeamId()), invite, curUid, teamName,configURL);
            }
        }
    }

    private List<String> getAdminUid(int tid){
        List<TeamAcl> admins = authorityService.getTeamAdminByTid(tid);
        Set<String> s = new HashSet<String>();
        for(TeamAcl admin : admins){
            s.add(admin.getUid());
        }
        return new ArrayList<String>(s);
    }

    private String getAppReason(String appliUser){
        return "受邀请者"+appliUser;
    }

    private VWBContext getVWBContext(HttpServletRequest p_request) {
        return VWBContext.createContext(p_request,UrlPatterns.MYSPACE);
    }
    /**
     * 判断用户是否存在
     * @param context
     * @param uid
     * @param password
     * @return
     */
    private boolean isExistedUser(VWBContext context,String uid,String password){
        boolean isExistedUser = aoneUserService.isCorrectUserInfo(uid, password);
        if(isExistedUser){
            return aoneUserService.isExistRegister(uid);
        }else{
            return false;
        }
    }

    /**
     * 是否是团队用户
     * @param context
     * @param tid
     * @param uid
     * @return
     */
    private boolean isTeamMember(int tid,String uid){
        return teamMemberService.checkTeamValidity(uid, tid);
    }

    private void loginWithEmailPwdWithoutRed(VWBContext context,String email,String displayName){
        Collection<Principal> ps = new ArrayList<Principal>();
        Principal p = new UserPrincipal(email,displayName,email);
        ps.add(p);
        context.getVWBSession().setPrincipals(ps);
        teamService.getPersonalTeam(email, displayName);
    }

    private ModelAndView registValidation(BindingResult br,HttpServletRequest request){
        ModelAndView result = null;
        String checkCode = request.getParameter("checkCode");
        boolean b = PictureCheckCodeUtil.checkCode(request, checkCode, "registType",true);
        if(!b){
            result= init(request);
            result.addObject("checkError", "验证码错误！");
        }

        if(result==null&&br.hasErrors()){
            VWBContext context = this.getVWBContext(request);
            result = layout(".aone.portal", context,"/jsp/aone/regist/regist.jsp");
            result.addObject("umtLoginURL", getOauthLogin(checkCode));
        }
        return result;
    }

    private void loginWithoutRedirect(VWBContext context,String email,String displayName) {
        Collection<Principal> ps = new ArrayList<Principal>();
        Principal p = new UserPrincipal(email,displayName,email);
        ps.add(p);
        context.getVWBSession().setPrincipals(ps);
        teamService.getPersonalTeam(email, displayName);
        //teamService.addUserToDefaultMember(new String[]{email}, new String[]{displayName});
    }

    @RequestMapping
    public ModelAndView init(HttpServletRequest request) {
        VWBContext context = this.getVWBContext(request);
        ModelAndView mv = layoutAdaptive(".aone.portal", context,"/jsp/aone/regist/regist.jsp");
        String groupName = request.getParameter("code");
        mv.addObject("umtLoginURL", getOauthLogin(groupName));
        if(groupName!=null&&!"".equals(groupName)){
            mv.addObject("joinGroupName", groupName);
            mv.addObject("joinTeamName", getTeamName(groupName));
        }
        mv.addObject("registerForm",new RegisterForm());
        return mv;
    }
    private String getOauthLogin(String checkCode){
        String loginUrl = config.getProperty(KeyConstants.SITE_BASEURL_KEY)+"/system/login";
        if(StringUtils.isNotEmpty(checkCode)){
            loginUrl+="?checkCode="+checkCode;
        }
        return loginUrl;
    }

    private Object getTeamName(String groupName) {
        String teamCode = Base64Util.decodeBase64(groupName);
        Team team =  teamService.getTeamByName(teamCode);
        if(team!=null){
            return team.getDisplayName();
        }
        return "";
    }
    /**
     * 用一个已经存在的用户接受邀请
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params="func=registByExistedUser")
    public ModelAndView registByExistedUser(HttpServletRequest request,HttpServletResponse response){
        String inviteURL = request.getParameter("existInviteURL");
        String curUid = request.getParameter("existedUser");
        String password = request.getParameter("existedPassword");
        VWBContext context = this.getVWBContext(request);
        if(!isExistedUser(context, curUid, password)){
            return layout(".aone.portal", context,"/jsp/aone/regist/registNotExisterUser.jsp");
        }
        String[] keyAndId = EncodeUtil.decodeKeyAndID(inviteURL);
        Invitation invite = invitationService.getInvitationInstance(keyAndId[0], keyAndId[1]);
        if(!isTeamMember(invite.getTeamId(), curUid)){
            addTeamMember(invite.getTeamId(), curUid, invite,true);
        }
        invitationService.updateInviteStatus(keyAndId[0], keyAndId[1], StatusUtil.ACCEPT);
        SimpleUser  sim = aoneUserService.getSimpleUserByUid(curUid);

        //将新注册的用户加入体验团队
        //teamService.addUserToDefaultMember(new String[]{sim.getUid()}, new String[]{sim.getName()});

        loginWithEmailPwdWithoutRed(context, curUid, sim.getName());
        return  gotoSwtichTeam();
    }

    @RequestMapping(params="func=submitWithInvite")
    public ModelAndView registWithInvite(@Valid @ModelAttribute("registerForm") RegisterForm form,BindingResult br,HttpServletRequest request) {
        String inviteURL = request.getParameter("inviteURL");
        VWBContext context = this.getVWBContext(request);
        String[] keyAndId = EncodeUtil.decodeKeyAndID(inviteURL);
        Invitation invite = invitationService.getInvitationInstance(keyAndId[0], keyAndId[1]);

        ModelAndView re = registValidation(br, request);
        if (re != null) {
            re.addObject("invitation", invite);
            re.addObject("team",teamService.getTeamByID(invite.getTeamId()));
            return re;
        }
        aoneUserService.createAccountInUMT(form.getUid(),form.getName(),form.getPassword());
        aoneUserService.registInAONE(form.getUid(),form.getName());

        //将新注册的用户加入体验团队
        //teamService.addUserToDefaultMember(new String[]{form.getUid()}, new String[]{form.getName()});

        loginWithoutRedirect(context, form.getUid(), form.getName());
        return gotoSwtichTeam();
    }

    /**
     * 使用不是受到邀请的email去接受邀请
     * @param form
     * @param br
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params="func=registWithInviteAndOtherEmail")
    public ModelAndView registWithInviteAndOtherEmail(@Valid @ModelAttribute("registerForm") RegisterForm form,BindingResult br,HttpServletRequest request,HttpServletResponse response){
        String inviteURL = request.getParameter("inviteURL");
        VWBContext context = this.getVWBContext(request);
        String[] keyAndId = EncodeUtil.decodeKeyAndID(inviteURL);
        Invitation invite = invitationService.getInvitationInstance(keyAndId[0], keyAndId[1]);

        ModelAndView re = registValidation(br, request);
        if (re != null) {
            re.addObject("invitation", invite);
            re.addObject("team",teamService.getTeamByID(invite.getTeamId()));
            return re;
        }
        aoneUserService.createAccountInUMT(form.getUid(),form.getName(),form.getPassword());
        aoneUserService.registInAONE(form.getUid(),form.getName());
        loginWithoutRedirect(context, form.getUid(), form.getName());

        addTeamMember( invite.getTeamId(), form.getUid(), invite,true);
        invitationService.updateInviteStatus(keyAndId[0], keyAndId[1], StatusUtil.ACCEPT);
        loginWithEmailPwdWithoutRed(context, form.getUid(), form.getName());
        return gotoSwtichTeam();
    }

    @RequestMapping(params="func=submit")
    public ModelAndView submit(@Valid @ModelAttribute("registerForm") RegisterForm form,BindingResult br,HttpServletRequest request) {
        ModelAndView re = registValidation(br, request);
        VWBSession session = VWBSession.findSession(request);
        String teamId = (String)session.getAttribute(Attributes.TEAM_ID_FOR_JOIN_PUBLIC_TEAM);
        if (re != null) {
            return re;
        }
        VWBContext context = this.getVWBContext(request);
        String tname = request.getParameter("joinGroupName");
        Activation instance = Activation.getInstance(form.getUid(), form.getName(), form.getPassword());
        if(tname!=null&&!"".equals(tname)){
            instance.setTname(tname);
        }else if(null != teamId && StringUtils.isNumeric(teamId)){//通过加入公开团队进行注册
            Team team = teamService.getTeamByID(Integer.parseInt(teamId));
            instance.setTname(Base64Util.encodeBase64(team.getName()));
        }
        int id = aoneUserService.saveActivation(instance);
        instance.setId(id);
        instance.setDisplayURL(EncodeUtil.getDisplayURL(instance));
        String activationURL =  urlGenerator.getAbsoluteURL(UrlPatterns.ACTIVITION, instance.getDisplayURL(),null);
        try {
            aonemailService.sendActivationMail(instance, activationURL);
        }catch(Exception e){
            LOGGER.error(e.getMessage());
            LOGGER.error("Send Activation Mail for "+instance.getEmail()+" Error in RegistController.submit()",e);
        }
        ModelAndView model = layoutAdaptive(".aone.portal", context,"/jsp/aone/regist/sendEmail.jsp");
        model.addObject("umtLogin", context.getContainer().getProperty("duckling.umt.login"));
        return model;
    }

    @RequestMapping(params="func=validate")
    public void validate(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String email = request.getParameter("uid");
        boolean flag = false;
        if(!StringUtils.isEmpty(email)){
            flag = aoneUserService.isExistUMTRegister(email);

        }
        PrintWriter writer = response.getWriter();
        writer.print(!flag);
        writer.flush();
    }

    @RequestMapping(params="func=validateEmail")
    public void validateEmail(HttpServletRequest request,HttpServletResponse response){
        String email = request.getParameter("existedUser");
        String password = request.getParameter("existedPassword");
        boolean flag = false;
        if(StringUtils.isNotEmpty(email)&&StringUtils.isNotEmpty(password)){
            VWBContext context = this.getVWBContext(request);
            flag = isExistedUser(context, email, password);
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("status", flag);
        JsonUtil.write(response, obj);
    }


}
