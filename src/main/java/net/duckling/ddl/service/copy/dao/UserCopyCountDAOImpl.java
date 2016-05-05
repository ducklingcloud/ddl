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
package net.duckling.ddl.service.copy.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.copy.UserCopyCount;
import net.duckling.ddl.service.copy.impl.UserCopyCountDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
@Repository
public class UserCopyCountDAOImpl extends AbstractBaseDAO implements UserCopyCountDAO{
	private static final String query = "select * from a1_user_copy_count where uid=? and copy_date=?";
	private static final String insert = "insert into a1_user_copy_count (uid,copy_date,count) values(?,?,?)";
	private static final String update = "update a1_user_copy_count set count=? where uid=? and copy_date=?";
	private RowMapper<UserCopyCount> rowMapper = new RowMapper<UserCopyCount>() {

		@Override
		public UserCopyCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserCopyCount c = new UserCopyCount();
			c.setCount(rs.getInt("count"));
			c.setUid(rs.getString("uid"));
			c.setDate(rs.getString("copy_date"));
			return c;
		}
		
	};
	/* (non-Javadoc)
	 * @see net.duckling.ddl.service.copy.dao.UserCopyCount#getUserCount(java.lang.String, java.lang.String)
	 */
	@Override
	public UserCopyCount getUserCount(String uid,String date){
		List<UserCopyCount> cs = getJdbcTemplate().query(query, new Object[]{uid,date}, rowMapper);
		if(cs==null||cs.isEmpty()){
			return null;
		}else{
			return cs.get(0);
		}
	}
	/* (non-Javadoc)
	 * @see net.duckling.ddl.service.copy.dao.UserCopyCount#insert(net.duckling.ddl.service.copy.UserCopyCount)
	 */
	@Override
	public void insert(UserCopyCount c){
		getJdbcTemplate().update(insert, new Object[]{c.getUid(),c.getDate(),c.getCount()});
	}
	
	/* (non-Javadoc)
	 * @see net.duckling.ddl.service.copy.dao.UserCopyCount#update(net.duckling.ddl.service.copy.UserCopyCount)
	 */
	@Override
	public void update(UserCopyCount c){
		getJdbcTemplate().update(update,new Object[]{c.getCount(),c.getUid(),c.getDate()});
	}
}
