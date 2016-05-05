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

package net.duckling.ddl.common;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.url.DefaultURLConstructor;
import net.duckling.ddl.service.url.URLConstructor;
import net.duckling.ddl.service.url.UrlPatterns;

/**
 * 站点对象。
 * 
 * @date Mar 9, 2010
 * @author xiejj@cnic.cn
 */
public class VWBSite implements Site, Serializable {
	private String absoluteTeamURL;

	private String basePath;

	private String baseurl;

	private Team m_teamInfo;

	private String relativeTeamURL;

	private URLConstructor urlConstructor;

	public void changeTitle(String title) {
		m_teamInfo.setDisplayName(title);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Site) {
			Site site = (Site) obj;
			return getId() == site.getId();
		} else {
			return false;
		}
	}

	public String getAbsoluteTeamBase() {
		if (absoluteTeamURL == null) {
			absoluteTeamURL = getBaseURL() + "/" + getTeamContext();
		}
		return absoluteTeamURL;
	}

	public String getBasePath() {
		if (basePath == null) {
			try {
				URL url = new URL(getBaseURL());
				basePath = url.getPath();
			} catch (MalformedURLException e) {
				basePath = "/ddl";
			}
		}
		return basePath;
	}

	public String getBaseURL() {
		return baseurl;
	}

	public String getEditURL(int resourceid) {
		return getURL(UrlPatterns.T_EDIT_PAGE, Integer.toString(resourceid), null);
	}

	public String getFrontPage() {
		return getURL("teamHome", "", "");
	}

	public int getId() {
		return m_teamInfo.getId();
	}

	public String getRelativeTeamBase() {
		if (relativeTeamURL == null) {
			relativeTeamURL = (getBasePath() + "/" + getTeamContext());
		}
		return relativeTeamURL;
	}

	public String getSiteName() {
		return m_teamInfo.getDisplayName();
	}

	public String getSitePrefix() {
		return m_teamInfo.getPrefix();
	}

	public String getTeamContext() {
		return m_teamInfo.getName();
	}

	public String getURL(String action, String pagename, String params) {
		return urlConstructor.makeURL(action, pagename, params);
	}

	public String getURL(String action, String pagename, String params,
			boolean absolute) {
		if (pagename == null) {
			pagename = "";
		}
		return urlConstructor.makeURL(action, pagename, params, absolute);
	}

	public String getViewURL(int resourceid) {
		return getURL(UrlPatterns.T_PAGE, Integer.toString(resourceid), null);
	}

	public int hashCode() {
		return getId();
	}

	public void setBaseUrl(String baseUrl){
		if (baseUrl != null && baseUrl.endsWith("/")) {
			this.baseurl = baseUrl.substring(0, baseUrl.length() - 1);
		}else{
			this.baseurl=baseUrl;
		}
		urlConstructor = new DefaultURLConstructor(getBaseURL(), getTeamContext());
	}

	public void setSiteInfo(Team team) {
		m_teamInfo = team;
	}
}