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

package net.duckling.ddl.service.authority.impl;

import java.util.Collections;
import java.util.List;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.authority.UserTeamAclBean;

import org.apache.log4j.Logger;

/**
 * @date 2011-5-26
 * @author Clive Lee
 */
public class AuthorityServiceImpl implements AuthorityService {

    private static final Logger LOG = Logger
            .getLogger(AuthorityServiceImpl.class);


    private TeamAclDAO teamAclDao;

    private String getActuralTeamPermission(int tid, String currUser) {
        LOG.debug("search team permission:" + tid + "," + currUser);
        return teamAclDao.getSingleMemberAuthority(tid, currUser);
    }

    private String getCurrUser(VWBSession session) {
        return session.getCurrentUser().getName();
    }

    private String getExpectedPermission(String operation) {
        if (VIEW.equals(operation)) {
            return VIEW;
        }
        if (ADMIN.equals(operation)) {
            return ADMIN;
        }
        if (EDIT.equals(operation)) {
            return EDIT;
        }
        return FORBID;
    }

    private int getPermissionPriority(String permission) {
        if (FORBID.equals(permission)) {
            return 4;
        }
        if (VIEW.equals(permission)) {
            return 3;
        }
        if (EDIT.equals(permission)) {
            return 2;
        }
        if (ADMIN.equals(permission)) {
            return 1;
        }
        return 6;
    }

    private boolean isPermissionEough(String expected, String actural) {
        int expectLevel = getPermissionPriority(expected);
        int acturalLevel = getPermissionPriority(actural);
        return expectLevel >= acturalLevel;
    }

    /*----Domain Methods--*/

    public void setTeamAclDao(TeamAclDAO teamAclDao) {
        this.teamAclDao = teamAclDao;
    }

    public List<TeamAcl> getTeamAdminByTid(int tid) {
        List<TeamAcl> result = teamAclDao.getTeamAclByTidAndAuth(tid, ADMIN);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    public List<UserTeamAclBean> getTeamAclByUidAndAuth(String uid, String auth) {
        return teamAclDao.getTeamAclByUidAndAuth(uid, auth);
    }

    public List<TeamAcl> getTeamMembersAuthority(int tid) {
        return teamAclDao.getTeamMembersAuthority(tid);
    }

    @Override
    public List<TeamAcl> getUserAllTeamAcl(String uid) {
        return teamAclDao.getUserAllTeamAcl(uid);
    }

    public String getTeamAuthority(int tid, String uid) {
        return teamAclDao.getSingleMemberAuthority(tid, uid);
    }

    public boolean teamAccessability(int tid, VWBSession session,
                                     String operation) {
        String expected = getExpectedPermission(operation);
        String actural = getActuralTeamPermission(tid, getCurrUser(session));
        LOG.debug("Team Permission{expected=" + expected + ",actural="
                  + actural + "}");
        return isPermissionEough(expected, actural);
    }

    @Override
    public void addBatchTeamAcl(String[] newUsers, int tid, String[] auth) {
        this.teamAclDao.addBatchTeamAcl(newUsers, tid, auth);
    }

    @Override
    public UserTeamAclBean getUserTeamAcl(int tid, String uid) {
        return this.teamAclDao.getUserTeamAcl(tid, uid);
    }

    @Override
    public void updateMembersAuthority(int tid, String[] oldUsers,
                                       String[] newAuths) {
        teamAclDao.updateMembersAuthority(tid, oldUsers, newAuths);
    }

    @Override
    public void removeMemberAcls(int tid, String[] uids) {
        teamAclDao.removeMemberAcls(tid, uids);
    }

    @Override
    public boolean haveTeamEditeAuth(int tid, String uid) {
        String auth = getTeamAuthority(tid, uid);
        return AuthorityService.ADMIN.equals(auth)||AuthorityService.EDIT.equals(auth);
    }

}
