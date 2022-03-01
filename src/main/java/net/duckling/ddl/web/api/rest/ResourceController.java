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
package net.duckling.ddl.web.api.rest;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PathName;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.ResourceQuery.ResourcePathQuery;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.ddl.web.vo.ErrorMsg;
import net.duckling.ddl.web.vo.ResourceView;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequirePermission(target="team", operation="view")
@RequestMapping("/api/v1")
public class ResourceController extends AbstractController {
    private static final String INCLUDE_FILE = "file";
    private static final String ORDER_BY_TITLE = "title";

    @RequestMapping(value="/resources",method = RequestMethod.GET)
    @ResponseBody
    public void list(
        @RequestParam(value="path", required=false) String path,
        @RequestParam(value="begin", required=false) Integer begin,
        @RequestParam(value="limit", required=false) Integer limit,
        @RequestParam(value="includePage", required=false) Boolean includePage,
        HttpServletRequest request,
        HttpServletResponse response) {
        begin = begin==null||begin<0 ? 0 : begin;
        limit = limit==null||limit<=0 ? 10 : limit;
        path = StringUtils.defaultIfBlank(path, PathName.DELIMITER);
        int tid = getCurrentTid();
        //是否包含DPage类型文件，默认true不包括
        includePage = includePage == null ? false:includePage;

        Resource res = folderPathService.getResourceByPath(tid, path);
        if(res == null){
            writeError(ErrorMsg.NOT_FOUND, response);
            return;
        }

        ResourceQuery rq = new ResourcePathQuery(res.getRid());
        rq.setTid(getCurrentTid());
        rq.setOffset(begin);
        rq.setSize(limit);
        if(!includePage){
            rq.setFileType(LynxConstants.SRERCH_TYPE_NOPAGE);
        }
        PaginationBean<Resource> resources = resourceService.query(rq);
        //设置path属性
        folderPathService.setResourceListPath(
            resources.getData(), folderPathService.getPathString(res.getRid()));

        /* TODO: skips jackson mixin; to be checked
         * ResourceView.class is empty? */
        // JsonUtil.write(response, resources, Resource.class, ResourceView.class);
        JsonUtil.write(response, resources);
    }

    @RequestMapping(value="/resource/search",method = RequestMethod.GET)
    @ResponseBody
    public void search(
        @RequestParam(value="begin", required=false) Integer begin,
        @RequestParam(value="limit", required=false) Integer limit,
        @RequestParam(value="includes", required=false) String includes,
        @RequestParam(value="q", required=false) String q,
        @RequestParam(value="orderBy", required=false) String orderBy,
        @RequestParam(value="order", required=false) String order,
        HttpServletRequest request,
        HttpServletResponse response) {
        begin = begin==null||begin<0 ? 0 : begin;
        limit = limit==null||limit<=0 ? 10 : limit;
        int tid = getCurrentTid();
        String type = "";
        int rootRid = 0;
        if(INCLUDE_FILE.equals(includes)){
            type = LynxConstants.TYPE_FILE;
        }

        String orderStr = "";
        if(ORDER_BY_TITLE.equals(orderBy)){
            orderStr = LynxConstants.ASC.equals(order) ? "title" : "titleDesc";
        }else{
            orderStr = LynxConstants.ASC.equals(order) ? "time" : "timeDesc";
        }

        PaginationBean<Resource> resources = folderPathService
                .getChildren(tid, rootRid, type, orderStr, begin, limit, q);
        //设置path属性
        folderPathService.setResourceListPath(resources.getData());

        /* to be checked */
        // JsonUtil.write(response, resources, Resource.class, ResourceView.class);
        JsonUtil.write(response, resources);
    }

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private FolderPathService folderPathService;
}
