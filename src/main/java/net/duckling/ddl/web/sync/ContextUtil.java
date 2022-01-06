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
package net.duckling.ddl.web.sync;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.sync.Context;

import org.apache.commons.lang.StringUtils;

public class ContextUtil {

    public static Context retrieveContext(HttpServletRequest req){
        String device = req.getParameter("device");
        String token = req.getParameter("access_token");
        String tidStr = req.getParameter("tid");
        int tid = 0;
        if(StringUtils.isNotEmpty(tidStr)){
            tid = Integer.parseInt(tidStr);
        }

        Context ctx = new Context();
        ctx.setDevice(device);
        ctx.setTid(tid);
        ctx.setToken(token);

        ctx.setUid(VWBSession.getCurrentUid(req));
        return ctx;
    }

}
