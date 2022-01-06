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

import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.service.authenticate.UserPrincipal;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.I18nUtil;
import net.duckling.ddl.util.SiteUtil;

/**
 * 代表当前请求的类
 *
 * @author xiejj@cnic.cn
 *
 * @creation Jan 28, 2010 9:14:35 AM
 */
public class VWBContext {
    private static ThreadLocal<Integer> tidThreadLocal = new ThreadLocal<Integer>();

    public static final String CONTEXT_KEY = "vwb.context";

    public static final String EDITOR_MODE = "D.E.";

    public static final int LATEST_VERSION = -1;

    private static Resource getResource(int rid){
        return DDLFacade.getBean(IResourceService.class).getResource(rid);
    }

    public static VWBContext createContext(HttpServletRequest request, String requestContext) {
        Site site = findSite(request);
        if (site != null) {
            VWBContext.setCurrentTid(site.getId());
        }
        Resource resource = null;
        VWBContext context = new VWBContext(requestContext, request, site, resource);
        request.setAttribute(CONTEXT_KEY, context);
        return context;
    }

    public static VWBContext createContext(HttpServletRequest request, String urlPattern, int rid) {
        Site site = findSite(request);
        Resource res = getResource(rid);
        VWBContext context = new VWBContext(urlPattern, request, site, res);
        request.setAttribute(CONTEXT_KEY, context);
        return context;
    }

    public static VWBContext createContext(HttpServletRequest request, String urlPattern, int rid, String itemType) {
        Site site = findSite(request);
        Resource res = getResource(rid, itemType);
        VWBContext context = new VWBContext(urlPattern, request, site, res);
        request.setAttribute(CONTEXT_KEY, context);
        return context;
    }

    public static VWBContext createContext(HttpServletRequest request, String requestContext, Resource vp) {
        Site site = findSite(request);
        VWBContext context = new VWBContext(requestContext, request, site, vp);
        request.setAttribute(CONTEXT_KEY, context);
        return context;
    }

    public static VWBContext createContext(int siteId, HttpServletRequest request, String requestContext, Resource vp) {
        VWBContainer container = VWBContainerImpl.findContainer();
        Site site = container.getSite(siteId);
        VWBContext context = new VWBContext(requestContext, request, site, vp);
        request.setAttribute(CONTEXT_KEY, context);
        return context;
    }

    public static Site findSite(HttpServletRequest request) {
        Site site = (Site) request.getAttribute(Attributes.REQUEST_SITE_KEY);
        if (site == null) {
            String teamCode = SiteUtil.parseTeamCode(request);
            if (teamCode != null) {
                VWBContainer container = VWBContainerImpl.findContainer();
                site = container.getSiteByName(teamCode);
                request.setAttribute(Attributes.REQUEST_SITE_KEY, site);
            }
        }
        return site;
    }

    public static VWBContext getContext(HttpServletRequest request) {
        return (VWBContext) request.getAttribute(CONTEXT_KEY);
    }

    public static int getCurrentTid() {
        if (tidThreadLocal.get() != null) {
            return tidThreadLocal.get();
        } else {
            return -1;
        }
    }
    public static String getCurrentTeamCode(){
        int tid = getCurrentTid();
        Team team =DDLFacade.getBean(TeamService.class).getTeamByID(tid);
        if(team!=null){
            return team.getName();
        }
        return null;
    }

    public static Resource getResource(int rid, String itemType){
        return DDLFacade.getBean(IResourceService.class).getResource(rid, getCurrentTid());
    }

    public static void saveSite(HttpServletRequest request, Site site) {
        if (site != null) {
            request.setAttribute(Attributes.REQUEST_SITE_KEY, site);
        }
    }

    public static void setCurrentTid(int tid) {
        if (tid != -1) {
            tidThreadLocal.set(tid);
        } else {
            tidThreadLocal.remove();
        }
    }

    private String itemType;

    private Map<String, Object> m_variables = new Hashtable<String, Object>();

    private HttpServletRequest request;

    private Resource resource;

    private int rid;

    private VWBSession session;

    private Site site;

    @SuppressWarnings("unused")
    private int tid;

    private String urlPattern;

    private boolean useDData = false;

    private String wysiwygEditorMode;

    public VWBContext(String urlPattern, HttpServletRequest request, Site site) {
        this.request = request;
        this.site = site;
        this.urlPattern = urlPattern;
        this.session = VWBSession.findSession(request);
        parseInputMode(request);
    }

    public VWBContext(String urlPattern, HttpServletRequest request, Site site, int rid) {
        this.request = request;
        this.site = site;
        this.urlPattern = urlPattern;
        this.rid = rid;
        this.session = VWBSession.findSession(request);
        parseInputMode(request);
    }

    public VWBContext(String urlPattern, HttpServletRequest request, Site site, int rid, String itemType) {
        this.request = request;
        this.site = site;
        this.urlPattern = urlPattern;
        this.rid = rid;
        this.itemType = itemType;
        this.session = VWBSession.findSession(request);
        parseInputMode(request);
    }

    public VWBContext(String requestContext, HttpServletRequest request, Site site, Resource vp) {
        this.request = request;
        this.site = site;
        this.urlPattern = requestContext;
        this.resource = vp;
        if (vp != null) {
            this.rid = vp.getRid();
            this.itemType = vp.getItemType();
        }
        this.session = VWBSession.findSession(request);
        parseInputMode(request);
    }

    public VWBContext(String urlPattern, int tid, Resource res) {
        this.tid = tid;
        this.resource = res;
        this.urlPattern = urlPattern;
        this.session = VWBSession.findSession(request);
        parseInputMode(request);
    }

    public VWBContext(String requestContext, Site site, VWBSession vwbsession, Resource resource, String displayMode) {
        this.site = site;
        this.session = vwbsession;
        this.resource = resource;
        this.urlPattern = requestContext;
        parseDisplayMode(displayMode);
    }

    private void parseDisplayMode(String mode) {
        if (mode != null) {
            if ("1".equals(mode)) {
                session.setFullMode(true);
            } else {
                session.setFullMode(false);
            }
        }
    }

    private void parseInputMode(HttpServletRequest request) {
        String mode = null;
        if (request != null) {
            mode = request.getParameter("m");
        }
        parseDisplayMode(mode);
    }

    public  String getBaseURL() {
        return VWBContainerImpl.findContainer().getBaseURL();
    }

    public ResourceBundle getBundle(String bundle) {
        return I18nUtil.getBundle(bundle, request.getLocale(), request.getLocales());
    }

    public VWBContainer getContainer() {
        return VWBContainerImpl.findContainer();
    }

    public String getCurrentUID() {
        if (session != null)
            return session.getCurrentUser().getName();
        return null;
    }
    public String getCurrentUserName() {
        if (session != null)
            return ((UserPrincipal) session.getCurrentUser()).getFullName();
        return null;
    }
    public String getFrontPage() {
        return site.getFrontPage();
    }
    public HttpServletRequest getHttpRequest() {
        return request;
    }


    public String getItemType() {
        return itemType;
    }

    public Resource getResource() {
        if (resource != null) {
            return resource;
        }
        if (rid != 0) {
            this.resource = getResource(rid);
            return resource;
        }
        return null;
    }

    public int getRid() {
        return rid;
    }

    public Site getSite() {
        return site;
    }

    public int getTid(){
        return VWBContext.getCurrentTid();
    }


    public String getURLPattern() {
        return urlPattern;
    }

    public Object getVariable(String key) {
        return m_variables.get(key);
    }

    public VWBSession getVWBSession() {
        if (session == null) {
            session = VWBSession.findSession(request);
        }
        return session;
    }

    public String getWysiwygEditorMode() {
        return wysiwygEditorMode;
    }

    public boolean isFullMode() {
        if (session != null) {
            return session.isFullMode();
        } else {
            return false;
        }
    }

    public boolean isUseDData() {
        return useDData;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setResource(int rid) {
        this.rid = rid;
    }

    public void setResource(int rid, String itemType) {
        this.rid = rid;
        this.itemType = itemType;
    }
    public void setResource(Resource resource){
        this.resource = resource;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setUseDData(boolean useDData) {
        this.useDData = useDData;
    }
    public void setVariable(String key, Object val) {
        m_variables.put(key, val);
    }

    public void setWysiwygEditorMode(String wysiwygEditorMode) {
        this.wysiwygEditorMode = wysiwygEditorMode;
    }
}
