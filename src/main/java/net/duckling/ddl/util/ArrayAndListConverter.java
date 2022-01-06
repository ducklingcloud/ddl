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

import java.util.ArrayList;
import java.util.List;

/**
 * 封装常用的基本类型，List的相互转化功能的类
 * @author Yangxp
 * @since 2012-12-18
 */
public class ArrayAndListConverter {

    /**
     * 将基本类型的整型数组转化为List<Integer>，若数组为null或空，则返回空的List
     * @param args
     * @return
     */
    public static List<Integer> convertInt2Integer(int[] args){
        List<Integer> result = new ArrayList<Integer>();
        if(null != args && args.length>0){
            for(int arg : args){
                result.add(arg);
            }
        }
        return result;
    }

    /**
     * 将List<Integer>转化为List<Long>，若List为null或空，则返回空的List。
     * @param args
     * @return
     */
    public static List<Long> convertInteger2Long(List<Integer> args){
        List<Long> result = new ArrayList<Long>();
        if(null != args && args.size()>0){
            for(int arg : args){
                result.add((long)arg);
            }
        }
        return result;
    }

    public static List<Long> convert2Long(int[] rids){
        List<Long> result = new ArrayList<Long>();
        for(int i=0; i<rids.length; i++){
            result.add((long)rids[i]);
        }
        return result;
    }
}
