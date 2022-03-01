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
package net.duckling.ddl.web.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.resource.TeamSpaceSize;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.sync.Context;
import net.duckling.ddl.service.sync.IJounalService;
import net.duckling.ddl.service.sync.Jounal;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.bean.Result;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequirePermission(target = "team", operation = "view")
public class FileJonualController {

    private static final Logger LOG = Logger.getLogger(FileJonualController.class);

    /*
     * 文件列表
     */
    @ResponseBody
    @RequestMapping(value = "/v1/sync/delta", method = RequestMethod.POST)
    public void delta(HttpServletRequest request, HttpServletResponse resp, @RequestParam("jid") Long jid) {
        Context ctx = ContextUtil.retrieveContext(request);
        List<Jounal> list = jounalService.list(ctx.getTid(), jid);
        TeamSpaceSize size = teamSpaceSizeService.getTeamSpaceSize(ctx.getTid());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("teamSize", size);
        result.put("jounals", list);
        JsonUtil.write(resp, new Result<Map<String, Object>>(result));
    }

    /*
     * 最近jid
     */
    @ResponseBody
    @RequestMapping(value = "/v1/sync/get_latest_jid", method = RequestMethod.POST)
    public void getLatestJid(HttpServletRequest request, HttpServletResponse resp) {
        Context ctx = ContextUtil.retrieveContext(request);
        Integer jid = jounalService.getLatestJid(ctx.getTid());
        JsonUtil.write(resp, new Result<Integer>(jid));
    }

    @Autowired
    private IJounalService jounalService;
    @Autowired
    TeamSpaceSizeService teamSpaceSizeService;
}
