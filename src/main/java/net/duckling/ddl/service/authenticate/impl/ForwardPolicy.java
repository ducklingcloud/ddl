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

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;


/**
 * @date Jul 14, 2011
 * @author xiejj@cnic.cn
 */
public class ForwardPolicy {

    public String getSavedFailURL(HttpServletRequest request) {
        return (String) getLoginSession(request).removeAttribute(Attributes.LOGIN_FAIL_URL);
    }

    public String getSavedSuccessURL(HttpServletRequest request) {
        return (String) getLoginSession(request).removeAttribute(Attributes.REQUEST_URL);
    }

    public void saveFailURL(HttpServletRequest request, String url) {
        if (url != null) {
            getLoginSession(request).setAttribute(Attributes.LOGIN_FAIL_URL, url);
        }
    }

    public void saveSuccessURL(HttpServletRequest request, String url) {
        LoginSession loginSession = getLoginSession(request);
        if (url == null) {
            // Saved from other place
            url = (String) VWBSession.findSession(request).removeAttribute(Attributes.REQUEST_URL);
            if (url != null) {
                loginSession.setAttribute(Attributes.REQUEST_URL, url);
            } else {
                // If has reference
                String referer = request.getHeader("Referer");
                if (referer != null) {
                    url = referer;
                } else {
                    // Use switch team
                    url = VWBContainerImpl.findContainer().getURL("switchTeam", null, null, true);
                }
            }
        }
        loginSession.setAttribute(Attributes.REQUEST_URL, url);
    }

    public void clearUrls(HttpServletRequest request) {
        LoginSession.removeLoginSession(request.getSession().getId());
    }

    private LoginSession getLoginSession(HttpServletRequest request) {
        String sessionid = request.getSession().getId();
        return LoginSession.getLoginSession(sessionid);
    }
}
