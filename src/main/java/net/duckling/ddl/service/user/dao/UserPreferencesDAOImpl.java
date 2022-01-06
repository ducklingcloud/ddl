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
package net.duckling.ddl.service.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.user.UserPreferences;
import net.duckling.ddl.service.user.impl.UserPreferencesDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class UserPreferencesDAOImpl extends AbstractBaseDAO implements UserPreferencesDAO {

    private static final Logger LOG = Logger.getLogger(UserPreferencesDAOImpl.class);

    private static final String SQL_CREATE = "insert into a1_user_preferences(uid,refresh_team_mode,default_team,access_home_mode)" +
            " values(?,?,?,?)";
    private static final String SQL_QUERY = "select * from a1_user_preferences";
    private static final String SQL_UPDATE = "update a1_user_preferences set refresh_team_mode=?, default_team=?, access_home_mode=?";
    private static final String BY_UID = " where uid=?";

    @Override
    public int create(final UserPreferences userPre) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setString(++i, userPre.getUid());
                    ps.setString(++i, userPre.getRefreshTeamMode());
                    ps.setInt(++i, userPre.getDefaultTeam());
                    ps.setString(++i, userPre.getAccessHomeMode());
                    return ps;
                }

            }, keyHolder);
        Number key = keyHolder.getKey();
        return (null!=key)?key.intValue():-1;
    }

    @Override
    public UserPreferences getUserPreferences(String uid) {
        List<UserPreferences> list = this.getJdbcTemplate().query(SQL_QUERY+BY_UID, new Object[]{uid}, new RowMapper<UserPreferences>(){

                @Override
                public UserPreferences mapRow(ResultSet rs, int index)
                        throws SQLException {
                    UserPreferences userPre = new UserPreferences();
                    userPre.setId(rs.getInt("id"));
                    userPre.setUid(rs.getString("uid"));
                    userPre.setRefreshTeamMode(rs.getString("refresh_team_mode"));
                    userPre.setDefaultTeam(rs.getInt("default_team"));
                    userPre.setAccessHomeMode(rs.getString("access_home_mode"));
                    return userPre;
                }

            });
        if(null == list || list.size() <= 0){
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exists more than one object while query UserPreferences" +
                      " by uid="+uid);
        }
        return list.get(0);
    }

    @Override
    public int update(UserPreferences userPre) {
        return this.getJdbcTemplate().update(SQL_UPDATE+BY_UID, new Object[]{
                userPre.getRefreshTeamMode(), userPre.getDefaultTeam(),
                userPre.getAccessHomeMode(), userPre.getUid()});
    }

    @Override
    public int updateRefreshTeamMode(String uid, String mode){
        String sql = "update a1_user_preferences set refresh_team_mode=? where uid=?";
        return this.getJdbcTemplate().update(sql, new Object[]{mode, uid});
    }
}
