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
package net.duckling.ddl.service.search.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.duckling.ddl.service.search.SearchLogAnalysis;
import net.duckling.ddl.service.search.UserAnalysis;
import net.duckling.ddl.service.timer.JobTask;
import net.duckling.ddl.service.timer.TimerService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li zexin 2012-09-01
 */
@Service
public class AnalysisTimer implements JobTask {
    private static final Logger LOG = Logger.getLogger(AnalysisTimer.class);
    @Autowired
    private SearchLogAnalysis searchLogAnalysis;
    @Autowired
    private UserAnalysis userAnalysis;
    @Autowired
    private TimerService timerService;

    @PostConstruct
    public void init() {
        timerService.addDailyTask("AnalysisTimer", 3, 0, this);
    }

    @PreDestroy
    public void destroy() {
        timerService.removeTask("AnalysisTimer");
    }

    /*
     * 每天定时分析当天日志
     */
    @Override
    public void execute(Date scheduledDate) {
        try {
            Date date = new Date();
            int day = 1;// 每天凌晨更新，添加一天以及以前的所有记录
            Calendar now = Calendar.getInstance();
            now.setTime(date);
            now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
            SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd");
            String endLogDate = edf.format(now.getTime());
            searchLogAnalysis.saveLog(endLogDate, endLogDate);
        } catch (IOException e) {
            LOG.error("AnalysisTime ", e);
        }
        LOG.info("AnalysisTimer job begin");
        searchLogAnalysis.anyasisLog();
        userAnalysis.analysisUser();
        LOG.info("AnalysisTimer job end");
    }

}
