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

/**
 * @date 2011-8-30
 * @author xiejj@cnic.cn
 */
public class RequestToken implements Cloneable {
    private String token;
    private String tokenSecret;
    private Date createTime;
    public static final RequestToken EMPTY=new RequestToken(null, null, null);
    public RequestToken(String token, String tokenSecret, Date createTime) {
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.createTime = createTime;
    }

    public RequestToken clone() {
        return new RequestToken(token, tokenSecret, createTime);
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
