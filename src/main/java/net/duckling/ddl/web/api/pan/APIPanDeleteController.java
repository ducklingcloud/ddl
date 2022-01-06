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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;


@Controller
@RequestMapping("/api/pan/pageDelete")
@RequirePermission(authenticated=true)
public class APIPanDeleteController {
    private static final Logger LOG = Logger.getLogger(APIPanDeleteController.class);
    @Autowired
    private IPanService service;
    /**
     * @param pid 准备删除的页面的ID
     * @param request
     * @param response
     * 返回信息有三个状态，1为删除成功，0为删除失败，-1为页面有人在编辑等操作被锁定，不能删除
     */
    @WebLog(method = "apiPanDelete", params = "rid,itemId,itemType")
    @RequestMapping
    public void delete(HttpServletRequest request, HttpServletResponse response){
        JSONObject obj = new JSONObject();
        int delete = 1;
        String rid = decode(request.getParameter("rid"));
        boolean result = false;
        PanAcl acl = PanAclUtil.getInstance(request);
        try {
            result = service.rm(acl, rid);
        } catch (MeePoException e) {
            LOG.error("", e);
        }
        if(result){
            LOG.info(acl.getUid()+" delete "+rid);
        }
        if(result){
            delete = 1;
        }
        obj.put("succ", delete);
        JsonUtil.writeJSONObject(response, obj);
    }


    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
