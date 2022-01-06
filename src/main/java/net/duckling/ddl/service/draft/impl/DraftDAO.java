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

package net.duckling.ddl.service.draft.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.draft.Draft;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-7-15
 * @author Clive Lee
 */
@Repository
public class DraftDAO extends AbstractBaseDAO {

    private static final String INSERT = "insert into vwb_dpage_draft(tid,rid,uid,type,title,content,modify_time) values(?,?,?,?,?,?,?)";
    private static final String UPDATE = "update vwb_dpage_draft set content=?,title=?,modify_time=? where id=? ";
    private static final String QUERY = "select * from vwb_dpage_draft where tid=? and rid=? and uid=? and type=?";
    private static final String QUERY_LATEST="select * from vwb_dpage_draft where tid=? and rid=? and uid=? order by modify_time desc limit 1";
    private static final String DELETE = "delete from vwb_dpage_draft where tid=? and rid=? and uid=? and type=?";
    private static final String CHECK_EXIST = "select count(id) from vwb_dpage_draft where tid=? and rid=? and uid=? and type=?";

    private RowMapper<Draft> rowMapper = new RowMapper<Draft>() {
            public Draft mapRow(ResultSet rs, int rowNum) throws SQLException {
                Draft tpv = new Draft();
                tpv.setId(rs.getInt("id"));
                tpv.setTid(rs.getInt("tid"));
                tpv.setRid(rs.getInt("rid"));
                tpv.setUid(rs.getString("uid"));
                tpv.setType(rs.getString("type"));
                tpv.setTitle(rs.getString("title"));
                tpv.setContent(rs.getString("content"));
                tpv.setModifyTime(rs.getTimestamp("modify_time"));
                return tpv;
            }
        };

    public int insert(final Draft tpv) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setInt(++i, tpv.getTid());
                    pst.setInt(++i, tpv.getRid());
                    pst.setString(++i, tpv.getUid());
                    pst.setString(++i, tpv.getType());
                    pst.setString(++i, tpv.getTitle());
                    pst.setString(++i, tpv.getContent());
                    pst.setTimestamp(++i, new Timestamp(tpv.getModifyTime().getTime()));
                    return pst;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public int update(Draft tpv) {
        getJdbcTemplate().update(UPDATE,new Object[] {tpv.getContent(),tpv.getTitle(),tpv.getModifyTime(),tpv.getId()});
        return 0;
    }
    public Draft getLatest(int tid, int rid, String uid){
        List<Draft> results = getJdbcTemplate().query(QUERY_LATEST, new Object[] {tid,rid,uid},rowMapper);
        return (results!=null&&results.size()!=0)?results.get(0):null;
    }
    public Draft getInstance(int tid,int rid,String uid,String type) {
        List<Draft> results = getJdbcTemplate().query(QUERY, new Object[] {tid,rid,uid,type},rowMapper);
        return (results!=null&&results.size()!=0)?results.get(0):null;
    }

    public boolean isExistDraft(int tid, int rid, String uid, String type) {
        int count = getJdbcTemplate().queryForObject(CHECK_EXIST,new Object[] {tid,rid,uid,type}, Integer.class);
        return count == 1;
    }

    public void delete(int tid, int rid, String uid, String type) {
        this.getJdbcTemplate().update(DELETE, new Object[] {tid,rid,uid,type});
    }
}
