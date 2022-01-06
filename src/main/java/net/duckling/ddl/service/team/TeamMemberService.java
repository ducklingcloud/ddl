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

import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;

/**
 * @date 2012-2-17
 * @author clive
 */
public interface TeamMemberService {
    List<UserExt> getTeamContacts(int tid);

    /**
     * 获取指定团队的所有成员，输出结果按姓名排序
     *
     * @param tid
     *            团队ID
     * @return
     */
    List<SimpleUser> getTeamMembersOrderByName(int tid);

    /**
     * 根据拼音查询团队内成员
     *
     * @param tid
     *            团队ID
     * @param pinyin
     *            成员姓名的拼音
     * @return
     */
    List<SimpleUser> getTeamMembersByPinyin(int tid, String pinyin);

    /**
     * 根据汉字（姓名）查询团队成员
     *
     * @param tid
     *            团队ID
     * @param name
     *            成员姓名中的汉字
     * @return
     */
    List<SimpleUser> getTeamMembersByName(int tid, String name);

    /**
     * 删除团队成员
     *
     * @param tid
     * @param uids
     */
    void removeMembers(int tid, String[] uids);

    List<UserExt> getUserExtContacts(int tid);

    boolean checkTeamValidity(String uid, int tid);

    boolean isAtTheSameTeam(String user1, String user2);

    boolean isUserInTeam(int tid, String curUid);

    /***
     * 更新所有个人拥有的团队的顺序
     *
     * @param uid
     *            用户id
     * @param ids
     *            团队id序列
     * */
    int updateTeamsSequence(String uid, String[] ids);

    /**
     * 判断用户是否属于该团队
     *
     * @param tid
     *            团队ID
     * @param uids
     *            用户ID集合
     * @return
     */
    Boolean[] isUsersInTeam(int tid, String[] uids);

    void addTeamMembers(String[] newUsers, int tid);

    /**
     * 获取团队成员数量
     * @param tid
     * @return
     */
    int getMemberAmount(int tid);

}
