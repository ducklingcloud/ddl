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
import net.duckling.ddl.service.team.TeamApplicantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 团队申请服务
 *
 * @author Yangxp
 * @since 2012-11-13
 */
@Service
public class TeamApplicantServiceImpl implements TeamApplicantService {

    @Autowired
    private TeamApplicantDAO taDAO;

    @Override
    public int create(TeamApplicant ta) {
        TeamApplicant temp = taDAO.get(ta.getTid(), ta.getUid());
        if (null == temp) {
            return taDAO.create(ta);
        } else {
            taDAO.updateByUIDTID(ta);
            return temp.getId();
        }
    }

    @Override
    public void audit(TeamApplicant ta) {
        taDAO.updateByUIDTID(ta);
    }

    @Override
    public void batchAudit(List<TeamApplicant> taList) {
        taDAO.batchUpdateByUIDTID(taList);
    }

    @Override
    public List<TeamApplicant> getUserApplicant(String uid) {
        return taDAO.getUserApplicant(uid);
    }

    @Override
    public List<TeamApplicantRender> getAcceptApplicantOfTeam(int tid) {
        return taDAO.getAcceptApplicantOfTeam(tid);
    }

    @Override
    public List<TeamApplicantRender> getWaitingApplicantOfTeam(int tid) {
        return taDAO.getWaitingApplicantOfTeam(tid);
    }

    @Override
    public List<TeamApplicantRender> getRejectApplicantOfTeam(int tid) {
        return taDAO.getRejectApplicantOfTeam(tid);
    }

    @Override
    public void cancelApply(int tid, String uid) {
        taDAO.delete(tid, uid);
    }

    @Override
    public void batchDelete(int tid, String[] uids) {
        taDAO.batchDelete(tid, uids);
    }

    @Override
    public List<TeamApplicantNoticeRender> getTeamApplicantNoticeInotKnow(
        String uid) {
        return taDAO.getTeamApplicantNoticeInotKnow(uid);
    }

    @Override
    public void iknowAllTeamApplicantNotice(String uid) {
        taDAO.iknowAllTeamApplicantNotice(uid);
    }
}
