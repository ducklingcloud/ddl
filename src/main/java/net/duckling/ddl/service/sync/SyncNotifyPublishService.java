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
package net.duckling.ddl.service.sync;

import java.util.Date;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RTopic;

@Deprecated
public class SyncNotifyPublishService {

    private RedissonClient redisson;

    private static final String APP_PREFIX = "DDL";

    public SyncNotifyPublishService(String host, int port) {
        Config config = new Config();
        config.useSingleServer().setAddress(host + ":" + port);
        config.setThreads(10);
        redisson = Redisson.create(config);
    }

    private RTopic getRemoteTopic(int tid) {
        return redisson.getTopic(APP_PREFIX + ":" + tid);
    }

    public void publishJounal(int tid, Jounal jnl) {
        RTopic t = this.getRemoteTopic(tid);
        long talk = t.publish(jnl);
        System.out.println(talk);
    }

    public static void main(String[] args) {
        SyncNotifyPublishService mps = new SyncNotifyPublishService("127.0.0.1", 6379);
        Jounal jnl = new Jounal();
        jnl.setDevice("newdevice");
        jnl.setFid(1);
        jnl.setOccurTime(new Date());
        jnl.setFver(1);
        jnl.setJid(2);
        jnl.setOperation("mkdir");
        jnl.setTid(1);
        mps.publishJounal(1, jnl);
    }

}
