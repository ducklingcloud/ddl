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
 * @date 2011-11-1
 * @author clive
 */
public class DAction {
	public static final String DOWNLOAD = "download"; //file
	public static final String UPLOAD = "upload"; //file
	public static final String CREATE = "create"; //team,collection,page
	public static final String MODIFY = "modify"; //team,collection,page,file
	public static final String DELETE = "delete"; //team,collection,page,file,comment
	public static final String RECOVER = "recover";//team,file,page
	public static final String REPLY = "reply"; //comment
	public static final String COMMENT = "comment"; //page
	public static final String MENTION = "mention";//page file
	public static final String SHARE = "share"; //page,file
	public static final String JOIN = "join"; //team
	public static final String QUIT = "quit"; //team
	public static final String EXPORT = "export"; //collection,page,file
	public static final String IMPORT = "import"; //collection,page,file	
	public static final String RECOMMEND = "recommend"; //page,file
	public static final String FAVORITE = "favorite"; //page,file
	public static final String CANCEL_FAVORITE = "cancelFavorite"; //page,file
	public static final String FOLLOW = "follow"; //person
	public static final String CANCEL_FOLLOW = "cancelFollow"; //person
	public static final String RENAME = "rename"; //team,collection,page,file
	//add by zhonghui
	public static final String TEAM_COPY="teamCopy";
	public static final String TEAM_MOVE="teamMove";
	//add by lvly
	public static final String DELETE_TAKER="deleteTaker";
	public static final String PLUS_TAKER="plusTaker";
	public static final String UPDATE_TASK="updateTask";
	public static final String OVER_TASK="overTask";
	
	private String name;
	private String displayName;
	
	public DAction(String name){
		this.name = name;
		this.displayName = getDisplayName(name);
	}
	
	private static final Map<String,String> DISPLAY_MAP = new HashMap<String,String>();
	
	private static String getDisplayName(String name){
		if(DISPLAY_MAP.size()==0){
			DISPLAY_MAP.put(DOWNLOAD, "下载");
			DISPLAY_MAP.put(UPLOAD, "上传");
			DISPLAY_MAP.put(CREATE, "创建");
			DISPLAY_MAP.put(MODIFY, "修改");
			DISPLAY_MAP.put(DELETE, "删除");
			DISPLAY_MAP.put(REPLY, "回复");
			DISPLAY_MAP.put(COMMENT, "评论");
			DISPLAY_MAP.put(SHARE, "分享");
			DISPLAY_MAP.put(JOIN, "加入");
			DISPLAY_MAP.put(RECOMMEND, "分享");
			DISPLAY_MAP.put(RENAME, "重命名");
			DISPLAY_MAP.put(FOLLOW, "关注");
			DISPLAY_MAP.put(DELETE_TAKER, "被踢出");
			DISPLAY_MAP.put(PLUS_TAKER, "被请进");
			DISPLAY_MAP.put(UPDATE_TASK, "更新任务");
			DISPLAY_MAP.put(OVER_TASK, "已经完成任务");
			DISPLAY_MAP.put(RECOVER, "恢复");
			DISPLAY_MAP.put(MENTION, "@了我");
			DISPLAY_MAP.put(TEAM_COPY, "复制");
			DISPLAY_MAP.put(TEAM_MOVE, "移动");
		}
		return DISPLAY_MAP.get(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=").append(name);
		sb.append(";displayName").append(displayName);
		return sb.toString();
	}
}
