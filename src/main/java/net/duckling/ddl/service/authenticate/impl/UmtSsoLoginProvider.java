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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContainerImpl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.api.umt.rmi.user.UserService;
import cn.vlabs.duckling.api.umt.sso.ILoginHandle;
import cn.vlabs.duckling.common.crypto.KeyFile;
import cn.vlabs.duckling.common.crypto.impl.RSAKey;
import cn.vlabs.duckling.common.http.WebSite;
import cn.vlabs.duckling.common.transmission.PublicKeyEnvelope;
import cn.vlabs.duckling.common.transmission.SignedEnvelope;
import cn.vlabs.duckling.common.transmission.UserCredentialEnvelope;
import cn.vlabs.duckling.common.util.Base64Util;

/**
 * UMT 单点登录方案
 * @date Jul 14, 2011
 * @author xiejj@cnic.cn
 */
public class UmtSsoLoginProvider implements LoginProvider {
    private static final Logger LOG = Logger.getLogger(UmtSsoLoginProvider.class);

    private static RSAKey umtKey = null;

    private String keyPath;

    private String publicKeyUrl;

    private String loginUrl;

    private UserService userService;
    public void setServiceUrl(String url){
        userService = new UserService(url);
    }
    public UserPrincipal login(String userName, String password){
        return userService.login(userName, password);
    }
    public Collection<Principal> commit(HttpServletRequest request) {
        String signedCredential = request.getParameter("signedCredential");
        if (umtKey == null) {
            downloadUMTKey(request);
            loadUMTKeyFromLocal(request);
        }
        if (umtKey != null) {
            if (StringUtils.isNotEmpty(signedCredential)) {
                signedCredential = Base64Util.decodeBase64(signedCredential);
                try {
                    SignedEnvelope signedData = SignedEnvelope
                            .valueOf(signedCredential);
                    if (signedData.verify(umtKey)) {
                        UserPrincipal user = UserCredentialEnvelope.valueOf(
                            signedData.getContent()).getUser();
                        LinkedList<Principal> principals = new LinkedList<Principal>();
                        principals.add(user);
                        return principals;
                    } else {
                        LOG.error("UMT credential verify failed.");
                    }
                } catch (Throwable e) {
                    LOG
                            .error("Signed credential is incorrent"
                                   + e.getMessage());
                    LOG.debug(signedCredential);
                    LOG.debug("Detail is :", e);
                }

            } else {
                LOG.error("signedCredential is empty");
            }
        }
        return null;
    }

    private LoginSession getLoginSession(HttpServletRequest request) {
        String sessionid = request.getSession().getId();
        return LoginSession.getLoginSession(sessionid);
    }

    public PageView localLogin(HttpServletRequest request, String acceptUrl,
                               String userName, String password) {
        LoginSession vwbsession = getLoginSession(request);
        vwbsession.setAttribute("username", userName);
        vwbsession.setAttribute("password", password);
        vwbsession.setAttribute("ssourl", makeSsoLoginUrl(request, acceptUrl));
        PageView pv = new PageView(true, getLoginPage());
        return pv;
    }

    private String getLoginPage() {
        return VWBContainerImpl.findContainer().getBaseURL() + "/umtlogin.jsp";
    }

    public PageView login(HttpServletRequest request, String acceptUrl) {
        return new PageView(true, makeSsoLoginUrl(request, acceptUrl));
    }

    private String makeSsoLoginUrl(HttpServletRequest request, String acceptUrl) {
        VWBContainer container = VWBContainerImpl.findContainer();
        String ssourl = loginUrl;
        try {
            String registerURL = container
                    .getProperty("duckling.umt.link.regist");
            ssourl = loginUrl
                    + "?WebServerURL="
                    + URLEncoder.encode(acceptUrl, "UTF-8")
                    + "&appname="
                    + URLEncoder.encode(container
                                        .getProperty("duckling.dct.localName"), "UTF-8")
                    + "&theme="
                    + container.getProperty("duckling.umt.theme")
                    + "&sid="
                    + request.getSession(true).getId()
                    + "&logoutURL="
                    + URLEncoder.encode(container.getURL("plain", "logout",
                                                         "umtSsoLogout=true", true), "UTF-8") + "&"
                    + ILoginHandle.APP_REGISTER_URL_KEY + "="
                    + URLEncoder.encode(registerURL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(),e);
        }
        return ssourl;
    }

    /**
     * 构建umt注册并返回url
     * @param request
     * @return
     */
    public String makeUmtRegistUrl(HttpServletRequest request,String regist){
        VWBContainer container = VWBContainerImpl.findContainer();
        String rootUrl = container.getProperty("duckling.umt.regist");
        String ssourl = rootUrl;
        try {
            String registURL= URLEncoder.encode(regist, "UTF-8");
            String appName = URLEncoder.encode(container.getProperty("ducking.ddl.appName"), "UTF-8");
            String sid = request.getSession(true).getId();
            String errorBack = URLEncoder.encode(VWBContainerImpl.findContainer().getURL("regist", null, null, true),"utf-8");
            ssourl = rootUrl+"?appname="+appName+"&sid="+sid+"&loginURL="+registURL+"&registURL="+errorBack;
        } catch (UnsupportedEncodingException e) {
            LOG.error("",e);
        }
        return ssourl;
    }

    public PageView logout(HttpServletRequest request, String acceptUrl) {
        VWBContainer container = VWBContainerImpl.findContainer();
        String ssourl = container.getProperty("duckling.umt.logout");
        try {
            ssourl = ssourl
                    + "?WebServerURL="
                    + URLEncoder.encode(acceptUrl, "UTF-8")
                    + "&sid="
                    + request.getSession().getId()
                    + "&appname="
                    + URLEncoder.encode(container
                                        .getProperty("duckling.dct.localName"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(),e);
        }
        return new PageView(true, ssourl);
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public void setPublicKeyUrl(String publicKeyUrl) {
        this.publicKeyUrl = publicKeyUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    private void downloadUMTKey(HttpServletRequest request) {
        String umtPublicKeyContent = WebSite.getBodyContent(publicKeyUrl);
        try {
            FileUtils.writeStringToFile(new File(keyPath), umtPublicKeyContent);
        } catch (IOException e) {
            LOG.error("failed:write umtpublickey to file(" + keyPath + ")", e);
        }
    }

    private void loadUMTKeyFromLocal(HttpServletRequest request) {
        if (new File(keyPath).exists()) {
            KeyFile keyFile = new KeyFile();
            try {
                umtKey = keyFile.loadFromPublicKeyContent(PublicKeyEnvelope
                                                          .valueOf(FileUtils.readFileToString(new File(keyPath)))
                                                          .getPublicKey());
            } catch (IOException e) {
                LOG.error("keyPath="+keyPath,e);
                throw new RuntimeException("");
            }
        }
    }

}
