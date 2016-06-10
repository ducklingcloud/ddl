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
package cn.vlabs.duckling.aone.content.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestHelper {
	public static String convert2String(Object obj) throws Exception{
		StringBuilder sb = new StringBuilder();
		if(null == obj)
			return null;
		Class objcls = obj.getClass();
		Method method = null;
		Field[] fields = objcls.getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			String name = fields[i].getName();
			char first = name.charAt(0);
			name = name.replaceFirst(""+first, ""+(char)(first-32));
			method = objcls.getDeclaredMethod("get"+name, null);
			String value = ""+method.invoke(obj, null);
			sb.append(name+":"+value+",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
