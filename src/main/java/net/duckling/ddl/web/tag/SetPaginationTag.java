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

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.util.TextUtil;

/**
 * Calculate pagination string. Used for page-info and search results
 *
 * <P>
 * <B>Attributes</B>
 * </P>
 * <UL>
 * <LI>start - start item of the page to be highlighted
 * <LI>total - total number of items
 * <LI>pagesize - total number of items per page
 * <LI>maxlinks - number of page links to be generated
 * <LI>fmtkey - pagination prefix of the i18n resource key
 * </UL>
 * <P>
 * Following optional attributes can be parameterised with '%s' (item count)
 * </P>
 * <UL>
 * <LI>href - href of each page link. (optional)
 * <LI>onclick - onclick of each page link. (optional)
 * </UL>
 *
 * @author Yong Ke
 */
public class SetPaginationTag extends VWBBaseTag {
    private static final long serialVersionUID = 0L;
    private static final int ALLITEMS = -1;

    private int start;
    private int total;
    private int pagesize;
    private int maxlinks;
    private String fmtkey;
    private String href;
    private String mOnclick;
    private int rid;
    private String urlPatterns;


    public void initTag() {
        super.initTag();
        start = 1;
        total = 0;
        pagesize = 20;
        maxlinks = 9;
        fmtkey = null;
        href = null;
        mOnclick = null;
    }

    public void setStart(int arg) {
        start = arg;
    }

    public void setTotal(int arg) {
        total = arg;
    }

    public void setPagesize(int arg) {
        pagesize = arg;
    }

    public void setMaxlinks(int arg) {
        maxlinks = arg;
        if (maxlinks % 2 == 0) {
            maxlinks--; /* must be odd */
        }
    }

    public void setFmtkey(String arg) {
        fmtkey = arg;
    }

    public void setHref(String arg) {
        href = arg;
    }

    public void setOnclick(String arg) {
        mOnclick = arg;
    }



    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    // 1 21 41 61
    // 1 21 41 61 81 next last
    // first previous 20 40 *60* 80 100 next last
    // fist previous 40 60 80 100 120
    public int doVWBStart() throws IOException {
        if (total < pagesize) {
            return SKIP_BODY;
        }

        StringBuffer pagination = new StringBuffer();

        if (start > total) {
            start = total;
        }
        if (start < ALLITEMS) {
            start = 1;
        }

        int maxs = pagesize * maxlinks;
        int mids = pagesize * (maxlinks / 2);

        pagination.append("<div class='pagination'>");

        pagination.append(LocaleSupport.getLocalizedMessage(pageContext, fmtkey)).append(" ");

        int cursor = 1;
        int cursormax = total;
        int tid = VWBContext.getCurrentTid();
        if (total > maxs) {// need to calculate real window ends
            if (start > mids) {
                cursor = start - mids;
            }
            if ((cursor + maxs) > total) {
                cursor = ((1 + total / pagesize) * pagesize) - maxs;
            }
            cursormax = cursor + maxs;
        }

        if ((start == ALLITEMS) || (cursor > 0)) {
            appendLink(pagination, 1, fmtkey + ".first",tid);
        }

        if ((start != ALLITEMS) && (start - pagesize >= 0)) {
            appendLink(pagination, start - pagesize, fmtkey + ".previous",tid);
        }

        if (start != ALLITEMS) {
            while (cursor <= cursormax) {
                if (cursor == start) {
                    pagination.append("<span class='cursor'>");
                    pagination.append(1 + cursor / pagesize);
                    pagination.append("</span> ");
                } else {
                    appendLink(pagination, cursor, 1 + cursor / pagesize,tid);
                }
                cursor += pagesize;
            }
        }

        if ((start != ALLITEMS) && (start + pagesize < total)) {
            appendLink(pagination, start + pagesize, fmtkey + ".next",tid);

            int lastStart = total - total % pagesize + 1;
            if ((start == ALLITEMS) || (cursormax <= total)) {
                appendLink(pagination, (lastStart >total ? lastStart - pagesize : lastStart), fmtkey + ".last",tid);
            }
        }

        if (start == ALLITEMS) {
            pagination.append("<span class='cursor'>");
            pagination.append(LocaleSupport.getLocalizedMessage(pageContext, fmtkey + ".all"));
            pagination.append("</span>&nbsp;&nbsp;");
        } else {
            appendLink(pagination, ALLITEMS, fmtkey + ".all",tid);
        }

        // (Total items: m_total )
        pagination.append(LocaleSupport.getLocalizedMessage(pageContext, fmtkey + ".total", new Object[] { total }));

        pagination.append("</div>");

        /* ****** processing done ****** */

        String p = pagination.toString();

        pageContext.getOut().println(p);

        pageContext.setAttribute("pagination", p); /*
                                                    * and cache for later use in
                                                    * page context
                                                    */

        return SKIP_BODY;
    }

    /**
     * Generate pagination links <a href='' title='' onclick=''>text</a> for
     * pagination blocks starting a page. Uses m_href and m_onclick as attribute
     * patterns '%s' in the patterns are replaced with page offset
     *
     * @param sb
     *            : stringbuffer to write output to
     * @param page
     *            : start of page block
     * @param onclick
     *            : link text
     *
     **/
    private void appendLink(StringBuffer sb, int page, String fmttextkey,int tid) {
        appendLink2(sb, page, LocaleSupport.getLocalizedMessage(pageContext, fmttextkey),tid);
    }

    private void appendLink(StringBuffer sb, int page, int paginationblock,int tid) {
        appendLink2(sb, page, Integer.toString(paginationblock),tid);
    }

    private void appendLink2(StringBuffer sb, int page, String text, int tid) {
        URLGenerator urlGenerator = DDLFacade.getBean(URLGenerator.class);
        sb.append("<a title=\"");
        if (page == ALLITEMS) {
            sb.append(LocaleSupport.getLocalizedMessage(pageContext, fmtkey + ".showall.title"));
        } else {
            sb.append(LocaleSupport.getLocalizedMessage(pageContext, fmtkey + ".show.title", new Object[] { (page),
                        (page + pagesize - 1) }));
        }
        sb.append("\" ");

        if (href != null) {
            sb.append("href=\"")
                    .append(urlGenerator.getURL(tid,urlPatterns,Integer.toString(rid),
                                                "start=" + page + "&query=" + pageContext.getAttribute("query", PageContext.REQUEST_SCOPE))).append("\" ");
        }

        if (mOnclick != null) {
            sb.append("onclick=\"");
            sb.append(TextUtil.replaceString(mOnclick, "%s", Integer.toString(page)));
            sb.append("\" ");
        }

        sb.append(">");
        sb.append(text);
        sb.append("</a> ");
    }

}
