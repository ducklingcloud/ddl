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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.contact.ContactExt;
import net.duckling.ddl.service.contact.ContactUtil;
import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.mail.notice.AbstractNoticeHelper;
import net.duckling.ddl.service.mail.notice.CompositeNotice;
import net.duckling.ddl.service.mail.notice.CompositeNoticeHelper;
import net.duckling.ddl.service.mail.notice.DailyCompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.service.mail.notice.DailyNoticeHelper;
import net.duckling.ddl.service.mail.notice.GroupNotice;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.web.AbstractSpaceController;
import net.duckling.ddl.web.bean.ContactsExportUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("/{teamCode}/notice")
@RequirePermission(target = "team", operation = "view")
public class TeamSpaceController extends AbstractSpaceController{
	@Autowired
	private AuthorityService authorityService;
	@Autowired
	private INoticeService noticeService;
	@Autowired
	private SubscriptionServiceImpl subscriptionService;
	@Autowired
	private TeamMemberService teamMemberService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
	@Autowired
	private TeamService teamService;

	private CompositeNotice[] convertToCompositeNoticeArray(
			GroupNotice[] tempGroup) {
		CompositeNotice[] newArray = new CompositeNotice[tempGroup.length];
		for (int j = 0; j < tempGroup.length; j++) {
			newArray[j] = (CompositeNotice) tempGroup[j];
		}
		return newArray;
	}

	private DailyNotice[] convertToDailyNoticeArray(GroupNotice[] dailyGroup) {
		DailyNotice[] results = new DailyNotice[dailyGroup.length];
		for (int i = 0; i < dailyGroup.length; i++) {
			results[i] = (DailyNotice) dailyGroup[i];
		}
		return results;
	}

	private ModelAndView createModelAndView(int tid, String uid,
			VWBContext context, String func) {
		String teamAcl = authorityService.getTeamAuthority(tid, uid);
		ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
		"/jsp/aone/team/singleTeamHome.jsp");
		mv.addObject("teamAcl", teamAcl);
		mv.addObject("teamId", tid);
		mv.addObject("currTab", func);
		mv.addObject("pageType", "singleTeam");
		mv.addObject("currentUserName", context.getCurrentUserName());
		return mv;
	}
	private ModelAndView createContactsModelAndView(int tid, String uid,
			VWBContext context, String func) {
		String teamAcl = authorityService.getTeamAuthority(tid, uid);
		ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
		"/jsp/aone/team/teamContacts.jsp");
		mv.addObject("teamAcl", teamAcl);
		mv.addObject("teamId", tid);
		mv.addObject("currTab", func);
		mv.addObject("currentUserName", context.getCurrentUserName());
		return mv;
	}

	private List<DailyCompositeNotice> getDailyCompositeList(
			DailyNotice[] dailyGroup) {
		AbstractNoticeHelper compositeGrouper = new CompositeNoticeHelper();
		List<DailyCompositeNotice> results = new ArrayList<DailyCompositeNotice>();
		for (int i = 0; i < dailyGroup.length; i++) {
			results.add(wrapDailyCompositeNotice(dailyGroup[i],
					compositeGrouper));
		}
		return results;
	}

	private DailyNotice[] getDailyNoticeArray(List<Notice> source) {
		AbstractNoticeHelper dailyGrouper = new DailyNoticeHelper();
		GroupNotice[] dailyGroup = dailyGrouper.getCNoticeArray(source);
		return convertToDailyNoticeArray(dailyGroup);
	}

	private Date getLastAccess(TeamPreferences p, String type) {
		if (NoticeRule.TEAM_NOTICE.equals(type)) {
			return p.getTeamAccess();
		}
		if (NoticeRule.PERSON_NOTICE.equals(type)) {
			return p.getPersonAccess();
		}
		return p.getMonitorAccess();
	}


	private VWBContext getVWBContext(HttpServletRequest request) {
		return VWBContext.createContext(request, UrlPatterns.T_NOTICE);
	}

	private Map<Integer, DEntity> transferPageFeedListToMap(
			List<Subscription> pageFeedList) {
		Map<Integer, DEntity> result = new HashMap<Integer, DEntity>();
		if (pageFeedList == null) {
			return result;
		}
		for (Subscription s : pageFeedList) {
			DEntity dEntity = new DEntity();
			dEntity.setId(String.valueOf(s.getPublisher().getId()));
			dEntity.setName(s.getPublisher().getName());
			dEntity.setUrl(s.getPublisher().getUrl());
			dEntity.setType(LynxConstants.TYPE_PAGE);
			result.put(s.getPublisher().getId(), dEntity);
		}
		return result;
	}

	private void updateNoticeStatus(int tid, String uid,
			List<Notice> noticeList, String type) {
		TeamPreferences pref = teamPreferenceService.getTeamPreferences(uid, tid);
		Date lastAccess = getLastAccess(pref, type);
		for (Notice n : noticeList) {
			if (n.getOccurTime().getTime() > lastAccess.getTime()) {
				n.setNoticeStatus("new");
			} else {
				n.setNoticeStatus("old");
			}
		}
	}

	private DailyCompositeNotice wrapDailyCompositeNotice(DailyNotice dn,
			AbstractNoticeHelper compositeGrouper) {
		DailyCompositeNotice temp = new DailyCompositeNotice();
		temp.setDate(dn.getDate());
		GroupNotice[] tempGroup = compositeGrouper.getCNoticeArray(dn
				.getRecords());
		temp.setCompositeArray(convertToCompositeNoticeArray(tempGroup));
		return temp;
	}
	@WebLog(method = "adminSubs")
	@RequestMapping(params = "func=adminSubs")
	public ModelAndView adminSubs(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		Team instance = teamService.getTeamByName(teamCode);
		String uid = context.getCurrentUID();
		if (instance != null) {
			int tid = instance.getId();
			ModelAndView mv = createModelAndView(tid, uid, context, "adminSubs");
			List<Subscription> pageFeedList = subscriptionService
					.getSubscriptionByUserId(tid, uid, "page");
			List<Subscription> personFeedList = subscriptionService
					.getSubscriptionByUserId(tid, uid, "person");
			mv.addObject("pageFeedList", pageFeedList);
			mv.addObject("personFeedList", personFeedList);
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	@WebLog(method = "contacts")
	@RequestMapping(params = "func=contacts")
	public ModelAndView contacts(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		Team instance = teamService.getTeamByName(teamCode);
		String uid = context.getCurrentUID();
		if (instance != null) {
			int tid = instance.getId();
			ModelAndView mv = createContactsModelAndView(tid, uid, context, "contacts");
			List<String> feedTeamMembers = subscriptionService
					.getTeamMemberFeedList(uid, tid);
			List<UserExt> contacts = teamMemberService.getTeamContacts(tid);
			List<Boolean> isFeedList = new ArrayList<Boolean>();
			List<TeamAcl> admins = authorityService.getTeamAdminByTid(tid);
			if (contacts != null) {
				for (UserExt m : contacts) {
					isFeedList.add(feedTeamMembers.contains(m.getUid()));
				}
			}
			Map<String, TeamAcl> adminUids = new HashMap<String, TeamAcl>();
			for (TeamAcl a : admins) {
				adminUids.put(a.getUid(), a);
			}
			mv.addObject("adminUids", adminUids);
			mv.addObject("isFeedList", isFeedList);
			mv.addObject("contacts", contacts);
			mv.addObject("pageType", "contacts");
			mv.addObject(LynxConstants.PAGE_TITLE, "通讯录");
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	@WebLog(method = "exportTeamContacts")
	@RequestMapping(params = "func=exportTeamContacts")
	public void exportTeamContacts(HttpServletRequest request,
			HttpServletResponse response) {
		String contactList = request.getParameter("contactList");
		String str = contactList.replace("\"", "");
		String teamIdStr = str.substring(str.indexOf("tids") + 6,
				str.lastIndexOf(']'));
		int curTid = VWBContext.getCurrentTid();
		List<ContactExt> contacts = new ArrayList<ContactExt>();

		if (teamIdStr != null && !"".equals(teamIdStr)) {
			String[] teamIds = teamIdStr.split(",");
			List<UserExt> teamContacts = teamMemberService
					.getTeamContacts(curTid);
			Iterator<UserExt> teamItr = teamContacts.iterator();
			List<UserExt> exportTeamContacts = new ArrayList<UserExt>();
			while (teamItr.hasNext()) {// 提取选中的teamIds中的通讯录信息
				UserExt ue = teamItr.next();
				for (String id : teamIds) {
					if (ue.getId() == Integer.parseInt(id)) {
						exportTeamContacts.add(ue);
					}
				}
			}
			Iterator<UserExt> ueItr = exportTeamContacts.iterator();
			while (ueItr.hasNext()) {
				contacts.add(ContactUtil.convertToContactExt(ueItr.next()));
			}
		}

		String fileStr = ContactsExportUtil.convert2CSVString(contacts);
		String fileName = "团队通讯录.csv";
		ContactsExportUtil.download(request, response, fileStr, fileName);
	}
	@WebLog(method = "historyNotice")
	@RequestMapping(params = "func=historyNotice")
	public ModelAndView historyNotice(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		Team instance = teamService.getTeamByName(teamCode);
		String uid = context.getCurrentUID();
		if (instance != null) {
			int tid = instance.getId();
			ModelAndView mv = createModelAndView(tid, uid, context, "historyNotice");
			AoneNoticeParam queryParam = new AoneNoticeParam(tid,NoticeRule.HISTORY_NOTICE, uid);
			List<Notice> teamNoticeList = noticeService.readNotification(
					queryParam, uid);
//			mv.addObject("resourceMap", getResourceMap(tid, teamNoticeList));
			mv.addObject("historyNoticeList",
					getDailyNoticeArray(teamNoticeList));
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}

	@RequestMapping
	public ModelAndView init(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		return teamNotice(request, response, teamCode);
	}
	@WebLog(method = "monitorNotice")
	@RequestMapping(params = "func=monitorNotice")
	public ModelAndView monitorNotice(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		Team instance = teamService.getTeamByName(teamCode);
		String uid = context.getCurrentUID();
		if (instance != null) {
			int tid = instance.getId();
			ModelAndView mv = createModelAndView(tid, uid, context, "monitorNotice");
			AoneNoticeParam queryParam = new AoneNoticeParam(tid,NoticeRule.MONITOR_NOTICE, uid);
			List<Notice> teamNoticeList = noticeService.readNotification(
					queryParam, context.getCurrentUID());
			updateNoticeStatus(tid, uid, teamNoticeList,
					NoticeRule.MONITOR_NOTICE);
			mv.addObject("monitorNoticeList",
					getDailyNoticeArray(teamNoticeList));
			mv.addObject("resourceMap", getResourceMap(tid, teamNoticeList));
			List<Subscription> pageFeedList = subscriptionService
					.getSubscriptionByUserId(queryParam.getTid(), uid, "page");
			List<Subscription> personFeedList = subscriptionService
					.getSubscriptionByUserId(queryParam.getTid(), uid, "person");
			mv.addObject("pageFeedList", pageFeedList);
			mv.addObject("pageFeedDEntityMap",
					transferPageFeedListToMap(pageFeedList));
			mv.addObject("personFeedList", personFeedList);
			teamPreferenceService.updateNoticeAccessTime(uid, tid,
					NoticeRule.MONITOR_NOTICE);
			mv.addObject(LynxConstants.PAGE_TITLE, "关注");
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	@WebLog(method = "personNotice")
	@RequestMapping(params = "func=personNotice")
	public ModelAndView personNotice(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		Team instance = teamService.getTeamByName(teamCode);
		String uid = context.getCurrentUID();
		if (instance != null) {
			int tid = instance.getId();
			ModelAndView mv = createModelAndView(tid, uid, context, "personNotice");
			AoneNoticeParam queryParam = new AoneNoticeParam(tid,NoticeRule.PERSON_NOTICE, uid);
			List<Notice> teamNoticeList = noticeService.readNotification(
					queryParam, uid);
			updateNoticeStatus(tid, uid, teamNoticeList,
					NoticeRule.PERSON_NOTICE);
			mv.addObject("personNoticeList",
					getDailyNoticeArray(teamNoticeList));
			mv.addObject("resourceMap", getResourceMap(tid, teamNoticeList));
			teamPreferenceService.updateNoticeAccessTime(uid, tid, NoticeRule.PERSON_NOTICE);
			mv.addObject(LynxConstants.PAGE_TITLE, "消息");
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	@WebLog(method = "teamNotice")
	@RequestMapping(params = "func=teamNotice")
	public ModelAndView teamNotice(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		Team instance = teamService.getTeamByName(teamCode);
		String uid = context.getCurrentUID();
		if (instance != null) {
			int tid = instance.getId();
			ModelAndView mv = createModelAndView(tid, uid, context, "teamNotice");
			AoneNoticeParam queryParam = new AoneNoticeParam(tid,NoticeRule.TEAM_NOTICE, tid+"");
			List<Notice> teamNoticeList = noticeService.readNotification(
					queryParam, uid);
			updateNoticeStatus(tid, uid, teamNoticeList, NoticeRule.TEAM_NOTICE);
			DailyNotice[] dailyGroup = getDailyNoticeArray(teamNoticeList);
			List<DailyCompositeNotice> results = getDailyCompositeList(dailyGroup);
			mv.addObject("teamNoticeList", results);
			mv.addObject("resourceMap", getResourceMap(tid, teamNoticeList));
			mv.addObject("resourceRidKeyMap", getResourceRidKeyMap(context, teamNoticeList));
			teamPreferenceService.updateNoticeAccessTime(uid, tid, NoticeRule.TEAM_NOTICE);
			mv.addObject(LynxConstants.PAGE_TITLE, "更新");
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}
	
	/*@RequestMapping(params = "func=historyNotice")
	public ModelAndView opHistory(HttpServletRequest request,HttpServletResponse response,@PathVariable("teamCode") String teamCode) throws IOException {
		VWBContext context = getVWBContext(request);
		int tid=VWBContext.getCurrentTid();
		String uid = context.getCurrentUID();
		if (tid>0&&!StringUtils.isBlank(uid)) {
			ModelAndView mv = createModelAndView(tid, uid, context, "teamNotice");
			AoneNoticeParam queryParam = new AoneNoticeParam(tid,NoticeRule.HISTORY_NOTICE, tid+"");
			List<Notice> teamNoticeList = noticeService.readNotification(
					queryParam, uid);
			updateNoticeStatus(tid, uid, teamNoticeList, NoticeRule.HISTORY_NOTICE);
			DailyNotice[] dailyGroup = getDailyNoticeArray(teamNoticeList);
			List<DailyCompositeNotice> results = getDailyCompositeList(dailyGroup);
			mv.addObject("historyNoticeList", results);
			mv.addObject("resourceMap", getResourceMap(context, teamNoticeList));
			teamPreferenceService.updateNoticeAccessTime(uid, tid, NoticeRule.HISTORY_NOTICE);
			mv.addObject(LynxConstants.PAGE_TITLE, "历史");
			return mv;
		} else {
			request.setAttribute("accessMain", Boolean.TRUE);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}*/

}
