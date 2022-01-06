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

import net.duckling.ddl.service.team.Team;


/**
 * 站点对象接口
 *
 * @date May 5, 2010
 * @author xiejj@cnic.cn
 */
public interface Site  {


    /**
     * 查询站点的BaseURL
     *
     * @return baseurl
     */
    String getBaseURL();
    /**
     * 查询团队的绝对URL
     * @return
     */
    String getAbsoluteTeamBase();
    /**
     * 查询团队的相对URL(不包含域名部分)
     * @return
     */
    String getRelativeTeamBase();


    /**
     * 查询站点的BasePath
     *
     * @return
     */
    String getBasePath();

    /**
     * 构造编辑链接
     *
     * @param resourceid
     * @return
     */
    String getEditURL(int resourceid);

    /**
     * 查询首页链接
     *
     * @return 首页浏览链接URL
     */
    String getFrontPage();


    /**
     * 查询站点的名称
     *
     * @return 站点的名称
     */
    String getSiteName();

    /**
     * 获得团队的contextPath
     *
     * @return
     */
    String getTeamContext();

    void setBaseUrl(String baseUrl);
    /**
     * 构造URL
     *
     * @param action
     *            URL对应的操作
     * @param pagename
     *            页面名称
     * @param params
     *            参数
     * @return 该页面对应的URL(是否使用绝对路径，由配置项决定)
     */
    String getURL(String action, String pagename, String params);

    /**
     * 构造页面访问URL
     *
     * @param action
     *            对页面的操作
     * @param pagename
     *            页面名称
     * @param params
     *            参数
     * @param absolute
     *            是否使用绝对路径
     * @param layout
     *            使用的布局 ["page", "collection"]
     * @return 该页面对应的URL，使用绝对路径时包含BaseURL部分
     */
    String getURL(String action, String pagename, String params, boolean absolute);

    /**
     * 构造浏览URL
     *
     * @param resourceid
     *            资源ID
     * @return 返回一个构造好的浏览URL
     */
    String getViewURL(int resourceid);

    int getId();

    /**
     * 设置站点源信息
     *
     * @param site
     */
    void setSiteInfo(Team site);

    void changeTitle(String title);

}
