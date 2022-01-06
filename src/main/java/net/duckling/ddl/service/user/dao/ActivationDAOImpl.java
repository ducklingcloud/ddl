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

import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.impl.ActivationDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;


/**
 * @date 2011-6-21
 * @author Clive Lee
 */
public class ActivationDAOImpl extends AbstractBaseDAO implements ActivationDAO {

    private static final String INSERT_SQL = "insert into vwb_activation (email,password,encode,name,status,tname) values(?,?,?,?,?,?)";

    @Override
    public int insertRecord(final Activation instance) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT_SQL,PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, instance.getEmail());
                    pst.setString(++i, instance.getPassword());
                    pst.setString(++i, instance.getEncode());
                    pst.setString(++i, instance.getName());
                    pst.setString(++i, instance.getStatus());
                    pst.setString(++i, instance.getTname());
                    return pst;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key != null) ? key.intValue() : -1;
    }

    private static final String QUERY_BY_ID_ENCODE = "select * from vwb_activation where id=? and encode=?";
    /* (non-Javadoc)
     * @see cn.vlabs.duckling.aone.infrastructure.repository.ActivationDAO#getActivationByIdAndEncode(int, java.lang.String)
     */
    @Override
    public Activation getActivationByIdAndEncode(int id,String encode) {
        return getJdbcTemplate().queryForObject(QUERY_BY_ID_ENCODE, new Object[] {id,encode},rowMapper);
    }

    private RowMapper<Activation> rowMapper = new RowMapper<Activation>() {
            public Activation mapRow(ResultSet rs, int rowNum) throws SQLException {
                Activation instance = new Activation();
                instance.setEmail(rs.getString("email"));
                instance.setId(rs.getInt("id"));
                instance.setName(rs.getString("name"));
                instance.setPassword(rs.getString("password"));
                instance.setEncode(rs.getString("encode"));
                instance.setStatus(rs.getString("status"));
                instance.setTname(rs.getString("tname"));
                return instance;
            }
        };

    @Override
    public void updateStatus(Activation instance) {
        String sql = "update vwb_activation set status=?,password=? where id=?";
        this.getJdbcTemplate().update(sql, new Object[] {instance.getStatus(),"",instance.getId()});
    }

}
