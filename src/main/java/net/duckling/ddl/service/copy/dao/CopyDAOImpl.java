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
/**
 * 
 */
package net.duckling.ddl.service.copy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.duckling.ddl.service.copy.CopyLog;
import net.duckling.ddl.service.copy.impl.CopyDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtils;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 * Copy功能JDBC实现类，hibernate实现类无限期延长
 * @author lvly
 * @since 2012-11-13
 */
@Repository
public class CopyDAOImpl extends AbstractBaseDAO implements CopyDAO {
	static final String SQL_SELECT="select * from a1_copy_log where 1=1 ";
	static final String SQL_COUNT_SELECT="select count(*) from a1_copy_log where 1=1";
	static final String SQL_CREATE="insert into a1_copy_log(from_rid,from_tid,from_version,to_rid,to_tid,to_version,uid,copy_time,type) "+
									"values(?,?,?,?,?,?,?,?,?)";
	static final String BY_TO_RID=" and to_rid=?";
	static final String BY_TO_VERSION=" and to_version=?";
	static final String BY_UPDATE=" and type='"+CopyLog.TYPE_UPDATE+"' ";
	static final String BY_CREATE=" and type='"+CopyLog.TYPE_CREATE+"' ";
	static final String ORDER_BY_COPY_TIME=" order by copy_time desc";

	@Override
	public boolean isCopyed(int rid) {
		int logCount=getJdbcTemplate().queryForObject(SQL_COUNT_SELECT+BY_TO_RID, new Object[]{rid}, Integer.class);
		return logCount>0;
	}
	private RowMapper<CopyLog> rowMapper = new RowMapper<CopyLog>() {
		public CopyLog mapRow(ResultSet rs, int index) throws SQLException {
			CopyLog log = new CopyLog();
			log.setId(rs.getInt("id"));
			log.setCopyTime(rs.getDate("copy_time"));
			log.setFromRid(rs.getInt("from_rid"));
			log.setFromTid(rs.getInt("from_tid"));
			log.setFromVersion(rs.getInt("from_version"));
			log.setToRid(rs.getInt("to_rid"));
			log.setToTid(rs.getInt("to_tid"));
			log.setToVersion(rs.getInt("to_version"));
			log.setUid(rs.getString("uid"));
			log.setType(rs.getString("type"));
			return log;
		}
	};

	@Override
	public boolean isDoUpdate(int fromRid, int toTid) {
		// A->B,A->B 或者A->B,B-A时更新，如若复制成一个圈，那不管，直接创建
		String sql=SQL_SELECT+" and ((from_rid=? and to_tid=?) or (to_rid=? and from_tid=?)) "+ORDER_BY_COPY_TIME;
		return CommonUtils.first(getJdbcTemplate().query(sql, new Object[]{fromRid,toTid,fromRid,toTid},rowMapper))!=null;
	}
	@Override
	public int addCopyLog(final CopyLog copy) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update((new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement pst = null;
				pst = conn.prepareStatement(SQL_CREATE,
						PreparedStatement.RETURN_GENERATED_KEYS);

				int i = 0;
				// from_rid,from_tid,from_version,to_rid,
				//to_tid,to_version,uid,copy_time,ancestry_rid
				pst.setInt(++i, copy.getFromRid());
				pst.setInt(++i, copy.getFromTid());
				pst.setInt(++i, copy.getFromVersion());
				pst.setInt(++i, copy.getToRid());
				pst.setInt(++i, copy.getToTid());
				pst.setInt(++i, copy.getToVersion());
				pst.setString(++i, copy.getUid());
				pst.setTimestamp(++i, new Timestamp(copy.getCopyTime().getTime()));
				pst.setString(++i, copy.getType());
				return pst;
			}
		}), keyHolder);
		return keyHolder.getKey().intValue();
	}
	@Override
	public CopyLog getCopyLogByTo(int toRid, int toVersion) {
		String sql=SQL_SELECT+BY_TO_RID+BY_TO_VERSION;
		return CommonUtils.first(getJdbcTemplate().query(sql,new Object[]{toRid,toVersion},rowMapper));
	}
	
	@Override
	public int getToRid(int fromRid, int toTid) {
		// A->B,A->B时更新
		String sql = SQL_SELECT + " and from_rid=? and to_tid=? "+BY_CREATE + ORDER_BY_COPY_TIME;
		CopyLog log = CommonUtils.first(getJdbcTemplate().query(sql, new Object[] { fromRid, toTid }, rowMapper));
		// 或者A->B,B-A时更新
		if (log == null) {
			sql = SQL_SELECT + " and to_rid=? and from_tid=? " +BY_CREATE+ ORDER_BY_COPY_TIME;
			log = CommonUtils.first(getJdbcTemplate().query(sql, new Object[] { fromRid, toTid }, rowMapper));
			return log.getFromRid();
		} else {
			return log.getToRid();
		}

	}

}
