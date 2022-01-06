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

package net.duckling.ddl.service.activity.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.activity.Activity;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.service.activity.dao.ActivityDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class ActivityDaoImpl extends AbstractBaseDAO implements ActivityDao{

    private static final String QUERY = "select id,uid,title,remark,begin_time,end_time,status from ddl_activity where id=?";
    private static final String QUERY_TITLE = "select id,uid,title,remark,begin_time,end_time,status from ddl_activity where title=? order by id desc";

    private RowMapper<Activity> rowMapper = new RowMapper<Activity>() {
            public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
                Activity o = new Activity();
                o.setId(rs.getInt("id"));
                o.setUid(rs.getString("uid"));
                o.setTitle(rs.getString("title"));
                o.setRemark(rs.getString("remark"));
                o.setBeginTime(rs.getTimestamp("begin_time"));
                o.setEndTime(rs.getTimestamp("end_time"));
                o.setStatus(rs.getInt("status"));
                return o;
            }
        };

    public Activity get(int id) {
        List<Activity> results = getJdbcTemplate().query(QUERY, new Object[] {id},rowMapper);
        return (results!=null&&results.size()!=0)?results.get(0):null;
    }

    @Override
    public Activity get(String title) {
        List<Activity> results = getJdbcTemplate().query(QUERY_TITLE, new Object[] {title},rowMapper);
        return (results!=null&&results.size()!=0)?results.get(0):null;
    }
}
