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
package net.duckling.ddl.service.grid.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import net.duckling.ddl.service.grid.GridItem;

public class GridGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String uid;
    private int tid;
    private Date lastEditTime;
    private Map<String, GridItem> gridItemMap;

    public GridGroup(String uid, int tid) {
        this.id = 0;
        this.uid = uid;
        this.tid = tid;
        this.lastEditTime = new Date();
        this.gridItemMap = new HashMap<String, GridItem>();
    }

    public GridGroup() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public Date getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Date lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Map<String, GridItem> getGridItemMap() {
        return gridItemMap;
    }
    public Map<String,String> getGridItemJsonMap(){
    	Map<String,String> result = new HashMap<String,String>();
    	for(Entry<String, GridItem> item : gridItemMap.entrySet()){
    		if(item.getValue()!=null){
    			result.put(item.getKey(), item.getValue().toJSONString());
    		}
    	}
    	return result;
    }
    public void setGridItemMap(Map<String, GridItem> gridItemMap) {
        this.gridItemMap = gridItemMap;
    }
    public void setGridItemJSONMap(Map<String,String> map){
    	if(map!=null){
    		Map<String,GridItem> result = new HashMap<String,GridItem>();
    		for(Entry<String,String> item : map.entrySet()){
    			if(StringUtils.isNotEmpty(item.getValue())){
    				GridItem r = GridItem.parseFromJOSN(item.getValue());
    				if(r!=null){
    					result.put(item.getKey(), r);
    				}
    			}
    		}
    		this.gridItemMap = result;
    	}
    }
}
