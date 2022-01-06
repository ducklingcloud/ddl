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
import net.duckling.ddl.service.diff.DifferenceService;


/**
 * Writes difference between two pages using a HTML table. If there is no
 * difference, includes the body.
 *
 * <P>
 * <B>Attributes</B>
 * </P>
 * <UL>
 * <LI>page - Page name to refer to. Default is the current page.
 * </UL>
 *
 * @author Yong Ke
 */
public class InsertDiffTag extends VWBBaseTag {
    private static final long serialVersionUID = 0L;

    /**
     * Attribute which is used to store the old page content to the Page Context
     */
    public static final String ATTR_OLDVERSION = "version";

    /**
     * Attribute which is used to store the new page content to the Page Context
     */
    public static final String ATTR_NEWVERSION = "compareTo";

    protected String m_pageName;

    /** {@inheritDoc} */
    public void initTag() {
        super.initTag();
        m_pageName = null;
    }

    /**
     * Sets the page name.
     *
     * @param page
     *            Page to get diff from.
     */
    public void setPage(String page) {
        m_pageName = page;
    }

    /**
     * Gets the page name.
     *
     * @return The page name.
     */
    public String getPage() {
        return m_pageName;
    }

    /** {@inheritDoc} */
    public final int doVWBStart() throws IOException {

        VWBContext ctx;

        if (m_pageName == null) {
            ctx = vwbcontext;
        } else {
            ctx = new VWBContext(vwbcontext.getURLPattern(), vwbcontext.getHttpRequest(), vwbcontext.getSite(),
                                 vwbcontext.getResource());

        }

        Integer vernew = (Integer) pageContext.getRequest().getAttribute(ATTR_NEWVERSION);
        Integer verold = (Integer) pageContext.getRequest().getAttribute(ATTR_OLDVERSION);

        LOG.debug("Request diff between version " + verold + " and " + vernew);

        if (ctx.getResource() != null) {
            JspWriter out = pageContext.getOut();

            String diff = DDLFacade.getBean(DifferenceService.class).makeDiff(ctx, ctx.getResource(),
                                                                              verold.intValue(), vernew.intValue());

            if (diff.length() == 0) {
                return EVAL_BODY_INCLUDE;
            }

            out.write(diff);
        }

        return SKIP_BODY;
    }
}
