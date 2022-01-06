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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.mobile.android.AndroidMessageBean;
import net.duckling.ddl.service.mobile.android.AndroidNoticeHandler;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * android 消息轮询获取Controller
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("api/androidMessgePull")
@RequirePermission(target="system",operation="view")
public class APIAndroidMessagePullController extends APIBaseController{
    @Autowired
    private TeamService teamService;
    @Autowired
    private AndroidNoticeHandler androidNoticeHandler;
    @RequestMapping
    public void init(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        String uid = findUser(req);
        if(StringUtils.isEmpty(uid)||"Guest".equals(uid)){
            resp.sendError(401);
            JSONObject obj = new JSONObject();
            obj.put("error", "未登陆!");
            JsonUtil.writeJSONObject(resp, obj);
            return;
        }
        String sessionId = req.getSession().getId();
        AndroidMessageBean bean = androidNoticeHandler.getUserMessage(uid, sessionId);
        JSONObject obj = new JSONObject();
        addMessage(bean, obj);
        if(bean.getLatestMessageTeamId()>0){
            Team team = teamService.getTeamByID(bean.getLatestMessageTeamId());
            if(team!=null){
                obj.put("latestMesageTeamCode", team.getName());
                obj.put("latestMesageTeamName", team.getDisplayName());
            }
        }
        JsonUtil.writeJSONObject(resp, obj);
    }

    private void addMessage(AndroidMessageBean bean,JSONObject obj){
        obj.put("messageType", bean.getType());
        if(bean.isNoMessage()){
            return;
        }
        obj.put("allMessage", bean.getMessageCount());
        obj.put("latestMessage", bean.getLatestCount());
        obj.put("message", bean.getMessage());
        obj.put("isMoreTeamMessage", bean.isMoreTeamMessage());
    }
}
