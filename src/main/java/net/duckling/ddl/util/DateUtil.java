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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lvly
 * @since 2012-6-10
 * 时间格式化工具类
 * */
public final class DateUtil {
	private DateUtil(){}
	/**默认日期格式*/
	public static final String DEFAULT_TYPE="yyyy-MM-dd HH:mm:ss";
	public static final SimpleDateFormat SDF_DEF=new SimpleDateFormat(DEFAULT_TYPE);
	/**时间长度*/
	public static final long DAY=1000*3600*24;
	public static final long WEEK=DAY*7;
	
	/**只获取到日，后面抛弃
	 * @param time 符合 yyyy-mm-dd hh:MM:ss
	 * @return yyyy-mm-dd
	 * */
	public static String getShortTime(String time){
		if(CommonUtil.isNullStr(time)){
			return "";
		}
		return time.split(" ")[0];
	}
	/**把指定日期转换成字符串
	 * @param date 指定日期*/
	public static synchronized String getTime(Date date){
		return SDF_DEF.format(date);
	}
	/**获得当前时间字符串，默认按YYYY-MM-DD显示
	 * @return 当前时间字符串
	 * */
	public static synchronized String getCurrentTime(){
		return SDF_DEF.format(new Date());
	}
	/**获得当前时间字符串，按指定格式显示
	 * @param fmtStr  日期格格式
	 * @return 当前时间字符串
	 * */
	public static String getCurrentTime(String fmtStr){
		SimpleDateFormat sdf=new SimpleDateFormat(fmtStr);
		return sdf.format(new Date());
	}
	/**获得今天的字符串
	 * @return 今天从00:00:00的字符串 到明天00:00:00
	 * */
	public static String[] getToday(){
		String result[] =new String[2];
		result[0]=toZero(getCurrentTime());
		result[1]=toZero(new Date(System.currentTimeMillis()+DAY));
		return result;
	}
	/**获得今天的字符串
	 * @return 前天从00:00:00的字符串到昨天00:00:00
	 * */
	public static String[] getYesterday(){
		String result[] =new String[2];
		result[0]=toZero(new Date(System.currentTimeMillis()-DAY));
		result[1]=toZero(new Date(System.currentTimeMillis()));
		return result;
	}
	/**获得上周一和上周日
	 * @return str[0]是上周一  str[1]是上周日
	 * */
	public static String[] getLastWeek(){
		String[] result=new String[2];
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 1);
		result[1]=toZero(cal.getTime());
		cal.add(Calendar.WEEK_OF_MONTH, -1);
		cal.set(Calendar.DAY_OF_WEEK, 2);
		result[0]=toZero(cal.getTime());	
		return result;
	}
	/**获得这周一和这周日
	 * @return str[0]是这周一  str[1]是这周日
	 * */
	public static String[] getThisWeek(){
		String[] result=new String[2];
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 2);
		result[0]=toZero(cal.getTime());	
		cal.set(Calendar.DAY_OF_WEEK, 1);
		cal.add(Calendar.WEEK_OF_MONTH, 1);
		result[1]=toZero(cal.getTime());
		return result;
	}
	/**获得这个月一日和下个月一日
	 * @return str[0]是这个月一日  str[1]是下个月一日
	 * */
	public static String[] getThisMonth(){
		String[] result=new String[2];
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		result[0]=toZero(cal.getTime());
		cal.add(Calendar.MONTH, 1);
		result[1]=toZero(cal.getTime());
		return result;
	}
	/**获得上个月一日和这个月一日
	 * @return str[0]是上个月一日  str[1]是这个月一日
	 * */
	public static String[] getLastMonth(){
		String[] result=new String[2];
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		result[1]=toZero(cal.getTime());
		cal.add(Calendar.MONTH, -1);
		result[0]=toZero(cal.getTime());
		return result;
	}
	//tool BEGIN
	private static String toZero(Date date){
		return getTime(date).split(" ")[0]+" 00:00:00";
	}
	private static String toZero(String date){
		return date.split(" ")[0]+" 00:00:00";
	}//END
}
