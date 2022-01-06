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

package net.duckling.ddl.web.controller.message;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @date 2011-7-8
 * @author Clive Lee
 */
@Controller
@RequestMapping("/{teamCode}/feed")
@RequirePermission(target = "team", operation = "view")
public class FeedController extends BaseController {

    @Autowired
    private SubscriptionServiceImpl subscriptionService;

    private void addFeedByType(String key, Integer value, String type,
                               VWBContext context, HttpServletResponse response) {
        String uid = context.getCurrentUID();
        int tid=context.getTid();
        int feedId = subscriptionService.addSingleFeedRecord(uid,
                                                             getPublisher(value, type,tid));
        JsonUtil.writeResult(response, "status", "success", key, value,
                             "feedId", feedId);
    }

    private Publisher getPublisher(Integer uid, String type,int tid) {
        Publisher pub = new Publisher();
        pub.setId(uid);
        pub.setType(type);
        pub.setTid(tid);
        return pub;
    }

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request, UrlPatterns.MYSPACE);
    }

    private void removeFeedByType(String key, Integer value, String type,
                                  VWBContext context, HttpServletResponse response) {
        String uid = context.getCurrentUID();
        int tid=context.getTid();
        subscriptionService.removeSubscription(uid,
                                               new Publisher[] { getPublisher(value, type,tid) });
        JsonUtil.writeResult(response, "status", "success", key, value);
    }

    @RequestMapping(params = "func=addPageFeed")
    public void addPageFeed(HttpServletRequest request,
                            HttpServletResponse response) {
        Integer pid = Integer.parseInt(request.getParameter("pid"));
        addFeedByType("pid", pid, Publisher.PAGE_TYPE, getVWBContext(request),
                      response);
    }

    @RequestMapping(params = "func=addPersonFeed")
    @RequirePermission(target = "team", operation = "view")
    public void addPersonFeed(HttpServletRequest request,
                              HttpServletResponse response) {
        Integer uid = Integer.parseInt(request.getParameter("uid"));
        addFeedByType("uid", uid, Publisher.PERSON_TYPE,
                      getVWBContext(request), response);
    }

    @RequestMapping(params = "func=preparePageFeed")
    public void prepareSubscription(HttpServletRequest request,
                                    HttpServletResponse response) {
        VWBContext context = getVWBContext(request);
        int pageId = Integer.parseInt(request.getParameter("pid"));
        String uid = context.getCurrentUID();
        int tid = context.getTid();
        List<Subscription> existSub = subscriptionService.getPageSubscribers(tid,pageId);
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        if (existSub != null) {
            for (Subscription temp : existSub) {
                if(uid.equals(temp.getUserId())){
                    jsonArray.add(temp.getPublisher().getType());
                }
            }
            object.put("pageId", pageId);
            object.put("exist", jsonArray);
        }
        JsonUtil.writeJSONObject(response,object);
    }

    @RequestMapping(params = "func=removeFeedByID")
    @RequirePermission(target = "team", operation = "view")
    public void removeFeedRecord(HttpServletRequest request,
                                 HttpServletResponse response) {
        Integer feedId = Integer.parseInt(request.getParameter("feedId"));
        subscriptionService.removeSubscription(feedId);
        JsonUtil.writeResult(response, "feedId", feedId);
    }

    @RequestMapping(params = "func=removePageFeed")
    public void removePageFeed(HttpServletRequest request,
                               HttpServletResponse response) {
        Integer pid = Integer.parseInt(request.getParameter("pid"));
        removeFeedByType("pid", pid, Publisher.PAGE_TYPE,
                         getVWBContext(request), response);
    }

    @RequestMapping(params = "func=removePersonFeed")
    @RequirePermission(target = "team", operation = "view")
    public void removePersonFeed(HttpServletRequest request,
                                 HttpServletResponse response) {
        Integer uid = Integer.parseInt(request.getParameter("uid"));
        removeFeedByType("uid", uid, Publisher.PERSON_TYPE,
                         getVWBContext(request), response);
    }

}
