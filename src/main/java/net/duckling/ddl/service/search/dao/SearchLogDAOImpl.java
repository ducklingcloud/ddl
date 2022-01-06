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
package net.duckling.ddl.service.search.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.search.SearchReocrd;
import net.duckling.ddl.service.search.impl.SearchLogDAO;
import net.duckling.ddl.service.search.impl.SqlSearchRecord;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


/**
 * @author lizexin
 *
 */
@Repository
public class SearchLogDAOImpl extends AbstractBaseDAO implements SearchLogDAO {

    private static final String SQL_CREATE = "insert into a1_searchlog(uid,keyword,item_type,item_id,tid,sequence,opername,time)" +
            " values(?,?,?,?,?,?,?,?)";
    private static final String DELETE_SEARCH_ALL = "delete from a1_searchlog" ;
    private static final String SQL_CREATESEARCHED= "insert into a1_searchedlog(uid,keyword,item_type,item_id,tid,sequence,opername,time)" +
            " values(?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY = "select * from a1_searchlog order by uid, keyword,time" ;

    @Override
    public void creatSearchLog(final List<SearchReocrd> recordList) {
        getJdbcTemplate().update(DELETE_SEARCH_ALL);
        BatchPreparedStatementSetter creatSearchLogSetter = new MyBatchStatementSetter(recordList);
        getJdbcTemplate().batchUpdate(SQL_CREATE, creatSearchLogSetter);
    }

    private static class MyBatchStatementSetter implements BatchPreparedStatementSetter{
        private List<SearchReocrd> recordList = null;
        MyBatchStatementSetter(final List<SearchReocrd> recordList){
            this.recordList = recordList;
        }
        @Override
        public int getBatchSize() {
            return recordList.size();
        }

        @Override
        public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            SearchReocrd record = recordList.get(i);
            int j = 0;
            ps.setString(++j, record.getUid());
            ps.setString(++j, record.getKeyword());
            ps.setString(++j, record.getType());
            ps.setInt(++j, Integer.valueOf(record.getPid()).intValue());
            ps.setInt(++j, Integer.valueOf(record.getTid()).intValue());
            ps.setInt(++j, record.getSeq());
            ps.setString(++j, record.getOpername());
            ps.setString(++j, record.getTime());
        }
    }


    @Override
    public void creatSearchedLog(final List<SqlSearchRecord> recordList) {
        BatchPreparedStatementSetter batchReocordListSetter = new MyBatchPreparedStatementSetter(recordList);
        getJdbcTemplate().batchUpdate(SQL_CREATESEARCHED, batchReocordListSetter);
    }



    private static class MyBatchPreparedStatementSetter implements BatchPreparedStatementSetter{
        private List<SqlSearchRecord> recordList = null;
        MyBatchPreparedStatementSetter(final List<SqlSearchRecord> recordList){
            this.recordList = recordList;
        }
        @Override
        public int getBatchSize() {
            return recordList.size();
        }

        @Override
        public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            SqlSearchRecord record = recordList.get(i);
            int j = 0;
            ps.setString(++j, record.getUid());
            ps.setString(++j, record.getKeyword());
            ps.setString(++j, record.getType());
            ps.setInt(++j, record.getItemId());
            ps.setInt(++j, record.getTid());
            ps.setInt(++j, record.getSeq());
            ps.setString(++j, record.getOper());
            ps.setString(++j, record.getTime());
        }
    }



    private static RowMapper<SqlSearchRecord> srchedRcrdRowMapper = new RowMapper<SqlSearchRecord>(){

            @Override
            public SqlSearchRecord mapRow(ResultSet rs, int index) throws SQLException {
                SqlSearchRecord sqlSearchRecord = new SqlSearchRecord();
                sqlSearchRecord.setId(rs.getInt("id"));
                sqlSearchRecord.setUid(rs.getString("uid"));
                sqlSearchRecord.setKeyword(rs.getString("keyword"));
                sqlSearchRecord.setItemId(rs.getInt("item_id"));
                sqlSearchRecord.setType(rs.getString("item_type"));
                sqlSearchRecord.setTid(rs.getInt("tid"));
                sqlSearchRecord.setSeq(rs.getInt("sequence"));
                sqlSearchRecord.setOper(rs.getString("opername"));
                sqlSearchRecord.setTime(rs.getString("time"));
                return sqlSearchRecord;
            }
        };

    @Override
    public List<SqlSearchRecord> getLog() {
        return getJdbcTemplate().query(SQL_QUERY, srchedRcrdRowMapper);
    }
}
