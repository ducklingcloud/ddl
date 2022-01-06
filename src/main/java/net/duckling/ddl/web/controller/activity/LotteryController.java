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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.service.activity.Activity;
import net.duckling.ddl.service.activity.ActivityService;
import net.duckling.ddl.service.lottery.LotteryService;
import net.duckling.ddl.service.lottery.model.Delivery;
import net.duckling.ddl.service.lottery.model.DrawResponse;
import net.duckling.ddl.service.lottery.model.DrawResult;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.space.SpaceGainedService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.EmailUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.umt.oauth.Oauth;
import cn.vlabs.umt.oauth.UMTOauthConnectException;

@Controller
@RequestMapping("/activity/lottery")
public class LotteryController extends BaseController {

    protected static final Logger LOG = Logger.getLogger(LotteryController.class);

    @RequestMapping
    @WebLog(method = "activity-lottery")
    public ModelAndView display(HttpServletRequest request) {
        VWBSession session = VWBSession.findSession(request);
        VWBContext context = VWBContext.createContext(request, UrlPatterns.SIMPLE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/activity/lottery.jsp");

        //中奖用户展示
        List<DrawResult> drawList = lotteryService.getGiftedUserList();
        for(DrawResult item : drawList){
            item.setUser(EmailUtil.coverEmail(item.getUser()));
        }
        mv.addObject("drawList", drawList);

        if (!session.isAuthenticated()) {
            mv.addObject("authenticated", false);
            mv.addObject("passportUrl", ducklingProperties.get("duckling.umt.site"));
            mv.addObject("loginUrl", getLoginUrl(request, "/activity/lottery"));
            mv.addObject("getEmbedLoginUrl", getEmbedLoginUrl(request, "/activity/lottery"));
            return mv;
        }

        mv.addObject("authenticated", true);
        return mv;
    }

    /**
     * 我的中奖记录
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(params = "func=myPrize", method = RequestMethod.GET)
    @RequirePermission(authenticated = true)
    public ModelAndView myPrize(HttpServletRequest request, HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.SIMPLE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/activity/myPrize.jsp");
        String uid = VWBSession.getCurrentUid(request);
        List<DrawResult> drawListAll = lotteryService.getMyDrawResultList(uid); //包含未中奖记录
        List<DrawResult> drawList = new ArrayList<DrawResult>();
        for(DrawResult item : drawListAll){
            if(item.getGiftLevel()>0){
                drawList.add(item);
            }
        }
        mv.addObject("drawList", drawList);
        return mv;
    }

    @RequestMapping(params = "func=delivery", method = RequestMethod.GET)
    @RequirePermission(authenticated = true)
    public ModelAndView delivery(HttpServletRequest request) {
        String uid = VWBSession.getCurrentUid(request);
        VWBContext context = VWBContext.createContext(request, UrlPatterns.SIMPLE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/activity/delivery.jsp");

        Delivery delivery = lotteryService.queryDelivery(uid);
        if(delivery==null){
            delivery = new Delivery();
        }
        mv.addObject("delivery", delivery);
        return mv;
    }

    @RequestMapping(params = "func=deliverySave", method = RequestMethod.POST)
    @RequirePermission(authenticated = true)
    public ModelAndView deliverySave(HttpServletRequest request) {
        String uid = VWBSession.getCurrentUid(request);
        String realName = request.getParameter("realName");
        String phoneNumber = request.getParameter("phoneNumber");
        String userAddress = request.getParameter("userAddress");

        Delivery delivery = lotteryService.queryDelivery(uid);
        if(delivery==null){
            delivery = new Delivery();
        }
        delivery.setRealName(realName);
        delivery.setPhoneNumber(phoneNumber);
        delivery.setUserAddress(userAddress);
        if(delivery.getUser()==null){
            delivery.setUser(uid);
            delivery.setGiftContent("");
            lotteryService.saveDeliveryInfo(delivery);
        }else{
            lotteryService.updateDelivery(delivery);
        }
        ModelAndView mv = delivery(request);
        mv.addObject("act", "save");
        return mv;
    }

    @ResponseBody
    @RequestMapping(params = "func=draw", method = RequestMethod.GET)
    @RequirePermission(authenticated = true)
    public DrawResponse draw(HttpServletRequest request) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(now);
        String uid = VWBSession.getCurrentUid(request);
        String ip = getIpAddress(request);
        DrawResponse resp = lotteryService.draw(date, VWBSession.getCurrentUid(request), ip);
        DrawResult drawResult = resp.getResult();

        if (drawResult == null) {
            return resp;
        }

        // 是否中空间奖
        if (drawResult.getGiftLevel() == 6 || drawResult.getGiftLevel() == 7) {
            deliverySpacePrize(drawResult.getGiftLevel(), uid, request);
        }
        return resp;
    }


    /**
     * 中奖名单
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value="/winners", method = RequestMethod.GET)
    public ModelAndView winner(HttpServletRequest request, HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.SIMPLE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/activity/winners.jsp");
        List<DrawResult> all = lotteryService.getAllOfDelivery();

        List<DrawResult> level1 = new ArrayList<DrawResult>();
        List<DrawResult> level2 = new ArrayList<DrawResult>();
        List<DrawResult> level3 = new ArrayList<DrawResult>();
        List<DrawResult> level4 = new ArrayList<DrawResult>();
        List<DrawResult> level5 = new ArrayList<DrawResult>();
        String giftname1 = "";
        String giftname2 = "";
        String giftname3 = "";
        String giftname4 = "";
        String giftname5 = "";
        for(DrawResult item : all){
            if(item.getGiftLevel() == 1){
                level1.add(item);
                giftname1 = item.getGiftName();
            } else if(item.getGiftLevel() == 2){
                level2.add(item);
                giftname2 = item.getGiftName();
            } else if(item.getGiftLevel() == 3){
                level3.add(item);
                giftname3 = item.getGiftName();
            } else if(item.getGiftLevel() == 4){
                level4.add(item);
                giftname4 = item.getGiftName();
            } else if(item.getGiftLevel() == 5){
                level5.add(item);
                giftname5 = item.getGiftName();
            }
            item.setUser(EmailUtil.coverEmail(item.getUser()));
        }

        mv.addObject("level1", level1);
        mv.addObject("level2", level2);
        mv.addObject("level3", level3);
        mv.addObject("level4", level4);
        mv.addObject("level5", level5);
        mv.addObject("giftname1", giftname1);
        mv.addObject("giftname2", giftname2);
        mv.addObject("giftname3", giftname3);
        mv.addObject("giftname4", giftname4);
        mv.addObject("giftname5", giftname5);
        return mv;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getLoginUrl(HttpServletRequest request, String uri) {
        Oauth o = new Oauth(ducklingProperties);
        String result = "";
        try {
            result = o.getAuthorizeURL(request) + "&state=" + URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException | UMTOauthConnectException e) {
            LOG.error("activity-lottery get loginUrl error. message:" + e.getMessage());
        }
        return result;
    }

    private String getEmbedLoginUrl(HttpServletRequest request, String uri) {
        String result = getLoginUrl(request, uri);
        return result.replace("theme=full", "theme=embed");
    }

    /**
     * 分配送空间奖品
     *
     * @param giftLevel
     * @param uid
     * @param request
     */
    private void deliverySpacePrize(int giftLevel, String uid, HttpServletRequest request) {
        Activity activity = activityService.get(ActivitySpaceConfig.ACTIVITY_TITLE_LOTTERY);
        if (!activity.isFinished()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (giftLevel == 6) {
                spaceGainedService.addSpaceOfTeam(uid, activity.getId(), ActivitySpaceConfig.SIZE_LOTTERY_TEAM,
                                                  ActivitySpaceConfig.REMARK_LOTTERY_TEAM + " " + sdf.format(new Date()));
            } else {
                spaceGainedService.addSpaceOfPan(uid, activity.getId(), ActivitySpaceConfig.SIZE_LOTTERY_PAN,
                                                 ActivitySpaceConfig.REMARK_LOTTERY_PAN + " " + sdf.format(new Date()),
                                                 PanAclUtil.getInstance(request));
            }
        }
    }

    @Autowired
    private LotteryService lotteryService;
    @Autowired
    private DucklingProperties ducklingProperties;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SpaceGainedService spaceGainedService;

}
