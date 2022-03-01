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

package net.duckling.ddl.web.controller.team;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.export.ExportService;
import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.TagGroupRender;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamApplicant;
import net.duckling.ddl.service.team.TeamApplicantRender;
import net.duckling.ddl.service.team.TeamApplicantService;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.HTMLConvertUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StatusUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import cn.vlabs.duckling.api.umt.rmi.user.UMTUser;
import cn.vlabs.duckling.common.util.Base64Util;

/**
 * @date 2011-5-26
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/configTeam")
@RequirePermission(target = "team", operation = "admin")
public class ConfigTeamController extends BaseController {

    @Autowired
    private ITagService tagService;
    @Autowired
    private ExportService exportService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private TeamApplicantService teamApplicantService;
    @Autowired
    private AoneMailService aoneMailService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private ICacheService cacheService;

    private static final Logger LOGGER = Logger.getLogger(ConfigTeamController.class);

    private VWBContext getVWBContext(HttpServletRequest pRequest) {
        return VWBContext.createContext(pRequest, "configTeam");
    }

    @RequestMapping
    public ModelAndView init(HttpServletRequest request, @RequestParam("teamCode") String teamName) {
        VWBContext context = getVWBContext(request);
        Team team = teamService.getTeamByName(teamName);
        ModelAndView mv = setTeamModel(context, team);
        setTeamModel(mv, team);
        setCurrentTab("basic", mv);
        String teamUrl = urlGenerator.getAbsoluteURL(team.getId(), UrlPatterns.T_TEAM, null, null).replace(":80/", "/");
        if(teamUrl.endsWith("/")){
            teamUrl = teamUrl.substring(0, teamUrl.length()-1);
        }
        mv.addObject("teamUrl", teamUrl);
        mv.addObject(LynxConstants.PAGE_TITLE, "基本设置");
        return mv;
    }

    private void setTeamModel(ModelAndView mv, Team team) {
        try {
            String url = urlGenerator.getAbsoluteURL(UrlPatterns.REGIST_CODE, null, null);
            String code = Base64Util.encodeBase64(team.getName());
            url = url + "/" + code;
            mv.addObject("currTeamUrl", url);
        } catch (Exception e) {
            LOGGER.error("获取URL时错误！", e);
        }
    }

    @RequestMapping(params = "func=adminBasic")
    public ModelAndView adminBasic(HttpServletRequest request, @RequestParam("teamCode") String teamName) {
        return init(request, teamName);
    }

    private ModelAndView setTeamModel(VWBContext context, Team team) {
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/team/configTeam.jsp");
        mv.addObject("currTeam", team);
        return mv;
    }

    private void setCurrentTab(String tab, ModelAndView mv) {
        mv.addObject("currTab", tab);
    }

    @RequestMapping(params = "func=adminApplicant")
    public ModelAndView addminApplicant(HttpServletRequest request, @RequestParam("teamCode") String teamName) {
        VWBContext context = getVWBContext(request);
        Team team = teamService.getTeamByName(teamName);
        TeamApplicantService tas = teamApplicantService;
        List<TeamApplicantRender> tar = tas.getWaitingApplicantOfTeam(team.getId());
        String[] uids = getWaitingUserID(tar);
        Boolean[] isMembers = teamMemberService.isUsersInTeam(team.getId(), uids);
        ModelAndView mv = setTeamModel(context, team);
        mv.addObject("waiting", tar);
        mv.addObject("isMembers", isMembers);
        mv.addObject("reject", tas.getRejectApplicantOfTeam(team.getId()));
        setCurrentTab("applicant", mv);
        mv.addObject(LynxConstants.PAGE_TITLE, "管理申请");
        return mv;
    }

    @RequestMapping(params = "func=adminUsers")
    public ModelAndView adminUsers(HttpServletRequest request, @RequestParam("teamCode") String teamName) {
        long begin = System.currentTimeMillis();
        VWBContext context = getVWBContext(request);
        Team team = teamService.getTeamByName(teamName);
        ModelAndView mv = setTeamModel(context, team);
        List<TeamAcl> resutls = authorityService.getTeamMembersAuthority(team.getId());
        long centry = System.currentTimeMillis();
        mv.addObject("existMembers", resutls);
        mv.addObject("memberCount", resutls.size());
        mv.addObject("currentUser", context.getCurrentUID());
        setCurrentTab("users", mv);
        mv.addObject(LynxConstants.PAGE_TITLE, "成员列表");
        long end = System.currentTimeMillis();
        if ((end - begin) > 1000) {
            LOGGER.info("---------->(centry-begin)=" + (centry - begin) + "(end-centry)=" + (end - centry) + "总耗时："
                        + (end - begin));
        }
        mv.addObject("csrfToken", super.getCsrfToken(request));
        return mv;
    }

    @RequirePermission(target = "team", operation = "edit")
    @RequestMapping(params = "func=adminInvitations")
    public ModelAndView adminInvitations(HttpServletRequest request, @RequestParam("teamCode") String teamName) {
        VWBContext context = getVWBContext(request);
        Team team = teamService.getTeamByName(teamName);
        List<Invitation> allList = invitationService.getInvitationListByTeam(team.getId());
        List<Invitation> waitingList = categoryByStatus(allList, StatusUtil.WAITING);
        List<Invitation> acceptList = categoryByStatus(allList, StatusUtil.ACCEPT);
        List<Invitation> invalidList = categoryByStatus(allList, StatusUtil.INVALID);
        ModelAndView mv = setTeamModel(context, team);
        mv.addObject("waitingList", waitingList);
        mv.addObject("acceptList", acceptList);
        mv.addObject("invalidList", invalidList);
        setCurrentTab("invitations", mv);
        if(Team.ACCESS_PRIVATE.equals(team.getAccessType())){
            mv.addObject("showUrl",false);
        }else{
            mv.addObject("showUrl",true);
            setTeamModel(mv, team);
        }
        mv.addObject(LynxConstants.PAGE_TITLE, "邀请成员");
        return mv;
    }

    @RequestMapping(params = "func=exportDocs")
    public ModelAndView exportDocs(HttpServletRequest request, @RequestParam("teamCode") String teamName) {
        VWBContext context = getVWBContext(request);
        Team team = teamService.getTeamByName(teamName);
        ITagService ts = tagService;
        List<TagGroupRender> render = ts.getTagGroupsForTeam(team.getId());
        List<Tag> tagNotInGroup = ts.getTagsNotInGroupForTeam(team.getId());
        addDefaultGroup(render, tagNotInGroup);
        ModelAndView mv = setTeamModel(context, team);
        mv.addObject("taggroup", render);
        mv.addObject("teamName", teamName);
        setCurrentTab("export", mv);
        mv.addObject(LynxConstants.PAGE_TITLE, "导出文档");
        return mv;
    }

    @RequestMapping(params = "func=download")
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam("teamCode") String teamName, @RequestParam("format") String format) {
        VWBContext context = getVWBContext(request);
        String[] tagids = request.getParameterValues("tag");
        Map<String, List<Tag>> groupTagMap = constructGroupTagMap(tagids);
        exportService.download(context, teamName, groupTagMap, response, format);
    }

    @RequestMapping(params = "func=searchUsers")
    public void searchUsers(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam("teamCode") String teamName, @RequestParam("keyword") String keyword) {
        if (keyword == null || keyword.length() == 0) {
            keyword = "@";
        }
        List<UMTUser> results = aoneUserService.search(keyword, 0, 1000);
        // 过滤已存在的成员
        Team team = teamService.getTeamByName(teamName);
        Set<String> filterSet = getFilterSet(teamMemberService.getTeamMembersOrderByName(team.getId()));
        JsonArray array = new JsonArray();
        for (UMTUser user : results) {
            if (!filterSet.contains(user.getUsername())) {
                JsonObject object = new JsonObject();
                object.addProperty("uid", user.getUsername());
                object.addProperty("name", user.getTruename());
                array.add(object);
            }
        }
        JsonUtil.write(response, array);
    }

    @RequestMapping(params = "func=removeMember")
    public void removeMember(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("teamCode") String teamCode, @RequestParam("uid") String uid) {
        Team team = teamService.getTeamByName(teamCode);
        boolean status = teamService.removeMembers(team.getId(), new String[]{uid}, true);
        LOGGER.info("user:"+VWBSession.getCurrentUid(request)+" remove member:"+uid+" from team"+team);
        JsonObject object = new JsonObject();
        object.addProperty("status", status);
        object.addProperty("uid", uid);
        JsonUtil.write(response, object);
    }

    @RequestMapping(params = "func=updateAllAuthority")
    public void updateAllMemberAuthority(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("teamCode") String teamName, @RequestParam("uid") String[] uids,
                                         @RequestParam("auth") String[] auths) {
        Team team = teamService.getTeamByName(teamName);
        teamService.updateMembersAuthority(team.getId(), uids, auths,false);
        LOGGER.info("user:"+VWBSession.getCurrentUid(request)+" update user :"+getUIDString(uids)+"Authority:"+getUIDString(auths)+" from team"+team);
        JsonObject object = new JsonObject();
        object.addProperty("status", "success");
        JsonUtil.write(response, object);
    }

    @RequestMapping(params = "func=updateOneAuthority")
    public void updateOneMemberAuthority(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("teamCode") String teamName, @RequestParam("uid") String uid,
                                         @RequestParam("auth") String auth) {
        if(super.isWrongCsrfToken(request)){return;}

        Team team = teamService.getTeamByName(teamName);
        teamService.updateMembersAuthority(team.getId(), new String[]{ uid}, new String[]{auth}, false);
        LOGGER.info("user:"+VWBSession.getCurrentUid(request)+" update user :"+uid+"Authority:"+auth+" from team"+team);
        JsonObject object = new JsonObject();
        object.addProperty("status", "success");
        JsonUtil.write(response, object);
    }

    private Set<String> getFilterSet(List<SimpleUser> teamMembers) {
        Set<String> filterSet = new HashSet<String>();
        if (teamMembers != null) {
            for (SimpleUser _user : teamMembers) {
                filterSet.add(_user.getUid());
            }
        }
        return filterSet;
    }

    @RequestMapping(params = "func=addMembers")
    public void addMembers(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("user-info") String[] userInfoArray, @RequestParam("teamCode") String teamName,
                           @RequestParam("defaultAuth") String defaultAuth) {
        if (userInfoArray != null) {
            JsonArray array = new JsonArray();
            String[] uids = new String[userInfoArray.length];
            String[] usernames = new String[userInfoArray.length];
            for (int i = 0; i < userInfoArray.length; i++) {
                String[] temp = userInfoArray[i].split("#");
                uids[i] = temp[0];
                usernames[i] = temp[1];
                JsonObject object = new JsonObject();
                object.addProperty("uid", uids[i]);
                object.addProperty("auth", defaultAuth);
                object.addProperty("name", usernames[i]);
                array.add(object);
            }
            Team team = teamService.getTeamByName(teamName);
            teamService.addTeamMembers(team.getId(), uids, usernames, defaultAuth);
            LOGGER.info("user:"+VWBSession.getCurrentUid(request)+" add  user :"+getUIDString(uids)+"Authority:"+defaultAuth+" to team"+team);
            JsonUtil.write(response, array);
        }
    }

    @RequestMapping(params = "func=adjustPlace")
    public ModelAndView adjustPlace(HttpServletRequest request, @RequestParam("teamCode") String teamCode) {
        return init(request, teamCode);
    }

    @RequestMapping(params = "func=updateBasicInfo")
    public void updateBasicInfo(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("teamCode") String teamCode) {
        if(StringUtil.illCharCheck(request, response, "title","description")){
            return;
        }
        String title = HTMLConvertUtil.replaceLtGt(request.getParameter("title"));
        String description = HTMLConvertUtil.replaceLtGt(request.getParameter("description"));
        VWBContext context = getVWBContext(request);
        Team team = teamService.getTeamByName(teamCode);
        String accessType = Team.ACCESS_PRIVATE;
        String defaultMemberAuth = Team.AUTH_VIEW;
        if (null != team && !team.isPersonalTeam()) {
            accessType = request.getParameter("accessType");
            defaultMemberAuth = request.getParameter("defaultMemberAuth");
        }
        String defaultView = request.getParameter("teamDefaultView");
        teamService.updateBasicInfo(teamCode, title, description, accessType, defaultMemberAuth,defaultView);
        LOGGER.info("user:"+VWBSession.getCurrentUid(request)+" update team basic info title:"+title+";access type:"+accessType+"; defaultMemberAuth"+defaultMemberAuth);
        context.getSite().changeTitle(title);
        JsonUtil.write(response, new JsonObject());
    }

    @RequestMapping(params = "func=sendTeamInvite")
    public void sendTeamInvite(HttpServletRequest request,HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String inviter = context.getCurrentUID();
        String recipient = request.getParameter("invitees");
        //html转义
        String message = HtmlUtils.htmlEscape(request.getParameter("message"));
        String teamName = context.getSite().getTeamContext();
        Team team = teamService.getTeamByName(teamName);
        String[] invitees = recipient.split(",");
        trimString(invitees);
        Set<String> filterSet = buildFilterSet(team);
        List<String> invalidInvitees = new ArrayList<String>();
        List<String> validInvitees = new ArrayList<String>();
        List<String> restrictInvitees = new ArrayList<String>();
        for (int i = 0; i < invitees.length; i++) {
            if (!StringUtil.isValidEmail(invitees[i])) {
                LOGGER.warn("在发送要请邮箱的时候，邮箱" + invitees[i] + "未通过校验！");
                continue;
            }
            if (filterSet.contains(invitees[i])) {
                invalidInvitees.add(invitees[i].trim());
            } else {
                if(!inviteeMailCount(inviter, invitees[i], getVWBContext(request))){
                    restrictInvitees.add(invitees[i].trim());
                }else{
                    validInvitees.add(invitees[i].trim());
                }
            }
        }

        if(!inviterMailCount(inviter, validInvitees.size(), getVWBContext(request))){
            JsonObject obj = new JsonObject();
            obj.addProperty("success", false);
            obj.addProperty("message", "发送邀请的邮件超过500封，请0.5小时后重新发送");
            LOGGER.warn("Amount of send mail exceed. " + "(inviter:"+ inviter + ")");
            JsonUtil.write(response, obj);
            return;
        }

        aoneMailService.sendInvitationMail(teamName, team.getId(), inviter, validInvitees, message,
                                           team.getDisplayName());

        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        obj.add("invalidInvitees", transferJSONArray(invalidInvitees));
        obj.add("restrictInvitees", transferJSONArray(restrictInvitees));
        obj.addProperty("validCount", validInvitees.size());
        JsonUtil.write(response, obj);
    }

    @RequestMapping(params = "func=cancelInvite")
    public void cancelInvite(HttpServletRequest request,HttpServletResponse response){
        String recipient = request.getParameter("invitees");
        int tid = VWBContext.getCurrentTid();
        Invitation inv =  invitationService.getExistValidInvitation(recipient, tid);
        invitationService.updateInviteStatus(inv.getEncode(), inv.getId()+"", StatusUtil.INVALID);
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        JsonUtil.write(response, obj);
    }

    private JsonArray transferJSONArray(List<String> list){
        JsonArray arr = new JsonArray();
        for(String s : list){
            arr.add(s);
        }
        return arr;
    }

    private void trimString(String[] ss) {
        if (ss != null && ss.length > 0) {
            for (int i = 0; i < ss.length; i++) {
                if (StringUtils.isNotEmpty(ss[i])) {
                    ss[i] = ss[i].trim();
                }
            }
        }
    }

    private Set<String> buildFilterSet(Team team) {
        List<SimpleUser> teamMembers = teamMemberService.getTeamMembersOrderByName(team.getId());
        Set<String> filterSet = new HashSet<String>();
        for (SimpleUser _s : teamMembers) {
            filterSet.add(_s.getUid());
        }
        return filterSet;
    }

    private List<Invitation> categoryByStatus(List<Invitation> src, String status) {
        List<Invitation> results = new ArrayList<Invitation>();
        if (src != null) {
            for (Invitation _t : src) {
                if (status.equals(_t.getStatus())) {
                    results.add(_t);
                }
            }
        }
        return results;
    }

    @RequestMapping(params = "func=uploadMailList", headers = { "X-File-Name" })
    public void uploadMailList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in = request.getInputStream();
        String fileName = request.getHeader("X-File-Name");
        uploadMailListCommon(fileName, in, response);
    }

    // Support for IE
    @RequestMapping(method = RequestMethod.POST, params = "func=uploadMailList")
    public void uploadMailList(@RequestParam("qqfile") MultipartFile uplFile, HttpServletResponse response)
            throws IOException {
        InputStream in = uplFile.getInputStream();
        String fileName = uplFile.getOriginalFilename();
        uploadMailListCommon(fileName, in, response);
    }

    private void uploadMailListCommon(String fileName, InputStream in, HttpServletResponse response) throws IOException{
        String fileType = fileName.substring(fileName.indexOf('.') + 1);
        List<Map<String, String>> list = EmailAddressFileAnalysisUtil.getContactsFromStream(in, fileType.toUpperCase());
        JsonArray jsonList = new JsonArray();
        JsonObject result = new JsonObject();
        if (null == list) {
            result.addProperty("success", false);
        } else {
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> map = list.get(i);
                map.put("index", String.valueOf(i));
                JsonObject jsonMap = new JsonObject();
                for (String key : map.keySet()) {
                    jsonMap.addProperty(key, map.get(key));
                }
                jsonList.add(jsonMap);
            }
            result.addProperty("success", true);
            result.add("list", jsonList);
        }

        in.close();
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.write(response, result);
    }

    private void addDefaultGroup(List<TagGroupRender> render, List<Tag> tags) {
        TagGroupRender _default = new TagGroupRender();
        TagGroup temp = createDefaultGroup();
        _default.setGroup(temp);
        _default.setTags(tags);
        render.add(_default);
    }

    private TagGroup createDefaultGroup() {
        TagGroup temp = new TagGroup();
        temp.setId(0);
        temp.setTitle("未分组标签");
        return temp;
    }

    private Map<String, List<Tag>> constructGroupTagMap(String[] tagids) {
        Map<String, List<Tag>> result = new HashMap<String, List<Tag>>();
        if (null != tagids && tagids.length > 0) {
            ITagService ts = tagService;
            int len = tagids.length;
            int[] ids = new int[len];
            for (int i = 0; i < len; i++) {
                ids[i] = Integer.parseInt(tagids[i]);
            }
            List<Tag> tagList = ts.getTags(ids);
            int curTagGroupId = -1;
            if (null != tagList && tagList.size() > 0) {
                List<Tag> temp = new ArrayList<Tag>();
                for (Tag tag : tagList) {
                    if (curTagGroupId != -1 && tag.getGroupId() != curTagGroupId) {
                        TagGroup tg = ts.getTagGroupById(temp.get(0).getGroupId());
                        tg = null == tg ? createDefaultGroup() : tg;
                        result.put(tg.getTitle(), temp);
                        temp = new ArrayList<Tag>();// 此处不能用clear()
                    }
                    temp.add(tag);
                    curTagGroupId = tag.getGroupId();
                }
                TagGroup tg = ts.getTagGroupById(temp.get(0).getGroupId());
                tg = null == tg ? createDefaultGroup() : tg;
                result.put(tg.getTitle(), temp);
            }
        }
        return result;
    }

    /**
     * 批量审核用户申请
     *
     * @param request
     * @param response
     * @param uids
     *            用户ID
     * @param status
     *            审核状态
     */
    @RequestMapping(params = "func=auditApplicant")
    public void auditApplicant(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("uids[]") String[] uids, @RequestParam("unames[]") String[] unames,
                               @RequestParam("auths[]") String[] auths, @RequestParam("status") String status) {
        if (uids.length <= 0) {
            JsonUtil.write(response, getJSONResponse(false, "参数错误"));
            return;
        }
        VWBContext context = getVWBContext(request);
        int teamId = VWBContext.getCurrentTid();

        //验证uids
        if(!checkApplicant(uids, teamId)){
            JsonUtil.write(response, getJSONResponse(false, "参数错误"));
            return;
        }

        updateTeamApplicant(context, teamId, uids, status);
        boolean isSuccess = updateTeamMember(teamId, uids, unames, auths, status);
        if (!isSuccess) {
            JsonUtil.write(response, getJSONResponse(false, "团队成员调整失败!"));
            return;
        }
        Team team = teamService.getTeamByID(teamId);
        for (String uid : uids) {
            String teamUrl = context.getBaseURL() + "/" + team.getName();
            if (TeamApplicant.STATUS_ACCEPT.equals(status)) {
                aoneMailService.sendApplyResultToUser(uid, team.getDisplayName(), teamUrl, true);
            } else {
                aoneMailService.sendApplyResultToUser(uid, team.getDisplayName(), teamUrl, false);
            }
        }
        JsonObject obj = getJSONResponse(true, "");
        obj.addProperty("uids", getUIDString(uids));
        obj.addProperty("status", status);
        JsonUtil.write(response, obj);
    }

    private void updateTeamApplicant(VWBContext context, int teamId, String[] uids, String status) {
        if (TeamApplicant.STATUS_ACCEPT.equals(status)) {
            teamApplicantService.batchDelete(teamId, uids);
            // 将审核后的邀请置为接受
            if (uids != null) {
                for (String uid : uids) {
                    invitationService.updateWaiteToAccept(teamId, uid);
                }
            }
        } else {
            List<TeamApplicant> taList = new ArrayList<TeamApplicant>();
            for (int i = 0; i < uids.length; i++) {
                taList.add(TeamApplicant.build(uids[i], teamId, status, null, false));
            }
            teamApplicantService.batchAudit(taList);
        }
    }

    private boolean updateTeamMember(int teamId, String[] uids, String[] unames, String[] auths,
                                     String status) {
        if (TeamApplicant.STATUS_ACCEPT.equals(status)) {
            // Service方法内部处理了用户是否已加入团队的问题
            teamService.addTeamMembers(teamId, uids, unames, auths);
        } else if (TeamApplicant.STATUS_REJECT.equals(status) || TeamApplicant.STATUS_WAITING.equals(status)) {
            teamService.removeMembers(teamId, uids,false);
        } else {
            return false;
        }
        return true;
    }

    private JsonObject getJSONResponse(boolean status, String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("status", status);
        obj.addProperty("message", message);
        return obj;
    }

    private String getUIDString(String[] uids) {
        StringBuilder result = new StringBuilder();
        if (null != uids && uids.length > 0) {
            for (String uid : uids) {
                result.append(uid + ",");
            }
            result.replace(result.lastIndexOf(","), result.length(), "");
        }
        return result.toString();
    }

    private String[] getWaitingUserID(List<TeamApplicantRender> tar) {
        if (null == tar || tar.isEmpty()) {
            return null;
        }
        int size = tar.size();
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            TeamApplicantRender tarItem = tar.get(i);
            result[i] = tarItem.getTeamApplicant().getUid();
        }
        return result;
    }

    /**
     * 验证发送邀请团队成员邮件的数量
     * @param sender
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean inviterMailCount(String sender, int sendAmount, VWBContext context){
        VWBContainer container = context.getContainer();
        Integer [] arr = parseMailParameters(container.getProperty("ddl.team.email.inviter"));
        int maxAmount = arr[0];
        int seconds = arr[1];
        if(sendAmount>maxAmount){
            return false;
        }

        String key = "TeamInvite_" + sender;
        Map<String,Object> obj = (HashMap<String,Object>)cacheService.get(key);
        long currentTime = (new Date()).getTime();
        if(obj == null){
            obj = new HashMap<String,Object>();
            obj.put("amount", 0);
            obj.put("lastTime", 0L);
        }
        Integer amount = (Integer)obj.get("amount");
        Integer newAmount = amount+sendAmount;
        obj.put("amount", newAmount);
        Long lastTime = (Long)obj.get("lastTime");
        if((currentTime - lastTime) <= seconds ){
            if(newAmount > maxAmount){
                return false;
            }
        }else{
            obj.put("amount", sendAmount);
            obj.put("lastTime", currentTime);
        }
        cacheService.set(key, obj);
        return true;
    }

    /**
     * 验证发送给每个被邀请者邮件数量
     * @param sender
     * @param sendAmount
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean inviteeMailCount(String sender, String receiver, VWBContext context){
        VWBContainer container = context.getContainer();
        Integer [] arr = parseMailParameters(container.getProperty("ddl.team.email.invitee"));
        int maxAmount = arr[0];
        int seconds = arr[1];

        String key = "TeamInvite_" + sender + "_" + receiver;
        Map<String,Object> obj = (HashMap<String,Object>)cacheService.get(key);
        long currentTime = (new Date()).getTime();
        if(obj == null){
            obj = new HashMap<String,Object>();
            obj.put("amount", 0);
            obj.put("lastTime", 0L);
        }
        Integer amount = (Integer)obj.get("amount");
        Integer newAmount = amount+1;
        obj.put("amount", newAmount);
        Long lastTime = (Long)obj.get("lastTime");
        if((currentTime - lastTime) <= seconds ){
            if(newAmount > maxAmount){
                return false;
            }
        }else{
            obj.put("amount", 1);
            obj.put("lastTime", currentTime);
        }
        cacheService.set(key, obj);
        return true;
    }

    /**
     * 验证是否是真实的申请者
     * @param uids
     * @param teamId
     * @return
     */
    private boolean checkApplicant(String[] uids, int teamId){
        if(uids==null || uids.length==0){
            return false;
        }
        List<TeamApplicantRender> list = teamApplicantService.getWaitingApplicantOfTeam(teamId);
        Map<String,String> applicantMap = new HashMap<String,String>();
        for(TeamApplicantRender item : list){
            applicantMap.put(item.getTeamApplicant().getUid(), null);
        }
        boolean result = true;
        for(String item : uids){
            if(!applicantMap.containsKey(item)){
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 解析邮件发送参数配置
     * @param config
     * @return
     */
    private Integer [] parseMailParameters(String config){
        Integer [] result = new Integer[2];
        String [] paramArr = config.split("/");
        result[0] = Integer.parseInt(paramArr[0]);
        result[1] = Integer.parseInt(paramArr[1]);
        return result;
    }
}
