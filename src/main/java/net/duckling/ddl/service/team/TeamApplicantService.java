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



/**
 * 团队申请服务接口
 * @author Yangxp
 * @since 2012-11-13
 */
public interface TeamApplicantService {
	/**
	 * 创建一条团队申请记录
	 * @param ta
	 * @return
	 */
	int create(TeamApplicant ta);
	/**
	 * 审核用户团队申请
	 * @param ta
	 */
	void audit(TeamApplicant ta);
	/**
	 * 批量审核用户团队申请
	 * @param taList
	 */
	void batchAudit(List<TeamApplicant> taList);
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
	 * 用户(uid)取消申请加入某团队(tid)
	 * @param tid 团队ID
	 * @param uid 用户ID
	 */
	void cancelApply(int tid, String uid);
	/**
	 * 批量删除某团队内的用户申请记录
	 * @param tid 团队ID
	 * @param uids 用户ID
	 */
	void batchDelete(int tid, String[] uids);
	/**
	 * 查询用户尚未查看的团队申请记录信息
	 * @param uid 用户ID
	 * @return
	 */
	List<TeamApplicantNoticeRender> getTeamApplicantNoticeInotKnow(String uid);
	/**
	 * 重置用户所有团队申请的iknow字段为YES
	 * @param uid 用户ID
	 */
	public void iknowAllTeamApplicantNotice(String uid);
}
