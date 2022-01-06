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

package net.duckling.ddl.service.authenticate.impl;

import java.security.Principal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


import cn.vlabs.commons.principal.UserPrincipal;

/**
 * @date Jul 14, 2011
 * @author xiejj@cnic.cn
 */
public interface LoginProvider {
    /**
     * 显示登录界面
     * @param request   Http请求对象
     * @param response  Http响应对象
     */
    PageView login(HttpServletRequest request, String acceptUrl);
    /**
     * 登录
     * @param userName
     * @param password
     * @return
     */
    UserPrincipal login(String userName, String password);
    /**
     * 本地登录
     * @param request
     * @param acceptUrl
     * @param userName
     * @param password
     * @return
     */
    PageView localLogin(HttpServletRequest request, String acceptUrl,
                        String userName, String password);
    /**
     * 分析用户输入
     * @param request
     * @return
     */
    Collection<Principal> commit(HttpServletRequest request);
    /**
     * 执行登出操作
     * @param request  Http请求对象
     * @param response Http响应对象
     */
    PageView logout(HttpServletRequest request, String acceptUrl);

    /**
     * 构建umt注册并返回url
     * @param request
     * @return
     */
    String makeUmtRegistUrl(HttpServletRequest request,String acceptUrl);
}
