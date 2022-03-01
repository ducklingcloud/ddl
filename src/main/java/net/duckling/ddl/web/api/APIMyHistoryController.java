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

package net.duckling.ddl.web.api;

import com.google.gson.Gson;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.devent.AoneNoticeParam;
import net.duckling.ddl.service.devent.INoticeService;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.devent.NoticeRule;
import net.duckling.ddl.service.mail.notice.DailyCompositeNotice;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 查询系统更新
 *
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/myHistory")
@RequirePermission(target="team", operation="view")
public class APIMyHistoryController extends APIBaseNoticeController {

    @Autowired
    private INoticeService noticeService;

    @SuppressWarnings("unchecked")
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response) {
        Site site = findSite(request);
        int tid = site.getId();
        String uid = findUser(request);
        AoneNoticeParam param = new AoneNoticeParam(tid,NoticeRule.HISTORY_NOTICE, uid);
        List<Notice> noticeList = noticeService.readNotification(param,uid);
        DailyNotice[] dailyGroup = getDailyNoticeArray(noticeList);
        List<DailyCompositeNotice> results = getDailyCompositeList(dailyGroup);
        JsonArray jsonArray = new Gson().toJsonTree(results).getAsJsonArray();

        String api = request.getParameter("api");
        if(api == null || "".equals(api)) {
            // 提交审核的1.1.1版本中没有api参数，兼容性处理
            JsonUtil.write(response, jsonArray);
        } else {
            JsonObject jsonObj = new JsonObject();
            jsonObj.add("records", jsonArray);
            jsonObj.addProperty("api", api);
            JsonUtil.write(response, jsonObj);
        }
    }

}
