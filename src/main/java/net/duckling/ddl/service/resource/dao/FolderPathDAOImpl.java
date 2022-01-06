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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.duckling.ddl.service.resource.FolderPath;
import net.duckling.ddl.service.resource.impl.FolderPathDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.StringUtil;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
@Repository
public class FolderPathDAOImpl extends AbstractBaseDAO implements FolderPathDAO{

    private RowMapper<FolderPath> pathMapper = new RowMapper<FolderPath>(){
            @Override
            public FolderPath mapRow(ResultSet rs, int index) throws SQLException {
                FolderPath path = new FolderPath();
                path.setAncestorRid(rs.getInt("ancestor_rid"));
                path.setLength(rs.getInt("length"));
                path.setRid(rs.getInt("rid"));
                path.setTid(rs.getInt("tid"));
                return path;
            }

        };
    private static final String CREATE_SQL = "insert into ddl_folder_path(rid,tid,ancestor_rid,length) values(?,?,?,?) ";
    private static final String DELETE__BY_RID="delete from ddl_folder_path where rid=?";
    private static final String QUERY_PATH = "select * from ddl_folder_path where tid=? and rid=? order by length desc";
    private static final String QUERY_PARENT="select * from ddl_folder_path where rid=? and length=1";
    private static final String QUERY_BY_LENGTH="select * from ddl_folder_path where ancestor_rid=? and length=?";
    private static final String QUERY_CHILDREN ="select * from ddl_folder_path where tid=? and ancestor_rid=?";
    private static final String DELETE_BY_RID_ANCESTOR ="delete from ddl_folder_path where rid=? and ancestor_rid=?";
    @Override
    public boolean create(FolderPath folder) {
        return getJdbcTemplate().update(CREATE_SQL, new Object[]{folder.getRid(),folder.getTid(),folder.getAncestorRid(),folder.getLength()})>0;
    }

    @Override
    public boolean delete(FolderPath folder) {
        return getJdbcTemplate().update(DELETE_BY_RID_ANCESTOR, new Object[]{folder.getRid(),folder.getAncestorRid()})>0;
    }

    @Override
    public boolean deleteByRid(int rid) {
        return getJdbcTemplate().update(DELETE__BY_RID, new Object[]{rid})>0;
    }

    @Override
    public List<FolderPath> getPath(int tid,int rid) {
        return getJdbcTemplate().query(QUERY_PATH, new Object[]{tid,rid}, pathMapper);
    }

    @Override
    public FolderPath get(int tid,int rid,int ancestorRid) {
        List<FolderPath> list =  getJdbcTemplate().query("select * from ddl_folder_path where tid=? and rid=? and ancestor_rid=? order by length desc",
                                                         new Object[]{tid,rid,ancestorRid}, pathMapper);
        if(list.size()==0){
            return null;
        }
        return list.get(0);
    }

    @Override
    public FolderPath getParent(int rid) {
        List<FolderPath> r = getJdbcTemplate().query(QUERY_PARENT, new Object[]{rid}, pathMapper);
        if(r==null||r.isEmpty()){
            return null;
        }
        return r.get(0);
    }

    @Override
    public List<FolderPath> query(int rid, int length) {
        return getJdbcTemplate().query(QUERY_BY_LENGTH, new Object[]{rid,length}, pathMapper);
    }

    @Override
    public List<FolderPath> getChildren(int tid,int rid) {
        return getJdbcTemplate().query(QUERY_CHILDREN+" and length=1", new Object[]{tid,rid}, pathMapper);
    }

    @Override
    public boolean deleteByRids(List<Integer> rids) {
        String sql = "delete from ddl_folder_path where rid in"+StringUtil.getSQLInFromInt(rids);
        return getJdbcTemplate().update(sql)>0;
    }

    @Override
    public boolean insertBatch(final List<FolderPath> paths) {
        return getJdbcTemplate().batchUpdate(CREATE_SQL, new BatchPreparedStatementSetter(){

                @Override
                public int getBatchSize() {
                    return paths.size();
                }

                @Override
                public void setValues(PreparedStatement statment, int index) throws SQLException {
                    FolderPath p = paths.get(index);
                    int i=0;
                    statment.setInt(++i, p.getRid());
                    statment.setInt(++i, p.getTid());
                    statment.setInt(++i, p.getAncestorRid());
                    statment.setInt(++i, p.getLength());
                }

            }).length>0;
    }

    @Override
    public boolean delete(final List<FolderPath> rids) {
        return getJdbcTemplate().batchUpdate(DELETE_BY_RID_ANCESTOR,new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement statement, int index) throws SQLException {
                    FolderPath p = rids.get(index);
                    int i=0;
                    statement.setInt(++i, p.getRid());
                    statement.setInt(++i, p.getAncestorRid());
                }

                @Override
                public int getBatchSize() {
                    return rids.size();
                }
            }).length>0;
    }

    @Override
    public List<FolderPath> getAncestor(int tid,int rid) {
        return getPath(tid,rid);
    }
    @Override
    public List<FolderPath> getAncestor(int tid, List<Integer> rids) {
        String sql = "select * from ddl_folder_path where tid=? and rid in"+StringUtil.getSQLInFromInt(rids)+" order by length desc";
        return getJdbcTemplate().query(sql, new Object[]{tid}, pathMapper);
    }
    @Override
    public List<FolderPath> getDescendants(int tid,int rid) {
        return getJdbcTemplate().query(QUERY_CHILDREN, new Object[]{tid,rid}, pathMapper);
    }
    @Override
    public List<FolderPath> getDescendants(int tid, List<Integer> rids) {
        String sql = "select * from ddl_folder_path where tid=? and ancestor_rid in"+StringUtil.getSQLInFromInt(rids);
        return getJdbcTemplate().query(sql, new Object[]{tid}, pathMapper);
    }
    @Override
    public boolean delete(int tid, List<Integer> rids) {
        String sql = "delete from ddl_folder_path where tid="+tid;
        if(rids==null||rids.size()==0){
            return true;
        }else if(rids.size()==1){
            sql = sql +" and rid="+rids.get(0);
            return getJdbcTemplate().update(sql)>0;
        }else{
            sql = sql + " and rid in"+StringUtil.getSQLInFromInt(rids);
            return getJdbcTemplate().update(sql)>0;
        }
    }



}
