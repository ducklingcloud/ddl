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

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;

import org.apache.commons.lang.StringUtils;

/**
 * Introduction Here.
 * 
 * @date Feb 24, 2010
 * @author xiejj@cnic.cn
 */
public abstract class VWBLinkTag extends VWBBaseTag {
    public static final int ANCHOR = 0;

    public static final int URL = 1;

    protected String m_pageName;
    protected int m_pageId = -1;

    protected int m_format = ANCHOR;
    protected String m_prefix;

    public void initTag() {
        super.initTag();
        m_pageName = null;
        m_format = ANCHOR;
        m_prefix = null;
    }
    protected Resource getResource(int rid){
    	 return DDLFacade.getBean(IResourceService.class).getResource(rid);
    }

    public void setPage(String page) {
        m_pageName = page;
        if (!StringUtils.isEmpty(page)) {
            try {
                m_pageId = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                m_pageId = -1;
            }
        }
    }

    public String getPage() {
        return m_pageName;
    }

    public void setPrefix(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            m_prefix = "c";
        } else {
            m_prefix = prefix;
        }
    }

    public String getPrefix() {
        return m_prefix;
    }

    public void setFormat(String mode) {
        if ("url".equalsIgnoreCase(mode)) {
            m_format = URL;
        } else {
            m_format = ANCHOR;
        }
    }

    public int doEndTag() {
        try {
            if (m_format == ANCHOR) {
                pageContext.getOut().print("</a>");
            }
        } catch (IOException e) {
        }

        return EVAL_PAGE;
    }
}
