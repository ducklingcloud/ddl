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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.impl.ResourcePathDAO;
import net.duckling.ddl.util.AbstractBaseDAO;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.TeamQueryUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
/**
 * resource 目录查询dao，
 * 用于resource的目录关系查询
 * @author zhonghui
 *
 */
@Repository
public class ResourcePathDAOImpl extends AbstractBaseDAO implements ResourcePathDAO {
	private RowMapper<Resource> resourceRowMapper = new ResourceRowMapper("r.");
	
	private static final String QUERY_CHILDREN_SIZE="select count(r.rid) as c from a1_resource r,ddl_folder_path p where r.rid=p.rid and r.tid=:tid and p.tid=:tid and status='"+LynxConstants.STATUS_AVAILABLE+"' and p.length=1";
	private static final String QUERY_CHILDREN="select r.* from a1_resource r,ddl_folder_path p where r.rid=p.rid and r.tid=:tid and status='"+LynxConstants.STATUS_AVAILABLE+"' and p.tid=:tid and p.length=1";
	private static final String QUERY_PARENT="select r.* from a1_resource r where r.rid=(select p.ancestor_rid from ddl_folder_path p where p.rid=? and p.length=1)";
	private static final String QUERY_PATH = "select r.* from a1_resource r ,ddl_folder_path p where r.rid=p.ancestor_rid and p.rid=? order by length desc";
	private static final String QUERY_FILENAME ="select r.* from a1_resource r INNER JOIN ddl_folder_path p on r.tid=p.tid and r.rid=p.rid"+
			 " where r.tid=? and p.length=1 and r.status='"+LynxConstants.STATUS_AVAILABLE+"' and r.title like ? and p.ancestor_rid=? ";
	private static final String QUERY_CHILDREN_FOLDER = "select r.* from a1_resource r INNER JOIN ddl_folder_path p on r.tid=p.tid and r.rid=p.rid" +
			 " where r.tid=? and p.length=1 and r.status='"+LynxConstants.STATUS_AVAILABLE+"' and p.ancestor_rid=? and item_type='"+LynxConstants.TYPE_FOLDER+"' order by r.last_edit_time desc";
	private static final String QUERY_CHILDREN_NO_PAGINATION = "select r.* from a1_resource r INNER JOIN ddl_folder_path p on r.tid=p.tid and r.rid=p.rid" +
			 " where r.tid=? and p.length=1 and r.status='"+LynxConstants.STATUS_AVAILABLE+"' and p.ancestor_rid=? order by r.last_edit_time desc";
	private static final String QUERY_DESCENDANTS="select r.* from a1_resource r,ddl_folder_path p where p.tid =? and p.tid=r.tid and (status='"+LynxConstants.STATUS_AVAILABLE+"' ) and" +
			" r.rid=p.rid and p.ancestor_rid=? order by p.length desc";
	
	@Override
	public PaginationBean<Resource> getChildren(int tid,int rid,String fileType,String order,int begin, int size, String keyWord) {
		String countSql=QUERY_CHILDREN_SIZE;
		String sql = QUERY_CHILDREN;
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("tid", tid);
		
		if(StringUtils.isNotBlank(keyWord)){
			String keyworkCond = " and lcase(r.title) like :keyWord ";
			countSql+=keyworkCond;
			sql+=keyworkCond;
			paramMap.put("keyWord",  "%"+keyWord.toLowerCase()+"%");
		}
		if(LynxConstants.SEARCH_TYPE_PICTURE.equals(fileType)){
			StringBuilder sb = new StringBuilder();
			String[] types = TeamQueryUtil.convertType(LynxConstants.SEARCH_TYPE_PICTURE);
			sb.append(" and ( r.item_type = :item_type and ");
			paramMap.put("item_type", types[0]);
			
			StringBuilder temp = new StringBuilder();
			temp.append(" (");
			for(int i=1;i<types.length;i++){
				temp.append(" r.file_type = :file_type" + i + " or  ");
				paramMap.put("file_type" + i, types[i]);
			}
			String subStr = temp.toString();
			subStr = subStr.substring(0,subStr.length()-"or  ".length());
			subStr = subStr + "))";
			sb.append(subStr);
			
			countSql+=sb.toString();
			sql+=sb.toString();
		}

		if(LynxConstants.TYPE_FILE.equals(fileType)){
			countSql+=" and r.item_type= :itemTypeDFile ";
			sql+=" and r.item_type= :itemTypeDFile ";
			paramMap.put("itemTypeDFile", LynxConstants.TYPE_FILE);
		}else if(LynxConstants.SEARCH_TYPE_EXCEPTFOLDER.equals(fileType)){
			countSql+=" and (item_type= :itemTypeDPage or item_type= :itemTypeDFile)";
			sql+=" and (item_type= :itemTypeDPage or item_type= :itemTypeDFile)";
			paramMap.put("itemTypeDPage", LynxConstants.TYPE_PAGE);
			paramMap.put("itemTypeDFile", LynxConstants.TYPE_FILE);
		}else{
			paramMap.put("rid", rid);
			countSql += " and p.ancestor_rid=:rid";
			sql += " and p.ancestor_rid=:rid";
		}
		int i =this.getNamedParameterJdbcTemplate().queryForObject(countSql, paramMap, Integer.class);
		sql+=ResourceOrderUtils.buildOrderSql("r",order);
		sql+=ResourceOrderUtils.buildDivPageSql(begin,size);
		List<Resource> resutl = this.getNamedParameterJdbcTemplate().query(sql,paramMap, resourceRowMapper);
		PaginationBean<Resource> r = new PaginationBean<Resource>();
		r.setBegin(begin);
		r.setTotal(i);
		r.setData(resutl);
		r.setSize(size);
		return r;
	}
	
	@Override
	public Resource getParent(int rid) {
		List<Resource> result=getJdbcTemplate().query(QUERY_PARENT, new Object[]{rid}, resourceRowMapper);
		if(result!=null&&result.size()>0){
			return result.get(0);
		}
		return null;
	}

	@Override
	public List<Resource> getPath(int rid) {
		return getJdbcTemplate().query(QUERY_PATH, new Object[]{rid}, resourceRowMapper);
	}

	@Override
	public List<Resource> getFolderByStartName(int tid, int parentRid, String queryName, String itemType) {
		String sql = QUERY_FILENAME;
		List<Object> params = new ArrayList<Object>();
		params.add(tid);
		params.add(queryName);
		params.add(parentRid);
		if(itemType!=null){
			sql += "and r.item_type=?";
			params.add(itemType);
		}
		return getJdbcTemplate().query(sql, params.toArray(new Object[0]), resourceRowMapper);
	}
	
	@Override
	public List<Resource> getChildrenFolder(int tid, int rid) {
		return getJdbcTemplate().query(QUERY_CHILDREN_FOLDER, new Object[]{tid,rid}, resourceRowMapper);
	}
	
	@Override
	public List<Resource> getResourceByName(int tid, int parentRid, String itemType, String name) {
		String sql = "select * from a1_resource r where tid=? and bid=? and item_type=? and title=? and status='"+LynxConstants.STATUS_AVAILABLE+"'";
		return getJdbcTemplate().query(sql, new Object[]{tid,parentRid, itemType,name},resourceRowMapper);
	}
	
	@Override
	public List<Resource> getResourceByName(int tid, int parentRid, String name) {
		String sql = "select * from a1_resource r where tid=? and bid=? and item_type!='"+ LynxConstants.TYPE_PAGE +"' and title=? and status='"+LynxConstants.STATUS_AVAILABLE+"'";
		return getJdbcTemplate().query(sql, new Object[]{tid,parentRid,name},resourceRowMapper);
	}

	@Override
	public List<Resource> getResourceByTitle(int tid, int parentRid, String title) {
		String sql = "select * from a1_resource r where tid=? and bid=? and title=? and status='" + LynxConstants.STATUS_AVAILABLE + "'";
		return getJdbcTemplate().query(sql, new Object[]{tid, parentRid, title}, resourceRowMapper);
	}

	@Override
	public List<Resource> getDescendants(int tid, int rid) {
		return getJdbcTemplate().query(QUERY_DESCENDANTS, new Object[]{tid,rid}, resourceRowMapper);
	}

	@Override
	public List<Resource> getChildren(int tid, int rid) {
		return getJdbcTemplate().query(QUERY_CHILDREN_NO_PAGINATION, new Object[]{tid,rid}, resourceRowMapper);
	}

	@Override
	public PaginationBean<Resource> searchResource(int tid, int ancesorRid, String keyWord, String order, int offset,
			int size) {
		String sql = " from a1_resource r , ddl_folder_path p where r.tid=:tid and r.rid=p.rid and p.ancestor_rid=:ancesorRid and r.status='"+LynxConstants.STATUS_AVAILABLE+"' and p.length >0 ";
		sql = sql+" and (lcase(title) like :keyWord or lcase(last_editor_name) like :keyWord or lcase(tags) like :keyWord) ";
		String countSql = "select count(r.rid) "+sql;
		String querySql = "select r.* "+sql+ResourceOrderUtils.buildOrderSql("order_type asc,r",order)+ResourceOrderUtils.buildDivPageSql(offset, size);
		Map<String,Object> queryParam = new HashMap<String,Object>();
		queryParam.put("tid", tid);
		queryParam.put("ancesorRid", ancesorRid);
		queryParam.put("keyWord",  "%"+keyWord.toLowerCase()+"%");
		Integer count = getNamedParameterJdbcTemplate().queryForObject(countSql, queryParam, Integer.class);
		List<Resource> data = getNamedParameterJdbcTemplate().query(querySql, queryParam, resourceRowMapper);
		PaginationBean<Resource> result = new PaginationBean<Resource>();
		result.setBegin(offset);
		result.setData(data);
		result.setTotal(count==null?0:count);
		result.setSize(size);
		return result;
	}

	@Override
	public List<Resource> getPathsByTitle(int tid, String folderTitle){
		String sql = "select r.* from (select fp2.ancestor_rid,fp2.length from a1_resource as r2 INNER JOIN ddl_folder_path fp2"
				+ " on fp2.rid = r2.rid where r2.tid=? and r2.item_type='"+LynxConstants.TYPE_FOLDER
				+ "' and r2.title=? and r2.`status`='" + LynxConstants.STATUS_AVAILABLE
				+ "' ORDER BY fp2.rid desc, fp2.length desc) fp inner join a1_resource r on fp.ancestor_rid = r.rid and fp.length>=0";
		return getJdbcTemplate().query(sql, new Object[]{tid, folderTitle},resourceRowMapper);
	}

}
