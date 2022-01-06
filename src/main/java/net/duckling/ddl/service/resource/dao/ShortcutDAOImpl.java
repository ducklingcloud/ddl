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

package net.duckling.ddl.service.resource.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import net.duckling.ddl.service.resource.DShortcut;
import net.duckling.ddl.service.resource.impl.ShortcutDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.StringUtil;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


/**
 *
 * @author zhonghui
 *
 */
@Repository
public class ShortcutDAOImpl extends AbstractBaseDAO implements ShortcutDAO {
    private static final String TABLE_NAME = " a1_shortcut ";
    private static final String INSERT_SQL="insert into "+TABLE_NAME+" (tid,tgid,rid,color,creator,createtime,sequence) values(?,?,?,?,?,?,?)";
    private static final String DELETE_SQL="delete from "+TABLE_NAME+" where id=?";
    private static final String QUERY_SQL ="select * from "+TABLE_NAME+" where tid=? and tgid=? order by sequence";
    private static final String COUNT_SQL ="select max(sequence) as sequence from "+TABLE_NAME+" where tid=? and tgid=?";
    private static final String UPDATE_SQL ="update "+TABLE_NAME+" set tid=?,tgid=?,rid=?,color=?,creator=?,createtime=?,sequence=? where id=?";
    private static final String QUERY_BYID = "select * from "+TABLE_NAME+" where id = ?";
    private static final String UPDATE_SEQUECE ="update "+TABLE_NAME+" set sequence=? where id =?";

    private RowMapper<DShortcut> shortcutMapper = new RowMapper<DShortcut>(){
            public DShortcut mapRow(ResultSet rs, int index) throws SQLException {
                DShortcut instance = new DShortcut();
                instance.setId(rs.getInt("id"));
                instance.setTid(rs.getInt("tid"));
                instance.setTgid(rs.getInt("tgid"));
                instance.setRid(rs.getInt("rid"));
                instance.setColor(rs.getString("color"));
                instance.setCreator(rs.getString("creator"));
                instance.setCreatorTime(rs.getDate("createTime"));
                instance.setSequence(rs.getInt("sequence"));
                return instance;
            }
        };

    @Override
    public boolean insert(final DShortcut instance) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setInt(++i, instance.getTid());
                    pst.setInt(++i, instance.getTgid());
                    pst.setInt(++i, instance.getRid());
                    pst.setString(++i, instance.getColor());
                    pst.setString(++i, instance.getCreator());
                    pst.setTimestamp(++i, new Timestamp(instance.getCreatorTime().getTime()));
                    pst.setInt(++i, instance.getSequence());
                    return pst;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        if(key!=null){
            instance.setId(key.intValue());
            return true;
        }else{
            instance.setId(-1);
            return false;
        }
    }

    /* (non-Javadoc)
     * @see net.duckling.ddl.dao.impl.ShortcutDAO#updateSequece(java.util.List)
     */
    @Override
    public boolean updateSequece(final List<Integer> ids){
        return getJdbcTemplate().batchUpdate(UPDATE_SEQUECE,new BatchPreparedStatementSetter() {
                private int count=0;
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    int n = 0;
                    ps.setInt(++n, ++count);
                    ps.setInt(++n, ids.get(i));

                }

                @Override
                public int getBatchSize() {
                    return ids.size();
                }


            } ).length>0;
    }

    @Override
    public boolean delete(int id) {
        return this.getJdbcTemplate().update(DELETE_SQL, new Object[] {id})> 0;
    }

    @Override
    public List<DShortcut> queryShortcut(int tid,int tgid) {
        return getJdbcTemplate().query(QUERY_SQL,new Object[]{tid,tgid}, shortcutMapper);
    }

    @Override
    public int getShortcutCount(int tid,int tgid){
        List<Integer> result = getJdbcTemplate().query(COUNT_SQL,new Object[]{tid,tgid} ,new RowMapper<Integer>(){
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("sequence");
                }

            });

        if(result==null||result.isEmpty()){
            return 0;
        }else{
            return result.get(0);
        }
    }

    @Override
    public boolean update(final DShortcut instance){
        return getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(UPDATE_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
                    setUpdatePreStatement(instance, pst);
                    return pst;
                }
            })>0;
    }

    @Override
    public boolean update( final List<DShortcut> dcList) {
        if(dcList==null||dcList.isEmpty()){
            return false ;
        }
        if(dcList.size()==1){
            return update(dcList.get(0));
        }
        return getJdbcTemplate().batchUpdate(UPDATE_SQL, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    setUpdatePreStatement(dcList.get(i), ps);

                }

                @Override
                public int getBatchSize() {
                    return dcList.size();
                }
            }).length>0;
    }

    private void setUpdatePreStatement(DShortcut instance,PreparedStatement pst) throws SQLException{
        int i = 0;
        pst.setInt(++i, instance.getTid());
        pst.setInt(++i, instance.getTgid());
        pst.setInt(++i, instance.getRid());
        pst.setString(++i, instance.getColor());
        pst.setString(++i, instance.getCreator());
        pst.setTimestamp(++i, new Timestamp(instance.getCreatorTime().getTime()));
        pst.setInt(++i, instance.getSequence());
        pst.setInt(++i, instance.getId());
    }

    @Override
    public List<DShortcut> getShortcuts(Collection<Integer> ids) {
        String sql ="select * from "+TABLE_NAME+ "where  id in"+StringUtil.getSQLInFromInt(ids);
        return getJdbcTemplate().query(sql, shortcutMapper);
    }

    @Override
    public List<DShortcut> queryShortcut(int tid, Collection<Integer> ids) {
        String sql = "select * from "+TABLE_NAME+" where tid=? and tgid in"+StringUtil.getSQLInFromInt(ids)+" order by sequence";
        return getJdbcTemplate().query(sql, new Object[]{tid}, shortcutMapper);
    }

    @Override
    public DShortcut getDSortcutById(int id) {
        List<DShortcut> s =  getJdbcTemplate().query(QUERY_BYID, new Object[]{id}, shortcutMapper);
        if(s==null||s.isEmpty()){
            return null;
        }else{
            return s.get(0);
        }
    }

}
