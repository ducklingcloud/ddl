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

package net.duckling.ddl.web;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.comment.Comment;
import net.duckling.ddl.service.comment.CommentService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.subscribe.MessageBody;
import net.duckling.ddl.service.subscribe.MessageService;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.JsonUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @date 2011-3-21
 * @author Clive Lee
 */
public abstract class AbstactCommentContoller {
	@Autowired
	private EventDispatcher eventDispatcher;
	@Autowired
	private MessageService messageService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private AoneUserService aoneUserService;

	private MessageBody buildCommentBody(Resource resource, Comment comment) {
		MessageBody body = new MessageBody();
		body.setTime(comment.getCreateTime());
		body.setType(Publisher.COMMENT_TYPE);
		body.setFrom(comment.getSender().getUid());
		body.setDigest(getCommentContent(comment));
		body.setTitle(resource.getTitle());
		body.setRid(comment.getRid());
		return body;
	}

	private String getCommentContent(Comment comment) {
		String content = comment.getContent();
		content = content.replace("<", "&lt;");
		content = content.replace(">", "&gt;");
		if (comment.getReceiver().getId() != 0) {
			return comment.getSender().getName() + " 回复"
					+ comment.getReceiver().getName() + ":" + content;
		} else {
			return comment.getSender().getName() + " 说:" + content;
		}
	}

	private void saveCommentForMyPage(Resource res, MessageBody body,
			Comment comment) {
		Publisher p = new Publisher();
		p.setType(Publisher.MY_PAGE_COMMENT);
		p.setId(comment.getId());
		if (!res.getCreator().equals(comment.getReceiver().getUid())) {
			messageService.saveRecommendCommentMessage(
					VWBContext.getCurrentTid(), body, p, res.getCreator());
		}
	}

	private void saveCommentForRecommend(MessageBody body, Comment comment) {
		Publisher publisher = new Publisher();
		publisher.setType(Publisher.RECOMMEND_COMMENT);
		publisher.setId(comment.getId());
		messageService.saveRecommendCommentMessage(VWBContext.getCurrentTid(),
				body, publisher, comment.getReceiver().getUid());
	}

	private void saveCommentForSubscription(VWBContext context,
			MessageBody body, Comment comment) {
		int rid = context.getRid();
		Publisher publisher = new Publisher();
		publisher.setType(Publisher.FEED_COMMENT);
		publisher.setId(comment.getId());
		messageService.saveFeedCommentMessage(context.getTid(), body, rid,
				comment.getId());
	}

	/**
	 * 将<a href="xxx" class='mention'>xxx</a> 转义回来
	 * 
	 * @param content
	 * @return
	 */
	private String transferContent(String content) {
		int index = 0;
		while (index < content.length()) {
			int i = content.indexOf("class='mention'", index);
			if (i <= 0) {
				break;
			}
			int begin = content.lastIndexOf("&lt;", i);
			int end = content.indexOf("&gt;", i + 19);
			String endString = content.substring(end + 4);
			String preString = content.substring(0, end + 4);
			String centreString = preString.substring(begin);
			preString = preString.substring(0, begin);
			centreString = centreString.replace("&lt;", "<");
			centreString = centreString.replace("&gt;", ">");
			content = preString + centreString + endString;
			index = i + 5;
		}
		return content;
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildCommentJSONObject(Comment instance) {
		JSONObject tempObject;
		tempObject = JsonUtil.getJSONObject(instance);
		JSONObject jsonSender = JsonUtil.getJSONObject(instance.getSender());
		tempObject.put("sender", jsonSender);
		if (instance.getReceiver().getId() != 0) {
			JSONObject jsonReceiver = JsonUtil.getJSONObject(instance
					.getReceiver());
			tempObject.put("receiver", jsonReceiver);
		}
		return tempObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getCommentDisplayObject(List<Comment> commentList,
			int size) {
		JSONArray result = new JSONArray();
		if (commentList != null && commentList.size() != 0) {
			for (Comment instance : commentList) {
				result.add(buildCommentJSONObject(instance));
			}
		}
		JSONObject resultObject = new JSONObject();
		resultObject.put("commentSize", size);
		resultObject.put("commentList", result);
		return resultObject;
	}

	private SimpleUser initSimpleUser(String senderId) {
		SimpleUser sender = new SimpleUser();
		sender.setUid(senderId);
		return sender;
	}

	private Comment saveCommentInstance(CommentService commentService,
			VWBContext context, HttpServletRequest request) {
		int pageId = context.getRid();
		String senderId = context.getCurrentUID();
		String receiverId = request.getParameter("receiver");
		String content = request.getParameter("content");
		String itemType = request.getParameter("itemType");
		content = content.replace("<", "&lt;"); // 去掉javascript脚本
		content = content.replace(">", "&gt;");
		content = content.replace("&lt;br&gt;", "<br>");  //将<br> 转义回来
		content = transferContent(content);
		String realContent = content;
		if (!"0".equals(receiverId) && content.contains("回复")
				&& content.contains(":")) { // 去掉"回复xx:"
			realContent = content.subSequence(content.indexOf(':') + 1,
					content.length()).toString();
		}
		SimpleUser receiver = initSimpleUser(receiverId);
		SimpleUser sender = initSimpleUser(senderId);
		Comment instance = wrapComment(context.getTid(), pageId, itemType,
				realContent, receiver, sender);
		int commentId = commentService.createComment(instance);
		Comment currentComment = commentService.getCommentInstance(
				context.getTid(), commentId);
		return currentComment;
	}

	/**
	 * 发送回复中关注事件,并发生关注邮件
	 * 
	 * @param context
	 * @param request
	 */
	private void sendMentionUserEvent(VWBContext context,
			HttpServletRequest request, Comment comment) {
		request.setAttribute("mention", "memtion");//dlog 记录@信息用
		String[] userIds = request.getParameterValues("mentionUserId[]");
		if (userIds == null || userIds.length == 0) {
			return;
		}
		Set<Integer> userId = new HashSet<Integer>();
		for (String user : userIds) {
			try {
				userId.add(Integer.parseInt(user));
			} catch (Exception e) {
			}
		}
		String creator = context.getCurrentUID();
		Resource r = resourceService.getResource(comment.getRid(), VWBContext.getCurrentTid());
		List<UserExt> users = aoneUserService.getUserExtByIds(userId);
		StringBuilder sb = new StringBuilder();
		for (UserExt user : users) {
			if (sb.length() != 0) {
				sb.append(",");
			}
			sb.append(user.getUid());
		}
		eventDispatcher.sendResourceMentionEvent(context.getSite().getId(),
				comment.getRid(), comment.getItemType(), r.getTitle(),
				r.getLastVersion(), comment.getContent(), creator,
				sb.toString());
	}

	private Comment wrapComment(int tid, int pageId, String itemType,
			String realContent, SimpleUser receiver, SimpleUser sender) {
		Comment instance = new Comment();
		instance.setTid(tid);
		instance.setCreateTime(new Date());
		instance.setSender(sender);
		instance.setReceiver(receiver);
		instance.setContent(realContent);
		instance.setItemType(itemType);
		instance.setRid(pageId);
		return instance;
	}

	protected void showBriefComments(VWBContext context,
			HttpServletResponse response) {
		int pageId = context.getRid();
		String itemType = context.getItemType();
		int tid = context.getTid();
		int count = commentService.getPageCommentCount(tid, pageId, itemType);
		List<Comment> commentList = commentService.getLatestComments(tid,
				pageId, itemType, 3);
		JSONObject resultObject = getCommentDisplayObject(commentList, count);
		JsonUtil.writeJSONObject(response, resultObject);
	}

	@SuppressWarnings("unchecked")
	protected void removeComment(VWBContext context,
			HttpServletRequest request, HttpServletResponse response) {
		int commentId = Integer.parseInt(request.getParameter("commentId"));
		int tid = context.getTid();
		commentService.deleteComment(tid, commentId);
		JSONObject result = new JSONObject();
		result.put("commentId", commentId);
		result.put("status", "success");
		JsonUtil.writeJSONObject(response, result);
	}

	protected void showAllComments(VWBContext context,
			HttpServletResponse response) {
		int rid = context.getRid();
		String itemType = context.getItemType();
		List<Comment> commentList = commentService.getPageComments(
				context.getTid(), rid, itemType);
		JSONObject resultObject = getCommentDisplayObject(commentList,
				commentList.size());
		JsonUtil.writeJSONObject(response, resultObject);
	}

	protected void submitComment(VWBContext context,
			HttpServletRequest request, HttpServletResponse response) {
		Comment comment = saveCommentInstance(commentService, context, request);
		Resource res = context.getResource();
		MessageBody body = buildCommentBody(res, comment);
		// 给页面创建者发送一条评论消息
		saveCommentForMyPage(res, body, comment);
		// 当评论是给当前用户的时候 则加一条推荐消息
		if (comment.getReceiver().getId() != 0) {
			saveCommentForRecommend(body, comment);
		}
		// 提交推荐的时候分两种情况:当页面关注的时候发一条关注新消息
		saveCommentForSubscription(context, body, comment);
		if (comment.getReceiver().getId() == 0) {
			eventDispatcher.sendResourceCommentEvent(context.getTid(),
					comment.getRid(), comment.getItemType(), res.getTitle(),
					comment.getSender().getUid(), res.getLastVersion(),
					comment.getContent());
		} else {
			eventDispatcher.sendResourceReplyEvent(context.getTid(),
					comment.getRid(), comment.getItemType(), res.getTitle(),
					comment.getSender().getUid(), res.getLastVersion(),
					comment.getContent(), comment.getReceiver().getUid());
		}
		sendMentionUserEvent(context, request, comment);
		JSONObject result = buildCommentJSONObject(comment);
		JsonUtil.writeJSONObject(response, result);
	}
}
