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

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.falcon.api.cache.impl.MemcachedCacheService;

public final class ShareResourceDownloadCountUtil {
	private static final Logger LOG = Logger.getLogger(ShareResourceDownloadCountUtil.class);
	
	private static MemcachedCacheService cache;
	public static void count(HttpServletRequest request){
		MemcachedCacheService cache = getCache();
		String ip = getRealIp(request);
		DownLoadCount count = (DownLoadCount)cache.get(getMemKey(ip));
		if(count==null||!count.isOneDay()){
			count = new DownLoadCount();
		}
		count.incrementAndGet();
		cache.set(getMemKey(ip),count);
		if(count.isMaxinum()){
			LOG.warn("[IP:"+ip+"] try download resource "+count.getCount()+" times.");
		}
	}
	private static String getMemKey(String ip){
		return "shareResourceDown"+ip;
	}
	
	
	private static MemcachedCacheService getCache(){
		if(cache==null){
			initCache();
		}
		return cache;
	}
	
	private static synchronized void initCache(){
		if(cache==null){
			cache = DDLFacade.getBean(MemcachedCacheService.class);
		}
	}
	
	public static String getRealIp(HttpServletRequest request){
		String ip = request.getHeader("x-real-ip");
		if(ip==null||ip.length()==0){
			ip = request.getRemoteAddr();
		}
		
		return ip;
	}
	
	static class DownLoadCount implements Serializable{
		private int count;
		private long createTime;
		DownLoadCount(){
			createTime = System.currentTimeMillis();
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public int incrementAndGet(){
			count++;
			return count;
		}
		
		
		public boolean isOneDay(){
			return System.currentTimeMillis()-createTime<(1000l*3600*24);
		}
		
		public boolean isMaxinum(){
			return count>10;
		}
	}
	
}
