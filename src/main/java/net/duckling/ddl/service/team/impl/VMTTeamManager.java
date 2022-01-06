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

import net.duckling.ddl.service.team.Team;
import net.duckling.vmt.api.domain.VmtUser;
/**
 * vmt团队管理
 * @author zhonghui
 *
 */
public interface VMTTeamManager {
    /**
     * 在VMT中创建team
     * @param team
     */
    String addTeam(Team team);
    /**
     * 更新VMT中team名称
     * @param team
     * @return
     */
    boolean updateTeam(Team team);
    /**
     * 获取team在VMT中的vmtdn
     * @param teamCode
     * @return
     */
    String getTeamVmtdn(String teamCode);
    /**
     * 向VMT中添加用户
     * @param vmtdn
     * @param uid
     * @return
     */
    boolean addUserToTeam(String vmtdn,String uid);
    /**
     * 移除VMT中的用户
     * @param vmtdn
     * @param uid
     * @return
     */
    boolean removeUserToTeam(String vmtdn,String uid);
    /**
     * 向vmt的team中添加管理员
     * @param vmtdn
     * @param uid
     * @return
     */
    boolean addAdminToTeam(String vmtdn,String uid);
    /**
     * 移除vmt的team中管理员
     * @param vmtdn
     * @param uid
     * @return
     */
    boolean removeAdminToTeam(String vmtdn,String uid);
    /**
     * 删除VMT中team
     * @param vmtdn
     */
    void deleteTeam(String vmtdn);

    VmtUser getUidByUmtId(String umtId);
    String[] getUidByUmtId(String[] umtId);
    boolean teamCodeExistInVmt(String code);

}
