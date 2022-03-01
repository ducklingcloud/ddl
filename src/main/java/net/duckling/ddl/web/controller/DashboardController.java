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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.UserTeamAclBean;
import net.duckling.ddl.service.contact.Contact;
import net.duckling.ddl.service.contact.ContactConstants;
import net.duckling.ddl.service.contact.ContactExt;
import net.duckling.ddl.service.contact.ContactUtil;
import net.duckling.ddl.service.contact.ContactsService;
import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.invitation.Invitation;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamApplicant;
import net.duckling.ddl.service.team.TeamApplicantNoticeRender;
import net.duckling.ddl.service.team.TeamApplicantService;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.service.user.UserPreferences;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.AbstractSpaceController;
import net.duckling.ddl.web.bean.SimpleResourceKey;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/dashboard")
@RequirePermission(authenticated = true)
public class DashboardController extends AbstractSpaceController {
    public static class NoticeEmailSetting {
        private int tid;
        private String teamName;
        private boolean isAll;
        private boolean isShare;

        public boolean getIsAll() {
            return isAll;
        }

        public boolean getIsShare() {
            return isShare;
        }

        public void setIsAll(boolean isAll) {
            this.isAll = isAll;
        }

        public void setIsShare(boolean isShare) {
            this.isShare = isShare;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }


    }

    private static final Logger LOG = Logger
            .getLogger(DashboardController.class);
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private INoticeService noticeService;
    @Autowired
    private IParamService paramService;

    @Autowired
    private SubscriptionServiceImpl subscriptionService;
    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private TeamApplicantService teamApplicantService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private ContactsService contactsService;
    @Autowired
    private URLGenerator urlGenerator;

    private void addInvitationData(String uid, VWBContext context,
                                   ModelAndView mv) {
        List<Invitation> invitList = invitationService.getInvitationListByUser(uid);
        for (Invitation i : invitList) {
            i.setDisplayURL(EncodeUtil.getDisplayURL(i));
        }
        String changePassURL = context.getContainer().getProperty(
            "duckling.umt.change.password");
        mv.addObject("changePasswordURL", changePassURL);
        mv.addObject("invites", invitList);
        mv.addObject("inviteCount", invitList.size());
    }

    private List<AoneNoticeParam> buildNoticeParamByPref(
        List<TeamPreferences> prefList, String type) {
        List<AoneNoticeParam> paramList = new ArrayList<AoneNoticeParam>();
        for (TeamPreferences pref : prefList) {
            AoneNoticeParam p = null;
            if (type.equals(NoticeRule.TEAM_NOTICE)
                && pref.getTeamNoticeCount() != 0) {
                int tid = pref.getTid();
                p = new AoneNoticeParam(tid,NoticeRule.TEAM_NOTICE, tid+"");
                p.setBeginDate(pref.getTeamAccess());
                p.setEventIds(pref.getTeamEventIdsSet());
            } else if (type.equals(NoticeRule.PERSON_NOTICE)
                       && pref.getPersonNoticeCount() != 0) {
                p = new AoneNoticeParam(pref.getTid(),NoticeRule.PERSON_NOTICE, pref.getUid());
                p.setBeginDate(pref.getPersonAccess());
                p.setEventIds(pref.getPersonEventIdsSet());
            } else if (type.equals(NoticeRule.MONITOR_NOTICE)
                       && pref.getMonitorNoticeCount() != 0) {
                p = new AoneNoticeParam(pref.getTid(),NoticeRule.MONITOR_NOTICE, pref.getUid());
                p.setBeginDate(pref.getMonitorAccess());
                p.setEventIds(pref.getMonitorEventIdsSet());
            } else if (type.equals(NoticeRule.HISTORY_NOTICE)) {
                p = new AoneNoticeParam(pref.getTid(),NoticeRule.HISTORY_NOTICE, pref.getUid());
            }
            if (p != null) {
                paramList.add(p);
            }
        }
        return paramList;
    }

    private String chooseTypeByFunc(String func) {
        if ("teamNotice".equals(func)) {
            return NoticeRule.TEAM_NOTICE;
        }
        if ("personNotice".equals(func)) {
            return NoticeRule.PERSON_NOTICE;
        }
        if ("monitorNotice".equals(func)) {
            return NoticeRule.MONITOR_NOTICE;
        }
        return NoticeRule.HISTORY_NOTICE;
    }

    private ModelAndView createModelAndView(int tid, String uid,
                                            VWBContext context, String func) {
        String teamAcl = authorityService.getTeamAuthority(tid, uid);
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                 "/jsp/aone/team/multipleTeamHome.jsp");
        mv.addObject("teamAcl", teamAcl);
        mv.addObject("teamId", tid);
        mv.addObject("currTab", func);
        mv.addObject("currentUserName", context.getCurrentUserName());

        return mv;
    }

    private void displayContactsByName(VWBContext context, ModelAndView mv) {
        mv.addObject("listMode", "byName");
        String user = context.getCurrentUID();
        mv.addObject("uid", user);
        // 取个人通讯录
        List<Contact> userContacts = contactsService.getUserContactsByUid(user);
        // 取团队通讯录
        Set<UserExt> teamContacts = new HashSet<UserExt>();
        List<Team> teamList = teamService.getAllUserTeams(user);
        Iterator<Team> teams = teamList.iterator();
        while (teams.hasNext()) {
            Team team = teams.next();
            if (team.getId() != 1 && !team.getName().equals("cerc")) {
                List<UserExt> teamMembers = teamMemberService.getTeamContacts(team.getId());
                Iterator<UserExt> it = teamMembers.iterator();
                while (it.hasNext()) {
                    teamContacts.add(it.next());
                }
            }
        }
        // 合并到一起，并排序
        List<ContactExt> contacts = new LinkedList<ContactExt>();
        Iterator<Contact> userIt = userContacts.iterator();
        while (userIt.hasNext()) {
            ContactExt ce = ContactUtil.convertToContactExt(userIt.next());
            contacts.add(ce);
        }
        Iterator<UserExt> teamIt = teamContacts.iterator();
        while (teamIt.hasNext()) {
            ContactExt ce = ContactUtil.convertToContactExt(teamIt.next());
            contacts.add(ce);
        }
        // jdk 7 sort有可能报错，所以加上下面一句。
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(contacts);
        Iterator<ContactExt> it = contacts.iterator();
        int fromIndex = 1;
        int size = contacts.size();
        while (it.hasNext() && fromIndex < size) {
            ContactExt ce = it.next();
            List<ContactExt> subList = contacts.subList(fromIndex, size);
            Iterator<ContactExt> subit = subList.iterator();
            while (subit.hasNext()) {
                ContactExt ceSub = subit.next();
                if (ce.getName().equals(ceSub.getName())) {
                    ce.setTag(ce.getTag() | ContactConstants.NAME_COLLISION);
                    ceSub.setTag(ceSub.getTag()
                                 | ContactConstants.NAME_COLLISION);
                }
                if (StringUtils.isNotEmpty(ce.getMainEmail())
                    && ce.getMainEmail().equals(ceSub.getMainEmail())) {
                    ce.setTag(ce.getTag() | ContactConstants.EMAIL_COLLISION);
                    ceSub.setTag(ceSub.getTag()
                                 | ContactConstants.EMAIL_COLLISION);
                }
            }
            fromIndex++;
        }
        mv.addObject("contacts", contacts);
    }

    private void displayContactsByTeam(VWBContext context, ModelAndView mv) {
        mv.addObject("listMode", "byTeam");
        String user = context.getVWBSession().getCurrentUser().getName();
        mv.addObject("uid", user);
        // 取个人通讯录
        List<Contact> userContacts = contactsService.getUserContactsByUid(user);
        // 取团队通讯录
        Map<String, List<UserExt>> teamContacts = new HashMap<String, List<UserExt>>();
        Map<String, String> teamNames = new HashMap<String, String>();
        List<Team> teamList = teamService.getAllUserTeams(user);
        Iterator<Team> teams = teamList.iterator();
        while (teams.hasNext()) {
            Team team = teams.next();
            if (team.getId() != 1) {
                List<UserExt> teamMembers =teamMemberService.getTeamContacts(team.getId());
                // 添加拼音处理
                teamContacts.put(team.getName(), teamMembers);
                teamNames.put(team.getName(), team.getDisplayName());
            }
        }
        mv.addObject("userContacts", userContacts);
        mv.addObject("teamContacts", teamContacts);
        mv.addObject("teamNames", teamNames);
    }

    private boolean generateApplicantMessage(
        List<TeamApplicantNoticeRender> tanr, StringBuilder waiting,
        StringBuilder reject) {
        boolean isEmpty = true;
        if (null != tanr && !tanr.isEmpty()) {
            for (TeamApplicantNoticeRender tanrItem : tanr) {
                String status = tanrItem.getTeamApplicant().getStatus();
                if (TeamApplicant.STATUS_WAITING.equals(status)) {
                    waiting.append(tanrItem.getTeamName() + ",");
                    isEmpty = false;
                } else if (TeamApplicant.STATUS_REJECT.equals(status)) {
                    reject.append(tanrItem.getTeamName() + ",");
                    isEmpty = false;
                } else {
                    LOG.info("包含不需要的申请状态");
                }
            }
            if (waiting.length() > 0) {
                waiting.replace(waiting.lastIndexOf(","), waiting.length(), "");
            }
            if (reject.length() > 0) {
                reject.replace(reject.lastIndexOf(","), reject.length(), "");
            }
        }
        return isEmpty;
    }

    private Map<Integer, List<Subscription>> getFeedMap(String uid,
                                                        SubscriptionServiceImpl subscriptionService,
                                                        Map<Integer, Team> teamMap, VWBContext context, String type) {
        Map<Integer, List<Subscription>> feedMap = new HashMap<Integer, List<Subscription>>();
        for (Team team : teamMap.values()) {
            List<Subscription> feedList = subscriptionService
                    .getSubscriptionByUserId(team.getId(), uid, type);
            feedMap.put(team.getId(), feedList);
        }
        return feedMap;
    }

    private String getFuncString(HttpServletRequest request) {
        String defaultTab = "teamNotice";
        return (request.getParameter("func") != null) ? request
                .getParameter("func") : defaultTab;
    }

    private Map<Integer, Team> getNoticeByFunc(VWBContext context,
                                               ModelAndView mv, String func) {
        String uid = context.getCurrentUID();
        String type = chooseTypeByFunc(func);
        List<TeamPreferences> prefList = teamService.getTeamPrefWithoutPersonSpace(uid);
        List<AoneNoticeParam> params = buildNoticeParamByPref(prefList, type);
        Map<Integer, List<Notice>> map = getNoticeMap(params, context);
        mv.addObject(type + "NoticeMap", map);
        Map<Integer, Team> teamMap = getTeamMap(prefList, context);
        mv.addObject("TeamMap", teamMap);
        List<TeamApplicantNoticeRender> applicantNotice = teamApplicantService.getTeamApplicantNoticeInotKnow(uid);
        StringBuilder waiting = new StringBuilder();
        StringBuilder reject = new StringBuilder();
        boolean isEmpty = generateApplicantMessage(applicantNotice, waiting,
                                                   reject);
        mv.addObject("applicantMessage", !isEmpty);
        mv.addObject("resourceMapMap", mapToMap(map, context.getHttpRequest()));
        mv.addObject("waitingApplicants", waiting.toString());
        mv.addObject("rejectApplicants", reject.toString());
        List<Map<String,Object>> array =
                getWaitingAndRejectTeam(uid, context, teamMap);
        mv.addObject("applicantTeams", array);
        mv.addObject("userAdminTeam", getUserAdminTeam(uid, teamMemberService));
        mv.addObject("teamCreatorInfos", getTeamCreator(prefList));
        return teamMap;
    }

    private Map<Integer, List<Notice>> getNoticeMap(
        List<AoneNoticeParam> paramList, VWBContext context) {
        Map<Integer, List<Notice>> noticeMap = new LinkedHashMap<Integer, List<Notice>>();
        for (AoneNoticeParam param : paramList) {
            noticeMap.put(
                param.getTid(),
                noticeService.getLastestNotices(param,
                                                context.getCurrentUID()));
        }
        return noticeMap;
    }

    private Map<Integer, SimpleUser> getTeamCreator(
        List<TeamPreferences> prefList) {
        Map<Integer, SimpleUser> result = new HashMap<Integer, SimpleUser>();
        for (TeamPreferences pref : prefList) {
            int tid = pref.getTid();
            Team t = teamService.getTeamByID(tid);
            if (StringUtils.isNotEmpty(t.getCreator())) {
                SimpleUser user = aoneUserService.getSimpleUserByUid(t
                                                                     .getCreator());
                result.put(tid, user);
            }
        }
        return result;
    }

    private Map<Integer, Team> getTeamMap(List<TeamPreferences> prefList,
                                          VWBContext context) {
        Map<Integer, Team> teamMap = new LinkedHashMap<Integer, Team>();
        for (TeamPreferences pref : prefList) {
            teamMap.put(pref.getTid(), teamService.getTeamByID(pref.getTid()));
        }
        return teamMap;
    }

    private Map<Integer, Integer> getUserAdminTeam(String uid,
                                                   TeamMemberService tm) {
        List<UserTeamAclBean> b = authorityService.getTeamAclByUidAndAuth(uid,
                                                                          Team.AUTH_ADMIN);
        Map<Integer, Integer> r = new HashMap<Integer, Integer>();
        if (b != null) {
            for (UserTeamAclBean bean : b) {
                r.put(bean.getTid(), bean.getTid());
            }
        }
        return r;
    }

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request, UrlPatterns.DASHBOARD);
    }

    /**
     * 过滤出待审核和被拒绝的团队，若待审核团队中有加入的团队，则剔除
     *
     * @param context
     * @param teamMap
     * @return
     */
    private List<Map<String,Object>> getWaitingAndRejectTeam(
        String uid, VWBContext context, Map<Integer, Team> teamMap) {
        List<TeamApplicant> applicantTeams =teamApplicantService.getUserApplicant(uid);
        List<Map<String,Object>> array = new ArrayList<Map<String,Object>>();
        if (null != applicantTeams && !applicantTeams.isEmpty()) {
            for (TeamApplicant ta : applicantTeams) {
                Integer tid = Integer.valueOf(ta.getTid());
                if (!teamMap.containsKey(tid)) {// 已加入的团队中不包含申请的团队
                    Team team = teamService.getTeamByID(tid);
                    Map<String,Object> obj = new HashMap<String,Object>();
                    obj.put("id", ta.getTid());
                    obj.put("displayName", team.getDisplayName());
                    obj.put("accessType", team.getAccessType());
                    obj.put("status", ta.getStatus());
                    array.add(obj);
                }
            }
        }
        return array;
    }

    private void isExistThenCreateElseUpdateParam(IParamService service,
                                                  String type, String itemId, String keys[], String value) {
        if (keys == null) {
            return;
        }
        for (String key : keys) {
            isExistThenCreateElseUpdateParam(service, type, itemId, key, value);
        }
    }

    private void isExistThenCreateElseUpdateParam(IParamService service,
                                                  String type, String itemId, String key, String value) {
        Param param = service.get(type, key, itemId);
        if (param == null) {
            param = new Param();
            param.setItemId(itemId);
            param.setType(type);
            param.setKey(key);
            param.setValue(value);
            service.addParam(param);
        } else {
            if (value != null && !value.equals(param.getValue())) {
                param.setValue(value);
                service.updateParam(param);
            }
        }
    }

    private boolean isNoticeEmailParamChecked(Param p) {
        return p != null
                && ParamConstants.NoticeEmailShareType.VALUE_CHECKED.equals(p
                                                                            .getValue());
    }

    private Map<Integer, Map<SimpleResourceKey, Resource>> mapToMap(
        Map<Integer, List<Notice>> map, HttpServletRequest request) {
        Map<Integer, Map<SimpleResourceKey, Resource>> n = new HashMap<Integer, Map<SimpleResourceKey, Resource>>();
        if (map == null) {
            return n;
        }
        for (Entry<Integer, List<Notice>> ln : map.entrySet()) {
            Map<SimpleResourceKey, Resource> r = getResourceMap(ln.getKey(),
                                                                ln.getValue());
            n.put(ln.getKey(), r);
        }
        return n;
    }

    private boolean needRefresh(Object[] a1, Object[] a2, Object[] a3,
                                Object[] a4) {
        return (CommonUtils.isNull(a1) && CommonUtils.isNull(a3))
                || (CommonUtils.isNull(a2) && CommonUtils.isNull(a4));
    }

    @RequestMapping(params = "func=capture")
    public ModelAndView capture(HttpServletRequest request,
                                HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        mv.addObject("baseURL", context.getBaseURL());
        mv.addObject("currTab", "capture");
        mv.addObject(LynxConstants.PAGE_TITLE, "网页收藏");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=contacts")
    public ModelAndView contacts(HttpServletRequest request,
                                 HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        String type = context.getHttpRequest().getParameter("type");
        if ("team".equals(type)) {
            displayContactsByTeam(context, mv);
        } else {
            displayContactsByName(context, mv);
        }
        mv.addObject(LynxConstants.PAGE_TITLE, "我的通讯录");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=decreaseScore")
    public void decreaseScore(HttpServletRequest request,
                              HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.DASHBOARD);
        String uid = context.getCurrentUID();
        List<TeamPreferences> prefs = teamPreferenceService.getAllTeamPrefs(uid);
        for (TeamPreferences pref : prefs) {
            gridService.decreaseItemScore(uid, pref.getTid());
        }
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        JsonUtil.write(response, json);
    }

    @RequestMapping(params = "func=historyNotice")
    public ModelAndView historyNotice(HttpServletRequest request,
                                      HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        getNoticeByFunc(context, mv, func);
        mv.addObject(LynxConstants.PAGE_TITLE, "我的活动");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping
    public ModelAndView init(HttpServletRequest request,
                             HttpServletResponse response) {
        return teamNotice(request, response);
    }

    @RequestMapping(params = "func=monitorNotice")
    public ModelAndView monitorNotice(HttpServletRequest request,
                                      HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        Map<Integer, Team> teamMap = getNoticeByFunc(context, mv, func);
        mv.addObject("pageFeedMap",
                     getFeedMap(uid, subscriptionService, teamMap, context, "page"));
        mv.addObject(
            "personFeedMap",
            getFeedMap(uid, subscriptionService, teamMap, context, "person"));
        mv.addObject(LynxConstants.PAGE_TITLE, "我的关注");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=noticeEmail")
    public ModelAndView noticeEmail(HttpServletRequest request,
                                    HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        String uid1 = context.getCurrentUID();
        List<TeamPreferences> prefList = teamService.getTeamPrefWithoutPersonSpace(uid1);
        boolean allChecked = true;
        boolean allUnChecked = true;
        boolean allNull = true;
        List<NoticeEmailSetting> result = new ArrayList<NoticeEmailSetting>();
        for (TeamPreferences p : prefList) {
            Param noticeShare = paramService.get(
                ParamConstants.NoticeEmailShareType.TYPE, p.getTid() + "",
                uid1 + "");
            Param noticeAll = paramService.get(
                ParamConstants.NoticeEmailAllType.TYPE, p.getTid() + "",
                uid1 + "");
            NoticeEmailSetting setting = new NoticeEmailSetting();
            setting.setTid(p.getTid());
            setting.setTeamName(teamService.getTeamByID(p.getTid()).getDisplayName());
            setting.setIsShare(isNoticeEmailParamChecked(noticeShare));
            setting.setIsAll(isNoticeEmailParamChecked(noticeAll));
            allChecked &= setting.getIsShare() && setting.getIsAll();
            allUnChecked &= !setting.getIsShare() && !setting.getIsAll();
            allNull = noticeShare == null && noticeAll == null;
            result.add(setting);
        }
        mv.addObject("allChecked", allChecked);
        mv.addObject("allUnChecked", allUnChecked);
        mv.addObject("allNull", allNull);
        mv.addObject("result", result);
        mv.addObject(LynxConstants.PAGE_TITLE, "邮件通知");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=personNotice")
    public ModelAndView personNotice(HttpServletRequest request,
                                     HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        getNoticeByFunc(context, mv, func);
        mv.addObject(LynxConstants.PAGE_TITLE, "我的消息");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=userPreferences")
    public ModelAndView userPreferences(HttpServletRequest request) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        String refreshTeamMode = request.getParameter("refreshTeamMode");
        String accessHomeMode = null;
        int team = -1;
        if (null != refreshTeamMode) {
            UserPreferences userPre = null;
            if (UserPreferences.REFRESH_TEAM_MODE_AUTO.equals(refreshTeamMode)) {
                userPre = aoneUserService.getUserPreferences(uid);
                userPre.setDefaultTeam(1);// 切换到上次访问团队选项时,默认团队为tid=1的团队
                userPre.setRefreshTeamMode(refreshTeamMode);
            } else if (UserPreferences.REFRESH_TEAM_MODE_CONFIG
                       .equals(refreshTeamMode)) {
                String defaultTeam = request.getParameter("defaultTeam");
                accessHomeMode = request.getParameter("accessHomeMode");
                team = Integer.parseInt(defaultTeam);
                userPre = UserPreferences.build(uid, refreshTeamMode, team,
                                                accessHomeMode);
            } else {
                userPre = UserPreferences.buildDefault(uid);
            }
            aoneUserService.updateUserPreferences(userPre);
        }
        VWBContext.setCurrentTid(-1);
        return new ModelAndView(new RedirectView(urlGenerator.getURL(UrlPatterns.DASHBOARD,null, "func=preferences")));
    }

    @RequestMapping(params = "func=preferences")
    public ModelAndView preferences(HttpServletRequest request,
                                    HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        String uid1 = context.getCurrentUID();
        List<Team> teamList = teamService.getAllUserTeams(uid1);
        UserPreferences userPre = aoneUserService.getUserPreferences(uid1);
        if (null == userPre) {
            userPre = UserPreferences.buildDefault(uid1);
            aoneUserService.createUserPreferences(userPre);
        }
        mv.addObject("teamList", teamList);
        mv.addObject("userPreferences", userPre);
        if (userPre.getDefaultTeam() > 0) {
            Team team = teamService.getTeamByID(userPre.getDefaultTeam());
            mv.addObject("defaultTeamName", team.getDisplayName());
        }
        Param param = paramService.get(ParamConstants.UserPreferenceType.TYPE,
                                       ParamConstants.UserPreferenceType.KEY_NAME_TAG,
                                       context.getCurrentUID());
        mv.addObject("useNameTag", param == null ? "false" : param.getValue());
        mv.addObject(LynxConstants.PAGE_TITLE, "个人偏好");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=profile")
    public ModelAndView profile(HttpServletRequest request,
                                HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        VWBContainer container = context.getContainer();
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        String func = getFuncString(request);
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        UserExt instance = aoneUserService.getUserExtInfo(uid);
        if (instance != null) {
            String changePassURL = container
                    .getProperty("duckling.umt.change.password");
            mv.addObject("changePasswordURL", changePassURL);
            mv.addObject("user", instance);
            if (instance.getUid().equals(uid)) {
                mv.addObject("isMyself", true);
            }
        }
        mv.addObject(LynxConstants.PAGE_TITLE, "个人资料");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=resortTeam")
    @ResponseBody
    public int resort(HttpServletRequest request) {
        String ids[] = request.getParameterValues("resortedIds[]");
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.DASHBOARD);
        String uid = context.getCurrentUID();
        return teamMemberService.updateTeamsSequence(uid, ids);
    }

    @RequestMapping(params = "func=emailNoticeAll")
    public void submitEmailNoticeAll(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam("shareNotice") boolean shareNotice,
                                     @RequestParam("allNotice") boolean allNotice) {
        VWBContext context = getVWBContext(request);
        List<TeamPreferences> prefList = teamService
                .getTeamPrefWithoutPersonSpace(context.getCurrentUID());
        for (TeamPreferences p : prefList) {
            isExistThenCreateElseUpdateParam(
                paramService,
                ParamConstants.NoticeEmailAllType.TYPE,
                context.getCurrentUID() + "",
                p.getTid() + "",
                allNotice ? ParamConstants.NoticeEmailAllType.VALUE_CHECKED
                : ParamConstants.NoticeEmailAllType.VALUE_UN_CHECKED);
            isExistThenCreateElseUpdateParam(
                paramService,
                ParamConstants.NoticeEmailShareType.TYPE,
                context.getCurrentUID() + "",
                p.getTid() + "",
                shareNotice ? ParamConstants.NoticeEmailShareType.VALUE_CHECKED
                : ParamConstants.NoticeEmailShareType.VALUE_UN_CHECKED);
        }
        JsonUtil.write(response, true);
    }

    @RequestMapping(params = "func=emailNoticeDetail")
    public void submitEmailNoticeDetail(HttpServletRequest request,
                                        HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String[] allNoticeChecked = request
                .getParameterValues("allNoticeChecked[]");
        String[] allNoticeUnChecked = request
                .getParameterValues("allNoticeUnChecked[]");
        String[] shareNoticeChecked = request
                .getParameterValues("shareNoticeChecked[]");
        String[] shareNoticeUnChecked = request
                .getParameterValues("shareNoticeUnChecked[]");
        boolean needRefresh = needRefresh(allNoticeChecked, allNoticeUnChecked,
                                          shareNoticeChecked, shareNoticeUnChecked);
        isExistThenCreateElseUpdateParam(paramService,
                                         ParamConstants.NoticeEmailAllType.TYPE, context.getCurrentUID()
                                         + "", allNoticeChecked,
                                         ParamConstants.NoticeEmailAllType.VALUE_CHECKED);
        isExistThenCreateElseUpdateParam(paramService,
                                         ParamConstants.NoticeEmailAllType.TYPE, context.getCurrentUID()
                                         + "", allNoticeUnChecked,
                                         ParamConstants.NoticeEmailAllType.VALUE_UN_CHECKED);
        isExistThenCreateElseUpdateParam(paramService,
                                         ParamConstants.NoticeEmailShareType.TYPE,
                                         context.getCurrentUID() + "", shareNoticeChecked,
                                         ParamConstants.NoticeEmailShareType.VALUE_CHECKED);
        isExistThenCreateElseUpdateParam(paramService,
                                         ParamConstants.NoticeEmailShareType.TYPE,
                                         context.getCurrentUID() + "", shareNoticeUnChecked,
                                         ParamConstants.NoticeEmailShareType.VALUE_UN_CHECKED);
        JsonUtil.write(response, needRefresh);
    }

    @RequestMapping(params = "func=teamNotice")
    public ModelAndView teamNotice(HttpServletRequest request,
                                   HttpServletResponse response) {
        String func = "teamNotice";
        VWBContext context = getVWBContext(request);
        String uid = context.getCurrentUID();
        int tid = teamService
                .getPersonalTeam(uid, context.getCurrentUserName());
        ModelAndView mv = createModelAndView(tid, uid, context, func);
        getNoticeByFunc(context, mv, func);
        addInvitationData(uid, context, mv);
        mv.addObject(LynxConstants.PAGE_TITLE, "我的团队");
        mv.addObject("showDetailNoticeEmail",
                     request.getParameter("showDetailNoticeEmail"));
        return mv;
    }

    @RequestMapping(params = "func=useNameTag")
    public void useNameTag(HttpServletRequest request,
                           HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        String useNameTag = request.getParameter("useNameTag");
        if(!checkUserNameTag(useNameTag)){
            return;
        }
        useNameTag = useNameTag.toLowerCase();
        Param param = paramService.get(ParamConstants.UserPreferenceType.TYPE,
                                       ParamConstants.UserPreferenceType.KEY_NAME_TAG,
                                       context.getCurrentUID());
        if (param == null) {
            Param p = new Param();
            p.setItemId(context.getCurrentUID());
            p.setKey(ParamConstants.UserPreferenceType.KEY_NAME_TAG);
            p.setValue(useNameTag);
            p.setType(ParamConstants.UserPreferenceType.TYPE);
            paramService.addParam(p);
        } else {
            param.setValue(useNameTag);
            paramService.updateParam(param);
        }
        JsonUtil.write(response, useNameTag);
    }

    private boolean checkUserNameTag(String userNameTag){
        if(userNameTag==null) {
            return false;
        }
        if(ParamConstants.UserPreferenceType.VALUE_NAME_TAG_TRUE.equals(userNameTag.toLowerCase())||
           ParamConstants.UserPreferenceType.VALUE_NAME_TAG_FALSE.equals(userNameTag.toLowerCase())){
            return true;
        }
        return false;
    }
}
