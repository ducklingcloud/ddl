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
package net.duckling.ddl.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;

import org.apache.commons.lang.StringUtils;

/**
 * 搜索resource，4.0后添加,
 * 用于团队内部resource搜索
 * @author zhonghui
 *
 */
public abstract class ResourceQuery {
	private  static final String ORDER_SORT_TIME="time";
	private  static final String ORDER_SORT_TIME_DESC="timeDesc";
	private  static final String ORDER_SORT_TITLE="title";
	private  static final String ORDER_SORT_TITLE_DESC="titleDesc";
	
	public final static int maxPageSize = 100;
	public final static String ALL = "all";
	public final static int SIZE_ALL = 999999; //返回所有的最大值
	
	private String date; //时间段：今天，昨天 .....
	private String type;  //查询的资源类型item_type
	private int[] tagIds; //标签
	private int offset; //显示偏移量
	private int size ; //每次取的条数（Sphinx）
//	private String tagFilter; //过滤器
	private String sortType;//排序类型，修改时间正序，修改时间逆序，title正序，title逆序
	private String keyword;//关键字搜索
	private int tid;
	private String fileType;
	private String orderType;
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	protected Map<String,Object> params = new HashMap<String,Object>();
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int[] getTagIds() {
		return tagIds;
	}
	public void setTagIds(int[] tagIds) {
		this.tagIds = tagIds;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	public void setKeyword(String keyword){
		this.keyword = keyword;
	}
	public String getKeyword(){
		return keyword;
	}
	abstract String getQuerySelect();
	abstract String getCountSelect();
	abstract String getFrom();
	abstract String getWhere();
	String getOrderString(){
		return buildOrderSql(getTableAlias(), getOrderType());
	}
	protected String getKeyWordString(){
		return ResourceQueryKeywordUtil.getKeyWordString(getKeyword(), params, getTableAlias());
	}
	protected String getFileTypeString(){
		if(StringUtils.isNotEmpty(getFileType())){
			if(LynxConstants.SEARCH_TYPE_PICTURE.equals(fileType)){ //图片类型
				StringBuilder sb = new StringBuilder();
				String[] types = TeamQueryUtil.convertType(LynxConstants.SEARCH_TYPE_PICTURE);
				sb.append(" and ( "+getTableAlias()+"item_type = :item_type and ");
				params.put("item_type", types[0]);
				StringBuilder temp = new StringBuilder();
				temp.append(" (");
				for(int i=1;i<types.length;i++){
					temp.append(" "+getTableAlias()+"file_type = :file_type" + i + " or  ");
					params.put("file_type" + i, types[i]);
				}
				String subStr = temp.toString();
				subStr = subStr.substring(0,subStr.length()-"or  ".length());
				subStr = subStr + "))";
				sb.append(subStr);
				return sb.toString();
			}else if(LynxConstants.SEARCH_TYPE_EXCEPTFOLDER.equals(fileType)){ //去除文件夹
				params.put("itemTypeDPage", LynxConstants.TYPE_PAGE);
				params.put("itemTypeDFile", LynxConstants.TYPE_FILE);
				return " and ("+getTableAlias()+"item_type= :itemTypeDPage or "+getTableAlias()+"item_type= :itemTypeDFile) ";
			} else if(LynxConstants.TYPE_PAGE.equals(fileType)){
				params.put("itemType", LynxConstants.TYPE_PAGE);
				return " and item_type=:itemType ";
			}else if(LynxConstants.SRERCH_TYPE_NOPAGE.equals(fileType)){
				params.put("itemType", LynxConstants.TYPE_PAGE);
				return " and item_type!=:itemType ";
			}else {
				//各种类型，doc，pdf等等
				String[] types = TeamQueryUtil.convertType(fileType);
				if (types != null && types.length > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(" and "+getTableAlias()+"item_type = '" + types[0] + "' ");
					if (types.length > 1) {
						sb.append(" and (");
						for (int i = 1; i < types.length; i++) {
							sb.append(""+getTableAlias()+"file_type like '%" + types[i] + "%' or ");
						}
						//除or
						sb.delete(sb.length() - 4, sb.length());
						sb.append(") ");
					}
					return sb.toString();
				}
			}
		}
		return "";
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType){
		this.fileType = fileType;
	}
	public String getKeyWord() {
		return keyword;
	}
	void addParams(String key,Object value){
		
	}
	public String getTableAlias(){
		return "";
	}
	Map<String,Object> getParamMap(){
		return null;
	}
	public static ResourceQuery buildForQuery(HttpServletRequest request){
		String order=request.getParameter("sortType");
		order=StringUtils.isEmpty(order)?"timeDesc":order;
		return buildForQuery(request, order);
	}
	public static ResourceQuery buildForQuery(HttpServletRequest request,String order){
		ResourceQuery query = null;
		String queryType =request.getParameter("queryType");
		queryType=StringUtils.isEmpty(queryType)?"":queryType;
		switch (queryType) {
		case "myCreate":
			query = new MyCreateQuery(VWBSession.getCurrentUid(request));
			break;
		case "myStarFiles":
			query = new MyStarFilesQuery(VWBSession.getCurrentUid(request));
			break;
		case "teamRecentChange":
			query = new TeamRecentChangeQuery();
			break;
		case "showFileByType":
		case "fileType":
			query = new FileTypeQuery();
			break;
		case "tagQuery":
			String d = request.getParameter("date");
			String all = request.getParameter("tagFilter");
			if("all".equals(all)){
				query = new ResourcePathQuery(getRequestRid(request));
				break;
			}
			if(StringUtils.isNotEmpty(d)){
				query = new ResourceTimeQuery(d);
			}else{
				query = new TagQuery(getTagIds(request));	
			}
			break;
		case "ExceptFolder":
			query = new ExceptFolderQuery();
			break;
		default:
			query = new ResourcePathQuery(getRequestRid(request));
			break;
		}
		query.setSortType(request.getParameter("sortType"));
		query.setKeyword(request.getParameter("keyWord"));
		query.setDate(request.getParameter("date"));
		query.setSize(getMaxSize(request));
		query.setTagIds(WebParamUtil.getIntegerValues(request,"tag"));
		query.setOffset(getInteger(request.getParameter("begin"),0));
		query.setFileType(request.getParameter("fileType"));
		if(StringUtils.isEmpty(query.getFileType())){
			query.setFileType(request.getParameter("type"));
		}
		query.setOrderType(order);
		query.setTid(VWBContext.getCurrentTid());
		return query;
	}
	private static int getRequestRid(HttpServletRequest request){
		String r = request.getParameter("rid");
		int rid = 0;
		if(StringUtils.isEmpty(r)){
			String path = request.getParameter("path");
			if(!StringUtils.isEmpty(path)){
				String[] s=path.split("/");
				try{
					rid = Integer.parseInt(s[s.length-1]);
				}catch(Exception e){}
			}
		}else{
			rid = Integer.parseInt(r);
		}
		return rid;
	}
	private static List<Integer> getTagIds(HttpServletRequest request){
		String tagIdsStr=StringUtils.defaultIfBlank(request.getParameter("tagId"), null);
		if(tagIdsStr==null){
			int[] tagIds = WebParamUtil.getIntegerValues(request,"tag");
			List<Integer> ids = new ArrayList<Integer>();
			for(int i : tagIds){
				ids.add(i);
			}
			return ids;
		}
		String[] tagIdsArray=tagIdsStr.split("_");
		List<Integer> tagIds=new ArrayList<Integer>();
		if(tagIdsArray!=null&&tagIdsArray.length>0){
			for(String tagIdTemp:tagIdsArray){
				tagIds.add(Integer.parseInt(tagIdTemp));
			}
		}
		return tagIds;
	}
	private static int getInteger(String s,int def){
		int result = def;
		try{
			result = Integer.parseInt(s);
		}catch(Exception e){}
		return result;
	}
	private static int getMaxSize(HttpServletRequest request) {
		String size = request.getParameter("maxPageSize");
		if(ALL.equals(size)){
			return SIZE_ALL;
		}
		if(StringUtils.isEmpty(size)){
			return maxPageSize;
		}
		try{
			return Integer.parseInt(size);
		}catch(Exception e){
			return maxPageSize;
		}
	}
	public String toCountString(){
		return null;
	}
	private QueryString queryString;
	public QueryString toQueryString(){
		queryString = new QueryString();
		String where = getWhere();
		String count = getCountSelect()+getFrom()+where;
		String select = getQuerySelect()+getFrom()+where+getOrderString()+getLimit();
		queryString.setCountString(count);
		queryString.setQueryString(select);
		queryString.paramMap =getParamMap();
		return queryString;
	}
	
	
	
	private String getLimit(){
		return " limit "+offset+","+size;
	}

	private String buildOrderSql(String tableAlias, String orderStr) {
		orderStr = StringUtils.isEmpty(orderStr) ? "" : orderStr;
		tableAlias = StringUtils.isEmpty(tableAlias) ? "" : (tableAlias);
		switch (orderStr) {
		case ORDER_SORT_TIME: {
			return " order by " + tableAlias + "order_type asc ,last_edit_time";
		}
		case ORDER_SORT_TIME_DESC: {
			return " order by " + tableAlias + "order_type asc ,last_edit_time desc";
		}
		case ORDER_SORT_TITLE: {
			return " order by " + tableAlias + "order_type asc,convert(title using gb2312)";
		}
		case ORDER_SORT_TITLE_DESC: {
			return " order by " + tableAlias + "order_type asc,convert(title using gb2312) desc";
		}
		default:
			return " order by order_type asc ";
		}
	}

	public static String[] convertType(String type){
		if(null!=type && !"".equals(type)){
			if(LynxConstants.TYPE_PAGE.equals(type) ||
			   LynxConstants.TYPE_FILE.equals(type)){
				return new String[]{type};
			}else if(isOfficeFileTypeForSearch(type)){
				return new String[]{LynxConstants.TYPE_FILE,type,type+"x"};
			}else if(isPdfFileTypeForSearch(type)){
				return new String[]{LynxConstants.TYPE_FILE,type};
			}else if(LynxConstants.SEARCH_TYPE_PICTURE.equals(type)){
				return new String[]{LynxConstants.TYPE_FILE,"png","jpg","jpeg","gif","bmp","tiff"};
			}else if(LynxConstants.TYPE_BUNDLE.equals(type)){
				return new String[]{LynxConstants.TYPE_FOLDER};
			}
		}
		return new String[0];
	}
	
	public static Long[] convertTimeRange(String date){
		long startTime = 0, endTime = 0;
		if("today".equals(date)){
			startTime = TimeUtil.getTodayMorning();
			endTime = TimeUtil.getTodayNight();
		}else if("yesterday".equals(date)){
			startTime = TimeUtil.getYesterdayMorning();
			endTime = TimeUtil.getYesterdayNight();
		}else if("thisweek".equals(date)){
			startTime = TimeUtil.getThisWeekMorning();
			endTime = TimeUtil.getThisWeekNight();
		}else if("lastweek".equals(date)){
			startTime = TimeUtil.getLastWeekMorning();
			endTime = TimeUtil.getLastWeekNight();
		}else if("thismonth".equals(date)){
			startTime = TimeUtil.getThisMonthMorning();
			endTime = TimeUtil.getThisMonthNight();
		}else if("lastmonth".equals(date)){
			startTime = TimeUtil.getLastMonthMorning();
			endTime = TimeUtil.getLastMonthNight();
		}else if("lastthreemonth".equals(date)){
			startTime = TimeUtil.getLastThreeMonthMorning();
			endTime = TimeUtil.getLastThreeMonthNight();
		}else if("thisyear".equals(date)){
			startTime = TimeUtil.getThisYearMorning();
			endTime = TimeUtil.getThisYearNight();
		}
		return new Long[]{startTime,endTime};
	}
	
	private static boolean isPdfFileTypeForSearch(String fileType){
		if("pdf".equals(fileType)){
			return true;
		}
		return false;
	}
	
	private static boolean isOfficeFileTypeForSearch(String fileType){
		if("doc".equals(fileType)||"ppt".equals(fileType)||"xls".equals(fileType)){
			return true;
		}
		return false;
	}
	
	public static class QueryString{
		private String countString;
		private String queryString;
		private Map<String,Object> paramMap=new HashMap<String,Object>();
		public String getCountString() {
			return countString;
		}
		public void setCountString(String countString) {
			this.countString = countString;
		}
		public String getQueryString() {
			return queryString;
		}
		public void setQueryString(String queryString) {
			this.queryString = queryString;
		}
		public Map<String, Object> getParamMap() {
			return paramMap;
		}
		void addParam(String key,Object value){
			paramMap.put(key, value);
		}
	}
	
	public static class MyCreateQuery extends ResourceQuery{
		private String uid;
		MyCreateQuery(String uid){
			this.uid = uid;
			params = new HashMap<String,Object>();
		}
		@Override
		String getCountSelect() {
			return "select count(*)";
		}
		@Override
		String getQuerySelect() {
			return "select * ";
		}
		@Override
		String getFrom() {
			return " from a1_resource ";
		}
		@Override
		String getWhere() {
			String where = "where tid=:tid and creator=:uid and item_type!=:item_type and (status = '" + LynxConstants.STATUS_AVAILABLE+ "' or status='"+LynxConstants.STATUS_UNPUBLISH+"')" ;
			return where +getKeyWordString()+getFileTypeString();
		}
		
		@Override
		void addParams(String key, Object value) {
			params.put(key, value);
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			params.put("uid",uid);
			params.put("item_type", LynxConstants.TYPE_FOLDER);
			return params;
		}
		@Override
		public String getTableAlias() {
			return "";
		}
	}
	
	public static class MyStarFilesQuery extends ResourceQuery{
		private String uid;
		MyStarFilesQuery(String uid){
			params = new HashMap<String,Object>();
			this.uid = uid;
			
		}
		@Override
		String getCountSelect() {
			return "select count(*)";
		}
		@Override
		String getQuerySelect() {
			return "select r.* ";
		}
		@Override
		String getFrom() {
			return " from a1_resource r,a1_starmark s ";
		}
		@Override
		String getWhere() {
			String where = " where r.rid=s.rid and r.tid=s.tid and uid=:uid  and s.tid=:tid ";
			where = where+getKeyWordString()+getFileTypeString();
			return where;
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			params.put("uid",uid);
			return params;
		}
		
		@Override
		public String getTableAlias() {
			return "r.";
		}
	}
	
	/**
	 * 团队最近更新
	 * @author zhonghui
	 *
	 */
	public static class TeamRecentChangeQuery extends ResourceQuery{
		@Override
		String getCountSelect() {
			return "select count(*)";
		}
		@Override
		String getQuerySelect() {
			return "select * ";
		}
		@Override
		String getFrom() {
			return " FROM a1_resource r ";
		}
		@Override
		String getWhere() {
			String where = " WHERE r.tid=:tid AND (r.item_type='DFile' OR r.item_type='DPage') and r.status = '" + LynxConstants.STATUS_AVAILABLE+ "' ";
			where = where+getKeyWordString()+getFileTypeString();
			return where;
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			return params;
		}
	}
	
	public static class TagQuery extends ResourceQuery{
		private List<Integer> tagIds;
		public TagQuery(List<Integer> tagIds){
			this.tagIds = tagIds;
		}
		@Override
		String getCountSelect() {
			return "select count(distinct r.rid)";
		}
		@Override
		String getQuerySelect() {
			return "select distinct r.* ";
		}
		@Override
		String getFrom() {
			return " FROM a1_tag_item a,a1_resource r ";
		}
		@Override
		String getWhere() {
			String where = " WHERE a.tid=:tid and a.tgid ="+tagIds.get(0)+" and r.rid=a.rid and r.status='"+LynxConstants.STATUS_AVAILABLE+"'";
			where = where+getKeyWordString()+getFileTypeString() +getExists();
			return where;
		}
		
		private String getExists(){
			StringBuilder sb = new StringBuilder();
			if(tagIds!=null&&tagIds.size()>1){
				for(int i=1;i<tagIds.size();i++){
					sb.append(" and exists(select 1 from a1_tag_item a"+i+" where a"+i+".rid=a.rid and a"+i+".tgid="+tagIds.get(i)+" )");
				}
			}
			return sb.toString();
		}
		
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			return params;
		}
		@Override
		public String getTableAlias() {
			return "r.";
		}
	}
	
	public static class FileTypeQuery extends ResourceQuery{
		@Override
		String getCountSelect() {
			return "select count(rid) ";
		}
		@Override
		String getQuerySelect() {
			return "select * ";
		}
		@Override
		String getFrom() {
			return " FROM a1_resource  ";
		}
		@Override
		String getWhere() {
			String where = " WHERE tid=:tid  and status='"+LynxConstants.STATUS_AVAILABLE+"'";
			where = where+getKeyWordString()+getFileTypeString();
			return where;
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			return params;
		}
	}
	
	public static class ResourcePathQuery extends ResourceQuery{
		private int rid;
		
		public ResourcePathQuery(int rid){
			this.rid = rid;
		}
		@Override
		String getCountSelect() {
			return "select count(r.rid) ";
		}
		@Override
		String getQuerySelect() {
			return "select r.* ";
		}
		@Override
		String getFrom() {
			return " from a1_resource r,ddl_folder_path p  ";
		}
		@Override
		String getWhere() {
			String where = " where r.rid=p.rid and r.tid=:tid and p.tid=:tid and p.ancestor_rid=:rid and p.length=1 and status='"+LynxConstants.STATUS_AVAILABLE+"'  ";
			where = where+getKeyWordString()+getFileTypeString();
			return where;
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			params.put("rid",rid);
			return params;
		}
		
		@Override
		public String getTableAlias() {
			return "r.";
		}
	}
	public static class ExceptFolderQuery extends ResourceQuery{
		@Override
		String getCountSelect() {
			return "select count(r.rid) ";
		}
		@Override
		String getQuerySelect() {
			return "select r.* ";
		}
		@Override
		String getFrom() {
			return " from a1_resource r,ddl_folder_path p  ";
		}
		@Override
		String getWhere() {
			String where = " where r.rid=p.rid and r.tid=:tid and p.tid=:tid and p.length=1 and r.status='"+LynxConstants.STATUS_AVAILABLE+"' and (r.item_type= :itemTypeDPage or r.item_type= :itemTypeDFile) ";
			where = where+getKeyWordString()+getFileTypeString();
			return where;
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			params.put("itemTypeDPage", LynxConstants.TYPE_PAGE);
			params.put("itemTypeDFile", LynxConstants.TYPE_FILE);
			return params;
		}
		
		@Override
		public String getTableAlias() {
			return "r.";
		}
	}
	
	public static class ResourceTimeQuery extends ResourceQuery{
		private String timeType;
		public ResourceTimeQuery(String timeType){
			this.timeType = timeType;
		}
		@Override
		String getCountSelect() {
			return "select count(rid) ";
		}
		@Override
		String getQuerySelect() {
			return "select * ";
		}
		@Override
		String getFrom() {
			return " from a1_resource  ";
		}
		@Override
		String getWhere() {
			String where = " where tid=:tid and status='"+LynxConstants.STATUS_AVAILABLE+"' "+getQueryDate();
			where = where+getKeyWordString()+getFileTypeString();
			return where;
		}
		@Override
		Map<String, Object> getParamMap() {
			params.put("tid", getTid());
			return params;
		}
		private String getQueryDate(){
			if(!StringUtils.isEmpty(timeType)){
				Long[] timeRange = convertTimeRange(timeType);
				return " and last_edit_time between '"+new Timestamp(timeRange[0])+"' and '"+new Timestamp(timeRange[1])+"' ";
			}else{
				return "";
			}
		}
		@Override
		public String getTableAlias() {
			return "";
		}
	}
}
