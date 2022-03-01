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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.IStarmarkService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("/{teamCode}/starmark")
@RequirePermission(target = "team", operation = "view")
public class LynxStarmarkController extends BaseController {


    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IStarmarkService starmarkService;
    @Autowired
    private URLGenerator generator;
    @RequestMapping
    public ModelAndView init(HttpServletRequest request) {
        String url = generator.getURL(VWBContext.getCurrentTid(), UrlPatterns.T_LIST, null, null)+"#queryType=myStarFiles";
        return new ModelAndView(new RedirectView(url));

        //        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_STARTMARK);
        //        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/tag/starmark.jsp");
        //        int tid = VWBContext.getCurrentTid();
        //        String uid = context.getCurrentUID();
        //        mv.addObject("tags", tagService.getTagsNotInGroupForTeam(tid));
        //        mv.addObject("tagGroups", tagService.getTagGroupsForTeam(tid));
        //        List<Resource> reslist = resourceService.getStarmarkResource(uid, tid);
        //        // 合并属于Bundle的Resource
        //        Map<Integer, List<Resource>> bundleItemList = new HashMap<Integer, List<Resource>>();
        //        List<Resource> filterResList = filterResult(context.getSite(), reslist, bundleItemList);
        //        mv.addObject("resourceList", filterResList);
        //        mv.addObject("bundleItemMap", bundleItemList);
        //        mv.addObject(LynxConstants.PAGE_TITLE, "星标文档");
        //        return mv;
    }

    @RequestMapping(params = "func=add")
    @WebLog(method = "addStarmark", params = "rid")
    public void addStarMark(HttpServletRequest request, HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_STARTMARK);
        int tid = context.getSite().getId();
        int rid = Integer.parseInt(request.getParameter("rid"));
        String uid = context.getCurrentUID();
        starmarkService.addStarmark(uid, rid, tid);
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("rid", rid);
        JsonUtil.write(response, json);
    }

    @RequestMapping(params = "func=remove")
    @WebLog(method = "removeStarmark", params = "rid")
    public void remove(HttpServletRequest request, HttpServletResponse response) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_STARTMARK);
        int rid = Integer.parseInt(request.getParameter("rid"));
        String uid = context.getCurrentUID();
        starmarkService.removeStarmark(uid, rid, context.getSite().getId());
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        json.addProperty("rid", rid);
        JsonUtil.write(response, json);
    }

    /**
     * 将属于Bundle的资源替换成对应的bundle并去重, 将Bundle中的Resource缓存在biMap中用于前台显示
     *
     * @param site
     *            Site对象，访问服务时用到
     * @param resList
     *            待过滤的Resource集合
     * @param biMap
     *            缓存Bundle与Bundle内资源的Map对象
     * @return Resource集合，包含Bundle本身以及不属于任何Bundle的Resource对象
     */
    private List<Resource> filterResult(Site site, List<Resource> resList, Map<Integer, List<Resource>> biMap) {
        List<Resource> result = new ArrayList<Resource>();
        Set<Integer> bids = new HashSet<Integer>();
        if (null == resList || resList.size() <= 0) {
            return null;
        }
        for (Resource res : resList) {
            int bid = res.getBid();
            if (bid == 0) {
                if (!res.isBundle()) {
                    result.add(res);
                } else if (!bids.contains(res.getRid())) {
                    result.add(res);
                    bids.add(res.getRid());
                }
            } else {
                if (bids.contains(bid)) {
                    List<Resource> temp = biMap.get(bid);
                    temp = (null != temp) ? temp : (new ArrayList<Resource>());
                    temp.add(res);
                    biMap.put(bid, temp);
                } else {
                    result.add(resourceService.getResource(res.getBid(), res.getTid()));
                    List<Resource> temp = new ArrayList<Resource>();
                    temp.add(res);
                    biMap.put(bid, temp);
                    bids.add(bid);
                }
            }
        }
        return result;
    }


}
