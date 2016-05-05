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
package net.duckling.ddl.service.mail.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.mail.EmailAttachment;
import net.duckling.ddl.service.mail.impl.EmailAttachmentDAO;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.dao.ResourceRowMapper;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.StringUtil;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class EmailAttachmentDAOImpl extends AbstractBaseDAO implements EmailAttachmentDAO {
	
	private static final String SQL_CREATE = "insert into a1_emailattach(tid,item_type,mid,creator,create_time,title,rid) values(?,?,?,?,?,?,?)";
	
	private static final String SQL_UPDATE = "update a1_emailattach set tid=?,item_type=?,mid=?,creator=?,create_time=?,title=?,rid=? where id=?";
	private static final String SQL_DELETE = "delete from a1_emailattach where id = ?";
	private static final String SQL_QUERY_BY_UID_TID="select * from a1_emailattach where uid=? and tid=?";
	private static final String SQL_QUERY_RESOURCE_BY_UID_TID = "select a1.* from a1_resource a1 where tid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"' order by last_edit_time desc limit ?,?";
	
	private static final String SQL_QUERY_BY_MID = "select * from a1_emailattach where mid=?";
	private static final String SQL_QUERY_EMAIL_ATTACH_BY_MID="select a1.* from a1_resource a1 where creator =? and tid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"' and a1.rid in(select rid from a1_emailattach em where em.mid=? and em.tid=a1.tid) order by a1.rid desc";
	private static final String SQL_QUERY_BY_UID_TID_COUNT = "select count(1) from a1_resource where tid=? and status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"'";
	private static final String SQL_QUERY_BY_TID_TITLE = "select a1.* from a1_resource a1 where tid=? and title=? and bid=0 and status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"'";
	
	
	private RowMapper<Resource> resourceRowMapper = new ResourceRowMapper("a1.");
	private RowMapper<EmailAttachment> rowMapper = new RowMapper<EmailAttachment>(){

		@Override
		public EmailAttachment mapRow(ResultSet rs, int rowNum) throws SQLException {
			EmailAttachment result = new EmailAttachment();
			result.setId(rs.getInt("id"));
			result.setCreator(rs.getString("creator"));
			result.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			result.setItemType(rs.getString("item_type"));
			result.setMid(rs.getString("mid"));
			result.setTid(rs.getInt("tid"));
			result.setTitle(rs.getString("title"));
			return result;
		}};
	@Override
	public int create(final EmailAttachment attachment) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = null;
				int i=1;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setInt(i++,attachment.getTid());
				ps.setString(i++, attachment.getItemType());
				ps.setString(i++, attachment.getMid());
				ps.setString(i++, attachment.getCreator());
				ps.setTimestamp(i++, new Timestamp(attachment.getCreateTime().getTime()));
				ps.setString(i++, attachment.getTitle());
				ps.setInt(i++, attachment.getRid());
				return ps;
			}},keyHolder);
		Number key = keyHolder.getKey();
		attachment.setId(key == null?-1:key.intValue());
		return attachment.getId();
	}

	@Override
	public boolean update(final EmailAttachment a) {
		
		return getJdbcTemplate().update(SQL_UPDATE, new PreparedStatementSetter(){

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int i = 1;
				ps.setInt(i++, a.getTid());
				ps.setString(i++, a.getItemType());
				ps.setString(i++, a.getMid());
				ps.setString(i++, a.getCreator());
				ps.setTimestamp(i++, new Timestamp(a.getCreateTime().getTime()));
				ps.setString(i++, a.getTitle());
				ps.setInt(i++, a.getRid());
				ps.setInt(i++, a.getId());
			}})>0;
	}

	@Override
	public boolean delete(int id) {
		return getJdbcTemplate().update(SQL_DELETE, new Object[]{id})>0;
	}

	@Override
	public List<EmailAttachment> findByUserAndTid(String uid, int tid) {
		
		return getJdbcTemplate().query(SQL_QUERY_BY_UID_TID, new Object[]{uid,tid}, rowMapper);
	}

	@Override
	public List<EmailAttachment> findByMid(String mid) {
		return getJdbcTemplate().query(SQL_QUERY_BY_MID, new Object[]{mid},rowMapper);
	}

	@Override
	public List<Resource> getFileByEmailMidAndUid(String mid, int tid, String uid) {
		return getJdbcTemplate().query(SQL_QUERY_EMAIL_ATTACH_BY_MID, new Object[]{uid,tid,mid}, resourceRowMapper);
	}

	@Override
	public List<Resource> getFileByTid(int[] tids, int offset, int rows) {
		if(tids!=null&&tids.length>0){
			if(tids.length==1){
				return getJdbcTemplate().query(SQL_QUERY_RESOURCE_BY_UID_TID, new Object[]{tids[0],offset,rows}, resourceRowMapper);
			}else{
				String sql = "select a1.* from a1_resource a1 where tid in"+StringUtil.getSQLInFromInt(tids)+" and status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"' order by last_edit_time desc limit ?,?";
				return getJdbcTemplate().query(sql, new Object[]{offset,rows}, resourceRowMapper);
			}
		}
		return Collections.emptyList();
	}

	@Override
	public int getTeamFileCount(int[] tids) {
		if(tids!=null&&tids.length>0){
			if(tids.length==1){
				return getJdbcTemplate().queryForObject(SQL_QUERY_BY_UID_TID_COUNT, new Object[]{tids[0]}, Integer.class);
			}else{
				String sql = "select count(1) from a1_resource where tid in"+StringUtil.getSQLInFromInt(tids)+" and  status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"'";;
				return getJdbcTemplate().queryForObject(sql, Integer.class);
			}
		}
		return 0;
	}

	@Override
	public List<Resource> getFileByTidAndTitle(String title, int tid) {
		return getJdbcTemplate().query(SQL_QUERY_BY_TID_TITLE, new Object[]{tid,title}, resourceRowMapper);
	}
	

}
