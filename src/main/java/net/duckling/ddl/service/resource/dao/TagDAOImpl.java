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

import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.impl.TagDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.PinyinUtil;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.HtmlUtils;


@Repository
public class TagDAOImpl extends AbstractBaseDAO implements TagDAO {
	
	private static final Logger LOG = Logger.getLogger(TagDAOImpl.class);
	
	private static final String SQL_CREATE = "insert into a1_tag(tid,title,creator,count,group_id,create_time,pinyin,sequence) values(?,?,?,?,?,?,?,?)";
	private static final String SQL_DELETE_BYID = "delete from a1_tag where id=?";
	private static final String SQL_DELETE = "delete from a1_tag ";
	private static final String SQL_UPDATE = "update a1_tag set tid=?, title=?, " +
			"creator=?, count=?, group_id=?, create_time=?, pinyin=?,sequence=? where id=?";
	private static final String SQL_QUERY_TAGBYID = "select * from a1_tag where id=?";
	private static final String SQL_QUERY = "select * from a1_tag";
	private static final String ALLTEAMTAG = " where tid=?";
	private static final String ALLTEAMTAG_INTAGGROUP = " where tid=? and group_id=? " +
			"order by sequence,count desc";
	
	private static final String SQL_UPDATE_GROUP = "update a1_tag set group_id = ?"; 

	private RowMapper<Tag> tagRowMapper = new RowMapper<Tag>(){

		@Override
		public Tag mapRow(ResultSet rs, int index) throws SQLException {
			Tag tag = new Tag();
			tag.setId(rs.getInt("id"));
			tag.setTid(rs.getInt("tid"));
			tag.setTitle(HtmlUtils.htmlEscape(rs.getString("title")));
			tag.setCreator(rs.getString("creator"));
			tag.setCount(rs.getInt("count"));
			tag.setGroupId(rs.getInt("group_id"));
			tag.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			tag.setPinyin(rs.getString("pinyin"));
			tag.setSequence(rs.getInt("sequence"));
			return tag;
		}
		
	};
	
	@Override
	public synchronized int create(final Tag tag) {
		final int sequence = getMaxSequenceInGroup(tag.getTid(), tag.getGroupId());
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		this.getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = null;
				ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, tag.getTid());
				ps.setString(++i, tag.getTitle());
				ps.setString(++i, tag.getCreator());
				ps.setInt(++i, tag.getCount());
				ps.setInt(++i, tag.getGroupId());
				ps.setTimestamp(++i, new Timestamp(tag.getCreateTime().getTime()));
				ps.setString(++i, PinyinUtil.getPinyin(tag.getTitle()));
				ps.setInt(++i, sequence+1);
				return ps;
			}
			
		}, keyHolder);
		Number key = keyHolder.getKey();
		int id = (key==null)?-1:key.intValue();
		tag.setId(id);
		return id;
	}
	
	private int getMaxSequenceInGroup(int tid, int groupId){
		String sql = "select max(sequence) from a1_tag where tid=? and group_id=?";
		try{
			return getJdbcTemplate().queryForObject(sql, new Object[]{tid, groupId}, Integer.class);
		}catch(NullPointerException e){
			return 0;
		}
	}

	@Override
	public int delete(int id) {
		return getJdbcTemplate().update(SQL_DELETE_BYID, new Object[]{id});
	}
	
	@Override
	public int deleteBatch(int[] ids){
		if(null == ids || ids.length<=0){
			return -1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("where id in(");
		for(int i=0;i<ids.length;i++){
			sb.append(ids[i]+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		return this.getJdbcTemplate().update(SQL_DELETE+sb.toString());
	}

	@Override
	public int update(int id, Tag tag) {
		String pinyin = PinyinUtil.getPinyin(tag.getTitle());
		return this.getJdbcTemplate().update(SQL_UPDATE, new Object[]{tag.getTid(), tag.getTitle(),
				tag.getCreator(), tag.getCount(), tag.getGroupId(), 
				new Timestamp(tag.getCreateTime().getTime()), pinyin,tag.getSequence(), id});	
	}

	@Override
	public Tag getTagById(int id) {
		List<Tag> list = this.getJdbcTemplate().query(SQL_QUERY_TAGBYID, new Object[]{id}, tagRowMapper);
		if(null==list || list.size()<=0){
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for Tag " +
					"by id = "+id);
		}
		return list.get(0);
	}

	@Override
	public List<Tag> getAllTag() {
		return this.getJdbcTemplate().query(SQL_QUERY, tagRowMapper);
	}

	@Override
	public List<Tag> getAllTagInTeam(int tid) {
		return this.getJdbcTemplate().query(SQL_QUERY+ALLTEAMTAG, new Object[]{tid}, tagRowMapper);
	}
	
	@Override
	public List<Tag> getTagsByPinyin(int tid, String pinyin){
		String sql = "select * from a1_tag where tid=:tid and pinyin like :pinyin";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", tid);
		params.put("pinyin", "%"+pinyin+"%");
		return this.getNamedParameterJdbcTemplate().query(sql, params, tagRowMapper);
	}
	
	@Override
	public List<Tag> getTagsByName(int tid, String name){
		String sql = "select * from a1_tag where tid=:tid and title like :name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", tid);
		params.put("name", "%"+name+"%");
		return this.getNamedParameterJdbcTemplate().query(sql, params, tagRowMapper);
	}

	@Override
	public List<Tag> getAllTagInTeamAndGroup(int tid, int tagGroupId) {
		return this.getJdbcTemplate().query(SQL_QUERY+ALLTEAMTAG_INTAGGROUP, 
				new Object[]{tid, tagGroupId}, tagRowMapper);
	}

	@Override
	public List<Tag> getAllGroupTagsByTeam(int tid) {
		String sql = "select * from a1_tag where tid=? and group_id<>0 order by sequence, count desc";
		return this.getJdbcTemplate().query(sql, new Object[]{tid}, tagRowMapper);
	}

	@Override
	public int addTags2Group(int tagGroupId, int[] tgids) {
		if(null == tgids || tgids.length<=0){
			return -1;
		}
		int len = tgids.length;
		StringBuilder condition = new StringBuilder();
		condition.append(" where id in(");
		for(int i=0; i<len; i++){
			condition.append(tgids[i]+",");
		}
		condition.deleteCharAt(condition.length()-1);
		condition.append(")");
		return this.getJdbcTemplate().update(SQL_UPDATE_GROUP+condition.toString(), new Object[]{tagGroupId});
	}

	@Override
	public int removeTagsFromGroup(int tagGroupId, int[] tgids) {
		if(null == tgids || tgids.length<=0){
			return -1;
		}
		int len = tgids.length;
		StringBuilder condition = new StringBuilder();
		condition.append(" where id in(");
		for(int i=0; i<len; i++){
			condition.append(tgids[i]+",");
		}
		condition.deleteCharAt(condition.length()-1);
		condition.append(")");
		return this.getJdbcTemplate().update(SQL_UPDATE_GROUP+condition.toString(), new Object[]{0});
	}

	@Override
	public int increaseCount(int tagid, int delta) {
		String sql = "update a1_tag set count = count +? where id=?";
		return this.getJdbcTemplate().update(sql,new Object[]{delta,tagid});
	}
	
	@Override
	public int decreaseCount(List<Integer> tagids, int delta){
		if(null==tagids || tagids.isEmpty()){
			return 0;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("update a1_tag set count = count - ? where id in(") ;
		for(Integer tagid: tagids){
			sb.append(tagid+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		return this.getJdbcTemplate().update(sb.toString(), new Object[]{delta});
	}
	
	@Override
	public int updateCount(int tagid, int count){
		String sql = "update a1_tag set count = ? where id = ?";
		return this.getJdbcTemplate().update(sql, new Object[]{count, tagid});
	}

	@Override
	public int getTagTitleCount(int tid, String title) {
		String sql = "select count(*) from a1_tag where tid=? and title=?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[]{tid, title}, Integer.class);
	}

	@Override
	public List<Tag> getNotRelatedTags(int rid, int tid) {
		String sql = "select a.* from a1_tag a where a.tid=? and not exists (select null from a1_tag_item b where b.tgid = a.id and b.tid=? and b.rid=?)";
		return this.getJdbcTemplate().query(sql, new Object[]{tid,tid,rid},tagRowMapper);
	}

	@Override
	public List<Tag> getTags(int[] tagids) {
		if(null == tagids || tagids.length<=0){
			return new ArrayList<Tag>();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select * from a1_tag where id in(");
		for(int i=0; i<tagids.length; i++){
			sb.append(tagids[i]+",");
		}
		sb.replace(sb.lastIndexOf(","), sb.length(), ")");
		sb.append(" order by group_id");
		return this.getJdbcTemplate().query(sb.toString(), tagRowMapper);
	}
	
	@Override
	public Tag getTag(int tid, String title){
		String sql = "select * from a1_tag where tid=? and title=?";
		List<Tag> list = this.getJdbcTemplate().query(sql, new Object[]{tid,title}, tagRowMapper);
		if(null == list || list.size()<=0){
			return null;
		}
		else if(list.size()>1){
			LOG.error("there exist more than one object while quering for Tag by tid="+
					tid+" and title="+title);
		}
		return list.get(0);
	}
	@Override
	public Tag getTag(int tid, int gid, String title) {
		String sql = "select * from a1_tag where tid=? and title=? and group_id=?";
		List<Tag> list = this.getJdbcTemplate().query(sql, new Object[]{tid,title,gid}, tagRowMapper);
		if(null == list || list.size()<=0){
			return null;
		}else if(list.size()>1){
			LOG.error("there exist more than one object while quering for Tag by tid="+
					tid+" and title="+title);
		}
		return list.get(0);
	}
	
	@Override
	public Tag getUserNameTag(String uid){
		String sql = "select * from a1_tag where title = (select name from vwb_user_ext where uid='"+uid+"')";
		List<Tag> list = this.getJdbcTemplate().query(sql,tagRowMapper);
		if(null == list || list.size()<=0){
			return null;
		}else if(list.size()>1){
			LOG.error("there exist more than one object while quering for UserName Tag by uid="+uid);
		}
		return list.get(0);
	}
}
