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
package net.duckling.ddl.service.resource.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.impl.PageLockDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
@Repository
public class PageLockDAOImpl extends AbstractBaseDAO implements PageLockDAO {
	private static final String QUERY_ALL="select * from ddl_page_lock";
	private static final String INSERT="insert ddl_page_lock (rid,tid,uid,last_access,max_version) values(?,?,?,?,?)";
	private static final String UPDATE = "update ddl_page_lock set last_access=? ,max_version=? ,uid=? where rid=?";
	private static final String DELETE = "delete from ddl_page_lock where rid=?";
	private static final String QUERY_BY_RID_TID ="select * from ddl_page_lock where rid=? and tid=?";
	private RowMapper<PageLock> rowMapper = new RowMapper<PageLock>(){

		@Override
		public PageLock mapRow(ResultSet rs, int rowNum) throws SQLException {
			PageLock lock = new PageLock();
			lock.setLastAccess(rs.getTimestamp("last_access"));
			lock.setMaxVersion(rs.getInt("max_version"));
			lock.setRid(rs.getInt("rid"));
			lock.setTid(rs.getInt("tid"));
			lock.setUid(rs.getString("uid"));
			return lock;
		}
		
	};
	@Override
	public List<PageLock> getAllPageLock() {
		return getJdbcTemplate().query(QUERY_ALL, rowMapper);
	}

	@Override
	public void addPageLock(PageLock p) {
		getJdbcTemplate().update(INSERT, new Object[]{p.getRid(),p.getTid(),p.getUid(),p.getLastAccess(),p.getMaxVersion()});
	}

	@Override
	public void updatePageLock(PageLock p) {
		getJdbcTemplate().update(UPDATE, new Object[]{p.getLastAccess(),p.getMaxVersion(),p.getUid(),p.getRid()});
	}

	@Override
	public void cleanPageLock(PageLock pageLock) {
		cleanPageLock(pageLock.getRid());
	}

	@Override
	public PageLock getPageLock(int tid, int rid) {
		List<PageLock> list = getJdbcTemplate().query(QUERY_BY_RID_TID, new Object[]{rid,tid}, rowMapper);
		if(list==null||list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

	@Override
	public void cleanPageLock(int rid) {
		getJdbcTemplate().update(DELETE, rid);
	}

}
