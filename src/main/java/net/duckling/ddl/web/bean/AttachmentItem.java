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

package net.duckling.ddl.web.bean;

import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.util.MimeType;
import cn.vlabs.clb.api.document.MetaInfo;

/**
 * @date 2011-6-2
 * @author Clive Lee
 */
public class AttachmentItem {
	private String author;
	private String title;
	private long size;
	private String type;
	private String modifyTime;
	private int version;
	private int clbId; 
	private int fid; 
	private int rid;
	
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public int getClbId() {
		return clbId;
	}
	public void setClbId(int clbId) {
		this.clbId = clbId;
	}
	public int getFid() {
		return fid;
	}
	public void setFid(int fid) {
		this.fid = fid;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public static AttachmentItem convertFromAttachment(FileVersion version) {
		AttachmentItem instance = new AttachmentItem();
		instance.setTitle(version.getTitle());
		instance.setSize(version.getSize());
		instance.setVersion(version.getVersion());
		instance.setModifyTime(version.getEditTime().toString());
		instance.setAuthor(version.getEditor());
		instance.setType(getFileExtend(version.getTitle()));
		instance.setRid(version.getRid());
		return instance;
	}
	
	private static String getFileExtend(String filename) {
		if(MimeType.isImage(filename))
		{
			return "IMAGE";
		}
		return "FILE";
	}
	
	public static AttachmentItem convertFromMetaInfo(MetaInfo metaInfo) {
		AttachmentItem instance = new AttachmentItem();
		instance.setClbId(metaInfo.getDocid());
		instance.setTitle(metaInfo.getTitle());
		instance.setSize(metaInfo.getSize());
		instance.setVersion(Integer.parseInt(metaInfo.getVersion()));
		instance.setModifyTime(metaInfo.getLastUpdate().toGMTString());
		instance.setAuthor(metaInfo.getCreatBy());
		instance.setType(getFileExtend(metaInfo.getTitle()));
		return instance;
	}
	
}
