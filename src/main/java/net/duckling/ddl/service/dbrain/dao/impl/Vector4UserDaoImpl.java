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
package net.duckling.ddl.service.dbrain.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.dbrain.Vector4User;
import net.duckling.ddl.service.dbrain.dao.Vector4UserDao;
import net.duckling.ddl.service.dbrain.util.Util;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.springframework.jdbc.core.RowMapper;

public class Vector4UserDaoImpl extends AbstractBaseDAO implements Vector4UserDao{

    private static final String TABLE_NAME = "vec4user";
    private static final String PASSPORT = "passport";
    private static final String USERVECTOR = "uservector";
    private static final String APPID = "appid";

    @Override
    public Vector4User getUserVecByID(int id) {
        String sql = "select * from " + TABLE_NAME + " where id = ?";
        List<Vector4User> list = getJdbcTemplate().query(sql,
                                                         new Object[] { id }, v4uRowMapper);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public float[] getUserVecByPassport(String passport) {
        String sql = "select * from " + TABLE_NAME + " where " + PASSPORT
                + " = ?";
        List<Vector4User> list = getJdbcTemplate().query(sql,
                                                         new Object[] { passport }, v4uRowMapper);

        float[] uservector = new float[200];
        float[] uservector1 = new float[200];
        float[] uservector2 = new float[200];

        if (list.size() == 1) {
            return Util.string2float(list.get(0).getUservector().split(","));
        } else if (list.size() == 2) {
            for (Vector4User v4ubean : list) {
                if (v4ubean.getAppid() == 1) {
                    uservector1 = Util.string2float(v4ubean.getUservector()
                                                    .split(","));
                } else {
                    uservector2 = Util.string2float(v4ubean.getUservector()
                                                    .split(","));
                }
            }

            for (int i = 0; i < 200; i++) {
                uservector[i] = (float) (0.25 * uservector1[i] + 0.75 * uservector2[i]);
            }
            return uservector;
        } else // 新用户
        {
            return null;
        }
    }

    @Override
    public int insertUserVec(Vector4User uservec) {
        String sql = "insert into " + TABLE_NAME + "(" + PASSPORT + ","
                + USERVECTOR + "," + APPID + ")" + " values(?,?,?)";
        Object[] params = new Object[] { uservec.getPassport(),
            uservec.getUservector(), uservec.getAppid() };
        return this.getJdbcTemplate().update(sql, params);
    }

    /**
     * 从vec4user表中查询信息
     */
    private RowMapper<Vector4User> v4uRowMapper = new RowMapper<Vector4User>() {
            public Vector4User mapRow(ResultSet rs, int index)
                    throws SQLException {
                Vector4User instance = new Vector4User();
                instance.setId(rs.getInt("id"));
                instance.setPassport(rs.getString(PASSPORT));
                instance.setUservector(rs.getString(USERVECTOR));
                instance.setAppid(rs.getInt(APPID));
                return instance;

            }
        };
}
