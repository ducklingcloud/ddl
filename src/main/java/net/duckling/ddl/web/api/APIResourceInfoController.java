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

import net.duckling.common.util.JSONHelper;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/resourceInfo")
@RequirePermission(target="team", operation="view")
public class APIResourceInfoController {
    @Autowired
    private IResourceService resourceService;


    @RequestMapping
    public void getResourceInfo(@RequestParam("rid")Integer rid,HttpServletRequest request,HttpServletResponse resp){
        Resource r = resourceService.getResource(rid);
        JSONHelper.writeJSONObject(resp, LynxResourceUtils.getResourceJson(VWBSession.getCurrentUid(request), r));
    }

}
