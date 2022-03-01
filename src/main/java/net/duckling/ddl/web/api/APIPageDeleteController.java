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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.comment.CommentService;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.PageLock;
import net.duckling.ddl.service.resource.PageLockService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.subscribe.SubscriptionService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.cnic.cerc.dlog.client.WebLog;


@Controller
@RequestMapping("/api/pageDelete")
@RequirePermission(target="team", operation="view")
public class APIPageDeleteController extends APIBaseController {

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private PageLockService pageLockService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FileVersionService fileVersionService;
    private boolean validateDeleteAuth(VWBContext context, Resource r){
        String u = context.getCurrentUID();
        if(authorityService.teamAccessability(VWBContext.getCurrentTid(),
                                              VWBSession.findSession(context.getHttpRequest()), AuthorityService.ADMIN)){
            return true;
        }else{
            if(r!=null&&u.equals(r.getCreator())){
                return true;
            }
        }
        return false;
    };
    /**
     * @param pid 准备删除的页面的ID
     * @param request
     * @param response
     * 返回信息有三个状态，1为删除成功，0为删除失败，-1为页面有人在编辑等操作被锁定，不能删除
     */
    @SuppressWarnings("unchecked")
    @WebLog(method = "apiDelete", params = "rid,itemId,itemType")
    @RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.DELETE);
        Site site = context.getSite();
        int tid = site.getId();
        JsonObject obj = new JsonObject();
        int delete = 1;
        Resource resource = getResoure(request, site);
        if(resource==null){
            obj.addProperty("succ", -2);
            JsonUtil.write(response, obj);
            return;
        }else if(resource.isDelete()){
            obj.addProperty("succ", -3);
            JsonUtil.write(response, obj);
            return;
        }
        if(!validateDeleteAuth(context,resource)){
            delete = 0;
        }else{
            String uid = context.getCurrentUID();
            if(resource.isPage()){
                int rid = resource.getRid();
                PageLock lock = pageLockService.getCurrentLock(resource.getTid(),resource.getRid());
                if(lock!=null){
                    delete = -1;
                }
                if(delete == 1) {
                    resourceOperateService.deleteResource(tid, rid,uid);
                    //                  fileVersionService.removePageRefers(rid,tid);
                    //                  subscriptionService.removePageSubscribe(site.getId(),rid);
                    //                  commentService.removePageComment(tid,rid,LynxConstants.TYPE_PAGE);
                }
            }else if(resource.isFile()){
                resourceOperateService.deleteResource(tid, resource.getRid(),uid);
                //              resourceOperateService.deleteFile(resource.getTid(), resource.getRid());
                //删除a1_grid_item中的记录
            }else if(resource.isFolder()){
                boolean b = resourceOperateService.deleteAuthValidate(tid, resource.getRid(), context.getCurrentUID());
                if(b){
                    resourceOperateService.deleteResource(tid, resource.getRid(),uid);
                }else{
                    delete = -1;
                }
            }
        }
        obj.addProperty("succ", delete);
        JsonUtil.write(response, obj);
    }

    private Resource getResoure(HttpServletRequest request,Site site){
        String rid = request.getParameter("rid");
        if(StringUtils.isNotEmpty(rid)){
            try{
                int id = Integer.parseInt(rid);
                return resourceService.getResource(id);
            }catch(Exception e){
            }
        }
        String itemId = request.getParameter("itemId");
        String itemType = request.getParameter("itemType");
        if(StringUtils.isEmpty(itemId)||StringUtils.isEmpty(itemType)){
            return null;
        }
        int id = Integer.parseInt(itemId);
        return resourceService.getResource(id, site.getId());
    }

    private void updateTagCount(Resource res, ITagService ts){
        Map<Integer, String> tagMap = res.getTagMap();
        if(null == tagMap || tagMap.isEmpty()){
            return;
        }
        int tid = res.getTid();
        for(Map.Entry<Integer, String> entry : tagMap.entrySet()){
            ts.updateTagCount(tid, entry.getKey());
        }
    }
}
