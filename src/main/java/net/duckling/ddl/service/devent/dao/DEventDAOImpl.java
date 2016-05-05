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

package net.duckling.ddl.service.devent.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.DEventBody;
import net.duckling.ddl.service.devent.impl.DEventDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-11-2
 * @author clive
 */
@Repository
public class DEventDAOImpl extends AbstractBaseDAO implements DEventDAO  {
	
	private static final String SAVE = "insert into vwb_event(tid,actor,operation,target,target_type,target_version,recipients,message,occur_time) values(?,?,?,?,?,?,?,?,?)";

	private static final String QUERY = "select * from vwb_event where id=?";

	private RowMapper<DEventBody> mapper = new RowMapper<DEventBody>() {
		public DEventBody mapRow(ResultSet rs, int index) throws SQLException {
			DEventBody b = new DEventBody();
			b.setActor(rs.getString("actor"));
			b.setId(rs.getInt("id"));
			b.setTid(rs.getInt("tid"));
			b.setOperation(rs.getString("operation"));
			b.setMessage(rs.getString("message"));
			b.setOccurTime(rs.getTime("occur_time"));
			b.setTarget(new DEntity(rs.getString("target_id"),rs.getString("target_type")));
			b.setTargetVersion(rs.getInt("target_version"));
			b.setRecipients(rs.getString("recpients"));
			return b;
		}
	};

	public int saveEvent(final DEventBody e) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement pst = conn.prepareStatement(SAVE,PreparedStatement.RETURN_GENERATED_KEYS);
				int i = 0;
				pst.setInt(++i, e.getTid());
				pst.setString(++i, e.getActor());
				pst.setString(++i, e.getOperation());
				pst.setString(++i, e.getTarget().getId());
				pst.setString(++i, e.getTarget().getType());
				pst.setInt(++i, e.getTargetVersion());
				pst.setString(++i, e.getRecipients());
				pst.setString(++i, e.getMessage());
				pst.setTimestamp(++i, new Timestamp(e.getOccurTime().getTime()));
				return pst;
			}
		}, keyHolder);
		Number key = keyHolder.getKey();
		return (key != null) ? key.intValue() : -1;
	}
	
	public DEventBody queryEventByID(int id){
		List<DEventBody> result = getJdbcTemplate().query(QUERY, new Object[]{id},mapper);
		return (result!=null && result.size()!=0)?result.get(0):null;
	}

}
