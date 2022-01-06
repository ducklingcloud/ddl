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
package net.duckling.ddl.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSite;
import net.duckling.ddl.util.SiteUtil;

import org.apache.commons.lang.StringUtils;


/**
 * @title: VWBFilter.java
 * @package cn.vlabs.duckling.vwb.ui
 * @description: 过滤器
 * @author xiejj clive
 * @last_edit_time 2012-11-7 下午3:24:02
 * @revised 新增URL黑名单过滤功能 BLACK_LIST (clive)
 */
public class VWBFilter implements Filter {
    private String encoding;
    public void destroy() {

    }

    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("charset");
        if (encoding == null) {
            encoding = "UTF-8";
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        if (request instanceof HttpServletRequest) {
            findTeam((HttpServletRequest) request);
            filterChain.doFilter(request, response);
            VWBContext.setCurrentTid(-1);
        }
    }

    private void findTeam(HttpServletRequest request) {
        String teamCode = null;
        String teamCodeParam = request.getParameter("teamCode");
        if (!StringUtils.isEmpty(teamCodeParam)) {
            teamCode = teamCodeParam;
        } else {
            teamCode = SiteUtil.parseTeamCode(request);
        }

        if (teamCode != null) {
            Site site = VWBContainerImpl.findContainer().getSiteByName(teamCode);
            if (site != null) {
                request.setAttribute("teamFounded", Boolean.TRUE);
                VWBContext.saveSite(request, site);
                VWBContext.setCurrentTid(site.getId());
            }
        }
    }
}
