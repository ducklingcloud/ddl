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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 分页信息
 * @author zhonghui
 *
 * @param <T>
 */

@JsonPropertyOrder(value={"begin", "limit", "total", "data" })
@JsonIgnoreProperties( {"end","loadedNum","nextStartNum"})
public class PaginationBean <T> {
	private int begin;
	@JsonProperty("limit") 
	private int size;
	private int end;
	private int total;
	private List<T> data;
	public PaginationBean(){
		
	}
	
	public PaginationBean(List<T> data){
		if(data==null){
			data=new ArrayList<T>();
		}
		
		this.begin=0;
		this.size=data.size();
		this.end=data.size()-1;
		this.total=data.size();
		this.data=data;
	}
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getNextStartNum(){
		return getBegin()+getData().size();
	}
	
	public int getLoadedNum(){
		return getData().size();
	}
	
	
}
