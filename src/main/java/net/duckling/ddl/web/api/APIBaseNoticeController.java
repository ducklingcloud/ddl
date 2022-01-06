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

package net.duckling.ddl.web.api;

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.mail.notice.AbstractNoticeHelper;
import net.duckling.ddl.service.mail.notice.CompositeNotice;
import net.duckling.ddl.service.mail.notice.CompositeNoticeHelper;
import net.duckling.ddl.service.mail.notice.DailyCompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.service.mail.notice.DailyNoticeHelper;
import net.duckling.ddl.service.mail.notice.GroupNotice;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class APIBaseNoticeController extends APIBaseController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    private CompositeNotice[] convertToCompositeNoticeArray(
        GroupNotice[] tempGroup) {
        CompositeNotice[] newArray = new CompositeNotice[tempGroup.length];
        for (int j = 0; j < tempGroup.length; j++) {
            newArray[j] = (CompositeNotice) tempGroup[j];
        }
        return newArray;
    }

    private DailyNotice[] convertToDailyNoticeArray(GroupNotice[] dailyGroup) {
        DailyNotice[] results = new DailyNotice[dailyGroup.length];
        for (int i = 0; i < dailyGroup.length; i++) {
            results[i] = (DailyNotice) dailyGroup[i];
        }
        return results;
    }

    private int totalCount(String uid, int tid) {
        List<TeamPreferences> prefList = teamService.getTeamPrefWithoutPersonSpace(uid);

        int count = 0;
        if (prefList != null) {
            for (TeamPreferences p : prefList) {
                if (p.getTid() == tid) {
                    count += p.getPersonNoticeCount();
                    count += p.getMonitorNoticeCount();
                }
            }
        }
        return count;
    }

    private DailyCompositeNotice wrapDailyCompositeNotice(DailyNotice dn,
                                                          AbstractNoticeHelper compositeGrouper) {
        DailyCompositeNotice temp = new DailyCompositeNotice();
        temp.setDate(dn.getDate());
        GroupNotice[] tempGroup = compositeGrouper.getCNoticeArray(dn
                                                                   .getRecords());
        temp.setCompositeArray(convertToCompositeNoticeArray(tempGroup));
        return temp;
    }

    protected List<DailyCompositeNotice> getDailyCompositeList(
        DailyNotice[] dailyGroup) {
        AbstractNoticeHelper compositeGrouper = new CompositeNoticeHelper();
        List<DailyCompositeNotice> results = new ArrayList<DailyCompositeNotice>();
        for (int i = 0; i < dailyGroup.length; i++) {
            results.add(wrapDailyCompositeNotice(dailyGroup[i],
                                                 compositeGrouper));
        }
        return results;
    }

    protected DailyNotice[] getDailyNoticeArray(List<Notice> source) {
        AbstractNoticeHelper dailyGrouper = new DailyNoticeHelper();
        GroupNotice[] dailyGroup = dailyGrouper.getCNoticeArray(source);
        return convertToDailyNoticeArray(dailyGroup);
    }

    protected int totalCount(Site site, String messageType, String uid) {
        if (messageType != null && !"".equals(messageType.trim())) {
            int tid = site.getId();
            teamPreferenceService.updateNoticeAccessTime(uid, tid, messageType);
        }

        return totalCount(uid, site.getId());
    }
}
