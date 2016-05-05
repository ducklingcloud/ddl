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

import net.duckling.ddl.service.user.impl.AuthorizationCodeDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.web.bean.AuthorizationCode;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class AuthorizationCodeDAOImpl extends AbstractBaseDAO implements AuthorizationCodeDAO {
	private static final String INSERT_SQL = "insert into a1_auth_code (code,access_token,uid,create_time,client_id,status) values(?,?,?,?,?,?)";
	private static final String QUERY_SQL = "select * from a1_auth_code where code=?";
	private RowMapper<AuthorizationCode> rowMapper = new RowMapper<AuthorizationCode>(){

		@Override
		public AuthorizationCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthorizationCode code = new AuthorizationCode();
			code.setId(rs.getInt("id"));
			code.setCode(rs.getString("code"));
			code.setAccessToken(rs.getString("access_token"));
			code.setUid(rs.getString("uid"));
			code.setCreateTime(rs.getTimestamp("create_time"));
			code.setClientId(rs.getString("client_id"));
			code.setStatus(rs.getString("status"));
			return code;
		}
	};
	
	@Override
	public void create( final AuthorizationCode code) {
		GeneratedKeyHolder key = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pst = con.prepareStatement(INSERT_SQL,PreparedStatement.RETURN_GENERATED_KEYS);
				int i=0;
				pst.setString(++i, code.getCode());
				pst.setString(++i, code.getAccessToken());
				pst.setString(++i, code.getUid());
				pst.setTimestamp(++i, new Timestamp(code.getCreateTime().getTime()));
				pst.setString(++i, code.getClientId());
				pst.setString(++i, code.getStatus());
				return pst;
			}
		}, key);
		code.setId(key.getKey().intValue());
	}

	@Override
	public AuthorizationCode getCode(String code) {
		List<AuthorizationCode> result = getJdbcTemplate().query(QUERY_SQL,new Object[]{code}, rowMapper);
		if(result!=null&&result.size()>0){
			return result.get(0);
		}
		return null;
	}

	@Override
	public void delete(int id) {
		String sql = "delete form a1_auth_code where id=?";
		getJdbcTemplate().update(sql, id);
	}

	@Override
	public void udateStatus(int id, String status) {
		String sql = "update a1_auth_code set status=? where id=?";
		getJdbcTemplate().update(sql, status,id);
	}
	
	
	

}
