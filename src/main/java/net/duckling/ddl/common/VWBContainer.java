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


/**
 * VWB容器对象
 *
 * @date Feb 4, 2010
 * @author xiejj@cnic.cn
 */
public interface VWBContainer {
    static int ADMIN_SITE_ID = 1;

    /**
     * 查询站点ID对应的Site
     *
     * @param id
     * @return
     */
    Site getSite(int id);

    /**
     * 查询站点
     *
     * @param id
     * @return
     */
    Site loadSite(int id);

    /**
     * 获取默认的域名<br>
     * 默认域名作用：当站点没有设置域名的时候，默认使用该默认域名，格式：http://默认域名:端口/site/siteId/<br>
     *
     * @return
     */
    String getDefaultDomain();

    /**
     * 通过站点的Context获取站点
     *
     * @param name
     * @return
     */
    Site getSiteByName(String name);

    /**
     * 构造全容器的URL
     *
     * @param context
     *            URLPattern中的名称
     * @param pageName
     *            在URLPattern中替换%n的内容
     * @param params
     *            参数
     * @param absolute
     *            是否为绝对地址
     * @return 返回构造好的url
     */
    String getURL(String context, String pageName, String params, boolean absolute);

    /**
     * 查询全局配置信息
     *
     * @param propertyName
     * @return
     */
    String getProperty(String propertyName);


    /**
     * 站点的字符编码类型
     *
     * @return 站点字符编码
     */
    String getContentEncoding();

    /**
     * 容器的BaseURL
     *
     * @return
     */
    String getBaseURL();

    /**
     * 容器的BasePath
     *
     * @return
     */
    String getBasePath();
}
