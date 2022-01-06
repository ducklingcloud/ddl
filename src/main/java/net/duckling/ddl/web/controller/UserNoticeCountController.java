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
package net.duckling.ddl.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.common.util.JSONHelper;
import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.invitation.InvitationService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("system/userNotice")
public class UserNoticeCountController {
    @Autowired
    private DucklingProperties config;
    @Autowired
    private TeamService teamService;
    @Autowired
    private InvitationService invitationService;
    @RequestMapping
    public void userNoticeCount(HttpServletRequest req,HttpServletResponse resp){
        JSONObject obj = new JSONObject();
        if(!validateIp(req)){
            obj.put("message", "请求ip没有权限");
            JSONHelper.writeJSONObject(resp, obj);
            return ;
        }
        String uid =req.getParameter("uid");
        if(StringUtils.isEmpty(uid)){
            obj.put("message", "uid不能为空");
            JSONHelper.writeJSONObject(resp, obj);
            return ;
        }
        List<TeamPreferences> pref = teamService.getTeamPrefWithoutPersonSpace(uid);
        int teamInvites = invitationService.getInvitationCount(uid);
        //      Map teamMap = getCountMapByType(pref, NoticeRule.TEAM_NOTICE);
        Map personMap = getCountMapByType(pref, NoticeRule.PERSON_NOTICE);
        Map monitorMap = getCountMapByType(pref, NoticeRule.MONITOR_NOTICE);
        //      int teamCount = getTotalCount(teamMap);
        int personCount = getTotalCount(personMap);
        int monitorCount = getTotalCount(monitorMap);
        int count = teamInvites + personCount + monitorCount;
        obj.put("count", count);
        JSONHelper.writeJSONObject(resp, obj);
    }

    private boolean validateIp(HttpServletRequest req){
        String dChatIp = config.getProperty("dchat.ip");
        if(StringUtils.isEmpty(dChatIp)){
            return false;
        }
        String[] ips = dChatIp.split(",");
        String id = req.getRemoteAddr();
        if(ipEquals(id, ips)){
            return true;
        }
        String nginxIp = req.getHeader("x-real-ip");
        if(ipEquals(nginxIp, ips)){
            return true;
        }

        return false;
    }
    private boolean ipEquals(String s,String[] configIps){
        if(StringUtils.isEmpty(s)){
            return false;
        }
        for(String ip: configIps){
            if(s.equals(ip)){
                return true;
            }
        }
        return false;
    }

    private int getTotalCount(Map<Integer, Integer> countMap) {
        int totalCount = 0;
        for (Integer count : countMap.values()) {
            totalCount += count;
        }
        return totalCount;
    }

    private Map<Integer, Integer> getCountMapByType(List<TeamPreferences> prefList, String type) {
        Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
        if (prefList != null) {
            for (TeamPreferences p : prefList) {
                if (NoticeRule.TEAM_NOTICE.equals(type)) {
                    countMap.put(p.getTid(), p.getTeamNoticeCount());
                } else if (NoticeRule.PERSON_NOTICE.equals(type)) {
                    countMap.put(p.getTid(), p.getPersonNoticeCount());
                } else {
                    countMap.put(p.getTid(), p.getMonitorNoticeCount());
                }
            }
        }
        return countMap;
    }


}
