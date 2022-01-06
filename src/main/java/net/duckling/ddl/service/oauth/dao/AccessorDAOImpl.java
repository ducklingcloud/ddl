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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.oauth.impl.AccessToken;
import net.duckling.ddl.service.oauth.impl.AccessorDAO;
import net.duckling.ddl.service.oauth.impl.AccessorPo;
import net.duckling.ddl.service.oauth.impl.RequestToken;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.Utility;

import org.springframework.jdbc.core.RowMapper;


/**
 * @date 2011-8-30
 * @author xiejj@cnic.cn
 */
public class AccessorDAOImpl extends AbstractBaseDAO implements AccessorDAO {
    private static final String QUERY_ACCESSOR="select * from vwb_oauth_accessor where request_token=? or access_token=?";
    public AccessorPo getAccessor(String token) {
        List<AccessorPo> accessors = getJdbcTemplate().query(QUERY_ACCESSOR, new Object[]{token, token}, new RowMapper<AccessorPo>(){
                public AccessorPo mapRow(ResultSet rs, int index)
                        throws SQLException {
                    AccessToken accessToken =
                            new AccessToken(rs.getString("access_token"),
                                            rs.getTimestamp("access_create_time"));
                    RequestToken requestToken=
                            new RequestToken(rs.getString("request_token"),
                                             rs.getString("token_secret"),
                                             rs.getTimestamp("request_create_time"));
                    AccessorPo accessor = new AccessorPo();
                    accessor.setAccessToken(accessToken);
                    accessor.setRequestToken(requestToken);
                    accessor.setConsumerKey(rs.getString("consumer_key"));
                    accessor.setAuthorized(Utility.int2bool(rs.getInt("authorized")));
                    accessor.setUserId(rs.getString("user_id"));
                    accessor.setId(rs.getInt("id"));
                    accessor.setScreenName(rs.getString("screen_name"));
                    return accessor;
                }

            });
        if (accessors!=null&& accessors.size()>0){
            return accessors.get(0);
        }else{
            return null;
        }
    }
    private static final String CREATE_ACCESSOR="insert into vwb_oauth_accessor" +
            "(request_token,token_secret, request_create_time, access_token," +
            " access_create_time,consumer_key, user_id,authorized,screen_name)" +
            " values(?,?,?,?,?,?,?,?,?)";
    public void createAccessor(AccessorPo accessor) {
        RequestToken requestToken = accessor.getRequestToken();
        AccessToken accessToken = accessor.getAccessToken();

        getJdbcTemplate().update(CREATE_ACCESSOR, new Object[]{
                requestToken.getToken(),
                requestToken.getTokenSecret(),
                Utility.date2Timestamp(requestToken.getCreateTime()),
                accessToken.getToken(),
                Utility.date2Timestamp(accessToken.getCreateTime()),
                accessor.getConsumerKey(),
                accessor.getUserId(),
                Utility.bool2int(accessor.isAuthorized()),
                accessor.getScreenName()
            });
    }
    private static final String UPDATE_ACCESSOR="update vwb_oauth_accessor set" +
            " request_token=? ,token_secret=?, request_create_time=?, access_token=?," +
            " access_create_time=?,consumer_key=?, user_id=?,authorized=?, screen_name=?" +
            " where id=?";
    public void updateAccessor(AccessorPo accessor){
        RequestToken requestToken = accessor.getRequestToken();
        AccessToken accessToken = accessor.getAccessToken();

        getJdbcTemplate().update(UPDATE_ACCESSOR, new Object[]{
                requestToken.getToken(),
                requestToken.getTokenSecret(),
                Utility.date2Timestamp(requestToken.getCreateTime()),
                accessToken.getToken(),
                Utility.date2Timestamp(accessToken.getCreateTime()),
                accessor.getConsumerKey(),
                accessor.getUserId(),
                Utility.bool2int(accessor.isAuthorized()),
                accessor.getScreenName(),
                accessor.getId()
            });
    }
    private static final String REMOVE_TIME_OUT_TOKEN="delete from vwb_oauth_accessor" +
            " where (request_create_time<=? and access_create_time is null)" +
            " or access_create_time<=?";
    public void removetTimeOut(Date date) {
        Timestamp deadline = Utility.date2Timestamp(date);
        getJdbcTemplate().update(REMOVE_TIME_OUT_TOKEN, new Object[]{deadline, deadline});
    }
}
