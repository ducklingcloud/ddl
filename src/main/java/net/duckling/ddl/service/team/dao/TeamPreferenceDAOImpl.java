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
package net.duckling.ddl.service.team.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import net.duckling.common.db.DbmsCompat;
import net.duckling.ddl.common.DBs;

import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.UserNoticeCount;
import net.duckling.ddl.service.team.impl.TeamPreferenceDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.CommonUtils;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

public class TeamPreferenceDAOImpl extends AbstractBaseDAO
        implements TeamPreferenceDAO {
    /*
     * To have CONCAT() compatible with different DBMS, these will be
     * set in the constructor.
     */
    private final String INCREASE_MONITOR_COUNT,
        INCREASE_PERSON_COUNT,
        INCREASE_TEAM_COUNT;
    
    private static final String UPDATE_MONITOR_ACCESS = "update vwb_team_member set monitor_access=?,monitor_notice_count=0,monitor_event_ids='' where uid=? and tid=?";
    private static final String UPDATE_PERSON_ACCESS = "update vwb_team_member set person_access=?,person_notice_count=0,person_event_ids='' where uid=? and tid=?";
    private static final String UPDATE_TEAM_ACCESS = "update vwb_team_member set team_access=?,team_notice_count=0,team_event_ids='' where uid=? and tid=? ";

    // add by lvly@2012-11-2
    private static final String ORDER_BY_SEQUENCE = " order by sequence,id ";
    private static final String QUERY_PREFERENCES = "select * from vwb_team_member where uid=? and tid<>? ";
    private static final String QUERY_USER_NOTICE_COUNT = "select uid,sum(team_notice_count)as team_notice_count,sum(person_notice_count)as person_notice_count,sum(monitor_notice_count) as monitor_notice_count from vwb_team_member where uid=? ";
    private static final String UPDATE_TEAM_PREFERENCE = "update vwb_team_member set tid=?,uid=?,sequence=?,team_access=?,person_access=?,monitor_access=?,team_notice_count=?,person_notice_count=?,monitor_notice_count=?,"
            + "team_event_ids=?,person_event_ids=?,monitor_event_ids=? where id=?";

    public TeamPreferenceDAOImpl() {
        String dbms = DBs.getDbms();
        
        INCREASE_MONITOR_COUNT =
                "UPDATE vwb_team_member "+
                "SET monitor_notice_count = monitor_notice_count + 1, "+
                "    monitor_event_ids = "+
                DbmsCompat.getCONCAT(dbms, "monitor_event_ids", "?") +
                "WHERE uid=? AND tid=?";

        INCREASE_PERSON_COUNT =
                "UPDATE vwb_team_member "+
                "SET person_notice_count = person_notice_count + 1, "+
                "    person_event_ids = "+
                DbmsCompat.getCONCAT(dbms, "person_event_ids", "?") +
                "WHERE uid=? AND tid=?";
        
        INCREASE_TEAM_COUNT =
                "UPDATE vwb_team_member "+
                "SET team_notice_count = team_notice_count + 1, "+
                "    team_event_ids = "+
                DbmsCompat.getCONCAT(dbms, "team_event_ids", "?") +
                "WHERE uid=? AND tid=?";
    }

    private String chooseSQLByType(String type) {
        if (NoticeRule.TEAM_NOTICE.equals(type))
            return INCREASE_TEAM_COUNT;
        if (NoticeRule.PERSON_NOTICE.equals(type))
            return INCREASE_PERSON_COUNT;
        return INCREASE_MONITOR_COUNT;
    }

    private RowMapper<TeamPreferences> prefMapper = new RowMapper<TeamPreferences>() {
            public TeamPreferences mapRow(ResultSet rs, int index)
                    throws SQLException {
                TeamPreferences n = new TeamPreferences();
                n.setId(rs.getInt("id"));
                n.setTid(rs.getInt("tid"));
                n.setUid(rs.getString("uid"));
                n.setSequence(rs.getInt("sequence"));
                n.setTeamAccess(rs.getTimestamp("team_access"));
                n.setPersonAccess(rs.getTimestamp("person_access"));
                n.setMonitorAccess(rs.getTimestamp("monitor_access"));
                n.setPersonNoticeCount(rs.getInt("person_notice_count"));
                n.setTeamNoticeCount(rs.getInt("team_notice_count"));
                n.setMonitorNoticeCount(rs.getInt("monitor_notice_count"));
                n.setMonitorEventIds(rs.getString("Monitor_event_ids"));
                n.setTeamEventIds(rs.getString("team_event_ids"));
                n.setPersonEventIds(rs.getString("person_event_ids"));
                return n;
            }
        };

    private RowMapper<UserNoticeCount> userNoticeCountMapper = new RowMapper<UserNoticeCount>() {

            @Override
            public UserNoticeCount mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                UserNoticeCount count = new UserNoticeCount();
                count.setUid(rs.getString("uid"));
                count.setTeamNoticeCount(rs.getInt("team_notice_count"));
                count.setPersonNoticeCount(rs.getInt("person_notice_count"));
                count.setMonitorNoticeCount(rs.getInt("monitor_notice_count"));
                return count;
            }

        };

    private String getWhereSQL(Set<Integer> tids) {
        if (CommonUtils.isNull(tids)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int tid : tids) {
            sb.append(tid).append(",");
        }
        return "and tid in(" + CommonUtils.format(sb.toString()) + ")";
    }

    @Override
    public TeamPreferences getTeamPreferences(String uid, int tid) {
        String sql = "select * from vwb_team_member where uid=? and tid=?";
        List<TeamPreferences> prefList = this.getJdbcTemplate().query(sql,
                                                                      new Object[] { uid, tid }, prefMapper);
        return (prefList != null && prefList.size() != 0) ? prefList.get(0)
                : null;
    }

    @Override
    public List<TeamPreferences> getAllTeamPreferencesByTids(Set<Integer> tids) {
        String sql = "select * from vwb_team_member where 1=1 "
                + getWhereSQL(tids);
        return this.getJdbcTemplate().query(sql, prefMapper);
    }

    @Override
    public List<TeamPreferences> getTeamPreferencesByUID(String uid,
                                                         int filterTeam) {
        return this.getJdbcTemplate().query(
            QUERY_PREFERENCES + ORDER_BY_SEQUENCE,
            new Object[] { uid, filterTeam }, prefMapper);
    }

    // get uid according tid
    @Override
    public List<TeamPreferences> getUidByTid(int tid) {
        String sql = "select * from vwb_team_member a where a.tid=?";
        return getJdbcTemplate().query(sql, new Object[] { tid }, prefMapper);
    }

    private void validateEvent(String uid, int tid) {
        String sql = "select id,tid,uid,team_notice_count,person_notice_count,monitor_notice_count from vwb_team_member where tid=? and uid=?";
        List<UserNoticeCount> un = getJdbcTemplate().query(sql,
                                                           new Object[] { tid, uid }, userNoticeCountMapper);
        if (un != null && !un.isEmpty()) {
            UserNoticeCount u = un.get(0);
            if (u.getTeamNoticeCount() > 500) {
                TeamPreferences t = getTeamPreferences(uid, tid);
                t.removeNocice(u.getTeamNoticeCount() - 499,
                               NoticeRule.TEAM_NOTICE);
                updatePreferene(t);
            }
            if (u.getMonitorNoticeCount() > 500) {
                TeamPreferences t = getTeamPreferences(uid, tid);
                t.removeNocice(u.getMonitorNoticeCount() - 499,
                               NoticeRule.MONITOR_NOTICE);
                updatePreferene(t);
            }
            if (u.getPersonNoticeCount() > 500) {
                TeamPreferences t = getTeamPreferences(uid, tid);
                t.removeNocice(u.getPersonNoticeCount() - 499,
                               NoticeRule.PERSON_NOTICE);
                updatePreferene(t);
            }
        }
    }

    @Override
    public UserNoticeCount getUserNoticeCount(String uid) {
        List<UserNoticeCount> c = getJdbcTemplate().query(
            QUERY_USER_NOTICE_COUNT, new Object[] { uid },
            userNoticeCountMapper);
        if (c != null && !c.isEmpty()) {
            return c.get(0);
        }
        return null;
    }

    private Timestamp getTimestamp(Date date) {
        if (date == null) {
            date = new Date();
        }
        return new Timestamp(date.getTime());
    }

    @Override
    public void batchIncreaseNoticeCount(final String[] uids, final int tid,
                                         final String type, final int eventId) {
        String sql = chooseSQLByType(type);
        for (String uid : uids) {
            synchronized (uid + tid) {
                validateEvent(uid, tid);
            }
        }
        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
                public int getBatchSize() {
                    return uids.length;
                }

                public void setValues(PreparedStatement pst, int index)
                        throws SQLException {
                    int i = 0;
                    pst.setString(++i, ";" + eventId);
                    pst.setString(++i, uids[index]);
                    pst.setInt(++i, tid);
                }
            });
    }

    @Override
    public boolean updatePreferene(final TeamPreferences pre) {
        return getJdbcTemplate().update(UPDATE_TEAM_PREFERENCE,
                                        new PreparedStatementSetter() {
                                            @Override
                                            public void setValues(PreparedStatement ps)
                                                    throws SQLException {
                                                int i = 0;
                                                ps.setString(++i, pre.getTid() + "");
                                                ps.setString(++i, pre.getUid());
                                                ps.setInt(++i, pre.getSequence());
                                                ps.setTimestamp(++i, getTimestamp(pre.getTeamAccess()));
                                                ps.setTimestamp(++i,
                                                                getTimestamp(pre.getPersonAccess()));
                                                ps.setTimestamp(++i,
                                                                getTimestamp(pre.getMonitorAccess()));
                                                ps.setInt(++i, pre.getTeamNoticeCount());
                                                ps.setInt(++i, pre.getPersonNoticeCount());
                                                ps.setInt(++i, pre.getMonitorNoticeCount());
                                                ps.setString(++i, pre.getTeamEventIds());
                                                ps.setString(++i, pre.getPersonEventIds());
                                                ps.setString(++i, pre.getMonitorEventIds());
                                                ps.setInt(++i, pre.getId());

                                            }
                                        }) > 0 ? true : false;
    }

    @Override
    public void updateNoticeAccessTime(String uid, int tid, String type) {
        String sql = null;
        if (NoticeRule.TEAM_NOTICE.equals(type)) {
            sql = UPDATE_TEAM_ACCESS;
        } else if (NoticeRule.PERSON_NOTICE.equals(type)) {
            sql = UPDATE_PERSON_ACCESS;
        } else {
            sql = UPDATE_MONITOR_ACCESS;
        }
        getJdbcTemplate().update(sql,
                                 new Object[] { new Timestamp(new Date().getTime()), uid, tid });
    }

    @Override
    public void updateAllMessageCount(String uid, String type) {
        String sql = "update vwb_team_member set " + type + "_access=?," + type
                + "_notice_count=0," + type + "_event_ids='' where uid=?";
        this.getJdbcTemplate().update(sql,
                                      new Object[] { new Timestamp(new Date().getTime()), uid });
    }

}
