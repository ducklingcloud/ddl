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
package net.duckling.ddl.service.bundle.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.bundle.impl.BundleDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class BundleDAOImpl extends AbstractBaseDAO implements BundleDAO {
	
	private static final Logger LOG = Logger.getLogger(BundleDAOImpl.class);
	
	private static final String SQL_CREATE = "insert into a1_bundle(bid,status,tid,title,create_time,creator,last_editor,last_edit_time,last_version,description)" +
			" values(?,?,?,?,?,?,?,?,?,?)";
	private static final String SQL_DELETE = "update a1_bundle set status ='"+LynxConstants.STATUS_DELETE+"' ";
	private static final String SQL_UPDATE = "update a1_bundle set status=?, " +
			"title=?, create_time=?, creator=?, last_editor=?, last_edit_time=?, last_version=?, description=?";
	private static final String SQL_QUERY_BUNDLE = "select * from a1_bundle";
	private static final String BY_BIDTID = " where bid=? and tid=?";
	private static final String BY_TID = " where tid=?";
	
	private static final String SQL_MAXBID = "select max(bid) from a1_bundle where tid=?";
	
	private RowMapper<Bundle> bundleRowMapper = new RowMapper<Bundle>(){

		@Override
		public Bundle mapRow(ResultSet rs, int index) throws SQLException {
			Bundle bundle = new Bundle();
			bundle.setId(rs.getInt("id"));
			bundle.setBid(rs.getInt("bid"));
			bundle.setStatus(rs.getString("status"));
			bundle.setTid(rs.getInt("tid"));
			bundle.setTitle(rs.getString("title"));
			bundle.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			bundle.setCreator(rs.getString("creator"));
			bundle.setLastEditor(rs.getString("last_editor"));
			bundle.setLastEditTime(new Date(rs.getTimestamp("last_edit_time").getTime()));
			bundle.setLastVersion(rs.getInt("last_version"));
			bundle.setDescription(rs.getString("description"));
			return bundle;
		}
		
	};
	
	@Override
	public synchronized int create(final Bundle bundle) {
		if(bundle.getBid()<=0){
			int newBid = getMaxBid(bundle.getTid())+1;
			bundle.setBid(newBid);
		}
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, bundle.getBid());
				ps.setString(++i, bundle.getStatus());
				ps.setInt(++i, bundle.getTid());
				ps.setString(++i, bundle.getTitle());
				ps.setTimestamp(++i, new Timestamp(bundle.getCreateTime().getTime()));
				ps.setString(++i, bundle.getCreator());
				ps.setString(++i, bundle.getLastEditor());
				ps.setTimestamp(++i, new Timestamp(bundle.getLastEditTime().getTime()));
				ps.setInt(++i, bundle.getLastVersion());
				ps.setString(++i, bundle.getDescription());
				return ps;
			}
			
		},keyHolder);
		return bundle.getBid();
	}

	@Override
	public int delete(int bid, int tid) {
		return this.getJdbcTemplate().update(SQL_DELETE+BY_BIDTID, 
				new Object[]{bid, tid});
	}
	
	@Override
	public int batchDelete(int tid, List<Integer> bids){
		if(null == bids || bids.isEmpty()){
			return 0;
		}
		String sql = SQL_DELETE + " where tid="+tid+" and bid in(";
		StringBuilder sb = new StringBuilder();
		for(int bid : bids){
			sb.append(bid+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		sql += sb.toString();
		return this.getJdbcTemplate().update(sql);
	}

	@Override
	public int update(int bid, int tid, Bundle bundle) {
		return this.getJdbcTemplate().update(SQL_UPDATE+BY_BIDTID, 
				new Object[]{bundle.getStatus() ,bundle.getTitle(),
				new Timestamp(bundle.getCreateTime().getTime()), bundle.getCreator(),
				bundle.getLastEditor(), new Timestamp(bundle.getLastEditTime().getTime()),
				bundle.getLastVersion(), bundle.getDescription(), bid, tid});
	}

	@Override
	public Bundle getBundle(int bid, int tid) {
		List<Bundle> list = this.getJdbcTemplate().query(SQL_QUERY_BUNDLE+BY_BIDTID, 
				new Object[]{bid, tid}, bundleRowMapper);
		if(null==list || list.size()<=0)
		{
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for Bundle " +
					"by bid = "+bid+" and tid = "+tid);
		}
		return list.get(0);
	}

	@Override
	public List<Bundle> getBundlesOfTeam(int tid, int offset, int size) {
		if(offset<0 || size<0 || (size==0 && offset>0)){
			LOG.error("offset and size should be zero or positive, however in this query," +
					"offset = "+offset+" and size = "+size);
			return null;
		}
		String limit = "";
		if(offset>=0 && size>0){
			limit = " limit "+offset+","+size;
		}
		return this.getJdbcTemplate().query(SQL_QUERY_BUNDLE+BY_TID+limit, 
				new Object[]{tid}, bundleRowMapper);
	}

	private int getMaxBid(int tid){
		return this.getJdbcTemplate().queryForObject(SQL_MAXBID,new Object[]{tid}, Integer.class);
	}

	@Override
	public int getBundleCountByTitle(int tid, String title, boolean status) {
		String bundleStatus = status?LynxConstants.STATUS_AVAILABLE:LynxConstants.STATUS_REMOVED;
		String sql = "select count(*) from a1_bundle where tid=? and title=? and status=?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[]{tid, title, bundleStatus}, Integer.class);
	}

	@Override
	public List<Integer> getRidsIfItemsInBundle(int tid, int[] rids) {
		List<Integer> result = new ArrayList<Integer>();
		if(null !=rids && rids.length>0){
			StringBuilder sql = new StringBuilder("select rid from a1_bundle_item where tid=? and rid in(");
			for(int rid : rids){
				sql.append(rid+",");
			}
			sql.replace(sql.lastIndexOf(","), sql.length(), ")");
			result = this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{tid}, Integer.class);
		}
		return result;
	}
}
