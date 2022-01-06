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
package net.duckling.ddl.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.bean.AuthorizationCode;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;

@Controller
@RequestMapping("/system/userToken")
public class LynxUserTokenValidateController {
    private static final Logger LOG = Logger.getLogger(LynxUserTokenValidateController.class);
    private static final char[] hexCode = "0123456789abcdef".toCharArray();
    @Autowired
    private AuthorizationCodeService authorizationCodeService;


    @RequestMapping
    public void validateUserToken(HttpServletRequest req,HttpServletResponse resp){
        String accessToken = req.getParameter("access_token");
        String clientId = req.getParameter("client_id");
        org.json.simple.JSONObject json = new org.json.simple.JSONObject();
        try {
            AccessToken token = authorizationCodeService.umtAccessTokenValidate(accessToken);
            if(token!=null){
                AuthorizationCode code = getCode(token, clientId);
                json.put("code", code.getCode());
            }

        } catch (OAuthProblemException e) {
            LOG.error("",e);
            json.put("error", e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            json.put("errorMessage", sw.toString());
        }
        JsonUtil.writeJSONObject(resp, json);
    }

    private AuthorizationCode getCode(AccessToken token,String clientId) throws OAuthProblemException{
        String c = md5GeneratValue();
        AuthorizationCode code = new AuthorizationCode();
        code.setCode(c);
        code.setAccessToken(token.getAccessToken());
        code.setCreateTime(new Date());
        code.setUid(token.getUserInfo().getCstnetId());
        code.setClientId(clientId);
        authorizationCodeService.create(code);
        return code;
    }


    private String md5GeneratValue() throws OAuthProblemException{
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(UUID.randomUUID().toString().getBytes());
            byte[] messageDigest = algorithm.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("", e);
            throw OAuthProblemException.error(e.getMessage(),e.getMessage());
        }

    }
    public static String toHexString(byte[] data) {
        if(data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length*2);
        for ( byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    @RequestMapping(params="func=getUserInfo")
    public void getUserInfo(HttpServletRequest req,HttpServletResponse resp){
        String accessToken = req.getParameter("access_token");
        try {
            AccessToken token = authorizationCodeService.umtAccessTokenValidate(accessToken);



        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }


}
