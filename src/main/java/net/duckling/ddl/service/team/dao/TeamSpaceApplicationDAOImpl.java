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
package net.duckling.ddl.service.team.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.team.TeamSpaceApplication;
import net.duckling.ddl.service.team.impl.TeamSpaceApplicationDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TeamSpaceApplicationDAOImpl extends AbstractBaseDAO implements TeamSpaceApplicationDAO {
    private RowMapper<TeamSpaceApplication> rowMapper = new RowMapper<TeamSpaceApplication>(){

            @Override
            public TeamSpaceApplication mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeamSpaceApplication bean = new TeamSpaceApplication();
                bean.setId(rs.getInt("id"));
                bean.setTid(rs.getInt("tid"));
                bean.setUid(rs.getString("uid"));
                bean.setApplicationTime(rs.getTimestamp("application_time"));
                bean.setApplicationType(rs.getString("application_type"));
                bean.setApproveTime(rs.getTimestamp("approve_time"));
                bean.setNewSize(rs.getLong("new_size"));
                bean.setOriginalSize(rs.getLong("original_size"));
                return bean;
            }

        } ;
    private static final String INSERT_SQL = "insert into a1_team_space_application (tid,uid,application_time,application_type,approve_time,new_size,original_size)values(?,?,?,?,?,?,?)";
    private static final String QUERY_BY_TID = "select * from a1_team_space_application where tid=? order by id desc";
    @Override
    public int create(final TeamSpaceApplication bean) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement stat = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                    int i=0;
                    stat.setInt(++i, bean.getTid());
                    stat.setString(++i, bean.getUid());
                    stat.setTimestamp(++i, new Timestamp(bean.getApplicationTime().getTime()));
                    stat.setString(++i, bean.getApplicationType());
                    stat.setTimestamp(++i, new Timestamp(bean.getApproveTime().getTime()));
                    stat.setLong(++i, bean.getNewSize());
                    stat.setLong(++i, bean.getOriginalSize());
                    return stat;
                }
            }, keyHolder);
        bean.setId(keyHolder.getKey().intValue());
        return  bean.getId();
    }

    @Override
    public List<TeamSpaceApplication> queryByTid(int tid) {
        return getJdbcTemplate().query(QUERY_BY_TID,new Object[]{tid}, rowMapper);
    }

    @Override
    public void delete(int id) {
        String sql = "delete from a1_team_space_application where id=?";
        getJdbcTemplate().update(sql,id);
    }

}
