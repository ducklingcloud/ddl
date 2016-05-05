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
package net.duckling.ddl.service.relaterec.impl;

public class PageSimilar implements Comparable<PageSimilar>{
	private int id;
	private double similar;
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setSimilar(double similar) {
		this.similar = similar;
	}
	public double getSimilar() {
		return similar;
	}
	public PageSimilar(int id,double similar){
		this.id = id;
		this.similar = similar;
	}
	
	
	public int compareTo(PageSimilar o) {
		// TODO Auto-generated method stub
		if(this.similar <= o.similar){
			return -1;
		}
		
		return 1;
	}
}
