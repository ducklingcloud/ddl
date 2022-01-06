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
package net.duckling.ddl.service.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.TeamSpaceSize;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.TeamSpaceConfigService;

@Service
public class TeamSpaceSizeServiceImpl implements TeamSpaceSizeService {
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private DucklingProperties config;
    @Autowired
    private TeamSpaceConfigService teamSpaceConfigService;
    @Override
    public TeamSpaceSize getTeamSpaceSize(int tid) {
        long used = resourceService.getTeamResourceSize(tid);
        return new TeamSpaceSize(tid, used, getTeamTotalSize(tid));
    }

    private long getTeamTotalSize(int tid){
        return teamSpaceConfigService.getTeamSpaceConfig(tid).getSize();
    }

    @Override
    public long updateTeamResSize(int tid) {
        return resourceService.updateTeamResSize(tid);
    }

    @Override
    public boolean validateTeamSize(int tid) {
        return validateTeamSize(tid, 0);
    }

    @Override
    public boolean validateTeamSize(int tid, long size) {
        //是否开启空间控制
        String open = config.getProperty("duckling.team.space.open");
        if(!"true".equalsIgnoreCase(open)){
            return true;
        }
        return (resourceService.getTeamResourceSize(tid)+size)<=getTeamTotalSize(tid);
    }

    @Override
    public boolean validateTeamSize(int tid, List<Resource> rs) {
        long sum = 0;
        for(Resource r : rs){
            sum+=r.getSize();
        }
        return validateTeamSize(tid, sum);
    }

    @Override
    public void validateTeamSizes(int tid, long size) throws NoEnoughSpaceException {
        if(!validateTeamSize(tid, size)){
            NoEnoughSpaceException n = new NoEnoughSpaceException("您的空间已达上限，如需要增加空间，请发邮件联系vlab@cnic.cn");
            n.setTid(tid);
            throw n;
        }
    }

    @Override
    public void resetTeamResSize(int tid) {
        resourceService.resetTeamResSize( tid);
    }


}
