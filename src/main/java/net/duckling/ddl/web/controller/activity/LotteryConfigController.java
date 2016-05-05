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

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.service.lottery.LotteryService;
import net.duckling.ddl.web.controller.BaseController;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/activity/lottery/config")
public class LotteryConfigController extends BaseController {

    protected static final Logger LOG = Logger.getLogger(LotteryConfigController.class);

    @ResponseBody
    @RequestMapping(value = "/reloadConfig", method = RequestMethod.GET)
    public String reloadConfig(@RequestParam("user") String user, @RequestParam("password") String pswd,
            HttpServletRequest request) {
        if (isValidate(user, pswd, request)) {
            lotteryService.reloadConfig();
            return "Config Path is reload";
        }
        return "403 Forbbiden";
    }

    @ResponseBody
    @RequestMapping(value = "/initLottery", method = RequestMethod.GET)
    public String generate(@RequestParam("user") String user, @RequestParam("password") String pswd,
            @RequestParam("date") String date, HttpServletRequest request) {
        if (isValidate(user, pswd, request)) {
            lotteryService.stopLottery();
            lotteryService.resetTodayLottery(date);
            lotteryService.reloadConfig();
            lotteryService.generateSequnce(date, null);
            lotteryService.startLottery();
            return "Lottery sequence of " + date + " had generated.";
        }
        return "403 Forbbiden";
    }

    private boolean isValidate(String user, String password, HttpServletRequest request) {
        String ip = this.getIpAddress(request);
        return "159.226.10.99".equals(ip) && "liji@cstnet.cn".equals(user) && "admin123".equals(password);
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

    @ResponseBody
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public String stop(@RequestParam("user") String user, @RequestParam("password") String pswd,
            HttpServletRequest request) {
        if (isValidate(user, pswd, request)) {
            lotteryService.stopLottery();
            return "Lottery is stoppted.";
        }
        return "403 Forbbiden";
    }

    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start(@RequestParam("user") String user, @RequestParam("password") String pswd,
            HttpServletRequest request) {
        if (isValidate(user, pswd, request)) {
            lotteryService.startLottery();
            return "Lottery is started.";
        }
        return "403 Forbbiden";
    }

    @Autowired
    private LotteryService lotteryService;

}
