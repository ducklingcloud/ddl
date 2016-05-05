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

package net.duckling.ddl.web.filter;

import java.util.Hashtable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authenticate.impl.LoginSession;


public class VWBSessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        LoginSession.removeLoginSession(session.getId());
        remove(session.getId());
    }

    public static VWBSession getVWBSession(String sessionid) {
        return sessionMap.get(sessionid);
    }

    public static void put(String sessionid, VWBSession vwbsession) {
        synchronized (sessionMap) {
            sessionMap.put(sessionid, vwbsession);
        }
    }

    public static void remove(String sessionid) {
        if (sessionid != null) {
            sessionMap.remove(sessionid);
        }
    }

    public static boolean contains(String sessionid) {
        if (sessionid != null) {
            return sessionMap.containsKey(sessionid);
        } else {
            return false;
        }
    }

    private static Hashtable<String, VWBSession> sessionMap = new Hashtable<String, VWBSession>();
}
