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
package net.duckling.ddl.service.user.impl;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.web.bean.AuthorizationCode;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.UserInfo;
import cn.vlabs.umt.oauth.client.HttpClient;
import cn.vlabs.umt.oauth.client.HttpsConnectionClient;
import cn.vlabs.umt.oauth.client.OAuthClient;
import cn.vlabs.umt.oauth.client.URLConnectionClient;
import cn.vlabs.umt.oauth.client.request.OAuthClientRequest;
import cn.vlabs.umt.oauth.client.request.OAuthClientRequest.TokenRequestBuilder;
import cn.vlabs.umt.oauth.client.response.OAuthJSONAccessTokenResponse;
import cn.vlabs.umt.oauth.common.OAuth;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;
import cn.vlabs.umt.oauth.common.exception.OAuthSystemException;

@Service("authorizationCodeService")
public class AuthorizationCodeServiceImpl implements AuthorizationCodeService {
    private static final String OAUTH_CLIENT_ID = "client_id";
    private static final String OAUTH_CLIENT_SECRET = "client_secret";
    private static final String OAUTH_ACCESS_TOKEN_URL = "access_token_URL";
    private static final String REDIRCET_URI = "redirect_uri";
    /**
     * umt token校验方法
     */
    private static final String OAUTH_GRANT = "validate_access_token";
    private static final String OAUTH_GRANT_PASSWORD = "password";
    private static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    private static final String OAUTH_GRANT_TYPE = "grant_type";
    @Autowired
    private AuthorizationCodeDAO authorizationCodeDAO;
    @Autowired
    private DucklingProperties properties;

    @Override
    public void create(AuthorizationCode code) {
        authorizationCodeDAO.create(code);
    }

    @Override
    public AuthorizationCode getCode(String code) {
        AuthorizationCode c = authorizationCodeDAO.getCode(code);
        if (c != null && !c.isUsed()) {
            authorizationCodeDAO.udateStatus(c.getId(),
                                             AuthorizationCode.STATUS_USED);
        }
        return c;
    }

    public AccessToken umtRefreshToken(String refreshtoken)
            throws OAuthProblemException {
        try {
            TokenRequestBuilder builder = OAuthClientRequest
                    .tokenLocation(properties
                                   .getProperty(OAUTH_ACCESS_TOKEN_URL));
            builder.setClientId(properties.getProperty(OAUTH_CLIENT_ID));
            builder.setClientSecret(properties.getProperty(OAUTH_CLIENT_SECRET));
            builder.setParameter(OAUTH_GRANT_TYPE, OAUTH_REFRESH_TOKEN);
            builder.setParameter("refresh_token", refreshtoken);

            OAuthClientRequest request = builder.buildBodyMessage();

            OAuthClient oAuthClient = new OAuthClient(getHttpClient());
            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
                    .accessToken(request, OAuthJSONAccessTokenResponse.class);
            String error = oAuthResponse.getParam("error");
            if (error != null && error.length() > 0) {
                OAuthProblemException ex = OAuthProblemException.error(error,
                                                                       oAuthResponse.getParam("error_description"));
                throw ex;
            }
            AccessToken token = new AccessToken();
            token.setAccessToken(oAuthResponse.getAccessToken());
            token.setRefreshToken(oAuthResponse.getRefreshToken());
            token.setExpiresIn(oAuthResponse.getExpiresIn());
            token.setScope(oAuthResponse.getScope());
            token.setUserInfo(getUserInfo(oAuthResponse.getParam("userInfo")));
            return token;
        } catch (OAuthSystemException e) {
            throw OAuthProblemException.error("systemError", e.getMessage());
        }
    }

    @Override
    public AccessToken umtAccessTokenValidate(String accessToken)
            throws OAuthProblemException {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(
                        properties.getProperty(OAUTH_ACCESS_TOKEN_URL))
                    .setClientId(properties.getProperty(OAUTH_CLIENT_ID))
                    .setClientSecret(
                        properties.getProperty(OAUTH_CLIENT_SECRET))
                    .setParameter(OAUTH_GRANT_TYPE, OAUTH_GRANT)
                    .setParameter(OAuth.OAUTH_ACCESS_TOKEN, accessToken)
                    .buildBodyMessage();
            OAuthClient oAuthClient = new OAuthClient(getHttpClient());
            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
                    .accessToken(request, OAuthJSONAccessTokenResponse.class);
            String error = oAuthResponse.getParam("error");
            if (error != null && error.length() > 0) {
                OAuthProblemException ex = OAuthProblemException.error(error,
                                                                       oAuthResponse.getParam("error_description"));
                throw ex;
            }
            AccessToken token = new AccessToken();
            token.setAccessToken(oAuthResponse.getAccessToken());
            token.setRefreshToken(oAuthResponse.getRefreshToken());
            token.setExpiresIn(oAuthResponse.getExpiresIn());
            token.setScope(oAuthResponse.getScope());
            token.setUserInfo(getUserInfo(oAuthResponse.getParam("userInfo")));
            return token;
        } catch (OAuthSystemException e) {
            throw OAuthProblemException.error("systemError", e.getMessage());
        }
    }

    public AccessToken umtPasswordAccessToken(String userName,String password) throws OAuthProblemException{
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(
                        properties.getProperty(OAUTH_ACCESS_TOKEN_URL))
                    .setClientId(properties.getProperty(OAUTH_CLIENT_ID))
                    .setClientSecret(
                        properties.getProperty(OAUTH_CLIENT_SECRET))
                    .setParameter(OAUTH_GRANT_TYPE, OAUTH_GRANT_PASSWORD)
                    .setParameter(OAuth.OAUTH_USERNAME, userName)
                    .setParameter(OAuth.OAUTH_PASSWORD, password)
                    .setParameter(OAuth.OAUTH_REDIRECT_URI, properties.getProperty(REDIRCET_URI))
                    .buildBodyMessage();
            OAuthClient oAuthClient = new OAuthClient(getHttpClient());
            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
                    .accessToken(request, OAuthJSONAccessTokenResponse.class);
            String error = oAuthResponse.getParam("error");
            if (error != null && error.length() > 0) {
                OAuthProblemException ex = OAuthProblemException.error(error,
                                                                       oAuthResponse.getParam("error_description"));
                throw ex;
            }
            AccessToken token = new AccessToken();
            token.setAccessToken(oAuthResponse.getAccessToken());
            token.setRefreshToken(oAuthResponse.getRefreshToken());
            token.setExpiresIn(oAuthResponse.getExpiresIn());
            token.setScope(oAuthResponse.getScope());
            token.setUserInfo(getUserInfo(oAuthResponse.getParam("userInfo")));
            return token;
        } catch (OAuthSystemException e) {
            throw OAuthProblemException.error("systemError", e.getMessage());
        }
    }
    private HttpClient getHttpClient() {
        if (properties.getProperty(OAUTH_ACCESS_TOKEN_URL).toLowerCase()
            .startsWith("https")) {
            return new HttpsConnectionClient();
        } else {
            return new URLConnectionClient();
        }
    }

    private UserInfo getUserInfo(String param) {
        if (param == null || param.length() == 0) {
            return null;
        }
        JSONObject obj;
        try {
            UserInfo user = new UserInfo();
            obj = new JSONObject(param);
            user.setType(getFromJSON(obj, "type"));
            user.setTrueName(getFromJSON(obj, "truename"));
            user.setCstnetId(getFromJSON(obj, "cstnetId"));
            user.setUmtId(getFromJSON(obj, "umtId"));
            user.setPasswordType(getFromJSON(obj, "passwordType"));
            user.setCstnetIdStatus(getFromJSON(obj, "cstnetIdStatus"));
            user.setSecurityEmail(getFromJSON(obj, "securityEmail"));
            try {
                JSONArray emails = obj.getJSONArray("secondaryEmails");
                if (emails != null && emails.length() > 0) {
                    String[] r = new String[emails.length()];
                    for (int i = 0; i < emails.length(); i++) {
                        r[i] = emails.getString(i);
                    }
                    user.setSecondaryEmails(r);
                }
            } catch (JSONException e) {
            }
            return user;
        } catch (JSONException e) {
            return null;
        }
    }

    private String getFromJSON(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }
}
