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

import java.util.Hashtable;

/**
 * @author xiejj@cnic.cn
 *
 * @creation Dec 12, 2010 7:45:13 PM
 */
public class LoginSession {
    private static Hashtable<String, LoginSession> maps = new Hashtable<String, LoginSession>();

    private Hashtable<String, Object> attributes = new Hashtable<String, Object>();

    public static LoginSession getLoginSession(String sessionid) {
        LoginSession login = maps.get(sessionid);
        if (login == null) {
            login = new LoginSession();
            maps.put(sessionid, login);
        }
        return login;
    }

    public static LoginSession removeLoginSession(String sessionid) {
        return maps.remove(sessionid);
    }

    public void setAttribute(String key, Object value) {
        if (value!=null) {
            attributes.put(key, value);
        }
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void invalidate() {
        attributes.clear();
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
}
