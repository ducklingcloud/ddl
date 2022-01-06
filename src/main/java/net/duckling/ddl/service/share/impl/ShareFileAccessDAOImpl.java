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

package net.duckling.ddl.service.share.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.share.ShareFileAccess;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-10-10
 * @author clive
 */
@Repository
public class ShareFileAccessDAOImpl extends AbstractBaseDAO {
    private static final String INSERT_SQL = "insert into vwb_share_access (uid,tid,fid,password,create_time,valid_of_days,clb_id,fetch_file_code) values(?,?,?,?,?,?,?,?)";
    private static final String QUERY_SQL = "select * from vwb_share_access where id=?";

    public int insertRecord(final ShareFileAccess instance) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT_SQL,PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, instance.getUid());
                    pst.setInt(++i, instance.getTid());
                    pst.setInt(++i, instance.getFid());
                    pst.setString(++i, instance.getPassword());
                    pst.setTimestamp(++i, new Timestamp(instance.getCreateTime().getTime()));
                    pst.setInt(++i, instance.getValidOfDays());
                    pst.setInt(++i, instance.getClbId());
                    pst.setString(++i, instance.getFetchFileCode());
                    return pst;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key != null) ? key.intValue() : -1;
    }

    private RowMapper<ShareFileAccess> rowMapper = new RowMapper<ShareFileAccess>() {
            public ShareFileAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
                ShareFileAccess instance = new ShareFileAccess();
                instance.setId(rs.getInt("id"));
                instance.setTid(rs.getInt("tid"));
                instance.setUid(rs.getString("uid"));
                instance.setFid(rs.getInt("fid"));
                instance.setPassword(rs.getString("password"));
                instance.setValidOfDays(rs.getInt("valid_of_days"));
                instance.setCreateTime(rs.getTimestamp("create_time"));
                instance.setClbId(rs.getInt("clb_id"));
                instance.setFetchFileCode(rs.getString("fetch_file_code"));
                return instance;
            }
        };

    //  public boolean isValidURL(ShareFileAccess s){
    //      return false;
    //  }

    public ShareFileAccess find(ShareFileAccess s){
        String sql = "select * from vwb_share_access where tid=? and clb_id=? and fid=? and password=?";
        return getFirstObject(getJdbcTemplate().query(sql,new Object[]{s.getTid(),s.getClbId(),s.getFid(),s.getPassword()},rowMapper));
    }

    public ShareFileAccess getInstanceById(int id){
        return getFirstObject(getJdbcTemplate().query(QUERY_SQL,new Object[]{id},rowMapper));
    }

    private ShareFileAccess getFirstObject(List<ShareFileAccess> results){
        return (results!=null&&results.size()!=0)?results.get(0):null;
    }
}
