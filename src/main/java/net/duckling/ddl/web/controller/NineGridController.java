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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.mail.notice.AbstractNoticeHelper;
import net.duckling.ddl.service.mail.notice.DailyNotice;
import net.duckling.ddl.service.mail.notice.DailyNoticeHelper;
import net.duckling.ddl.service.mail.notice.GroupNotice;
import net.duckling.ddl.service.resource.DShortcut;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceDirectoryTrash;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.ShortcutService;
import net.duckling.ddl.service.resource.impl.ResourceDirectoryTrashService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.WebParamUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

//import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("/{teamCode}")
@RequirePermission(target = "team", operation = "view")
public class NineGridController extends BaseController {

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private ShortcutService shotcutService;
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private ResourceDirectoryTrashService resourceDirectoryTrashService;

    @RequestMapping
    public ModelAndView home(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(new RedirectView(urlGenerator.getURL(VWBContext.getCurrentTid(), UrlPatterns.T_LIST, "", null)));
        return mv;
        /*VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
          ModelAndView mv = BaseController.layout(ELayout.LYNX_MAIN, context,"/jsp/aone/tag/grids.jsp");
          int tid = VWBContext.getCurrentTid();
          String uid = context.getCurrentUID();
          mv.addObject("tags",tagService.getTagsNotInGroupForTeam(tid));
          mv.addObject("tagGroups", tagService.getTagGroupsForTeam(tid));
          TeamQuery query = new TeamQuery();
          query.setTid(new int[]{tid});
          query.setOffset(0);
          query.setSize(QUERY_SIZE);
          List<GridItem> gridItems = gridService.getTopKGridItem(uid, tid, QUERY_SIZE);
          List<Resource> resourceList = new ArrayList<Resource>();
          for(GridItem item:gridItems){
          Resource res = resourceService.getResource(item.getItemId(), tid, item.getItemType());
          if(res!=null&&!LynxConstants.STATUS_DELETE.equals(res.getStatus())){
          resourceList.add(res);
          }else{
          gridService.kickout(uid, tid, item.getItemId(), item.getItemType());
          }
          }
          NoticeParam queryParam = NoticeParam.getHistoryQueryParam(tid, uid);
          List<Notice> teamNoticeList = noticeService.readNotification(queryParam,uid);
          mv.addObject("historyNoticeList",getDailyNoticeArray(teamNoticeList));
          mv.addObject("reslist", resourceList);
          mv.addObject("gridItems",gridItems);
          mv.addObject("resources", getAllShortCut(context.getSite(), tid));
          return mv;*/

    }

    private List<ShortcutDisplay> getAllShortCut(Site site ,int tid){
        List<DShortcut> shortcuts = shotcutService.getCollectionShortcut(tid, null);
        List<ShortcutDisplay> result = new ArrayList<ShortcutDisplay>();
        for(DShortcut ds : shortcuts){
            Resource r = resourceService.getResource(ds.getRid());
            ShortcutDisplay vo = new ShortcutDisplay(ds);
            vo.setResource(r);
            vo.setResourceURL(getResourceURL(site,r));
            result.add(vo);
        }
        return result;
    }

    private String getResourceURL(Site site, Resource resource){
        String url = null;
        String pagename = String.valueOf(resource.getRid());
        if(resource.isPage()){
            url = site.getURL(UrlPatterns.T_VIEW_R, pagename, null);
        }else if(resource.isFile()){
            url = site.getURL(UrlPatterns.T_VIEW_R, pagename, null);
        }else{
            url = site.getURL(UrlPatterns.T_VIEW_R, pagename, null);
        }
        return url;
    }
    protected DailyNotice[] getDailyNoticeArray(List<Notice> source){
        AbstractNoticeHelper dailyGrouper = new DailyNoticeHelper();
        GroupNotice[] dailyGroup = dailyGrouper.getCNoticeArray(source);
        return convertToDailyNoticeArray(dailyGroup);
    }

    protected DailyNotice[] convertToDailyNoticeArray(GroupNotice[] dailyGroup) {
        DailyNotice[] results = new DailyNotice[dailyGroup.length];
        for(int i=0;i<dailyGroup.length;i++){
            results[i] = (DailyNotice)dailyGroup[i];
        }
        return results;
    }

    @RequestMapping(params="func=pin")
    //@WebLog(method="pin",params="rid,itemType,level")
    public void pin(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        int rid = WebParamUtil.getIntegerValue(request, "rid");
        String itemType = request.getParameter("itemType");
        int level = WebParamUtil.getIntegerValue(request, "level");
        gridService.pin(uid, tid, rid, itemType, level);
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        JsonUtil.write(response, json);
    }

    @RequestMapping(params="func=unpin")
    //@WebLog(method="unpin",params="rid,itemType,level")
    public void unpin(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        int rid = WebParamUtil.getIntegerValue(request, "rid");
        String itemType = request.getParameter("itemType");
        int level = WebParamUtil.getIntegerValue(request, "level");
        gridService.unpin(uid, tid, rid, itemType, level);
        JsonObject json = new JsonObject();
        json.addProperty("status", "success");
        JsonUtil.write(response, json);
    }

    @RequestMapping(params="func=kickout")
    //@WebLog(method="kickout",params="rid,itemType")
    public void kickout(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        int rid = WebParamUtil.getIntegerValue(request, "rid");
        String itemType = request.getParameter("itemType");
        boolean flag = gridService.kickout(uid, tid, rid, itemType);
        JsonObject json = new JsonObject();
        json.addProperty("status", flag);
        JsonUtil.write(response, json);
    }

    @RequestMapping(params="func=validateParent")
    public void validateParent(HttpServletRequest request,HttpServletResponse response){
        int rid = Integer.parseInt(request.getParameter("rid"));
        JsonObject obj = new JsonObject();
        Resource r = resourceService.getResource(rid);
        obj.addProperty("status", true);
        obj.addProperty("parentRid", r.getBid());
        if(r.isFolder()){
            ResourceDirectoryTrash t = resourceDirectoryTrashService.getResoourceTrash(rid);
            if(t==null){
                obj.addProperty("status", false);
                obj.addProperty("message", "文件目录信息已经不存在");
            }else{
                if(validateParentStaus(r)){
                    obj.addProperty("haveParent", true);
                }else{
                    obj.addProperty("haveParent", false);
                }
            }
        }else{
            if(validateParentStaus(r)){
                obj.addProperty("haveParent", true);
            }else{
                obj.addProperty("haveParent", false);
            }
        }
        JsonUtil.write(response, obj);
    }
    private boolean validateParentStaus(Resource r){
        if(r.getBid()==0){
            return true;
        }else{
            Resource p = resourceService.getResource(r.getBid());
            return p.isAvailable();
        }
    }

    @RequestMapping(params="func=recoverResource")
    //@WebLog(method = "recoverResource", params = "rid")
    public void recoverResource(HttpServletRequest request,HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        JsonObject json = new JsonObject();
        Site site = context.getSite();
        int tid = site.getId();
        int rid = Integer.parseInt(request.getParameter("rid"));
        String itemType = request.getParameter("itemType");
        Resource r = resourceService.getResource(rid, tid);
        int parentRid = r.getBid();
        try{
            parentRid = Integer.parseInt(request.getParameter("parentRid"));
        }catch(Exception e){}
        if(r!=null&&!r.getCreator().equals(context.getCurrentUID())&&!isAdmin(context, tid)){
            json.addProperty("status", false);
            json.addProperty("message", "您无权限恢复此资源！");
        }else {
            try{
                if(LynxConstants.TYPE_FILE.equals(itemType)){
                    resourceOperateService.recoverFile(tid, r.getRid(),parentRid,VWBSession.getCurrentUid(request));
                }else if(LynxConstants.TYPE_PAGE.equals(itemType)) {
                    resourceOperateService.recoverDDoc(tid,r.getRid(),parentRid,VWBSession.getCurrentUid(request));
                }else if(LynxConstants.TYPE_FOLDER.equals(itemType)){
                    json.addProperty("status", false);
                    json.addProperty("message", "不支持文件恢复");
                    //                  resourceOperateService.recoverFolder(tid, r.getRid(), parentRid,VWBSession.getCurrentUid(request));
                }
            }catch(NoEnoughSpaceException e){
                json.addProperty("status", false);
                json.addProperty("message", e.getMessage());
                JsonUtil.write(response, json);
                return ;
            }
            json.addProperty("status", true);
            json.addProperty("redirectURL", site.getURL(UrlPatterns.T_VIEW_R, rid+"", null));
            if(r.isFile()){
                eventDispatcher.sendFileRecoverEvent(tid, r,context.getCurrentUID());
            }else if(r.isPage()){
                eventDispatcher.sendPageRecoverEvent(tid, r,context.getCurrentUID());
            }else if(r.isFolder()){
                eventDispatcher.sendFolderRecoverEvent(tid, r,context.getCurrentUID());
            }
        }
        JsonUtil.write(response, json);
    }

    private boolean isAdmin(VWBContext context,int tid){
        String user = context.getCurrentUID();
        if(user==null||user.length()==0){
            return false;
        }
        return Team.AUTH_ADMIN.equals( authorityService.getTeamAuthority(tid, user));
    }
}
