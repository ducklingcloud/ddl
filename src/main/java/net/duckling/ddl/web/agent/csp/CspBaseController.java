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
package net.duckling.ddl.web.agent.csp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.vo.ErrorMsg;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class CspBaseController {
    @OnDeny("*")
    public void onDeny(String method, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        JsonUtil.writeJSONP(request, response, ErrorMsg.NEED_PERMISSION, null, null);
    }

    @ExceptionHandler
    public void exp(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        if(ex instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException me = (MissingServletRequestParameterException)ex;
            ErrorMsg msg = ErrorMsg.MISSING_PARAMETER;
            msg.setMsg("Required parameter ["+ me.getParameterName() +"] is missing.");
            writeError(msg, request, response);
        }else if(ex instanceof TypeMismatchException){
            TypeMismatchException te = (TypeMismatchException)ex;
            ErrorMsg msg = ErrorMsg.TYPE_MISMATCH;
            msg.setMsg("Value ["+ te.getValue() +"] of type is mismatch.");
            writeError(msg, request, response);
        }else{
            writeError(ErrorMsg.UNKNOW_ERROR, request, response);
        }
    }

    public String getCurrentUid(HttpServletRequest request) {
        return VWBSession.getCurrentUid(request);
    }

    public String getCurrentUsername(HttpServletRequest request) {
        return VWBSession.getCurrentUidName(request);
    }

    public int getCurrentTid() {
        return VWBContext.getCurrentTid();
    }

    public void writeError(ErrorMsg msg, int status, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(status);
        JsonUtil.writeJSONP(request, response, msg, null, null);
    }

    public void writeError(ErrorMsg msg, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        JsonUtil.writeJSONP(request, response, msg, null);
    }

    protected Team getUserTeam(String uid, String teamCode){
        if(StringUtils.isEmpty(teamCode)){
            return null;
        }
        List<Team> teamList = teamService.getAllUserTeams(uid);
        for(Team t : teamList){
            if(teamCode.equals(t.getName())){
                return t;
            }
        }
        return null;
    }

    @Autowired
    TeamService teamService;
}
