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
package net.duckling.ddl.service.jobmaster.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import net.duckling.ddl.service.jobmaster.JobmasterService;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JobmasterServiceImpl implements JobmasterService {
    private static final Logger LOG = Logger
            .getLogger(JobmasterServiceImpl.class);
    @Autowired
    private ICacheService cacheService;
    @Autowired
    private JobRecordDAO jobRecords;

    @PostConstruct
    public void doInit() {
    }

    @PreDestroy
    public void doDestroy() {
    }

    @Override
    public boolean take(String jobName) {
        String key = getCacheKey(jobName);
        if (cacheService.get(key) == null) {
            cacheService.set(key, "1", 60);
            if (!jobRecords.exist(jobName)) {
                jobRecords.saveJob(jobName);
                return true;
            }
        }
        return false;
    }

    /**
     * memcached key不能包含空格
     * @param jobName
     * @return
     */
    private String getCacheKey(String jobName){
        return jobName.replaceAll(" ", "_");
    }
}
