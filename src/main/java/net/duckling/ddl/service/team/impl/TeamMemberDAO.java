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
package net.duckling.ddl.service.team.impl;

import java.util.List;

import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;

public interface TeamMemberDAO {

    boolean addTeamMembers(final String[] uids, final int tid);


    List<SimpleUser> getMembersByName(int tid, String name);

    List<SimpleUser> getMembersByPinyin(int tid, String pinyin);

    List<SimpleUser> getMembersOrderByName(int tid);

    List<UserExt> getTeamContacts(int tid);

    // get userext according tid
    List<UserExt> getUserExtContacts(int tid);

    boolean isAtTheSameTeam(String user1, String user2);

    Boolean[] isUsersInTeam(int tid, String[] uids);

    /**
     * 批量删除团队成员
     *
     * @param tid
     * @param uids
     */
    void removeMembers(final int tid, final String[] uids);
    int[] updateTeamSequence(final String uid, final String[] ids);

    int getMemberAmount(int tid);
}
