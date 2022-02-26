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
package net.duckling.ddl.web.controller.activity;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.activity.Activity;
import net.duckling.ddl.service.activity.ActivityService;
import net.duckling.ddl.service.lottery.LotteryService;
import net.duckling.ddl.service.lottery.model.DrawResult;
import net.duckling.ddl.service.space.SpaceGained;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.EmailUtil;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.web.AbstractSpaceController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.umt.oauth.Oauth;
import cn.vlabs.umt.oauth.UMTOauthConnectException;

import com.meepotech.sdk.MeePoException;

@Controller
@RequestMapping("/activity")
@RequirePermission(authenticated = false)
public class ActivityController extends AbstractSpaceController{

    protected static final Logger LOG = Logger.getLogger(ActivityController.class);

    @RequestMapping
    @WebLog(method = "activity-index")
    public ModelAndView display(HttpServletRequest request, HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request,UrlPatterns.SIMPLE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/activity/index.jsp");
        VWBSession session = VWBSession.findSession(request);

        //中奖用户展示
        List<DrawResult> drawList = lotteryService.getGiftedUserList();
        for(DrawResult item : drawList){
            item.setUser(EmailUtil.coverEmail(item.getUser()));
        }
        mv.addObject("drawList", drawList);

        if(!session.isAuthenticated()){
            mv.addObject("authenticated", false);
            mv.addObject("passportUrl", ducklingProperties.get("duckling.umt.site"));
            mv.addObject("loginUrl", getLoginUrl(request,"/activity"));
            return mv;
        }
        mv.addObject("authenticated", true);
        return mv;
    }

    /**
     * 活动主页
     * @param request
     * @param response
     * @throws MeePoException
     */
    @RequestMapping("/task-win-space")
    @WebLog(method = "task-win-space")
    public ModelAndView taskWinSpace(HttpServletRequest request, HttpServletResponse response){
        VWBSession session = VWBSession.findSession(request);
        VWBContext context = VWBContext.createContext(request,UrlPatterns.SIMPLE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/activity/taskWinSpace.jsp");
        if(!session.isAuthenticated()){
            mv.addObject("authenticated", false);
            mv.addObject("passportUrl", ducklingProperties.get("duckling.umt.site"));
            mv.addObject("loginUrl", getLoginUrl(request,"/activity/task-win-space"));
            return mv;
        }
        mv.addObject("authenticated", true);

        String uid = VWBSession.getCurrentUid(request);
        Activity spaceActivity = activityService.get(ActivitySpaceConfig.ACTIVITY_TITLE);

        List<SpaceGained> spaceGainedList = super.spaceGainedService.getList(uid, spaceActivity.getId(), null, null);
        long totalSize = ActivitySpaceConfig.SIZE_COMPUTER_LOGIN + ActivitySpaceConfig.SIZE_CREATE_TEAM +
                ActivitySpaceConfig.SIZE_MOBILE_LOGIN + ActivitySpaceConfig.SIZE_SHARE_FILE;
        long gainedSizePan = 0L;
        long gainedSizeTeam = 0L;
        boolean task1 = false;
        boolean task2 = false;
        boolean task3 = false;
        boolean task4 = false;
        boolean task5 = false;
        for(SpaceGained item : spaceGainedList){
            if(ActivitySpaceConfig.REMARK_WEB_LOGIN.equals(item.getRemark())){
                gainedSizePan+=ActivitySpaceConfig.SIZE_WEB_LOGIN;
                task1 = true;
            }else if(ActivitySpaceConfig.REMARK_COMPUTER_LOGIN.equals(item.getRemark())){
                gainedSizePan+=ActivitySpaceConfig.SIZE_COMPUTER_LOGIN;
                task2 = true;
            }else if(ActivitySpaceConfig.REMARK_MOBILE_LOGIN.equals(item.getRemark())){
                gainedSizePan+=ActivitySpaceConfig.SIZE_MOBILE_LOGIN;
                task3 = true;
            }else if(ActivitySpaceConfig.REMARK_SHARE_FILE.equals(item.getRemark())){
                gainedSizePan+=ActivitySpaceConfig.SIZE_SHARE_FILE;
                task4 = true;
            }else if(SpaceGained.SPACE_TYPE_TEAM == item.getSpaceType() && ActivitySpaceConfig.REMARK_CREATE_TEAM.equals(item.getRemark())){
                gainedSizeTeam+=ActivitySpaceConfig.SIZE_CREATE_TEAM;
                task5 = true;
            }
        }
        mv.addObject("userName", VWBSession.getCurrentUidName(request));
        mv.addObject("totalSize", FileSizeUtils.getFileSize(totalSize));
        mv.addObject("gainedSizePan", FileSizeUtils.getFileSize(gainedSizePan));
        mv.addObject("gainedSizeTeam", FileSizeUtils.getFileSize(gainedSizeTeam));
        mv.addObject("task1", task1);
        mv.addObject("task2", task2);
        mv.addObject("task3", task3);
        mv.addObject("task4", task4);
        mv.addObject("task5", task5);
        return mv;
    }


    private String getLoginUrl(HttpServletRequest request, String uri){
        Oauth o = new Oauth(ducklingProperties);
        String result = "";
        try {
            result = o.getAuthorizeURL(request) + "&state="+URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException | UMTOauthConnectException e) {
            LOG.error("activity get loginUrl error. message:" + e.getMessage());
        }
        return result;
    }

    @Autowired
    private ActivityService activityService;
    @Autowired
    private DucklingProperties ducklingProperties;
    @Autowired
    private LotteryService lotteryService;
}
