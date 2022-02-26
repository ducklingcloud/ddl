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

import java.net.MalformedURLException;
import java.net.URL;
import net.duckling.common.DucklingProperties;

import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.ContainerURLConstructor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

/**
 * VWB容器实现类
 *
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 * @revised by clive lee
 * @content 版本10040 更改site的初始化方式，将所有site的初始化过程推迟到使用前 (clive lee)
 */
public class VWBContainerImpl implements VWBContainer {
    private static BeanFactory beanfactory;
    private static final Logger LOG = Logger.getLogger(VWBContainerImpl.class);

    public static VWBContainer findContainer() {
        return (VWBContainer) beanfactory.getBean(Attributes.CONTAINER_KEY);
    }

    public static void setBeanFactory(BeanFactory factory) {
        beanfactory = factory;
    }

    private String baseURL;
    private DucklingProperties systemProperty;

    private TeamService teamService;

    private ContainerURLConstructor urlConstructor;

    private Site newSite(int teamId) {
        Team team = teamService.getTeamByID(teamId);
        if (team != null && team.isWorking()) {
            Site site = new VWBSite();
            site.setSiteInfo(team);
            site.setBaseUrl(baseURL);
            return site;
        } else {
            return null;
        }
    }

    public String getBasePath() {
        String basePath;
        String baseURL = this.getBaseURL();
        try {
            URL url = new URL(baseURL);
            basePath = url.getPath();
        } catch (MalformedURLException e) {
            basePath = "/ddl";
        }
        return basePath;
    }

    public String getBaseURL() {
        return this.baseURL;
    }

    public String getContentEncoding() {
        return systemProperty.getProperty(KeyConstants.ENCODING);
    }

    public String getDefaultDomain() {
        return systemProperty.getProperty("duckling.baseURL");
    }

    //  public String getFrontPage() {
    //      return this.getURL("switchTeam", "", "", true);
    //  }

    public String getProperty(String propertyName) {
        return systemProperty.getProperty(propertyName);
    }

    public Site getSite(int id) {
        return newSite(id);
    }

    public Site getSiteByName(String name) {
        Team team = teamService.getTeamByName(name);
        if (team == null) {
            if("pan".equals(name)){
                return null;
            }
            LOG.error("Not found team : [" + name + "] when getSiteByName");
            return null;
        } else {
            Site site = new VWBSite();
            site.setSiteInfo(team);
            site.setBaseUrl(baseURL);
            return site;
        }
    }

    public String getURL(String context, String pageName, String params,
                         boolean absolute) {
        return urlConstructor.makeURL(context, pageName, params, absolute);
    }

    public Site loadSite(int id) {
        Team siteMeta = teamService.getTeamByID(id);
        Site site = (Site) beanfactory.getBean(Attributes.SITE_KEY);
        site.setSiteInfo(siteMeta);
        return site;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public void setSystemProperty(DucklingProperties systemProperty) {
        this.systemProperty = systemProperty;
    }

    public void setTeamService(TeamService ts) {
        this.teamService = ts;
    }

    public void setUrlConstructor(ContainerURLConstructor urlConstructor) {
        this.urlConstructor = urlConstructor;
    }
}
