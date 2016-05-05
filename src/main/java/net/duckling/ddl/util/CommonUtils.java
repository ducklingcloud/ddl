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
import java.util.Set;
import java.util.regex.Pattern;


/**常用的工具类了
 * @author lvly
 * @since 2012-6-18
 * */
public final class CommonUtils {
	public static final Pattern INT = Pattern.compile("^\\d+$|-\\d+$"); 
	public static final Pattern DOUBLE = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
	private CommonUtils(){}
	/**
	 * 判断是否为数字，小数或者整数
	 * */
	public static boolean isNumber(String str){
		if(isNull(str)){
			return false;
		}
		return INT.matcher(str).find()||DOUBLE.matcher(str).find();
	}
	/**
	 * 通用是否为空
	 * @param obj 
	 * @return isNull or isEmpty
	 * */
	public static boolean isNull(Object obj){
		if(obj==null){
			return true;
		}
		if(obj instanceof List){
			return isNull((List<?>)obj);
		}else if(obj instanceof Map){
			return isNull((Map<?,?>)obj);
		}else if(obj instanceof String[]){
			return isNull((String[])obj);
		}else if(obj instanceof int[]){
			return isNull((int[])obj);
		}else if(obj instanceof Set){
			return isNull((Set<?>)obj);
		}else if(obj instanceof String){
			return isNull((String)obj);
		}else if(obj instanceof Object[]){
			return isNull((Object[])obj);
		}
		//最好不要走到这一步
		return false;
	}
	/**判断数组是否为空，判断.split出来的数组格外有效
	 * @param array 判定数组
	 * */
	public static boolean isNull(String[] array){
		return (array==null||array.length==0||(array.length==1&&"".equals(array[0])));
	}
	public static boolean isNull(Object[] array){
		return (array==null||array.length==0);
	}
	/**判断数组为空，int类型
	 * @param array int类型的判定数组
	 * */
	public static boolean isNull(int[] array){
		return (array==null||array.length==0);
	}
	/**是否空列表，
	 * @param list 待判定列表
	 * */
	public static boolean isNull(List<?> list){
		return (list==null)||(list.size()==0);
	}
	/**是否空列表，
	 * @param list 待判定列表
	 * */
	public static boolean isNull(Set<?> list){
		return (list==null)||(list.size()==0);
	}
	/**
	 * 判断是否是空集合
	 * @param map Map对象
	 * */
	public static boolean isNull(Map<?,?> map){
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
		if(isNull(list)){
			return null;
		}
		return list.get(0);
	}
	/**判断字符串是否为空
	 * @param str the String what need to judice
	 * @param boolean isNull?
	 * */
	public static boolean isNull(String str){
		return str==null||"".equals(str.trim())||"null".equals(str.toLowerCase().trim());
	}
	/**杀掉空串和空
	 * @param str the String what need to judice
	 * @param str never return null
	 * */
	public static String killNull(Object obj){
		return CommonUtils.isNull(obj)?"":obj.toString().trim();
	}
	public static String killNull(String str){
		return CommonUtils.isNull(str)?"":str.trim();
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
	/**把字符串格式化一下，就是拼接字符串的时候把最后一个","之类的东西删掉 
	 * @param str 需要格式化的字符串
	 * @param flag ","或者什么的，默认是","
	 * @return 返回格式化后的字符串
	 * */
	public static String format(String str,String flag){
		if(CommonUtils.isNull(str)){
			return "";
		}
		StringBuilder sb=new StringBuilder(str);
		if(sb.indexOf(flag)>0){
			return sb.substring(0,sb.lastIndexOf(flag));
		}
		return sb.toString();
	}
	
	/**把字符串格式化一下，就是拼接字符串的时候把最后一个","之类的东西删掉 
	 * @param str 需要格式化的字符串,默认是","
	 * @return 返回格式化后的字符串
	 * */
	public static String format(String str){
		return format(str,",");
	}
	/**
	 * 把一个数字的字符串转换成Int，如果是小数，则强转成int
	 * 
	 * */
	public static int toInt(String x){
		return (int)Float.parseFloat(x);
	}
	 
}
