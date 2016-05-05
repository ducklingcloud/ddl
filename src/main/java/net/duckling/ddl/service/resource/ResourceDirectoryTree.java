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
import java.util.ArrayList;
import java.util.List;

/**
 * resource目录树，用于数据的删除记录。 
 * @author zhonghui
 *
 */
public class ResourceDirectoryTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6943239666108560675L;
	
	private int rid;
	private int parentRid;
	private String itemType;
	private List<ResourceDirectoryTree> children;
	public ResourceDirectoryTree(int rid,String itemType,int parentRid){
		this.rid = rid;
		this.itemType = itemType;
		this.parentRid = parentRid;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getItemType() {
		return itemType;
	}
	public int getParentRid() {
		return parentRid;
	}
	public void setParentRid(int parentRid) {
		this.parentRid = parentRid;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public List<ResourceDirectoryTree> getChildren() {
		return children;
	}
	public void addChildren(ResourceDirectoryTree r){
		if(children==null){
			initChildren();
		}
		children.add(r);
	}
	
	private synchronized void initChildren() {
		if(children == null){
			children = new ArrayList<ResourceDirectoryTree>();
		}
	}
	
}
