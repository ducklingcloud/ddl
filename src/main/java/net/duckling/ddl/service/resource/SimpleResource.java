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

import java.io.Serializable;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.util.JsonUtil;

public class SimpleResource implements Serializable {
	
	private static final long serialVersionUID=1l;
	
	private int rid;
	private String itemType;
	private int tid;
	
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
	 * @return the rid
	 */
	public int getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
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
	
	public boolean equalsPageType(){
		return LynxConstants.TYPE_PAGE.equals(getItemType());
	}
	
	public boolean equalsBundleType(){
		return LynxConstants.TYPE_BUNDLE.equals(getItemType());
	}
	
	public boolean equalsFileType(){
		return LynxConstants.TYPE_FILE.equals(getItemType());
	}
	
	public String toString(){
		return JsonUtil.getJSONString(this);
	}
	
}
