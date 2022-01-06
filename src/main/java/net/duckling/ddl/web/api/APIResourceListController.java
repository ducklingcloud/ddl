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
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.grid.GridItem;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.AbstractRecommendContrller;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * 移动端数据获取
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/api/list")
@RequirePermission(target="team", operation="view")
public class APIResourceListController extends AbstractRecommendContrller{
    public final static int maxPageSize = 30;

    public final static String QUERY_TYPE = "queryType"; //查询类型
    public final static String QUERY_TYPE_MYCREATE="myCreate";
    public final static String QUERY_TYPE_MYSTARTFILES="myStarFiles";
    public final static String QUERY_TYPE_MYRECENTFILES="myRecentFiles";
    public final static String QUERY_TYPE_TEAMRECENTCHANGE="teamRecentChange";
    public final static String QUERY_TYPE_FILETYPE="fileType";
    public final static String QUERY_TYPE_TAG="tagQuery";

    /**
     * 返回记录格式
     */
    public final static String RECORD_FORMAT="recordFormat";
    public final static String RECORD_FORMAT_LITE="lite"; //简版

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private IGridService gridService;
    @RequestMapping
    public void list(HttpServletRequest request,HttpServletResponse response){
        String queryType =StringUtil.getValue(request.getParameter(QUERY_TYPE));
        String recordFormat = request.getParameter(RECORD_FORMAT);

        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String uid = context.getCurrentUID();
        int rid = getRequestRid(request);

        PaginationBean<Resource> resources = getResources(request,queryType, uid,rid);

        JSONObject j = null;
        if(RECORD_FORMAT_LITE.equals(recordFormat)){
            j = buildQueryResultLite(uid, rid, resources,queryType);
        }else{
            j = buildQueryResult(uid, rid, resources,queryType);
        }
        JsonUtil.writeJSONObject(response, j);
    }

    /**
     * 获取资源树结构
     * @param request
     * @param response
     */
    @RequestMapping(params="func=tree")
    public void tree(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = context.getTid();
        int rid = getRequestRid(request);
        Resource resource = resourceService.getTree(tid, rid);

        JsonUtil.writeJSONObject(response, JsonUtil.toJson(resource));
    }

    private  PaginationBean<Resource> getResources(HttpServletRequest request, String queryType, String uid, int rid){
        Site site =  findSite(request);
        int tid =site.getId();
        int begin = 0;
        String b = request.getParameter("begin");
        try{
            begin = Integer.parseInt(b);
        }catch(Exception e){}
        String order=request.getParameter("sortType");
        String keyWord=request.getParameter("keyWord");
        order=StringUtils.isEmpty(order)?"timeDesc":order;
        return getResourceListByParam(request,queryType, begin, tid, uid,rid, order,keyWord);
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildQueryResult(String uid, int rid,PaginationBean<Resource> resources,String queryType) {
        JSONObject j = buildQueryResultCommon(uid, rid, resources, queryType);
        j.put("children", JsonUtil.getJSONArrayFromListResource(resources.getData()));
        return j;
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildQueryResultLite(String uid, int rid,PaginationBean<Resource> resources,String queryType) {
        JSONObject j = buildQueryResultCommon(uid, rid, resources, queryType);
        j.put("children", JsonUtil.getJSONArrayLite(resources.getData()));
        return j;
    }

    @SuppressWarnings("unchecked")
    private JSONObject buildQueryResultCommon(String uid, int rid,PaginationBean<Resource> resources,String queryType) {
        if(resources==null){
            resources=new PaginationBean<Resource>();
        }
        JSONObject j = new JSONObject();
        j.put("total", resources.getTotal());
        if(rid>0){
            j.put("currentResource", LynxResourceUtils.getResourceJson(uid, resourceService.getResource(rid)));
            j.put("path", LynxResourceUtils.getResourceJSON(folderPathService.getResourcePath(rid), uid));
        }
        j.put("nextBeginNum", resources.getNextStartNum());
        j.put("loadedNum",resources.getLoadedNum());
        j.put("size", resources.getSize());
        if(StringUtils.equals(queryType, QUERY_TYPE_MYRECENTFILES)){
            j.put("showSort",false);
            j.put("showSearch",false);
        }else{
            j.put("showSort",true);
            j.put("showSearch",true);
        }
        return j;
    }

    private PaginationBean<Resource> getResourceListByParam(HttpServletRequest request, String queryType, int begin, int tid,
                                                            String uid, int rid, String order,String keyWord) {
        PaginationBean<Resource> resources;
        //      int maxSize = getMaxSize(request);
        switch(queryType){
            case QUERY_TYPE_MYRECENTFILES:{
                List<GridItem> gridItems = gridService.getTopKGridItem(uid, tid, 9);
                List<Resource> resourceList = new ArrayList<Resource>();
                for(GridItem item:gridItems){
                    Resource res = resourceService.getResource(item.getRid(), tid);
                    if(res!=null&&!LynxConstants.STATUS_DELETE.equals(res.getStatus())){
                        resourceList.add(res);
                    }else{
                        gridService.kickout(uid, tid, item.getRid(), item.getItemType());
                    }
                }
                resources=new PaginationBean<Resource>(resourceList);
                break;
            }
            case QUERY_TYPE_TEAMRECENTCHANGE:{
                //              resources=resourceService.getTeamRecentChange(tid, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_MYSTARTFILES:{
                //              resources=starmarkService.getMyStartFiles(tid, uid, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_MYCREATE:{
                //              resources=resourceService.getMyCreatedFiles(tid, uid, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_TAG:{
                //              String tagId = request.getParameter("tagFilter");
                //              if("all".equals(tagId)){
                //                  resources =folderPathService.getChildren(tid, rid, order,begin, maxSize, keyWord);
                //              }else{
                ResourceQuery rq = ResourceQuery.buildForQuery(request);
                resources = resourceService.query(rq);
                //              }
                break;
            }case QUERY_TYPE_FILETYPE:{
                 //                 String type = request.getParameter("queryFileType");
                 //                 resources = resourceService.getResourceByFileType(tid, type, begin, maxSize, order,"");
                 //             break;
             }
            default:{
                ResourceQuery rq = ResourceQuery.buildForQuery(request);
                resources = resourceService.query(rq);
                //              resources =folderPathService.getChildren(tid, rid, order,begin, maxSize, keyWord);
            }
        }
        return resources;
    }
    /**
     * 或取每页记录数
     * @param request
     * @return
     */
    //  private int getMaxSize(HttpServletRequest request) {
    //      String size = request.getParameter("maxPageSize");
    //      try{
    //          return Integer.parseInt(size);
    //      }catch(Exception e){
    //          return maxPageSize;
    //      }
    //  }
    private int getInteger(String s,int def){
        int result = def;
        try{
            result = Integer.parseInt(s);
        }catch(Exception e){}
        return result;
    }
    public Site findSite(HttpServletRequest request) {
        return VWBContext.findSite(request);
    }
    private int getRequestRid(HttpServletRequest request){
        String r = request.getParameter("rid");
        int rid = 0;
        if(StringUtils.isEmpty(r)){
            String path = request.getParameter("path");
            if(!StringUtils.isEmpty(path)){
                String[] s=path.split("/");
                try{
                    rid = Integer.parseInt(s[s.length-1]);
                }catch(Exception e){}
            }
        }else{
            rid = Integer.parseInt(r);
        }
        return rid;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params="func=getResourceInfo")
    public void getResourceInfo(@RequestParam("parentRid")int parentRid,@RequestParam("name")String name,HttpServletRequest request, HttpServletResponse response){
        int tid =VWBContext.getCurrentTid();
        List<Resource> folders= folderPathService.getResourceByName(tid, parentRid, LynxConstants.TYPE_FOLDER, name);
        List<Resource> files = folderPathService.getResourceByName(tid, parentRid, LynxConstants.TYPE_FILE, name);
        List<Resource> ddoc = folderPathService.getResourceByName(tid, parentRid, LynxConstants.TYPE_PAGE, name);
        folders.addAll(files);
        folders.addAll(ddoc);
        JSONObject object = new JSONObject();
        object.put("total", folders.size());
        JSONArray array = JsonUtil.getJSONArrayFromListResource(folders);
        object.put("children", array);
        JsonUtil.writeJSONObject(response, object);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params="func=getChildrenFolder")
    public void getFolder(HttpServletRequest request,HttpServletResponse response){
        Site site =  findSite(request);
        int tid =site.getId();
        int rid = getInteger(request.getParameter("rid"), 0);
        List<Resource> p = folderPathService.getChildrenFolder(tid, rid);
        JSONArray result = JsonUtil.getJSONArrayFromListResource(p);
        JSONObject o = new JSONObject();
        o.put("childrenFolder", result);
        o.put("total", p.size());
        JsonUtil.writeJSONObject(response, o);
    }
}
