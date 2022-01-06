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

package net.duckling.ddl.web.interceptor.access;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authority.AuthorityService;

/**
 * 权限检查
 *
 * @date May 9, 2011
 * @author xiejj@cnic.cn
 */
public class VWBPermissionChecker implements PermissionChecker {
    public static final String TEAM = "team";
    public static final String PAGE = "page";
    public static final String COLLECTION = "collection";
    public static final String SYSTEM = "system";
    private AuthorityService authorityService;

    public void setAuthorityService(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    public boolean hasAccess(HttpServletRequest request,
                             RequirePermission requirePermission) throws PageNotFound {
        String target = TEAM;
        if (requirePermission.target() != null) {
            target = requirePermission.target();
        }
        if (requirePermission.authenticated()) {
            VWBSession vwbSession = VWBSession.findSession(request);
            return vwbSession.isAuthenticated();
        }

        if (TEAM.equals(target)) {
            return checkTeamOperation(request, requirePermission);
        }
        return true;
    }

    private boolean checkTeamOperation(HttpServletRequest request,
                                       RequirePermission requirePermission) {
        VWBSession session = VWBSession.findSession(request);
        int tid = VWBContext.getCurrentTid();
        return authorityService.teamAccessability(tid, session,
                                                  requirePermission.operation());
    }
}
