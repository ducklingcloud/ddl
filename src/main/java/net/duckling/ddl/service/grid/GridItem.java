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
package net.duckling.ddl.service.grid;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONObject;


public class GridItem implements Serializable,Comparable<GridItem> {
	private static final long serialVersionUID = 1L;
	
	private int tid;
	private String uid;
	private String itemType;
	private Double score;
	private boolean fixed;
	private int rid;
	public GridItem(){}
	public GridItem(int tid,String uid,int rid,String itemType,Double score,boolean fixed){
		this.tid = tid;
		this.uid = uid;
		this.rid = rid;
		this.itemType = itemType;
		this.score = score;
		this.fixed = fixed;
	}
	
	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}
	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}
	/**
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}
	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	/**
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}
	/**
	 * @param tid the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}
	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	/**
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	public int compareTo(GridItem other) {
		if (this.getScore().compareTo(other.getScore()) < 0) {
			return 1;
		}
		if (this.getScore().compareTo(other.getScore()) > 0) {
			return -1;
		}
		if (this.getRid() > other.getRid()) {
			return 1;
		}
		if (this.getRid() < other.getRid()) {
			return -1;
		}
		if(this.getItemType().compareTo(other.getItemType())>0) {
			return 1;
		}
		if(this.getItemType().compareTo(other.getItemType())<0) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) 	{
			return true;
		}
		if (!(o instanceof GridItem)) {
			return false;
		}
		final GridItem other = (GridItem) o;
		if (this.getScore() .equals( other.getScore()) && this.getRid() == other.getRid() && this.getItemType().equals(other.getItemType())){
			return true;
		}
		return false;
	}
	
	public String toJSONString(){
		JSONObject o = new JSONObject();
		o.put("uid", getUid());
		o.put("tid",getTid());
		o.put("rid", getRid());
		o.put("itemType", getItemType());
		o.put("score", getScore());
		o.put("fixed", isFixed());
		return o.toString();
	}
	
	public static GridItem parseFromJOSN(String s){
		try {
			JSONObject j = new JSONObject(s);
			GridItem result = new GridItem();
			result.setTid(j.getInt("tid"));
			result.setUid(j.getString("uid"));
			result.setRid(j.getInt("rid"));
			result.setItemType(j.getString("itemType"));
			result.setScore(j.getDouble("score"));
			result.setFixed(j.getBoolean("fixed"));
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
