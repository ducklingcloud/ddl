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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 为API设计的我的通知
 *
 * @date 2012-12-8
 * @author zzb@cnic.cn
 */
@Controller
@RequestMapping("/api/myFeeds")
@RequirePermission(target="team",operation="view")
public class APIMyFeedsController extends APIBaseNoticeController {

    @Autowired
    private INoticeService noticeService;

    @SuppressWarnings("unchecked")
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response){
        Site site = findSite(request);
        int tid = site.getId();
        String uid = findUser(request);
        AoneNoticeParam queryParam = new AoneNoticeParam(tid,NoticeRule.MONITOR_NOTICE, uid);
        List<Notice> noticeList = noticeService.readNotification(queryParam, uid);
        DailyNotice[] dailyGroup = getDailyNoticeArray(noticeList);
        List<DailyCompositeNotice> results = getDailyCompositeList(dailyGroup);
        JSONArray jsonArray = JsonUtil.getJSONArrayFromList(results);

        String api = request.getParameter("api");
        if(api == null || "".equals(api.trim())) {
            // 提交审核的1.1.2版本中没有api参数，兼容性处理
            JsonUtil.writeJSONObject(response, jsonArray);
            return;
        }

        String messageType = request.getParameter("messageType");
        int teamCount = totalCount(site, messageType, uid);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("api", api);
        jsonObj.put("records", jsonArray);
        jsonObj.put("totalCount", teamCount);
        JsonUtil.writeJSONObject(response, jsonObj);
    }

}
