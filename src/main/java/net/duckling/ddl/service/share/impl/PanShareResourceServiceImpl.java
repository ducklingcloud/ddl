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

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceDao;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.meepo.api.IPanService;

@Service
public class PanShareResourceServiceImpl implements PanShareResourceService {
    @Autowired
    private PanShareResourceDao panShareResourceDao;
    @Autowired
    private IPanService service;

    @Override
    public int add(PanShareResource panShareResource) {
        return panShareResourceDao.add(panShareResource);
    }

    @Override
    public void update(PanShareResource panShareResource) {
        panShareResourceDao.update(panShareResource);
    }

    @Override
    public void delete(int id) {
        PanShareResource pan = get(id);
        if(pan!=null){
            pan.setStatus(LynxConstants.STATUS_DELETE);
            pan.setPassword(null);
            panShareResourceDao.update(pan);
        }
    }

    @Override
    public PanShareResource get(int id) {
        return panShareResourceDao.get(id);
    }
    public PanShareResource getByPath(String uid,String path){
        List<PanShareResource> ls = getByUid(uid);
        if(ls!=null){
            for(PanShareResource p:ls){
                if(path.equals(p.getSharePath())){
                    return p;
                }
            }
        }
        return null;
    }

    public PanShareResource queryByPath(String uid,String path){
        List<PanShareResource> list = panShareResourceDao.getByPath(uid, path);
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public PanShareResource getAllByPath(String uid,String path){
        List<PanShareResource> ls = getAllByUid(uid);
        if(ls!=null){
            for(PanShareResource p:ls){
                if(path.equals(p.getSharePath())){
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public List<PanShareResource> getByUid(String uid) {
        return panShareResourceDao.getByUid(uid);
    }
    @Override
    public List<PanShareResource> getAllByUid(String uid) {
        return panShareResourceDao.getAllByUid(uid);
    }

}
