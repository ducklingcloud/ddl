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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.navbar.INavbarService;
import net.duckling.ddl.service.navbar.NavbarItem;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("/{teamCode}/navbar")
public class NavbarController {

    @Autowired
    private INavbarService navbarService;

    @RequestMapping
    public void getNavbar(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String uid = context.getCurrentUID();
        int tid = VWBContext.getCurrentTid();
        List<NavbarItem> items = navbarService.getNavbarItems(uid, tid);
        JsonUtil.write(response, items);
    }

    @RequestMapping(params="func=addItem")
    @WebLog(method="addNavbarItem",params="title,url")
    public void addNavbarItem(HttpServletRequest request, HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String uid = context.getCurrentUID();
        int tid = VWBContext.getCurrentTid();
        String title = request.getParameter("title");
        String url = request.getParameter("url");
        if(null == title || null == url || "".equals(title) || "".equals(url)){
            JsonObject obj = new JsonObject();
            obj.addProperty("status", false);
            JsonUtil.write(response, obj);
            return ;
        }
        NavbarItem item = NavbarItem.build(uid, tid, title, url);
        int navBarItemId = navbarService.create(item);
        JsonObject obj = new JsonObject();
        obj.addProperty("id", navBarItemId);
        obj.addProperty("url", item.getUrl());
        obj.addProperty("title", item.getTitle());
        JsonUtil.write(response, obj);
    }

    @SuppressWarnings("unused")
    @RequestMapping(params="func=delete")
    @WebLog(method="deleteNavbarItem",params="id")
    public void deleteNavbar(HttpServletRequest request, HttpServletResponse response,@RequestParam("id") int id){
        navbarService.delete(id);
        JsonUtil.write(response, new JsonObject());
    }

}
