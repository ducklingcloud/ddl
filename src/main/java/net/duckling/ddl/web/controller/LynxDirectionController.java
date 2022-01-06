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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.grid.GridItem;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserSortPreference;
import net.duckling.ddl.service.user.impl.UserSortPreferenceService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;


/**
 * team下目录的展现，关系管理
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/{teamCode}/list")
@RequirePermission(target = "team", operation = "view")
public class LynxDirectionController extends BaseController{
    private static final Logger LOG = Logger.getLogger(LynxDirectionController.class);
    /**
     * 分页数
     */
    public final static int maxPageSize = 30;
    public final static int defaultDivPageSize=10;
    /**
     * 我创建的
     */
    public final static String QUERY_TYPE_MYCREATE="myCreate";
    /**
     * 我的星标文件
     */
    public final static String QUERY_TYPE_MYSTARTFILES="myStarFiles";
    public final static String QUERY_TYPE_MYRECENTFILES="myRecentFiles";
    public final static String QUERY_TYPE_TEAMRECENTCHANGE="teamRecentChange";
    public final static String QUERY_TYPE_TAG="tagQuery";
    public final static String QUERY_TYPE_FILETYPE = "showFileByType";
    public final static String QUERY_TYPE_EXCEPTFOLDER="ExceptFolder";
    /**
     * 图片类型
     */
    public final static String QUERY_TYPE_PICTURE="Picture";
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private UserSortPreferenceService userSortPreferenceService;


    @RequestMapping
    public ModelAndView display(HttpServletRequest request){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        ModelAndView m = layout(ELayout.LYNX_MAIN, context,"/jsp/featureTest.jsp");
        m.addObject("teamUrl", urlGenerator.getURL(tid,UrlPatterns.T_LIST, null,null));
        m.addObject("teamHome",urlGenerator.getURL(tid,UrlPatterns.T_TEAM_HOME, null,null));
        m.addObject("pageType", "list");
        m.addObject("baseUrl", urlGenerator.getBaseUrl());
        m.addObject("tags",tagService.getTagsNotInGroupForTeam(tid));
        m.addObject("tagGroups", tagService.getTagGroupsForTeam(tid));
        m.addObject("uid", context.getCurrentUID());
        m.addObject("sortType", userSortPreferenceService.getUidSortPreference(context.getCurrentUID(), null, UserSortPreference.WEB_TYPE));
        addMyTeam(request, m);
        return m;
    }
    @WebLog(method = "queryFileItem", params = "queryType,keyWord,sortType,type")
    @RequestMapping(params="func=query")
    public void queryFileItem(HttpServletRequest request,HttpServletResponse response){
        String queryType =request.getParameter("queryType");
        queryType=StringUtils.isEmpty(queryType)?"":queryType;
        JSONObject j;
        int tid = VWBContext.getCurrentTid();
        String path = request.getParameter("needPath");
        String uid = VWBSession.getCurrentUid(request);
        int rid = getRequestRid(request);
        PaginationBean<Resource> resources ;
        String order = getOrder(request,queryType);
        resources = getResourceListByParam( request,queryType, tid, uid,order);
        j = buildQueryResultJson(path, uid, rid, resources,queryType);
        String tokenKey=request.getParameter("tokenKey");
        j.put("tokenKey", tokenKey);
        j.put("order", order);
        JsonUtil.writeJSONObject(response, j);
    }

    private String getOrder(HttpServletRequest request,String queryType){
        String order=request.getParameter("sortType");
        if(QUERY_TYPE_TEAMRECENTCHANGE.equals(queryType)){
            return StringUtils.isEmpty(order)?"timeDesc":order;
        }
        String uid = VWBSession.getCurrentUid(request);
        order = userSortPreferenceService.getUidSortPreference(uid, order, UserSortPreference.WEB_TYPE);
        return StringUtils.isEmpty(order)?"timeDesc":order;
    }

    private PaginationBean<Resource> getResourceListByParam(HttpServletRequest request, String queryType, int tid,
                                                            String uid,String order) {
        PaginationBean<Resource> resources;
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
            case QUERY_TYPE_MYCREATE:{
                //              resources=resourceService.getMyCreatedFiles(tid, uid, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_MYSTARTFILES:{
                //              resources=starmarkService.getMyStartFiles(tid, uid, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_TEAMRECENTCHANGE:{
                //              resources=resourceService.getTeamRecentChange(tid, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_TAG:{
                //              String tagIdsStr=StringUtils.defaultIfBlank(request.getParameter("tagId"), "0");
                //              String[] tagIdsArray=tagIdsStr.split("_");
                //              List<Integer> tagIds=new ArrayList<Integer>();
                //              if(tagIdsArray!=null&&tagIdsArray.length>0){
                //                  for(String tagIdTemp:tagIdsArray){
                //                      tagIds.add(Integer.parseInt(tagIdTemp));
                //                  }
                //              }
                //              resources = tagService.getTeamTagFiles(tid, tagIds, begin, maxSize, order, keyWord);
                //              break;
            }
            case QUERY_TYPE_EXCEPTFOLDER:{
                //              resources =folderPathService.getDescendants(tid, rid, order,begin, maxSize, keyWord);
                //              break;
            }
            case QUERY_TYPE_FILETYPE:{
                //              resources = resourceService.getResourceByFileType(tid, type, begin, maxSize, order,keyWord);
                //              break;
            }
            default:{
                //              if(QUERY_TYPE_PICTURE.equals(type)){
                //                  resources =folderPathService.getChildren(tid, rid, type, order,begin, maxSize, keyWord);
                //              }else{
                //                  resources =folderPathService.getChildren(tid, rid, order,begin, maxSize, keyWord);
                //              }
                ResourceQuery rq = ResourceQuery.buildForQuery(request,order);
                resources = resourceService.query(rq);
            }
        }
        return resources;
    }

    private JSONObject buildQueryResultJson(String path, String uid, int rid,PaginationBean<Resource> resources,String queryType) {
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
        j.put("children", LynxResourceUtils.getResourceJSON(resources.getData(), uid));
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


    private List<Resource> sphinxQuery(HttpServletRequest request,HttpServletResponse response){

        return null;
    }

    @RequestMapping(params="func=createFolder")
    @WebLog(method = "createFolder")
    public void createFolder(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String uid = context.getCurrentUID();
        String parentRidS = request.getParameter("parentRid");

        int tid = VWBContext.getCurrentTid();
        String tidStr = request.getParameter("tid");
        if(StringUtils.isNumeric(tidStr)){
            tid = Integer.valueOf(tidStr);
        }

        JSONObject j = new JSONObject();
        if(!authorityService.haveTeamEditeAuth(tid,uid)){
            j.put("result", false);
            j.put("message", "您没有权限创建文件夹");
            JsonUtil.writeJSONObject(response, j);
            return;
        }
        int parentRid = 0;
        try{
            parentRid = Integer.parseInt(parentRidS);
        }catch(Exception e){}
        if(StringUtil.illCharCheck(request, response, "fileName")){
            return;
        }
        String name = request.getParameter("fileName");
        Resource r = new Resource();
        r.setTid(tid);
        r.setBid(parentRid);
        r.setCreateTime(new Date());
        r.setCreator(uid);
        r.setItemType(LynxConstants.TYPE_FOLDER);
        r.setTitle(name);
        r.setLastEditor(uid);
        r.setLastEditorName(aoneUserService.getUserNameByID(uid));
        r.setLastEditTime(new Date());
        r.setStatus(LynxConstants.STATUS_AVAILABLE);
        resourceOperateService.createFolder(r);

        j.put("resource", LynxResourceUtils.getResourceJson(uid,r));
        j.put("result", true);
        JsonUtil.writeJSONObject(response, j);
    }
    @RequestMapping(params="func=editFileName")
    @WebLog(method = "editFileName")
    public void editeFileName(HttpServletRequest request,HttpServletResponse response){
        int rid = getInteger(request.getParameter("rid"), 0);
        JSONObject o = new JSONObject();
        if(!authorityService.haveTeamEditeAuth(VWBContext.getCurrentTid(),VWBSession.getCurrentUid(request))){
            o.put("result", false);
            o.put("message", "您没有权限修改文件名");
            JsonUtil.writeJSONObject(response, o);
            return;
        }

        if(rid ==0){
            o.put("result", false);
        }else{
            VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
            if(StringUtil.illCharCheck(request, response, "fileName")){
                return;
            }
            String fileName = request.getParameter("fileName");
            if(StringUtils.isEmpty(fileName)){
                o.put("result", false);
                o.put("message", "文件名不能为空");
                JsonUtil.writeJSONObject(response, o);
                return;
            }
            fileName = fileName.trim();
            Resource oldResource=resourceService.getResource(rid);
            int parentRid=oldResource.getBid();

            String message="";
            boolean result=true;
            if(!resourceOperateService.canUseFileName(VWBContext.getCurrentTid(), parentRid, rid,oldResource.getItemType(), fileName)){
                message="当前文件夹下存在重名文件！";
                result=false;
            }else{
                boolean r = resourceOperateService.renameResource(VWBContext.getCurrentTid(),rid,context.getCurrentUID(),fileName);
                if(!r){
                    result=false;
                    message="重命名失败";
                }
            }
            o.put("result", result);
            if(!result){
                o.put("message", message);
            }

            Resource resource = resourceService.getResource(rid);
            o.put("resource", LynxResourceUtils.getResourceJson(context.getCurrentUID(),resource));
        }
        JsonUtil.writeJSONObject(response, o);
    }
    private int getInteger(String s,int def){
        int result = def;
        try{
            result = Integer.parseInt(s);
        }catch(Exception e){}
        return result;
    }

    @RequestMapping(params="func=getPath")
    public void getResourcePath(HttpServletRequest request,HttpServletResponse response){
        int rid = 0;
        try{
            rid = Integer.parseInt(request.getParameter("rid"));
        }catch(Exception e){}
        List<Resource> rs = folderPathService.getResourcePath(rid);
        JSONObject j = new JSONObject();
        StringBuilder sb = new StringBuilder();
        for(Resource r : rs){
            sb.append("/"+r.getRid());
        }
        j.put("ridPath", sb.toString());
        JsonUtil.writeJSONObject(response, j);
    }
    @RequestMapping(params="func=deleteResource")
    @WebLog(method = "deleteResource",params="rid")
    public void deleteResource(HttpServletRequest request,HttpServletResponse response){
        int rid = getInteger(request.getParameter("rid"), 0);
        JSONObject o = new JSONObject();
        if(rid==0){
            o.put("result", false);
            o.put("message", "文档不存在！");
        }else{
            VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
            boolean b = resourceOperateService.deleteAuthValidate(VWBContext.getCurrentTid(), rid, context.getCurrentUID());
            if(!b){
                o.put("result", false);
                o.put("message", "没有权限删除该文件！");
            }else{
                Resource r =resourceService.getResource(rid);
                String uid = VWBSession.getCurrentUid(request);
                resourceOperateService.deleteResource(VWBContext.getCurrentTid(), rid,uid);
                o.put("result", true);
            }
        }
        JsonUtil.writeJSONObject(response, o);
    }

    @RequestMapping(params="func=deleteResources")
    @WebLog(method = "deleteResources",params="rids[]")
    public void deleteResources(HttpServletRequest request,HttpServletResponse response){
        String[] ridStrs = request.getParameterValues("rids[]");
        List<Integer> rids = new ArrayList<Integer>();
        JSONObject o = new JSONObject();
        if(ridStrs==null){
            o.put("result", false);
            o.put("message", "文档不存在！");
        }else{
            for(String r: ridStrs){
                try{
                    rids.add(Integer.parseInt(r));
                }catch(Exception e){}
            }
            if(rids.size()>0){
                VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
                boolean b = false;
                for(Integer rid : rids){
                    b = resourceOperateService.deleteAuthValidate(VWBContext.getCurrentTid(), rid, context.getCurrentUID());
                    if(!b){
                        break;
                    }
                }
                if(!b){
                    o.put("result", false);
                    o.put("message", "没有权限删除该文件！");
                }else{
                    String uid = VWBSession.getCurrentUid(request);
                    resourceOperateService.deleteResource(VWBContext.getCurrentTid(), rids,uid);
                    o.put("result", true);
                    o.put("message", "没有权限删除该文件！");
                }
            }
        }
        JsonUtil.writeJSONObject(response, o);
    }

    private ModelAndView addMyTeam(HttpServletRequest request, ModelAndView mv){
        VWBContext context = getVWBContext(request);
        int myTeamId = teamService.getPersonalTeamNoCreate(context.getCurrentUID());
        String myTeamCode = teamService.getTeamNameFromEmail(context.getCurrentUID());
        mv.addObject("myTeamId",myTeamId);
        mv.addObject("myTeamCode",myTeamCode);
        return mv;
    }

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request, UrlPatterns.T_LIST);
    }
}
