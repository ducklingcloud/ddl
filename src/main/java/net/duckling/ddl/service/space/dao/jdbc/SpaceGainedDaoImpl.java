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
package net.duckling.ddl.service.space.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import net.duckling.ddl.service.space.SpaceGained;
import net.duckling.ddl.service.space.dao.SpaceGainedDao;
import net.duckling.ddl.util.AbstractBaseDAO;
@Repository
public class SpaceGainedDaoImpl extends AbstractBaseDAO implements SpaceGainedDao{
	private static final String INSERT = "insert into ddl_space_gained(uid,obj_id,obj_type,space_type,size,remark,create_time) values(?,?,?,?,?,?,?)";
	private static final String SELECT = "select id,uid,obj_id,obj_type,space_type,size,remark,create_time from ddl_space_gained where uid=?";
	
	@Override
	public List<SpaceGained> getList(String uid,Integer objId, String remark) {
		return getList(uid, objId, remark, null);
	}
	
	@Override
	public List<SpaceGained> getList(String uid, Integer objId, String remark, Integer spaceType) {
		String sql = SELECT;
		List<Object> params = new ArrayList<Object>();
		params.add(uid);
		if(objId!=null){
			sql+=" and obj_id=?";
			params.add(objId);
		}
		if(remark!=null){
			sql+=" and remark=?";
			params.add(remark);
		}
		if(spaceType!=null){
			sql+=" and space_type=?";
			params.add(spaceType);
		}
		List<SpaceGained> results = getJdbcTemplate().query(sql, params.toArray(),rowMapper);
		return results;
	}
	
	@Override
	public int insert(final SpaceGained o) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement pst = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
				pst.setString(1, o.getUid());
				pst.setInt(2, o.getObjId());
				pst.setInt(3, o.getObjType());
				pst.setInt(4, o.getSpaceType());
				pst.setLong(5, o.getSize());
				pst.setString(6, o.getRemark());
				pst.setTimestamp(7, new Timestamp(o.getCreateTime().getTime()));
				return pst;
			}
		}, keyHolder);
		return keyHolder.getKey().intValue();
	}

	private RowMapper<SpaceGained> rowMapper = new RowMapper<SpaceGained>() {
		public SpaceGained mapRow(ResultSet rs, int rowNum) throws SQLException {
			SpaceGained o = new SpaceGained();
			o.setId(rs.getInt("id"));
			o.setUid(rs.getString("uid"));
			o.setObjId(rs.getInt("obj_id"));
			o.setObjType(rs.getInt("obj_type"));
			o.setSpaceType(rs.getInt("space_type"));
			o.setSize(rs.getLong("size"));
			o.setRemark(rs.getString("remark"));
			o.setCreateTime(rs.getTimestamp("create_time"));
			return o;
		}
	};
}
