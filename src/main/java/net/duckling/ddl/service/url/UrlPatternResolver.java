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

package net.duckling.ddl.service.url;

import java.util.HashMap;
import java.util.Map;

/**
 * URL Pattern定义
 * @date May 19, 2011
 * @author xiejj@cnic.cn
 */
public class UrlPatternResolver extends UrlPatterns {
	
	
    /**
	 * URLPattern中包含以下内容时:
	 * %u		使用相对地址时使用basepath替换,使用绝对地址时使用baseURL替换
	 * %U		使用绝对地址BaseURL替换
	 * %p		用basePath替换
	 * %n		用page替换
	 * %v		用ViewPort替换(page/)
	 * %t		Team ID
	 */
	private HashMap<String, String> _PATTERNS;
	private HashMap<String, String> _REVERSE_PATTHERNS;
	public UrlPatternResolver(){
		//反向解析URL Patterns
		_REVERSE_PATTHERNS = new HashMap<String, String>();
		_REVERSE_PATTHERNS.put(T_ATTACH, "/attach/{key}");
		_REVERSE_PATTHERNS.put(T_DOWNLOAD, "/downloadResource/{key}");
		_REVERSE_PATTHERNS.put(T_DOWNLOAD_CACHE, "/downloadResource/{key}?func=cache");
		_REVERSE_PATTHERNS.put(T_CACHABLE, "/cachable/{key}");
		_REVERSE_PATTHERNS.put(T_FILE, "/file/{key}");
		_REVERSE_PATTHERNS.put(T_PAGE, "/f/{key}");
		_REVERSE_PATTHERNS.put(PORTLET, "/portal/{key}");
		_REVERSE_PATTHERNS.put(VIEW_COLLECTION, "/collection/{key}");
		_REVERSE_PATTHERNS.put(T_EDIT_PAGE, "/edit\\?rid={key}");
		
		_PATTERNS = new HashMap<String, String>();

		//Portal
		_PATTERNS.put(PORTLET, "%u/%t/portal/%n");
		_PATTERNS.put(SIMPLE, "%U/%t/portal/%n");
		//DPage

		
		//VWBCommand
		_PATTERNS.put(PLAIN,"%u/%n");
		_PATTERNS.put(CONTACTS, "%u/dashboard/contacts");
		_PATTERNS.put(LOGIN, "%U/system/login");
		_PATTERNS.put(LOGOUT, "%U/system/logout");
		_PATTERNS.put(ESCIENCE_REGIST,"%U/system/escienceregist");
		_PATTERNS.put(TO_DHOME, "%U/system/toDhome");
		
		//CollectionCommand
		_PATTERNS.put(VIEW_COLLECTION, "%u/%t/collection/%n");
		_PATTERNS.put(CONFIG_COLLECTION,"%u/%t/configCollection");
		_PATTERNS.put("adminCollection", "%u/%t/adminCollection");
		_PATTERNS.put(CREATE_COLLECTION,"%u/%t/createCollection");
		_PATTERNS.put("editCollections", "%u/%t/editCollections");
		
		//TeamCommand
//		_PATTERNS.put("configTeam", "%u/system/configTeam?teamCode=%n&func=adminApplicant");
		_PATTERNS.put(CONFIG_TEAM, "%u/system/configTeam?teamCode=%n");
		_PATTERNS.put(CREATE_TEAM, "%u/system/createTeam");
		_PATTERNS.put(SWITCH_TEAM, "%u/system/switch");
		
		//share file
		_PATTERNS.put(SHARE_FILE, "%u/system/shareFile");
		_PATTERNS.put(SHARE_FILE_AGAIN, "%u/system/shareAgain");
		_PATTERNS.put(SHARE_FILE_SUCCESS, "%u/system/shareFileSuccess");
		_PATTERNS.put(DIRECT, "%u/direct/%n");
		//copy file 
		_PATTERNS.put(COPYFILE, "%u/copyfile/%n");
		
		//System URL
		_PATTERNS.put(REGIST, "%u/system/regist");
		_PATTERNS.put(REGIST_CODE, "%u/t");
		_PATTERNS.put(USER_HOME, "%u/home"); 
		_PATTERNS.put(USER, "%u/system/user/%n");
		_PATTERNS.put(INVITE, "%u/system/invite/%n");
		_PATTERNS.put(ACTIVITION, "%u/system/activation/%n");
		_PATTERNS.put(FEEDBACK, "%u/system/feedback");
		_PATTERNS.put(ADMIN_AONE_FEEDBACK, "%U/aone/admin/feedback");
		_PATTERNS.put(QUIT_TEAM, "%u/system/quitTeam");
		_PATTERNS.put(LOGGING, "%u/system/logger");
		_PATTERNS.put(CHECK_SERVER_STATE, "%u/system/check");
		_PATTERNS.put(PICTURE_CHECK, "%u/system/pictrueCheckCode");
		_PATTERNS.put(DATA_SYNC, "%u/system/data/synchronized");
		_PATTERNS.put(JOIN_PUBLIC_TEAM, "%u/system/joinPublicTeam");
		_PATTERNS.put(USER_GUIDE, "%u/system/userguide");
		_PATTERNS.put(USER_GUIDE, "%u/system/userguide");
		_PATTERNS.put(GLOBAL_SEARCH, "%u/system/search");
	    _PATTERNS.put(DASHBOARD, "%u/dashboard");
	    _PATTERNS.put(MYSPACE, "%u/myspace");
		
	    _PATTERNS.put(RESOURCE_SHARE, "%u/f");
	    _PATTERNS.put(PAN_SHARE, "%u/ff");
	    
        _PATTERNS.put(T_SUBSCRIBE, "%u/%t/subscribePage?rid=%n");
        _PATTERNS.put(T_LIST, "%u/%t/list");
        _PATTERNS.put(T_VIEW_R, "%u/%t/r/%n");
        
        _PATTERNS.put(T_PAGE, "%u/%t/page/%n");
        _PATTERNS.put(T_BUNDLE, "%u/%t/bundle/%n");
        _PATTERNS.put(T_FILE, "%u/%t/file/%n");
        _PATTERNS.put(T_INFO_FILE, "%u/%t/infoFile?rid=%n");

        _PATTERNS.put(T_EDIT_PAGE, "%u/%t/edit?rid=%n");
        _PATTERNS.put(T_CREATE_PAGE, "%u/%t/createPage?cid=%n");
        _PATTERNS.put(T_DIFF, "%u/%t/infoPage?rid=%n&func=diff");
        _PATTERNS.put(T_INFO, "%u/%t/infoPage?rid=%n");
        _PATTERNS.put(T_EXPORT, "%u/%t/exportPage?rid=%n");
        _PATTERNS.put(T_SHARE_PAGE, "%u/%t/sharePage?rid=%n");
        _PATTERNS.put(T_PREVIEW, "%u/%t/edit?func=previewToEdit&rid=%n");
        _PATTERNS.put(T_PREVIEW_OFFICE, "%u/%t/preview/%n");
	    _PATTERNS.put(T_TEAM, "%u/%t/%n");
        _PATTERNS.put(T_ATTACH, "%u/%t/attach/%n");
        _PATTERNS.put(T_CACHABLE, "%u/%t/cachable/%n");
        _PATTERNS.put(T_DOWNLOAD, "%u/%t/downloadResource/%n");
        _PATTERNS.put(T_DOWNLOAD_CACHE, "%u/%t/downloadResource/%n?func=cache");
		_PATTERNS.put(T_RECOMMEND, "%u/%t/recommend");
		_PATTERNS.put(T_FEED, "%u/%t/feed");
		_PATTERNS.put(T_RECLOGGING, "%u/%t/reclogger");
		_PATTERNS.put(T_NINE_GRID, "%u/%t/home");
		_PATTERNS.put(T_TAG, "%u/%t/tag");
		_PATTERNS.put(T_NOTICE, "%u/%t/notice");
		_PATTERNS.put(T_TEAM_HOME, "%u/%t");

		_PATTERNS.put(T_CONFIG_TAG, "%u/%t/config/tag");
		_PATTERNS.put(T_STARTMARK, "%u/%t/starmark");
		_PATTERNS.put(T_CREATE_BUNDLE, "%u/%t/createBundle");
		_PATTERNS.put(T_RESOURCE, "%u/%t/resource");
		_PATTERNS.put(T_COMMENT, "%u/%t/comment");
		_PATTERNS.put(T_NAVBAR, "%u/%t/navbar");
		_PATTERNS.put(T_TASK_PATTERNS, "%u/%t/task");
		_PATTERNS.put(T_COPY_PATTERNS, "%u/%t/copy");
		_PATTERNS.put(T_BUNDLE_BASE, "%u/%t/bundle");
		_PATTERNS.put(T_CONFIG_SHORT_CUT, "%u/%t/configShortCut");
        _PATTERNS.put(T_SEARCH, "%u/%t/search");
        // Upload Command
        _PATTERNS.put(T_UPLOAD, "%u/%t/upload");

        _PATTERNS.put(T_ORIGINAL_IMAGE, "%u/%t/originalimage/%n");
        // quick upload
        _PATTERNS.put(T_QUICK, "%u/%t/quick");
        //查看文件或文档
        _PATTERNS.put(T_VIEW_FILE, "%u/%t/r/%n");
		
        _PATTERNS.put(PAN_LIST, "%u/pan/list");
        _PATTERNS.put(PAN_VIEW, "%u/pan/list#path=%n");
        _PATTERNS.put(PAN_DOWNLOAD, "%u/pan/download?path=%n");
        _PATTERNS.put(PAN_PREVIEW, "%u/pan/preview?path=%n");
        _PATTERNS.put(PAN_UPLOAD, "%u/pan/upload?path=%n");
        _PATTERNS.put(PAN_THUMBNAILS, "%u/pan/download/thumbnails?path=%n");
        _PATTERNS.put(PAN_HISTORY, "%u/pan/history?path=%n");
        _PATTERNS.put(PAN_APPLICATION, "%u/pan/applicationSpace");
        
		
	}
	public Map<String, String> getPatterns(){
		return _PATTERNS;
	}
	public Map<String, String> getReversPatterns(){
		return _REVERSE_PATTHERNS;
	}
}
