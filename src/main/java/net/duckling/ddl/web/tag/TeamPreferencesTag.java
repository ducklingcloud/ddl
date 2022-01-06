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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.navbar.INavbarService;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.TeamSpaceSize;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.meepo.api.IPanService;

import org.apache.log4j.Logger;

import com.meepotech.sdk.MeePoUsage;


/**
 * @date 2011-11-14
 * @author clive
 */
public class TeamPreferencesTag extends VWBBaseTag {
    private static final Logger LOG = Logger.getLogger(TeamPreferencesTag.class);
    private static final long serialVersionUID = 1L;

    @Override
    public int doVWBStart() throws Exception {
        String uid = vwbcontext.getCurrentUID();
        TeamService teamService = DDLFacade.getBean(TeamService.class);
        List<TeamPreferences> prefList = teamService
                .getTeamPrefWithoutPersonSpace(uid);
        List<Team> teamList = new ArrayList<Team>();
        if(prefList!=null){
            for (TeamPreferences p : prefList) {
                teamList.add(getBean(TeamService.class).getTeamByID(p.getTid()));
            }
        }else{
            recordLog(uid);
        }
        ServletRequest request = pageContext.getRequest();
        request.setAttribute("myTeamList", teamList);
        int tid = VWBContext.getCurrentTid();
        if (tid == -1) {
            if("pan".equals(request.getAttribute("teamType"))){
                request.setAttribute("current", "pan");
                MeePoUsage usage = DDLFacade.getBean(IPanService.class).usage(PanAclUtil.getInstance((HttpServletRequest)request));
                SimpleUser u = DDLFacade.getBean(AoneUserService.class).getSimpleUserByUid(VWBSession.getCurrentUid((HttpServletRequest)request));
                request.setAttribute("panName", u.getName()+"的个人空间");
                TeamSpaceSize size = new TeamSpaceSize();
                size.setTotal(usage.quota);
                size.setUsed(usage.used);
                request.setAttribute("teamSize", size);

            }else{
                request.setAttribute("current", "dashboard");
                if(request.getAttribute("currPageName")==null){
                    request.setAttribute("currPageName", "个人面板");
                }
            }
        } else {
            Team team = teamService.getTeamByID(tid);
            String teamAcl =DDLFacade.getBean(AuthorityService.class).getTeamAuthority(tid, uid);
            request.setAttribute("teamAcl", teamAcl);
            if (team.isPersonalTeam() && team.getCreator().equals(uid)) {
                request.setAttribute("current", "myspace");
            } else {
                request.setAttribute("current", team.getName());
            }
            if(vwbcontext.getSite()!=null){
                request.setAttribute("navbarList", DDLFacade.getBean(INavbarService.class).getNavbarItems(uid, tid));
            }else{
                recordLog(uid);
            }
            request.setAttribute("currPageName", team.getDisplayName());
            TeamSpaceSize size = DDLFacade.getBean(TeamSpaceSizeService.class).getTeamSpaceSize(tid);
            request.setAttribute("teamSize", size);
            request.setAttribute("teamMemberAmount", DDLFacade.getBean(TeamMemberService.class).getMemberAmount(tid));
            request.setAttribute("teamResourceAmount", DDLFacade.getBean(IResourceService.class).getTeamResourceAmount(tid));
            request.setAttribute("isConferenceTeam", Team.CONFERENCE_TEAM.equals(team.getType()));
        }
        return EVAL_PAGE;
    }

    private void recordLog(String uid){
        ServletRequest request = pageContext.getRequest();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        if(stack!=null){
            for(StackTraceElement s :stack){
                sb.append(s.toString()).append("\n");
            }
        }
        if(request instanceof HttpServletRequest ){
            LOG.warn("请注意这一个警告：prefList is null ;uid=["+uid+"];request URL"+((HttpServletRequest)pageContext.getRequest()).getRequestURI()+sb.toString());
        }else{
            LOG.warn("请注意这一个警告：prefList is null ;uid=["+uid+"];request ContentType"+request.getContentType()+sb.toString());
        }
    }

}
