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

package net.duckling.ddl.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;


/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class DiffLinkTag extends VWBLinkTag {
	private static final long serialVersionUID = 0L;

	public static final String VER_LATEST = "latest";

	public static final String VER_PREVIOUS = "previous";

	public static final String VER_CURRENT = "current";

	private String m_version = VER_LATEST;

	private String m_newVersion = VER_LATEST;

	public void initTag() {
		super.initTag();
		m_version = m_newVersion = VER_LATEST;
	}

	public final String getVersion() {
		return m_version;
	}

	public void setVersion(String arg) {
		m_version = arg;
	}

	public final String getNewVersion() {
		return m_newVersion;
	}

	public void setNewVersion(String arg) {
		m_newVersion = arg;
	}
	private IResourceService getResourceService(){
		return DDLFacade.getBean(IResourceService.class);
	}
	public final int doVWBStart() throws IOException {
		int pageName = m_pageId;

		if (pageName == -1) {
			if (vwbcontext.getResource() != null) {
				pageName =vwbcontext.getResource().getRid();
			} else {
				return SKIP_BODY;
			}
		}

		JspWriter out = pageContext.getOut();

		int r1 = 0;
		int r2 = 0;

		if (getResourceService().getResource(pageName)==null) {
			return SKIP_BODY;
		}

		if (VER_LATEST.equals(getVersion())) {
			Resource meta = getResource(pageName);
			if (meta == null) {
				return SKIP_BODY;
			}
			r1 = meta.getLastVersion();
		} else if (VER_PREVIOUS.equals(getVersion())) {
			r1 = vwbcontext.getResource().getLastVersion() - 1;
			r1 = (r1 < 1) ? 1 : r1;
		} else if (VER_CURRENT.equals(getVersion())) {
			r1 = vwbcontext.getResource().getLastVersion();
		} else {
			r1 = Integer.parseInt(getVersion());
		}

		if (VER_LATEST.equals(getNewVersion())) {
			Resource meta = getResource(pageName);
			r2 = meta.getLastVersion();
		} else if (VER_PREVIOUS.equals(getNewVersion())) {
			r2 = vwbcontext.getResource().getLastVersion() - 1;
			r2 = (r2 < 1) ? 1 : r2;
		} else if (VER_CURRENT.equals(getNewVersion())) {
			r2 = vwbcontext.getResource().getLastVersion();
		} else {
			r2 = Integer.parseInt(getNewVersion());
		}
		int tid = VWBContext.getCurrentTid();
		String url = DDLFacade.getBean(URLGenerator.class).getURL(tid,UrlPatterns.T_DIFF, Integer.toString(pageName), "r1="+ r1 + "&amp;r2=" + r2);
		switch (m_format) {
		case ANCHOR:
			out.print("<a href=\"" + url + "\">");

			break;

		case URL:
			out.print(url);
			break;
		default:break;
		}

		return EVAL_BODY_INCLUDE;
	}
}
