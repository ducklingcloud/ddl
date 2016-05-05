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
package net.duckling.ddl.service.tobedelete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.file.DFileRef;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.StringUtil;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class FileDAOImpl extends AbstractBaseDAO implements FileDAO {

	private static final Logger LOG = Logger.getLogger(FileDAOImpl.class);
	
	private static final String SQL_CREATE = "insert into a1_file(clb_id,status,fid,tid,title,create_time,creator,last_editor,last_edit_time,last_version,clb_version) " +
			"values(?,?,?,?,?,?,?,?,?,?,?)";
	//modify by lvly@2012-07-20
	private static final String SQL_DELETE = "update a1_file set status='"+LynxConstants.STATUS_DELETE+"'";
	private static final String SQL_QUERY = "select * from a1_file";
	private static final String SQL_UPDATE = "update a1_file set clb_id=?," +
			"status=?, title=?, create_time=?, creator=?, last_editor=?," +
			"last_edit_time=?, last_version=?,clb_version=?";
	private static final String BY_TID = " where tid=?";
	private static final String BY_FIDTID = " where fid=? and tid=?";
	private static final String SQL_MAXFID = "select max(fid) from a1_file where tid=?";
	
	private static final String SQL_QUERY_STARTNAME = "select * from a1_file where tid=? and title like ? and status='"+LynxConstants.STATUS_AVAILABLE+"'";
	
	private static final String SQL_QUERY_EMAIL_ATTACH_BY_MID="select * from a1_file a1 where creator =? and tid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' and fid in(select fid from a1_emailattach em where em.mid=? and a1.tid=em.tid) order by id desc";
	
	private static final String SQL_QUERY_BY_UID_TID = "select * from a1_file where tid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' order by last_edit_time desc limit ?,?";
	private static final String SQL_QUERY_BY_UID_TID_COUNT = "select count(1) from a1_file where tid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' ";
	private static final String SQL_QUERY_BY_TID_TITLE = "select * from a1_file where tid=? and title=? and status='"+LynxConstants.STATUS_AVAILABLE+"' ";
	private RowMapper<File> fileRowMapper = new RowMapper<File>(){

		@Override
		public File mapRow(ResultSet rs, int index) throws SQLException {
			File file = new File();
			file.setId(rs.getInt("id"));
			file.setClbId(rs.getInt("clb_id"));
			file.setStatus(rs.getString("status"));
			file.setFid(rs.getInt("fid"));
			file.setTid(rs.getInt("tid"));
			file.setTitle(rs.getString("title"));
			file.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			file.setCreator(rs.getString("creator"));
			file.setLastEditor(rs.getString("last_editor"));
			file.setLastEditTime(new Date(rs.getTimestamp("last_edit_time").getTime()));
			file.setLastVersion(rs.getInt("last_version"));
			file.setClbVersion(rs.getInt("clb_version"));
			return file;
		}
		
	};
	
	@Override
	public synchronized int create(final File file) {
		if(file.getFid()<=0){
			int newFid = getMaxFid(file.getTid())+1;
			file.setFid(newFid);
		}
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, file.getClbId());
				ps.setString(++i, file.getStatus());
				ps.setInt(++i, file.getFid());
				ps.setInt(++i, file.getTid());
				ps.setString(++i, file.getTitle());
				ps.setTimestamp(++i, new Timestamp(file.getCreateTime().getTime()));
				ps.setString(++i, file.getCreator());
				ps.setString(++i, file.getLastEditor());
				ps.setTimestamp(++i, new Timestamp(file.getLastEditTime().getTime()));
				ps.setInt(++i, file.getLastVersion());
				ps.setInt(++i, file.getClbVersion());
				return ps;
			}
			
		}, keyHolder);
		return file.getFid();
	}
	
	@Override
	public int delete(int fid, int tid) {
		return this.getJdbcTemplate().update(SQL_DELETE+BY_FIDTID, new Object[]{fid, tid});
	}
	
	@Override
	public int batchDelete(int tid, List<Integer> fids){
		if(null == fids || fids.isEmpty()){
			return 0;
		}
		String sql = SQL_DELETE + " where tid="+tid+" and fid in(";
		StringBuilder sb = new StringBuilder();
		for(int fid : fids){
			sb.append(fid+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		sql += sb.toString();
		return this.getJdbcTemplate().update(sql);
	}

	@Override
	public int update(int fid, int tid, File file) {
		return this.getJdbcTemplate().update(SQL_UPDATE+BY_FIDTID, new Object[]{
				file.getClbId(), file.getStatus(), file.getTitle(),
				new Timestamp(file.getCreateTime().getTime()), file.getCreator(),
				file.getLastEditor(), new Timestamp(file.getLastEditTime().getTime()),
				file.getLastVersion(),file.getClbVersion(), fid, tid});
	}
	public int update( File file) {
		return this.getJdbcTemplate().update(SQL_UPDATE+" where id=?", new Object[]{
				file.getClbId(), file.getStatus(), file.getTitle(),
				new Timestamp(file.getCreateTime().getTime()), file.getCreator(),
				file.getLastEditor(), new Timestamp(file.getLastEditTime().getTime()),
				file.getLastVersion(),file.getClbVersion(), file.getId()});
	}
	@Override
	public File getFile(int fid, int tid) {
		List<File> list = this.getJdbcTemplate().query(SQL_QUERY+BY_FIDTID, 
				new Object[]{fid, tid}, fileRowMapper);
		if(null==list || list.size()<=0)
		{
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for File " +
					"by fid = "+fid+" and tid = "+tid);
		}
		return list.get(0);
	}

	@Override
	public List<File> getFilesOfTeam(int tid, int offset, int size) {
		if(offset<0 || size<0 || (size==0 && offset>0)){
			LOG.error("offset and size should be zero or positive, however in this query," +
					"offset = "+offset+" and size = "+size);
			return null;
		}
		String limit = "";
		if(offset>=0 && size>0){
			limit = " limit "+offset+","+size;
		}
		return this.getJdbcTemplate().query(SQL_QUERY+BY_TID+limit, new Object[]{tid}, fileRowMapper);
	}

	private int getMaxFid(int tid){
		return this.getJdbcTemplate().queryForInt(SQL_MAXFID, new Object[]{tid});
	}
	
	
	private RowMapper<DFileRef> refMapper = new RowMapper<DFileRef>() {
		@Override
		public DFileRef mapRow(ResultSet rs, int rowNum) throws SQLException {
			DFileRef r = new DFileRef();
			r.setId(rs.getInt("id"));
			r.setFileRid(rs.getInt("file_rid"));
			r.setPageRid(rs.getInt("page_rid"));
			r.setTid(rs.getInt("tid"));
			return r;
		}
	};
	
	private static final String SELECT_DFILE_REF = "SELECT * FROM vwb_dfile_ref WHERE fid=? and tid=?";
	@Override
	public List<DFileRef> getReferenceOfDFile(int fid,int tid) {
		return getJdbcTemplate().query(SELECT_DFILE_REF, new Object[] { fid,tid }, refMapper);
	}

	@Override
	public void deleteDFileReference(int fid, int tid) {
		String sql = "delete from vwb_dfile_ref where tid=? and fid=? ";
		getJdbcTemplate().update(sql,new Object[]{tid,fid});
	}
	
	public void deleteFileAndPageReference(int fid,int pid,int tid){
	    String sql = "DELETE FROM vwb_dfile_ref WHERE fid=? and pid=? and tid=?";
	    getJdbcTemplate().update(sql,new Object[]{fid,pid,tid});
	}
	
	@Override
	public void removePageRefers(int pid,int tid) {
		String sql = "delete from vwb_dfile_ref where tid=? and pid=?";
		getJdbcTemplate().update(sql, new Object[]{tid,pid});
	}

	@Override
	public List<File> getFileByStartName(int tid, String name) {
		return getJdbcTemplate().query(SQL_QUERY_STARTNAME, new Object[]{tid,name}, fileRowMapper);
	}

	public List<File> getFileByEmailMidAndUid(String mid,int tid,String uid){
		return getJdbcTemplate().query(SQL_QUERY_EMAIL_ATTACH_BY_MID, new Object[]{uid,tid,mid}, fileRowMapper);
	}

	@Override
	public List<File> getFileByTid(int[] tids,int offset,int rows) {
		if(tids!=null&&tids.length>0){
			if(tids.length==1){
				return getJdbcTemplate().query(SQL_QUERY_BY_UID_TID, new Object[]{tids[0],offset,rows}, fileRowMapper);
			}else{
				String sql = "select * from a1_file where tid in"+StringUtil.getSQLInFromInt(tids)+" and status='"+LynxConstants.STATUS_AVAILABLE+"' order by last_edit_time desc limit ?,?";
				return getJdbcTemplate().query(sql, new Object[]{offset,rows}, fileRowMapper);
			}
		}
		return Collections.emptyList();
	}

	@Override
	public int getTeamFileCount(int[] tids) {
		if(tids!=null&&tids.length>0){
			if(tids.length==1){
				return getJdbcTemplate().queryForInt(SQL_QUERY_BY_UID_TID_COUNT, new Object[]{tids[0]});
			}else{
				String sql = "select count(1) from a1_file where tid in"+StringUtil.getSQLInFromInt(tids)+" and  status='"+LynxConstants.STATUS_AVAILABLE+"' ";;
				return getJdbcTemplate().queryForInt(sql);
			}
		}
		return 0;
	}

	@Override
	public List<File> getFileByTidAndTitle(String title, int tid) {
		return getJdbcTemplate().query(SQL_QUERY_BY_TID_TITLE, new Object[]{tid,title}, fileRowMapper);
	}
}
