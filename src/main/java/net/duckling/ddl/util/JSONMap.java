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
import java.util.HashMap;
/**
 * 请勿存储.,用追尾法，查询value
 * a.b[0].c
 * 并不会查key为"a.b[0].c"的value
 * 而是查，"a"下面属性为b的值，b下面第一个元素属性为c的值，返回
 * */
public class JSONMap extends HashMap<String, Object>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3061898688541525020L;
	public static final String POINT=".";
	public static final String POINT_SPLIT="\\.";
	@Override
	public Object get(Object key) {
		if(key instanceof String){
			String keyStr=key.toString();
			if(keyStr.contains(POINT)){
				String root=keyStr.split(POINT_SPLIT)[0];
				return ReflectUtils.getLikeJSon(super.get(root), keyStr);
			}else{
				return super.get(key);
			}
		}else{
			return super.get(key);
		}
	}
}
