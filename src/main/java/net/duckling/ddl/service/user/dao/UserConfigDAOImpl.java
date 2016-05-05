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
package net.duckling.ddl.service.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import net.duckling.ddl.service.user.UserConfig;
import net.duckling.ddl.service.user.impl.UserConfigDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
@Repository
public class UserConfigDAOImpl extends AbstractBaseDAO implements UserConfigDAO {
	private static final String QUERY_BY_ID = "select * from a1_user_config where id=?";
	private static final String QUERY_BY_UID = "select * from a1_user_config where uid=?";
	private static final String UPDATE = "update a1_user_config set max_create_team=?,config_uid=?,config_date=?,description=? where id=?";
	private static final String INSERT = "insert into a1_user_config (uid,max_create_team,config_uid,config_date,description) values(?,?,?,?,?)";
	private static final String DELETE = "delete from a1_user_config where id=?";
	private static final String QUERY_ALL = "select * from a1_user_config order by id desc";
	private static RowMapper<UserConfig> rowMapper = new RowMapper<UserConfig>(){

		@Override
		public UserConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserConfig config = new UserConfig();
			config.setId(rs.getInt("id"));
			config.setConfigDate(rs.getDate("config_date"));
			config.setUid(rs.getString("uid"));
			config.setDescription(rs.getString("description"));
			config.setMaxCreateTeam(rs.getInt("max_create_team"));
			config.setConfigUid(rs.getString("config_uid"));
			return config;
		}
	};
	@Override
	public UserConfig getById(int id) {
		List<UserConfig> c = getJdbcTemplate().query(QUERY_BY_ID, new Object[]{id}, rowMapper);
		if(c==null||c.isEmpty()){
			return null;
		}
		return c.get(0);
	}

	@Override
	public UserConfig getByUid(String uid) {
		List<UserConfig> c = getJdbcTemplate().query(QUERY_BY_UID, new Object[]{uid}, rowMapper);
		if(c==null||c.isEmpty()){
			return null;
		}
		return c.get(0);
	}

	@Override
	public boolean update(UserConfig c) {
		return getJdbcTemplate().update(UPDATE, c.getMaxCreateTeam(),c.getConfigUid(),c.getConfigDate(),c.getDescription(),c.getId())>0;
	}

	@Override
	public boolean insert(final UserConfig c) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT,PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				ps.setString(++i, c.getUid());
				ps.setInt(++i, c.getMaxCreateTeam());
				ps.setString(++i, c.getConfigUid());
				ps.setTimestamp(++i, new Timestamp(c.getConfigDate().getTime()));
				ps.setString(++i, c.getDescription());
				return ps;
			}
		}, keyHolder);
		c.setId(keyHolder.getKey().intValue());
		return true;
	}

	@Override
	public boolean delete(int id) {
		return getJdbcTemplate().update(DELETE, id)>0;
	}

	@Override
	public List<UserConfig> getAllConfig() {
		return getJdbcTemplate().query(QUERY_ALL, rowMapper);
	}

}
