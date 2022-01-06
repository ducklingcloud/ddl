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
package net.duckling.ddl.service.mail.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;



import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.dbrain.RecommendationService;
import net.duckling.ddl.service.dbrain.util.Word2VEC;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.mail.NoticeMailHelper;
import net.duckling.ddl.service.mail.impl.TeamNotices;
import net.duckling.ddl.service.mail.notice.AbstractNoticeHelper;
import net.duckling.ddl.service.mail.notice.CompositeNotice;
import net.duckling.ddl.service.mail.notice.CompositeNoticeHelper;
import net.duckling.ddl.service.mail.notice.DailyCompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.service.mail.notice.DailyNoticeHelper;
import net.duckling.ddl.service.mail.notice.GroupNotice;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.JSONMap;

import org.apache.log4j.Logger;

/**
 * 邮件发送线程
 *
 * @author lvly
 *
 */
public class EmailNoticeThread extends Thread {
    private static final int DEFAULT_NOTICE_SIZE = 3;
    private static final int DEFAULT_TEAM_COUNT = 5;
    public static final String WEEK_PREFIX = "week_";

    private int triggerWeek;
    private static String WORD_VECTOR_PATH;

    private static int emailCount = 0;
    private static Map<String, TeamPreferences> exactTeamPreferencesCache = new HashMap<String, TeamPreferences>();
    private static final Logger LOG = Logger.getLogger(EmailNoticeThread.class);

    private static Map<String, List<Notice>> noticesCache = new HashMap<String, List<Notice>>();
    private static Map<String, Param> paramCache = new HashMap<String, Param>();
    private static LinkedBlockingQueue<SimpleUser> queue = new LinkedBlockingQueue<SimpleUser>();
    private static Map<String, List<TeamPreferences>> teamPreferencesCache = new HashMap<String, List<TeamPreferences>>();
    public static boolean END_FLAG = false;

    private RecommendationService recommendationService;
    private static Word2VEC word2vec = null;


    private static void clearCache() {
        paramCache = new HashMap<String, Param>();
        noticesCache = new HashMap<String, List<Notice>>();
        teamPreferencesCache = new HashMap<String, List<TeamPreferences>>();
        exactTeamPreferencesCache = new HashMap<String, TeamPreferences>();
        word2vec = null;
    }

    private static Word2VEC getWord2VEC(){
        try {
            if(word2vec==null){
                word2vec = new Word2VEC();
                word2vec.loadModel(WORD_VECTOR_PATH);
            }
            return word2vec;
        } catch (IOException e) {
            LOG.error("load Word vector file error." +  e.getMessage());
        }
        return null;
    }

    private static int increaseEmailCount() {
        return emailCount++;
    }

    public static void addExactTeamPreference(String key, TeamPreferences value) {
        exactTeamPreferencesCache.put(key, value);
    }

    public static void addNotice(String key, Notice value) {
        addObject(noticesCache, key, value);
    }

    public static <T> void addObject(Map<String, List<T>> map, String key,
                                     T value) {
        List<T> list = map.get(key);
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.add(value);
        map.put(key, list);
    }

    public static void addParam(Param param) {
        paramCache.put(param.getItemId() + "_" + param.getKey(), param);
    }

    public static void addTeamPreference(String key, TeamPreferences value) {
        addObject(teamPreferencesCache, key, value);
    }

    public static void addUser(SimpleUser user) {
        queue.add(user);
    }

    /**
     * 重置email数量计数器
     */
    public static void resetEmailCount() {
        emailCount = 0;
    }

    private String baseAddress;

    private String baseUrl;
    private boolean flag = true;
    private TeamService teamService;

    public EmailNoticeThread() {
        super("EmailNoticeThread");
        this.setDaemon(true);
    }

    private DailyNotice[] getDailyNoticeArray(List<Notice> source) {
        AbstractNoticeHelper dailyGrouper = new DailyNoticeHelper();
        GroupNotice[] dailyGroup = dailyGrouper.getCNoticeArray(source);
        return convertToDailyNoticeArray(dailyGroup);
    }

    private CompositeNotice[] getCompositeNoticeArray(List<Notice> source) {
        AbstractNoticeHelper grouper = new CompositeNoticeHelper();
        GroupNotice[] group = grouper.getCNoticeArray(source);
        return convertToCompositeNoticeArray(group);
    }

    private Date getLastAccess(TeamPreferences p, String type) {
        if (NoticeRule.TEAM_NOTICE.equals(type)) {
            return p.getTeamAccess();
        }
        if (NoticeRule.PERSON_NOTICE.equals(type)) {
            return p.getPersonAccess();
        }
        return p.getMonitorAccess();
    }

    private TeamNotices getTeamNotices(String uid, int tid) {
        TeamNotices notices = new TeamNotices();
        // 更新
        List<Notice> teamNotices = noticesCache.get(tid + "_" + tid + "_"
                                                    + NoticeRule.TEAM_NOTICE);
        notices.setTeamNoticeSize(teamNotices == null ? 0 : teamNotices.size());
        teamNotices = getListTop(teamNotices,DEFAULT_NOTICE_SIZE);
        updateNoticeStatus(teamNotices, NoticeRule.TEAM_NOTICE, uid, tid);
        DailyNotice[] dailyGroup = getDailyNoticeArray(teamNotices);
        List<DailyCompositeNotice> results = getDailyCompositeList(dailyGroup);
        notices.setTeamNoticeList(results);

        // 消息
        List<Notice> personNotices = noticesCache.get(uid + "_" + tid + "_"
                                                      + NoticeRule.PERSON_NOTICE);
        notices.setPersonNoticeSize(personNotices == null ? 0 : personNotices
                                    .size());
        personNotices = getListTop(personNotices,DEFAULT_NOTICE_SIZE);
        updateNoticeStatus(personNotices, NoticeRule.PERSON_NOTICE, uid, tid);
        notices.setPersonNoticeList(getDailyNoticeArray(personNotices));

        // 关注
        List<Notice> monitorNotices = noticesCache.get(uid + "_" + tid + "_"
                                                       + NoticeRule.MONITOR_NOTICE);
        notices.setMonitorNoticeSize(monitorNotices == null ? 0
                                     : monitorNotices.size());
        monitorNotices = getListTop(monitorNotices, DEFAULT_NOTICE_SIZE);
        updateNoticeStatus(monitorNotices, NoticeRule.MONITOR_NOTICE, uid, tid);
        notices.setMonitorNoticeList(getDailyNoticeArray(monitorNotices));

        Team team = teamService.getTeamByID(tid);
        notices.setTeam(team);
        return notices;
    }

    private <T> List<T> getListTop(List<T> list, int num) {
        if (CommonUtils.isNull(list) || list.size() <= num) {
            return list;
        } else {
            List<T> result = new ArrayList<T>();
            for (int i = 0; i < num; i++) {
                result.add(list.get(i));
            }
            return result;
        }
    }

    private <T> T [] getArrayTop(T [] arr, int num) {
        if (arr == null || arr.length <= num) {
            return arr;
        } else {
            return Arrays.copyOfRange(arr, 0, num);
        }
    }

    /**
     * 队列中最后一个用户清理信息
     *
     * @throws InterruptedException
     */
    private void lastUserClearInfo() throws InterruptedException {
        if (queue.isEmpty() && END_FLAG) {
            END_FLAG = false;
            LOG.info("email notice queue is empty! job end!");
            LOG.info("加入邮件队列总数为:" + emailCount);
            clearCache();
            resetEmailCount();
            Thread.sleep(10000);
        }
    }

    private void updateNoticeStatus(List<Notice> noticeList, String type,
                                    String uid, int tid) {
        TeamPreferences pref = exactTeamPreferencesCache.get(uid + "_" + tid);
        Date lastAccess = getLastAccess(pref, type);
        if (CommonUtils.isNull(noticeList)) {
            return;
        }
        for (Notice n : noticeList) {
            if (n.getOccurTime().getTime() > lastAccess.getTime()) {
                n.setNoticeStatus("new");
            } else {
                n.setNoticeStatus("old");
            }
        }
    }

    private CompositeNotice[] convertToCompositeNoticeArray(
        GroupNotice[] tempGroup) {
        CompositeNotice[] newArray = new CompositeNotice[tempGroup.length];
        for (int j = 0; j < tempGroup.length; j++) {
            newArray[j] = (CompositeNotice) tempGroup[j];
        }
        return newArray;
    }

    private DailyNotice[] convertToDailyNoticeArray(GroupNotice[] dailyGroup) {
        if (CommonUtils.isNull(dailyGroup)) {
            return null;
        }
        DailyNotice[] results = new DailyNotice[dailyGroup.length];
        for (int i = 0; i < dailyGroup.length; i++) {
            results[i] = (DailyNotice) dailyGroup[i];
        }
        return results;
    }

    private List<DailyCompositeNotice> getDailyCompositeList(
        DailyNotice[] dailyGroup) {
        AbstractNoticeHelper compositeGrouper = new CompositeNoticeHelper();
        List<DailyCompositeNotice> results = new ArrayList<DailyCompositeNotice>();
        if (CommonUtils.isNull(dailyGroup)) {
            return results;
        }
        for (int i = 0; i < dailyGroup.length; i++) {
            results.add(wrapDailyCompositeNotice(dailyGroup[i],
                                                 compositeGrouper));
        }
        return results;
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

    public void run() {
        while (flag) {
            SimpleUser user = null;
            try{
                user = queue.take();
                if (user == null) {
                    lastUserClearInfo();
                    continue;
                }

                if(getCurrentWeek()==triggerWeek ){
                    sendWeekNotice(user);
                }
                lastUserClearInfo();
            } catch (InterruptedException e) {
                LOG.error("send mail InterruptedException. {uid:" + user.getUid() +"}", e);
                try {
                    lastUserClearInfo();
                } catch (InterruptedException e1) {
                    LOG.error("清理用户信息时出现错误", e);
                }
                continue;
            } catch (Throwable e) {
                LOG.error("send mail error. {uid:" + user.getUid() +"}", e);
                try {
                    lastUserClearInfo();
                } catch (InterruptedException e1) {
                    LOG.error("清理用户信息时出现错误", e);
                }
                continue;
            }
        }
    }

    private void sendWeekNotice(SimpleUser user){
        List<TeamPreferences> prefList = teamPreferencesCache.get(WEEK_PREFIX + user.getUid());
        if (CommonUtils.isNull(prefList)) {
            return;
        }
        TeamNotices notice = new TeamNotices();

        // 团队更新
        List<Notice> teamNotices = new ArrayList<Notice>();
        Map<Integer,Team> mapTeam = new HashMap<Integer,Team>();
        for (TeamPreferences p : prefList) {
            if(mapTeam.get(p.getTid())==null){
                mapTeam.put(p.getTid(), teamService.getTeamByID(p.getTid()));
                List<Notice> list = noticesCache.get(WEEK_PREFIX + p.getTid() + "_" + NoticeRule.TEAM_NOTICE);
                if(!CommonUtils.isNull(list)){
                    teamNotices.addAll(list);
                }
            }
        }
        notice.setTeamNoticeSize(teamNotices == null ? 0 : teamNotices.size());

        teamNotices = recommendationService.getRecommendNotices(teamNotices, user.getUid(), getWord2VEC());
        //teamNotices = getListTop(teamNotices,5);
        CompositeNotice[] compositeGroup = getCompositeNoticeArray(filterNotice(teamNotices,user.getUid()));
        //取前5条
        compositeGroup = getArrayTop(compositeGroup, 5);
        List<DailyCompositeNotice> results = new ArrayList<DailyCompositeNotice>();
        DailyCompositeNotice item = new DailyCompositeNotice();
        item.setCompositeArray(compositeGroup);
        results.add(item);
        notice.setTeamNoticeList(results);

        // 消息
        List<Notice> personNotices = filterNotice(noticesCache.get(WEEK_PREFIX + user.getUid() + "_" + NoticeRule.PERSON_NOTICE),user.getUid());
        notice.setPersonNoticeSize(personNotices == null ? 0 : personNotices.size());
        personNotices = getListTop(personNotices,5);
        notice.setPersonNoticeList(getDailyNoticeArray(personNotices));

        // 关注
        List<Notice> monitorNotices =filterNotice( noticesCache.get(WEEK_PREFIX + user.getUid() + "_" + NoticeRule.MONITOR_NOTICE),user.getUid());
        notice.setMonitorNoticeSize(monitorNotices == null ? 0 : monitorNotices.size());
        //monitorNotices = removeDuplicateNotice(monitorNotices);
        monitorNotices = getListTop(monitorNotices,5);
        notice.setMonitorNoticeList(getDailyNoticeArray(monitorNotices));

        if (notice.isEmpty()) {
            LOG.info(user.getUid() + " week notices is empty!");
            return;
        }
        Map<String, Object> map = new JSONMap();
        map.put("notice", notice);
        map.put("personNotices", personNotices);
        map.put("monitorNotices", monitorNotices);
        map.put("user", user);
        map.put("baseUrl", baseUrl);
        map.put("baseAddress", baseAddress);
        map.put("teamCache", mapTeam);

        Calendar calBegin=Calendar.getInstance();
        calBegin.add(Calendar.DAY_OF_MONTH, -7);
        Calendar calEnd=Calendar.getInstance();
        calEnd.add(Calendar.DAY_OF_MONTH, -1);
        map.put("beginDate", calBegin.get(Calendar.YEAR) + "." + (calBegin.get(Calendar.MONTH)+1) + "." + (calBegin.get(Calendar.DAY_OF_MONTH)));
        map.put("endDate", calEnd.get(Calendar.YEAR) + "." + (calEnd.get(Calendar.MONTH)+1)+"."+(calEnd.get(Calendar.DAY_OF_MONTH)));

        NoticeMailHelper.sendAllNoticeMailWeek(user.getEmail(), map);

        increaseEmailCount();
    }

    private void sendTodayNotice(SimpleUser user){
        List<TeamPreferences> prefList = teamPreferencesCache.get(user.getUid());
        if (CommonUtils.isNull(prefList)) {
            return;
        }
        List<TeamNotices> notices = new ArrayList<TeamNotices>();
        int teamCount = 0;
        for (TeamPreferences p : prefList) {
            try {
                if (teamCount > DEFAULT_TEAM_COUNT) {
                    break;
                }
                Param param = paramCache.get(user.getUid() + "_"
                                             + p.getTid());
                // 用户默认没有勾选，或者已经勾选了，那么就骚扰
                if (param == null
                    || ParamConstants.NoticeEmailAllType.VALUE_CHECKED
                    .equals(param.getValue())) {
                    TeamNotices tNotices = getTeamNotices(
                        user.getUid(), p.getTid());
                    if (tNotices != null && !tNotices.isEmpty()) {
                        teamCount++;
                        notices.add(tNotices);
                    }
                } else {
                    continue;
                }
            } catch (Throwable e) {
                LOG.error(user.getUid() + "@" + p.getId() + ":" + e.getMessage(), e);
                continue;
            }
        }
        if (!notices.isEmpty()) {
            Map<String, Object> map = new JSONMap();
            map.put("notices", notices);
            map.put("user", user);
            map.put("baseUrl", baseUrl);
            map.put("baseAddress", baseAddress);
            NoticeMailHelper
                    .sendAllNoticeMail(user.getEmail(), map);
            increaseEmailCount();
        } else {
            LOG.info(user.getUid() + " notices is empty!");
        }
    }

    /**
     * 过滤用户设置屏蔽的团队消息
     * @param noticeList
     * @param uid
     * @return
     */
    private List<Notice> filterNotice(List<Notice> noticeList, String uid){
        if(noticeList == null){
            return new ArrayList<Notice>();
        }
        List<Notice> list = new ArrayList<Notice>();
        for(Notice item : noticeList){
            Param param = paramCache.get(uid + "_" + item.getTid());
            // 用户默认没有勾选，或者已经勾选了，那么就骚扰
            if (param == null || ParamConstants.NoticeEmailAllType.VALUE_CHECKED.equals(param.getValue())) {
                list.add(item);
            }
        }
        return list;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public synchronized void shutdown() {
        clearCache();
        flag = false;
        interrupt();
    }

    public static int getCurrentWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setRecommendationService(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public void setTriggerWeek(int triggerWeek) {
        this.triggerWeek = triggerWeek;
    }
    public void setWordVectorPath(String wordVectorPath) {
        WORD_VECTOR_PATH = wordVectorPath;
    }

    /**
     * 去除重复notice 对象ID作为唯一条件
     * @param list
     * @return
     */
    private List<Notice> removeDuplicateNotice(List<Notice> list){
        if(list==null){
            return new ArrayList<Notice>();
        }
        List<Notice> newList = new ArrayList<Notice>();
        Map<String, Notice> map = new HashMap<String, Notice>();
        for(Notice item : list){
            if(!map.containsKey(item.getTarget().getId())){
                map.put(item.getTarget().getId(), item);
                newList.add(item);
            }
        }
        return newList;
    }

}
