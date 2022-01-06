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

package net.duckling.ddl.service.devent;

import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.team.TeamPreferences;


/**
 * @date 2011-11-2
 * @author clive
 */
public interface INoticeService {
    void writeNotification(List<Notice> data);
    List<Notice> readNotification(AoneNoticeParam param,String uid);
    /**查询今天的消息*/
    List<Notice> readTodayNotification();

    /**
     * 查询本周内的
     * @return
     */
    List<Notice> readThisWeekWithoutHistory();

    /**
     * 通过主键获得Notice
     * @param id notice Id
     * @return
     */
    Notice getNoticeById(int id);
    Notice convertToNotice(DEventBody e,String recipient,String reason);
    Integer getUnreadNoticeCount(AoneNoticeParam param);
    List<Notice> getLastestNotices(AoneNoticeParam param,String uid);
    Map<String,Map<Integer,Integer>> getCountMap(List<TeamPreferences> pref,String uid,String type);
    void increaseNoticeCount(String[] teamMembers, int tid,String teamNotice,int eventId);
    List<Notice> getNoticeByTypeAndTargId(String type,int targetId);

    List<Notice> getNoticeByEventId(int eventId);
    /**
     * 获取用户最近的一条notice，如果没有返回空
     * @param uid
     * @return
     */
    public Notice getUserLatestNotice(String uid);
}
