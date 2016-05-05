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

import java.util.HashMap;
import java.util.Map;

public class UserVSM {
	private String userID;
	private Map<Integer,Double> vector;
	/**
	 * @return the userID
	 */
	public UserVSM(){
		vector = new HashMap<Integer,Double>();
	}
	
	public String getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * @return the vector
	 */
	public Map<Integer,Double> getVector() {
		return vector;
	}
	/**
	 * @param vector the vector to set
	 */
	public void setVector(Map<Integer,Double> vector) {		
		this.vector = vector;
	}
	
	public void addToVector(FeatureWeight[] weights){
		for(FeatureWeight fw: weights){
			Double w = vector.get(fw.getIndex());
			if(w == null){
				vector.put(fw.getIndex(), fw.getWeight());
			}
			else{
				vector.put(fw.getIndex(), w + fw.getWeight());
			}
		}
			
	}
	
}