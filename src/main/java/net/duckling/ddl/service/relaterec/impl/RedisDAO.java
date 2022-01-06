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
package net.duckling.ddl.service.relaterec.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Repository
public class RedisDAO extends AbstractBaseDAO {

    private static final Logger LOGGER = Logger.getLogger(RedisDAO.class);

    private boolean isConnect;

    private JedisPool pool;

    private UserVSM getUserVSM(String type, String userID) {
        String value = getValue(type + userID);

        if (value == null) {
            return null;
        }

        UserVSM user = new UserVSM();
        user.setUserID(userID);

        if (value.equals("")) {
            user.addToVector(new FeatureWeight[0]);
            return user;
        }

        String[] weights = value.split(";");

        Map<Integer, Double> vector = new HashMap<Integer, Double>();

        for (int i = 0; i < weights.length; ++i) {
            int idx = weights[i].indexOf(" ");
            vector.put(Integer.parseInt(weights[i].substring(0, idx)),
                       Double.parseDouble(weights[i].substring(idx + 1)));
        }

        user.setVector(vector);
        return user;
    }

    private String getValue(String key) {
        Jedis jedis = pool.getResource();
        String value = jedis.get(key);
        pool.returnResource(jedis);

        return value;
    }

    public void addPageToUpdate(int tid, int pid) {
        Jedis jedis = pool.getResource();

        jedis.sadd("RelatedRecs:" + tid + ":pages:ToUpdate", pid + "");

        pool.returnResource(jedis);
    }

    public Vector<PageSimilar> getPageSimilar(int tid, int pid) {
        Vector<PageSimilar> vector = null;
        String key = "RelatedRecs:" + tid + ":pages:similar:" + pid;
        String value = getValue(key);

        if (value != null && !value.equals("")) {
            vector = new Vector<PageSimilar>();
            String[] similars = value.split(";");
            for (String sim : similars) {
                int idx = sim.indexOf(" ");
                // if(idx<0){
                // continue;
                // }
                vector.add(new PageSimilar(Integer.parseInt(sim.substring(0,
                                                                          idx)), Double.parseDouble(sim.substring(idx + 1))));
            }
        }

        return vector;
    }

    public FeatureWeight[] getPageVSM(int tid, int pid) {
        String key = "RelatedRecs:" + tid + ":pages:vsm:" + pid;
        String value = getValue(key);

        if (value == null) {
            return null;
        }

        if (value.equals("")) {
            return new FeatureWeight[0];
        }

        String[] weights = value.split(";");

        FeatureWeight[] pageVSM = new FeatureWeight[weights.length];

        for (int i = 0; i < weights.length; ++i) {
            int idx = weights[i].indexOf(" ");

            pageVSM[i] = new FeatureWeight(Integer.parseInt(weights[i]
                                                            .substring(0, idx)), Double.parseDouble(weights[i]
                                                                                                    .substring(idx + 1)));
        }

        return pageVSM;
    }

    public UserVSM getUserLongVSM(int tid, String userID) {
        String type = "RelatedRecs:" + tid + ":users:longvsm:";

        return getUserVSM(type, userID);
    }

    public boolean isConnect() {
        if (isConnect) {
            try {
                Jedis jedis = pool.getResource();
                pool.returnResource(jedis);
            } catch (Exception e) {
                LOGGER.error("", e);
                isConnect = false;
            }
        }

        return isConnect;
    }

    @Value("${duckling.redis.server}")
    public void setRedisServer(String url) {
        this.pool = new JedisPool(new JedisPoolConfig(), url);
        this.isConnect = true;

    }

}
