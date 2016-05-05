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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.subscribe.impl.SubscriptionDAO;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamState;
import net.duckling.ddl.service.team.impl.TeamDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.PaginationBean;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


/**
 * @date 2011-3-1
 * @author Clive Lee
 */
public class TeamDAOImpl extends AbstractBaseDAO implements TeamDAO{
	protected static final Logger log = Logger.getLogger(SubscriptionDAO.class);

	private RowMapper<Team> rowMapper = new RowMapper<Team>() {
		public Team mapRow(ResultSet rs, int index) throws SQLException {
			Team instance = new Team();
			instance.setId(rs.getInt("id"));
			instance.setName(rs.getString("name"));
			instance.setDisplayName(rs.getString("display_name"));
			instance.setDescription(rs.getString("description"));
			instance.setCreateTime(rs.getDate("create_time"));
			instance.setCreator(rs.getString("creator"));
			instance.setType(rs.getString("type"));
			instance.setAccessType(rs.getString("access_type"));
			instance.setDefaultMemberAuth(rs.getString("default_member_auth"));
			instance.setState(TeamState.valueOf(rs.getString("state")));
			instance.setVmtdn(rs.getString("vmtdn"));
			instance.setTeamDefaultView(rs.getString("team_default_view"));
			return instance;
		}
	};

	public Team getTeamByName(String name) {
		String sql = "select * from vwb_team where name=?";
		return getJdbcTemplate().query(sql, new Object[] { name },
				new ResultSetExtractor<Team>() {
					public Team extractData(ResultSet rs) throws SQLException,
							DataAccessException {
						if (rs.next()) {
							return rowMapper.mapRow(rs, 0);
						} else{
							return null;							
						}

					}

				});
	}

	public Team getTeamById(int tid) {
		String sql = "select * from vwb_team where id=?";
		return  getJdbcTemplate().queryForObject(sql,new Object[] { tid }, rowMapper);
	}

	public void updateBasicInfo(String tid, String title, String description, String accessType, String defaultMemberAuth,Date time,String defaultView) {
		String sql = "update vwb_team set display_name=?,description=?,access_type=?, default_member_auth=?,create_time=?,team_default_view=? where name=?";
		getJdbcTemplate().update(sql, new Object[] { title, description, accessType, defaultMemberAuth,new Timestamp(time.getTime()),defaultView, tid });
	}

	public List<Team> getAllUserTeams(String user) {
		String sql = "select t.* from vwb_team_member m,vwb_team t where m.uid=? and m.tid=t.id and t.state!='hangup' order by m.id desc";
		return this.getJdbcTemplate().query(sql, new Object[] { user },
				rowMapper);
	}

	public synchronized int createTeam(final Team team) {
		final String sql = "insert into vwb_team (state,create_time,name,display_name," +
				" description,creator,type,access_type,default_member_auth,vmtdn,team_default_view) values(?,?,?,?,?,?,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				int i = 0;
				PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				ps.setString(++i, team.getState().getValue());
				ps.setTimestamp(++i, new Timestamp(team.getCreateTime().getTime()));
				ps.setString(++i, team.getName());
				ps.setString(++i, team.getDisplayName());
				ps.setString(++i, team.getDescription());
				ps.setString(++i, team.getCreator());
				ps.setString(++i, team.getType());
				ps.setString(++i, team.getAccessType());
				ps.setString(++i, team.getDefaultMemberAuth());
				ps.setString(++i, team.getVmtdn());
				ps.setString(++i, team.getTeamDefaultView());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public void updateTeamState(int tid, TeamState state) {
	        getJdbcTemplate().update("update vwb_team set state=? where id=?",
	                new Object[] { state.getValue(), tid});
	}
	
	@Override
	public void updateTeamName(int tid, String name) {
		//清空vmtdn
        getJdbcTemplate().update("update vwb_team set name=?,vmtdn='' where id=?",
                new Object[] { name, tid});
	}
	
	public boolean updateTeamVmtDn(int tid,String vmtdn){
		return getJdbcTemplate().update("update vwb_team set vmtdn=? where id=?", new Object[]{vmtdn,tid})>0;
	}
	public List<Team> getAllTeams() {
        String sql = "SELECT * FROM vwb_team where state!='hangup'  order by create_time desc";
        return getJdbcTemplate().query(sql, rowMapper);
	}

	@Override
	public List<Team> getAllPublicAndProtectedTeam(int offset, int size) {
		if(offset<0 || size<0 || (size==0 && offset>0)){
			log.error("offset and size should be zero or positive, however in this query," +
					"offset = "+offset+" and size = "+size);
			return null;
		}
		String sql = "select * from vwb_team where state='work' and access_type in('"+Team.ACCESS_PUBLIC+"','"+Team.ACCESS_PROTECTED+"') order by id desc";
		String limit = "";
		if(offset>=0 && size>0){
			limit = " limit "+offset+","+size;
		}
		return this.getJdbcTemplate().query(sql+limit, rowMapper);
	}
	
	@Override
	public List<Team> getAllUserPublicAndProtectedTeam(String uid){
		String sql = "select t.* from vwb_team_member m,vwb_team t where m.uid=? and m.tid=t.id " +
				"and t.access_type in('"+Team.ACCESS_PUBLIC+"','"+Team.ACCESS_PROTECTED+"') and t.state='work'  order by m.id desc";
		return this.getJdbcTemplate().query(sql, new Object[]{uid}, rowMapper);
	}
	
	@Override
	public int getTotalTeamNumber() {
		String sql = "select count(*) from vwb_team where state='work' and type!='personal'";
		return this.getJdbcTemplate().queryForObject(sql, Integer.class);
	}

	@Override
	public List<Team> getTeamByCreator(String uid) {
		String sql = "select * from vwb_team where creator=? and state='work'";
		return getJdbcTemplate().query(sql, new Object[]{uid}, rowMapper);
	}

	@Override
	public List<Team> queryByTeamCode(String queryWord) {
		String sql = "select * from vwb_team where `name` like ? and state!='hangup' order by id desc";
		return getJdbcTemplate().query(sql,new Object[]{"%"+queryWord+"%"}, rowMapper);
	}

	@Override
	public List<Team> queryByTeamName(String queryWord) {
		String sql = "select * from vwb_team where display_name like ? and state!='hangup' order by id desc";
		return getJdbcTemplate().query(sql,new Object[]{"%"+queryWord+"%"}, rowMapper);
	}

	@Override
	public List<Team> getTeamByType(String type) {
		String sql  = "select * from vwb_team where type=? and state!='hangup' ";
		return getJdbcTemplate().query(sql, new Object[]{type}, rowMapper);
	}

	@Override
	public PaginationBean<Team> queryByTeamName(String queryWord, int offset, int size) {
		String countSql = "select count(1) from vwb_team where state='work' and access_type in('"+Team.ACCESS_PUBLIC+"','"+Team.ACCESS_PROTECTED+"')  and display_name like ?";
		Integer i = getJdbcTemplate().queryForObject(countSql, new Object[]{"%"+queryWord+"%"}, Integer.class);
		PaginationBean<Team> result = new PaginationBean<Team>();
		result.setBegin(offset);
		result.setTotal(i);
		result.setSize(size);
		String sql = "select * from vwb_team where state='work' and access_type in('"+Team.ACCESS_PUBLIC+"','"+Team.ACCESS_PROTECTED+"') and display_name like ? order by id desc limit ?,?";
		List<Team> r = getJdbcTemplate().query(sql, new Object[]{"%"+queryWord+"%",offset,size}, rowMapper);
		result.setData(r);
		return result;
	}
}
