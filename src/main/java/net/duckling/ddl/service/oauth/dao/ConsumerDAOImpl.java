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

package net.duckling.ddl.service.oauth.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.oauth.OAuthConsumerExt;
import net.duckling.ddl.service.oauth.impl.ConsumerDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.Utility;

import org.springframework.jdbc.core.RowMapper;


/**
 * consumer的数据库管理类。
 *
 * @date 2011-8-29
 * @author xiejj
 */
public class ConsumerDAOImpl extends AbstractBaseDAO implements
                                                     ConsumerDAO {
    private static final String QUERY_CONSUMER = "select * from vwb_oauth_consumer where `key`=?";
    private static final String DELETE_CONSUMERS = "delete from vwb_oauth_consumer";
    private static final String CREATE_CONSUMER = "insert into vwb_oauth_consumer(`key`, secret, callback_url, `enable`, xauth, name, description) values(?,?,?,?,?,?,?)";
    private static final String UPDATE_CONSUMER = "update vwb_oauth_consumer set secret=?, callback_url=?, `enable`=?, xauth=? , name=?, description=? where `key`=?";

    public OAuthConsumerExt getConsumer(String consumerKey) {
        List<OAuthConsumerExt> consumers = getJdbcTemplate().query(
            QUERY_CONSUMER, new Object[] { consumerKey },
            new RowMapper<OAuthConsumerExt>() {
                public OAuthConsumerExt mapRow(ResultSet rs, int index)
                        throws SQLException {
                    String consumerKey = rs.getString("key");
                    String consumerSecret = rs.getString("secret");
                    String callbackURL = rs.getString("callback_url");
                    OAuthConsumerExt consumer = new OAuthConsumerExt(
                        callbackURL, consumerKey, consumerSecret);
                    consumer.setEnable(
                        Utility.int2bool(rs.getInt("enable")));
                    consumer.setUseXAuth(
                        Utility.int2bool(rs.getInt("xauth")));
                    consumer.setProperty("description",
                                         rs.getString("description"));
                    consumer.setProperty("name", rs.getString("name"));
                    return consumer;
                }
            });
        if (consumers != null && consumers.size() > 0) {
            return consumers.get(0);
        }
        return null;
    }

    public void createConsumer(OAuthConsumerExt consumer) {
        getJdbcTemplate().update(
            CREATE_CONSUMER,
            new Object[] { consumer.consumerKey, consumer.consumerSecret,
                consumer.callbackURL, consumer.isEnabled(),
                consumer.isUseXAuth(), consumer.getProperty("name"),
                consumer.getProperty("description") });
    }

    public void updateConsumer(OAuthConsumerExt consumer) {
        getJdbcTemplate().update(
            UPDATE_CONSUMER,
            new Object[] { consumer.consumerSecret, consumer.callbackURL,
                Utility.bool2int(consumer.isEnabled()),
                Utility.bool2int(consumer.isUseXAuth()),
                consumer.getProperty("name"),
                consumer.getProperty("description"),
                consumer.consumerKey });
    }

    public void clear() {
        getJdbcTemplate().update(DELETE_CONSUMERS);
    }
}
