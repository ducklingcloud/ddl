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
package net.duckling.ddl.web.controller.space;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.activity.Activity;
import net.duckling.ddl.service.activity.ActivityService;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.UserTeamAclBean;
import net.duckling.ddl.service.resource.TeamSpaceSize;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.space.SpaceGained;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.team.TeamSpaceApplicationService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.AbstractSpaceController;
// import net.duckling.ddl.web.controller.activity.ActivitySpaceConfig;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequirePermission(authenticated = true)
@RequestMapping("/system/space")
public class SpaceController extends AbstractSpaceController{

    protected static final Logger LOG = Logger.getLogger(SpaceController.class);

    private static final int SUCCESS = 0;
    private static final int ERROR = 2;

    @RequestMapping
    @WebLog(method = "spaceManager")
    public ModelAndView display(HttpServletRequest request){
        String uid = VWBSession.getCurrentUid(request);
        UserExt ext = aoneUserService.getUserExtInfo(uid);
        // Activity spaceActivity = activityService.get(ActivitySpaceConfig.ACTIVITY_TITLE);

        List<UserTeamAclBean> adminAclList = authorityService.getTeamAclByUidAndAuth(uid, AuthorityService.ADMIN);

        List<TeamSpaceSize> teamSpaceSizeList = new ArrayList<TeamSpaceSize>();
        TeamSpaceSize personalSpaceSize = null;
        for(UserTeamAclBean item : adminAclList){
            Team team = teamService.getTeamByID(item.getTid());
            TeamSpaceSize obj = teamSpaceSizeService.getTeamSpaceSize(item.getTid());
            obj.setTeamDisplayName(team.getDisplayName());
            if(team.isPersonalTeam()){
                personalSpaceSize = obj;
            }else{
                teamSpaceSizeList.add(obj);
            }
        }

        VWBContext context = VWBContext.createContext(request,UrlPatterns.ADMIN);
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/space/manager.jsp");

        long spaceGainedCount = spaceGainedCount(uid);
        mv.addObject("spaceGainedCount",FileSizeUtils.getFileSize(spaceGainedCount));
        mv.addObject("allocatedSpace", FileSizeUtils.getFileSize(spaceGainedCount-ext.getUnallocatedSpace()));
        mv.addObject("unallocatedSpace", FileSizeUtils.getFileSize(ext.getUnallocatedSpace()));

        // mv.addObject("endTime",DateUtil.formatDate(spaceActivity.getEndTime(), "yyyy.MM.dd"));
        mv.addObject("endTime", "yyyy.MM.dd");
        mv.addObject("teamSpaceSizeList", teamSpaceSizeList);
        mv.addObject("personalSpaceSize", personalSpaceSize);

        //页面显示标题
        mv.addObject("currPageName", "分配空间");
        return mv;
    }

    @RequestMapping(params="func=allocate")
    public void allocate(HttpServletRequest request, HttpServletResponse response){
        int allocatedTid = Integer.parseInt(request.getParameter("allocatedTid"));
        String uid = VWBSession.getCurrentUid(request);
        //验证权限
        String auth = authorityService.getTeamAuthority(allocatedTid, uid);
        if(!AuthorityService.ADMIN.equals(auth)){
            writeResponse(response, ERROR, "您没有团队的管理权限.", null);
            return;
        }

        long allocatedSize = FileSizeUtils.getByteSize(request.getParameter("allocatedSize").replace(" ", ""));
        UserExt ext = aoneUserService.getUserExtInfo(uid);
        if(ext.getUnallocatedSpace()<allocatedSize || allocatedSize<=0){
            writeResponse(response, ERROR, "可分配空间不足.", null);
            return;
        }
        TeamSpaceSize teamSpaceSize = null;

        long unallocatedSpace = teamSpaceApplicationService.updateSpaceAllocate(uid, allocatedSize, allocatedTid);
        teamSpaceSize = teamSpaceSizeService.getTeamSpaceSize(allocatedTid);

        Map<String, Object> mv = new HashMap<String, Object>();
        long spaceGainedCount = spaceGainedCount(uid);
        mv.put("allocatedSpace", FileSizeUtils.getFileSize(spaceGainedCount - unallocatedSpace));
        mv.put("unallocatedSpace", FileSizeUtils.getFileSize(unallocatedSpace));
        mv.put("teamSpaceTotal", teamSpaceSize.getTotalDisplay());
        mv.put("teamSpaceUsed", teamSpaceSize.getUsedDisplay());
        mv.put("teamSpacePercent", teamSpaceSize.getPercentDisplay());
        writeResponse(response, SUCCESS, "分配操作成功!", mv);
    }

    @RequestMapping(params="func=allocateAll")
    public void allocateAll(HttpServletRequest request, HttpServletResponse response){
        String uid = VWBSession.getCurrentUid(request);
        VWBContext context = VWBContext.createContext(request, UrlPatterns.PLAIN);
        VWBContainer container = context.getContainer();
        String admin = container.getProperty("duckling.rootvo.admincount");
        if(!admin.equals(uid)){
            writeResponse(response, ERROR, "对不起，没有管理权限。", null);
            return;
        }
        int count = teamSpaceApplicationService.updateSpaceAllocateAll();
        writeResponse(response, SUCCESS, "分配操作成功, 用户数：" + count, null);
    }

    @SuppressWarnings("unchecked")
    private static void writeResponse(HttpServletResponse response,
                                      int state, String message,
                                      Map<String,Object> params) {
        JsonObject msg = new JsonObject();
        msg.addProperty("state", state);
        msg.addProperty("msg", message);
        if (params != null) {
            Gson gson = new Gson();
            for (String key : params.keySet()) {
                msg.add(key, gson.toJsonTree(params.get(key)));
            }
        }
        JsonUtil.write(response, msg);
    }

    /**
     * 赚得的空间统计
     * @param uid
     * @return
     */
    private long spaceGainedCount(String uid){
        List<SpaceGained> spaceGainedList = super.spaceGainedService.getList(uid, null, null, SpaceGained.SPACE_TYPE_TEAM);
        long amount = 0L;
        for(SpaceGained item : spaceGainedList){
            amount+=item.getSize();
        }
        return amount;
    }

    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private TeamSpaceApplicationService teamSpaceApplicationService;
}
