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

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.dao.ResourceRowMapper;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceDao;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ShareResourceDaoImpl extends AbstractBaseDAO implements ShareResourceDao {
	private static final String INSERT = "insert into share_resource(rid,tid,share_uid,create_time,last_editor,last_edit_time,password,download_count) values(?,?,?,?,?,?,?,?)";
	private static final String UPDATE = "update share_resource set last_editor=?,last_edit_time=?,password=?,download_count=? where rid=?";
	private static final String DELETE = "delete from share_resource where rid=?";
	private static final String QUERY_BY_RID = "select * from share_resource where rid=?";
	private static final String QUERY_BY_TID = "select * from share_resource where tid=? order by create_time desc";
	private static final String QUERY_TEAM_RESOURCE = "select r.* from a1_resource r,share_resource s where r.rid=s.rid and s.tid=? order by s.create_time desc";
	
	private RowMapper<ShareResource> rowMapper = new RowMapper<ShareResource>(){

		@Override
		public ShareResource mapRow(ResultSet rs, int rowNum) throws SQLException {
			ShareResource r = new ShareResource();
			r.setTid(rs.getInt("tid"));
			r.setRid(rs.getInt("rid"));
			r.setCreateTime(rs.getTimestamp("create_time"));
			r.setLastEditor(rs.getString("last_editor"));
			r.setLastEditTime(rs.getTimestamp("last_edit_time"));
			r.setPassword(rs.getString("password"));
			r.setShareUid(rs.getString("share_uid"));
			r.setDownloadCount(rs.getInt("download_count"));
			return r;
		}
		
	};
	private RowMapper<Resource> resourceMapper = new ResourceRowMapper("r.");
	
	@Override
	public int add(final ShareResource sr) {
		getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT);
				int i = 0;
				ps.setInt(++i, sr.getRid());
				ps.setInt(++i, sr.getTid());
				ps.setString(++i, sr.getShareUid());
				ps.setTimestamp(++i, new Timestamp(sr.getCreateTime().getTime()));
				ps.setString(++i, sr.getLastEditor());
				ps.setTimestamp(++i, new Timestamp(sr.getLastEditTime().getTime()));
				ps.setString(++i, sr.getPassword());
				ps.setInt(++i, sr.getDownloadCount());
				return ps;
			}
			
		});
		return 0;
	}

	@Override
	public int update(ShareResource sr) {
		return getJdbcTemplate().update(UPDATE, sr.getLastEditor(),sr.getLastEditTime(),sr.getPassword(),sr.getDownloadCount(),sr.getRid());
	}

	@Override
	public boolean delete(int id) {
		return getJdbcTemplate().update(DELETE, id)>0;
	}

	@Override
	public ShareResource get(int id) {
		List<ShareResource> ls = getJdbcTemplate().query(QUERY_BY_RID, new Object[]{id}, rowMapper);
		if(ls==null||ls.isEmpty()){
			return null;
		}else{
			return ls.get(0);
		}
	}

	@Override
	public List<ShareResource> queryByTid(int tid) {
		return getJdbcTemplate().query(QUERY_BY_TID, new Object[]{tid}, rowMapper);
	}

	@Override
	public List<Resource> queryTeamShareResource(int tid) {
		return getJdbcTemplate().query(QUERY_TEAM_RESOURCE, new Object[]{tid}, resourceMapper);
	}
	
}
