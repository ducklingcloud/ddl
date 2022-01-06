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
package net.duckling.ddl.service.pan;

import java.util.Date;
import java.util.List;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.exception.MessageException;
import net.duckling.ddl.service.pan.dao.PanSpaceApplicationDao;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoUsage;

@Service
public class PanSpaceApplicationServiceImpl implements PanSpaceApplicationService {
    private final static String MSG_QUOTA_ERR = "同步盘扩容失败.";

    @Autowired
    private PanSpaceApplicationDao panSpaceApplicationDao;
    @Autowired
    private IPanService panService;

    @Override
    public int add(long newSize, long originalSize, String uid, String applicationType) {
        PanSpaceApplication bean = new PanSpaceApplication();
        bean.setApplicationTime(new Date());
        bean.setApplicationType(applicationType);
        bean.setApproveTime(new Date());
        bean.setNewSize(newSize);
        bean.setOriginalSize(originalSize);
        bean.setUid(uid);
        return panSpaceApplicationDao.create(bean);
    }

    @Override
    public void updateSpaceAllocate(String uid, long size, PanAcl acl) throws MeePoException, MessageException {
        MeePoUsage use = panService.usage(acl);

        String rootToken = "product".equals(config.getProperty("ddl.profile.env")) ? panService.getRootToken("root", "cas__", "iphone") :
                panService.getRootToken("root", "meiyoumima", "iphone");
        long newQuota =  use.quota + size;
        MeePoUsage newUsage = panService.updateQuota(acl, rootToken, newQuota);
        if(newUsage.quota!=newQuota){
            throw new MessageException(MSG_QUOTA_ERR);
        }
        this.add(newQuota, use.quota, acl.getUid(), PanSpaceApplication.TYPE_ACTIVITY);
    }

    @Override
    public List<PanSpaceApplication> queryByUid(String uid) {
        return panSpaceApplicationDao.queryByUid(uid);
    }

    @Override
    public void delete(int id) {
        panSpaceApplicationDao.delete(id);
    }

    public void setPanSpaceApplicationDao(PanSpaceApplicationDao panSpaceApplicationDao) {
        this.panSpaceApplicationDao = panSpaceApplicationDao;
    }

    @Autowired
    private DucklingProperties config;
}
