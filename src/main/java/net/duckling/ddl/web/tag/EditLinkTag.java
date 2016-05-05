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
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class EditLinkTag extends VWBLinkTag {
    private static final long serialVersionUID = 0L;

    public String m_version = null;
    public String m_title = "";
    public String m_accesskey = "";

    public void initTag() {
        super.initTag();
        m_version = null;
    }

    public void setVersion(String vers) {
        m_version = vers;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public void setAccesskey(String access) {
        m_accesskey = access;
    }

    public final int doVWBStart() throws IOException {
        PageRender page = null;
        String versionString = "";
        int rid = -1;
        int tid = VWBContext.getCurrentTid();
        if (m_pageName == null) {
            page = DDLFacade.getBean(ResourceOperateService.class).getPageRender(tid, vwbcontext.getRid());
            if (page == null) {
                // You can't call this on the page itself anyways.
                return SKIP_BODY;
            }
            rid = page.getMeta().getRid();
        } else {
            rid = m_pageId;
        }

        if (m_version != null) {
            if ("this".equalsIgnoreCase(m_version)) {
                if (page == null) {
                    // No page, so go fetch according to page name.
                    page = DDLFacade.getBean(ResourceOperateService.class).getPageRender(tid, Integer.parseInt(id));
                }
                if (page != null) {
                    versionString = "version=" + page.getMeta().getLastVersion();
                }
            } else {
                versionString = "version=" + m_version;
            }
        }
        URLGenerator urlGenerator = DDLFacade.getBean(URLGenerator.class);
        JspWriter out = pageContext.getOut();
        switch (m_format) {
        case ANCHOR:
            out.print("<a href=\"" + urlGenerator.getURL(tid, UrlPatterns.T_EDIT_PAGE, Integer.toString(rid), versionString)
                    + "\" accesskey=\"" + m_accesskey + "\" title=\"" + m_title + "\">");
            break;

        case URL:
            out.print(urlGenerator.getURL(tid, UrlPatterns.T_EDIT_PAGE, Integer.toString(rid), versionString));
            break;
        default:
            break;
        }

        return EVAL_BODY_INCLUDE;
    }
}
