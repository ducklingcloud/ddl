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

import net.duckling.ddl.common.DBs;
import org.apache.commons.lang.StringUtils;

public final class ResourceOrderUtils {
    private ResourceOrderUtils(){}
    private static final String ORDER_SORT_TIME = "time";
    private static final String ORDER_SORT_TIME_DESC = "timeDesc";
    private static final String ORDER_SORT_TITLE = "title";
    private static final String ORDER_SORT_TITLE_DESC = "titleDesc";

    public static String buildOrderSql(String tableAlies,String orderStr){

        orderStr=StringUtils.isEmpty(orderStr)?"":orderStr;

        tableAlies=StringUtils.isEmpty(tableAlies)?"":(tableAlies+".");

        switch(orderStr){
            case ORDER_SORT_TIME:{
                return " order by "+tableAlies+"order_type asc ,"+tableAlies+"last_edit_time";
            }
            case ORDER_SORT_TIME_DESC:{
                return " order by "+tableAlies+"order_type asc ,"+tableAlies+"last_edit_time desc";
            }
            case ORDER_SORT_TITLE:{
                return " ORDER BY "+ tableAlies +"order_type ASC, "+
                        tableAlies +"title";
            }
            case ORDER_SORT_TITLE_DESC:{
                return " ORDER BY "+ tableAlies +"order_type ASC, "+
                        tableAlies +"title DESC";
            }
            default:
                return " order by "+tableAlies+"order_type asc ";
        }
    }

    public static String buildDivPageSql(int offset,int size){
        if(offset<0 || size<0 || (size==0 && offset>0)){
            return "";
        }

        return DBs.getDbms().equals("mysql") ?
                " LIMIT "+ offset +","+ size :
                " OFFSET "+ offset +" ROWS FETCH NEXT "+ size +" ROWS ONLY";
    }
}
