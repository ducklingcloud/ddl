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
package net.duckling.ddl.service.resource.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.Starmark;
import net.duckling.ddl.service.resource.impl.StarmarkDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQueryKeywordUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class StarmarkDAOImpl extends AbstractBaseDAO implements StarmarkDAO {

	private static final Logger LOG = Logger.getLogger(StarmarkDAOImpl.class);
	
	private static final String SQL_CREATE = "insert into a1_starmark(rid,tid,uid,create_time) values(?,?,?,?)";
	private static final String SQL_DELETE_ALL = "delete from a1_starmark where tid=? and uid=?";
	private static final String SQL_DELETE = "delete from a1_starmark where rid=? and uid=?";
	private static final String SQL_QUERY = "select * from a1_starmark where rid=? and uid=?";
	
	private  static final String ORDER_SORT_TIME="time";
	private  static final String ORDER_SORT_TIME_DESC="timeDesc";
	private  static final String ORDER_SORT_TITLE="title";
	private  static final String ORDER_SORT_TITLE_DESC="titleDesc";
	
	@Override
	public int create(final Starmark starmark) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, starmark.getRid());
				ps.setInt(++i, starmark.getTid());
				ps.setString(++i, starmark.getUid());
				ps.setTimestamp(++i, new Timestamp(starmark.getCreateTime().getTime()));
				return ps;
			}
			
		},keyHolder);
		Number key = keyHolder.getKey();
		return (key==null)?-1:key.intValue();
	}
	
	@Override
	public int batchCreate(final String uid, final int tid, final List<Long> rids){
		this.getJdbcTemplate().batchUpdate(SQL_CREATE, new BatchPreparedStatementSetter(){

			@Override
			public int getBatchSize() {
				return (null==rids || rids.isEmpty())?0:rids.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				int i=0;
				long rid = rids.get(index);
				ps.setInt(++i, (int)rid);
				ps.setInt(++i, tid);
				ps.setString(++i, uid);
				ps.setTimestamp(++i, new Timestamp((new Date()).getTime()));
			}
			
		});
		return 1;
	}

	@Override
	public int deleteAllStarmark(final int tid, final String uid) {
		return this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_DELETE_ALL);
				int i = 0;
				ps.setInt(++i, tid);
				ps.setString(++i, uid);
				return ps;
			}
			
		});
	}
	
	@Override
	public int delete(int rid,String uid) {
		return this.getJdbcTemplate().update(SQL_DELETE, new Object[]{rid,uid});
	}
	
	@Override
	public int batchDelete(String uid, int tid, List<Long> rids){
		if(null == rids || rids.isEmpty()){
			return 0;
		}
		String sql = "delete from a1_starmark where uid=? and tid=? and rid in(";
		StringBuilder sb = new StringBuilder();
		for(long rid : rids){
			sb.append(rid+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		sql += sb.toString();
		return this.getJdbcTemplate().update(sql, new Object[]{uid, tid});
	}

	@Override
	public Starmark getStarmarkById(int id){
		List<Starmark> list = this.getJdbcTemplate().query(SQL_QUERY, new Object[]{id}, starmarkRowMapper);
		if(null==list || list.size()<=0){
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for Starmark " +
					"by id = "+id);
		}
		return list.get(0);
	}
	
	private RowMapper<Starmark> starmarkRowMapper  = new RowMapper<Starmark>(){

		@Override
		public Starmark mapRow(ResultSet rs, int index) throws SQLException {
			Starmark s = new Starmark();
			s.setId(rs.getInt("id"));
			s.setTid(rs.getInt("tid"));
			s.setRid(rs.getInt("rid"));
			s.setUid(rs.getString("uid"));
			s.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			return s;
		}
	};

	@Override
	public List<Starmark> getAllStarmark() {
		String sql = "select * from a1_starmark";
		return this.getJdbcTemplate().query(sql, starmarkRowMapper);
	}

	@Override
	public List<Starmark> getStarmarkOfRids(String uid, int tid, List<Long> rids) {
		List<Starmark> results = new ArrayList<Starmark>();
		if(null != rids && !rids.isEmpty()){
			String sql = "select * from a1_starmark where uid=? and tid=? and rid in (";
			StringBuilder sb = new StringBuilder();
			for(long rid : rids){
				sb.append(rid+",");
			}
			sb.replace(sb.lastIndexOf(","), sb.length(), ")");
			sql += sb.toString();
			results = this.getJdbcTemplate().query(sql, new Object[]{uid, tid}, starmarkRowMapper);
		}
		return results;
	}

	@Override
	public int batchDeleteResourceStar(int tid, int rid, Set<String> uids) {
		if(uids==null||uids.isEmpty()){
			return 0;
		}
		String sql ="delete from a1_starmark where tid=? and rid=? and uid in (";
		StringBuilder sb = new StringBuilder();
		for(String uid : uids){
			sb.append("'"+uid+"',");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		sql += sb.toString();
		return getJdbcTemplate().update(sql, tid,rid);
	}

	
	@Override
	public PaginationBean<Resource> getMyStartFiles(int tid, String uid,
			int offset, int size, String order, String keyWord) {
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("uid", uid);
		paramMap.put("tid", tid);
		
		String countSql="select count(*) from a1_resource r,a1_starmark s where r.rid=s.rid and r.tid=s.tid and s.uid=:uid and s.tid=:tid";
		String sql="select r.* from a1_resource r,a1_starmark s where r.rid=s.rid and r.tid=s.tid and uid=:uid  and s.tid=:tid";
		
		if(!StringUtils.isBlank(keyWord)){
			String s = ResourceQueryKeywordUtil.getKeyWordString(keyWord, paramMap,"");
			countSql+=s;
			sql+=s;
		}
		
		sql+=ResourceOrderUtils.buildOrderSql("",order);
		sql+=ResourceOrderUtils.buildDivPageSql(offset,size);
		
		
		
		int total =this.getNamedParameterJdbcTemplate().queryForObject(countSql, paramMap,Integer.class);
		PaginationBean<Resource> result =new PaginationBean<Resource>();
		result.setData(getNamedParameterJdbcTemplate().query(sql, paramMap, resourceRowMapper));
		result.setBegin(offset);
		result.setSize(size);
		result.setTotal(total);
		return result ;
	}
	
	private RowMapper<Resource> resourceRowMapper = new ResourceRowMapper("");
}
