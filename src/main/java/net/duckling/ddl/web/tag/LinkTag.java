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

import java.security.ProviderException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.TextUtil;
import net.duckling.ddl.util.UrlUtil;

import org.apache.commons.lang.StringUtils;


/**
 *  Provides a generic link tag for all kinds of linking
 *  purposes.
 *  <p>
 *  If parameter <i>jsp</i> is defined, constructs a URL pointing
 *  to the specified JSP page, under the baseURL known by the WikiEngine.
 *  Any ParamTag name-value pairs contained in the body are added to this
 *  URL to provide support for arbitrary JSP calls.
 *  <p>
 *  @since 2.3.50
 */
public class LinkTag extends VWBLinkTag implements ParamHandler, BodyTag {
    static final long serialVersionUID = 0L;

    private String version = null;
    private String mClass = null;
    private String mStyle = null;
    private String mTitle = null;
    private String mTarget = null;
    private String mCompareToVersion = null;
    private String mRel = null;
    private String mJsp = null;
    // 页面内的索引
    @SuppressWarnings("unused")
    private String mRef = null;
    private String mContext = null;
    private String mAccesskey = null;
    private boolean mAbsolute = false;
    private boolean mOverrideAbsolute = false;

    private Map<String, String> mContainedParams;

    private BodyContent mBodyContent;

    public void initTag() {
        super.initTag();
        version = mClass = mStyle = mTitle = mTarget = mCompareToVersion = mRel = mJsp = mRef = mAccesskey = null;
        mContainedParams = null;
        mAbsolute = false;
    }

    public void setAccessKey(String key) {
        mAccesskey = key;
    }

    public void setAbsolute(String arg) {
        mOverrideAbsolute = true;
        mAbsolute = TextUtil.isPositive(arg);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String arg) {
        version = arg;
    }

    public void setClass(String arg) {
        mClass = arg;
    }

    public void setStyle(String style) {
        mStyle = style;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setTarget(String target) {
        mTarget = target;
    }

    public void setCompareToVersion(String ver) {
        mCompareToVersion = ver;
    }

    public void setRel(String rel) {
        mRel = rel;
    }

    public void setRef(String ref) {
        mRef = ref;
    }

    public void setJsp(String jsp) {
        mJsp = jsp;
        if (mContext == null) {
            mContext = UrlPatterns.PLAIN;
        }

    }

    public void setContext(String context) {
        mContext = context;
    }

    /**
     * Support for ParamTag supplied parameters in body.
     */
    public void setContainedParameter(String name, String value) {
        if (name != null) {
            if (mContainedParams == null) {
                mContainedParams = new HashMap<String, String>();
            }
            mContainedParams.put(name, value);
        }
    }


    /**
     *  This method figures out what kind of an URL should be output.  It mirrors heavily
     *  on JSPWikiMarkupParser.handleHyperlinks();
     *
     * @return
     * @throws ProviderException
     */
    private String figureOutURL() throws ProviderException {
        String url = null;
        int tid = VWBContext.getCurrentTid();
        Site site = vwbcontext.getSite();
        if (StringUtils.isEmpty(m_pageName)) {
            Resource resource = vwbcontext.getResource();
            if (resource != null) {
                m_pageId = resource.getRid();
            }
        }

        if (mJsp != null) {
            String params = addParamsForRecipient(null, mContainedParams);
            if (mContext != null) {
                url = makeBasicURL(tid,mContext,mJsp,params,mAbsolute);
            } else {
                url = makeBasicURL(tid,UrlPatterns.PLAIN,0+"",params,mAbsolute);
            }
        } else if (!StringUtils.isEmpty(m_pageName)) {
            if (StringUtils.isEmpty(mContext)) {
                mContext = UrlPatterns.T_PAGE;
            }
            if (UrlPatterns.T_PAGE.equals(mContext) && m_pageId != -1) {
                Resource p = getResource(m_pageId);
                if (p != null) {

                    String params = (version != null) ? "version=" + getVersion() : null;

                    params = addParamsForRecipient(params, mContainedParams);
                    url = makeBasicURL(tid,mContext, m_pageId+"", params, mAbsolute);
                    return url;
                }
            }
            String params = addParamsForRecipient(null, mContainedParams);
            if(m_pageId==-1){
                url = this.makeBasicURL(tid, mContext, m_pageName, params, mAbsolute);
            }else{
                url = this.makeBasicURL(tid, mContext, m_pageId+"", params, mAbsolute);
            }
        } else {
            if (site != null && tid != -1) {
                Resource p = DDLFacade.getBean(IResourceService.class).getResource(1);
                if(p!=null){
                    int page = p.getRid();
                    url = makeBasicURL(tid, mContext, page+"", null, mAbsolute);
                }else{
                    url = makeBasicURL(tid, mContext, null, null, mAbsolute);
                }
            } else {
                url = makeBasicURL(tid, mContext, null, null, mAbsolute);
            }
        }

        return url;
    }

    private String addParamsForRecipient(String addTo, Map params) {
        if (params == null || params.size() == 0) {
            return addTo;
        }
        StringBuffer buf = new StringBuffer();
        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            String n = (String) e.getKey();
            String v = (String) e.getValue();
            buf.append(n);
            buf.append("=");
            buf.append(v);
            if (it.hasNext()) {
                buf.append("&");
            }
        }
        if (addTo == null) {
            return buf.toString();
        }
        if (!addTo.endsWith("&")) {
            return addTo + "&" + buf.toString();
        }
        return addTo + buf.toString();
    }

    private String makeBasicURL(int tid,String context, String page, String parms, boolean absolute) {
        String url;

        if (context.equals(UrlPatterns.T_DIFF)) {
            int r1 = 0;
            int r2 = 0;

            if (DiffLinkTag.VER_LATEST.equals(getVersion())) {
                Resource res = VWBContext.getResource(Integer.parseInt(page), LynxConstants.TYPE_PAGE);
                r1 = res.getLastVersion();
            } else if (DiffLinkTag.VER_PREVIOUS.equals(getVersion())) {
                r1 = vwbcontext.getResource().getLastVersion() - 1;
                r1 = (r1 < 1) ? 1 : r1;
            } else if (DiffLinkTag.VER_CURRENT.equals(getVersion())) {
                r1 = vwbcontext.getResource().getLastVersion();
            } else {
                r1 = Integer.parseInt(getVersion());
            }

            if (DiffLinkTag.VER_LATEST.equals(mCompareToVersion)) {
                Resource res = VWBContext.getResource(Integer.parseInt(page), LynxConstants.TYPE_PAGE);
                r2 = res.getLastVersion();
            } else if (DiffLinkTag.VER_PREVIOUS.equals(mCompareToVersion)) {
                r2 = vwbcontext.getResource().getLastVersion() - 1;
                r2 = (r2 < 1) ? 1 : r2;
            } else if (DiffLinkTag.VER_CURRENT.equals(mCompareToVersion)) {
                r2 = vwbcontext.getResource().getLastVersion();
            } else {
                r2 = Integer.parseInt(mCompareToVersion);
            }

            parms = "r1=" + r1 + "&amp;r2=" + r2;
        }

        URLGenerator urlGenerator = DDLFacade.getBean(URLGenerator.class);

        if((UrlPatterns.PLAIN.equals(context)||UrlPatterns.CONFIG_TEAM.equals(context)) && StringUtils.isNotEmpty(page)){
            if(absolute){
                url = urlGenerator.getAbsoluteURL(tid, mContext, page, parms);
            }else{
                url = urlGenerator.getURL(mContext, page, parms);
            }
        }else{
            if(absolute){
                if (tid > 0) {
                    url = urlGenerator.getAbsoluteURL(tid, mContext, m_pageName, parms);
                } else {
                    url = urlGenerator.getAbsoluteURL(mContext, m_pageName, parms);
                }
            }else{
                if (tid>0){
                    url = urlGenerator.getURL(tid,mContext, m_pageName, parms);
                }else{
                    url = urlGenerator.getURL(mContext, m_pageName, parms);
                }
            }
        }


        return url;
    }

    public int doVWBStart() throws Exception {
        return EVAL_BODY_BUFFERED;
    }

    public int doEndTag() {
        try {
            if (!mOverrideAbsolute) {
                VWBContainer container = vwbcontext.getContainer();
                mAbsolute = "absolute".equals(container.getProperty(KeyConstants.PREF_REFER_STYLE));
            }

            JspWriter out = pageContext.getOut();
            String url = figureOutURL();
            url = chooseURLScheme(url);
            StringBuffer sb = new StringBuffer(20);

            sb.append((mClass != null) ? "class=\"" + mClass + "\" " : "");
            sb.append((mStyle != null) ? "style=\"" + mStyle + "\" " : "");
            sb.append((mTarget != null) ? "target=\"" + mTarget + "\" " : "");
            sb.append((mTitle != null) ? "title=\"" + mTitle + "\" " : "");
            sb.append((mRel != null) ? "rel=\"" + mRel + "\" " : "");
            sb.append((mAccesskey != null) ? "accesskey=\"" + mAccesskey + "\" " : "");

            switch (m_format) {
                case URL:
                    out.print(url);
                    break;
                default:
                case ANCHOR:
                    out.print("<a " + sb.toString() + " href=\"" + url + "\">");
                    break;
            }

            // Add any explicit body content. This is not the intended use
            // of LinkTag, but happens to be the way it has worked previously.
            if (mBodyContent != null) {
                String linktext = mBodyContent.getString().trim();
                out.write(linktext);
            }

            // Finish off by closing opened anchor
            if (m_format == ANCHOR) {
                out.print("</a>");
            }
        } catch (Exception e) {
            // Yes, we want to catch all exceptions here, including
            // RuntimeExceptions
            LOG.error("Tag failed", e);
        }

        return EVAL_PAGE;
    }

    private String chooseURLScheme(String url) {
        if (url == null || url.length() == 0) {
            return url;
        }
        String scheme = pageContext.getRequest().getScheme();
        if ("https".equals(scheme)) {
            int port = pageContext.getRequest().getLocalPort();
            return UrlUtil.changeSchemeToHttps(url, port);
        }
        return url;
    }

    public void setBodyContent(BodyContent bc) {
        mBodyContent = bc;
    }

    public void doInitBody() throws JspException {
    }
}
