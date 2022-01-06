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

package net.duckling.ddl.service.oauth.impl;

import java.util.Date;

import net.oauth.OAuthAccessor;


/**
 * Accessor的数据库存储对象。
 *
 * @date 2011-8-30
 * @author xiejj@cnic.cn
 */
public class AccessorPo {
    private String consumerKey;
    private AccessToken accessToken;
    private RequestToken requestToken;
    private String userId;
    private String screenName;
    private boolean authorized;
    private int id;
    public AccessorPo(){
        accessToken = AccessToken.EMPTY;
        requestToken = RequestToken.EMPTY;
    }
    public void copyDateTo(OAuthAccessor accessor){
        accessor.requestToken=getRequestToken().getToken();
        accessor.tokenSecret =getRequestToken().getTokenSecret();
        accessor.accessToken =getAccessToken().getToken();
        if (isAuthorized()){
            accessor.setProperty("authorized", Boolean.TRUE);
            accessor.setProperty("user", getUserId());
            accessor.setProperty("screenName", screenName);
        }
    }

    public void copyDateFrom(OAuthAccessor accessor){
        if (accessor.requestToken!=null){
            requestToken = new RequestToken(accessor.requestToken, accessor.tokenSecret, new Date());
        }else{
            requestToken = RequestToken.EMPTY;
        }
        if (accessor.accessToken!=null){
            accessToken = new AccessToken(accessor.accessToken, new Date());
        }else{
            accessToken = AccessToken.EMPTY;
        }

        consumerKey=accessor.consumer.consumerKey;
        if (Boolean.TRUE.equals(accessor.getProperty("authorized"))){
            authorized=true;
            userId=(String) accessor.getProperty("user");
            screenName = (String)accessor.getProperty("screenName");
        }
    }
    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public RequestToken getRequestToken() {
        return requestToken;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public void setConsumerKey(String consumerId) {
        this.consumerKey = consumerId;
    }

    public void setRequestToken(RequestToken requestToken) {
        this.requestToken = requestToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the screenName
     */
    public String getScreenName() {
        return screenName;
    }
    /**
     * @param screenName the screenName to set
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}
