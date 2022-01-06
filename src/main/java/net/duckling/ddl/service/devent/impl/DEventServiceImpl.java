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
import java.util.List;

import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.DEvent;
import net.duckling.ddl.service.devent.DEventBody;
import net.duckling.ddl.service.devent.IDEventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @date 2011-11-2
 * @author clive
 */
@Service
public class DEventServiceImpl implements IDEventService {
    @Autowired
    private DEventDAO eventDao;

    @Override
    public String getHistoryReciever(DEvent e) {
        return e.getEventBody().getActor();
    }

    @Override
    public List<String> getPersonReciever(DEvent e) {
        return new ArrayList<String>();
    }

    @Override
    public String getTeamReciever(DEvent e) {
        return e.getEventBody().getTid()+"";
    }

    @Override
    public String[] choseNoticeType(DEntity entity) {
        return null;
    }

    @Override
    public int writeEvent(DEventBody e) {
        return eventDao.saveEvent(e);
    }

    @Override
    public DEventBody readEvent(int id) {
        return eventDao.queryEventByID(id);
    }

}
