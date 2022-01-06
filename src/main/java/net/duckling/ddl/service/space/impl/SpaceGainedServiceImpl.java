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
package net.duckling.ddl.service.space.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.meepotech.sdk.MeePoException;

import net.duckling.ddl.exception.MessageException;
import net.duckling.ddl.service.pan.PanSpaceApplicationService;
import net.duckling.ddl.service.space.SpaceGained;
import net.duckling.ddl.service.space.SpaceGainedService;
import net.duckling.ddl.service.space.dao.SpaceGainedDao;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.meepo.api.PanAcl;
@Service
public class SpaceGainedServiceImpl implements SpaceGainedService{
    private static final Logger LOG = Logger.getLogger(SpaceGainedServiceImpl.class);

    @Override
    public int add(SpaceGained spaceGained) {
        return spaceGainedDao.insert(spaceGained);
    }

    @Override
    public List<SpaceGained> getList(String uid, Integer objId, String remark, Integer spaceType) {
        return spaceGainedDao.getList(uid, objId, remark, spaceType);
    }

    @Override
    public List<SpaceGained> getList(String uid, Integer objId, String remark) {
        return spaceGainedDao.getList(uid, objId, remark);
    }

    public void addSpaceOfTeam(String uid, Integer objId, Long size, String remark){
        addSpace(uid, objId, size, remark, SpaceGained.SPACE_TYPE_TEAM, null);
    }

    public void addSpaceOfPan(String uid, Integer objId, Long size, String remark, PanAcl acl){
        addSpace(uid, objId, size, remark, SpaceGained.SPACE_TYPE_PAN, acl);
    }

    private void addSpace(String uid, Integer objId, Long size, String remark, int spaceType,  PanAcl acl){
        List<SpaceGained> gainedList = this.getList(uid, objId, remark);
        //检查是否存在，仅记录一次
        if(gainedList.size()>0){
            return;
        }
        SpaceGained obj = new SpaceGained();
        obj.setUid(uid);
        obj.setObjId(objId);
        obj.setObjType(SpaceGained.OBJ_TYPE_ACTIVITY);
        obj.setSpaceType(spaceType);
        obj.setSize(size);
        obj.setRemark(remark);
        obj.setCreateTime(new Date());
        try{
            spaceGainedDao.insert(obj);

            if(spaceType == SpaceGained.SPACE_TYPE_TEAM){
                dealSpaceTypeOfTeam(uid, size);
            }else if(spaceType == SpaceGained.SPACE_TYPE_PAN){
                dealSpaceTypeOfPan(uid, size, acl);
            }

        }catch(DuplicateKeyException e){
            //防止重复记录，数据库已创建联合唯一索引(uid,objId,remark);
        }
    }

    /**
     * 团队空间，记录用户未分配空间
     * @param uid
     * @param size
     */
    private void dealSpaceTypeOfTeam(String uid, long size){
        //更新用户未分配空间数量
        UserExt older = aoneUserService.getUserExtInfo(uid);
        older.setUnallocatedSpace(older.getUnallocatedSpace() + size);
        aoneUserService.modifyUserProfile(older);
    }

    /**
     * pan空间，自动分配到盘
     * @param uid
     * @param size
     */
    private void dealSpaceTypeOfPan(String uid, long size, PanAcl acl){
        try {
            panSpaceApplicationService.updateSpaceAllocate(uid, size, acl);
        } catch (MeePoException | MessageException e) {
            LOG.error("sapce of pan allocated error.uid:"+uid+", size:"+size+", error message:" + e.getMessage());
        }
    }

    @Autowired
    SpaceGainedDao spaceGainedDao;
    @Autowired
    AoneUserService aoneUserService;
    @Autowired
    private PanSpaceApplicationService panSpaceApplicationService;
}
