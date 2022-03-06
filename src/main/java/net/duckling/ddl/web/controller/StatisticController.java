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

import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("{teamCode}/statistics/upload")
@RequirePermission(target = "team", operation = "view")
public class StatisticController {

    /**
     * 收集文件上传的数据到dlog中
     * @param request
     * @param response
     * @param uidsta
     * @param rid
     * @param fname 文件名称
     * @param size 文件大小
     * @param time1  文件开始上传到最后一次 XmlHttpRequst的时间
     * @param time2 最后一次XmlHttpRequst到上传彻底完成的时间
     * @param total 文件上传总耗时
     */
    @RequestMapping
    //@WebLog(method = "st.upload",params="uid,rid,fname,size,time1,time2,total")
    public void uploadStatistic(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("uid")String uid,
                                @RequestParam("rid")int rid,
                                @RequestParam("fname")String fname,
                                @RequestParam("size")int size,
                                @RequestParam("time1")int time1,
                                @RequestParam("time2")int time2,
                                @RequestParam("total")int total) {
        JsonObject retVal = new JsonObject();
        retVal.addProperty("state", "recived");
        JsonUtil.write(response, retVal);
    }

    /**
     * 更新文件时记录日志到dlog
     */
    @RequestMapping(params = "func=update")
    //@WebLog(method = "st.update",params="uid,rid,fname,size,time1,time2,total")
    public void updateStatistic(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("uid")String uid,
                                @RequestParam("rid")int rid,
                                @RequestParam("fname")String fname,
                                @RequestParam("size")int size,
                                @RequestParam("time1")int time1,
                                @RequestParam("time2")int time2,
                                @RequestParam("total")int total) {
        JsonObject retVal = new JsonObject();
        retVal.addProperty("state", "recived");
        JsonUtil.write(response, retVal);
    }
}
