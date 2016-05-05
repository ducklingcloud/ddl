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
package net.duckling.ddl.service.mobile.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.mobile.IphoneDeviceToken;
import net.duckling.ddl.service.mobile.impl.DeviceTokenDao;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceTokenDaoImpl extends AbstractBaseDAO implements DeviceTokenDao {
	
	protected static final Logger log = Logger.getLogger(DeviceTokenDaoImpl.class);

	private RowMapper<IphoneDeviceToken> rowMapper = new RowMapper<IphoneDeviceToken>() {
		public IphoneDeviceToken mapRow(ResultSet rs, int index) throws SQLException {
			IphoneDeviceToken deviceToken = new IphoneDeviceToken();
			deviceToken.setId(rs.getInt("id"));
			deviceToken.setDeviceToken(rs.getString("device_token"));
			deviceToken.setUid(rs.getString("uid"));
			deviceToken.setLastLoginTime(new Date(rs.getTimestamp("last_login_time").getTime()));
			return deviceToken;
		}
	};

	public List<IphoneDeviceToken> getAllDeviceToken() {
		String sql = "select * from a1_device_token";
		return  getJdbcTemplate().query(sql,new Object[]{}, rowMapper);
	}

	public int insertDeviceToken(final IphoneDeviceToken deviceToken) {
		final String sql = "insert into a1_device_token (last_login_time,uid,device_token) values(?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				int i = 0;
				PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				ps.setTimestamp(++i, new Timestamp(deviceToken.getLastLoginTime().getTime()));
				ps.setString(++i, deviceToken.getUid());
				ps.setString(++i, deviceToken.getDeviceToken());
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	public IphoneDeviceToken getIphoneDeviceToken(String deviceToken) {
		String sql = "select * from a1_device_token where device_token=?";
		try {  
			return getJdbcTemplate().queryForObject(sql,new Object[] { deviceToken }, rowMapper);
		} catch(DataAccessException accessException) {
			if(log.isDebugEnabled()){
				log.debug("no device token with device id " + deviceToken + " is found!");
			}
			return null;
		}
	}
	
	public IphoneDeviceToken getIphoneDeviceTokenByUid(String uid) {
		String sql = "select * from a1_device_token where uid=?";
		try {  
			return getJdbcTemplate().queryForObject(sql,new Object[] { uid }, rowMapper);
		} catch(DataAccessException accessException) {
			if(log.isDebugEnabled()){
				log.debug("no device token with uid " + uid + " is found!");
			}
			return null;
		}
	}
	
	public int updateDeviceToken(IphoneDeviceToken deviceToken) {
		String sql = "update a1_device_token set last_login_time=?, uid=? where device_token=?";
		return getJdbcTemplate().update(sql, new Object[]{new Timestamp(deviceToken.getLastLoginTime().getTime()), 
				deviceToken.getUid(), deviceToken.getDeviceToken()});
	}

}
