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
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.mobile.MobileVersion;
import net.duckling.ddl.service.mobile.impl.MobileVersionDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class MobileVersionDAOImpl extends AbstractBaseDAO implements MobileVersionDAO {
	private static final String SQL_INSERT = "insert into a1_mobile_version (type,version,creator,create_time,description) values(?,?,?,?,?)";
	private static final String SQL_QUERY_LATEST="select * from a1_mobile_version a where type=? and create_time=(select max(create_time) from a1_mobile_version b where b.type=a.type )";
	@Override
	public void create(final MobileVersion version) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator(){

			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
				int i =1;
				ps.setString(i++, version.getType());
				ps.setString(i++, version.getVersion());
				ps.setString(i++, version.getCreator());
				ps.setTimestamp(i++, new Timestamp(version.getCreateTime().getTime()));
				ps.setString(i++, version.getDescription());
				return ps;
			}}, keyHolder);
		version.setId(keyHolder.getKey().intValue());
	}

	@Override
	public MobileVersion getLatestVersionByType(String type) {
		List<MobileVersion> re = getJdbcTemplate().query(SQL_QUERY_LATEST, new Object[]{type}, rowMapper);
		if(re!=null&&!re.isEmpty()){
			return re.get(0);
		}
		return null;
	}
	
	private RowMapper<MobileVersion> rowMapper = new RowMapper<MobileVersion>(){

		@Override
		public MobileVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
			MobileVersion v = new MobileVersion();
			v.setCreateTime(rs.getTimestamp("create_time"));
			v.setCreator(rs.getString("creator"));
			v.setId(rs.getInt("id"));
			v.setType(rs.getString("type"));
			v.setVersion(rs.getString("version"));
			v.setDescription(rs.getString("description"));
			return v;
		}};

}
