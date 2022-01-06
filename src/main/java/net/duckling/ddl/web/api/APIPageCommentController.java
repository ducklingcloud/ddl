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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;


/**
 * 查询系统更新
 *
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/pageComments")
@RequirePermission(target="team", operation="view")
public class APIPageCommentController extends APIBaseCommentController {

    @RequestMapping
    @WebLog(method = "apiShowAllComments", params = "itemId,itemType")
    public void service(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam("itemId")Integer itemId,@RequestParam("itemType")String itemType) {
        VWBContext context = createVWBContext(request, itemId, itemType);
        showAllComments(context, response);
    }

}
