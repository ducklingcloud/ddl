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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class HtmlTemplate {
	
	protected static final Logger LOGGER = Logger.getLogger(HtmlTemplate.class);
	
	private static final String CHARSET="utf-8";
	public static final String DATA_PREFIX = "${";
	public static final String DATA_SUFFIX = "}";
	public static final String TEMPLATE_ROOT_PATH = "../templates/html/";
	
	private static Map<String,String> cache = new HashMap<String, String>();
	
	/**
	 * 模板合并数据
	 * @param templatePath
	 * @param datas
	 * @return
	 */
	public static String merge(String templatePath, Map<String,String> datas) {
        String result = loadFile(templatePath);
        if(result == null){
        	return null;
        }
        
        for(String key : datas.keySet()){
        	result = result.replace(DATA_PREFIX + key + DATA_SUFFIX, datas.get(key));
        }
        return result;
	}
	
	public static void clearAll(){
		cache.clear();
	}
	
	public static void clear(String templatePath){
		cache.remove(templatePath);
	}
	
	private static String loadFile(String templatePath){
		
		if(cache.containsKey(templatePath)){
			return cache.get(templatePath);
		}
		
		StringBuilder sb = new StringBuilder();
        try {
			String tempStr = "";
			FileInputStream is = new FileInputStream(HtmlTemplate.class.getResource("/").getPath() + 
						TEMPLATE_ROOT_PATH + templatePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET));
			while ((tempStr = br.readLine()) != null){
				sb.append(tempStr);
			}
			is.close();
        } catch (IOException e) {
        		LOGGER.error("模板文件读取错误:" + templatePath , e);
                return null;
        }
        String result = sb.toString();
        
        //模板放入缓存
        cache.put(templatePath, result);
        return result;
	}
	
	
}
