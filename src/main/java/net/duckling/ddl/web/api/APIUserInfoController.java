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

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/userInfo")
@RequirePermission(target = "team", operation = "view")
public class APIUserInfoController extends APIBaseController {
    @Autowired
    private AoneUserService aoneUserService;

    @RequestMapping
    public void getUserInfo(HttpServletRequest req, HttpServletResponse resp) {
        JsonObject object = new JsonObject();
        String uid = req.getParameter("uid");
        String id = req.getParameter("id");
        if (StringUtils.isEmpty(uid) && StringUtils.isEmpty(id)) {
            object.addProperty("result", false);
            object.addProperty("message", "提供的id信息不能为空");
            JsonUtil.write(resp, object);
            return;
        }
        UserExt user = null;
        if (StringUtils.isNotEmpty(uid)) {
            user = aoneUserService.getUserExtInfo(uid);
        }
        if (StringUtils.isNotEmpty(id)) {
            int i = Integer.parseInt(id);
            user = aoneUserService.getUserExtByAutoID(i);
        }
        if (user != null) {
            putJson("result", true, object);
            putJson("id", user.getId(), object);
            putJson("uid", user.getUid(), object);
            putJson("address", user.getAddress(), object);
            putJson("birthday", user.getBirthday(), object);
            putJson("department", user.getDepartment(), object);
            putJson("email", user.getEmail(), object);
            putJson("mobile", user.getMobile(), object);
            putJson("name", user.getName(), object);
            putJson("sex", user.getSex(), object);
            putJson("telephone", user.getTelephone(), object);
            putJson("qq", user.getQq(), object);
            putJson("weibo", user.getWeibo(), object);
            putJson("orgnization", user.getOrgnization(), object);
        } else {
            object.addProperty("result", false);
            object.addProperty("message", "提供的id信息不正确");
        }
        JsonUtil.write(resp, object);
    }

    private void putJson(String key, Object value, JsonObject object) {
        if (value instanceof String) {
            if (StringUtils.isEmpty((String)value)) {
                object.addProperty(key, "");
            } else {
                object.addProperty(key, (String)value);
            }
        } else {
            object.add(key, new Gson().toJsonTree(value));
        }
    }
}
