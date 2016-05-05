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

import net.duckling.ddl.service.user.UserSortPreference;
import net.duckling.ddl.service.user.impl.UserSortPreferenceDao;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserSortPreferenceDaoImpl extends AbstractBaseDAO implements UserSortPreferenceDao {
	
	private static final Logger LOG = Logger.getLogger(UserSortPreferenceDaoImpl.class);
	
	private static final String INSERT = "insert into a1_user_sort_preference (uid,type,sort_type,last_modify) values(?,?,?,?)";
	
	private RowMapper<UserSortPreference> rowMapper = new RowMapper<UserSortPreference>() {
		public UserSortPreference mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserSortPreference user = new UserSortPreference();
			user.setId(rs.getInt("id"));
			user.setLastModify(rs.getTimestamp("last_modify"));
			user.setSortType(rs.getString("sort_type"));
			user.setType(rs.getString("type"));
			user.setUid(rs.getString("uid"));
			return user;
		}
	};
	
	@Override
	public int create(final UserSortPreference user) {
		KeyHolder key = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator(){
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pre = con.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				pre.setString(++i, user.getUid());
				pre.setString(++i, user.getType());
				pre.setString(++i, user.getSortType());
				pre.setTimestamp(++i, new Timestamp(user.getLastModify().getTime()));
				return pre;
			}
			
		}, key);
		return 0;
	}

	@Override
	public void update(UserSortPreference user) {
		String sql = "update a1_user_sort_preference set type=?,sort_type=?,last_modify=? where id=?";
		getJdbcTemplate().update(sql, user.getType(),user.getSortType(),user.getLastModify(),user.getId());
	}

	@Override
	public int delete(int id) {
		String sql = "delete from a1_user_sort_preference where id=?";
		return getJdbcTemplate().update(sql, id);
	}

	@Override
	public UserSortPreference query(String uid, String type) {
		String sql = "select * from a1_user_sort_preference where uid=? and type=?";
		List<UserSortPreference> result = getJdbcTemplate().query(sql, new Object[]{uid,type}, rowMapper);
		if(result==null||result.isEmpty()){
			return null;
		}else{
			if(result.size()>1){
				LOG.warn("user sort preference data error :"+result);
			}
			return result.get(0);
		}
	}

	@Override
	public List<UserSortPreference> query(String uid) {
		String sql = "select * from a1_user_sort_preference where uid=?";
		return getJdbcTemplate().query(sql, new Object[]{uid}, rowMapper);
	}

}
