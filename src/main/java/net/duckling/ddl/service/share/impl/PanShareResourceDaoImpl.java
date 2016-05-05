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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceDao;
import net.duckling.ddl.util.AbstractBaseDAO;

@Repository
public class PanShareResourceDaoImpl extends AbstractBaseDAO implements PanShareResourceDao {
	private static final String INSERT = "insert into pan_share_resource (share_uid,share_path,pan_share_id,password,share_time,expire_millis,download_count,status) values(?,?,?,?,?,?,?,?)";
	private static final String UPDATE = "update pan_share_resource set password=?,share_time=?,download_count=?,status=? where id=?";
	private static final String DELETE_BY_ID = "delete from pan_share_resource where id=?";
	private static final String QUERY_BY_ID = "select * from pan_share_resource where id =?";
	private static final String QUERY_BY_UID = "select * from pan_share_resource where share_uid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' order by id desc";
	private static final String QUERY_BY_PATH = "select * from pan_share_resource where share_uid=? and share_path=? and status='"+LynxConstants.STATUS_AVAILABLE+"' order by id desc";
	private static final String QUERY_ALL_BY_UID = "select * from pan_share_resource where share_uid=?";
	
	private RowMapper<PanShareResource> rowMapper = new RowMapper<PanShareResource>(){
		@Override
		public PanShareResource mapRow(ResultSet rs, int rowNum) throws SQLException {
			PanShareResource pr = new PanShareResource();
			pr.setId(rs.getInt("id"));
			pr.setDownloadCount(rs.getInt("download_count"));
			pr.setExpireMillis(rs.getLong("expire_millis"));
			pr.setPanShareId(rs.getString("pan_share_id"));
			pr.setPassword(rs.getString("password"));
			pr.setSharePath(rs.getString("share_path"));
			pr.setShareTime(rs.getTimestamp("share_time"));
			pr.setShareUid(rs.getString("share_uid"));
			pr.setStatus(rs.getString("status"));
			return pr;
		}
	};
	@Override
	public int add(final PanShareResource p) {
		GeneratedKeyHolder key = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setString(++i, p.getShareUid());
				ps.setString(++i, p.getSharePath());
				ps.setString(++i, p.getPanShareId());
				ps.setString(++i, p.getPassword());
				ps.setTimestamp(++i,new Timestamp(p.getShareTime().getTime()));
				ps.setLong(++i, p.getExpireMillis());
				ps.setInt(++i, p.getDownloadCount());
				ps.setString(++i, p.getStatus());
				return ps;
			}
			
		}, key);
		int k = key.getKey().intValue();
		p.setId(k);
		return k;
	}

	@Override
	public void update(PanShareResource p) {
		getJdbcTemplate().update(UPDATE, p.getPassword(),p.getShareTime(),p.getDownloadCount(),p.getStatus(),p.getId());
	}

	@Override
	public void delete(int id) {
		getJdbcTemplate().update(DELETE_BY_ID, id);
	}

	@Override
	public PanShareResource get(int id) {
		List<PanShareResource> ls = getJdbcTemplate().query(QUERY_BY_ID, new Object[]{id}, rowMapper);
		if(ls==null||ls.isEmpty()){
			return null;
		}
		return ls.get(0);
	}

	@Override
	public List<PanShareResource> getByUid(String uid) {
		return getJdbcTemplate().query(QUERY_BY_UID, new Object[]{uid}, rowMapper);
	}

	@Override
	public List<PanShareResource> getAllByUid(String uid) {
		return getJdbcTemplate().query(QUERY_ALL_BY_UID, new Object[]{uid}, rowMapper);
	}
	@Override
	public List<PanShareResource> getByPath(String uid, String path) {
		return getJdbcTemplate().query(QUERY_BY_PATH, new Object[]{uid, path}, rowMapper);
	}
}
