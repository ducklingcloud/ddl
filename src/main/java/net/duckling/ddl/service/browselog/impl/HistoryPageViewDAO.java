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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.browselog.BrowseLog;
import net.duckling.ddl.service.browselog.BrowseStat;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class HistoryPageViewDAO extends AbstractBaseDAO {
	private BrowseLogRowMapper rowmapper = new BrowseLogRowMapper();
	private static final String INSERT = "insert into a1_page_view(tid, rid, item_type, user_id, display_name,tracking_id, browse_time) values(?,?,?,?,?,?,?)";
	private static final String QUERY = "select * from a1_page_view where rid=? order by browse_time desc limit ?";
	private static final String COUNT_HOTS = "select count(*) from a1_page_view where rid=?";
	private static final String TOP_RESOURCE="select count(p.rid) total, p.rid, p.item_type, title" +
			" from a1_page_view p, a1_resource r" +
			" where r.id=p.rid and p.tid=? and p.browse_time>? group by p.rid order by total desc limit ?";

	public void savePageView(final List<BrowseLog> list) {
		getJdbcTemplate().batchUpdate(INSERT, new BrowseLogBatchSetter(list));
	}

	public List<BrowseLog> getResourceVisitor(int rid, int count) {
		return getJdbcTemplate().query(QUERY, new Object[] { rid, count },
				rowmapper);
	}

	public int countHots(int rid) {
		return getJdbcTemplate().queryForObject(COUNT_HOTS, new Object[]{rid},
				Integer.class);
	}

	/**
	 * 查询热度榜
	 * @param tid		团队ID
	 * @param length	热度榜的长度
	 * @param timePoint 时间期限
	 * @return	热度榜
	 */
	public List<BrowseStat> getTopPageView(int tid, int length, Date timePoint) {
		return getJdbcTemplate().query(TOP_RESOURCE, new Object[] { tid, tid, new Timestamp(timePoint.getTime()),
						tid, length }, new RowMapper<BrowseStat>() {
					public BrowseStat mapRow(ResultSet rs, int index)
							throws SQLException {
						BrowseStat stat = new BrowseStat();
						stat.setCount(rs.getInt("total"));
						stat.setRid(rs.getInt("rid"));
						stat.setTitle(rs.getString("title"));
						stat.setType(rs.getString("item_type"));
						return stat;
					}

				});
	}
}