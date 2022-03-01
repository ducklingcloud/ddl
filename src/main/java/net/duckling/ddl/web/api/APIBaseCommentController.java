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

package net.duckling.ddl.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.AbstactCommentContoller;
import net.duckling.ddl.web.interceptor.access.OnDeny;

import com.google.gson.JsonObject;


/**
 * Comment基础的Controller
 * @date 2012-12-8
 * @author zzb@cnic.cn
 */
public class APIBaseCommentController extends AbstactCommentContoller {
    @SuppressWarnings("unchecked")
    @OnDeny("*")
    public void onDeny(String method, HttpServletRequest request, HttpServletResponse response){
        JsonObject object = new JsonObject();
        object.addProperty("message", "Permission denied or session is time out.");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        JsonUtil.write(response, object);
    }

    protected VWBContext createVWBContext(HttpServletRequest pRequest,int itemId,String itemType) {
        return VWBContext.createContext(pRequest, UrlPatterns.T_PAGE, itemId, itemType);
    }

}
