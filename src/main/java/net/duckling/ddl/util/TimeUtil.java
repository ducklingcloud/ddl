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

import java.util.Calendar;

public final class TimeUtil {
	private TimeUtil(){}
	private static final int UINX_TIME_BASE = 1000;
	private static final int MY_HOUR_OF_DAY = 23;
	private static final int MY_SECOND = 59;
	private static final int MY_MINUTE = 59;
	private static final int MY_MILLISECOND = 999;
	private static final int MY_HOUR_OF_DAY_FOR_YM = -24;
	private static final int MONTH_LAST3MONTH = -2;
	private static final int DAY_OF_WEEK_THIS_WEEK = 6;
	private static final int DAY_OF_WEEK_LAST_WEEK = -7;
	private static final int DAY_OF_WEEK_LAST_WEEK_N = -6;
	
	//获得当天0点时间
	public static long getTodayMorning(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	//获得当天24点时间
	public static long getTodayNight(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY);
		cal.set(Calendar.SECOND, MY_SECOND);
		cal.set(Calendar.MINUTE, MY_MINUTE);
		cal.set(Calendar.MILLISECOND, MY_MILLISECOND);
		return cal.getTimeInMillis();
	}
	
	//获得昨天0点时间
	public static long getYesterdayMorning(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY_FOR_YM);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	//获得昨天24点时间
	public static long getYesterdayNight(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, -1);
		cal.set(Calendar.SECOND, MY_SECOND);
		cal.set(Calendar.MINUTE, MY_MINUTE);
		cal.set(Calendar.MILLISECOND, MY_MILLISECOND);
		return cal.getTimeInMillis();
	}
	
	//获得本周一0点时间
	public static long getThisWeekMorning(){
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
	    cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	    cal.setFirstDayOfWeek(Calendar.MONDAY);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	    return cal.getTimeInMillis();
	}
	
	//获得本周日24点时间
	public static long getThisWeekNight(){
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
	    cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	    cal.setFirstDayOfWeek(Calendar.MONDAY);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	    cal.add(Calendar.DAY_OF_WEEK, DAY_OF_WEEK_THIS_WEEK);
		return cal.getTimeInMillis();
	}
	
	// 获得上周一0点时间
	public static long getLastWeekMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DAY_OF_WEEK, DAY_OF_WEEK_LAST_WEEK);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	// 获得上周日24点时间
	public static long getLastWeekNight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.add(Calendar.DAY_OF_WEEK, DAY_OF_WEEK_LAST_WEEK_N);
		cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY);
		cal.set(Calendar.MINUTE, MY_MINUTE);
		cal.set(Calendar.SECOND, MY_SECOND);
		cal.set(Calendar.MILLISECOND, MY_MILLISECOND);
		return cal.getTimeInMillis();
	}
	
	//获得本月第一天0点时间
	public static long getThisMonthMorning(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);  
        cal.set(Calendar.HOUR_OF_DAY, 0);  
        cal.set(Calendar.MINUTE, 0);  
        cal.set(Calendar.SECOND, 0);  
        cal.set(Calendar.MILLISECOND, 0); 
		return cal.getTimeInMillis();
	}
	
	//获得本月最后一天24点时间
	public static long getThisMonthNight(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
        cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY);  
        cal.set(Calendar.MINUTE, MY_MINUTE);  
        cal.set(Calendar.SECOND, MY_SECOND);  
        cal.set(Calendar.MILLISECOND, MY_MILLISECOND); 
		return cal.getTimeInMillis();
	}
	
	// 获得上月第一天0点时间
	public static long getLastMonthMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);// 设为当前月的1号 
		cal.add(Calendar.MONTH, -1);// 减一个月，变为下月的1号  
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}  

	// 获得上月最后一天24点时间
	public static long getLastMonthNight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);// 设为当前月的1号 
		cal.add(Calendar.DATE, -1);//减去一天，变为上月最后一天 
		cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY);
		cal.set(Calendar.MINUTE, MY_MINUTE);
		cal.set(Calendar.SECOND, MY_SECOND);
		cal.set(Calendar.MILLISECOND, MY_MILLISECOND);
		return cal.getTimeInMillis();
	}
	
	// 获得上二个月第一天0点时间
	public static long getLastThreeMonthMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);// 设为当前月的1号
		cal.add(Calendar.MONTH, MONTH_LAST3MONTH);// 减一个月，变为下月的1号
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

	// 获得上三个月（本月为第三个月）最后一天24点时间（其实是本月的最后一天）
	public static long getLastThreeMonthNight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));  
        cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY);  
        cal.set(Calendar.MINUTE, MY_MINUTE);  
        cal.set(Calendar.SECOND, MY_SECOND);  
        cal.set(Calendar.MILLISECOND, MY_MILLISECOND); 
		return cal.getTimeInMillis();
	}
	
	//获得本年第一天0点时间
	public static long getThisYearMorning(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, 1);  
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0);  
        return cal.getTimeInMillis();  
	}
	
	//获得本年最后一天24点时间
	public static long getThisYearNight(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));  
        cal.set(Calendar.HOUR_OF_DAY, MY_HOUR_OF_DAY);  
        cal.set(Calendar.MINUTE, MY_MINUTE);  
        cal.set(Calendar.SECOND, MY_SECOND);  
        cal.set(Calendar.MILLISECOND, MY_MILLISECOND);  
        return cal.getTimeInMillis();
	}
	
	//将java中毫秒数转换成Unix时间戳
	public static long convert2UnixTimestamp(long javatime){
		return javatime/UINX_TIME_BASE;
	}
	
	//将Unix时间戳转换成java中毫秒数
	public static long convert2JavaTimestamp(long unixtime){
		return unixtime*UINX_TIME_BASE;
	}
}
