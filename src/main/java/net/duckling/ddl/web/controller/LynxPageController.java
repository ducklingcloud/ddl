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
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.comment.CommentService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.itemtypemap.ItemTypeMappingService;
import net.duckling.ddl.service.itemtypemap.ItemTypemapping;
import net.duckling.ddl.service.relaterec.DGridDisplay;
import net.duckling.ddl.service.relaterec.impl.RelatedRecServiceImpl;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.subscribe.SubscriptionService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.bean.AttachmentItem;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;

/**
 * @date 2011-5-6
 * @author Clive Lee
 */
@Controller
@RequestMapping("/{teamCode}/page/{pid}")
@RequirePermission(target = "team", operation = "view")
public class LynxPageController extends BaseController {

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RelatedRecServiceImpl relateRecService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private PageLockService pageLockService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private ItemTypeMappingService itemTypeMappingService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private URLGenerator urlGenerator;

    protected static final Logger LOG = Logger.getLogger(LynxPageController.class);

    private boolean validateDeleteAuth(VWBContext context, int rid,String resourceType){
        String u = context.getCurrentUID();
        if(authorityService.teamAccessability(VWBContext.getCurrentTid(),
                                              VWBSession.findSession(context.getHttpRequest()), AuthorityService.ADMIN)){
            return true;
        }else{
            Resource r = resourceService.getResource(rid,VWBContext.getCurrentTid());
            if(r!=null&&u.equals(r.getCreator())){
                return true;
            }
        }
        return false;
    }
    @RequestMapping(params = "func=del")
    @RequirePermission(target="team", operation="edit")
    @WebLog(method="pageDelete",params="rid")
    public void del(HttpServletRequest request, HttpServletResponse response,@PathVariable("rid")Integer rid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.DELETE);
        Site site = context.getSite();
        int tid = site.getId();
        JSONObject obj = new JSONObject();
        if(!validateDeleteAuth(context, rid, LynxConstants.TYPE_PAGE)){
            obj.put("status", false);
            JsonUtil.writeJSONObject(response, obj);
            return ;
        }
        PageLock lock = pageLockService.getCurrentLock(VWBContext.getCurrentTid(),rid);
        if(lock!=null){
            List<PageLock> locks = new ArrayList<PageLock>();
            locks.add(lock);
            PageLockValidateUtils.pageLockMessage(locks, response, resourceOperateService);
            return ;
        }
        resourceOperateService.deleteResource(tid, rid,context.getCurrentUID());
        fileVersionService.deleteRefer(rid,tid);
        resourceService.delete(rid, tid, LynxConstants.TYPE_PAGE);
        subscriptionService.removePageSubscribe(tid,rid);
        commentService.removePageComment(tid,rid,LynxConstants.TYPE_PAGE);
        obj.put("status", true);
        obj.put("redirectUrl", urlGenerator.getURL(tid, UrlPatterns.T_TEAM_HOME, "", null));
        JsonUtil.writeJSONObject(response, obj);
    }

    @RequestMapping
    @WebLog(method="pageView",params="pid")
    public ModelAndView display(HttpServletRequest request,HttpServletResponse response, @PathVariable("pid")Integer pid) {
        int tid = VWBContext.getCurrentTid();
        ItemTypemapping i = itemTypeMappingService.getItemTypeMapping(tid,pid, LynxConstants.TYPE_PAGE);
        if(i==null){
            notFound(request, response, true);
        }
        ModelAndView mv = new ModelAndView(new RedirectView(urlGenerator.getURL(tid, UrlPatterns.T_VIEW_R, i.getRid()+"", null)));
        return mv;
        /*
          VWBContext context = VWBContext.createContext(request,UrlPatterns.VIEW, pid,LynxConstants.TYPE_PAGE);
          ItemTypemapping m = itemTypeMappingService.getItemTypeMapping(pid, LynxConstants.TYPE_PAGE);
          Resource resource = context.getResource();
          //add by lvly@2012-07-20
          if(resource!=null&&!StringUtils.isNullOrEmpty(resource.getStatus())&&LynxConstants.STATUS_DELETE.equals(resource.getStatus())){
          ModelAndView mv = BaseController.layout(ELayout.LYNX_MAIN, context, "/jsp/aone/page/pageRemoved.jsp");
          if(resource.getCreator().equals(context.getCurrentUID())||isAdmin(context, resource.getTid())){
          mv.addObject("recoverFlag", true);
          mv.addObject("rid", resource.getRid());
          }
          return mv;
          }
          if(resource!=null && resource.getBid()!=0){
          return getBundleView(context, request, resource.getRid(), resource.getBid());
          }
          PageRender render = null;
          try{
          render = getPageRender(pid,context);
          }catch(NullPointerException e){
          notFound(request, response, true);
          return null;
          }
          Resource meta = render.getMeta();
          String uid = context.getCurrentUID();
          int tid = context.getSite().getId();
          if(meta==null){
          notFound(request, response, true);
          return null;
          }
          int latestVersion = meta.getLastVersion();
          int version = getRequestVersion(request);
          if (VWBContext.LATEST_VERSION != version && meta.getLastVersion() != version) {
          render = resourceOperateService.getPageRender(tid,meta.getRid(),version);
          meta = render.getMeta();
          }
          gridService.clickItem(uid, tid, pid, LynxConstants.TYPE_PAGE);
          ModelAndView mv = BaseController.layout(VIEW_TEMPLATE, context, new DPageRendable(VWBContext.getCurrentTid(), meta.getRid(), meta.getLastVersion()));
          loadAttachmentList(mv, pid);
          loadRelatedRecPagesList(mv,context,pid);
          mv.addObject("autoClosepageFlage", request.getParameter("autoClosepageFlage"));
          mv.addObject("resource", resource);
          mv.addObject("pageMeta",meta);
          mv.addObject("editor",  context.getContainer().getAoneUserService().getUserNameByID(meta.getLastEditor()));
          mv.addObject("version", meta.getLastVersion());
          mv.addObject("copyLog",copyService.getCopyedDisplay(resource.getRid(), meta.getLastVersion()));
          mv.addObject("latestVersion",latestVersion);
          mv.addObject("uid", context.getCurrentUID());
          mv.addObject("pid", pid);
          mv.addObject("bid", resource.getBid());
          Set<String> starmark = resource.getMarkedUserSet();
          if(null!=starmark && starmark.contains(context.getCurrentUID())){
          mv.addObject("starmark", true);
          }else{
          mv.addObject("starmark", false);
          }
          context.setResource(pid, LynxConstants.TYPE_PAGE);
          domainEvents.pageVisited(context.getSite(),   pid,context.getCurrentUID(),context.getCurrentUserName());
          return mv;
        */
    }

    private void loadAttachmentList(ModelAndView mv,int pid) {
        List<FileVersion> results = fileVersionService.getFilesOfPage(pid,VWBContext.getCurrentTid());
        List<AttachmentItem> itemList = new ArrayList<AttachmentItem>();
        for(FileVersion att:results) {
            itemList.add(AttachmentItem.convertFromAttachment(att));
        }
        mv.addObject("attachments",itemList);
    }
    private boolean isAdmin(VWBContext context,int tid){
        String user = context.getCurrentUID();
        if(user==null||user.length()==0){
            return false;
        }
        return Team.AUTH_ADMIN.equals( authorityService.getTeamAuthority(tid, user));
    }
    private void loadRelatedRecPagesList(ModelAndView mv,VWBContext context,int pid){
        int num = 5;
        int tid = VWBContext.getCurrentTid();
        String uid = context.getCurrentUID();
        DGridDisplay dGridDisplay = relateRecService.getRelatedRecOfPage(tid, uid, pid, num);
        mv.addObject("relatedGrids", dGridDisplay);
    }

    private PageRender getPageRender(int pid, VWBContext context) {
        int tid = context.getSite().getId();
        return resourceOperateService.getPageRender(tid, pid);
    }

    private int getRequestVersion(HttpServletRequest request) {
        String version = request.getParameter("version");
        if (version != null) {
            try {
                return Integer.parseInt(version);
            } catch (NumberFormatException e) {
                LOG.warn(e.getMessage(),e);
            }
        }
        return VWBContext.LATEST_VERSION;
    }
    private ModelAndView getBundleView(VWBContext context, HttpServletRequest request,
                                       int rid, int bid) {
        int version = getRequestVersion(request);
        String params = "rid="+rid;
        params = (version<=0)?params:(params+"&version="+version);

        ModelAndView mv = new ModelAndView(new RedirectView(urlGenerator.getURL(UrlPatterns.T_BUNDLE, bid+"", params)));
        mv.addObject("autoClosepageFlage", request.getParameter("autoClosepageFlage"));
        return mv;
    }

}
