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
package net.duckling.ddl.web.agent.util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import cn.vlabs.commons.principal.UserPrincipal;

public class AccessUtil {
    public static void openSession(String uid, HttpServletRequest request){
        VWBSession vwbsession = VWBSession.findSession(request);
        UserPrincipal user = new UserPrincipal(uid, "", uid, "");
        List<Principal> set = new ArrayList<Principal>();
        set.add(user);
        vwbsession.setPrincipals(set);
    }

    public static void setCurrentTid(int tid){
        VWBContext.setCurrentTid(tid);
    }
}
