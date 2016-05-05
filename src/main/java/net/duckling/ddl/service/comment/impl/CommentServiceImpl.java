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

package net.duckling.ddl.service.comment.impl;

import java.util.Collections;
import java.util.List;

import net.duckling.ddl.service.comment.Comment;
import net.duckling.ddl.service.comment.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentDAO commentDao;

	public CommentDAO getCommentDao() {
		return commentDao;
	}

	public void setCommentDao(CommentDAO commentDao) {
		this.commentDao = commentDao;
	}

	public Comment getCommentInstance(int tid, int commentId) {
		return commentDao.getComment(tid, commentId);
	}

	public List<Comment> getPageComments(int tid, int rid, String type) {
		return commentDao.getPageComments(tid, rid, type);
	}

	public int createComment(Comment comment) {
		return commentDao.createComment(comment);
	}

	public void deleteComment(int tid, int commentId) {
		commentDao.removeComment(tid, commentId);
	}

	/**
	 * 删除页面的对应评论 ，实质是更新标志位
	 * 
	 * @author lvly
	 * @since 2012-07-20
	 * @param rid
	 *            页面ID
	 * @param type
	 *            LynxConstants.TYPE_XXX
	 * */
	public void removePageComment(int tid, int rid, String type) {
		commentDao.removePageComment(tid, rid, type);
	}

	public int getPageCommentCount(int tid, int pageId, String type) {
		return commentDao.getPageCommentCount(tid, pageId, type);
	}

	public List<Comment> getLatestComments(int tid, int pageId, String type,
			int size) {
		List<Comment> resultList = commentDao.getPageBriefComments(tid, pageId,
				type, size);
		Collections.reverse(resultList);
		return resultList;
	}

}
