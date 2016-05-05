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
package net.duckling.ddl.service.file;

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.service.resource.Resource;

public class ZipResourceTree {
	private ZipResourceTree parent;
	private List<ZipResourceTree> children;
	private String path;
	private Resource resource;
	public ZipResourceTree getParent() {
		return parent;
	}
	public void setParent(ZipResourceTree parent) {
		this.parent = parent;
	}
	public String getPath() {
		if(path!=null){
			return path;
		}else if(parent!=null){
			return parent.getPath()+"/"+resource.getTitle();
		}else{
			return resource.getTitle();
		}
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public List<ZipResourceTree> getChildren() {
		return children;
	}
	public void addChild(ZipResourceTree child){
		if(children==null){
			children = new ArrayList<ZipResourceTree>();
		}
		children.add(child);
	}
	
	
}
