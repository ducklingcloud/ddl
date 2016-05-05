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

package net.duckling.ddl.service.devent.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.DAction;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.devent.impl.NoticeDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.StringUtil;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-11-2
 * @author clive
 */
@Repository
public class NoticeDAOImpl extends AbstractBaseDAO implements NoticeDAO{
	
	
	private static final String SAVE = "insert into vwb_notice(notice_type,tid,event_id,recipient,actor_id,actor_name,actor_url," +
			"operation,target_id,target_type,target_name,target_url,target_version,reason,message,occur_time,addition,relative_id,relative_name,relative_url) " +
			"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String QUERY_ONE_TEAM_NOTICE = "select * from vwb_notice where recipient=? and tid=? and notice_type=? " +
			"and occur_time>=? and occur_time<? order by occur_time desc";
	private static final String QUERY_A_NOTICE_BY_ID="select * from vwb_notice where id=?";
	
	private static final String QUERY_TODAY_TEAM_NOTICE="select * from vwb_notice where  occur_time>=? and notice_type!='"+ NoticeRule.HISTORY_NOTICE  +"' order by occur_time desc";
	private static final String GET_TOP_K_NOTICE = "SELECT * FROM (SELECT * FROM vwb_notice WHERE recipient=? and tid=? and notice_type=? and occur_time>=? and occur_time<? and event_id in(??) ORDER BY occur_time DESC) n " +
			"GROUP BY n.target_id,n.target_type ORDER BY n.id DESC LIMIT ?;";
	
	private static final String COUNT_UNREAD_NOTICE = "select count(n.id) from (select id from vwb_notice where recipient=? and tid=? and notice_type=? and occur_time>=? and occur_time<? " +
			"Group BY target_id,target_type ORDER BY occur_time DESC) n";
	
	private static final String QUERY_BY_UID_TARGETID = "select * from vwb_notice where notice_type=? and  target_id=?";

	private static final String QUERY_BY_EVENTID = "select * from vwb_notice where event_id =?";
	private RowMapper<Notice> mapper = new RowMapper<Notice>() {
		public Notice mapRow(ResultSet rs, int index) throws SQLException {
			Notice b = new Notice();
			b.setId(rs.getInt("id"));
			b.setTid(rs.getInt("tid"));
			b.setEventId(rs.getInt("event_id"));
			b.setRecipient(rs.getString("recipient"));
			b.setActor(new DEntity(rs.getString("actor_id"),DEntity.DUSER,rs.getString("actor_name"),rs.getString("actor_url")));
			b.setOperation(new DAction(rs.getString("operation")));
			b.setTarget(new DEntity(rs.getString("target_id"),rs.getString("target_type"),rs.getString("target_name"),rs.getString("target_url")));
			b.setReason(rs.getString("reason"));
			b.setMessage(rs.getString("message"));
			b.setOccurTime(rs.getTimestamp("occur_time"));
			b.setAddition(rs.getString("addition"));
			b.setTargetVersion(rs.getInt("target_version"));
			b.setNoticeType(rs.getString("notice_type"));
			if(rs.getString("relative_id")!=null){
				b.setRelative(new DEntity(rs.getString("relative_id"),DEntity.DUSER,rs.getString("relative_name"),rs.getString("relative_url")));
			}
			return b;
		}
	};

	@Override
	public Notice getNoticeById(int id) {
		List<Notice> notices=getJdbcTemplate().query(QUERY_A_NOTICE_BY_ID,new Object[]{id}, mapper);
		return CommonUtils.first(notices);
	}
	public void batchWriteNotices(final List<Notice> data) {
		getJdbcTemplate().batchUpdate(SAVE,
				new BatchPreparedStatementSetter(){
					public int getBatchSize() {
						return data.size();
					}

					public void setValues(PreparedStatement pst, int index)
							throws SQLException {
						int i = 0;
						Notice e = data.get(index);
						pst.setString(++i, e.getNoticeType());
						pst.setInt(++i, e.getTid());
						pst.setInt(++i, e.getEventId());
						pst.setString(++i, e.getRecipient());
						pst.setString(++i, e.getActor().getId());
						pst.setString(++i, e.getActor().getName());
						pst.setString(++i, e.getActor().getUrl());
						pst.setString(++i, e.getOperation().getName());
						pst.setString(++i, e.getTarget().getId());
						pst.setString(++i, e.getTarget().getType());
						pst.setString(++i, e.getTarget().getName());
						pst.setString(++i, e.getTarget().getUrl());
						pst.setInt(++i, e.getTargetVersion());
						pst.setString(++i, e.getReason());
						pst.setString(++i, e.getMessage());
						pst.setTimestamp(++i, new Timestamp(e.getOccurTime().getTime()));
						pst.setString(++i, e.getAddition());
						if(e.getRelative()!=null){
							pst.setString(++i, e.getRelative().getId());
							pst.setString(++i, e.getRelative().getName());
							pst.setString(++i, e.getRelative().getUrl());
						}else{
							pst.setString(++i, null);
							pst.setString(++i, null);
							pst.setString(++i, null);
						}
					}
				});
	}
	@Override
	public List<Notice> getRecentNotices(AoneNoticeParam param,int k){
		if(param.getEventIds()==null||param.getEventIds().isEmpty()){
			return Collections.emptyList();
		}
		String in = net.duckling.ddl.util.StringUtil.getSQLInFromInt(param.getEventIds());
		String sql = GET_TOP_K_NOTICE.replace("(??)",in);
		return getJdbcTemplate().query(sql, new Object[]{param.getRecipient(),param.getTid(),param.getNoticeType(),
				param.getBeginDate(),param.getEndDate(),k},mapper);
	}
	@Override
	public List<Notice> readOneTeamNotices(AoneNoticeParam param) {
		return getJdbcTemplate().query(QUERY_ONE_TEAM_NOTICE, new Object[]{param.getRecipient(),param.getTid(),param.getNoticeType(),
				param.getBeginDate(),param.getEndDate()},mapper);
	}
	@Override
	public List<Notice> readOneTeamTodayNotices() {
		return getJdbcTemplate().query(QUERY_TODAY_TEAM_NOTICE, new Object[]{
				DateUtils.addDays(new Date(), -1)},mapper);
	}
	
	@Override
	public List<Notice> readThisWeekWithoutHistory() {
		String sql =  "select * from vwb_notice where  occur_time>=? and notice_type!='"+ NoticeRule.HISTORY_NOTICE  +"' order by occur_time desc";
		return getJdbcTemplate().query(sql, new Object[]{
				DateUtils.addDays(new Date(), -7)},mapper);
	}
	
	@Override
	public int getRecentNoticeCount(AoneNoticeParam param) {
		return getJdbcTemplate().queryForObject(COUNT_UNREAD_NOTICE, 
				new Object[]{param.getRecipient(),param.getTid(),param.getNoticeType(),
				param.getBeginDate(),param.getEndDate()}, Integer.class);
	}

	@Override
	public List<Notice> getNoticeByTypeAndTargId(String uid, int targetId) {
		return getJdbcTemplate().query(QUERY_BY_UID_TARGETID, new Object[]{uid,targetId+""}, mapper);
	}
	@Override
	public List<Notice> getNoticeByEventId(int eventId){
		return getJdbcTemplate().query(QUERY_BY_EVENTID, new Object[]{eventId},mapper);
	}
	@Override
	public Notice getUserLatestNotice(String uid, List<Integer> eventId) {
		String sql = "select * from vwb_notice where recipient=? and event_id in"+StringUtil.getSQLInFromInt(eventId)
				+" order by id desc limit 1";
		List<Notice> result = getJdbcTemplate().query(sql,new Object[]{uid},mapper);
		if(result!=null&&!result.isEmpty()){
			return result.get(0);
		}
		return null;
	}
}
