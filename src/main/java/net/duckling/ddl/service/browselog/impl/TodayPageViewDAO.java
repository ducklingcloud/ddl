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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.browselog.BrowseLog;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.stereotype.Repository;

@Repository
public class TodayPageViewDAO extends AbstractBaseDAO {

    private static final String COUNT_HOTS = "select count(*) from a1_browse_log where rid=? and  browse_time >= ?";

    /* Fix ONLY_FULL_GROUP_BY issue
     * kai.nan@yahoo.com <2022-01-15 Sat>
     */
    private static final String QUERY_RESOURCE_VISITOR =
            "SELECT tid, rid, browse_time, user_id, display_name " +
            "FROM ( SELECT * FROM a1_browse_log " +
            "  WHERE rid=? and browse_time>=? ) AS t1 INNER JOIN " +
            "  ( SELECT tracking_id, max(id) last_one " +
            "  FROM a1_browse_log GROUP BY tracking_id ) AS t2 " +
            "  ON  t1.tracking_id = t2.tracking_id and " +
            "  t1.id = t2.last_one " +
            "ORDER BY browse_time DESC LIMIT ?";
    // private static final String QUERY_RESOURCE_VISITOR = "select tid, rid, max(browse_time) browse_time, user_id, display_name "
    //         + " from a1_browse_log where rid=? and browse_time>=? GROUP by tracking_id order by browse_time desc limit ?";

    /* Fix only_full_group_by */
    private static final String GRAB_PAGE_VIEW =
            "SELECT browse_time, tid, rid, item_type, user_id, "+
            "       display_name, t1.tracking_id as tracking_id "+
            "FROM ( "+
            "  SELECT * FROM  a1_browse_log "+
            "  WHERE browse_time>=? and browse_time<=? "+
            ") AS t1 "+
            "INNER JOIN ( "+
            "  SELECT tracking_id, max(id) last_one "+
            "  FROM a1_browse_log "+
            "  GROUP BY tracking_id "+
            ") AS t2 "+
            "ON t1.tracking_id = t2.tracking_id and "+
            "   t1.id = t2.last_one ";
    // private static final String GRAB_PAGE_VIEW = "select max(browse_time) browse_time, tid, rid, item_type, user_id, display_name, tracking_id"
    //         + " from a1_browse_log where browse_time>=? and browse_time<=? group by tracking_id";

    private static final String INSERT_SQL = "insert into a1_browse_log(tid, rid, item_type,user_id, display_name, tracking_id,browse_time) values(?,?,?,?,?,?,?)";

    private BrowseLogRowMapper rowmapper = new BrowseLogRowMapper();

    private String today() {
        Date date = new Date();
        String today = DateUtils.format(date, "yyyy-MM-dd");
        return today;
    }

    public int countHots(int rid) {
        return getJdbcTemplate().queryForObject(COUNT_HOTS,
                                                new Object[] { rid, today() }, Integer.class);
    }

    public List<BrowseLog> getResourceVisitor(int rid, int count) {
        return getJdbcTemplate().query(QUERY_RESOURCE_VISITOR,
                                       new Object[] { rid, today(), count }, rowmapper);
    }

    public List<BrowseLog> getPageViewAt(Date day) {
        String dayStart = DateUtils.format(day, "yyyy-MM-dd");
        String dayEnd = dayStart+" 23:59:59";
        return getJdbcTemplate().query(GRAB_PAGE_VIEW,
                                       new Object[] { dayStart, dayEnd }, rowmapper);
    }

    public void batchSave(final Collection<BrowseLog> browseLogs) {
        getJdbcTemplate().batchUpdate(INSERT_SQL,
                                      new BrowseLogBatchSetter(browseLogs));
    }
}
