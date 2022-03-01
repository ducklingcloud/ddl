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
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 更新团队消息数量
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/api/updateTeamNotice")
public class APIUpdateTeamNoticeController extends APIBaseController{
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @RequestMapping
    public void updateTeamNotice(HttpServletRequest request, HttpServletResponse response) {
        String type = request.getParameter("messageType");
        int tid = Integer.parseInt(request.getParameter("teamId"));
        VWBContext context = VWBContext.createContext(request, UrlPatterns.SWITCH_TEAM);
        String uid = context.getCurrentUID();
        teamPreferenceService.updateNoticeAccessTime(uid, tid, type);
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("tid", tid);
        JsonUtil.write(response, json);
    }
}
