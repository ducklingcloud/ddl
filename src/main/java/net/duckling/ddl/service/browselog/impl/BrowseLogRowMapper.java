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
package net.duckling.ddl.service.browselog.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.duckling.ddl.service.browselog.BrowseLog;

import org.springframework.jdbc.core.RowMapper;

public class BrowseLogRowMapper implements RowMapper<BrowseLog> {
	public BrowseLog mapRow(ResultSet rs, int index) throws SQLException {
		BrowseLog log = new BrowseLog();
		log.setTid(rs.getInt("tid"));
		log.setBrowseTime(rs.getTimestamp("browse_time"));
		log.setRid(rs.getInt("rid"));
		log.setDisplayName(rs.getString("display_name"));
		log.setUserId(rs.getString("user_id"));
		return log;
	}
}
