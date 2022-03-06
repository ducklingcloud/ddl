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
package net.duckling.ddl.web.api;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.util.JsonUtil;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;

@Controller
@RequestMapping("/oauth/umt/access_token")
public class APIAccessTokenUMTController {
    private static final Logger LOG = Logger.getLogger(APIAccessTokenUMTController.class);
    @Autowired
    private AuthorizationCodeService authcodeService;

    @SuppressWarnings("unchecked")
    //@WebLog(method = " APIAccessTokenUMT", params = "userName")
    @RequestMapping(method=RequestMethod.POST)
    public void service(HttpServletRequest request,HttpServletResponse response){
        JsonObject obj = new JsonObject();
        String uid = request.getParameter("userName");
        String password = request.getParameter("password");
        try {
            AccessToken token = authcodeService.umtPasswordAccessToken(uid, password);
            obj.addProperty("accessToken", token.getAccessToken());
            obj.addProperty("refreshToken", token.getRefreshToken());
            obj.addProperty("expiresIn", token.getExpiresIn());
            obj.addProperty("DisplayName", token.getUserInfo().getTrueName());
            String cstnetId = token.getUserInfo().getCstnetId();
            obj.addProperty("uid", cstnetId);

            LOG.info("uid="+cstnetId+",accessToken="+token.getAccessToken()+",refreshToken="+token.getRefreshToken());
        } catch (OAuthProblemException e) {
            String s = e.getDescription();
            if("用户名或密码校验错误".equals(s)){
                obj.addProperty("message", "用户名或密码错误");
            }else{
                obj.addProperty("message", e.getDescription());
            }
            LOG.error("", e);
        }
        JsonUtil.write(response, obj);
    }
}
