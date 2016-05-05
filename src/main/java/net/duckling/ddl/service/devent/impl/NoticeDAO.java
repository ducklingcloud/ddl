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

package net.duckling.ddl.service.devent.impl;

import java.util.List;

import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.Notice;

/**
 * @date 2011-11-2
 * @author clive
 */
public interface NoticeDAO {

	void batchWriteNotices(List<Notice> data);

	List<Notice> readOneTeamNotices(AoneNoticeParam param);


	List<Notice> getRecentNotices(AoneNoticeParam param, int k);

	int getRecentNoticeCount(AoneNoticeParam param);

	/**
	 * 获得一天的所有团队消息通知 只取三条
	 * 
	 * @param AoneNoticeParam
	 *            消息查询条件对象
	 * */
	List<Notice> readOneTeamTodayNotices();
	
	
	/**
	 * 获取一周内的notice，排除掉History类型
	 * @return
	 */
	List<Notice> readThisWeekWithoutHistory();

	List<Notice> getNoticeByTypeAndTargId(String type, int targetId);

	/**
	 * @param id
	 * @return
	 */
	Notice getNoticeById(int id);

	List<Notice> getNoticeByEventId(int eventId);

	Notice getUserLatestNotice(String uid, List<Integer> eventId);
}
