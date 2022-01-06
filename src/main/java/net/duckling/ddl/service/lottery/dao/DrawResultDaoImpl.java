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
package net.duckling.ddl.service.lottery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import net.duckling.common.repository.BaseDao;
import net.duckling.ddl.service.lottery.model.DrawResult;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class DrawResultDaoImpl extends BaseDao implements IDrawResultDao {

    private static final String SAVE_DRAE_RESULT = "insert into ddl_lottery(user, date, drawed_time, gift_level, gift_name, lottery_name) values (?,?,?,?,?,?);";

    private RowMapper<DrawResult> rowMapper = new RowMapper<DrawResult>() {
            public DrawResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                DrawResult o = new DrawResult();
                o.setId(rs.getInt("id"));
                o.setUser(rs.getString("user"));
                o.setDate(rs.getString("date"));
                o.setDrawedTime(rs.getTimestamp("drawed_time"));
                o.setGiftLevel(rs.getInt("gift_level"));
                o.setGiftName(rs.getString("gift_name"));
                o.setLotteryName(rs.getString("lottery_name"));
                return o;
            }
        };

    @Override
    public int save(final DrawResult dr) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update((new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = null;
                    pst = conn.prepareStatement(SAVE_DRAE_RESULT, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, dr.getUser());
                    pst.setString(++i, dr.getDate());
                    pst.setTimestamp(++i, new Timestamp(dr.getDrawedTime().getTime()));
                    pst.setInt(++i, dr.getGiftLevel());
                    pst.setString(++i, dr.getGiftName());
                    pst.setString(++i, dr.getLotteryName());
                    return pst;
                }
            }), keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<DrawResult> queryByUser(String user) {
        String sql = "select * from ddl_lottery where user=?";
        return getJdbcTemplate().query(sql, new Object[] { user }, rowMapper);
    }

    @Override
    public DrawResult query(String date, String user) {
        String sql = "select * from ddl_lottery where user=? and date=?";
        List<DrawResult> list = getJdbcTemplate().query(sql, new Object[] { user, date }, rowMapper);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public List<DrawResult> getDailyDrawResult(String lotteryName, int minGiftLevel) {
        String sql = "select * from ddl_lottery where lottery_name=? and gift_level <= ? and gift_level <> 0 order by id desc limit 50";
        List<DrawResult> list = getJdbcTemplate().query(sql, new Object[] { lotteryName, minGiftLevel }, rowMapper);
        return list;
    }

    @Override
    public List<DrawResult> getTodayDrawResult(String date) {
        String sql = "select * from ddl_lottery where date=? ";
        List<DrawResult> list = getJdbcTemplate().query(sql, new Object[] { date }, rowMapper);
        return list;
    }

    @Override
    public List<DrawResult> queryAllOfDelivery() {
        int top = 6;
        String sql = "select * from ddl_lottery where gift_level>0 && gift_level<?";
        return getJdbcTemplate().query(sql, new Object[] { top }, rowMapper);
    }

}
