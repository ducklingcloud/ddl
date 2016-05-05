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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.impl.BundleItemDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class BundleItemDAOImpl extends AbstractBaseDAO implements
		BundleItemDAO {
	
	private static final Logger LOG = Logger.getLogger(BundleItemDAOImpl.class);
	
	private static final String SQL_CREATE = "insert into a1_bundle_item(tid,bid,rid,sequence) values(?,?,?,?)";
	private static final String SQL_DELETE = "delete from a1_bundle_item";
	private static final String SQL_UPDATE = "update a1_bundle_item set tid=?,bid=?,rid=?,sequence=?";
	private static final String SQL_QUERY= "select * from a1_bundle_item";
	private static final String BY_ID = " where id=?";
	private static final String BY_TIDBID = " where tid=? and bid=?";
	private static final String BY_TIDBIDRID = " where tid=? and bid=? and rid=?";
	private static final String ORDER = " order by sequence asc";

	private RowMapper<BundleItem> rowMapper = new RowMapper<BundleItem>(){

		@Override
		public BundleItem mapRow(ResultSet rs, int index) throws SQLException {
			BundleItem bundleItem = new BundleItem();
			bundleItem.setId(rs.getInt("id"));
			bundleItem.setTid(rs.getInt("tid"));
			bundleItem.setBid(rs.getInt("bid"));
			bundleItem.setRid(rs.getInt("rid"));
			bundleItem.setSequence(rs.getInt("sequence"));
			return bundleItem;
		}
		
	};
	
	@Override
	public int create(final BundleItem bundleItem) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, bundleItem.getTid());
				ps.setInt(++i, bundleItem.getBid());
				ps.setInt(++i, bundleItem.getRid());
				ps.setInt(++i, bundleItem.getSequence());
				return ps;
			}
			
		},keyHolder);
		Number key = keyHolder.getKey();
		return (key==null)?-1:key.intValue();
	}

	@Override
	public int delete(int id) {
		return this.getJdbcTemplate().update(SQL_DELETE+BY_ID, new Object[]{id});
	}

	@Override
	public int update(int id, BundleItem bundleItem) {
		return this.getJdbcTemplate().update(SQL_UPDATE+BY_ID, 
				new Object[]{bundleItem.getTid(),bundleItem.getBid(),bundleItem.getRid(),
				bundleItem.getSequence(),id});
	}

	@Override
	public BundleItem getBundleItemById(int id){
		List<BundleItem> list = this.getJdbcTemplate().query(SQL_QUERY+BY_ID, 
				new Object[]{id}, rowMapper);
		if(null==list || list.size()<=0)
		{
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for BundleItem " +
					"by id = "+id);
		}
		return list.get(0);
	}
	
	@Override
	public boolean isInBundle(int bid, int tid, int rid){
		String sql = "select * from a1_bundle_item where bid=? and tid=? and rid=?";
		List<BundleItem> list = this.getJdbcTemplate().query(sql, new Object[]{bid, tid, rid}, rowMapper);
		return !(null==list || list.size()<=0);
	}
	
	@Override
	public List<BundleItem> getBundleItemsByBidTid(int bid, int tid){
		return this.getJdbcTemplate().query(SQL_QUERY+BY_TIDBID+ORDER,new Object[]{tid,bid},rowMapper);
	}
	@Override
	public List<BundleItem> getBundleItemsByBidTid(int bid, int tid, int offset, int size) {
		return  this.getJdbcTemplate().query(SQL_QUERY+BY_TIDBID+ORDER+ getLimitString(offset,size),new Object[]{tid,bid},rowMapper);
	}
	private String getLimitString(int offset,int size){
		String sql=" limit ";
		if(offset<0){
			sql+="0,";
		}else{
			sql+=offset+", ";
		}
		if(size<0){
			sql+=Integer.MAX_VALUE+"";
		}else{
			sql+=size+"";
		}
		return sql;
	}

	@Override
	public int deleteAllItemInBundle(int tid, int bid) {
		return this.getJdbcTemplate().update(SQL_DELETE+BY_TIDBID, new Object[]{tid,bid});
	}

	@Override
	public int addItemsToBundle(final int bid, final int tid, final BundleItem[] items) {
		this.getJdbcTemplate().batchUpdate(SQL_CREATE, new BatchPreparedStatementSetter(){

			@Override
			public int getBatchSize() {
				return items.length;
			}

			@Override
			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				int i = 0;
				ps.setInt(++i, tid);
				ps.setInt(++i, bid);
				ps.setInt(++i, items[index].getRid());
				ps.setInt(++i, items[index].getSequence());
			}
			
		});
		return 1;
	}

	public void addBundleItemSequence(int tid,int bid,int add){
		String sql ="update a1_bundle_item set sequence=sequence+? where tid=? and bid=?";
		getJdbcTemplate().update(sql, new Object[]{add,tid,bid});
	}
	@Override
	public int deleteItemsWithIds(int[] ids) {
		int len = ids.length;
		if(len>0){
			StringBuilder sb = new StringBuilder();
			sb.append(" where id in(");
			for(int i=0; i<len; i++){
				sb.append(ids[i]+",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(")");
			this.getJdbcTemplate().update(SQL_DELETE+sb.toString());
			return 1;
		}
		return 0;
	}

	@Override
	public int deleteItemsWithoutIds(final int bid, final int tid, final int[] rids) {
		this.getJdbcTemplate().batchUpdate(SQL_DELETE+BY_TIDBIDRID , new BatchPreparedStatementSetter(){

			@Override
			public int getBatchSize() {
				return (null!=rids && rids.length>0)?rids.length:0;
			}

			@Override
			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				int i = 0;
				ps.setInt(++i, tid);
				ps.setInt(++i, bid);
				ps.setInt(++i, rids[index]);
			}
			
		});
		return 1;
	}

	@Override
	public int reorderBundleItems(final int bid, final int tid, final Map<Integer, Integer> orderMap) {
		if(null == orderMap || orderMap.isEmpty()){
			return -1;
		}
		final int[][] ids = convertMap2IntArray(orderMap);
		String sql = "update a1_bundle_item set sequence=? where rid=? and tid=? and bid=?";
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter(){

			@Override
			public int getBatchSize() {
				return orderMap.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int index)
					throws SQLException {
				int i = 0;
				ps.setInt(++i, ids[index][1]);
				ps.setInt(++i, ids[index][0]);
				ps.setInt(++i, tid);
				ps.setInt(++i, bid);
			}
			
		});
		return 1;
	}

	@Override
	public List<BundleItem> getBundleItemsByRid(int rid) {
		String sql = "select a.* from a1_bundle_item a, a1_resource b where a.tid=b.tid and a.bid=b.item_id and b.rid="+rid;
		List<BundleItem> result = this.getJdbcTemplate().query(sql, rowMapper);
		return (result!=null && result.size()>0)?result:new ArrayList<BundleItem>();
	}

	private int[][] convertMap2IntArray(Map<Integer, Integer> map){
		if(null == map || map.isEmpty()){
			return null;
		}
		int[][] array = new int[map.size()][2];
		Iterator<Map.Entry<Integer, Integer>> itr = map.entrySet().iterator();
		int i = 0;
		while(itr.hasNext()){
			Map.Entry<Integer, Integer> entry = itr.next();
			array[i][0]=entry.getKey();
			array[i][1]=entry.getValue();
			i++;
		}
		return array;
	}

	@Override
	public List<BundleItem> getAllBundleItem() {
		String sql = "select * from a1_bundle_item";
		return this.getJdbcTemplate().query(sql, rowMapper);
	}

	@Override
	public int getMaxSequenceInBundle(int bid, int tid) {
		String sql = "select max(sequence) from a1_bundle_item where bid=? and tid=?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[]{bid, tid}, Integer.class);
	}
}
