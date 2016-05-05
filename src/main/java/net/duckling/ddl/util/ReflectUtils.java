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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/***
 * 反射工具类
 * 
 * @author lvly
 * @since 2012-11-6
 * */
public class ReflectUtils {
	private static final Logger LOG=Logger.getLogger(ReflectUtils.class);
	public static final Pattern ARRAY_PATTERN = Pattern.compile("^[a-zA-Z0-9]+\\[\\d+\\]$");

	public static <T> boolean setValue(T obj, String fieldName, Object value) {
		if (obj == null) {
			return false;
		}
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (ReflectiveOperationException e) {
			Method method;
			try {
				method = obj.getClass().getMethod(getSetMethodName(fieldName),obj.getClass());
				method.invoke(obj,value);
			} catch (ReflectiveOperationException  e1) {
				LOG.error(e1);
				return false;
			}
		}
		return true;

	}

	public static <T> Object getValue(T obj, String fieldName) {
		if (obj == null) {
			return null;
		}
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (ReflectiveOperationException e) {
			Method method;
			try {
				method = obj.getClass().getMethod(getGetMethodName(fieldName));
				return method.invoke(obj);
			} catch (ReflectiveOperationException  e1) {
				LOG.error(e1.getMessage(),e1);
			}
			return null;
		}
	}

	public static <T> Object getLikeJSon(Object root, String likeJson) {
		String[] split = likeJson.split("\\.");
		Object obj = root;
		for (int i = 1; i < split.length && obj != null; i++) {
			if (isArray(split[i])) {
				String field = split[i].split("\\[")[0];
				String index = split[i].split("\\[")[1].replaceAll("\\]", "");
				Object array = getValue(obj, field);
				if(CommonUtils.isNull(array)){
					return null;
				}
				if(array instanceof Object[]){
					obj = ((Object[])array)[Integer.parseInt(index)];
				}else if(array instanceof List){
					obj = ((List)array).get(Integer.parseInt(index));
				}else{
					return null;
				}
				
			} else {
				obj = getValue(obj, split[i]);
			}
		}
		return obj;
	}

	public static boolean isArray(String str) {
		return ARRAY_PATTERN.matcher(str).find();
	}

	private static String getGetMethodName(String fieldName) {
		return "get" + low2up(fieldName.charAt(0)) + fieldName.substring(1);
	}

	private static String getSetMethodName(String fieldName) {
		return "set" + low2up(fieldName.charAt(0)) + fieldName.substring(1);
	}

	private static char low2up(char c) {
		return (char) (c - 32);
	}

}
