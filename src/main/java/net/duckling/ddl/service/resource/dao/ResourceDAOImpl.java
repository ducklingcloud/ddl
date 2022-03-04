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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.rowset.serial.SerialBlob;

import net.duckling.common.util.CommonUtils;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.SimpleResource;
import net.duckling.ddl.service.resource.impl.ResourceDAO;
import net.duckling.ddl.service.tobedelete.PageContentRender;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.ResourceQuery.QueryString;
import net.duckling.ddl.util.ResourceQueryKeywordUtil;
import net.duckling.ddl.util.SQLObjectMapper;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.util.TeamQueryUtil;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceDAOImpl extends AbstractBaseDAO implements ResourceDAO {
    private static final String SQL_CREATE =
            "INSERT INTO a1_resource (tid,item_type,title,creator,"+
            "create_time,last_editor,last_editor_name,last_edit_time,"+
            "last_version,tags,file_type,marked_users,bid,order_type,"+
            "status,size,shared)"+
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    //modify by lvly@2012-07-20
    private static final String SQL_DELETE = "update a1_resource set tags='',marked_users='',status='"+LynxConstants.STATUS_DELETE+"'";
    private static final String SQL_UPDATE = "update a1_resource set title=?,last_editor=?,last_editor_name=?, last_edit_time=?," +
            " last_version=?,file_type=?,bid=? ,status=?,size=?";
    private static final String SQL_QUERY = "select * from a1_resource";
    private static final String BY_IDTIDTYPE = " where rid=? and tid=? and item_type=?";
    private static final String BY_TYPE_TID = " where tid=? and item_type=?";
    private static final String BY_SPHINX_ID = " where rid = ?";

    private static final Logger LOG = Logger.getLogger(ResourceDAOImpl.class);
    private RowMapper<Resource> resourceRowMapper = new ResourceRowMapper("");

    @Override
    public List<Resource> query(TeamQuery q){
        return getJdbcTemplate().query(TeamQueryUtil.buildDynamicSQL(q),resourceRowMapper);
    }

    @Override
    public PaginationBean<Resource> query(ResourceQuery q){
        QueryString qs =q.toQueryString();
        Integer count = getNamedParameterJdbcTemplate().queryForObject(qs.getCountString(), qs.getParamMap(), Integer.class);
        RowMapper<Resource> resourceRowMapper = new ResourceRowMapper(q.getTableAlias());
        List<Resource> rs = getNamedParameterJdbcTemplate().query(qs.getQueryString(), qs.getParamMap(), resourceRowMapper);
        PaginationBean<Resource> result = new PaginationBean<Resource>();
        result.setBegin(q.getOffset());
        result.setData(rs);
        result.setSize(q.getSize());
        result.setTotal(count==null?0:count);
        return result;
    }

    @Override
    public synchronized int create(final Resource res) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        this.getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                    Connection conn) throws SQLException {
                    PreparedStatement ps = null;
                    ps = conn.prepareStatement(
                        SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    int i = 0;
                    // tid, item_type, title, creator, create_time, 
                    ps.setInt(++i, res.getTid());
                    ps.setString(++i, res.getItemType());
                    ps.setString(++i, res.getTitle());
                    ps.setString(++i, res.getCreator());
                    ps.setTimestamp(++i, new Timestamp(
                        res.getCreateTime().getTime()));
                    // last_editor, last_editor_name, last_edit_time, 
                    ps.setString(++i, res.getLastEditor());
                    ps.setString(++i, res.getLastEditorName());
                    ps.setTimestamp(++i, new Timestamp(
                        res.getLastEditTime().getTime()));
                    // last_version, tags, file_type, marked_users,
                    ps.setInt(++i, res.getLastVersion());
                    ps.setString(++i,
                                 JsonUtil.getJSONString(res.getTagMap()));
                    String fileType = (res.getFileType()!=null) ?
                            res.getFileType().toLowerCase() : null;
                    ps.setString(++i, fileType);
                    SerialBlob blobdata = new SerialBlob(
                        SQLObjectMapper.getBytes(res.getMarkedUserSet()));
                    ps.setBlob(++i, blobdata);
                    // bid, order_type, status, size, shared
                    ps.setInt(++i, res.getBid());
                    ps.setInt(++i, res.getOrderType());
                    ps.setString(++i, res.getStatus());
                    ps.setLong(++i, res.getSize());
                    ps.setBoolean(++i,res.isShared());
                    return ps;
                }
            }, keyHolder);
        Number key = keyHolder.getKey();
        return (key==null) ? -1 : key.intValue();
    }

    @Override
    public int delete(int rid, int tid) {
        return this.getJdbcTemplate().update(SQL_DELETE+" where rid=? and tid=? ", new Object[]{rid, tid});
    }

    @Override
    public int batchDelete(final List<Integer> rids){
        if(null == rids || rids.isEmpty()){
            return 0;
        }
        String sql = "update a1_resource set status='"+LynxConstants.STATUS_DELETE+"' where rid in(";
        int size = rids.size();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<size; i++){
            sb.append(rids.get(i)+",");
        }
        sb.replace(sb.lastIndexOf(","), sb.length(), ")");
        sql += sb.toString();
        return this.getJdbcTemplate().update(sql);
    }

    @Override
    public int update(Resource r) {
        String fileType = (r.getFileType()!=null)?r.getFileType().toLowerCase():r.getFileType();
        return this.getJdbcTemplate().update(SQL_UPDATE+BY_IDTIDTYPE, new Object[]{
                r.getTitle(), r.getLastEditor(),r.getLastEditorName(), r.getLastEditTime(), r.getLastVersion(),
                fileType,r.getBid(),r.getStatus(),r.getSize(), r.getRid(),r.getTid(),r.getItemType()});
    }
    @Override
    public void update(final List<Resource> res) {
        getJdbcTemplate().batchUpdate(SQL_UPDATE+BY_IDTIDTYPE, new BatchPreparedStatementSetter(){

                @Override
                public void setValues(PreparedStatement ps, int index) throws SQLException {
                    Resource r = res.get(index);
                    int i = 0;
                    ps.setString(++i, r.getTitle());
                    ps.setString(++i, r.getLastEditor());
                    ps.setString(++i, r.getLastEditorName());
                    ps.setTimestamp(++i, new Timestamp(r.getLastEditTime().getTime()));
                    ps.setInt(++i, r.getLastVersion());
                    ps.setString(++i, r.getFileType());
                    ps.setInt(++i, r.getBid());
                    ps.setString(++i, r.getStatus());
                    ps.setLong(++i, r.getSize());
                    ps.setInt(++i, r.getRid());
                    ps.setInt(++i, r.getTid());
                    ps.setString(++i, r.getItemType());
                    ps.setBoolean(i++, r.isShared());
                }

                @Override
                public int getBatchSize() {
                    return res.size();
                }

            });
    }

    @Override
    public Resource getResourceById(int rid, int tid) {
        List<Resource> list = getJdbcTemplate().query(SQL_QUERY+" where rid=? and tid=? ", new Object[]{rid, tid},resourceRowMapper);
        if(null==list || list.size()==0){
            return null;
        } else if(list.size()>1){
            LOG.error("there exist more than one object while quering for Resource by rid = "+rid+" and tid = "+tid);
        }
        return list.get(0);
    }

    @Override
    public Resource getResource(int rid){
        List<Resource> list = getJdbcTemplate().query(SQL_QUERY+BY_SPHINX_ID, new Object[]{rid}, resourceRowMapper);
        if(null==list || list.size()<=0)
        {
            return null;
        }
        else if(list.size()>1){
            LOG.error("there exist more than one object while quering for Resource " +
                      "by rid = "+rid);
        }
        return list.get(0);
    }

    @Override
    public List<Resource> getUnBundleResource(int bid, int tid, String title, int offset, int size){
        String sql="select * from a1_resource where tid=" + tid +
                " and bid=0 and item_id <> " + bid + " and ( status = '" + LynxConstants.STATUS_AVAILABLE+ "') ";
        if(null != title && !"".equals(title)){
            sql = sql+" and title like '%"+title+"%'";
        }
        sql = sql + " order by last_edit_time desc limit "+offset+", "+size;
        return this.getJdbcTemplate().query(sql, resourceRowMapper);
    }

    @Override
    public List<Resource> getResourceByTypeAndTid(int tid, String type) {
        if(!Resource.isSupportedType(type)){
            LOG.error("Unsupported Resource Type! type = "+type);
            return null;
        }
        return this.getJdbcTemplate().query(SQL_QUERY+BY_TYPE_TID, new Object[]{tid, type}, resourceRowMapper);
    }

    private static final String UPDATE_TAG_MAP = "update a1_resource set tags=? where tid=? and rid=? ";
    @Override
    public void updateTagMap(final List<Resource> resList) {
        this.getJdbcTemplate().batchUpdate(UPDATE_TAG_MAP, new BatchPreparedStatementSetter(){

                @Override
                public int getBatchSize() {
                    return (null==resList || resList.isEmpty())?0:resList.size();
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    Resource res = resList.get(index);
                    String tagmap = JsonUtil.getJSONString(res.getTagMap());
                    int i=0;
                    ps.setString(++i, tagmap);
                    ps.setInt(++i, res.getTid());
                    ps.setInt(++i, res.getRid());
                }

            });
    }

    private static final String UPDATE_USER_SET = "update a1_resource set marked_users=? where rid=?";
    @Override
    public int updateMarkedUserSet(final List<Resource> resList){
        this.getJdbcTemplate().batchUpdate(UPDATE_USER_SET, new BatchPreparedStatementSetter(){

                @Override
                public int getBatchSize() {
                    return (null == resList || resList.isEmpty())?0:resList.size();
                }

                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    Resource res = resList.get(index);
                    int i=0;
                    ps.setObject(++i, res.getMarkedUserSet());
                    ps.setInt(++i, res.getRid());
                }

            });
        return 1;
    }

    private static final String UPDATE_BID = "update a1_resource set bid=? where rid=?";
    @Override
    public int updateBid(final int bid,final List<Long> rids){
        this.getJdbcTemplate().batchUpdate(UPDATE_BID, new BatchPreparedStatementSetter(){
                @Override
                public int getBatchSize() {
                    return (null!=rids && !rids.isEmpty())?rids.size():0;
                }
                @Override
                public void setValues(PreparedStatement ps, int index)
                        throws SQLException {
                    long rid = rids.get(index);
                    int i = 0;
                    ps.setInt(++i, bid);
                    ps.setInt(++i, (int)rid);
                }

            });
        return 1;
    }

    private String buildSelectInSQL(List<Long> ids, String sql) {
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        for (Long id : ids) {
            sb.append(id.toString());
            sb.append(",");
        }
        int length = sb.toString().length();
        return sb.substring(0, length - 1) + ")";
    }

    private String buildBatchQuerySQL(List<Long> ids) {
        String sql = "select * from a1_resource where ( status!='"+LynxConstants.STATUS_DELETE+"') and rid in (";
        return buildSelectInSQL(ids, sql);
    }


    private List<Resource> maintainOrderAsID(List<Long> ids,List<Resource> dataSource){
        Map<Long,Resource> tempMap = new HashMap<Long,Resource>();
        for(Resource instance:dataSource) {
            tempMap.put(Long.valueOf(instance.getRid()), instance);
        }
        List<Resource> sortList = new ArrayList<Resource>();
        for(Long id : ids) {
            if(tempMap.containsKey(id)){
                sortList.add(tempMap.get(id));
            }
        }
        return sortList;
    }

    @Override
    public List<Resource> getResourceByRids(List<Long> ids) {
        if(ids==null || ids.size()==0){
            return new ArrayList<Resource>();
        }
        List<Resource> src =getJdbcTemplate().query(buildBatchQuerySQL(ids), resourceRowMapper);
        return maintainOrderAsID(ids, src);
    }

    @Override
    public List<Resource> getStarmarkResource(String uid, int tid) {
        String sql = "select r.* from a1_resource r, a1_starmark s where r.rid = s.rid and s.uid=? and s.tid=?";
        return this.getJdbcTemplate().query(sql, new Object[]{uid,tid}, resourceRowMapper);
    }

    @Override
    public List<SimpleResource> getSimpleResourceByTeam(int tid) {
        String sql = "select rid,tid,item_id,item_type from a1_resource where tid=?";
        return this.getJdbcTemplate().query(sql, new Object[]{tid},simpleMapper);
    }

    private RowMapper<SimpleResource> simpleMapper = new RowMapper<SimpleResource>(){
            @Override
            public SimpleResource mapRow(ResultSet rs, int index) throws SQLException {
                SimpleResource r = new SimpleResource();
                r.setRid(rs.getInt("rid"));
                r.setTid(rs.getInt("tid"));
                r.setItemType(rs.getString("item_type"));
                return r;
            }
        };

    @Override
    public Set<String> getStarmarkOfResources(List<Long> rids){
        if(null==rids || rids.size()<=0){
            return new HashSet<String>();
        }
        Set<String> result = new HashSet<String>();
        for(long rid : rids){
            Resource resource= getResource((int)rid);
            if(resource!=null&&!resource.getMarkedUserSet().isEmpty()){
                result.addAll(resource.getMarkedUserSet());
            }
        }
        return result;
    }

    @Override
    public List<Resource> getAllResource() {
        String sql = "select * from a1_resource";
        return this.getJdbcTemplate().query(sql,resourceRowMapper);
    }

    @Override
    public String getAllBundleItemFileType(int bid, int tid){
        String sql = "select group_concat(distinct(file_type)) from a1_resource where bid=? and tid=? and file_type is not null";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{bid, tid}, String.class);
    }

    @Override
    public void updateBundleFileType(int bid, int tid, String fileType) {
        String sql = "update a1_resource set file_type = ? where item_id = ? and tid = ? and item_type='Bundle'";
        this.getJdbcTemplate().update(sql, new Object[]{fileType, bid, tid});
    }

    @Override
    public List<Resource> queryReferableFiles(String keyword, int tid) {
        String sql = "select * from a1_resource where item_type = 'DFile' and tid=? and status is null and title like ? ";
        return this.getJdbcTemplate().query(sql,new Object[]{tid,"%"+keyword+"%"}, resourceRowMapper);
    }

    @Override
    public void updateOrderColumn(List<Resource> itemResList) {
        if(null == itemResList || itemResList.isEmpty()){
            return;
        }
        String sql = "update a1_resource set order_title=:title, order_date=:date where rid=:rid";
        Map<String, Object>[] params = buildParameters(itemResList);
        this.getNamedParameterJdbcTemplate().batchUpdate(sql, params);
    }

    private Map<String, Object>[] buildParameters(List<Resource> resources) {
        int len = (null != resources) ? resources.size() : 0;
        @SuppressWarnings("unchecked")
            HashMap<String, Object>[] maps = new HashMap[len];
        if (null != resources && resources.size() > 0) {
            for (int i = 0; i < len; i++) {
                Resource resource = resources.get(i);
                maps[i] = new HashMap<String, Object>();
                maps[i].put("title", resource.getOrderTitle());
                maps[i].put("date", resource.getOrderDate());
                maps[i].put("rid", resource.getRid());
            }
        }
        return maps;
    }

    @Override
    public List<Resource> getResources(Collection<Integer> rids, int tid, String itemType) {
        String ins =StringUtil.getSQLInFromInt(rids);
        String sql = "select * from a1_resource where item_type = '"+itemType+"' and tid="+tid+" and rid in"+ins;
        return this.getJdbcTemplate().query(sql, resourceRowMapper);
    }

    @Override
    public int queryReferableFilesCount(String keyword, int[] tid) {
        if(tid!=null&&tid.length>0){
            String sql = null;
            if(tid.length==1){
                sql = "select count(1) from a1_resource where item_type = 'DFile' and tid=? and  status ='"+LynxConstants.STATUS_AVAILABLE+"' and title like ? ";
                return this.getJdbcTemplate().queryForObject(sql,new Object[]{tid[0],"%"+keyword+"%"}, Integer.class );
            }else{
                sql = "select count(1) from a1_resource where item_type = 'DFile' and tid in "+StringUtil.getSQLInFromInt(tid)+" and status ='"+LynxConstants.STATUS_AVAILABLE+"' and title like ? ";
                return this.getJdbcTemplate().queryForObject(sql, new Object[]{"%"+keyword+"%"},Integer.class);
            }
        }
        return 0;
    }

    @Override
    public List<Resource> queryReferableFiles(String keyword, int[] tid, int offset, int size) {
        if(tid!=null&&tid.length>0){
            if(tid.length==1){
                String sql = "select * from a1_resource where item_type = 'DFile' and tid=? and  status ='"+LynxConstants.STATUS_AVAILABLE+"' and title like ? order by last_edit_time desc limit ?,? ";
                return this.getJdbcTemplate().query(sql, new Object[]{tid[0],"%"+keyword+"%",offset,size}, resourceRowMapper);
            }else{
                String sql = "select * from a1_resource where item_type = 'DFile' and tid in "+StringUtil.getSQLInFromInt(tid)+" and status ='"+LynxConstants.STATUS_AVAILABLE+"' and title like ? order by last_edit_time desc limit ?,? ";
                return this.getJdbcTemplate().query(sql, new Object[]{"%"+keyword+"%",offset,size}, resourceRowMapper);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<Resource> getDDoc(int tid, List<Integer> rids) {
        String sql = "select * from a1_resource where tid=? and item_type='"+LynxConstants.TYPE_PAGE+"' and rid in "+StringUtil.getSQLInFromInt(rids);
        return getJdbcTemplate().query(sql, new Object[]{tid},resourceRowMapper);
    }

    private static final String SQL_QUERY_STARTNAME = "select * from a1_resource where tid=? and title like ? and status='"+LynxConstants.STATUS_AVAILABLE+"' and item_type='"+LynxConstants.TYPE_FILE+"'";

    @Override
    public List<Resource> getFileByStartName(int tid, String name) {
        return getJdbcTemplate().query(SQL_QUERY_STARTNAME, new Object[]{tid,name}, resourceRowMapper);
    }
    @Override
    public PaginationBean<Resource> getMyCreatedFiles(int tid, String uId,
                                                      int offset, int size, String order, String keyWord) {
        Map<String,Object> paramMap=new HashMap<String,Object>();
        paramMap.put("tid", tid);
        paramMap.put("uid", uId);
        paramMap.put("item_type", LynxConstants.TYPE_FOLDER);
        //获得总数
        String countSql="select count(*) from a1_resource where tid=:tid and creator=:uid and item_type!=:item_type and (status = '" + LynxConstants.STATUS_AVAILABLE+ "' or status='"+LynxConstants.STATUS_UNPUBLISH+"') ";

        String sql ="select * from a1_resource where tid=:tid and creator=:uid and item_type!=:item_type and (status = '" + LynxConstants.STATUS_AVAILABLE+ "' or status='"+LynxConstants.STATUS_UNPUBLISH+"') ";

        if (! CommonUtils.isBlank(keyWord)) {
            String s = ResourceQueryKeywordUtil.getKeyWordString(keyWord, paramMap,"");
            countSql+=s;
            sql+=s;
            paramMap.put("keyWord", "%"+keyWord.toLowerCase()+"%");
        }

        sql+=ResourceOrderUtils.buildOrderSql("",order);
        sql+=ResourceOrderUtils.buildDivPageSql(offset,size);

        int total=this.getNamedParameterJdbcTemplate().queryForObject(countSql, paramMap, Integer.class);
        PaginationBean<Resource> result =new PaginationBean<Resource>();
        result.setData(getNamedParameterJdbcTemplate().query(sql, paramMap, resourceRowMapper));
        result.setBegin(offset);
        result.setSize(size);
        result.setTotal(total);
        return result ;
    }

    @Override
    public PaginationBean<Resource> getTeamRecentChange(int tid,int offset, int size, String keyWord, String order) {
        Map<String,Object> paramMap=new HashMap<String,Object>();
        paramMap.put("tid", tid);
        //获得总数
        String countSql="select count(*) FROM a1_resource r WHERE r.tid=:tid AND (r.item_type='DFile' OR r.item_type='DPage') and r.status = '" + LynxConstants.STATUS_AVAILABLE+ "' ";

        String sql ="SELECT * FROM a1_resource r WHERE r.tid=:tid AND (r.item_type='DFile' OR r.item_type='DPage') and  r.status = '" + LynxConstants.STATUS_AVAILABLE+ "' ";

        if (! CommonUtils.isBlank(keyWord)) {
            String s = ResourceQueryKeywordUtil.getKeyWordString(keyWord, paramMap,"r.");
            countSql+=s;
            sql+=s;
        }
        sql+=ResourceOrderUtils.buildOrderSql("",order);
        sql+=ResourceOrderUtils.buildDivPageSql(offset,size);

        int total=this.getNamedParameterJdbcTemplate().queryForObject(countSql, paramMap, Integer.class);
        PaginationBean<Resource> result =new PaginationBean<Resource>();
        result.setData(getNamedParameterJdbcTemplate().query(sql, paramMap, resourceRowMapper));
        result.setBegin(offset);
        result.setSize(size);
        result.setTotal(total);
        return result ;
    }


    @Override
    public PaginationBean<Resource> getMyRecentFiles(int tid, String uId,
                                                     int offset, int size, String order) {
        return null;
    }

    @Override
    public List<Resource> getFileByTitle(int tid, int parentRid, String title) {
        String sql = "select * from a1_resource where tid=? and bid=? and title=? and item_type='"+LynxConstants.TYPE_FILE+"' and  status ='"+LynxConstants.STATUS_AVAILABLE+"' ";
        return getJdbcTemplate().query(sql, new Object[]{tid,parentRid,title}, resourceRowMapper);
    }
    @Override
    public List<Resource> getResourceByTitle(int tid, int parentRid, String type, String title) {
        String sql = "select * from a1_resource where tid=? and bid=? and title=? and item_type=? and  status ='"+LynxConstants.STATUS_AVAILABLE+"' ";
        return getJdbcTemplate().query(sql, new Object[]{tid,parentRid,title,type}, resourceRowMapper);
    }

    @Override
    public List<Resource> getResourceByTitle(int tid, int parentRid, String type, String title,String status) {
        String sql = "select * from a1_resource where tid=? and bid=? and title=? and item_type=? ";
        if(status != null){
            sql += " and  status ='"+status+"' ";
        }
        return getJdbcTemplate().query(sql, new Object[]{tid,parentRid,title,type}, resourceRowMapper);
    }

    @Override
    public List<Resource> fetchDPageBasicListByPageIncrementId(List<Long> pageIds) {
        if(null == pageIds || pageIds.size()<=0){
            return null;
        }

        String idsStr = buildSphinxIdsSQL(pageIds);
        String orderStr = idsStr.replace("(", "(rid,");
        String extend = " where rid in "+ idsStr + "and item_type='"+LynxConstants.TYPE_PAGE+"' order by field" + orderStr;
        return getJdbcTemplate().query(SQL_QUERY+extend, resourceRowMapper);
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
    public List<PageContentRender> fetchDPageContentByIncrementId(List<Long> rids) {
        String sql = "select a.rid as rid, a.tid as tid, a.content as content from a1_page_version a" +
                " inner join a1_resource b on a.rid=b.rid and a.tid=b.tid and a.version=b.last_version ";
        if(null == rids || rids.size()<=0){
            return null;
        }
        String idsStr = buildSphinxIdsSQL(rids);
        String orderStr = idsStr.replace("(", "(b.rid,");
        String extend = " where b.rid in "+ idsStr + " order by field" + orderStr;
        return getJdbcTemplate().query(sql+extend,contentRowMapper);
    }

    private RowMapper<PageContentRender> contentRowMapper = new RowMapper<PageContentRender>(){

            @Override
            public PageContentRender mapRow(ResultSet rs, int index) throws SQLException {
                PageContentRender pcr = new PageContentRender();
                pcr.setContent(rs.getString("content"));
                pcr.setId(rs.getInt("rid"));
                pcr.setTid(rs.getInt("tid"));
                return pcr;
            }

        };

    @Override
    public PaginationBean<Resource> getResourceByFileType(int tid, String type, int offset, int size, String order,String keyWord) {
        String where = " where tid=:tid and item_type=:itemType  ";
        String[] types = TeamQueryUtil.convertType(type);
        if(types.length>1){
            StringBuilder sb = new StringBuilder(" and (");
            for(int i=1;i<types.length;i++){
                sb.append("file_type like '%"+types[i]+"%' or ");
            }
            sb.delete(sb.length()-4, sb.length());
            sb.append(")");
            where = where+sb.toString();
        }
        Map<String,Object> queryParam = new HashMap<String,Object>();
        queryParam.put("tid", tid);
        queryParam.put("itemType", types[0]);
        if (! CommonUtils.isEmpty(keyWord)) {
            String s = ResourceQueryKeywordUtil.getKeyWordString(keyWord, queryParam,"");
            where = where +s;
        }
        where = where +" and status='"+LynxConstants.STATUS_AVAILABLE+"'";
        Integer total = getNamedParameterJdbcTemplate().queryForObject("select count(rid) from a1_resource"+where, queryParam, Integer.class);
        String querySql="select * from a1_resource "+where+ResourceOrderUtils.buildOrderSql("",order)+ResourceOrderUtils.buildDivPageSql(offset, size);
        List<Resource> resources = getNamedParameterJdbcTemplate().query(querySql, queryParam, resourceRowMapper);
        PaginationBean<Resource> result =new PaginationBean<Resource>();
        result.setData(resources);
        result.setBegin(offset);
        result.setEnd(offset+size);
        result.setSize(size);
        result.setTotal(total==null?0:total);
        return result;
    }
    @Override
    public int getTeamResourceAmount(int tid) {
        String sql = "select count(*) from a1_resource where tid =? and status='"+LynxConstants.STATUS_AVAILABLE+"'";
        return getJdbcTemplate().queryForObject(sql,new Object[]{tid},Integer.class);
    }
    @Override
    public long getTeamResourceSize(int tid){
        //查询小于8K的资源数量
        String countSizeMin = "select count(1) from a1_resource where tid =? and status='"+LynxConstants.STATUS_AVAILABLE
                +"' and size <" + Resource.SIZE_MIN_OCCUPIED;
        Integer amount = getJdbcTemplate().queryForObject(countSizeMin, new Object[]{tid}, Integer.class);
        if(amount==null){
            amount = 0;
        }
        //统计大于8k的文件占用
        String sql = "select sum(size) from a1_resource where tid=? and status='"+LynxConstants.STATUS_AVAILABLE+
                "' and size >=" + Resource.SIZE_MIN_OCCUPIED;
        Long n = getJdbcTemplate().queryForObject(sql, new Object[]{tid}, Long.class);
        if(n==null){
            n=0L;
        }
        Long amountSizeMin = amount * Resource.SIZE_MIN_OCCUPIED;
        return amountSizeMin + n;
    }
    @Override
    public void updateResourceStatus(Collection<Integer> rids, String status) {
        String sql = "update a1_resource set status='"+status+"' where rid in" +StringUtil.getSQLInFromInt(rids);
        getJdbcTemplate().update(sql);
    }

    @Override
    public void updateShared(Integer rid, boolean shared) {
        int s = shared == true ? 1 : 0;
        String sql = "update a1_resource set shared="+ s +" where rid =" +rid;
        getJdbcTemplate().update(sql);
    }

}
