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

public class PageInfo{
	
	private String fileType;
	private String fileExtend;
	private boolean tagExist;
	private String downloadUrl;
	private String shortFileSize;
	private boolean supported;
	private boolean starmark;
	private String pdfstatus;
	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}
	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	/**
	 * @return the fileExtend
	 */
	public String getFileExtend() {
		return fileExtend;
	}
	/**
	 * @param fileExtend the fileExtend to set
	 */
	public void setFileExtend(String fileExtend) {
		this.fileExtend = fileExtend;
	}
	/**
	 * @return the pdfViewUrl
	 */
	public boolean getTagExist() {
		return tagExist;
	}
	/**
	 * @param pdfViewUrl the pdfViewUrl to set
	 */
	public void setTagExist(boolean tagExist) {
		this.tagExist = tagExist;
	}
	/**
	 * @return the downloadUrl
	 */
	public String getDownloadUrl() {
		return downloadUrl;
	}
	/**
	 * @param downloadUrl the downloadUrl to set
	 */
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	/**
	 * @return the shortFileSize
	 */
	public String getShortFileSize() {
		return shortFileSize;
	}
	/**
	 * @param shortFileSize the shortFileSize to set
	 */
	public void setShortFileSize(String shortFileSize) {
		this.shortFileSize = shortFileSize;
	}
	/**
	 * @return the supported
	 */
	public boolean getSupported() {
		return supported;
	}
	/**
	 * @param supported the supported to set
	 */
	public void setSupported(boolean supported) {
		this.supported = supported;
	}
	/**
	 * @return the pdfstatus
	 */
	public String getPdfstatus() {
		return pdfstatus;
	}
	/**
	 * @param pdfstatus the pdfstatus to set
	 */
	public void setPdfstatus(String pdfstatus) {
		this.pdfstatus = pdfstatus;
	}
	public boolean getStarmark() {
		return starmark;
	}
	public void setStarmark(boolean starmark) {
		this.starmark = starmark;
	}	
}
