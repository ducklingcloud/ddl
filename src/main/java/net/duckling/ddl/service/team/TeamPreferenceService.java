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
package net.duckling.ddl.service.team;

import java.util.List;
import java.util.Set;


public interface TeamPreferenceService {
    /**
     *
     * @param tid
     * @return uid
     */
    List<TeamPreferences> getUidByTid(int tid);
    List<TeamPreferences> getTeamPrefWithoutTeam(String uid, int tid);

    /**
     * 获取所有的团队
     * @param tids
     * @return
     */
    List<TeamPreferences> getAllTeamPreferencesByTids(Set<Integer> tids);

    public List<TeamPreferences> getAllTeamPrefs(String uid);
    TeamPreferences getTeamPreferences(String uid, int tid);

    UserNoticeCount getUserNoticeCount(String uid) ;


    void increaseNoticeCount(String[] uids, int tid, String type,int eventId);

    /**
     * 一次移除一个notice事件
     * @param uid
     * @param tid
     * @param type
     * @param eventId
     */
    void updateOneNoticeAccessTime(String uid, int tid, String type, Set<Integer> eventId);
    void updateNoticeAccessTime(String uid, int tid, String type);
    void updateAllMessage(String uid, String type);
}
