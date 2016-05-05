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
package net.duckling.ddl.web.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.constant.SupportedFileForOfficeViewer;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.web.controller.pan.PanResourceBean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import cn.vlabs.clb.api.io.impl.MimeType;

public class LynxResourceUtils {
	
	@SuppressWarnings("unchecked")
	public static JSONArray getResourceJSON(List<Resource> rs,String uid){
		JSONArray array = new JSONArray();
		for(Resource r:rs){
			JSONObject o = getResourceJson(uid, r);
			array.add(o);
		}
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getResourceJson(String uid,Resource r) {
		JSONObject o = new JSONObject();
		o.put("rid", r.getRid());
		o.put("fileName", r.getTitle());
		o.put("itemType", r.getItemType());
		o.put("parentRid", r.getBid());
		o.put("createTime", formatTime(r.getCreateTime()));
		o.put("modofyTime", formatTime(r.getLastEditTime()));
		String userName = DDLFacade.getBean(AoneUserService.class).getUserNameByID(r.getLastEditor());
		o.put("lastEditor",userName);
		o.put("lastEditorUid", r.getLastEditor());
		o.put("isOffice", "false");
		if(r.isFile()||r.isPage()){
			o.put("fileType", r.getFileType());
			URLGenerator urlGenerator = (URLGenerator)DDLFacade.getBean(URLGenerator.class);
			if(SupportedFileForOfficeViewer.isSupportedFile(r.getTitle())){
				o.put("isOffice", "true");
				String url = urlGenerator.getURL(r.getTid(), UrlPatterns.T_PREVIEW_OFFICE, r.getRid()+"", "redirect=redirect&from=web");
				o.put("previewUrl", url);
			}else if(r.isPage()){
				String url = urlGenerator.getURL(r.getTid(), UrlPatterns.T_VIEW_R, r.getRid()+"",null);
				o.put("previewUrl", url);
			}else{
				String url = urlGenerator.getURL(r.getTid(), UrlPatterns.T_PREVIEW_OFFICE, r.getRid()+"", "redirect=redirect&from=web");
				o.put("previewUrl", url);
			}
		}else{
			o.put("fileType", "");
		}
		
		//TODO
		o.put("parentRid", r.getBid());
		o.put("tags",getTags(r.getTagMap()));
		o.put("star", isUserStar(uid, r.getMarkedUserSet()));
		o.put("lastVersion",r.getLastVersion());
		o.put("status", r.getStatus());
		String contentType = StringUtils.isEmpty(r.getFileType()) ? "" : MimeType.getContentType(r.getFileType());
		o.put("contentType", contentType);
		o.put("size", FileSizeUtils.getFileSize(r.getSize()));
		o.put("shared", r.isShared());
		return o;
	}
	
	private static String isUserStar(String uid,Set<String> stars){
		if(stars==null){
			return "unchecked";
		}else if(stars.contains(uid)){
			return "checked";
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private static JSONArray getTags(Map<Integer,String> tags){
		JSONArray a = new JSONArray();
		if(tags==null){
			return a;
		}
		for(Entry<Integer,String> e : tags.entrySet()){
			JSONObject o = new JSONObject();
			o.put("tagId", e.getKey());
			o.put("tagValue", e.getValue());
			a.add(o);
		}
		return a;
	}
	
	private static String formatTime(Date date){
		if(date==null){
			return "";
		}
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return d.format(date);
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getPanResourceJSON(List<PanResourceBean> resources, String uid) {
		JSONArray arr = new JSONArray();
		if(resources!=null)
		for(PanResourceBean b : resources){
			arr.add(getPanResourceJson(b, uid));
		}
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPanResourceJson(PanResourceBean r, String uid) {
		JSONObject o = new JSONObject();
		o.put("rid", r.getRid());
		o.put("fileName", r.getTitle());
		o.put("itemType", r.getItemType());
		o.put("parentRid", r.getParentPath());
		o.put("createTime", formatTime(r.getCreateTime()));
		o.put("modofyTime", formatTime(r.getModifyTime()));
		if(!StringUtils.isEmpty(r.getLastEditor())){
			String userName = DDLFacade.getBean(AoneUserService.class).getUserNameByID(r.getLastEditor());
			o.put("lastEditor",userName);
			o.put("lastEditorUid", r.getLastEditor());
		}
		if(r.isFile()){
			o.put("fileType", r.getFileType());
		}else{
			o.put("fileType", "");
		}
		o.put("parentRid", r.getParentPath());
		o.put("lastVersion",r.getVersion());
		String contentType = StringUtils.isEmpty(r.getFileType()) ? "" : MimeType.getContentType(r.getFileType());
		o.put("contentType", contentType);
		o.put("size", FileSizeUtils.getFileSize(r.getSize()));
		if(r.isSearchResult()){
			dealSearchResult(o,r);
		}else{
			o.put("searchResult", false);
		}
		o.put("shared", r.getShared());
		return o;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getPanResourceJsonList(List<PanResourceBean> resources, String uid) {
		JSONArray arr = new JSONArray();
		if(resources!=null)
		for(PanResourceBean b : resources){
			arr.add(getPanResourceJson(b, uid));
		}
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	private static void dealSearchResult(JSONObject o, PanResourceBean r) {
		o.put("searchResult", true);
		try{
			String p = URLDecoder.decode(r.getParentPath(), "utf-8");
			if(StringUtils.isEmpty(p)||"/".equals(p)){
				o.put("parentPathName", "所有文件");
			}else{
				o.put("parentPathName", p);
			}
		}catch(Exception e){}
		
		String path = r.getPath();
		int index = path.lastIndexOf("/");
		if(index>0){
			String tmp = path.substring(0, index);
			int be = tmp.lastIndexOf("/");
			if(be!=-1){
				String re = tmp.substring(be+1);
				if("/".equals(re)){
					o.put("parentName", "所有文件");
				}else{
					o.put("parentName", re);
				}
				return ;
			}
			
		}
		o.put("parentName", "所有文件");
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getPanResourceList(List<PanResourceBean> resources, String uid) {
		JSONArray arr = new JSONArray();
		if(resources!=null)
		for(PanResourceBean b : resources){
			arr.add(getPanResource(uid, b));
		}
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray getPanResourceListLite(List<PanResourceBean> resources, String uid) {
		JSONArray arr = new JSONArray();
		if(resources!=null)
		for(PanResourceBean b : resources){
			arr.add(getPanResourceLite(uid, b));
		}
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPanResource(String uid,PanResourceBean r) {
		JSONObject o = new JSONObject();
		o.put("rid", r.getRid());
		o.put("title", r.getTitle());
		o.put("itemType", r.getItemType());
		o.put("bid", r.getParentPath());
		o.put("createTime", formatTime(r.getCreateTime()));
		o.put("lastEditTime", formatTime(r.getModifyTime()));
		if(!StringUtils.isEmpty(r.getLastEditor())){
			String userName = DDLFacade.getBean(AoneUserService.class).getUserNameByID(r.getLastEditor());
			o.put("lastEditorName",userName);
			o.put("creatorName",userName);
			o.put("lastEditor", r.getLastEditor());
			o.put("creator", r.getLastEditor());
		}
		if(r.isFile()){
			o.put("fileType", r.getFileType());
		}else{
			o.put("fileType", "");
		}
		o.put("file", r.isFile());
		o.put("bundle", r.isFolder());
		o.put("folder", r.isFolder());
		o.put("parentRid", r.getParentPath());
		o.put("lastVersion",r.getVersion());
		String contentType = StringUtils.isEmpty(r.getFileType()) ? "" : MimeType.getContentType(r.getFileType());
		o.put("contentType", contentType);
		o.put("size", r.getSize());
		o.put("sizeStr", FileSizeUtils.getFileSize(r.getSize()));
		return o;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPanResourceLite(String uid,PanResourceBean r) {
		JSONObject o = new JSONObject();
		o.put("rid", r.getRid());
		return o;
	}
}