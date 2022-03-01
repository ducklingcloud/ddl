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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.grid.GridItem;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * API调用的Controller（查询集合页面）
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/myCommons")
@RequirePermission(target="team", operation="view")
public class APIMyCommonsController extends APIBaseResourceController {

    private static final int QUERY_SIZE = 9;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IGridService gridService;

    @SuppressWarnings("unchecked")
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response){
        JsonObject object= new JsonObject();
        Site site =  findSite(request);
        String uid = findUser(request);
        int tid = site.getId();
        List<GridItem> gridItems = gridService.getTopKGridItem(uid, tid, QUERY_SIZE);

        List<Resource> resourceList = new ArrayList<Resource>();
        for(GridItem item:gridItems){
            Resource res = resourceService.getResource(item.getRid(), tid);
            if(res!=null&&!LynxConstants.STATUS_DELETE.equals(res.getStatus())){
                resourceList.add(res);
            }else{
                gridService.kickout(uid, tid, item.getRid(), item.getItemType());
            }
        }

        JsonArray array = JsonUtil.getJSONArrayFromList(resourceList);
        object.add("records", array);
        String api = request.getParameter("api");
        object.addProperty("api", api);
        JsonUtil.write(response, object);
    }

}
