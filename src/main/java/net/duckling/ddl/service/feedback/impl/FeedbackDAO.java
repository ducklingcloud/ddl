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

package net.duckling.ddl.service.feedback.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.ddl.service.feedback.Feedback;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


/**
 * @date 2011-12-8
 * @author clive
 */
@Repository
public class FeedbackDAO extends AbstractBaseDAO{
	
	private RowMapper<Feedback> rowMapper = new RowMapper<Feedback>() {
		public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {
			Feedback f = new Feedback();
			f.setId(rs.getInt("id"));
			f.setEmail(rs.getString("email"));
			f.setMessage(rs.getString("message"));
			f.setSendTime(rs.getTimestamp("send_time"));
			f.setStatus(rs.getString("status"));
			return f;
		}
	};
	
	private static final String GET_ALL = "select * from vwb_user_feedback";
	private static final String GET_TYPE_FEEDBACKS = "select * from vwb_user_feedback where status=?";
	private static final String INSERT_FEEDBACK = "insert into vwb_user_feedback(email,message,send_time,status) values(?,?,?,?)";
	private static final String UPDATE_FEEDBACKS = "update vwb_user_feedback set status=? where id=?";
	
	public void inset(Feedback f){
		getJdbcTemplate().update(INSERT_FEEDBACK,new Object[]{f.getEmail(),f.getMessage(),new Timestamp(f.getSendTime().getTime()),f.getStatus()});
	}
	
	public List<Feedback> getAll(){
		return getJdbcTemplate().query(GET_ALL, rowMapper);
	}
	
	public List<Feedback> getFeedbackByType(String type){
		return getJdbcTemplate().query(GET_TYPE_FEEDBACKS, new Object[]{type},rowMapper);
	}
	
	public void updateFeedbacks(final Feedback[] array) {
		getJdbcTemplate().batchUpdate(UPDATE_FEEDBACKS,
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return array.length;
					}

					public void setValues(PreparedStatement pst, int index)
							throws SQLException {
						int i = 0;
						pst.setString(++i, array[index].getStatus());
						pst.setInt(++i, array[index].getId());
					}

				});
	}

}
