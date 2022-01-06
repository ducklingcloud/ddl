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
package net.duckling.ddl.service.grid.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.grid.impl.GridGroup;
import net.duckling.ddl.service.grid.impl.GridGroupDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.SQLObjectMapper;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class GridGroupDAOImpl extends AbstractBaseDAO implements GridGroupDAO {

    private static final Logger LOG = Logger.getLogger(GridGroupDAOImpl.class);

    private static final String SQL_CREATE = "insert into a1_grid_group(tid,uid,last_edit_time,grid_map) values(?,?,?,?)";
    private static final String SQL_UPDATE = "update a1_grid_group set last_edit_time=?, grid_map=? where uid=? and tid=?";
    private static final String QUERY_SQL = "select * from a1_grid_group where uid=? and tid=?";

    private RowMapper<GridGroup> rowMapper = new RowMapper<GridGroup>(){
            @Override
            public GridGroup mapRow(ResultSet rs, int index) throws SQLException {
                GridGroup g = new GridGroup();
                g.setId(rs.getInt("id"));
                g.setTid(rs.getInt("tid"));
                g.setUid(rs.getString("uid"));
                g.setLastEditTime(new Date(rs.getTimestamp("last_edit_time").getTime()));
                g.setGridItemJSONMap((Map<String,String>)SQLObjectMapper.writeObject(rs, "grid_map"));
                return g;
            }

        };

    @Override
    public int addGroup(final GridGroup gg) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setInt(++i,gg.getTid());
                    ps.setString(++i, gg.getUid());
                    ps.setTimestamp(++i, new Timestamp(gg.getLastEditTime().getTime()));
                    ps.setObject(++i, gg.getGridItemJsonMap());
                    return ps;
                }

            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key==null)?-1:key.intValue();
    }

    @Override
    public int updateGroup(final GridGroup gg) {
        return getJdbcTemplate().update(SQL_UPDATE,new Object[]{gg.getLastEditTime(),gg.getGridItemJsonMap(),gg.getUid(),gg.getTid()});
    }

    @Override
    public int deleteGroup(int ggid) {
        return 0;
    }

    @Override
    public GridGroup getGridGroup(String uid, int tid) {
        List<GridGroup> list = this.getJdbcTemplate().query(QUERY_SQL, new Object[]{uid,tid},rowMapper);
        if(null==list || list.size()<=0){
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for GridGroup " +
                      "by uid = "+uid+" and tid = "+tid);
        }
        return list.get(0);
    }

}
