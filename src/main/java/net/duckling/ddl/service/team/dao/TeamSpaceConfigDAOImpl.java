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
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.team.TeamSpaceConfig;
import net.duckling.ddl.service.team.impl.TeamSpaceConfigDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TeamSpaceConfigDAOImpl extends AbstractBaseDAO implements TeamSpaceConfigDAO {
	private static final String QUERY = "select * from a1_team_space_config where tid=? order by id desc";
	private static final String UPDATE = "update a1_team_space_config set tid=?,size=?,update_time=?,update_uid=?,description=? where id=?";
	private static final String INSERT = "insert into a1_team_space_config (tid,size,update_time,update_uid,description) values(?,?,?,?,?)";
	private static final String QUERY_ALL= "select * from a1_team_space_config order by id desc";
	private static RowMapper<TeamSpaceConfig> rowMapper = new RowMapper<TeamSpaceConfig>(){

		@Override
		public TeamSpaceConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			TeamSpaceConfig config = new TeamSpaceConfig();
			config.setId(rs.getInt("id"));
			config.setSize(rs.getLong("size"));
			config.setTid(rs.getInt("tid"));
			config.setUpdateTime(rs.getDate("update_time"));
			config.setUpdateUid(rs.getString("update_uid"));
			config.setDescription(rs.getString("description"));
			return config;
		}
		
	};
	@Override
	public TeamSpaceConfig getTeamSpaceConfig(int tid) {
		List<TeamSpaceConfig> c = getJdbcTemplate().query(QUERY + " limit 1", new Object[]{tid}, rowMapper);
		if(c==null||c.isEmpty()){
			return null;
		}
		return c.get(0);
	}

	@Override
	public boolean update(TeamSpaceConfig config) {
		return getJdbcTemplate().update(UPDATE, new Object[]{config.getTid(),config.getSize(),config.getUpdateTime(),config.getUpdateUid(),config.getDescription(),config.getId()})>0;
	}

	@Override
	public boolean insert(final TeamSpaceConfig c) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT,PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setInt(++i, c.getTid());
				ps.setLong(++i,c.getSize());
				ps.setTimestamp(++i, new Timestamp(c.getUpdateTime().getTime()));
				ps.setString(++i, c.getUpdateUid());
				ps.setString(++i, c.getDescription());
				return ps;
			}
		}, keyHolder);
		c.setId(keyHolder.getKey().intValue());
		return true;
	}

	@Override
	public List<TeamSpaceConfig> getAllTeamSpaceConfig() {
		return getJdbcTemplate().query(QUERY_ALL, rowMapper);
	}

	@Override
	public void delete(int id) {
		String delete = "delete from a1_team_space_config where id=?";
		getJdbcTemplate().update(delete,new Object[]{id});
	}

	@Override
	public TeamSpaceConfig getById(int id) {
		String sql = "select * from a1_team_space_config where id=?";
		List<TeamSpaceConfig> c = getJdbcTemplate().query(sql, new Object[]{id}, rowMapper);
		if(c==null||c.isEmpty()){
			return null;
		}
		return c.get(0);
	}

}
