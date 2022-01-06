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

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authority.AuthorityService;

/**
 *  检查当前team下用户权限，如果未达到权限要求，不以显示tag内的内容
 * @author zhonghui
 *
 */
public class UserAuthCheckTag extends VWBBaseTag {
    /**
     *
     */
    private static final long serialVersionUID = 555566644431L;
    private String auth;

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public int doVWBStart() throws Exception {
        VWBSession session = vwbcontext.getVWBSession();
        int tid = VWBContext.getCurrentTid();
        if(DDLFacade.getBean(AuthorityService.class).teamAccessability(tid, session, auth)){
            return EVAL_BODY_INCLUDE;
        }else{
            return SKIP_BODY;
        }
    }

}
