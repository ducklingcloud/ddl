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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.duckling.ddl.service.oauth.OAuthConsumerExt;
import net.duckling.ddl.service.oauth.dao.ConsumerDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

/**
 * @date 2011-8-29
 * @author xiejj@cnic.cn
 */
public class OAuthConsumerDAOTest extends BaseTest {
    private ConsumerDAOImpl consumerDAO;

    @Before
    public void setUp() throws Exception {
        consumerDAO = f.getBean(ConsumerDAOImpl.class);
    }

    @After
    public void tearDown() throws Exception {
        consumerDAO.clear();
        consumerDAO = null;
    }

    @Test
    public void testGetConsumer() {
        OAuthConsumerExt consumer = new OAuthConsumerExt("http://localhost/oauth/call-back", "myKey", "mySecret");
        consumer.setEnable(false);
        consumer.setUseXAuth(true);
        consumerDAO.createConsumer(consumer);
        OAuthConsumerExt another = consumerDAO.getConsumer("myKey");
        assertNotNull(another);
        assertFalse(another.isEnabled());
        assertTrue(another.isUseXAuth());
    }

    @Test
    public void testUpdateConsumer() {
        OAuthConsumerExt consumer = new OAuthConsumerExt("http://localhost/oauth/call-back", "myKey", "mySecret");
        consumer.setEnable(false);
        consumer.setUseXAuth(true);
        consumerDAO.createConsumer(consumer);
        consumer.setEnable(true);
        consumerDAO.updateConsumer(consumer);
        OAuthConsumerExt another = consumerDAO.getConsumer("myKey");
        assertNotNull(another);
        assertTrue(another.isEnabled());
        assertTrue(another.isUseXAuth());
    }

}
