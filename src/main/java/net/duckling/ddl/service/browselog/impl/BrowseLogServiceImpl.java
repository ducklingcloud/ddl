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
package net.duckling.ddl.service.browselog.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.duckling.ddl.service.browselog.BrowseLog;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.browselog.BrowseStat;
import net.duckling.ddl.service.jobmaster.JobmasterService;
import net.duckling.ddl.service.timer.JobTask;
import net.duckling.ddl.service.timer.TimerService;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrowseLogServiceImpl implements BrowseLogService {
    private class CleanTask implements JobTask {
        @Override
        public void execute(Date scheduledDate) {
            cleanPageViewToRepository();
        }
    }

    private class TransferTask implements JobTask {
        @Override
        public void execute(Date scheduledDate) {
            Date yesterday = DateUtils.addHours(scheduledDate, -1);
            transferToHistory(yesterday);
        }

    }

    private List<BrowseLog> cache;

    private int capacity;
    @Autowired
    private HistoryPageViewDAO historyDao;
    @Autowired
    private JobmasterService jobmaster;
    @Autowired
    private TimerService timerService;
    @Autowired
    private TodayPageViewDAO todayDao;

    private String buildJobName(Date currentDay) {
        return "pageview"+DateFormatUtils.format(currentDay, "yyyy-MM-dd");
    }

    private void cleanPageViewToRepository() {
        List<BrowseLog> oldCache;
        synchronized (this) {
            if (cache.size()>0){
                oldCache = cache;
                cache = new ArrayList<BrowseLog>();
            }else{
                return;
            }
        }
        todayDao.batchSave(oldCache);
    }
    private void savePageView(BrowseLog log) {
        synchronized (this) {
            cache.add(log);
            if (cache.size() >= capacity) {
                cleanPageViewToRepository();
            }
        }
    }

    private void transferToHistory(Date currentDay) {
        if (jobmaster.take(buildJobName(currentDay))){
            List<BrowseLog> browseLog = todayDao.getPageViewAt(currentDay);
            historyDao.savePageView(browseLog);
        }
    }

    @PreDestroy
    public void doDestroy() {
        timerService.removeTask("CleanJob");
        timerService.removeTask("TransferTask");
    }

    @PostConstruct
    public void doInit() {
        this.capacity = 10;
        this.cache = new ArrayList<BrowseLog>();
        timerService.addMinutelyTask("CleanJob", new CleanTask());
        timerService.addDailyTask("TransferTask", 17, 19, new TransferTask());
    }

    public List<BrowseStat> getTopPageView(int tid, int length, int daysAgo) {
        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date dateOfDaysAgo = DateUtils.addDays(now, -daysAgo);
        return historyDao.getTopPageView(tid, length, dateOfDaysAgo);
    }

    @Override
    public int getVisitCount(int rid) {
        return historyDao.countHots(rid) + todayDao.countHots(rid);
    }

    @Override
    public List<BrowseLog> getVisitor(int rid, int count) {
        List<BrowseLog> historyLog = historyDao.getResourceVisitor(rid, count);
        List<BrowseLog> todayLog = todayDao.getResourceVisitor(rid, count);
        todayLog.addAll(historyLog);
        if(todayLog==null||todayLog.isEmpty()){
            return Collections.emptyList();
        }else if(todayLog.size()<count){
            return todayLog;
        }
        return todayLog.subList(0, count);
    }

    @Override
    public void resourceVisited(int tid, int rid, String uid, String userName,
                                String item_type) {
        BrowseLog log = new BrowseLog();
        log.setTid(tid);
        log.setBrowseTime(new Date());
        log.setRid(rid);
        log.setUserId(uid);
        log.setDisplayName(userName);
        log.setItemType(item_type);
        savePageView(log);
    }
}
