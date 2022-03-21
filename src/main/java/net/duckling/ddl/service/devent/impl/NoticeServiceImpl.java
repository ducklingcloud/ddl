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

package net.duckling.ddl.service.devent.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.DEventBody;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.mobile.impl.MobileNoticeQueue;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.ResourceOperateService;
// import net.duckling.ddl.service.task.TaskService;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.CommonUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @date 2011-11-2
 * @author clive
 */
@Service
public class NoticeServiceImpl implements INoticeService {
    private static final Logger LOG = Logger.getLogger(NoticeServiceImpl.class);
    private static final int RECENT_NOTICE_NUM = 5;

    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private NoticeDAO noticeDao;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private URLGenerator urlGenerator;
    private String chooseNoticeTypeByReason(String reason){
        if(NoticeRule.REASON_TEAM.equals(reason)){
            return NoticeRule.TEAM_NOTICE;
        }
        if(NoticeRule.REASON_HISTORY.equals(reason)){
            return NoticeRule.HISTORY_NOTICE;
        }
        if(NoticeRule.REASON_FOLLOW.equals(reason) || NoticeRule.REASON_CONCERN.equals(reason)){
            return NoticeRule.MONITOR_NOTICE;
        }
        return NoticeRule.PERSON_NOTICE;
    }

    private boolean hasPageDigest(String targetType, String noticeType,String reason) {
        return DEntity.DPAGE.equals(targetType) &&
                (NoticeRule.REASON_FOLLOW.equals(reason) || NoticeRule.REASON_CONCERN.equals(reason));
    }
    private void addListFromSet(List<Integer> list, Set<Integer> set) {
        if (set != null && !set.isEmpty()) {
            int i = -1;
            for (Integer s : set) {
                i = s;
            }
            if (i != -1) {
                list.add(i);
            }
        }
    }

    private void changeName(DEntity entity, String currentUid) {
        if (entity != null && currentUid.equals(entity.getId())) {
            entity.setName("我");
        }
    }
    private boolean hasRecipientList(String operation,String reason){
        return DAction.RECOMMEND.equals(operation) && NoticeRule.REASON_HISTORY.equals(reason);
    }
    private void chooseAdditionOption(DEventBody e, String reason, Notice n) {
        if (hasRecipientList(e.getOperation(), reason)) {
            n.setAddition(e.getRecipients());
        }
        if (hasPageDigest(e.getTarget().getType(),
                          n.getNoticeType(), reason)) {
            n.setAddition(getPageDigest(e.getTid(),
                                        Integer.parseInt(e.getTarget().getId())));
        }
    }

    private List<Notice> fillAddtionDisplay(List<Notice> results, String uid) {
        if (CommonUtils.isNull(results)) {
            return new ArrayList<Notice>();
        }
        for (Notice n : results) {
            if (n.getAddition() != null) {
                n.setAdditionDisplay(getAdditionDisplay(n.getAddition(),
                                                        chooseNoticeTypeByReason(n.getReason())));
            }
            String userId = (CommonUtils.isNull(uid) ? n.getRecipient() : uid);
            changeName(n.getActor(), userId);
            changeName(n.getTarget(), userId);
            changeName(n.getRelative(), userId);
        }
        return results;
    }

    private String getAdditionDisplay(String addition, String type) {
        if (!NoticeRule.HISTORY_NOTICE.equals(type)) {
            return addition;
        }
        String[] uids = addition.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uids.length; i++) {
            sb.append(aoneUserService.getUserNameByID(uids[i].trim()));
            if (i != (uids.length - 1)) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String getDEntityURL(DEventBody e) {
        DEntity t = e.getTarget();
        if (DEntity.DFILE.equals(t.getType())) {
            return urlGenerator.getURL(e.getTid(), UrlPatterns.T_VIEW_R,
                                       t.getId(), null);
        }
        if (DEntity.DPAGE.equals(t.getType())) {
            return urlGenerator.getURL(e.getTid(), UrlPatterns.T_VIEW_R,
                                       t.getId(), null);
        }
        if(DEntity.DFOLDER.equals(t.getType())){
            return urlGenerator.getURL(e.getTid(), UrlPatterns.T_VIEW_R,
                                       t.getId(), null);
        }
        if (DEntity.DCOLLECTION.equals(t.getType())) {
            return urlGenerator.getURL(e.getTid(), UrlPatterns.VIEW_COLLECTION,
                                       t.getId(), null);
        }
        if (DEntity.DTEAM.equals(t.getType())) {
            return urlGenerator.getURL(e.getTid(), UrlPatterns.T_TEAM_HOME,
                                       t.getId(), null);
        }
        if (DEntity.DUSER.equals(t.getType())) {
            UserExt ext = aoneUserService.getUserExtInfo(t.getId());
            return urlGenerator
                    .getURL(UrlPatterns.USER, ext.getId() + "", null);
        }
        // add by lvly@2012-6-25
        /*
         * Disabled the 'task' feature <2022-03-22 Tue>
         */
        /*
        if (DEntity.DTASK_SHARE.equals(t.getType())) {
            String param = null;
            if (DAction.OVER_TASK.equals(e.getOperation())) {
                param = "&listType=allHistory";
            }
            return urlGenerator.getURL(UrlPatterns.T_TASK_PATTERNS, "", null)
                    + "?taskId=" + t.getId() + "&taskType="
                    + TaskService.TYPE_SHARE + StringUtils.trimToEmpty(param);
        }
        if (DEntity.DTASK_INDEPENDENT.equals(t.getType())) {
            String param = null;
            if (DAction.OVER_TASK.equals(e.getOperation())) {
                param = "&listType=allHistory";
            }
            return urlGenerator.getURL(e.getTid(), UrlPatterns.T_TASK_PATTERNS,
                                       "", null)
                    + "?taskId="
                    + t.getId()
                    + "&taskType="
                    + TaskService.TYPE_INDEPENDENT
                    + StringUtils.trimToEmpty(param);
        }
        */

        return null;
    }

    private String getPageDigest(int tid, int pid) {
        PageRender resource = getPageRender(tid, pid, -1);
        return DPageDigester.digest(resource.getDetail().getContent(),
                                    DPageDigester.DIGEST_LENGTH);
    }

    private PageRender getPageRender(int tid, int pid, int version) {
        if (version == -1) {
            return resourceOperateService.getPageRender(tid, pid);
        }
        return resourceOperateService.getPageRender(tid, pid, version);
    }

    private DEntity getTargetEntity(DEventBody t) {
        DEntity e = new DEntity();
        e.setId(t.getTarget().getId());
        e.setType(t.getTarget().getType());
        e.setName(t.getTarget().getName());
        e.setUrl(getDEntityURL(t));
        return e;
    }

    private DEntity getUserEntity(String uid) {
        DEntity e = new DEntity();
        e.setId(uid);
        e.setName(aoneUserService.getUserNameByID(uid));
        e.setType(DEntity.DUSER);
        UserExt ext = aoneUserService.getUserExtInfo(uid);
        if(ext!=null){
            e.setUrl(urlGenerator.getURL(UrlPatterns.USER, ext.getId() + "", ""));
        }else{
            LOG.error("用户"+uid+"未找到UserExt");
        }
        return e;
    }
    private boolean hasRelative(String reason){
        return NoticeRule.REASON_RECOMMEND.equals(reason) || NoticeRule.REASON_REPLY.equals(reason);
    }

    public Notice convertToNotice(DEventBody e, String recipient, String reason) {
        Notice n = new Notice();
        n.setRecipient(recipient);
        n.setActor(getUserEntity(e.getActor()));
        n.setEventId(e.getId());
        n.setMessage(e.getMessage());
        n.setOccurTime(e.getOccurTime());
        n.setOperation(new DAction(e.getOperation()));
        n.setNoticeType(chooseNoticeTypeByReason(reason));
        n.setTid(e.getTid());
        n.setTarget(getTargetEntity(e));
        n.setTargetVersion(e.getTargetVersion());
        chooseAdditionOption(e, reason, n);
        if (hasRelative(reason)) {
            n.setRelative(getUserEntity(recipient));
        }
        n.setReason(reason);
        return n;
    }

    public Map<String, Map<Integer, Integer>> getCountMap(
        List<TeamPreferences> pref, String uid, String type) {
        Map<Integer, Integer> teamMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> personMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> monitorMap = new HashMap<Integer, Integer>();
        for (TeamPreferences p : pref) {
            int tid = p.getTid();
            AoneNoticeParam param = new AoneNoticeParam(tid,NoticeRule.TEAM_NOTICE, tid+"");
            param.setBeginDate(p.getTeamAccess());
            teamMap.put(p.getTid(), getUnreadNoticeCount(param));
            param = new AoneNoticeParam(p.getTid(),NoticeRule.PERSON_NOTICE, uid);
            param.setBeginDate(p.getPersonAccess());
            personMap.put(p.getTid(), getUnreadNoticeCount(param));
            param = new AoneNoticeParam(p.getTid(),NoticeRule.MONITOR_NOTICE, uid);
            param.setBeginDate(p.getMonitorAccess());
            monitorMap.put(p.getTid(), getUnreadNoticeCount(param));
        }
        Map<String, Map<Integer, Integer>> countMapList = new HashMap<String, Map<Integer, Integer>>();
        countMapList.put("teamMap", teamMap);
        countMapList.put("personMap", personMap);
        countMapList.put("monitorMap", monitorMap);
        return countMapList;
    }

    public List<Notice> getLastestNotices(AoneNoticeParam param, String uid) {
        List<Notice> results = noticeDao.getRecentNotices(param,
                                                          RECENT_NOTICE_NUM);
        return fillAddtionDisplay(results, uid);
    }

    @Override
    public List<Notice> getNoticeByEventId(int eventId) {
        return noticeDao.getNoticeByEventId(eventId);
    }

    @Override
    public Notice getNoticeById(int id) {
        return noticeDao.getNoticeById(id);
    }

    @Override
    public List<Notice> getNoticeByTypeAndTargId(String type, int targetId) {
        return noticeDao.getNoticeByTypeAndTargId(type, targetId);
    }

    public Integer getUnreadNoticeCount(AoneNoticeParam param) {
        return noticeDao.getRecentNoticeCount(param);
    }

    public Notice getUserLatestNotice(String uid) {
        List<TeamPreferences> prefs = teamPreferenceService
                .getAllTeamPrefs(uid);
        if (prefs == null || prefs.isEmpty()) {
            return null;
        }
        List<Integer> eventId = new ArrayList<Integer>();
        for (TeamPreferences pref : prefs) {
            LinkedHashSet<Integer> s = (LinkedHashSet<Integer>) pref
                    .getMonitorEventIdsSet();
            addListFromSet(eventId, s);
            addListFromSet(eventId, pref.getPersonEventIdsSet());
        }
        if (!eventId.isEmpty()) {
            return noticeDao.getUserLatestNotice(uid, eventId);
        }
        return null;
    }

    public void increaseNoticeCount(String[] uids, int tid, String type,
                                    int eventId) {
        teamPreferenceService.increaseNoticeCount(uids, tid, type, eventId);
    }

    public List<Notice> readNotification(AoneNoticeParam param, String uid) {
        return fillAddtionDisplay(noticeDao.readOneTeamNotices(param), uid);
    }

    @Override
    public List<Notice> readTodayNotification() {
        return fillAddtionDisplay(noticeDao.readOneTeamTodayNotices(), null);
    }

    @Override
    public List<Notice> readThisWeekWithoutHistory() {
        return fillAddtionDisplay(noticeDao.readThisWeekWithoutHistory(), null);
    }

    @Override
    public void writeNotification(List<Notice> data) {
        try{
            noticeDao.batchWriteNotices(data);
            MobileNoticeQueue.add(data);
        }catch(Exception e){
            LOG.error(data.toString(), e);
        }
    }

}
