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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.MessageServiceImpl;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.web.bean.CommentUpdater;
import net.duckling.ddl.web.bean.MessageDisplay;
import net.duckling.ddl.web.bean.MessageUpdater;
import net.duckling.ddl.web.bean.PersonUpdater;
import net.duckling.ddl.web.bean.RecommendUpdater;
import net.duckling.ddl.web.bean.SubscriptionUpdater;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @date 2011-3-3
 * @author Clive Lee
 */
@Controller
@RequestMapping("/{teamCode}/myspace")
@RequirePermission(authenticated = true)
public class PageBarController extends BaseController {

	private static final String VIEW_TEMPLATE = ELayout.LYNX_MAIN;
	@Autowired
	private MessageServiceImpl messageService;

	@Autowired
	private SubscriptionServiceImpl subscriptionService;

	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private URLGenerator urlGenerator;

	private VWBContext getVWBContext(HttpServletRequest request) {
		return VWBContext.createContext(request, UrlPatterns.MYSPACE);
	}

	private Map<String, SimpleUser> getAllSimpleUser(VWBContext context,
			List<Message> recommends) {
		Set<String> candidates = new HashSet<String>();
		for (Message instance : recommends) {
			candidates.add(instance.getBody().getFrom());
			candidates.add(instance.getUserId());
		}
		Map<String, SimpleUser> cacheUser = aoneUserService
				.getUserExtMap(candidates.toArray(new String[0]));
		return cacheUser;
	}

	private void updateMessageSize(HttpServletRequest request) {
		Object newMessageSize = request.getSession().getAttribute(
				"newMessageSize");
		if (newMessageSize != null) {
			int size = Integer.parseInt(request.getSession()
					.getAttribute("newMessageSize").toString()) - 1;
			request.getSession().setAttribute("newMessageSize", size);
		}
	}

	private MessageDisplay[] getUnreadMessages(List<Message> messages,
			MessageUpdater updater) {
		MessageDisplay[] allMessages = merge(messages, updater);
		List<MessageDisplay> filterList = new ArrayList<MessageDisplay>();
		for (MessageDisplay display : allMessages) {
			if (display.isNewMessage()) {
				filterList.add(display);
			}
		}
		return filterList.toArray(new MessageDisplay[0]);
	}

	private MessageDisplay[] merge(List<Message> input, MessageUpdater updater) {
		List<Message> messages = updater.beforeMerge(input);
		HashMap<Object, MessageDisplay> mergedMap = new HashMap<Object, MessageDisplay>();
		for (Message instance : messages) {
			saveToMap(mergedMap, instance, updater);
		}
		List<MessageDisplay> results = new ArrayList<MessageDisplay>();
		results.addAll(mergedMap.values());
		updater.afterMerge(results);
		return results.toArray(new MessageDisplay[0]);
	}

	private void saveToMap(Map<Object, MessageDisplay> mergedMap,
			Message message, MessageUpdater updater) {
		MessageDisplay currentDisplay;
		Object key = updater.getMessageKey(message);
		if (mergedMap.containsKey(key)) {
			// 已存在推荐这个页面的推荐记录
			currentDisplay = mergedMap.get(key);
		} else {
			currentDisplay = updater.create(message);
			mergedMap.put(key, currentDisplay);
		}
		if ((!currentDisplay.isNewMessage()) && (message.isNewMessage())) {
			currentDisplay.setStatus(message.getStatus());
		}
		// 取最新的时间
		if (currentDisplay.getCreateTime().before(message.getBody().getTime())) {
			currentDisplay.setCreateTime(message.getBody().getTime());
		}
		updater.update(currentDisplay, message);
	}

	private MessageDisplay[] combineFeedAndComment(VWBContext context,
			List<Message> messages, boolean isAll) {
		MessageDisplay[] combineList = null;
		Map<String, SimpleUser> cacheUser = getAllSimpleUser(context, messages);
		if (isAll) {
			MessageDisplay[] subList = getAllMessages(messages,
					new SubscriptionUpdater(cacheUser));
			MessageDisplay[] feedCommentList = getAllMessages(messages,
					new CommentUpdater(cacheUser, true));
			MessageDisplay[] personList = getAllMessages(messages,
					new PersonUpdater(urlGenerator, cacheUser));
			combineList = MessageDisplay.mergeAndSort(subList, feedCommentList);
			combineList = MessageDisplay.mergeAndSort(combineList, personList);
		} else {
			MessageDisplay[] subList = getUnreadMessages(messages,
					new SubscriptionUpdater(cacheUser));
			MessageDisplay[] feedList = getUnreadMessages(messages,
					new CommentUpdater(cacheUser, true));
			MessageDisplay[] personList = getUnreadMessages(messages,
					new PersonUpdater(urlGenerator, cacheUser));
			combineList = MessageDisplay.mergeAndSort(subList, feedList);
			combineList = MessageDisplay.mergeAndSort(combineList, personList);
		}
		return combineList;
	}

	private MessageDisplay[] combinRecommendAndComment(VWBContext context,
			List<Message> messages, boolean isAll) {
		MessageDisplay[] combineList = null;
		Map<String, SimpleUser> cachedUser = getAllSimpleUser(context, messages);
		if (isAll) {
			MessageDisplay[] recommendList = getAllMessages(messages,
					new RecommendUpdater(cachedUser));
			MessageDisplay[] commentList = getAllMessages(messages,
					new CommentUpdater(cachedUser, false));
			combineList = MessageDisplay.mergeAndSort(recommendList,
					commentList);
		} else {
			MessageDisplay[] recommendList = getUnreadMessages(messages,
					new RecommendUpdater(cachedUser));
			MessageDisplay[] commentList = getUnreadMessages(messages,
					new CommentUpdater(cachedUser, false));
			combineList = MessageDisplay.mergeAndSort(recommendList,
					commentList);
		}
		return combineList;
	}

	private MessageDisplay[] getAllMessages(List<Message> messages,
			MessageUpdater updater) {
		return merge(messages, updater);
	}

	private void resetSizeInfo(HttpServletRequest pRequest) {
		pRequest.setAttribute("newMessageSize", pRequest.getSession()
				.getAttribute("newMessageSize"));
	}

	@RequestMapping(params = "func=feedManager")
	public ModelAndView feedManager(HttpServletRequest pRequest) {
		VWBContext context = getVWBContext(pRequest);
		String currUserId = context.getCurrentUID();
		List<Subscription> pageFeedList = subscriptionService
				.getSubscriptionByUserId(VWBContext.getCurrentTid(),
						currUserId, "page");
		List<Subscription> personFeedList = subscriptionService
				.getSubscriptionByUserId(VWBContext.getCurrentTid(),
						currUserId, "person");
		pRequest.setAttribute("pageFeedList", pageFeedList);
		pRequest.setAttribute("personFeedList", personFeedList);
		pRequest.setAttribute("tab", "adminFeed");
		return layout(VIEW_TEMPLATE, context,
				"/jsp/aone/subscription/adminSubs.jsp");
	}

	@RequestMapping(params = "func=feedHistory")
	public ModelAndView gotoFeedHistory(HttpServletRequest pRequest) {
		VWBContext context = getVWBContext(pRequest);
		String currUser = context.getCurrentUID();
		List<Message> messages = messageService.getMessage(
				VWBContext.getCurrentTid(), currUser);
		MessageDisplay[] combineList = combineFeedAndComment(context, messages,
				true);
		resetSizeInfo(pRequest);
		pRequest.setAttribute("feedMessages", combineList);
		pRequest.setAttribute("tab", "feed");
		pRequest.setAttribute("isAllDisplay", true);
		return layout(VIEW_TEMPLATE, context, "/jsp/aone/allSubscriptions.jsp");
	}

	@RequestMapping(params = "func=recommendHistory")
	public ModelAndView gotoRecommendHistory(HttpServletRequest pRequest) {
		VWBContext context = getVWBContext(pRequest);
		String currUser = context.getCurrentUID();
		List<Message> messages = messageService.getMessage(
				VWBContext.getCurrentTid(), currUser);
		MessageDisplay[] combineList = combinRecommendAndComment(context,
				messages, true);
		resetSizeInfo(pRequest);
		pRequest.setAttribute("tab", "recommend");
		pRequest.setAttribute("recommendMessages", combineList);
		pRequest.setAttribute("isAllDisplay", true);
		return layout(VIEW_TEMPLATE, context, "/jsp/aone/allRecommends.jsp");
	}

	@RequestMapping
	public ModelAndView init(HttpServletRequest pRequest) {
		// FIXME:currUser
		VWBContext context = getVWBContext(pRequest);
		String currUser = context.getCurrentUID();
		List<Message> messages = messageService.getMessage(
				VWBContext.getCurrentTid(), currUser);
		MessageDisplay[] feedCombineList = combineFeedAndComment(context,
				messages, true);
		MessageDisplay[] recommendCombineList = combinRecommendAndComment(
				context, messages, false);
		int newMessageSize = recommendCombineList.length
				+ feedCombineList.length;
		pRequest.setAttribute("user", currUser);
		pRequest.getSession().setAttribute("newMessageSize", newMessageSize);
		resetSizeInfo(pRequest);
		pRequest.setAttribute("feedMessages", feedCombineList);
		pRequest.setAttribute("recommendMessages", recommendCombineList);
		pRequest.setAttribute("tab", "all");
		pRequest.setAttribute("isAllDisplay", false);
		return layout(VIEW_TEMPLATE, context, "/jsp/aone/index.jsp");
	}

	@RequestMapping(params = "func=teamPageNavigator")
	public ModelAndView teamPageNavigator(HttpServletRequest pRequest) {
		VWBContext context = getVWBContext(pRequest);
		pRequest.setAttribute("tab", "recommend");
		return layout(VIEW_TEMPLATE, context, "/jsp/aone/allRecommends.jsp");
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "func=updateMessageStatus")
	public void updateMessageStatus(HttpServletRequest pRequest,
			HttpServletResponse pResponse) throws IOException {
		VWBContext context = getVWBContext(pRequest);
		int pid = Integer.parseInt(pRequest.getParameter("pid"));
		String currUser = context.getCurrentUID();
		String messageType = pRequest.getParameter("messageType");
		int tid = VWBContext.getCurrentTid();
		messageService.updateMessageStatus(VWBContext.getCurrentTid(), pid,
				currUser, messageType);
		updateMessageSize(pRequest);
		JSONObject object = new JSONObject();
		object.put("status", "success");
		String targetUrl;
		if ("person".equals(messageType)) {
			targetUrl = pRequest.getParameter("url");
		} else {
			targetUrl = urlGenerator.getURL(tid, UrlPatterns.T_PAGE, pid + "",
					null);
		}
		object.put("url", targetUrl);
		PrintWriter writer = pResponse.getWriter();
		writer.write(object.toString());
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "func=updateRecommendStatus")
	public void updateRecommendStatus(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		VWBContext ctx = getVWBContext(request);
		String uid = ctx.getCurrentUID();
		Team team = teamService.getTeamByName(ctx.getSite().getTeamContext());
		messageService.updateRecommendTypeMessages(uid, team.getId());
		JSONObject object = new JSONObject();
		object.put("status", "success");
		PrintWriter writer = response.getWriter();
		writer.write(object.toString());
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "func=updateSubscriptionStatus")
	public void updateSubscriptionStatus(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		VWBContext ctx = getVWBContext(request);
		String user = ctx.getCurrentUID();
		Team team = teamService.getTeamByName(ctx.getSite().getTeamContext());
		messageService.updateFeedTypeMessages(user, team.getId());
		JSONObject object = new JSONObject();
		object.put("status", "success");
		PrintWriter writer = response.getWriter();
		writer.write(object.toString());
		writer.flush();
		writer.close();
	}

}
