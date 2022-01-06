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

import net.duckling.ddl.service.user.UserGuide;
import net.duckling.ddl.service.user.impl.UserGuideDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;


public class UserGuideDAOImpl extends AbstractBaseDAO implements UserGuideDAO{

    private static final Logger LOG = Logger.getLogger(UserGuideDAOImpl.class);

    @Override
    public int create(final UserGuide userGuide) {
        final String sql = "insert into a1_user_guide(uid,module,step) values(?,?,?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setString(++i, userGuide.getUid());
                    ps.setString(++i, userGuide.getModule());
                    ps.setInt(++i, userGuide.getStep());
                    return ps;
                }

            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key==null)?-1:key.intValue();
    }

    @Override
    public void updateStep(String uid, String module, int step) {
        String sql = "update a1_user_guide set step=? where uid=? and module=?";
        this.getJdbcTemplate().update(sql, new Object[]{step, uid, module});
    }

    @Override
    public int getStep(String uid, String module) {
        String sql = "select * from a1_user_guide where uid=? and module=?";
        List<UserGuide> list = this.getJdbcTemplate().query(sql, new Object[]{uid, module}, new RowMapper<UserGuide>(){

                @Override
                public UserGuide mapRow(ResultSet rs, int index)
                        throws SQLException {
                    UserGuide ug = new UserGuide();
                    ug.setId(rs.getInt("id"));
                    ug.setUid(rs.getString("uid"));
                    ug.setModule(rs.getString("module"));
                    ug.setStep(rs.getInt("step"));
                    return ug;
                }

            });
        if(null == list || list.size()<=0){
            return -1;
        }
        else if(list.size()>1){
            LOG.error("there exists more than one object while query for " +
                      "UserGuide by uid="+uid+" and module="+module);
        }
        return list.get(0).getStep();
    }

}
