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
package net.duckling.ddl.service.user;

import java.io.Serializable;
import java.util.Date;

public class UserConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3459631199040312299L;
	private int id;
	private String uid;
	private int maxCreateTeam;
	private String configUid;
	private Date configDate;
	private String description;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getMaxCreateTeam() {
		return maxCreateTeam;
	}
	public void setMaxCreateTeam(int maxCreateTeam) {
		this.maxCreateTeam = maxCreateTeam;
	}
	public String getConfigUid() {
		return configUid;
	}
	public void setConfigUid(String configUid) {
		this.configUid = configUid;
	}
	public Date getConfigDate() {
		return configDate;
	}
	public void setConfigDate(Date configDate) {
		this.configDate = configDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
