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
import java.util.List;

import net.duckling.common.repository.BaseDao;
import net.duckling.ddl.service.lottery.model.Delivery;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class DeliveryDaoImpl extends BaseDao implements IDeliveryDao {

    private static final String SAVE_DELIVERY = "insert into ddl_lottery_delivery(user, user_address, real_name, phone_number, gift_content) values (?,?,?,?,?);";

    private RowMapper<Delivery> rowMapper = new RowMapper<Delivery>() {
            public Delivery mapRow(ResultSet rs, int rowNum) throws SQLException {
                Delivery o = new Delivery();
                o.setId(rs.getInt("id"));
                o.setUser(rs.getString("user"));
                o.setGiftContent(rs.getString("gift_content"));
                o.setPhoneNumber(rs.getString("phone_number"));
                o.setRealName(rs.getString("real_name"));
                o.setUserAddress(rs.getString("user_address"));
                return o;
            }
        };

    @Override
    public int save(final Delivery dv) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update((new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement pst = null;
                    pst = conn.prepareStatement(SAVE_DELIVERY, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setString(++i, dv.getUser());
                    pst.setString(++i, dv.getUserAddress());
                    pst.setString(++i, dv.getRealName());
                    pst.setString(++i, dv.getPhoneNumber());
                    pst.setString(++i, dv.getGiftContent());
                    return pst;
                }
            }), keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public Delivery query(String user) {
        String sql = "select * from ddl_lottery_delivery where user = ?";
        List<Delivery> list = getJdbcTemplate().query(sql, new Object[] { user }, rowMapper);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public void update(Delivery dv) {
        String sql = "update ddl_lottery_delivery set user_address=?, real_name=?, phone_number=?, gift_content=? where user=?";
        this.getJdbcTemplate().update(sql,
                                      new Object[] { dv.getUserAddress(), dv.getRealName(), dv.getPhoneNumber(), dv.getGiftContent(), dv.getUser() });
    }

}
