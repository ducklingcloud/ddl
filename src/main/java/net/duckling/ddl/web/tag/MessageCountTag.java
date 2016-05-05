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

package net.duckling.ddl.web.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;


/**
 * @date 2011-3-15
 * @author Clive Lee
 */
public class MessageCountTag extends VWBBaseTag {
    private static final long serialVersionUID = 1L;

    @Override
    public int doVWBStart() throws Exception {
        setMultipleTeamCount();
        return EVAL_PAGE;
    }

    private Map<Integer, Integer> getCountMapByType(List<TeamPreferences> prefList, String type) {
        Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        if (prefList != null) {
            for (TeamPreferences p : prefList) {
                if (NoticeRule.TEAM_NOTICE.equals(type)) {
                    countMap.put(p.getTid(), p.getTeamNoticeCount());
                } else if (NoticeRule.PERSON_NOTICE.equals(type)) {
                    countMap.put(p.getTid(), p.getPersonNoticeCount());
                } else {
                    countMap.put(p.getTid(), p.getMonitorNoticeCount());
                }
            }
        }
        return countMap;
    }

    private int getTotalCount(Map<Integer, Integer> countMap) {
        int totalCount = 0;
        for (Integer count : countMap.values()) {
            totalCount += count;
        }
        return totalCount;
    }

    private void setMultipleTeamCount() {
        String uid = vwbcontext.getCurrentUID();
        List<TeamPreferences> pref = getBean(TeamService.class).getTeamPrefWithoutPersonSpace(uid);
        int teamInvites = getBean(InvitationService.class).getInvitationCount(uid);
        Map teamMap = getCountMapByType(pref, NoticeRule.TEAM_NOTICE);
        Map personMap = getCountMapByType(pref, NoticeRule.PERSON_NOTICE);
        Map monitorMap = getCountMapByType(pref, NoticeRule.MONITOR_NOTICE);
        pageContext.getRequest().setAttribute("TeamCountMap", teamMap);
        pageContext.getRequest().setAttribute("PersonCountMap", personMap);
        pageContext.getRequest().setAttribute("MonitorCountMap", monitorMap);
        int teamCount = getTotalCount(teamMap);
        int personCount = getTotalCount(personMap);
        int monitorCount = getTotalCount(monitorMap);
        pageContext.getRequest().setAttribute("teamInvites", teamInvites);
        pageContext.getRequest().setAttribute("teamCount", teamCount);
        pageContext.getRequest().setAttribute("personCount", personCount);
        pageContext.getRequest().setAttribute("monitorCount", monitorCount);
        pageContext.getRequest().setAttribute("totalCount", teamInvites + personCount + monitorCount);
    }

}
