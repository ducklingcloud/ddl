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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.IStarmarkService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.Starmark;
import net.duckling.ddl.util.PaginationBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StarmarkServiceImpl implements IStarmarkService {
    @Autowired
    private StarmarkDAO starmarkDAO;
    @Autowired
    private IResourceService resourceService;

    @Override
    public void addStarmark(String uid, int rid,int tid) {
        Starmark s = Starmark.build(uid, tid, rid);
        int sid = starmarkDAO.create(s);
        s.setId(sid);
        Resource res = resourceService.getResource(rid);
        res.getMarkedUserSet().add(uid);
        resourceService.updateMarkedUserSet(Arrays.asList(new Resource[]{res}));
        //如果当前属于某个bundle则更新bundle的maredUserSet
    }

    @Override
    public void removeStarmark(String uid,int rid,int tid){
        Resource res = resourceService.getResource(rid);
        res.getMarkedUserSet().remove(uid);
        resourceService.updateMarkedUserSet(Arrays.asList(new Resource[]{res}));
        starmarkDAO.batchDelete(uid, tid, Arrays.asList(new Long[]{(long)rid}));
    }

    @Override
    public void removeResourceStarmark(Resource r) {
        Set<String> uid = r.getMarkedUserSet();
        starmarkDAO.batchDeleteResourceStar(r.getTid(), r.getRid(), uid);
        r.setMarkedUserSet(new HashSet<String>());
        resourceService.updateMarkedUserSet(Arrays.asList(new Resource[]{r}));
    }

    @Override
    public PaginationBean<Resource> getMyStartFiles(int tid, String uId,
                                                    int offset, int size, String order, String keyWord) {
        return starmarkDAO.getMyStartFiles(tid, uId, offset, size, order, keyWord);
    }
}
