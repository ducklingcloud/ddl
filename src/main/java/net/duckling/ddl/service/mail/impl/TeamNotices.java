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
/**
 *
 */
package net.duckling.ddl.service.mail.impl;

import java.util.List;

import net.duckling.ddl.service.mail.notice.DailyCompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.util.CommonUtils;


/**
 * @author lvly
 * @since 2012-11-12
 */
public class TeamNotices {

    private Team team;
    private List<DailyCompositeNotice> teamNoticeList;
    private DailyNotice[] personNoticeList;
    private DailyNotice[] monitorNoticeList;
    private int teamNoticeSize;
    private int personNoticeSize;
    private int monitorNoticeSize;


    public int getTeamNoticeSize() {
        return teamNoticeSize;
    }

    public void setTeamNoticeSize(int teamNoticeSize) {
        this.teamNoticeSize = teamNoticeSize;
    }

    public int getPersonNoticeSize() {
        return personNoticeSize;
    }

    public void setPersonNoticeSize(int personNoticeSize) {
        this.personNoticeSize = personNoticeSize;
    }

    public int getMonitorNoticeSize() {
        return monitorNoticeSize;
    }

    public void setMonitorNoticeSize(int monitorNoticeSize) {
        this.monitorNoticeSize = monitorNoticeSize;
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return CommonUtils.isNull(teamNoticeList)&&CommonUtils.isNull(personNoticeList)&&CommonUtils.isNull(monitorNoticeList);
    }

    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public List<DailyCompositeNotice> getTeamNoticeList() {
        return teamNoticeList;
    }
    public void setTeamNoticeList(List<DailyCompositeNotice> teamNoticeList) {
        this.teamNoticeList = teamNoticeList;
    }
    public DailyNotice[] getPersonNoticeList() {
        return personNoticeList;
    }
    public void setPersonNoticeList(DailyNotice[] personNoticeList) {
        this.personNoticeList = personNoticeList;
    }
    public DailyNotice[] getMonitorNoticeList() {
        return monitorNoticeList;
    }
    public void setMonitorNoticeList(DailyNotice[] monitorNoticeList) {
        this.monitorNoticeList = monitorNoticeList;
    }


}
