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

package cn.vlabs.duckling.aone.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.duckling.ddl.service.subscribe.NotifyPolicy;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionDAO;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import cn.vlabs.duckling.BaseTest;

/**
 * @date 2011-3-2
 * @author Administrator
 */
public class SubscriptionDAOTest extends BaseTest{
    
    private SubscriptionDAO sdao;

    @Before
    public void setUp() throws Exception {
        sdao = f.getBean(SubscriptionDAO.class);
    }

    @After
    public void tearDown() throws Exception {
        sdao = null;
    }
    
    public void testSave() {
        Subscription subs = new Subscription();
        subs.setUserId("zyh@cnic.cn");
        Publisher pub = new Publisher();
        pub.setId(10);
        pub.setType(Publisher.PAGE_TYPE);
        subs.setPublisher(pub);
        NotifyPolicy np = new NotifyPolicy();
        np.setPolicy("message");
        subs.setNotifyPolicy(np);
        Subscription[] subArray = new Subscription[1];
        subArray[0]=subs;
        sdao.batchSave(subArray);
    }
    
    private int  save(){
    	Subscription subs = new Subscription();
        subs.setUserId("zyh@cnic.cn");
        Publisher pub = new Publisher();
        pub.setId(10);
        pub.setType(Publisher.PAGE_TYPE);
        subs.setPublisher(pub);
        NotifyPolicy np = new NotifyPolicy();
        np.setPolicy("message");
        subs.setNotifyPolicy(np);
        return sdao.save(subs);
    }
    
    public void testDelete() {
        Subscription subs = new Subscription();
        subs.setId(1);
        sdao.delete(subs);
    }
    
    public void testFind() {
    	List<Integer> ids = new ArrayList<Integer>();
    	for(int i =0;i<3;i++){
    		ids.add(save());
    	}
        Set<String> subscribers = sdao.findSubscribers(1,10, Publisher.PAGE_TYPE);
        Assert.assertTrue( subscribers.size()>=3);
        for(int i :ids){
        	Subscription persistentInstance = new Subscription();
        	persistentInstance.setId(i);
        	sdao.delete(persistentInstance);
        }
    }

}
