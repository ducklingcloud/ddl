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

package cn.vlabs.duckling.vwb.services.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import net.duckling.ddl.service.oauth.dao.AccessorDAOImpl;
import net.duckling.ddl.service.oauth.impl.AccessToken;
import net.duckling.ddl.service.oauth.impl.AccessorPo;
import net.duckling.ddl.service.oauth.impl.RequestToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

/**
 * @date 2011-8-30
 * @author xiejj
 */
public class AccessorDAOImplTest extends BaseTest {
    private AccessorDAOImpl accessorDao;

    @Before
    public void setUp() throws Exception {
        accessorDao = f.getBean(AccessorDAOImpl.class);
    }

    @After
    public void tearDown() throws Exception {
        accessorDao = null;
    }

    @Test
    public void testCreateAccessor() throws InterruptedException {
        AccessorPo accessor = new AccessorPo();
        accessor.setAuthorized(false);
        accessor.setUserId(null);
        accessor.setConsumerKey("myKey");
        accessor.setScreenName("hello");
        RequestToken requestToken = new RequestToken("1123", "2", new Date());
        accessor.setRequestToken(requestToken);

        accessorDao.createAccessor(accessor);
        AccessorPo loaded = accessorDao.getAccessor("1123");
        assertNotNull(loaded);
        assertNotNull(loaded.getRequestToken());
        assertEquals("1123", loaded.getRequestToken().getToken());

        AccessToken accessToken = new AccessToken("11234", new Date());
        accessor.setAccessToken(accessToken);
        accessor.setId(loaded.getId());
        accessorDao.updateAccessor(accessor);

        loaded = accessorDao.getAccessor("11234");
        assertNotNull(loaded);
        assertNotNull(loaded.getRequestToken());
        assertEquals("1123", loaded.getRequestToken().getToken());
        assertNotNull(loaded.getAccessToken());
        assertEquals("11234", loaded.getAccessToken().getToken());

        Thread.sleep(100);
        accessorDao.removetTimeOut(new Date());
        loaded = accessorDao.getAccessor("1123");
        assertNull(loaded);
    }

}
