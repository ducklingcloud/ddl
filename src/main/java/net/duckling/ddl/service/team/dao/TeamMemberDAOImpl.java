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

package net.duckling.ddl.service.team.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.team.impl.TeamMemberDAO;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

/**
 * @date 2011-5-27
 * @author Clive Lee
 */
public class TeamMemberDAOImpl extends AbstractBaseDAO implements TeamMemberDAO {
	private static final String UPDATE_TEAM_SEQUENCE = "update vwb_team_member set sequence=? where uid=? and tid=? ";
	private static final String INSERT_SQL = "insert into vwb_team_member(tid, uid,sequence,team_access,person_access,monitor_access,team_notice_count,person_notice_count,monitor_notice_count,team_event_ids,person_event_ids,monitor_event_ids) values(?,?,?,?,?,?,0,0,0,'','','')";
	private static final String QUERY = "select m.uid,e.name,e.pinyin,e.email,e.id from vwb_team_member m,vwb_user_ext e where m.tid=? and m.uid=e.uid order by convert(e.name using gbk)";

	private static final String REMOVE_MEMBER = "delete from vwb_team_member where tid=? and uid=?";

	private RowMapper<SimpleUser> rowMapper = new RowMapper<SimpleUser>() {
		public SimpleUser mapRow(ResultSet rs, int index) throws SQLException {
			SimpleUser instance = new SimpleUser();
			instance.setUid(rs.getString("uid"));
			instance.setName(rs.getString("name"));
			instance.setPinyin(rs.getString("pinyin"));
			instance.setEmail(rs.getString("email"));
			instance.setId(rs.getInt("id"));
			return instance;
		}
	};

	private RowMapper<UserExt> userExtRowMapper = new RowMapper<UserExt>() {
		public UserExt mapRow(ResultSet rs, int index) throws SQLException {
			UserExt instance = new UserExt();
			instance.setId(rs.getInt("id"));
			instance.setUid(rs.getString("uid"));
			instance.setName(rs.getString("name"));
			instance.setMobile(rs.getString("mobile"));
			instance.setEmail(rs.getString("email"));
			instance.setAddress(rs.getString("address"));
			instance.setMsn(rs.getString("msn"));
			instance.setQq(rs.getString("qq"));
			instance.setSex(rs.getString("sex"));
			instance.setOrgnization(rs.getString("orgnization"));
			instance.setTelephone(rs.getString("telephone"));
			// instance.setBirthday(rs.getDate("birthday"));
			instance.setDepartment(rs.getString("department"));
			instance.setWeibo(rs.getString("weibo"));
			instance.setPinyin(rs.getString("pinyin"));
			return instance;
		}
	};

	private RowMapper<UserExt> userExtRowMapperforUserContacts = new RowMapper<UserExt>() {
		public UserExt mapRow(ResultSet rs, int index) throws SQLException {
			UserExt instance = new UserExt();
			instance.setUid(rs.getString("uid"));
			instance.setName(rs.getString("name"));
			instance.setMobile(rs.getString("mobile"));
			instance.setEmail(rs.getString("email"));
			instance.setAddress(rs.getString("address"));
			instance.setMsn(rs.getString("msn"));
			instance.setQq(rs.getString("qq"));
			instance.setSex(rs.getString("sex"));
			instance.setOrgnization(rs.getString("orgnization"));
			instance.setTelephone(rs.getString("telephone"));
			instance.setPhoto(rs.getString("photo"));
			instance.setDepartment(rs.getString("department"));
			instance.setWeibo(rs.getString("weibo"));
			instance.setOperation(rs.getInt("operation"));
			if (rs.getTimestamp("regist_time") == null
					|| rs.getTimestamp("regist_time").toString().equals(""))
				instance.setRegist_time(rs.getTimestamp("regist_time"));
			else
				instance.setRegist_time(rs.getTimestamp("regist_time"));
			if (rs.getTimestamp("modifytime") == null
					|| rs.getTimestamp("modifytime").toString().equals(""))
				instance.setModifytime(rs.getTimestamp("modifytime"));
			else
				instance.setModifytime(rs.getTimestamp("modifytime"));
			instance.setVersion(rs.getInt("version"));

			return instance;
		}
	};

	@Override
	public synchronized boolean addTeamMembers(final String[] uids,
			final int tid) {
		getJdbcTemplate().batchUpdate(INSERT_SQL,
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return uids.length;
					}

					public void setValues(PreparedStatement pst, int index)
							throws SQLException {
						int i = 0;
						pst.setInt(++i, tid);
						pst.setString(++i, uids[index]);
						pst.setInt(++i, -1);
						pst.setTimestamp(++i,
								new Timestamp(new Date().getTime()));
						pst.setTimestamp(++i,
								new Timestamp(new Date().getTime()));
						pst.setTimestamp(++i,
								new Timestamp(new Date().getTime()));
					}
				});
		return false;
	}

	@Override
	public List<SimpleUser> getMembersByName(int tid, String name) {
		String sql = "select a.uid, b.name, b.pinyin,b.email from vwb_team_member a "
				+ "left join vwb_user_ext b on a.uid=b.uid where a.tid="
				+ tid
				+ " and b.name like '%" + name + "%'";
		return this.getJdbcTemplate().query(sql, rowMapper);
	}

	@Override
	public List<SimpleUser> getMembersByPinyin(int tid, String pinyin) {
		String sql = "select a.uid, b.name, b.pinyin ,b.email from vwb_team_member a "
				+ "left join vwb_user_ext b on a.uid=b.uid where a.tid="
				+ tid
				+ " and b.pinyin like '%" + pinyin + "%'";
		return this.getJdbcTemplate().query(sql, rowMapper);
	}

	@Override
	public List<SimpleUser> getMembersOrderByName(int tid) {
		return getJdbcTemplate().query(QUERY, new Object[] { tid }, rowMapper);
	}

	@Override
	public List<UserExt> getTeamContacts(int tid) {
		String sql = "select a.tid,b.* from vwb_team_member a, vwb_user_ext b where a.uid=b.uid and a.tid=?";
		return getJdbcTemplate().query(sql, new Object[] { tid },
				userExtRowMapper);
	}

	// get userext according tid
	@Override
	public List<UserExt> getUserExtContacts(int tid) {
		String sql = "select b.* from vwb_team_member a, vwb_user_ext b where a.uid=b.uid and a.tid=?";
		return getJdbcTemplate().query(sql, new Object[] { tid },
				userExtRowMapperforUserContacts);
	}

	@Override
	public boolean isAtTheSameTeam(String user1, String user2) {
		String sql = "select count(*) from vwb_team_member where uid=? and tid in (select tid from vwb_team_member WHERE uid=?)";
		return getJdbcTemplate().queryForObject(sql,
				new Object[] { user1, user2 }, Integer.class) != 0;
	}

	private boolean isUserInTeam(int tid, String uid) {
		String sql = "select count(*) from vwb_team_member where tid=? and uid=?";
		return getJdbcTemplate().queryForObject(sql,
				new Object[] { tid, uid }, Integer.class)>0;
	}

	@Override
	public Boolean[] isUsersInTeam(int tid, String[] uids) {
		if (null != uids && uids.length > 0) {
			Boolean[] result = new Boolean[uids.length];
			for (int i = 0; i < uids.length; i++) {
				result[i] = isUserInTeam(tid, uids[i]);
			}
			return result;
		}
		return null;
	}

	@Override
	public void removeMembers(final int tid, final String[] uids) {
		if (null == uids || uids.length <= 0) {
			return;
		}
		this.getJdbcTemplate().batchUpdate(REMOVE_MEMBER,
				new BatchPreparedStatementSetter() {

					@Override
					public int getBatchSize() {
						return uids.length;
					}

					@Override
					public void setValues(PreparedStatement ps, int index)
							throws SQLException {
						ps.setInt(1, tid);
						ps.setString(2, uids[index]);
					}

				});
	}

	@Override
	public int[] updateTeamSequence(final String uid, final String[] ids) {
		return getJdbcTemplate().batchUpdate(UPDATE_TEAM_SEQUENCE,
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return ids.length;
					}

					public void setValues(PreparedStatement pst, int index)
							throws SQLException {
						int i = 0;
						pst.setInt(++i, index);
						pst.setString(++i, uid);
						pst.setInt(++i, Integer.valueOf(ids[index]));
					}
				});
	}
	
	@Override
	public int getMemberAmount(int tid) {
		String sql = "select count(*) from vwb_team_member where tid=?";
		return this.getJdbcTemplate().queryForObject(sql, new Object[] { tid }, Integer.class);
	}
}