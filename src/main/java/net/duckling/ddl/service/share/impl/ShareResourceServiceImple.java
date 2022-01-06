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
package net.duckling.ddl.service.share.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.impl.ResourceDAO;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceDao;
import net.duckling.ddl.service.share.ShareResourceService;

@Service
public class ShareResourceServiceImple implements ShareResourceService {
    @Autowired
    private ShareResourceDao shareResourceDao;
    @Autowired
    private ResourceDAO resourceDao;

    @Override
    public int add(ShareResource sr) {
        int result = shareResourceDao.add(sr);
        resourceDao.updateShared(sr.getRid(), true);
        return result;
    }

    @Override
    public int update(ShareResource sr) {
        return shareResourceDao.update(sr);
    }

    @Override
    public boolean delete(int id) {
        boolean result = shareResourceDao.delete(id);
        resourceDao.updateShared(id, false);
        return result;
    }

    @Override
    public ShareResource get(int id) {
        return shareResourceDao.get(id);
    }

    @Override
    public List<ShareResource> queryByTid(int tid) {
        return shareResourceDao.queryByTid(tid);
    }

    @Override
    public List<Resource> queryTeamShareResource(int tid) {
        return shareResourceDao.queryTeamShareResource(tid);
    }

}
