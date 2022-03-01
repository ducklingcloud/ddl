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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.JsonUtil;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cnic.cerc.dlog.client.WebLog;


/**
 * @date 2012-03-21
 * @author zhuzs
 * This is demo for logging system
 */
@Controller
@RequestMapping("/{teamCode}/reclogger")
public class RecLoggingController extends BaseController{
    @Autowired
    private TeamService teamService;
    /*
     * 日志代号记录
     *
     * 点击事件 click
     *
     */
    @RequestMapping(params="func=cl")
    @WebLog(method="recommendLog",params="pid")
    public void recordClickLog(HttpServletRequest request,HttpServletResponse response, @PathVariable("teamCode")String teamCode){
        int cftid = teamService.getTeamByName(teamCode).getId();
        request.setAttribute("cftid", cftid);
        JsonUtil.write(response, new JsonObject());
    }

    @RequestMapping(params="func=dapgelog")
    @WebLog(method="pageoper",params="type,pid,oper_name")
    public void getLog(HttpServletRequest request,HttpServletResponse response, @PathVariable("teamCode")String teamCode) {
        String operName = request.getParameter("oper_name");
        String pid = request.getParameter("pid");
        String type = request.getParameter("type");
        if(type.equals("page")){
            int cftid = teamService.getTeamByName(teamCode).getId();
            request.setAttribute("cftid", cftid);
        }
        if(type.equals("file")){
            int cftid = teamService.getTeamByName(teamCode).getId();
            request.setAttribute("cftid", cftid);
        }
        if(type.equals("bundle")){
            int cftid = teamService.getTeamByName(teamCode).getId();
            request.setAttribute("cftid", cftid);
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("type",type);
        obj.addProperty("pid",pid);
        obj.addProperty("oper_name",operName);
        JsonUtil.write(response, obj);
    }

}
