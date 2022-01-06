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

import java.util.Map;

import net.duckling.ddl.constant.LynxConstants;

import org.apache.commons.lang.StringUtils;

public final class ResourceQueryKeywordUtil {

    public static String getKeyWordString(String keyword,Map<String,Object> param,String ableAlies){
        if(StringUtils.isNotEmpty(keyword)){
            if(keyword.endsWith("ddoc")){
                if(keyword.length()=="ddoc".length()||".ddoc".equalsIgnoreCase(keyword)){
                    String s = " and "+ableAlies+"item_type='"+LynxConstants.TYPE_PAGE+"' ";
                    return s;
                }else{
                    String s = " and "+ableAlies+"item_type='"+LynxConstants.TYPE_PAGE+"' and lcase("+ableAlies+"title) like :keyWord ";
                    String u = keyword.substring(0, keyword.lastIndexOf(".ddoc"));
                    setMap("keyWord", u,param);
                    return s;
                }

            }else{
                String s = " and (lcase("+ableAlies+"title) like :keyWord or lcase("+ableAlies+"last_editor_name) like :keyWord or lcase("+ableAlies+"tags) like :keyWord) ";
                setMap("keyWord", keyword,param);
                return s;
            }
        }else{
            return "";
        }
    }

    private static void setMap(String key,String value,Map<String,Object> param){
        if(param!=null){
            param.put(key, "%"+value.toLowerCase()+"%");
        }
    }
}
