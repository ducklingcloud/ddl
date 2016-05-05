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
package net.duckling.ddl.service.file.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.impl.FileVersionDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class FileVersionDAOImpl extends AbstractBaseDAO implements FileVersionDAO {

	private static final Logger LOG = Logger.getLogger(FileVersionDAOImpl.class);
	
	private static final String SQL_CREATE = "insert into a1_file_version(tid,version,clb_id,size,title,editor,edit_time,status,clb_version,rid,device,checksum) " +
			" values(?,?,?,?,?,?,?,?,?,?,?,?)";
	//modify by lvly@2012-07-20
	private static final String SQL_DELETE = "update a1_file_version set status='"+LynxConstants.STATUS_DELETE+"' ";
	private static final String SQL_QUERY = "select * from a1_file_version";
	private static final String SQL_UPDATE = "update a1_file_version set rid=?, tid=?, version=?," +
			"clb_id=?, size=?, title=?, editor=?, edit_time=?,clb_version=?,checksum=?";
	private static final String BY_ID = " where id=?";
	private static final String BY_RIDTID = " where rid=? and tid=?";
	private static final String BY_RIDTIDVER = " where rid=? and tid=? and version=?";
	private static final String SQL_QUERY_LATEST = "select * from a1_file_version where " +
			"rid=? and tid=? order by version desc limit 1";
	private static final String SQL_QUERY_FIRST = "select * from a1_file_version where " +
			"rid=? and tid=? order by version ASC limit 1";
	private static final String RECOVER_FILE_VERSION = "update a1_file_version set status='"+LynxConstants.STATUS_AVAILABLE+"' where tid=? and rid=?";
	
	private RowMapper<FileVersion> fileVersionRowMapper = new RowMapper<FileVersion>(){

		@Override
		public FileVersion mapRow(ResultSet rs, int index) throws SQLException {
			FileVersion fileVersion = new FileVersion();
			fileVersion.setId(rs.getInt("id"));
			fileVersion.setRid(rs.getInt("rid"));
			fileVersion.setTid(rs.getInt("tid"));
			fileVersion.setVersion(rs.getInt("version"));
			fileVersion.setClbId(rs.getInt("clb_id"));
			fileVersion.setClbVersion(rs.getInt("clb_version"));
			fileVersion.setSize(rs.getLong("size"));
			fileVersion.setTitle(rs.getString("title"));
			fileVersion.setEditor(rs.getString("editor"));
			fileVersion.setEditTime(new Date(rs.getTimestamp("edit_time").getTime()));
			fileVersion.setStatus(rs.getString("status"));
			fileVersion.setDevice(rs.getString("device"));
			fileVersion.setChecksum(rs.getString("checksum"));
			return fileVersion;
		}
		
	};
	
	@Override
	public int create(final FileVersion fileVersion) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, fileVersion.getTid());
				ps.setInt(++i, fileVersion.getVersion());
				ps.setInt(++i, fileVersion.getClbId());
				ps.setLong(++i, fileVersion.getSize());
				ps.setString(++i, fileVersion.getTitle());
				ps.setString(++i, fileVersion.getEditor());
				ps.setTimestamp(++i, new Timestamp(fileVersion.getEditTime().getTime()));
				ps.setString(++i, fileVersion.getStatus());
				ps.setInt(++i, fileVersion.getClbVersion());
				ps.setInt(++i, fileVersion.getRid());
				ps.setString(++i, fileVersion.getDevice());
				ps.setString(++i, fileVersion.getChecksum());
				return ps;
			}
			
		}, keyHolder);
		Number key = keyHolder.getKey();
		return (key==null)?-1:key.intValue();
	}

	@Override
	public int delete(int id) {
		return this.getJdbcTemplate().update(SQL_DELETE+BY_ID, new Object[]{id});
	}
	
	@Override
	public int delete(int rid, int tid, int version){
		return this.getJdbcTemplate().update(SQL_DELETE+BY_RIDTIDVER, new Object[]{rid, tid, version});
	}
	
	@Override
	public int deleteAllFileVersion(int rid, int tid){
		return this.getJdbcTemplate().update(SQL_DELETE+BY_RIDTID, new Object[]{rid, tid});
	}

	@Override
	public int update(int id, FileVersion fileVersion) {
		
		return this.getJdbcTemplate().update(SQL_UPDATE+BY_ID, new Object[]{
			fileVersion.getRid(), fileVersion.getTid(), fileVersion.getVersion(), fileVersion.getClbId(), fileVersion.getSize(),
			fileVersion.getTitle(), fileVersion.getEditor(),fileVersion.getEditTime(),fileVersion.getClbVersion(),fileVersion.getChecksum(),id
		});
	}

	@Override
	public FileVersion getFileVersionById(int id) {
		List<FileVersion> list = this.getJdbcTemplate().query(SQL_QUERY+BY_ID, 
				new Object[]{id}, fileVersionRowMapper);
		if(null==list || list.size()<=0){
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for FileVersion " +
					"by id = "+id);
		}
		return list.get(0);
	}
	
	@Override
	public FileVersion getFileVersion(int rid, int tid, int version){
		List<FileVersion> list = this.getJdbcTemplate().query(SQL_QUERY+BY_RIDTIDVER, 
				new Object[]{rid, tid, version}, fileVersionRowMapper);
		if(null==list || list.size()<=0){
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for FileVersion " +
					"by rid = "+rid+" and tid = "+tid+" and version = "+version);
		}
		return list.get(0);
	}
	
	@Override
	public FileVersion getLatestFileVersion(int rid, int tid){
		List<FileVersion> list = this.getJdbcTemplate().query(SQL_QUERY_LATEST, 
				new Object[]{rid, tid}, fileVersionRowMapper);
		if(null==list || list.size()<=0)
		{
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for FileVersion " +
					"by rid = "+rid+" and tid = "+tid);
		}
		return list.get(0);
	}
	
	@Override
	public FileVersion getFirstFileVersion(int rid, int tid){
		List<FileVersion> list = this.getJdbcTemplate().query(SQL_QUERY_FIRST, 
				new Object[]{rid, tid}, fileVersionRowMapper);
		if(null==list || list.size()<=0)
		{
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for FileVersion " +
					"by rid = "+rid+" and tid = "+tid);
		}
		return list.get(0);
	}

	@Override
	public List<FileVersion> getFileVersions(int rid, int tid) {
		return getJdbcTemplate().query(SQL_QUERY+BY_RIDTID + " and version>0", new Object[]{rid,tid}, fileVersionRowMapper);
	}
	
	@Override
	public List<FileVersion> getFileVersions(int rid, int tid,int offset,int pageSize) {
		String sql=SQL_QUERY+BY_RIDTID+" and version>0 order by version desc limit ?,?";
		return getJdbcTemplate().query(sql, new Object[]{rid,tid,offset,pageSize}, fileVersionRowMapper);
	}

	@Override
	public List<FileVersion> getFileSizeByRids(List<Long> ids){
		if(null == ids || ids.size()<=0){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select a.* from a1_file_version a, a1_resource b " +
				"where a.rid=b.rid and a.tid=b.tid and a.version=b.last_version " +
				"and b.item_type='DFile' and b.rid in (");
		for(long rid : ids){
			sb.append(rid+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		return this.getJdbcTemplate().query(sb.toString(), new RowMapper<FileVersion>(){

			@Override
			public FileVersion mapRow(ResultSet rs, int index)
					throws SQLException {
				FileVersion fileVersion = new FileVersion();
				fileVersion.setId(rs.getInt("id"));
				fileVersion.setRid(rs.getInt("rid"));
				fileVersion.setTid(rs.getInt("tid"));
				fileVersion.setVersion(rs.getInt("version"));
				fileVersion.setClbId(rs.getInt("clb_id"));
				fileVersion.setSize(rs.getLong("size"));
				fileVersion.setTitle(rs.getString("title"));
				fileVersion.setEditor(rs.getString("editor"));
				fileVersion.setEditTime(new Date(rs.getTimestamp("edit_time").getTime()));
				fileVersion.setStatus(rs.getString("status"));
				fileVersion.setDevice(rs.getString("device"));
				fileVersion.setChecksum(rs.getString("checksum"));
				return fileVersion;
			}
			
		});
	}
	
	private static final String PAGE_FILES = "SELECT b.* FROM vwb_dfile_ref a, a1_file_version b where a.page_rid=? and a.tid=? and a.file_rid=b.rid and a.tid=b.tid  " +
			"and b.version=(select max(c.version) from a1_file_version c where c.rid=a.file_rid and c.tid=a.tid and c.tid=a.tid) ";
	@Override
	public List<FileVersion> getDFilesOfPage(int pageRid,int tid) {
		return getJdbcTemplate().query(PAGE_FILES, new Object[] { pageRid,tid}, fileVersionRowMapper);
	}
	

	@Override
	public List<FileVersion> getLatestFileVersions(int[] rids, int tid) {
		String sql = "select a.* from a1_file_version a where a.tid="+tid;
		if(rids.length>0){
			StringBuilder sb = new StringBuilder(" and a.rid in (");
			for(int i=0; i<rids.length; i++){
				sb.append(""+rids[i]+",");
			}
			sb.replace(sb.lastIndexOf(","), sb.length(), ")");
			sql = sql + sb.toString();
		}
		sql = sql + " and a.version = (select max(b.version) from a1_file_version b where a.rid=b.rid and a.tid=b.tid)";
		return this.getJdbcTemplate().query(sql, fileVersionRowMapper);
	}

	@Override
	public int recoverFileVersion(int rid, int tid) {
		return this.getJdbcTemplate().update(RECOVER_FILE_VERSION, new Object[]{tid,rid});
	}

	@Override
	public FileVersion getFileVersionByDocId(int clbId, String clbVersion) {
		String sql = "select * from a1_file_version where clb_id=? and clb_version=?";
		List<FileVersion> f = getJdbcTemplate().query(sql, new Object[]{clbId,clbVersion}, fileVersionRowMapper);
		if(f!=null&&!f.isEmpty()){
			return f.get(0);
		}
		return null;
	}

}
