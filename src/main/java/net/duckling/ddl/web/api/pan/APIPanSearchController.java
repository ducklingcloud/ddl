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
package net.duckling.ddl.web.api.pan;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.controller.pan.MeePoMetaToPanBeanUtil;
import net.duckling.ddl.web.controller.pan.PanResourceBean;
import net.duckling.meepo.api.IPanService;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.PanQueryResult;

@Controller
@RequestMapping("/api/pan/search")
public class APIPanSearchController {
    @Autowired
    private IPanService service;
    @Autowired
    private AoneUserService aoneUserService;
    @RequestMapping
    public void service(@RequestParam("keyword") String keyword,
                        HttpServletRequest request, HttpServletResponse response){

        try {
            PanQueryResult[] result = service.search(PanAclUtil.getInstance(request), "/", keyword, 100);
            JSONObject obj = new JSONObject();
            SimpleUser user = aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request));
            List<PanResourceBean> pbs = adapterMeta(result,user);
            obj.put("fileResult",LynxResourceUtils.getPanResourceList(pbs,user.getUid()) );
            JsonUtil.writeJSONObject(response, obj);
        } catch (MeePoException e) {
            e.printStackTrace();
        }

    }


    private List<PanResourceBean> adapterMeta(PanQueryResult[] result, SimpleUser user) {
        List<PanResourceBean> rs = new ArrayList<PanResourceBean>();
        if(result!=null){
            for(PanQueryResult r : result){
                rs.add(MeePoMetaToPanBeanUtil.transferSearchResoult(r,user));
            }
        }
        return rs;
    }
}
