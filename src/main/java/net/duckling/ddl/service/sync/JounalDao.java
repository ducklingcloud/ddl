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
package net.duckling.ddl.service.sync;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JounalDao extends AbstractDao implements IJounalDao{
    private static final String INSERT = "insert into ddl_jounal(tid,jid,device,operation,fid,fver,occur_time,is_dir,path,to_path) values(?,?,?,?,?,?,?,?,?,?)";
    private static final String DELETE = "delete from ddl_jounal where jid=?";
    private static final String LIST = "select jid,tid,operation,device,fid,fver,occur_time,is_dir,path,to_path from ddl_jounal ";

    private RowMapper<Jounal> rowMapper = new RowMapper<Jounal>() {
            public Jounal mapRow(ResultSet rs, int rowNum) throws SQLException {
                Jounal obj = new Jounal();
                obj.setTid(rs.getInt("tid"));
                obj.setJid(rs.getLong("jid"));
                obj.setOperation(rs.getString("operation"));
                obj.setDevice(rs.getString("device"));
                obj.setFid(rs.getLong("fid"));
                obj.setFver(rs.getLong("fver"));
                obj.setOccurTime(rs.getDate("occur_time"));
                obj.setDir(rs.getBoolean("is_dir"));
                obj.setPath(rs.getString("path"));
                obj.setToPath(rs.getString("to_path"));
                return obj;
            }
        };

    @Override
    public long insert(final Jounal obj) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement pst = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    pst.setInt(++i, obj.getTid());
                    pst.setLong(++i, obj.getJid());
                    pst.setString(++i, obj.getDevice());
                    pst.setString(++i, obj.getOperation());
                    pst.setLong(++i, obj.getFid());
                    pst.setLong(++i, obj.getFver());
                    pst.setTimestamp(++i, new Timestamp(obj.getOccurTime().getTime()));
                    pst.setBoolean(++i, obj.isDir());
                    pst.setString(++i, obj.getPath());
                    pst.setString(++i, obj.getToPath());
                    return pst;
                }
            }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean insertBath(final List<Jounal> jounals) {

        return getJdbcTemplate().batchUpdate(INSERT, new BatchPreparedStatementSetter(){

                @Override
                public int getBatchSize() {
                    return jounals.size();
                }
                @Override
                public void setValues(PreparedStatement pst, int index) throws SQLException {
                    Jounal obj = jounals.get(index);
                    int i=0;
                    pst.setInt(++i, obj.getTid());
                    pst.setLong(++i, obj.getJid());
                    pst.setString(++i, obj.getDevice());
                    pst.setString(++i, obj.getOperation());
                    pst.setLong(++i, obj.getFid());
                    pst.setLong(++i, obj.getFver());
                    pst.setTimestamp(++i, new Timestamp(obj.getOccurTime().getTime()));
                    pst.setBoolean(++i, obj.isDir());
                    pst.setString(++i, obj.getPath());
                    pst.setString(++i, obj.getToPath());
                }
            }).length>0;
    }

    @Override
    public void delete(long jid) {
        this.getJdbcTemplate().update(DELETE, new Object[] {jid});
    }

    @Override
    public List<Jounal> list(int tid, long fid, long fver) {
        String sql = LIST + "where tid=? and fid=? and fver=?";
        return getJdbcTemplate().query(sql, new Object[]{tid, fid, fver}, rowMapper);
    }

    @Override
    public List<Jounal> list(int tid, long jid) {
        String sql = LIST + "where tid=? and jid>?";
        return getJdbcTemplate().query(sql, new Object[]{tid, jid}, rowMapper);
    }

    @Override
    public int getLatestJid(int tid) {
        String sql = "select max(jid) from ddl_jounal where tid=?";
        try{
            return getJdbcTemplate().queryForObject(sql, new Object[]{tid}, Integer.class);
        }catch(NullPointerException e){
            return 0;
        }
    }
}
