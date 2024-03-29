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

package net.duckling.ddl.service.search.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import net.duckling.common.db.DbmsCompat;
import net.duckling.ddl.common.DBs;

import net.duckling.ddl.service.search.WeightPair;
import net.duckling.ddl.service.search.impl.UserAnalysisDAO;
import net.duckling.ddl.service.search.impl.UserInterestRecord;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserAnalysisDAOImpl extends AbstractBaseDAO implements UserAnalysisDAO {

    //将vwb_browse_log和a1_resource通过item(item_id和tid)连接 然后统计
    //每个用户浏览其他用户创建的文档数量 （不包括用户自身，且返回结果
    //按照用户和被访问者排序）
    private static final String QUERY_BROWSE_LOG =
            "SELECT user_id, creator, score FROM ( "+
            "  SELECT user_id, creator, count(creator) AS score FROM ( "+
            "    SELECT user_id, item_id, item_type, tid "+
            "    FROM vwb_browse_log "+
            "    WHERE browse_time BETWEEN ? AND CURRENT_TIMESTAMP "+
            "  ) AS a "+
            "  INNER JOIN a1_resource AS b "+
            "  ON a.item_id = b.rid AND a.tid = b.tid "+
            "  WHERE a.user_id <> b.creator "+
            "  GROUP BY a.user_id, b.creator "+
            ") AS tmp "+
            "WHERE creator NOT LIKE "+
            DbmsCompat.getCONCAT(DBs.getDbms(), "'%'", "user_id", "'%'");

    // private static final String INSERT_USER_INTEREST_NEW =
    //         "INSERT INTO a1_userinterest_temp (uid, interest, score, time) "+
    //         "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
    private static final String INSERT_USER_INTEREST_NEW =
            "INSERT INTO a1_userinterest (uid, interest, score, time) "+
            "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

    // private static final String RENAME_USER_INTEREST_TOTEMP =
    //         DbmsCompat.getRenameTable("a1_userinterest",
    //                                   "a1_userinterest_temp");

    // private static final String CREAT_INTEREST_TABLE = "CREATE TABLE IF NOT EXISTS a1_userinterest (id int(11) NOT NULL auto_increment,uid varchar(255) NOT NULL,interest varchar(255) NOT NULL,score int(11) default '0',time timestamp NOT NULL default '1970-01-02 00:00:00' on update CURRENT_TIMESTAMP,  PRIMARY KEY  (id)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";

    // private static final String DELET_DUPLICATE =
    //         "DELETE FROM a1_userinterest WHERE uid = interest";

    // private static final String DROP_TEMP_TABLE = "DROP TABLE IF EXISTS a1_userinterest_temp";

    // private static final String INSERT_USER_INTEREST="insert into a1_userinterest select NULL,uid,interest,SUM(score),now() from a1_userinterest_temp group by uid,interest";

    private static final String QUERY_USER_INTEREST =
            "SELECT interest FROM a1_userinterest "+
            "WHERE uid = ? ORDER BY score DESC";

    private static final String QUERY_USER_INTEREST_DOC =
            "SELECT rid,weight FROM a1_search_docweight AS a1 "+
            "WHERE keyword=? AND uid IN ( "+
            "  SELECT interest FROM ( "+
            "    SELECT interest FROM a1_userinterest user "+
            "    WHERE user.uid=? ORDER BY score DESC "+
            ( DBs.getDbms().equals("mysql") ?
              " LIMIT 10 " :
              " FETCH FIRST 10 ROWS ONLY ") +
            "  ) AS tmp)";

    // private static final String CREAT_SIM_TABLE = "CREATE TABLE IF NOT EXISTS a1_user_sim (id int(11) NOT NULL auto_increment,uid varchar(255) NOT NULL,simuid varchar(255) NOT NULL,score int(11) default '0',time timestamp NOT NULL default '1970-01-02 00:00:00' on update CURRENT_TIMESTAMP,  PRIMARY KEY  (id)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";

    /* Fix only_full_group_by
     * Remove 'item_type' which seems not-in-use */
    private static final String INSERT_USER_SIM =
            "INSERT INTO a1_user_sim (uid, simuid, score) "+
            "  SELECT uid, simuid, sum(score) "+
            "  FROM ( "+
            "    SELECT a.user_id AS uid, b.user_id AS simuid, "+
            "           CASE WHEN a.count < b.count THEN a.count "+
            "                ELSE b.count END AS score, "+
            "           a.item_id "+
            "    FROM ( "+
            "      SELECT user_id , item_id, count(item_id) as count, tid "+
            "      FROM vwb_browse_log "+
            "      WHERE browse_time BETWEEN ? AND CURRENT_TIMESTAMP "+
            "      GROUP BY user_id, tid, item_id "+
            "    ) AS a "+
            "    INNER JOIN ( "+
            "      SELECT user_id , item_id, count(item_id) as count, tid "+
            "      FROM vwb_browse_log "+
            "      WHERE browse_time between ? AND CURRENT_TIMESTAMP "+
            "      GROUP BY user_id, tid, item_id "+
            "    ) AS b "+
            "    ON a.user_id <> b.user_id AND a.item_id = b.item_id "+
            "       AND a.tid = b.tid "+
            "  ) AS t "+
            "  GROUP BY uid, simuid";

    // private static final String INSERT_USER_SIM = "insert into a1_user_sim select NULL,uid,simuid,sum(score),NULL from(select a.user_id as uid ,b.user_id as simuid,if(a.count<b.count,a.count,b.count) as score,a.item_id, a.item_type from(select user_id , item_id,count(item_id) as count, item_type,tid from vwb_browse_log where TO_DAYS(now())-TO_DAYS(browse_time)<=150  group by user_id,tid,item_id) as a inner join (select user_id , item_id,count(item_id) as count, item_type,tid from vwb_browse_log where TO_DAYS(now())-TO_DAYS(browse_time)<=150 group by user_id,tid,item_id) as b on a.user_id <> b.user_id and a.item_id=b.item_id and a.tid=b.tid) as t group by uid,simuid" ;

    private static final String QUERY_USER_SIM =
            "SELECT simuid FROM a1_user_sim WHERE uid = ? ORDER BY score DESC";

    private static final String CLEAN_SIM = "TRUNCATE TABLE a1_user_sim";

    // private static final String QUERY_USER_SIM_DOC =
    //         "select rid,weight from a1_search_docweight a1 where keyword=? and uid in (select simuid from (select simuid from a1_user_sim user where user.uid=? order by score desc "+
    //         ( DBs.getDbms().equals("mysql") ?
    //           " LIMIT 10 " :
    //           " FETCH FIRST 10 ROWS ONLY ") +
    //         " ) as tmp)";
    private static final String QUERY_USER_SIM_DOC =
            "SELECT rid, weight FROM a1_search_docweight a1 "+
            "WHERE keyword=? AND uid IN ( "+
            "  SELECT simuid FROM a1_user_sim user "+
            "  WHERE user.uid=? ORDER BY score DESC "+
            ( DBs.getDbms().equals("mysql") ?
              " LIMIT 10 " :
              " FETCH FIRST 10 ROWS ONLY ") +
            ")";

    private static RowMapper<UserInterestRecord> usrIntRcrdRowMapper =
            new RowMapper<UserInterestRecord>() {
                @Override
                public UserInterestRecord mapRow(ResultSet rs, int index)
                        throws SQLException {
                    UserInterestRecord userInterestRecord = new UserInterestRecord();
                    userInterestRecord.setUid(rs.getString("user_id"));
                    userInterestRecord.setInterst(rs.getString("creator"));
                    userInterestRecord.setScore(rs.getInt("score"));
                    return userInterestRecord;
                }
            };

    @Override
    public List<UserInterestRecord> getBrowse() {
        LocalDate startDay = LocalDate.now().minusDays(150);
        return this.getJdbcTemplate().query(
            QUERY_BROWSE_LOG,
            new Object[] { Timestamp.valueOf(startDay.atStartOfDay()) },
            usrIntRcrdRowMapper);
    }

    @Override
    public void creatUserInterest(final List<UserInterestRecord> recordList) {
        // 建立临时表 主要是删除uid = interest 用户关注自己的情况
        // <2022-03-07 Mon> TODO: 当前是在新建的空表中删除，疑无效；
        // 通过临时表做了一个sum(core)，尚不知其效果.
        // TODO: transaction here?
        /*
         * 临时简化，随后根据实际数据情况及逻辑再修改 <2022-03-07 Mon>
         */
        /* getJdbcTemplate().update(CREAT_INTEREST_TABLE);//保证有USER_INTEREST可以转换 */
        /* drop_temp_table_anyway()//保证没有临时表 */
        /* getJdbcTemplate().update(RENAME_USER_INTEREST_TOTEMP); */
        /* getJdbcTemplate().update(CREAT_INTEREST_TABLE);//建立新的USER_INTEREST可以转换 */
        BatchPreparedStatementSetter creatUserInterestSetter = new MyBatchStatementSetter(recordList);
        getJdbcTemplate().batchUpdate(INSERT_USER_INTEREST_NEW, creatUserInterestSetter);
        /* getJdbcTemplate().update(DELET_DUPLICATE);      //删除重复项 */
        /* getJdbcTemplate().update(INSERT_USER_INTEREST); */
        /* getJdbcTemplate().update(DROP_TEMP_TABLE);//删除临时表 */
    }

    // private void drop_temp_table_anyway() {
    //     try {
    //         getJdbcTemplate().update(DROP_TEMP_TABLE);
    //     } catch (SQLException e) {
    //         // Ignore not-exist
    //     }
    // }

    private static class MyBatchStatementSetter implements BatchPreparedStatementSetter{
        private List<UserInterestRecord> recordList = null;
        MyBatchStatementSetter(final List<UserInterestRecord> recordList){
            this.recordList = recordList;
        }
        @Override
        public int getBatchSize() {
            return recordList.size();
        }
        @Override
        public void setValues(PreparedStatement ps, int i)
                throws SQLException {
            UserInterestRecord record = recordList.get(i);
            int j = 0;
            ps.setString(++j, record.getUid());
            ps.setString(++j, record.getInterst());
            ps.setInt(++j, record.getScore());
        }
    }

    public List<String> getInterest(String uid){
        try{
            return this.getJdbcTemplate().queryForList(QUERY_USER_INTEREST, String.class,new Object[]{uid});
        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    public List<WeightPair> getInterestDocWeight(String keyword,String uid) {
        try{
            return this.getJdbcTemplate()
                    .query(QUERY_USER_INTEREST_DOC,
                           new Object[]{keyword,uid},
                           docWeightPairRowMapper);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static RowMapper<WeightPair> docWeightPairRowMapper =
            new RowMapper<WeightPair>() {
                @Override
                public WeightPair mapRow(ResultSet rs, int index)
                        throws SQLException {
                    WeightPair weightPair = new WeightPair();
                    weightPair.setId(rs.getLong("rid"));
                    int weight = rs.getInt("weight");
                    weightPair.setWeight(weight>20?20:weight);
                    return weightPair;
                }
            };

    public int creatUserSim() {
        // getJdbcTemplate().update(CREAT_SIM_TABLE);
        getJdbcTemplate().update(CLEAN_SIM);
        LocalDate startDay = LocalDate.now().minusDays(150);
        Timestamp ts = Timestamp.valueOf(startDay.atStartOfDay());
        return this.getJdbcTemplate().update(INSERT_USER_SIM, ts, ts);
    }

    public List<String> getSim (String uid) {
        try {
            return this.getJdbcTemplate()
                    .queryForList(QUERY_USER_SIM,
                                  String.class,
                                  new Object[]{uid});
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<WeightPair> getSimDocWeight(String keyword, String uid) {
        try {
            return this.getJdbcTemplate()
                    .query(QUERY_USER_SIM_DOC,
                           new Object[]{keyword, uid},
                           docWeightPairRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
