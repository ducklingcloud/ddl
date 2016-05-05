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

import java.util.Date;
import java.util.HashSet;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.file.FileVersion;


public final class ResourceBuilder {
	private ResourceBuilder(){}
	
	
	public static Resource getNewResource(int tid,int parentRid,String uid,String title){
		Date d = new Date();
		Resource res = new Resource();
		res.setCreateTime(d);
		res.setCreator(uid);
		res.setBid(parentRid);
		res.setLastEditor(uid);
		res.setLastEditTime(d);
		res.setLastVersion(LynxConstants.INITIAL_VERSION);
		res.setTitle(title);
		res.setTid(tid);
		res.setStatus(LynxConstants.STATUS_AVAILABLE);
		return res;
	}
	public static Resource getNewFolder(int tid,int parentRid,String uid,String title,long size){
		Resource r = getNewResource(tid, parentRid, uid, title);
		r.setSize(size);
		r.setItemType(LynxConstants.TYPE_FOLDER);
		r.setOrderType(Resource.FOLDER_ORDER_TYPE);
		return r;
	}
	public static Resource getNewFile(int tid,int parentRid,String uid,String title, String fileType,long size){
		Resource r = getNewResource(tid, parentRid, uid, title);
		r.setSize(size);
		r.setItemType(LynxConstants.TYPE_FILE);
		r.setFileType(fileType);
		r.setOrderType(Resource.NO_FOLDER_ORDER_TYPE);
		return r;
	}
	public static Resource getNewPage(int tid,int parentRid,String uid,String title,long size){
		Resource r = getNewResource(tid, parentRid, uid, title);
		r.setFileType("ddoc");
		r.setSize(size);
		r.setItemType(LynxConstants.TYPE_PAGE);
		r.setOrderType(Resource.NO_FOLDER_ORDER_TYPE);
		return r;
	}

	public static void updateResource(Resource res,FileVersion fileVersion){
		String title = fileVersion.getTitle();
		res.setLastEditor(fileVersion.getEditor());
		res.setLastEditTime(fileVersion.getEditTime());
		res.setLastVersion(fileVersion.getVersion());
		res.setTitle(fileVersion.getTitle());
		res.setFileType(title.substring(title.lastIndexOf('.')+1, title.length()));
		res.setSize(fileVersion.getSize());
		res.setStatus(LynxConstants.STATUS_AVAILABLE);
	}
	
	public static Resource build(Bundle b) {
		Resource res = new Resource();
		res.setCreateTime(b.getCreateTime());
		res.setCreator(b.getCreator());
		res.setLastEditor(b.getLastEditor());
		res.setLastEditTime(b.getLastEditTime());
		res.setLastVersion(b.getLastVersion());
		res.setTitle(b.getTitle());
		res.setTid(b.getTid());
		res.setItemType(LynxConstants.TYPE_BUNDLE);
		res.setFileType(null);
		res.setMarkedUserSet(new HashSet<String>());
		res.setBid(LynxConstants.DEFAULT_BID);
		res.setOrderTitle(b.getTitle());
		res.setOrderDate(b.getLastEditTime());
		return res;
	}
	
	public static SimpleResource getSimpleResource(Resource resource) {
		SimpleResource sr = new SimpleResource();
		sr.setRid(resource.getRid());
		sr.setRid(resource.getRid());
		sr.setItemType(resource.getItemType());
		sr.setTid(resource.getTid());
		return sr;
	}
	
	public static String getItemKey(int rid, String itemType, int tid) {
		return rid+"_"+itemType+"_"+tid;
	}
	
	public static String getItemKey(SimpleResource s){
		return getItemKey(s.getRid(),s.getItemType(),s.getTid());
	}
	
	public static String getItemKey(Resource r){
		return getItemKey(r.getRid(),r.getItemType(),r.getTid());
	}
}
