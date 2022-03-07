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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.duckling.ddl.common.DBs;
import org.springframework.jdbc.core.JdbcTemplate;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.service.resource.impl.TagItemDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQueryKeywordUtil;
import net.duckling.ddl.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TagItemDAOImpl extends AbstractBaseDAO implements TagItemDAO {
    private static final Logger LOG = Logger.getLogger(TagDAOImpl.class);

    private static final String SQL_DELETE = "delete from a1_tag_item";
    private static final String TAGID = " where tgid=?";
    private static final String SQL_UPDATE = "update a1_tag_item set tid=?, tgid=?, rid=? where id=? ";
    private static final String SQL_QUERY = "select * from a1_tag_item ";
    private static final String TAGITEMBYID = " where id=?";

    // For dbms compatibility, set in constructor.
    private final String SQL_CREATE;

    private RowMapper<TagItem> tagItemRowMapper = new RowMapper<TagItem>() {
            @Override
            public TagItem mapRow(ResultSet rs, int index) throws SQLException {
                TagItem tagItem = new TagItem();
                tagItem.setId(rs.getInt("id"));
                tagItem.setTid(rs.getInt("tid"));
                tagItem.setTgid(rs.getInt("tgid"));
                tagItem.setRid(rs.getInt("rid"));
                return tagItem;
            }
        };

    public TagItemDAOImpl() {
        switch (DBs.getDbms()) {
            case "mysql":
                SQL_CREATE =
                        "INSERT INTO a1_tag_item (tid,tgid,rid) "+
                        "SELECT ?,?,? FROM DUAL "+
                        "WHERE NOT EXISTS ( "+
                        "  SELECT * FROM a1_tag_item "+
                        "  WHERE a1_tag_item.tid = ? AND a1_tag_item.tgid = ? "+
                        "    AND a1_tag_item.rid = ? "+
                        ")";
                break;
            case "derby":
                SQL_CREATE =
                        "INSERT INTO a1_tag_item (tid, tgid, rid) "+
                        "SELECT * FROM ( VALUES ( "+
                        "  CAST(? as int), CAST(? as int), CAST(? as int) "+
                        "  ) ) AS t "+
                        "WHERE NOT EXISTS ( "+
                        "  SELECT * FROM a1_tag_item "+
                        "  WHERE tid = ? AND tgid = ? "+
                        "    AND rid = ? "+
                        ")";
                break;
            default:
                SQL_CREATE = "DBMS Not Supported";
                LOG.error("Fatal: DBMS not supported. Please check configuration.");
                break;
        }
    }

    @Override
    public int create(final TagItem tagItem) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = this.getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection conn)
                        throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(SQL_CREATE,
                                               PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    ps.setInt(++i, tagItem.getTid());
                    ps.setInt(++i, tagItem.getTgid());
                    ps.setInt(++i, tagItem.getRid());
                    ps.setInt(++i, tagItem.getTid());
                    ps.setInt(++i, tagItem.getTgid());
                    ps.setInt(++i, tagItem.getRid());
                    return ps;
                }
            }, keyHolder);
        return (rows > 0) ? keyHolder.getKey().intValue() : -1;
    }

    @Override
    public int delete(int id) {
        return this.getJdbcTemplate().update(SQL_DELETE+TAGITEMBYID, new Object[]{id});
    }

    @Override
    public int update(int id, TagItem tagItem) {
        return this.getJdbcTemplate().update(SQL_UPDATE, new Object[]{tagItem.getTid(),
                tagItem.getTgid(), tagItem.getRid(),id});
    }

    @Override
    public int batchUpdateWithTag(final int tid, final int tagid, final List<Long> rids) {
        if (rids == null || rids.size() <= 0) {
            return 0;
        }
        this.getJdbcTemplate().batchUpdate(SQL_CREATE, new BatchPreparedStatementSetter() {

                @Override
                public int getBatchSize() {
                    return rids.size();
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    long rid = rids.get(index);
                    int i = 0;
                    ps.setInt(++i, tid);
                    ps.setInt(++i, tagid);
                    ps.setInt(++i, (int)rid);
                    ps.setInt(++i, tid);
                    ps.setInt(++i, tagid);
                    ps.setInt(++i, (int)rid);
                }

            });
        return 1;
    }

    @Override
    public TagItem getTagItemById(int id) {
        List<TagItem> list = this.getJdbcTemplate().query(SQL_QUERY+TAGITEMBYID,
                                                          new Object[]{id}, tagItemRowMapper);
        if(null==list || list.size()<=0)
        {
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for TagItem " +
                      "by id = "+id);
        }
        return list.get(0);
    }

    @Override
    public int deleteByTagId(int tgid) {
        return this.getJdbcTemplate().update(SQL_DELETE+TAGID, new Object[]{tgid});
    }

    @Override
    public List<TagItem> getItemsInTags(int[] tgids, int offset, int size) {
        if(offset<0 || size<0 || (size==0 && offset>0)){
            LOG.error("offset and size should be positive while query, however, in this query, " +
                      "offset = "+offset+", size = "+size);
            return new ArrayList<TagItem>();
        }
        int len = tgids.length;
        String condition = "";
        if(len>0){
            StringBuilder sb = new StringBuilder();
            sb.append(" where tgid in(");
            for(int i = 0; i<tgids.length; i++){
                sb.append(tgids[i]+",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
            condition = sb.toString();
        }
        String limit = "";
        if(offset>=0 && size>0){
            limit = DBs.getDbms().equals("mysql") ?
                    " LIMIT "+ offset +","+ size :
                    " OFFSET "+ offset +" ROWS FETCH NEXT "+ size +" ROWS ONLY";
        }
        String sql = SQL_QUERY+condition+limit;
        return this.getJdbcTemplate().query(sql, tagItemRowMapper);
    }

    @Override
    public int removeItems(int tid, int tgid, List<Integer> rids) {
        if(null == rids || rids.isEmpty()){
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" where tid="+tid+" and tgid="+tgid+" and rid in(");
        for(int rid : rids){
            sb.append(rid+",");
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), ")");
        return this.getJdbcTemplate().update(SQL_DELETE+sb.toString());
    }

    @Override
    public int deleteByTagIds(int[] tgids) {
        if(null == tgids || tgids.length<=0){
            return -1;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" where tgid in(");
        for(int i=0;i<tgids.length;i++){
            sb.append(tgids[i]+",");
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), ")");
        return this.getJdbcTemplate().update(SQL_DELETE+sb.toString());
    }

    @Override
    public void deleteTagItem(List<Integer> rids, Integer tagId) {
        if(null==rids || rids.size()<=0){
            return;
        }
        String sql = "delete from a1_tag_item where tgid=? and rid in(";
        StringBuilder sb = new StringBuilder();
        for(Integer rid : rids){
            sb.append(rid+",");
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), ")");
        sql += sb.toString();
        this.getJdbcTemplate().update(sql,new Object[]{tagId});
    }

    @Override
    public List<TagItem> getItemsInTag(int tgid) {
        String sql = "select * from a1_tag_item where tgid=?";
        return this.getJdbcTemplate().query(sql, new Object[]{tgid},tagItemRowMapper);
    }

    @Override
    public boolean isItemHasTag(int rid, int existTagId) {
        String sql = "select count(id) from a1_tag_item where rid=? and tgid=?";
        int count  = this.getJdbcTemplate().queryForObject(sql,new Object[]{rid,existTagId}, Integer.class);
        return count!=0;
    }

    @Override
    public List<TagItem> getAllTagItem() {
        String sql = "select * from a1_tag_item";
        return this.getJdbcTemplate().query(sql, tagItemRowMapper);
    }

    @Override
    public List<TagItem> getAllTagItemOfRid(int tid, int rid) {
        String sql = "select * from a1_tag_item where tid=? and rid=?";
        return this.getJdbcTemplate().query(sql, new Object[]{tid, rid}, tagItemRowMapper);
    }

    @Override
    public void deleteAllTagItemOfRid(int tid, int rid) {
        String sql = "delete from a1_tag_item where tid=? and rid=?";
        this.getJdbcTemplate().update(sql, new Object[]{tid, rid});
    }

    @Override
    public int getTagCount(int tid, int tagId) {
        String sql = "select count(*) from a1_tag_item a inner join a1_resource b on a.rid=b.rid " +
                "and a.tid=b.tid where a.tid=? and a.tgid=? and b.status  ='"
                + LynxConstants.STATUS_AVAILABLE + "'";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{tid, tagId},Integer.class);
    }


    private RowMapper<Resource> resourceRowMapper = new ResourceRowMapper("r.");
    @Override
    public PaginationBean<Resource> getTeamTagFiles(int tid, int tagId, int begin, int maxPageSize, String order, String keyWord) {
        List<Integer> tagIds=new ArrayList<Integer>();
        tagIds.add(tagId);
        return getTeamTagFiles( tid , tagIds, begin, maxPageSize, order, keyWord);
    }

    public PaginationBean<Resource> getTeamTagFiles(int tid, Collection<Integer> tagIds, int begin, int maxPageSize, String order, String keyWord) {
        Map<String,Object> paramMap=new HashMap<String,Object>();
        paramMap.put("tid", tid);
        String countSql = "select count(distinct r.rid) from a1_tag_item a,a1_resource r where a.tid=:tid and a.tgid in "+StringUtil.getSQLInFromInt(tagIds)+" and r.rid=a.rid and (r.status='"+LynxConstants.STATUS_AVAILABLE+"' or r.status is null) ";
        String sql = "select distinct r.* from a1_tag_item a,a1_resource r where a.tid=:tid and a.tgid in"+StringUtil.getSQLInFromInt(tagIds)+" and r.rid=a.rid and (r.status='"+LynxConstants.STATUS_AVAILABLE+"' or r.status is null) ";
        if(!StringUtils.isBlank(keyWord)){
            String s = ResourceQueryKeywordUtil.getKeyWordString(keyWord, paramMap,"r.");
            countSql+=s;
            sql+=s;
        }
        sql=sql + ResourceOrderUtils.buildOrderSql("", order);
        sql= sql+ResourceOrderUtils.buildDivPageSql(begin, maxPageSize);


        int total=this.getNamedParameterJdbcTemplate().queryForObject(countSql, paramMap, Integer.class);
        List<Resource> r = getNamedParameterJdbcTemplate().query(sql, paramMap, resourceRowMapper);
        PaginationBean<Resource> result =new PaginationBean<Resource>();
        result.setBegin(begin);
        result.setData(r);
        result.setSize(maxPageSize);
        result.setTotal(total);
        return result;
    }

}
