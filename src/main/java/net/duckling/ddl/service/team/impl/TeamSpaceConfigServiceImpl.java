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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.service.team.TeamSpaceConfig;
import net.duckling.ddl.service.team.TeamSpaceConfigService;
import net.duckling.falcon.api.cache.ICacheService;

@Service
public class TeamSpaceConfigServiceImpl implements TeamSpaceConfigService{
    @Override
    public TeamSpaceConfig getTeamSpaceConfig(int tid) {
        TeamSpaceConfig result = getFromCache(tid);
        if(result == null){
            result = getConfig(tid);
        }
        return result;
    }
    private TeamSpaceConfig getConfig(int tid) {
        TeamSpaceConfig result = teamSpaceConfigDAO.getTeamSpaceConfig(tid);
        if(result ==null){
            result = new TeamSpaceConfig();
            result.setSize(getDefaultTeamSize());
            result.setTid(tid);
        }
        updateCache(result);
        return result;
    }

    private long getDefaultTeamSize(){
        return Long.valueOf(systemProperty.getProperty("duckling.team.default.size"));
    }

    private TeamSpaceConfig getFromCache(int tid){

        return (TeamSpaceConfig) memcachedService.get(getCacheKey(tid));
    }

    private void updateCache(TeamSpaceConfig config){
        memcachedService.set(getCacheKey(config.getTid()), config);
    }

    private String getCacheKey(int tid){
        return "team-space-config-tid-"+tid;
    }
    @Override
    public boolean update(TeamSpaceConfig config) {
        if(config.getId()<=0){
            return false;
        }
        memcachedService.remove(getCacheKey(config.getTid()));
        return teamSpaceConfigDAO.update(config);
    }

    @Override
    public List<TeamSpaceConfig> getAllTeamSpaceConfig() {
        return teamSpaceConfigDAO.getAllTeamSpaceConfig();
    }
    @Override
    public boolean insert(TeamSpaceConfig c) {
        teamSpaceConfigDAO.insert(c);
        updateCache(c);
        return true;
    }
    @Override
    public void delete(int id) {
        TeamSpaceConfig config = teamSpaceConfigDAO.getById(id);
        if(config!=null){
            memcachedService.remove(getCacheKey(config.getTid()));
        }
        teamSpaceConfigDAO.delete(id);
    }

    @Autowired
    private ICacheService memcachedService;
    @Autowired
    private DucklingProperties systemProperty;
    @Autowired
    private TeamSpaceConfigDAO teamSpaceConfigDAO;
}
