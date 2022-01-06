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
/**
 *
 */
package net.duckling.ddl.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.copy.CopyException;
import net.duckling.ddl.service.copy.ICopyService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author lvly
 * @since 2012-11-13
 */
@Controller
@RequestMapping("{teamCode}/copy")
@RequirePermission(target = "team", operation = "view")
public class CopyController extends BaseController {

    private static final Logger LOG=Logger.getLogger(CopyController.class);

    @Autowired
    private ICopyService copyService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AuthorityService authorityService;
    @RequestMapping
    @ResponseBody
    public boolean copy(HttpServletRequest request,
                        @RequestParam("cover[]")boolean[] cover,
                        @RequestParam("fromRid")int fromRid,
                        @RequestParam("toTids[]")int[] toTids,
                        @RequestParam("version") int version){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_COPY_PATTERNS);
        try{
            copyService.doCopy(fromRid,version,VWBContext.getCurrentTid(), toTids, cover,context.getCurrentUID());
        }catch(CopyException e){
            LOG.error("Do Copy Error:", e);
            return false;
        }
        return true;
    }
    @RequestMapping(params="func=test")
    public ModelAndView test(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_TASK_PATTERNS);
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                 "/jsp/aone/tag/bundle-flow-layout.jsp");
        return mv;
    }
    @RequestMapping(params="func=getCanEditTeamList")
    @ResponseBody
    public List<CopyTeam> getCanEditTeamList(HttpServletRequest request,@RequestParam("fromRid")int rid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_COPY_PATTERNS);
        List<TeamPreferences> prefList = teamPreferenceService.getAllTeamPrefs(context.getCurrentUID());
        List<CopyTeam> teamList = new ArrayList<CopyTeam>();
        for (TeamPreferences p : prefList) {
            String auth=authorityService.getTeamAuthority(p.getTid(), context.getCurrentUID());
            if(Team.AUTH_ADMIN.equals(auth)||Team.AUTH_EDIT.equals(auth)){
                boolean needCover=VWBContext.getCurrentTid()==p.getTid()?false: copyService.isNeedCover(rid, p.getTid());
                teamList.add(new CopyTeam(teamService.getTeamByID(p.getTid()),needCover));
            }
        }
        return teamList;
    }
    public static class CopyTeam{
        private Team team;
        private boolean isNeedCover;
        public CopyTeam(Team team,boolean isNeedCover){
            this.team=team;
            this.isNeedCover=isNeedCover;
        }
        public Team getTeam() {
            return team;
        }
        public void setTeam(Team team) {
            this.team = team;
        }
        public boolean isNeedCover() {
            return isNeedCover;
        }
        public void setNeed(boolean isNeed) {
            this.isNeedCover = isNeed;
        }

    }
}
