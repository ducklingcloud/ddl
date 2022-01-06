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
package net.duckling.ddl.service.tobedelete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.StringUtil;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;


@Repository
public class PageDAOImpl extends AbstractBaseDAO implements PageDAO {

    private static final Logger LOG = Logger.getLogger(PageDAOImpl.class);

    private static final String SQL_CREATE = "insert into a1_page(pid,tid,status,title,create_time,creator,creator_name,last_editor,last_editor_name,last_edit_time,last_version) " +
            "values(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_DELETE = "update a1_page set status='"+LynxConstants.STATUS_DELETE+"' ";
    private static final String SQL_QUERY = "select * from a1_page";
    private static final String SQL_UPDATE = "update a1_page set status=?," +
            " title=?, create_time=?, creator=?, creator_name=?, last_editor=?, " +
            "last_editor_name=?, last_edit_time=?, last_version=?";
    private static final String BY_PIDTID = " where pid=? and tid=?";
    private static final String BY_TID = " where tid=?";

    private static final String SQL_MAXPID = "select max(pid) from a1_page where tid=?";

    private RowMapper<Page> pageRowMapper = new RowMapper<Page>(){

            @Override
            public Page mapRow(ResultSet rs, int index) throws SQLException {
                Page page = new Page();
                page.setId(rs.getInt("id"));
                page.setPid(rs.getInt("pid"));
                page.setTid(rs.getInt("tid"));
                page.setStatus(rs.getString("status"));
                page.setTitle(rs.getString("title"));
                page.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
                page.setCreator(rs.getString("creator"));
                page.setCreatorName(rs.getString("creator_name"));
                page.setLastEditor(rs.getString("last_editor"));
                page.setLastEditorName(rs.getString("last_editor_name"));
                page.setLastEditTime(new Date(rs.getTimestamp("last_edit_time").getTime()));
                page.setLastVersion(rs.getInt("last_version"));
                return page;
            }

        };

    private RowMapper<PageContentRender> contentRowMapper = new RowMapper<PageContentRender>(){

            @Override
            public PageContentRender mapRow(ResultSet rs, int index) throws SQLException {
                PageContentRender pcr = new PageContentRender();
                pcr.setContent(rs.getString("content"));
                pcr.setId(rs.getInt("pid"));
                pcr.setTid(rs.getInt("tid"));
                return pcr;
            }

        };

    @Override
    public synchronized int create(final Page page) {
        if(page.getId()<=0){
            int newPid = getMaxPid(page.getTid())+1;
            page.setPid(newPid);
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator(){

                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setInt(++i, page.getPid());
                    ps.setInt(++i, page.getTid());
                    ps.setString(++i, page.getStatus());
                    ps.setString(++i, page.getTitle());
                    ps.setTimestamp(++i, new Timestamp(page.getCreateTime().getTime()));
                    ps.setString(++i, page.getCreator());
                    ps.setString(++i, page.getCreatorName());
                    ps.setString(++i, page.getLastEditor());
                    ps.setString(++i, page.getLastEditorName());
                    ps.setTimestamp(++i, new Timestamp(page.getLastEditTime().getTime()));
                    ps.setInt(++i, page.getLastVersion());
                    return ps;
                }

            }, keyHolder);
        return page.getPid();
    }

    @Override
    public int delete(int pid, int tid) {
        return getJdbcTemplate().update(SQL_DELETE+BY_PIDTID, new Object[]{pid, tid});
    }

    @Override
    public int batchDelete(int tid, List<Integer> pids){
        if(null == pids || pids.isEmpty()){
            return 0;
        }
        String sql = SQL_DELETE + " where tid="+tid+" and pid in(";
        StringBuilder sb = new StringBuilder();
        for(int pid : pids){
            sb.append(pid +",");
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), ")");
        sql += sb.toString();
        return getJdbcTemplate().update(sql);
    }

    @Override
    public int update(int pid, int tid, Page page) {
        return getJdbcTemplate().update(SQL_UPDATE+BY_PIDTID, new Object[]{
                page.getStatus(), page.getTitle(),
                new Timestamp(page.getCreateTime().getTime()), page.getCreator(),page.getCreatorName(),
                page.getLastEditor(),page.getLastEditorName(),new Timestamp(page.getLastEditTime().getTime()),
                page.getLastVersion(), pid, tid
            });
    }

    @Override
    public int update(Page page) {
        return getJdbcTemplate().update(SQL_UPDATE+BY_PIDTID, new Object[]{
                page.getStatus(), page.getTitle(),
                new Timestamp(page.getCreateTime().getTime()), page.getCreator(),page.getCreatorName(),
                page.getLastEditor(),page.getLastEditorName(),new Timestamp(page.getLastEditTime().getTime()),
                page.getLastVersion(), page.getPid(), page.getTid()
            });
    }

    @Override
    public Page getPage(int pid, int tid) {
        List<Page> list = getJdbcTemplate().query(SQL_QUERY+BY_PIDTID, new Object[]{pid,tid},pageRowMapper);
        if(null==list || list.size()<=0)
        {
            LOG.info("not found page " +
                     "by pid = "+pid+" and tid = "+tid);
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for Page " +
                      "by pid = "+pid+" and tid = "+tid);
        }
        return list.get(0);
    }

    @Override
    public List<Page> getPagesOfTeam(int tid, int offset, int size) {
        if(offset<0 || size<0 || (size==0 && offset>0)){
            LOG.error("offset and size should be zero or positive, however in this query," +
                      "offset = "+offset+" and size = "+size);
            return null;
        }
        String limit = "";
        if(offset>=0 && size>0){
            limit = " limit "+offset+","+size;
        }
        return getJdbcTemplate().query(SQL_QUERY+BY_TID+limit, new Object[]{tid}, pageRowMapper);
    }

    @Override
    public List<Page> fetchDPageBasicListByPageIncrementId(List<Long> pageIds) {
        if(null == pageIds || pageIds.size()<=0){
            return null;
        }

        String idsStr = buildSphinxIdsSQL(pageIds);
        String orderStr = idsStr.replace("(", "(id,");
        String extend = " where id in "+ idsStr + " order by field" + orderStr;
        return getJdbcTemplate().query(SQL_QUERY+extend, pageRowMapper);
    }

    @Override
    public List<PageContentRender> fetchDPageContentByIncrementId(List<Long> page_ids) {
        String sql = "select a.pid as pid, a.tid as tid, a.content as content from a1_page_version a" +
                " inner join a1_page b on a.pid=b.pid and a.tid=b.tid and a.version=b.last_version ";
        if(null == page_ids || page_ids.size()<=0){
            return null;
        }
        String idsStr = buildSphinxIdsSQL(page_ids);
        String orderStr = idsStr.replace("(", "(b.id,");
        String extend = " where b.id in "+ idsStr + " order by field" + orderStr;
        return getJdbcTemplate().query(sql+extend,contentRowMapper);
    }

    private int getMaxPid(int tid){
        return getJdbcTemplate().queryForObject(SQL_MAXPID, new Object[]{tid}, Integer.class);
    }

    private String buildSphinxIdsSQL(List<Long> page_ids){
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(Long l : page_ids){
            sb.append(l+",");
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), ")");
        return sb.toString();
    }

    @Override
    public List<Page> getRecentUserEditPages(int tid,String uid, int offset, int size) {
        String sql = "select * from a1_page a where a.tid=? and a.last_editor=? and a.last_version<>0 order by a.last_edit_time desc limit ?,?";
        return getJdbcTemplate().query(sql, new Object[]{tid,uid,offset,size},pageRowMapper);
    }

    @Override
    public List<Page> getRecentUserCreatePages(int tid, String uid, int offset, int size) {
        String sql = "select * from a1_page a where a.tid=? and a.creator=? and a.last_version<>0 order by a.create_time desc limit ?,?";
        return getJdbcTemplate().query(sql, new Object[]{tid,uid,offset,size},pageRowMapper);
    }

    @Override
    public List<Page> searchResourceByTitle(int tid, String title) {
        String sql = "select * from a1_page where status!='delete' and tid="+tid+" and title like '%"+title+"%' order by id desc";
        return getJdbcTemplate().query(sql, pageRowMapper);
    }

    @Override
    public List<Page> getPage(List<Integer> pids, int tid) {
        String sql = "select * from a1_page where tid="+tid+" and pid in"+StringUtil.getSQLInFromInt(pids);
        return getJdbcTemplate().query(sql, pageRowMapper);
    }

}
