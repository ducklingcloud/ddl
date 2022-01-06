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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

/**
 * @date Mar 9, 2011
 * @author xiejj@cnic.cn
 */
public final class AoneTimeUtils {
    private AoneTimeUtils(){}
    private static final Logger log = Logger.getLogger(AoneTimeUtils.class);
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    public static String formatToDate(Date date){
        return DateFormatUtils.format(date, DATE_PATTERN);
    }

    public static String formatToDateTime(Date date){
        return DateFormatUtils.format(date, DATE_TIME_PATTERN);
    }

    public static String formatToTime(Date date){
        return DateFormatUtils.format(date, TIME_PATTERN);
    }

    public static Date parseDateTime(String textDate) {
        try {
            return DateUtils.parseDate(textDate, new String[]{DATE_TIME_PATTERN});
        } catch (ParseException e) {
            log.error("parse date time error for input "+textDate,e);
        }
        return null;
    }

    public static Date parseDate(String textDate) {
        try {
            return DateUtils.parseDate(textDate, new String[]{DATE_PATTERN});
        } catch (ParseException e) {
            log.error("parse date error for input "+textDate,e);
        }
        return null;
    }

    public static Date parseTime(String textDate) {
        try {
            return DateUtils.parseDate(textDate, new String[]{TIME_PATTERN});
        } catch (ParseException e) {
            log.error("parse time error for input "+textDate,e);
        }
        return null;
    }

    public static boolean isSameDay(Date first, Date second){
        if ((first==null || second==null)&&!first.equals(second)){
            return false;
        }
        Date firstDay = DateUtils.truncate(first, Calendar.DAY_OF_MONTH);
        Date secondDay = DateUtils.truncate(second, Calendar.DAY_OF_MONTH);
        return firstDay.equals(secondDay);
    }
    public static boolean isSameMonth(Date first, Date second){
        if ((first==null || second==null)&&!first.equals(second)){
            return false;
        }
        Date firstMonth = DateUtils.truncate(first, Calendar.MONTH);
        Date secondMonth = DateUtils.truncate(second, Calendar.MONTH);
        return firstMonth.equals(secondMonth);
    }

    public static boolean isSameYear(Date first, Date second) {
        if ((first==null || second==null)&&first!=second){
            return false;
        }
        Date firstYear = DateUtils.truncate(first, Calendar.YEAR);
        Date secondYear = DateUtils.truncate(second, Calendar.YEAR);
        return firstYear.equals(secondYear);
    }

    private final static long DAY_MILLS = 1000*60*60*24;
    private final  static long HOUR_MILLS = 1000*60*60;

    public static String getLastTime(long delta){
        if(delta<=0){
            return "已过期";
        }
        long temp = delta;
        long days = temp/DAY_MILLS;
        temp = delta - (days)*DAY_MILLS;
        long hours =temp/1000/60/60;
        temp = temp - (hours)*HOUR_MILLS;
        long mins = temp/1000/60;
        if(days>0){
            return days+"天"+hours+"小时"+mins+"分钟";
        }
        if(hours>0){
            return hours+"小时"+mins+"分钟";
        }
        return mins+"分钟";
    }
}
