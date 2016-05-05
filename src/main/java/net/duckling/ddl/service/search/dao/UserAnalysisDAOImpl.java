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

import net.duckling.ddl.service.search.WeightPair;
import net.duckling.ddl.service.search.impl.UserAnalysisDAO;
import net.duckling.ddl.service.search.impl.UserInterestRecord;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class UserAnalysisDAOImpl extends AbstractBaseDAO implements UserAnalysisDAO {
	
	private static final String QUERY_BROWSE_LOG =" select user_id,creator,score from (select user_id,creator,count(creator) as score  from (select user_id,item_id,item_type,tid from vwb_browse_log where TO_DAYS(now())-TO_DAYS(browse_time)<=150 )as a inner join a1_resource as b on a.item_id = b.rid and a.tid=b.tid where a.user_id <>b.creator group by a.user_id,b.creator) as tmp where creator not like concat(\"%\", user_id, \"%\")";
	//将vwb_browse_log和a1_resource通过item(item_id和tid)连接 然后统计每个用户浏览其他用户创建的文档数量 （不包括用户自身，且返回结果按照用户和被访问者排序）
	private static final String INSERT_USER_INTEREST_NEW ="insert into a1_userinterest_temp (uid, interest,score,time) values(?,?,?,now())";
	private static final String RENAME_USER_INTEREST_TOTEMP ="Alter TABLE a1_userinterest RENAME TO a1_userinterest_temp";
	private static final String CREAT_INTEREST_TABLE = "CREATE TABLE IF NOT EXISTS `a1_userinterest` (`id` int(11) NOT NULL auto_increment,`uid` varchar(255) NOT NULL,`interest` varchar(255) NOT NULL,`score` int(11) default '0',`time` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,  PRIMARY KEY  (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
	private static final String DELET_DUPLICATE = "delete from a1_userinterest where uid = interest";
	private static final String DROP_TEMP_TABLE = "DROP TABLE IF EXISTS `a1_userinterest_temp`";
	private static final String INSERT_USER_INTEREST="insert into a1_userinterest select NULL,uid,interest,SUM(score),now() from a1_userinterest_temp group by uid,interest";
	private static final String QUERY_USER_INTEREST = "select interest from a1_userinterest where uid = ? order by score desc";
	private static final String QUERY_USER_INTEREST_DOC = "select rid,weight from a1_search_docweight as a1 where keyword=? and uid in (select interest from (select interest from a1_userinterest user where user.uid=? order by score desc limit 10) as tmp)";
	
	private static final String CREAT_SIM_TABLE = "CREATE TABLE IF NOT EXISTS `a1_user_sim` (`id` int(11) NOT NULL auto_increment,`uid` varchar(255) NOT NULL,`simuid` varchar(255) NOT NULL,`score` int(11) default '0',`time` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,  PRIMARY KEY  (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
	private static final String INSERT_USER_SIM = "insert into a1_user_sim select NULL,uid,simuid,sum(score),NULL from(select a.user_id as uid ,b.user_id as simuid,if(a.count<b.count,a.count,b.count) as score,a.item_id, a.item_type from(select user_id , item_id,count(item_id) as count, item_type,tid from vwb_browse_log where TO_DAYS(now())-TO_DAYS(browse_time)<=150  group by user_id,tid,item_id) as a inner join (select user_id , item_id,count(item_id) as count, item_type,tid from vwb_browse_log where TO_DAYS(now())-TO_DAYS(browse_time)<=150 group by user_id,tid,item_id) as b on a.user_id <> b.user_id and a.item_id=b.item_id and a.tid=b.tid) as t group by uid,simuid" ;	
	private static final String QUERY_USER_SIM = "select simuid from a1_user_sim where uid = ? order by score desc";
	private static final String CLEAN_SIM = "truncate table a1_user_sim";
	private static final String QUERY_USER_SIM_DOC ="select rid,weight from a1_search_docweight a1 where keyword=? and uid in (select simuid from (select simuid from a1_user_sim user where user.uid=? order by score desc limit 10) as tmp)";
	private static RowMapper<UserInterestRecord> usrIntRcrdRowMapper = new RowMapper<UserInterestRecord>(){
		@Override
		public UserInterestRecord mapRow(ResultSet rs, int index) throws SQLException {
			UserInterestRecord userInterestRecord = new UserInterestRecord();
			userInterestRecord.setUid(rs.getString("user_id"));
			userInterestRecord.setInterst(rs.getString("creator"));
			userInterestRecord.setScore(rs.getInt("score"));
			return userInterestRecord;
		}		
	};
	
	@Override
	public List<UserInterestRecord> getBrowse() {
		return this.getJdbcTemplate().query(QUERY_BROWSE_LOG, usrIntRcrdRowMapper);
	}
	
	
	@Override
	public void creatUserInterest(final List<UserInterestRecord> recordList) {
		//简历临时表 主要是删除uid = interest 用户关注自己的情况
		getJdbcTemplate().update(CREAT_INTEREST_TABLE);//保证有USER_INTEREST可以转换
		getJdbcTemplate().update(DROP_TEMP_TABLE);//保证没有临时表
		getJdbcTemplate().update(RENAME_USER_INTEREST_TOTEMP);
		getJdbcTemplate().update(CREAT_INTEREST_TABLE);//建立新的USER_INTEREST可以转换
		BatchPreparedStatementSetter creatUserInterestSetter = new MyBatchStatementSetter(recordList);
		getJdbcTemplate().batchUpdate(INSERT_USER_INTEREST_NEW, creatUserInterestSetter);		
		getJdbcTemplate().update(DELET_DUPLICATE);		//删除重复项
		getJdbcTemplate().update(INSERT_USER_INTEREST);
		getJdbcTemplate().update(DROP_TEMP_TABLE);//删除临时表
	}
	
	private static class MyBatchStatementSetter implements BatchPreparedStatementSetter{
		private List<UserInterestRecord> recordList = null;
		MyBatchStatementSetter(final List<UserInterestRecord> recordList){
			this.recordList = recordList;
		}
		@Override
		public int getBatchSize() {
			return recordList.size();
		}

		@Override
		public void setValues(PreparedStatement ps, int i)
				throws SQLException {
			UserInterestRecord record = recordList.get(i);
			int j = 0;
			ps.setString(++j, record.getUid());
			ps.setString(++j, record.getInterst());
			ps.setInt(++j, record.getScore());
		}	
	}
	
	public List<String> getInterest(String uid){
		try{
			return this.getJdbcTemplate().queryForList(QUERY_USER_INTEREST, String.class,new Object[]{uid});
		}catch (EmptyResultDataAccessException e){
			return null;
		}
				
	}
	public List<WeightPair> getInterestDocWeight(String keyword,String uid){
		try{
			return this.getJdbcTemplate().query(QUERY_USER_INTEREST_DOC,new Object[]{keyword,uid},docWeightPairRowMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}	
	}
	
	private static RowMapper<WeightPair> docWeightPairRowMapper = new RowMapper<WeightPair>(){
		@Override
		public WeightPair mapRow(ResultSet rs, int index) throws SQLException {
			WeightPair weightPair = new WeightPair();
			weightPair.setId(rs.getLong("rid"));
			int weight = rs.getInt("weight");
			weightPair.setWeight(weight>20?20:weight);
			return weightPair;
		}		
	};
	public int creatUserSim(){
		getJdbcTemplate().update(CREAT_SIM_TABLE);
		getJdbcTemplate().update(CLEAN_SIM);
		return this.getJdbcTemplate().update(INSERT_USER_SIM);		
	}
	
	public List<String> getSim(String uid){
		try{
			return this.getJdbcTemplate().queryForList(QUERY_USER_SIM,String.class,new Object[]{uid});	
		}catch (EmptyResultDataAccessException e){
			return null;
		}
			
	}
	public List<WeightPair> getSimDocWeight(String keyword,String uid){
		try{
			return this.getJdbcTemplate().query(QUERY_USER_SIM_DOC,new Object[]{keyword,uid},docWeightPairRowMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
