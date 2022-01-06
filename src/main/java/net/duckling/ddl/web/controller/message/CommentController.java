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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.AbstactCommentContoller;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;

/**
 * @date 2011-3-21
 * @author Clive Lee
 */
@Controller
@RequestMapping("/{teamCode}/comment")
public class CommentController extends AbstactCommentContoller {

    private VWBContext getVWBContext(HttpServletRequest pRequest, int rid) {
        return VWBContext.createContext(pRequest, UrlPatterns.T_PAGE, rid);
    }

    @RequestMapping(params = "func=loadComments")
    @WebLog(method = "loadComments", params = "rid")
    public void loadComments(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam("itemType") String itemType,@RequestParam("rid") Integer rid) {
        showBriefComments(getVWBContext(request, rid), response);
    }

    @RequestMapping(params = "func=showBriefComments")
    @WebLog(method = "showBriefComments", params = "rid")
    public void showBriefComments(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam("itemType") String itemType,@RequestParam("rid") Integer rid) {
        showBriefComments(getVWBContext(request, rid), response);
    }

    @RequestMapping(params = "func=showDetailComments")
    @WebLog(method = "showDetailComments", params = "rid")
    public void showDetailComments(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam("itemType") String itemType,@RequestParam("rid") Integer rid) {
        showAllComments(getVWBContext(request, rid), response);
    }

    @RequirePermission(target = "team", authenticated = true)
    @RequestMapping(params = "func=submitComment")
    @WebLog(method = "submitComment", params = "rid")
    public void submitComment(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam("itemType") String itemType,@RequestParam("rid") Integer rid) {
        submitComment(getVWBContext(request, rid), request,
                      response);
    }

    @RequestMapping(params = "func=removeComment")
    @WebLog(method = "removeComment", params = "rid")
    public void removeComment(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam("itemType") String itemType,@RequestParam("rid") Integer rid) {
        removeComment(getVWBContext(request, rid), request,
                      response);
    }

}
