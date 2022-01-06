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
import java.util.List;
import java.util.Date;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.impl.PageVersionDAO;
import net.duckling.ddl.util.AbstractBaseDAO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class PageVersionDAOImpl extends AbstractBaseDAO implements PageVersionDAO {

    private static final Logger LOG = Logger.getLogger(PageVersionDAOImpl.class);

    private static final String SQL_CREATE = "insert into a1_page_version(tid,rid,version,title,editor,edit_time,content,size)" +
            " values(?,?,?,?,?,?,?,?)";
    //modify by lvly@2012-7-20
    private static final String SQL_DELETE = "update  a1_page_version set status='"+LynxConstants.STATUS_DELETE+"' ";
    private static final String SQL_QUERY = "select * from a1_page_version";
    private static final String SQL_UPDATE = "update a1_page_version set tid=?, " +
            "version=?, title=?, editor=?, edit_time=?, content=? ,size=?";
    private static final String BY_ID = " where id=?";
    private static final String BY_TIDRID = " where tid=? and rid=?";
    private static final String BY_PIDVER = " where rid=? and version=?";
    private static final String SQL_QUERY_LATEST = "select * from a1_page_version where  rid=? order by version desc limit 1 ";
    private static final String RECOVER_PAGE_VERSION = "update a1_page_version set status='"+LynxConstants.STATUS_AVAILABLE+"' where tid=? and rid=?";
    private RowMapper<PageVersion> pageVersionRowMapper = new RowMapper<PageVersion>(){

            @Override
            public PageVersion mapRow(ResultSet rs, int index) throws SQLException {
                PageVersion pageVersion = new PageVersion();
                pageVersion.setId(rs.getInt("id"));
                pageVersion.setTid(rs.getInt("tid"));
                pageVersion.setRid(rs.getInt("rid"));
                pageVersion.setVersion(rs.getInt("version"));
                pageVersion.setTitle(rs.getString("title"));
                pageVersion.setEditor(rs.getString("editor"));
                pageVersion.setEditTime(new Date(rs.getTimestamp("edit_time").getTime()));
                pageVersion.setContent(rs.getString("content"));
                pageVersion.setSize(rs.getLong("size"));
                return pageVersion;
            }
        };

    @Override
    public int create(final PageVersion pageVersion) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setInt(++i, pageVersion.getTid());
                    ps.setInt(++i, pageVersion.getRid());
                    ps.setInt(++i, pageVersion.getVersion());
                    ps.setString(++i, pageVersion.getTitle());
                    ps.setString(++i, pageVersion.getEditor());
                    ps.setTimestamp(++i, new Timestamp(pageVersion.getEditTime().getTime()));
                    ps.setString(++i, pageVersion.getContent());
                    ps.setLong(++i, pageVersion.getSize());
                    return ps;
                }

            }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public int delete(int id) {
        return this.getJdbcTemplate().update(SQL_DELETE+BY_ID, new Object[]{id});
    }

    @Override
    public int deleteAllPageVersion(int rid, int tid){
        return this.getJdbcTemplate().update(SQL_DELETE+BY_TIDRID, new Object[]{tid, rid});
    }

    @Override
    public int update(int id, PageVersion pageVersion) {
        return this.getJdbcTemplate().update(SQL_UPDATE+BY_ID, new Object[]{
                pageVersion.getTid(),
                pageVersion.getVersion(), pageVersion.getTitle(), pageVersion.getEditor(),
                pageVersion.getEditTime(), pageVersion.getContent(),pageVersion.getSize(), id
            });
    }

    @Override
    public PageVersion getPageVersionById(int id) {
        List<PageVersion> list = this.getJdbcTemplate().query(SQL_QUERY+BY_ID, new Object[]{id},
                                                              pageVersionRowMapper);
        if(null==list || list.size()<=0){
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for PageVersion " +
                      "by id = "+id);
        }
        return list.get(0);
    }

    @Override
    public PageVersion getPageVersion(int rid,  int version){
        List<PageVersion> list = this.getJdbcTemplate().query(SQL_QUERY+BY_PIDVER,
                                                              new Object[]{ rid, version}, pageVersionRowMapper);
        if(null==list || list.size()<=0){
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for PageVersion " +
                      "by rid = "+rid+" and  version = "+version);
        }
        return list.get(0);
    }

    @Override
    public PageVersion getLatestPageVersion(int rid){
        List<PageVersion> list = this.getJdbcTemplate().query(SQL_QUERY_LATEST,
                                                              new Object[]{rid}, pageVersionRowMapper);
        if(null==list || list.size()<=0)
        {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<PageVersion> getAllPageVersionByTIDRID(int tid, int rid) {
        return this.getJdbcTemplate().query(SQL_QUERY+BY_TIDRID,
                                            new Object[]{tid,rid}, pageVersionRowMapper);
    }

    @Override
    public List<PageVersion> getVersions(int rid, int tid, int offset, int size) {
        String sql = "select * from a1_page_version where rid=? and tid=? order by version desc limit ?,?";
        return getJdbcTemplate().query(sql, new Object[]{rid,tid,offset,size},pageVersionRowMapper);
    }

    @Override
    public List<PageVersion> getUserRecentPages(){

        return null;
    }

    @Override
    public int recoverPafeVersion(int rid, int tid) {
        return getJdbcTemplate().update(RECOVER_PAGE_VERSION, new Object[]{tid,rid});
    }

}
