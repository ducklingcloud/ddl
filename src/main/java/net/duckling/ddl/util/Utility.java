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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;




/**
 * Introduction Here.
 * 
 * @date Feb 2, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public final class Utility {
	private Utility(){}
	public static boolean int2bool(int value){
		return (value!=0);
	}
	public static int bool2int(boolean value){
		return (value?1:0);
	}
	public static Timestamp date2Timestamp(Date date){
		if (date==null){
			return null;
		}
		else{
			return new Timestamp(date.getTime());
		}
	}
	// 将 s 进行 BASE64 编码
	public static String getBASE64(String s) {
		if (s == null){
			return null;
		}
		return new String(Base64.encodeBase64(s.getBytes()));
	}

	public static String getFromBASE64(String s) {
		if (s == null){
			return null;
		}
		try {
			return new String(Base64.decodeBase64(s.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDateShort(Date date) {
		Calendar calToday = Calendar.getInstance();
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		Calendar calYear = Calendar.getInstance();
		calYear.set(Calendar.DAY_OF_YEAR, 1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.after(calToday)){
			return (new SimpleDateFormat("HH:mm")).format(cal.getTime());
		}else if (cal.after(calYear)){
			return (new SimpleDateFormat("MM月dd日")).format(cal.getTime());
		}else{
			return (new SimpleDateFormat("yyyy年")).format(cal.getTime());
		}
	}

	public static String getDateCustom(Date date, String fmt) {
		Calendar calToday = Calendar.getInstance();
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		Calendar calYear = Calendar.getInstance();
		calYear.set(Calendar.DAY_OF_YEAR, 1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return (new SimpleDateFormat(fmt)).format(cal.getTime());
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
