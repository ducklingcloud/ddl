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

import java.util.Date;

import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageLockServiceImpl implements PageLockService {
    @Autowired
    private PageLockProvider lockProvider;

    @Override
    public PageLock getCurrentLock(int tid, int pageid){
        return lockProvider.getCurrentLock(tid,pageid);
    }

    @Override
    public void unlockPage(int tid,int pid,String uid){
        lockProvider.unlockPage(tid,pid,uid);
    }

    @Override
    public void updateLockTime(int tid,int pageid) {
        lockProvider.updateLockTime(tid,pageid);
    }

    @Override
    public long getLeftTimeOfPageLock(int tid,int pid) {
        return lockProvider.getLeftTimeOfPageLock(tid,pid);
    }

    @Override
    public boolean isLockTimeOut(int tid,int pid) {
        PageLock lock = lockProvider.getCurrentLock(tid,pid);
        if(lock!=null){
            return lockProvider.isTimeOut(lock.getLastAccess(), new Date());
        }
        return true;
    }

    @Override
    public PageLock lockPage(int tid,int pid,String uid,int version){
        return lockProvider.lockPage(tid, pid, uid,version);
    }

    @Override
    public boolean isLockTimeOut(PageLock lock) {
        return lockProvider.isTimeOut(lock.getLastAccess(), new Date());
    }
}
