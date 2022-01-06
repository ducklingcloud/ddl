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

package net.duckling.ddl.service.authenticate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.vwb.LoginContext;

/**
 * 认证服务
 *
 * @date May 6, 2010
 * @author xiejj@cnic.cn
 */
public interface AuthenticationService {
    UserPrincipal login(String userName, String password);

    /**
     * 登录
     *
     * @param request
     * @return
     */
    void login(HttpServletRequest request, HttpServletResponse response,
               String succesUrl, String failUrl);

    /**
     * 本地登录
     *
     * @param request
     * @param response
     * @param login
     */
    void login(HttpServletRequest request, HttpServletResponse response,
               LoginContext login);

    void commitLogin(UserPrincipal up, HttpServletRequest request);

    /**
     * 退出
     *
     * @param request
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    void invalidateSession(HttpServletRequest request);

    /**
     * 分析用户输入的信息
     *
     * @param request
     * @param response
     */
    void commit(HttpServletRequest request, HttpServletResponse response);

    /**
     * escience跳转
     *
     * @param request
     * @param response
     */
    void escienceRedirect(HttpServletRequest request,
                          HttpServletResponse response);

    /**
     * 设置登录成功，跳转页面
     *
     * @param request
     * @param url
     */
    void saveSuccessURL(HttpServletRequest request, String url);
}
