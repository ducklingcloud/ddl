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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.search.DocWeightRecord;
import net.duckling.ddl.service.search.WeightPair;
import net.duckling.ddl.service.search.impl.SearchDocWeightDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


/**
 * @author lizexin
 *
 */
@Repository
public class SearchDocWeightDAOImpl extends AbstractBaseDAO implements SearchDocWeightDAO {

    private static final String QUERY_ALL_WEIGHTS =" select rid, uid,keyword,weight,time from a1_search_docweight where keyword =? group by uid ,keyword,rid";
    private static final String QUERY_OWN_WEIGHTS = " select rid, weight,time from a1_search_docweight where keyword =? and uid=? group by uid ,keyword,rid";
    private static final String INSERT_NEWLOG_RESOUCE = "insert into a1_search_docweight select NULL, uid, keyword, rid, weight, time from (select uid, keyword, item_type, item_id,tid, sum(sequence) as weight, time from a1_searchedlog where item_type <>'Dpage' and item_type <>'rid' group by uid, keyword, item_id) as a inner join a1_resource as b on a.item_type = b.item_type and a.item_id = b.rid and a.tid=b.tid";
    private static final String INSERT_NEWLOG_PAGE = "insert into a1_search_docweight select NULL, uid, keyword, id, weight, time from (select uid, keyword, item_type, item_id, sum(sequence) as weight, time from a1_searchedlog where item_type =\"Dpage\" group by uid, keyword, item_id) as a inner join a1_page as b on a.item_id = b.pid";
    private static final String INSERT_NEWLOG_RID = "insert into a1_search_docweight select NULL,uid,keyword ,item_id,sum(sequence),time from a1_searchedlog where item_type ='rid' group by uid, keyword, item_id";
    private static final String DELETE_ALL = "truncate table a1_search_docweight";
    //页面被浏览次数_作为权重因子
    private static final String QUERY_VIEWDOC = "select b.id as rid,weight from ((select count(user_id) as weight,item_id,tid from vwb_browse_log where TO_DAYS(now())-TO_DAYS(browse_time)<=14 group by item_id,tid) as a inner join (select pid,tid,id from a1_page where id in(";
    private static final String GROUP_BY = ")) as b on a.item_id = b.pid and a.tid = b.tid)";
    @Override
    public List<DocWeightRecord> getDocWeight(String keyword){
        try{
            return this.getJdbcTemplate().query(QUERY_ALL_WEIGHTS,new Object[]{keyword},docWeightRcrdRowMapper);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    @Override
    public List<WeightPair> getOwnDocWeight(String keyword,String uid){
        try{
            return this.getJdbcTemplate().query(QUERY_OWN_WEIGHTS,new Object[]{keyword,uid},viewWeightPairRowMapper);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    private static RowMapper<DocWeightRecord> docWeightRcrdRowMapper = new RowMapper<DocWeightRecord>(){
            @Override
            public DocWeightRecord mapRow(ResultSet rs, int index) throws SQLException {
                DocWeightRecord docWeightRecord = new DocWeightRecord();
                docWeightRecord.setUid(rs.getString("uid"));
                docWeightRecord.setKeyword(rs.getString("keyword"));
                docWeightRecord.setId(rs.getInt("rid"));
                int weight = Integer.valueOf(rs.getString("weight"));
                docWeightRecord.setWeight(weight>20?20:weight);
                docWeightRecord.setTime(rs.getString("time"));
                return docWeightRecord;
            }
        };

    @Override
    public int updateDocWeight(){
        int count =0;
        this.getJdbcTemplate().update(DELETE_ALL);
        count += this.getJdbcTemplate().update(INSERT_NEWLOG_RESOUCE);
        count += this.getJdbcTemplate().update(INSERT_NEWLOG_PAGE);
        count += this.getJdbcTemplate().update(INSERT_NEWLOG_RID);
        return count;
    }
    /*
      private static RowMapper<DocWeightRecord> docWghtRcrdRowMapper = new RowMapper<DocWeightRecord>(){
      @Override
      public DocWeightRecord mapRow(ResultSet rs, int index) throws SQLException {
      DocWeightRecord docWeightRecord = new DocWeightRecord();
      docWeightRecord.setUid(rs.getString("uid"));
      docWeightRecord.setKeyword(rs.getString("keyword"));
      docWeightRecord.setId(rs.getInt("rid"));
      docWeightRecord.setSequence(rs.getInt("sequence"));
      docWeightRecord.setOpername(rs.getString("opername"));
      docWeightRecord.setTime(rs.getString("time"));
      return docWeightRecord;
      }
      };
    */
    /*
      private static class MyBatchStatementSetter implements BatchPreparedStatementSetter{
      private List<DocWeightRecord> recordList = null;
      MyBatchStatementSetter(final List<DocWeightRecord> recordList){
      this.recordList = recordList;
      }
      @Override
      public int getBatchSize() {
      return recordList.size();
      }

      @Override
      public void setValues(PreparedStatement ps, int i)
      throws SQLException {
      DocWeightRecord record = recordList.get(i);
      int j = 0;
      ps.setString(++j, record.getUid());
      ps.setString(++j, record.getKeyword());
      ps.setInt(++j, record.getId());
      ps.setInt(++j, record.getWeight());
      ps.setString(++j, record.getTime());
      }
      }
    */
    /*
      private void computWeight(List<DocWeightRecord> docWeightRecords){
      Iterator<DocWeightRecord> it = docWeightRecords.iterator();
      while(it.hasNext()){
      int factor = 1;
      DocWeightRecord docWeightRecord = new DocWeightRecord();
      docWeightRecord = it.next();
      if(docWeightRecord.getOpername()!=null){
      if(docWeightRecord.getOpername().equals("toolEdit")){
      factor = 1;
      }
      if(docWeightRecord.getOpername().equals("recommend-box")){
      factor = 2;
      }
      if(docWeightRecord.getOpername().equals("replyButton")){
      factor = 1;
      }
      }
      docWeightRecord.setWeight(factor*docWeightRecord.getSequence());
      }
      }
    */
    @Override
    public List<WeightPair> getViewDocWeight(List<Long> rids){
        String ID = "";
        for(int i=0;i<rids.size();i++){
            ID = ID + rids.get(i).toString() + ",";
        }
        ID = ID.substring(0,ID.length()-1);

        String FINALQUERY = QUERY_VIEWDOC + ID + GROUP_BY;
        try{
            return this.getJdbcTemplate().query(FINALQUERY,viewWeightPairRowMapper);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    private static RowMapper<WeightPair> viewWeightPairRowMapper = new RowMapper<WeightPair>(){
            @Override
            public WeightPair mapRow(ResultSet rs, int index) throws SQLException {
                WeightPair weightPair = new WeightPair();
                weightPair.setId(rs.getLong("rid"));
                int weight = rs.getInt("weight");
                weightPair.setWeight(weight>20?20:weight);
                return weightPair;
            }
        };

}
