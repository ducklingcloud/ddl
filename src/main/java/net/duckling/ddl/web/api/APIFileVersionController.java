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

import net.duckling.ddl.common.Site;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.util.JsonUtil;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/queryfileversion")
public class APIFileVersionController extends APIBaseController{

    @Autowired
    private FileVersionService fileVersionService;

    @RequestMapping
    public void queryFileVersion(HttpServletRequest request,HttpServletResponse response){
        Site site = findSite(request);
        int tid = site.getId();
        int fid = Integer.valueOf(request.getParameter("fid"));
        FileVersion v = fileVersionService.getLatestFileVersion(fid, tid);
        JSONObject jsonObj = new JSONObject();
        if(v==null){
            jsonObj.put("result", false);
        }else{
            jsonObj.put("result", true);
            jsonObj.put("latestVesion", v.getVersion());
            jsonObj.put("title", v.getTitle());
            jsonObj.put("rid", v.getRid());
        }
        JsonUtil.writeJSONObject(response, jsonObj);
    }
}
