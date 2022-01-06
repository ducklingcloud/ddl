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

package net.duckling.ddl.service.url;

import java.net.MalformedURLException;
import java.net.URL;

import net.duckling.ddl.util.TextUtil;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @date Mar 2, 2010
 * @author xiejj@cnic.cn
 */
public class ContainerURLConstructor implements URLConstructor {
    private String basePath = "";
    private String baseURL = "";
    private boolean m_useRelative = false;
    public ContainerURLConstructor(String baseUrl, String useRelative){
        this.baseURL = baseUrl;
        this.basePath = getBasePath(baseUrl);
        if ( "relative".equals(useRelative)){
            this.m_useRelative = true;
        }else{
            this.m_useRelative = false;
        }
    }
    private String buildURLWithFullParam(String action, String name, String params, boolean absolute) {
        if (UrlPatterns.PLAIN.equals(action) && !StringUtils.isEmpty(params)) {
            params = (name.indexOf('?') != -1) ? "&amp;" : "?" + params;
        }
        String urlPattern = UrlPatterns.getInstance().findUrlPattern(action);
        if (urlPattern == null) {
            urlPattern = UrlPatterns.getInstance().findUrlPattern(UrlPatterns.T_PAGE);
        }
        String url = doReplace(urlPattern, name, absolute);
        if (!StringUtils.isEmpty(params)) {
            if (url.indexOf('?') != -1) {
                url = url + "&" + params;
            } else {
                url = url + "?" + params;
            }
        }

        return url;
    }
    /**
     * 合成URL URLPattern中包含以下内容时: %u 使用相对地址时使用basepath替换,使用绝对地址时使用baseURL替换 %U
     * 使用绝对地址BaseURL替换 %p 用basePath替换 %n 用page替换 %v 用ViewPort替换(page/) %s 用站点替换
     * %t Team ID
     *
     * @param urlpattern
     *            url的模式
     * @param page
     *            访问的页面
     * @param absolute
     *            是否使用绝对地址
     * @return 合成以后的URL
     */
    private String doReplace(String urlpattern, String page, boolean absolute) {
        String url = urlpattern;
        if (absolute) {
            url = TextUtil.replaceString(url, "%u", baseURL);
        } else {
            url = TextUtil.replaceString(url, "%u", basePath);
        }
        url = TextUtil.replaceString(url, "%U", baseURL);
        url = TextUtil.replaceString(url, "%p", basePath);
        if (page != null) {
            url = TextUtil.replaceString(url, "%n", page);
        }

        return url;
    }

    private String getBasePath(String baseUrl) {
        String basePath;
        try {
            URL url = new URL(baseUrl);
            basePath = url.getPath();
        } catch (MalformedURLException e) {
            basePath = "/ddl";
        }
        return basePath;
    }

    public String makeURL(String action, String resourceId, String params) {
        return buildURLWithFullParam(action, resourceId, params, !m_useRelative);
    }

    public String makeURL(String action, String name, String params, boolean absolute) {
        return buildURLWithFullParam(action, name, params, absolute);
    }
}
