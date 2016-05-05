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
package net.duckling.ddl.service.comment;

import java.util.List;

/**
 * @date 2011-3-21
 * @author Clive Lee
 */
public interface CommentService {

	Comment getCommentInstance(int tid, int commentId);

	List<Comment> getPageComments(int tid, int rid, String type);

	int createComment(Comment comment);

	void deleteComment(int tid, int commentId);

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
	void removePageComment(int tid, int rid, String type);

	int getPageCommentCount(int tid, int pageId, String type);

	List<Comment> getLatestComments(int tid, int pageId, String type, int size);

}
