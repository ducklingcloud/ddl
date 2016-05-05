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
/**
 * 
 */
package net.duckling.ddl.service.copy;

/**
 * 用来显示页面用
 * @author lvly
 * @since 2012-11-16
 */
public class CopyLogDisplay {
	private String fromTeamName;
	private String copyDate;
	private String fromVersion;
	private String userName;
	private String rTitle;
	public String getrTitle() {
		return rTitle;
	}
	public void setrTitle(String rTitle) {
		this.rTitle = rTitle;
	}
	public String getFromTeamName() {
		return fromTeamName;
	}
	public void setFromTeamName(String fromTeamName) {
		this.fromTeamName = fromTeamName;
	}
	public String getCopyDate() {
		return copyDate;
	}
	public void setCopyDate(String copyDate) {
		this.copyDate = copyDate;
	}
	public String getFromVersion() {
		return fromVersion;
	}
	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	


}
