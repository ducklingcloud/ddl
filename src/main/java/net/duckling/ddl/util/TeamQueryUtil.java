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

import net.duckling.ddl.constant.LynxConstants;


public final class TeamQueryUtil {
	private TeamQueryUtil(){}
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
	private static boolean isOfficeFileTypeForSearch(String fileType){
		if("doc".equals(fileType)||"ppt".equals(fileType)||"xls".equals(fileType)){
			return true;
		}
		return false;
	}
	private static boolean isPdfFileTypeForSearch(String fileType){
		if("pdf".equals(fileType)){
			return true;
		}
		return false;
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
	
	public static String buildDynamicSQL(TeamQuery q){
		String sql = "select * from a1_resource a ";
		StringBuilder sb = new StringBuilder();
		boolean hasWhere = false;
		sb.append(sql);
		if(q.getTagIds()!=null){
			hasWhere = insertWhereClause(sb,hasWhere);
			int[] tagIds = q.getTagIds();
			for(int tgid:tagIds){
				sb.append(" a.tags like '%\""+tgid+"\"%' and ");
			}
		}
		if(q.getDate()!=null){
			hasWhere = insertWhereClause(sb, hasWhere);
			Long[] timeRange = convertTimeRange(q.getDate());
			sb.append(" a.last_edit_time between '"+new Timestamp(timeRange[0])+"' and '"+new Timestamp(timeRange[1])+"' and ");
		}
		if(q.getType()!=null&&q.getType().length()>0){
			hasWhere = insertWhereClause(sb, hasWhere);
			String[] types = convertType(q.getType());
			if(types!=null){
				sb.append(" a.item_type = '"+types[0]+"' and ");
				if(types.length>1){
					StringBuilder temp = new StringBuilder();
					temp.append(" (");
					for(int i=1;i<types.length;i++){
						temp.append(" a.file_type like '%"+types[i]+"%' or  ");
					}
					String subStr = temp.toString();
					subStr = subStr.substring(0,subStr.length()-"or  ".length());
					subStr = subStr + ")";
					sb.append(subStr);
					sb.append(" and ");
				}
			}
		}
		if(q.getFilter()!=null){
			hasWhere = insertWhereClause(sb,hasWhere);
			sb.append(" (a.tags = '{}' or a.tags='null') and ");
		}
		String notDelete=" and (status is null or status!='"+LynxConstants.STATUS_DELETE+"') ";
		String version=getQueryVersion(convertType(q.getType()));
		if(hasWhere){
			String temp = sb.toString();
			String result = temp.substring(0, temp.length()-4);
			return result + " and "+ getTidString(q.getTid())+notDelete+version+getOrder(q)+" limit "+q.getOffset()+","+q.getSize();
		}else{
			String result = sb.toString()+" where "+getTidString(q.getTid())+notDelete+version+getOrder(q)+" limit "+q.getOffset()+","+q.getSize();
			return result;
		}
	}
	/**
	 * 判断type是否为bundle如果为bundle就不加版本控制
	 * @param convertType
	 * @return
	 */
	private static String getQueryVersion(String[] convertType) {
		if(convertType==null||convertType.length==0){
			return " and last_version >0 ";
		}else if(convertType.length==1){
			if(LynxConstants.TYPE_FOLDER.equals(convertType[0])){
				return "";
			}
		}
		return " and last_version >0 ";
	}
	private static String getTidString(int[] tids){
		StringBuilder sb = new StringBuilder();
		if(null != tids && tids.length>0){
			sb.append(" tid in(");
			for(int tid : tids){
				sb.append(tid+",");
			}
			sb.replace(sb.lastIndexOf(","), sb.length(), ") ");
		}else{
			sb.append(" 1=1 ");
		}
		return sb.toString();
	}
	
	private static boolean insertWhereClause(StringBuilder sb, boolean hasWhere) {
		if(!hasWhere){
			sb.append(" where ");
			return true;
		}
		return hasWhere;
	}
	
	private static String getOrder(TeamQuery q){
		String orderTitle = q.getOrderTitle();
		String orderDate = q.getOrderDate();
		boolean titleEmpty = isEmpty(orderTitle);
		boolean dateEmpty = isEmpty(orderDate);
		String result = " ";
		if(!dateEmpty){
			result += "order by last_edit_time ";
			result += isAsc(orderDate, "date")?"asc":"desc";
		}else if(!titleEmpty){
			result += "order by convert(title using gb2312) ";
			result += isAsc(orderTitle, "title")?"asc":"desc";
		}else{
			result += "order by last_edit_time desc";
		}
		result += " ";
		return result;
	}
	
	public static boolean isEmpty(String tmp){
		return (null == tmp || "".equals(tmp))?true:false;
	}
	
	public static boolean isAsc(String orderParam, String column){
		if("date".equals(column)){
			return TeamQuery.SORT_DATE_ASC.equals(orderParam)?true:false;
		}else{
			return TeamQuery.SORT_TITLE_ASC.equals(orderParam)?true:false;
		}
	}
}
