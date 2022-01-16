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
package net.duckling.ddl.service.mail.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.ParamConstants;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.jobmaster.JobmasterService;
import net.duckling.ddl.service.param.IParamService;
import net.duckling.ddl.service.param.Param;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.timer.JobTask;
import net.duckling.ddl.service.timer.TimerService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.ReflectUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author lvly
 * @since 2012-11-6
 */
@Service
public class EmailNoticeJob implements JobTask {
    private static final Logger LOG = Logger.getLogger(EmailNoticeJob.class);
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private INoticeService noticeService;
    @Autowired
    private IParamService paramService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private TimerService timerService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private JobmasterService jobmaster;
    @Value("${duckling.emailnotice.triggertime}")
    private String triggerTime; //邮件触发时间 格式为 HH:MM HH为0-24 MM为0-60 如 11:0 十一点触发
    @Value("${duckling.emailnotice.triggerdayofweek}")
    private int triggerWeek;

    /**
     * @param readTodayNotification
     * @return
     */
    private boolean addCacheToThread() {

        List<Notice> weekNotices = null;
        boolean hasWeekNotice = false;
        if(EmailNoticeThread.getCurrentWeek() == triggerWeek){
            weekNotices = noticeService.readThisWeekWithoutHistory();
            if (CommonUtils.isNull(weekNotices)) {
                LOG.info("this week has no notices!~");
            }else{
                hasWeekNotice = true;
            }
        }

        if(!hasWeekNotice){
            return false;
        }

        // param
        List<Param> params = paramService.getParamByType(ParamConstants.NoticeEmailAllType.TYPE);
        for (Param p : params) {
            EmailNoticeThread.addParam(p);
        }
        LOG.info("param number " + getCollectionSize(params));
        Set<Integer> personTeams = getPersonTeamIds();

        if(hasWeekNotice){
            weekCache(weekNotices, personTeams);
        }
        return true;
    }


    /**
     * 天消息缓存
     * @param notices
     * @param personTeams
     */
    private void todayCache(List<Notice> notices, Set<Integer> personTeams){
        // 今天产生notice的team
        Set<Integer> tids = new HashSet<Integer>();
        for (Notice notice : notices) {
            if(isFilterNotice(notice, personTeams)){
                continue;
            }
            tids.add(notice.getTid());
            dealPageName(notice);
            EmailNoticeThread.addNotice(notice.getRecipient() + "_" + notice.getTid() + "_"
                                        + notice.getNoticeType(), notice);
        }
        LOG.info("notices number " + getCollectionSize(notices));
        if(tids.size()==0){
            return;
        }
        // teamMember
        List<TeamPreferences> teams = teamPreferenceService
                .getAllTeamPreferencesByTids(tids);
        for (TeamPreferences team : teams) {
            EmailNoticeThread.addTeamPreference(team.getUid(), team);
            EmailNoticeThread.addExactTeamPreference(team.getUid() + "_" + team.getTid(), team);
        }
        LOG.info("teams number " + getCollectionSize(teams));
    }

    /**
     * 周动态数据缓存
     */
    private void weekCache(List<Notice> notices, Set<Integer> personTeams){
        //本周产生notice的team
        Set<Integer> weekTids = new HashSet<Integer>();
        for (Notice item : notices) {
            if(isFilterNotice(item, personTeams)){
                continue;
            }
            weekTids.add(item.getTid());
            dealPageName(item);
            EmailNoticeThread.addNotice(EmailNoticeThread.WEEK_PREFIX + item.getRecipient() +  "_" + item.getNoticeType(), item);
        }
        LOG.info("week notices number " + getCollectionSize(notices));
        if(weekTids.size()==0){
            return;
        }
        List<TeamPreferences> teamsWeek = teamPreferenceService.getAllTeamPreferencesByTids(weekTids);
        for (TeamPreferences team : teamsWeek) {
            EmailNoticeThread.addTeamPreference(EmailNoticeThread.WEEK_PREFIX + team.getUid(), team);
            //EmailNoticeThread.addExactTeamPreference(EmailNoticeThread.WEEK_PREFIX + team.getUid() + "_" + team.getTid(), team);
        }
        LOG.info("week teams number " + getCollectionSize(teamsWeek));
    }

    private Set<Integer> getPersonTeamIds() {
        List<Team> teams = teamService.getTeamByType(Team.PESONAL_TEAM);
        Set<Integer> ids = new HashSet<Integer>(teams.size());
        for(Team t : teams){
            ids.add(t.getId());
        }
        return ids;
    }

    private boolean isFilterNotice(Notice notice,Set<Integer> personTeams){
        if(notice.getTid()==1){
            return true;
        }
        if(personTeams.contains(notice.getTid())){
            return true;
        }
        return false;
    }

    private void dealPageName(Notice n){
        if(n!=null&&n.getTarget()!=null){
            DEntity de = n.getTarget();
            try{
                if(LynxConstants.TYPE_PAGE.equals(de.getType())&&!de.getName().endsWith(".ddoc")){
                    de.setName(de.getName()+".ddoc");
                }
            }catch(Exception e){}
        }
    }

    private int getCollectionSize(Collection<?> c) {
        if (c == null) {
            return 0;
        } else {
            return c.size();
        }
    }

    @PostConstruct
    public void init() {
        List<Integer> time = getTriggerTime();
        timerService.addDailyTask("EmailNoticeJob",time.get(0), time.get(1), this);
    }

    private List<Integer> getTriggerTime(){
        List<Integer> result = new ArrayList<Integer>();
        if(StringUtils.isNotEmpty(triggerTime)){
            String[] s = triggerTime.split(":");
            if(s.length!=2){
                result.add(1);
                result.add(0);
            }else{
                int i = 1;
                try{
                    i = Integer.parseInt(s[0]);
                }catch(Exception e){}
                result.add(i);
                try{
                    i=Integer.parseInt(s[1]);
                }catch(Exception e){}
                result.add(i);
            }
        }else{
            result.add(1);
            result.add(0);
        }
        return result;
    }

    @PreDestroy
    public void destroy() {
        timerService.removeTask("EmailNoticeJob");
        timerService = null;
    }

    @Override
    public void execute(Date scheduledDate) {
        if(getToken(scheduledDate)){
            long start = System.currentTimeMillis();
            LOG.info("email notice job start!");
            ReflectUtils.setValue(noticeService, "aoneUserService", aoneUserService);
            List<SimpleUser> allUsers = aoneUserService.getAllSimpleUser();
            if (CommonUtils.isNull(allUsers)) {
                LOG.info("the email notice job is interupt,cause the users's setting is all empty");
            }
            boolean data = addCacheToThread();

            EmailNoticeThread.resetEmailCount();
            if (data) {
                // 每个用户一封邮件
                for (SimpleUser user : allUsers) {
                    EmailNoticeThread.addUser(user);
                }
            }
            EmailNoticeThread.END_FLAG = true;
            long end = System.currentTimeMillis();
            LOG.info("email notice job end! cost:" + (end - start)
                     + "ms;user number " + getCollectionSize(allUsers));
        }
    }

    private boolean getToken(Date d){
        return jobmaster.take(buildJobName(d));
    }

    private String buildJobName(Date currentDay) {
        return "emailNotice"+DateFormatUtils.format(currentDay, "yyyy-MM-dd");
    }

}
