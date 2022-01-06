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
package net.duckling.ddl.service.task;

import java.util.HashMap;
import java.util.Map;

import net.duckling.ddl.service.task.impl.TaskDAO;
import net.duckling.ddl.util.CommonUtil;
import net.duckling.ddl.util.DateUtil;




/**
 * 里头有sql就执行 里头的查询条件，没有就算了，执行默认
 *
 * @author lvly
 * @since 2012-6-28
 * */
public final class SQLThreadLocal {
    private SQLThreadLocal (){}
    private static final ThreadLocal<String> SQL_COLLECTION = new ThreadLocal<String>();
    /** 查询条件 */
    public static final String CON_DATE = "date";
    public static final String CON_TASK_TYPE = "taskType";
    /** 词典 */
    public static final Map<String, String> CONVERT_MAP = new HashMap<String, String>();
    /** 查询条件 */
    public static final String DATE_TODAY = "today";
    public static final String DATE_YESTERDAY = "yesterday";
    public static final String DATE_LAST_WEEK = "lastweek";
    public static final String DATE_THIS_WEEK = "thisweek";
    public static final String DATE_LAST_MONTH = "lastmonth";
    public static final String DATE_THIS_MONTH = "thismonth";
    /**
     * 追加sql条件
     *
     * @param key
     *            需要追加的条件value
     * @param value
     *            追加条件的key
     * */
    public static void append(String key, String value) {
        if(CommonUtil.isNullStr(value)){
            return;
        }
        String sql=getSQL(key,value);
        String sqlC = CommonUtil.isNullStr(SQL_COLLECTION.get()) ? ""
                : SQL_COLLECTION.get();
        sqlC += sql;
        SQL_COLLECTION.set(sqlC);
    }

    /**
     * 获得sql的方法，只能调用一次，调用完了，里面的sql就会清空
     **/
    public static String get() {
        String sql = SQL_COLLECTION.get();
        SQL_COLLECTION.remove();
        return sql;

    }
    /**替换时间查询条件的 from值和to值
     * @param result[] length为2,0存的是开始时间,1存的是结束时间
     * */
    private static String getDateSQL(String[] result){
        return TaskDAO.BY_CREATE_TIME.replace("${from}", result[0]).replace("${to}", result[1]);
    }
    /**根据条件名和条件值获取完整的条件SQL
     * @param key 条件名字
     * @param value 条件值
     * */
    private static String getSQL(String key, String value) {
        String sql = "";
        if (CON_TASK_TYPE.equals(key)) {
            sql+=TaskDAO.BY_TASK_TYPE.replace("?","'"+value+"'" );
        } else if (CON_DATE.equals(key)) {
            String dateConSql="";
            switch (value) {
                case DATE_TODAY:
                    dateConSql=getDateSQL(DateUtil.getToday());break;
                case DATE_YESTERDAY:
                    dateConSql=getDateSQL(DateUtil.getYesterday());break;
                case DATE_LAST_WEEK:
                    dateConSql=getDateSQL(DateUtil.getLastWeek());break;
                case DATE_THIS_WEEK:
                    dateConSql=getDateSQL(DateUtil.getThisWeek());break;
                case DATE_LAST_MONTH:
                    dateConSql=getDateSQL(DateUtil.getLastMonth());break;
                case DATE_THIS_MONTH:
                    dateConSql=getDateSQL(DateUtil.getThisMonth());break;
                default:break;
            }
            sql+=dateConSql;
        }
        return sql;
    }
}
