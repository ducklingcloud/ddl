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

import net.duckling.ddl.service.team.TeamApplicant;
import net.duckling.ddl.service.team.TeamApplicantNoticeRender;
import net.duckling.ddl.service.team.TeamApplicantRender;


/**
 * 团队申请记录 的DAO接口
 * @author Yangxp
 * @since 2012-11-13
 */
public interface TeamApplicantDAO {
    /**
     * 创建一条团队申请记录
     * @param ta
     * @return
     */
    int create(TeamApplicant ta);
    /**
     * 更新用户申请加入团队的记录
     * @param ta
     */
    void updateByUIDTID(TeamApplicant ta);
    /**
     * 批量更新用户申请加入团队的记录
     * @param taList
     */
    void batchUpdateByUIDTID(List<TeamApplicant> taList);
    /**
     * 查询某用户(uid)所有的团队申请记录
     * @param uid
     * @return
     */
    List<TeamApplicant> getUserApplicant(String uid);
    /**
     * 查询团队(tid)的所有审核通过的申请者记录
     * @param tid
     * @return
     */
    List<TeamApplicantRender> getAcceptApplicantOfTeam(int tid);
    /**
     * 查询团队(tid)的所有待审核的申请者记录
     * @param tid
     * @return
     */
    List<TeamApplicantRender> getWaitingApplicantOfTeam(int tid);
    /**
     * 查询团队(tid)的所有被拒绝的申请者记录
     * @param tid
     * @return
     */
    List<TeamApplicantRender> getRejectApplicantOfTeam(int tid);
    /**
     * 删除用户的团队申请记录
     * @param tid
     * @param uid
     */
    void delete(int tid, String uid);
    /**
     * 批量删除用户的团队删除记录
     * @param tid 申请的团队ID
     * @param uids 申请者ID集合
     */
    void batchDelete(int tid, String[] uids);
    /**
     * 查询用户(uid)申请加入团队(tid)的记录
     * @param tid 团队ID
     * @param uid 用户ID
     * @return 存在则返回对象，否则返回null
     */
    TeamApplicant get(int tid, String uid);
    /**
     * 查询用户(uid)尚未查看的团队审核通知
     * @param uid 用户ID
     * @return
     */
    List<TeamApplicantNoticeRender> getTeamApplicantNoticeInotKnow(String uid);
    /**
     * 重置用户所有团队申请的iknow字段为YES
     * @param uid 用户ID
     */
    void iknowAllTeamApplicantNotice(String uid);

}
