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
package net.duckling.ddl.service.resource;

import net.duckling.ddl.util.FileSizeUtils;

import org.apache.commons.lang.StringUtils;

/**
 * team空间大小bean，包含总大小，已使用大小
 * 
 * @author zhonghui
 *
 */
public class TeamSpaceSize {
	private int tid;
	private String teamDisplayName;
	private long used;
	private long total;
	private float percent;
	private String percentDisplay;
	private String usedDisplay;
	private String totalDisplay;
	public TeamSpaceSize(){}
	
	public TeamSpaceSize(int tid, String teamDisplayName, long used, long total) {
		this.tid = tid;
		this.teamDisplayName = teamDisplayName;
		this.used = used;
		this.total = total;
	}
	
	public TeamSpaceSize(int tid, long used, long total) {
		this.tid = tid;
		this.used = used;
		this.total = total;
	}

	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public long getUsed() {
		return used;
	}
	public void setUsed(long used) {
		this.used = used;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	public String getPercentDisplay() {
		if(StringUtils.isEmpty(percentDisplay)){
			long n = used*100/total;
			if(n>100){
				n=100;
			}
			percentDisplay = n+"%";
		}
		return percentDisplay;
	}


	public float getPercent() {
		percent = ((float)used)/total;
		return percent;
	}
	
	public String getUsedDisplay() {
		if(StringUtils.isEmpty(usedDisplay)){
			usedDisplay = getSize(used);
		}
		return usedDisplay;
	}
	
	private String getSize(long size){
		return FileSizeUtils.getFileSize(size);
	}
	public void setUsedDisplay(String usedDisplay) {
		this.usedDisplay = usedDisplay;
	}
	public String getTotalDisplay() {
		if(StringUtils.isEmpty(totalDisplay)){
			totalDisplay = getSize(total);
		}
		return totalDisplay;
		
	}
	public void setTotalDisplay(String totalDisplay) {
		this.totalDisplay = totalDisplay;
	}
	
	public boolean isApplication(){
		return (getTotal()-getUsed())<=FileSizeUtils.ONE_GB*5;
	}
	public String getTeamDisplayName() {
		return teamDisplayName;
	}
	public void setTeamDisplayName(String teamDisplayName) {
		this.teamDisplayName = teamDisplayName;
	}
	
}
