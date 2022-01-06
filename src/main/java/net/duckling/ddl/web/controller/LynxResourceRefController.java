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

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 文档关联
 * @date 2013-10-12
 * @author Shanbo Li
 */

@Controller
@RequestMapping("/{teamCode}/rr/{rid}")
@RequirePermission(target = "team", operation = "view")
public class LynxResourceRefController extends BaseController {

    @Autowired
    private FileVersionService fileVersionService;

    private static final Logger LOG = Logger.getLogger(LynxResourceRefController.class);


    @SuppressWarnings("unchecked")
    @RequestMapping(params = "func=deleteFileRef")
    @RequirePermission(target = "team", operation = "edit")
    public void deleteFileRef(HttpServletRequest request, HttpServletResponse response, @PathVariable("rid") int rid) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        fileVersionService.deleteFileAndPageRefer(rid, pid, VWBContext.getCurrentTid());
        JSONObject object = new JSONObject();
        object.put("docid", rid);
        JsonUtil.writeJSONObject(response, object);
    }

}
