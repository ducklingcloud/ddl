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

package net.duckling.ddl.web.tag;

import java.io.IOException;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.resource.Resource;

import org.apache.commons.lang.StringUtils;


/**
 * Introduction Here.
 * @date Mar 1, 2010
 * @author xiejj@cnic.cn
 */
public class PermissionTag extends VWBBaseTag {
    private static final String NAME_TEAM_ADMIN = "AdminTeam";
    private static final String NAME_TEAM_EDIT = "EditTeam";
    private static final String NAME_TEAM_VIEW = "ViewTeam";
    private boolean isTeamPermission(String operation) {
        return (NAME_TEAM_VIEW.equals(operation) || NAME_TEAM_EDIT.equals(operation) || NAME_TEAM_ADMIN
                .equals(operation));
    }
    private static final long serialVersionUID = 3761412993048982325L;

    private String[] m_permissionList;

    /**
     * Initializes the tag.
     */
    public void initTag() {
        super.initTag();
        m_permissionList = null;
    }

    /**
     * Sets the permissions to look for (case sensitive).  See above for the format.
     *
     * @param permission A list of permissions
     */
    public void setPermission(String permission) {
        m_permissionList = StringUtils.split(permission, '|');
    }

    /**
     *  Checks a single permission.
     *
     *  @param permission
     *  @return
     */
    private boolean checkPermission(String permission) {
        VWBSession session = vwbcontext.getVWBSession();
        Resource resource = vwbcontext.getResource();
        AuthorityService authorityService=DDLFacade.getBean(AuthorityService.class);
        Site site = vwbcontext.getSite();
        boolean gotPermission = false;
        if (isTeamPermission(permission)) {
            gotPermission = authorityService.teamAccessability(site.getId(), session,permission);
        } else if (resource != null) {
            return true;
        }

        return gotPermission;
    }

    /**
     * Initializes the tag.
     * @return the result of the tag: SKIP_BODY or EVAL_BODY_CONTINUE
     * @throws IOException this exception will never be thrown
     */
    public final int doVWBStart() throws IOException {
        for (int i = 0; i < m_permissionList.length; i++) {
            String perm = m_permissionList[i];

            boolean hasPermission = false;

            if (perm.charAt(0) == '!') {
                hasPermission = !checkPermission(perm.substring(1));
            } else {
                hasPermission = checkPermission(perm);
            }

            if (hasPermission)
            {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
