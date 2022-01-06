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
package net.duckling.ddl.service.navbar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.navbar.NavbarItem;
import net.duckling.ddl.service.navbar.impl.NavbarItemDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class NavbarItemDAOImpl extends AbstractBaseDAO implements NavbarItemDAO {

    private static final Logger LOG = Logger.getLogger(NavbarItemDAOImpl.class);

    private static final String SQL_QUERY = "select * from a1_navbar_item";
    private static final String SQL_DELETE = "delete from a1_navbar_item";
    private static final String SQL_CREATE = "insert into a1_navbar_item(uid,tid,title,url,sequence) values(?,?,?,?,?)";
    private static final String SQL_UPDATE = "update a1_navbar_item set title=?, url=?";
    private static final String BY_UIDTID = " where uid=? and tid=?";
    private static final String BY_ID = " where id=?";

    private RowMapper<NavbarItem> navbarRowMapper = new RowMapper<NavbarItem>(){

            @Override
            public NavbarItem mapRow(ResultSet rs, int index) throws SQLException {
                NavbarItem navbarItem = new NavbarItem();
                navbarItem.setId(rs.getInt("id"));
                navbarItem.setUid(rs.getString("uid"));
                navbarItem.setTid(rs.getInt("tid"));
                navbarItem.setTitle(rs.getString("title"));
                navbarItem.setUrl(rs.getString("url"));
                navbarItem.setSequence(rs.getInt("sequence"));
                return navbarItem;
            }

        };

    @Override
    public int create(final NavbarItem navbarItem) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setString(++i, navbarItem.getUid());
                    ps.setInt(++i, navbarItem.getTid());
                    ps.setString(++i, navbarItem.getTitle());
                    ps.setString(++i, navbarItem.getUrl());
                    ps.setInt(++i, navbarItem.getSequence());
                    return ps;
                }

            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key==null)?-1:key.intValue();
    }

    @Override
    public int delete(int id) {
        return this.getJdbcTemplate().update(SQL_DELETE+BY_ID, new Object[]{id});
    }

    @Override
    public int update(int id, String title, String url) {
        return this.getJdbcTemplate().update(SQL_UPDATE+BY_ID, new Object[]{
                title, url, id});
    }

    @Override
    public NavbarItem getNavbarItemById(int id) {
        List<NavbarItem> list = this.getJdbcTemplate().query(SQL_QUERY+BY_ID, new Object[]{id}, navbarRowMapper);
        if(null==list || list.size()<=0){
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for NavbarItem " +
                      "by id = "+id);
        }
        return list.get(0);
    }

    @Override
    public List<NavbarItem> getAllNavbarItemByUidTid(String uid, int tid) {
        return this.getJdbcTemplate().query(SQL_QUERY+BY_UIDTID, new Object[]{uid,tid}, navbarRowMapper);
    }

}
