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
package net.duckling.ddl.service.invitation.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ClientMessageDAO extends AbstractBaseDAO {

	public void saveMessage(final List<String> invitors, final String message) {
		String sql = "insert into vwb_client_message(username, message, readed, createtime) values(?,?,?,?)";
		final Timestamp now = new Timestamp(new Date().getTime());
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				ps.setString(1, invitors.get(i));
				ps.setString(2, message);
				ps.setInt(3, 0);
				ps.setTimestamp(4, now);
			}

			@Override
			public int getBatchSize() {
				return invitors.size();
			}

		});
	}

	public List<String> readMessage(String username) {
		String sql = "select message from vwb_client_message where username=? and readed=0";
		return getJdbcTemplate().query(sql, new Object[]{username}, new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
			
		});
	}

	public void markMessageReaded(String username) {
		String sql = "update vwb_client_message set readed=1, readtime=now() where username=? and readed=0";
		getJdbcTemplate().update(sql, new Object[]{username});
	}

}
