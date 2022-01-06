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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.TeamQuery;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cnic.cerc.dlog.client.WebLog;


/**
 * API调用的Controller（查询集合页面）
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/tagItems")
@RequirePermission(target="team", operation="view")
public class APITagItemsController extends APIBaseResourceController {

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private FolderPathService folderPathService;

    @SuppressWarnings("unchecked")
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response){
        JSONObject object= new JSONObject();
        Site site =  findSite(request);

        TeamQuery teamQuery = TeamQuery.buildForQuery(request);
        teamQuery.setTid(new int[]{site.getId()});
        int size = teamQuery.getSize();
        List<Resource> elementList = resourceService.getResource(teamQuery);
        Set<Integer> bundleId = new HashSet<Integer>();
        List<Resource> resultList = filterDocInBundle(elementList,site,bundleId);
        while((resultList == null || resultList.size() < size)
              && elementList != null && elementList.size() > 0) {// 查询结果小于size，循环查询
            //          teamQuery.setSize(teamQuery.getSize() + size);
            int offset = elementList.size();
            teamQuery.setOffset(teamQuery.getOffset() + offset);
            elementList = resourceService.getResource(teamQuery);
            List<Resource> tempList = filterDocInBundle(elementList,site,bundleId);
            resultList.addAll(tempList);
        }

        JSONArray array = JsonUtil.getJSONArrayFromResourceList(resultList);
        object.put("records", array);
        int offset = teamQuery.getOffset();
        int count = elementList.size();
        offset += count;
        object.put("offset", offset);
        JsonUtil.writeJSONObject(response, object);
    }

    private List<Resource> filterDocInBundle(List<Resource> elementList,Site site,Set<Integer> bundleIds) {
        List<Resource> result = new ArrayList<Resource>();
        if(null == elementList || elementList.size() <= 0){
            return result;
        }
        for(Resource res : elementList){
            int bid = res.getBid();
            if(bid == 0 ) {// 不在bundle里边
                if(res.isFolder()){
                    bundleIds.add(res.getRid());
                }
                result.add(res);
            }else{
                List<Resource> r = folderPathService.getResourcePath(bid);
                if(r!=null&&r.size()>0){
                    if(!bundleIds.contains(r.get(0).getRid())){
                        bundleIds.add(r.get(0).getRid());
                        result.add(r.get(0));
                    }
                }

            }
        }
        return result;
    }

    //1.0版api接口记录保留
    /*@SuppressWarnings("unchecked")
      @RequestMapping
      public void service(@RequestParam("cid") int cid, HttpServletRequest request, HttpServletResponse response){
      JSONObject object= new JSONObject();
      Site s =  VWBSite.findSite(request);
      ITagService ts = VWBSite.findSite(request).getLynxTagService();
      List<TagItem> tagItemList = ts.getTagItems(cid);
      object.put("totalCount", tagItemList.size());
      List<Long> rids = new ArrayList<Long>();
      for(TagItem item:tagItemList){
      rids.add(new Long(item.getRid()));
      }
      List<Resource> elementList = s.getLynxResourceService().getResourcesBySphinxID(rids);
      JSONArray array = new JSONArray();
      if (elementList!=null){
      for (Resource element:elementList){
      if(element.isPage()){
      JSONObject jsonEle = new JSONObject();
      jsonEle.put("id", element.getItemId());
      jsonEle.put("title", element.getTitle());
      jsonEle.put("lastUpdate", AoneTimeUtils.formatToDateTime(element.getLastEditTime()));
      jsonEle.put("lastUpdateBy", element.getLastEditor());
      jsonEle.put("version", element.getLastVersion());
      jsonEle.put("author", element.getCreator());
      array.add(jsonEle);
      }
      }
      }
      object.put("records", array);
      JSONHelper.writeJSONObject(response, object);
      }*/
}
