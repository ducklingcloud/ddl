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
package net.duckling.ddl.service.mobile.impl;

import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.Notice;

public final class Notice2Message {
	private Notice2Message(){};
	public static String convert(Notice lastNotice) {
		if (lastNotice == null) {
			return "";
		}
		String oprat = lastNotice.getOperation().getName();
		String end = "";
		String n = "在";
		if (DAction.MENTION.equals(oprat)) {
			end = "@了你";
		} else if (DAction.COMMENT.equals(oprat)) {
			n = "评论了";
		} else if (DAction.REPLY.equals(oprat)) {
			end = "回复了你";
		} else if (DAction.RECOMMEND.equals(oprat)) {
			n = "将";
			end = "分享给了你";
		} else if (DAction.RECOVER.equals(oprat)) {
			n = "恢复了";
		} else if (DAction.DELETE.equals(oprat)) {
			n = "删除了";
		} else if (DAction.MODIFY.equals(oprat)) {
			n = "修改了";
		} else {
			return "您有一条新消息";
		}
		return lastNotice.getActor().getName() + n
				+ lastNotice.getTarget().getName() + end;
	}
}
