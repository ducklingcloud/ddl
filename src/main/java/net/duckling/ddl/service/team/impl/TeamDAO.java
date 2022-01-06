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

import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamState;
import net.duckling.ddl.util.PaginationBean;



/**
 * @date 2011-3-1
 * @author Clive Lee
 */
public interface TeamDAO{
    Team getTeamByName(String name);
    Team getTeamById(int tid);
    void updateBasicInfo(String tid, String title, String description, String accessType, String defaultMemberAuth,Date time,String defaultView);
    List<Team> getAllUserTeams(String user);
    int createTeam(final Team team);
    void updateTeamState(int tid, TeamState state);
    /**
     * 更新团队名称
     * @param tid
     * @param name
     */
    void updateTeamName(int tid, String name);
    List<Team> getAllTeams();
    List<Team> getAllPublicAndProtectedTeam(int offset, int size);
    List<Team> getAllUserPublicAndProtectedTeam(String uid);
    /**
     * 获取当前系统内的团队数
     * @return 团队数
     */
    int getTotalTeamNumber();
    /**
     * 更新当前团队vmtdn
     * @param tid
     * @param vmtdn
     * @return
     */
    boolean updateTeamVmtDn(int tid,String vmtdn);
    List<Team> getTeamByCreator(String uid);
    List<Team> queryByTeamCode(String queryWord);
    List<Team> queryByTeamName(String queryWord);
    List<Team> getTeamByType(String type);
    PaginationBean<Team> queryByTeamName(String queryWord, int offset, int size);
}
