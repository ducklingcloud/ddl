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
import java.util.Map;

;

/**常用的工具类了
 * @author lvly
 * @since 2012-6-18
 * */
public final class CommonUtil {
	private CommonUtil(){}
	/**判断数组是否为空，判断.split出来的数组格外有效
	 * @param array 判定数组
	 * */
	public static boolean isNullArray(String[] array){
		return (array==null||array.length==0||(array.length==1&&"".equals(array[0])));
	}
	/**判断数组为空，int类型
	 * @param array int类型的判定数组
	 * */
	public static boolean isNullArray(int[] array){
		return (array==null||array.length==0);
	}
	/**是否空列表，
	 * @param list 待判定列表
	 * */
	public static boolean isNullArray(List list){
		return (list==null)||(list.size()==0);
	}
	/**
	 * 判断是否是空集合
	 * @param map Map对象
	 * */
	public static boolean isNullArray(Map map){
		return (map==null||(map.isEmpty()));
	}
	/**把单个的对象转化为数组，匹配参数用
	 * @param k 某一个对象
	 * @return List<K>
	 * */
	public static <K> List<K> getList(K k){
		List<K> ks=new ArrayList<K>();
		ks.add(k);
		return ks;
	}
	/**把队列里的第一个元素拿出来而不抛异常
	 * @param List<K> 原队列
	 * @return K 第一个元素
	 * */
	public static <K> K first(List<K> list){
		if(CommonUtil.isNullArray(list)){
			return null;
		}
		return list.get(0);
	}
	/**判断字符串是否为空
	 * @param str the String what need to judice
	 * @param boolean isNull?
	 * */
	public static boolean isNullStr(String str){
		return str==null||"".equals(str.trim())||"null".equals(str.toLowerCase().trim());
	}
	/**把数组里的元素转换成List
	 * @param array 数组
	 * @return list ArrayList
	 * */
	public static <K> List<K> array2List(K[] array){
		List<K> list=new ArrayList<K>();
		if(array!=null&&array.length>0){
			for(K k:array){
				list.add(k);
			}
		}
		return list;
	}
	/**把数组里的String元素转换成Integer元素
	 * @param array 数组
	 * @return array 
	 * */
	public static  Integer[] stringArray2IntArray(String[] array){
		Integer[] result=new Integer[0];
		if(array!=null){
			result=new Integer[array.length];
			for(int i=0;i<array.length;i++){
				result[i]=Integer.parseInt(array[i]);
			}
		}
		return result;
	}
}
