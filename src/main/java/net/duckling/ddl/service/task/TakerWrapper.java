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

import java.util.List;


import net.duckling.ddl.util.CommonUtil;

import org.apache.commons.lang.StringUtils;



/**接受者包装类，主要用于展示
 * @author lvly
 * @since 2012-6-21
 * */
public final class TakerWrapper {
    private TakerWrapper (){}
    /**以userName方式显示
     * @param takers 接受者队列
     * */
    public static String toView(List<TaskTaker> takers){
        return getSome(takers,0);
    }
    /**以UID方式显示
     * @param takers 接受者队列
     * */
    public static String toUID(List<TaskTaker> takers){
        return getSome(takers,1);
    }
    private static String getSome(List<TaskTaker> takers,int index){
        StringBuffer sb=new StringBuffer();
        String split=" | ";
        if(CommonUtil.isNullArray(takers)){
            sb.append("暂无");
            return sb.toString();
        }
        for(TaskTaker taker:takers){
            if(taker==null||CommonUtil.isNullStr(taker.getUserId())){
                continue;
            }
            try{
                sb.append(taker.getUserId().split("%")[index]).append(split);
            }catch(Exception e){
                continue;
            }
        }
        if(sb.indexOf(split)>0){
            sb.delete(sb.length()-split.length(),sb.length());
        }
        return sb.toString();
    }
    /**从 【名称%userId】中把名称剥离出来
     * @param str 【名称%userId】格式的字符串
     * @return String 名称
     * */
    public static String getUserName(String str){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        if(!str.contains("%")) {
            return str;
        }
        return str.split("%")[0];
    }
    /**从 【名称%userId】中把userId剥离出来
     * @param str 【名称%userId】格式的字符串
     * @return String userId
     * */
    public static String getUserID(String str){
        if(StringUtils.isEmpty(str)){
            return "";
        }
        if(!str.contains("%")) {
            return str;
        }
        return str.split("%")[1];
    }
}
