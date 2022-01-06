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
package net.duckling.ddl.service.authority;

import java.util.List;

import net.duckling.ddl.common.VWBSession;

public interface AuthorityService {
    public static final String ADMIN = "admin";
    public static final String EDIT = "edit";

    public static final String FORBID = "forbid";
    public static final String VIEW = "view";

    boolean teamAccessability(int tid, VWBSession session, String operation);
    /**
     * 获取一个team里面所有的有管理权限的TeamAcl
     * @param tid
     * @return
     */
    List<TeamAcl> getTeamAdminByTid(int tid);
    /**
     * 获取用户某个权限下的所有列表
     * @param uid
     * @param auth
     * @return
     */
    List<UserTeamAclBean> getTeamAclByUidAndAuth(String uid,String auth);
    String getTeamAuthority(int tid, String uid);
    List<TeamAcl> getTeamMembersAuthority(int tid);
    List<TeamAcl> getUserAllTeamAcl(String uid);
    void addBatchTeamAcl(String[] newUsers, int tid, String[] newUsersAuth);
    UserTeamAclBean getUserTeamAcl(int tid, String uid);
    void updateMembersAuthority(int tid, String[] oldUsers, String[] newAuths);
    void removeMemberAcls(int tid, String[] uids);

    boolean haveTeamEditeAuth(int tid,String uid);
}
