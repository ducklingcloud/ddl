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
package net.duckling.ddl.constant;
/**系统全局参数对象*/
public final class LynxConstants {
	private LynxConstants(){}
	public static final int INITIAL_VERSION = 0;
	public static final String STATUS_AVAILABLE = "available";
	/**
	 * 未发布类型，暂用于新建文档；（后台数据varchar(10)用unpublished会超长）
	 */
	public static final String STATUS_UNPUBLISH = "unpublish";
	public static final String STATUS_REMOVED = "removed";
	/**
	 * 分块上传的准备阶段
	 */
	public static final String STATUS_PREPARE = "prepare";
	public static final String TYPE_PAGE = "DPage";
	public static final String TYPE_FILE = "DFile";
	public static final String TYPE_BUNDLE = "Bundle";
	public static final String TYPE_FOLDER = "Folder";
	public static final String SEARCH_TYPE_PICTURE = "Picture";
	public static final String SRERCH_TYPE_NOPAGE = "NoPage";
	public static final String SEARCH_TYPE_EXCEPTFOLDER = "ExceptFolder";
	public static final String TYPE_FUNCTION = "JSP";
	public static final String TYPE_PORTAL = "Portal";
	public static final String TYPE_COLLECTION = "DCollection";
	public static final int NONE = 0;
	public static final int DEFAULT_SEQUENCE = 65535;
	public static final int DEFAULT_BID = 0;
	public static final String DEFAULT_PAGE_TITLE = "新建页面";
	public static final String DEFAULT_DDOC_TITLE = "新建协作文档";
	/**标记资源被删除*/
	public static final String STATUS_DELETE="delete";
	
	//默认显示txt和代码文件时文件大小不能超过此阈值
	public static final int MAXFILESIZE_CODEREVIEW = 1*1024*1024;
	
	public static final String PAGE_TITLE = "pageTitle";
	public static final String TEAM_TITLE = "teamTitle";
	
	public static final String UTF8 = "UTF-8";
	
	public static final String DESC = "desc";
	public static final String ASC = "asc";
}
