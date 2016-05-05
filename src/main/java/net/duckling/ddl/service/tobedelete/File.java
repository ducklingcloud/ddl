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
package net.duckling.ddl.service.tobedelete;

import java.util.Date;

import net.duckling.ddl.constant.LynxConstants;


public class File implements Cloneable{
	
	private int id;
	private int clbId;
	private String status;
	private int fid;
	private int tid;
	private String title;
	private Date createTime;
	private String creator;
	private String lastEditor;
	private Date lastEditTime;
	private int lastVersion;
	private int clbVersion;
	

	public int getClbVersion() {
		return clbVersion;
	}

	public void setClbVersion(int clbVersion) {
		this.clbVersion = clbVersion;
	}
	

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the clbId
	 */
	public int getClbId() {
		return clbId;
	}

	/**
	 * @param clbId the clbId to set
	 */
	public void setClbId(int clbId) {
		this.clbId = clbId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the fid
	 */
	public int getFid() {
		return fid;
	}

	/**
	 * @param fid the fid to set
	 */
	public void setFid(int fid) {
		this.fid = fid;
	}

	/**
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * @param tid the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the lastEditor
	 */
	public String getLastEditor() {
		return lastEditor;
	}

	/**
	 * @param lastEditor the lastEditor to set
	 */
	public void setLastEditor(String lastEditor) {
		this.lastEditor = lastEditor;
	}

	/**
	 * @return the lastEditTime
	 */
	public Date getLastEditTime() {
		return lastEditTime;
	}

	/**
	 * @param lastEditTime the lastEditTime to set
	 */
	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}

	/**
	 * @return the lastVersion
	 */
	public int getLastVersion() {
		return lastVersion;
	}

	/**
	 * @param lastVersion the lastVersion to set
	 */
	public void setLastVersion(int lastVersion) {
		this.lastVersion = lastVersion;
	}

	public boolean isAvailableFile(){
		return LynxConstants.STATUS_AVAILABLE.equals(this.getStatus());
	}
	
	public String getType() {
		return LynxConstants.TYPE_FILE;
	}
	
	public Object clone(){
		File file = new File();
		file.setClbId(clbId);
		file.setStatus(status);
		file.setTid(tid);
		file.setTitle(this.getTitle());
		file.setCreateTime(this.getCreateTime());
		file.setCreator(this.getCreator());
		file.setLastEditor(this.getLastEditor());
		file.setLastEditTime(this.getLastEditTime());
		file.setLastVersion(this.getLastVersion());
		return file;
	}
	
	public static boolean isOfficeFileTypeForSearch(String fileType){
		if("doc".equals(fileType)||"ppt".equals(fileType)||"xls".equals(fileType)){
			return true;
		}
		return false;
	}
	
	public static boolean isPdfFileTypeForSearch(String fileType){
		if("pdf".equals(fileType)){
			return true;
		}
		return false;
	}
	
	public static boolean isPictureFileTypeForSearch(String fileType){
		if(fileType!=null){
			String tempfileType = fileType.toLowerCase();
			if("png".equals(tempfileType)||"gif".equals(tempfileType)||"jpg".equals(tempfileType)
					|| "jpeg".equals(tempfileType)||"bmp".equals(tempfileType)||"tiff".equals(tempfileType)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isPicture(String title){
		if(null == title || "".equals(title) || !title.contains(".")){
			return false;
		}
		String suffix = title.substring(title.lastIndexOf('.')+1, title.length());
		return isPictureFileTypeForSearch(suffix);
	}
	
}
