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
package net.duckling.ddl.web.controller.pan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileSizeUtils;

import org.apache.commons.lang.StringUtils;

import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.MeePoRevision;
import com.meepotech.sdk.PanQueryResult;
import com.meepotech.sdk.PanMeta;

public class MeePoMetaToPanBeanUtil {
	
	public static PanResourceBean transfer(MeePoMeta meta,SimpleUser user){
		PanResourceBean bean = new PanResourceBean();
    	bean.setCreateTime(new Date(meta.created));
    	if(user!=null){
    		bean.setCreator(user.getUid());
    		bean.setLastEditor(user.getUid());
    	}
    	bean.setModifyTime(new Date(meta.modified));
    	bean.setFileType(getFileType(meta.name));
    	bean.setItemType(getFileType(meta.isDir));
    	bean.setPath(meta.restorePath);
		bean.setRid(encode(meta.restorePath));
    	bean.setSize(meta.size);
    	bean.setSizeStr(FileSizeUtils.getFileSize(meta.size));
    	bean.setTitle(meta.name);
    	bean.setMeePoVersion((int)meta.version);
    	bean.setVersion((int)meta.version);
    	bean.setParentPath(getParentRid(meta.restorePath));
    	return bean;
	}
	
	public static PanResourceBean transfer(PanMeta meta,SimpleUser user){
		PanResourceBean bean = new PanResourceBean();
    	bean.setCreateTime(new Date(meta.getCreated_millis()));
    	if(user!=null){
    		bean.setCreator(user.getUid());
    		bean.setLastEditor(user.getUid());
    	}
    	bean.setModifyTime(new Date(meta.getModified_millis()));
    	bean.setFileType(getFileType(meta.getName()));
    	bean.setItemType(getFileType(meta.isIs_dir()));
    	bean.setPath(meta.getPath());
		bean.setRid(encode(meta.getPath()));
    	bean.setSize(meta.getBytes());
    	bean.setSizeStr(meta.getSize());
    	bean.setTitle(meta.getName());
    	bean.setMeePoVersion(meta.getVersion());
    	bean.setVersion(meta.getVersion());
    	bean.setParentPath(getParentRid(meta.getPath()));
    	return bean;
	}
	
	public static PanResourceBean transferSearchResoult(PanQueryResult meta,SimpleUser user){
		PanResourceBean bean = new PanResourceBean();
		bean.setCreateTime(new Date(meta.getCreated_millis()));
    	bean.setCreator(user.getUid());
    	bean.setLastEditor(user.getUid());
    	bean.setModifyTime(new Date(meta.getModified_millis()));
    	bean.setFileType(getFileType(meta.getName()));
    	bean.setItemType(getFileType(meta.isIs_dir()));
    	bean.setPath(meta.getPath());
		bean.setRid(encode(meta.getPath()));
    	bean.setSize(meta.getBytes());
    	bean.setSizeStr(meta.getSize());
    	bean.setTitle(meta.getName());
    	bean.setMeePoVersion((int)meta.getVersion());
    	bean.setParentPath(getParentRid(meta.getPath()));
    	bean.setBeanType(PanResourceBean.BEAN_TYPE_SEARCH);
		return bean;
	}
	
	private static String encode(String s){
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	private static String getParentRid(String s){
		if(StringUtils.isEmpty(s)||"/".equals(s)){
			return "/";
		}else{
			int index = s.lastIndexOf("/");
			if(index!=-1){
				return encode(s.substring(0, index));
			}else{
				return s;
			}
		}
	}
	public static String getFileType(String fileName){
    	if(StringUtils.isEmpty(fileName)){
    		return "";
    	}
    	int index = fileName.lastIndexOf(".");
    	if(index==-1){
    		return "";
    	}
    	return fileName.toLowerCase().substring(index+1);
    }

	private static String getFileType(boolean isDir) {
		if(isDir){
			return LynxConstants.TYPE_FOLDER;
		}else{
			return LynxConstants.TYPE_FILE;
		}
	}
	public static PanResourceBean transfer(MeePoMeta meta,MeePoRevision vision,SimpleUser user){
		PanResourceBean bean = new PanResourceBean();
		bean.setCreateTime(new Date(meta.created));
    	bean.setCreator(user.getUid());
    	bean.setLastEditor(user.getUid());
    	bean.setModifyTime(new Date(vision.modified));
    	bean.setFileType(getFileType(meta.name));
    	bean.setItemType(getFileType(meta.isDir));
    	bean.setPath(meta.restorePath);
		bean.setRid(encode(meta.restorePath));
    	bean.setSize(vision.size);
    	bean.setSizeStr(vision.sizeString);
    	bean.setTitle(meta.name);
    	bean.setMeePoVersion((int)vision.version);
    	bean.setParentPath(getParentRid(meta.restorePath));
    	return bean;
	}
	
}
