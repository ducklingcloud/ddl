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

package net.duckling.ddl.service.devent;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2011-10-31
 * @author clive
 */
public class DEntity {
	private String id;
	private String type;
	private String url;
	private String name;
	private String typeDisplay;

	public String getTypeDisplay() {
		return typeDisplay;
	}

	public void setTypeDisplay(String typeDisplay) {
		this.typeDisplay = typeDisplay;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}
	
	public DEntity(){};
	
	public DEntity(String id,String type){
		this.id = id;
		this.type = type;
		this.typeDisplay = getTypeDisplayName(type);
	}
	
	public DEntity(String id,String type,String name,String url){
		this.id = id;
		this.type = type;
		this.name = name;
		this.url = url;
		this.typeDisplay = getTypeDisplayName(type);
	}
	
	public static final String DPAGE = "DPage";
	public static final String DFILE = "DFile";
	public static final String DFOLDER="Folder";
	public static final String DTEAM = "DTeam";
	public static final String DCOLLECTION = "DCollection";
	public static final String DUSER = "DUser";
	//add by lvly @2012-06-13
	public static final String DTASK_SHARE="DTaskShare";
	public static final String DTASK_INDEPENDENT="DTaskIndependent";
	
	private static final Map<String,String> TYPE_DISPLAY_MAP = new HashMap<String,String>();
	private static final int DISPLAY_MAP_SIZE = 6;
	
	private static String getTypeDisplayName(String name){
		if(TYPE_DISPLAY_MAP.size() != DISPLAY_MAP_SIZE){
			TYPE_DISPLAY_MAP.put(DPAGE, "协作文档");
			TYPE_DISPLAY_MAP.put(DFILE, "文件");
			TYPE_DISPLAY_MAP.put(DTEAM, "团队");
			TYPE_DISPLAY_MAP.put(DCOLLECTION, "集合");
			TYPE_DISPLAY_MAP.put(DUSER, "好友");
			//add by lvly @2012-06-13
			TYPE_DISPLAY_MAP.put(DTASK_SHARE, "共享任务");
			TYPE_DISPLAY_MAP.put(DTASK_INDEPENDENT, "独立任务");
			TYPE_DISPLAY_MAP.put(DFOLDER, "文件夹");
		}
		return TYPE_DISPLAY_MAP.get(name);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id).append(";type=").append(type).append(";name=").append(name);
		sb.append(";typeDisplay=").append(typeDisplay).append(";url=").append(url);
		return sb.toString();
	}
}
