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
package net.duckling.ddl.service.user.impl;

import java.util.Date;

import net.duckling.ddl.service.user.UserSortPreference;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSortPreferenceServiceImpl implements UserSortPreferenceService {
    @Autowired
    private UserSortPreferenceDao userSortPreferenceDao;
    @Autowired
    private ICacheService memcachedService;


    @Override
    public String getUidSortPreference(String uid, String userChoiceSort, String useType) {
        if(StringUtils.isEmpty(userChoiceSort)){
            return getSortOnly(uid, useType);
        }else{
            String r = getSortOnly(uid, useType);
            if(!userChoiceSort.equals(r)){
                updateUserPreference(uid, userChoiceSort, useType);
            }
            return userChoiceSort;
        }
    }
    private String getSortOnly(String uid,String useType){
        String result = (String)memcachedService.get(getMemKey(uid, useType));
        if(StringUtils.isNotEmpty(result)){
            return result;
        }else{
            UserSortPreference user = userSortPreferenceDao.query(uid, useType);
            if(user!=null){
                memcachedService.set(getMemKey(uid, useType), user.getSortType());
                return user.getSortType();
            }
            return null;
        }
    }
    private void updateUserPreference(String uid,String userChoiceSort,String useType ){
        UserSortPreference user = userSortPreferenceDao.query(uid, useType);
        if(user==null){
            user = new UserSortPreference();
            user.setUid(uid);
            user.setSortType(userChoiceSort);
            user.setType(useType);
            user.setLastModify(new Date());
            userSortPreferenceDao.create(user);
        }else{
            user.setSortType(userChoiceSort);
            user.setLastModify(new Date());
            userSortPreferenceDao.update(user);
        }
        memcachedService.set(getMemKey(uid, useType), user.getSortType());
    }


    private String getMemKey(String uid,String useType){
        return "user-sort:"+uid+":type"+useType;
    }

}
