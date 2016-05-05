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
import java.util.List;

import net.duckling.ddl.service.team.TeamCreateInfo;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TeamCreateInfoDAOImpl extends AbstractBaseDAO{
	private RowMapper<TeamCreateInfo> rowMapper = new RowMapper<TeamCreateInfo>(){

		@Override
		public TeamCreateInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			TeamCreateInfo info = new TeamCreateInfo();
			info.setId(rs.getInt("id"));
			info.setParamKey(rs.getString("param_key"));
			info.setParamValue(rs.getString("param_value"));
			info.setTid(rs.getInt("tid"));
			return null;
		}
		
	};
	public boolean create(final TeamCreateInfo info){
		final String sql = "insert into vwb_team_create_info (tid,param_key,param_value) values(?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				int i = 0;
				PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				ps.setInt(++i, info.getTid());
				ps.setString(++i, info.getParamKey());
				ps.setString(++i, info.getParamValue());
				
				return ps;
			}
		}, keyHolder);
		return true;
	}
	
	public List<TeamCreateInfo> getTeamCreatInfo(int tid){
		String sql = "select * from vwb_team_create_info where tid=?";
		return getJdbcTemplate().query(sql, new Object[]{tid}, rowMapper);
	}
}
