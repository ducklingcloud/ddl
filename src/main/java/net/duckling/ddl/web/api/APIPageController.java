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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authenticate.UserPrincipal;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.file.AttSaver;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.render.DPageRendable;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.ResourceNotFound;

/**
 * API接口中访问页面接口
 * @date 2011-8-22
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/mobile/page")
@RequirePermission(target="team", operation="view")
public class APIPageController extends APIBaseController {
    private static final Logger LOG = Logger.getLogger(APIPageController.class);
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private BrowseLogService browseLogService;

    @SuppressWarnings("unchecked")
    @WebLog(method = "mobilePage", params = "pid,itemType")
    @RequestMapping
    public ModelAndView service(@RequestParam("pid") int rid, @RequestParam("itemType") String itemType,
                                HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isEmpty(itemType)){
            Resource r = resourceService.getResource(rid);
            if(r==null||VWBContext.getCurrentTid()!=r.getTid()||!r.isAvailable()){
                JSONObject object= new JSONObject();
                object.put("错误", "您浏览的文件没有找到！");
                JsonUtil.writeJSONObject(response, object);
                return null;
            }
            itemType = r.getItemType();
        }
        String uid = VWBSession.findSession(request).getCurrentUser().getName();
        String userName = ((UserPrincipal)VWBSession.findSession(request).getCurrentUser()).getFullName();
        if(LynxConstants.TYPE_FILE.equals(itemType)) {
            // 是文件，下载直接显示，支持txt,pdf,doc,ppt,xsl等文件类型
            Resource dfile = null;
            try {
                dfile = resourceService.getResource(rid, VWBContext.getCurrentTid());
                if(dfile==null){
                    throw new AccessForbidden(userName);
                }
                FileVersion f = getFileVersion(dfile, request.getParameter("version"));
                downloadFileContentFromCLB(request, response, f, resourceOperateService);
            } catch(ResourceNotFound notFound) {
                JSONObject object= new JSONObject();
                object.put("错误", "您浏览的文件没有找到！");
                JsonUtil.writeJSONObject(response, object);
                return null;
            } catch(AccessForbidden forbidden) {
                JSONObject object= new JSONObject();
                object.put("错误", "您没有权限查看此文件！");
                JsonUtil.writeJSONObject(response, object);
                return null;
            }
            gridService.clickItem(uid,dfile.getTid(), dfile.getRid(), LynxConstants.TYPE_FILE);
            browseLogService.resourceVisited(dfile.getTid(), rid, uid, userName, LynxConstants.TYPE_FILE);
            return null;
        }
        // 是页面，显示页面内容
        Resource meta = resourceService.getResource(rid, VWBContext.getCurrentTid());
        gridService.clickItem(uid,meta.getTid(), meta.getRid(), LynxConstants.TYPE_PAGE);
        browseLogService.resourceVisited(meta.getTid(), rid, uid, userName, LynxConstants.TYPE_PAGE);
        VWBContext.createContext(request, UrlPatterns.T_PAGE, meta);
        ModelAndView mv = new ModelAndView("mobile/mobile_dpage");
        mv.addObject("title", meta.getTitle());
        mv.addObject("content", new DPageRendable(meta.getTid(), meta.getRid()));
        return mv;
    }
    /**
     * 获取文件的title
     * @param pid
     * @param itemType
     * @param request
     * @param response
     */
    @RequestMapping(params="func=getFileTitle")
    public void getFileTitle(@RequestParam("rid") int rid, @RequestParam("itemType") String itemType,
                             HttpServletRequest request, HttpServletResponse response){
        if(StringUtils.isEmpty(itemType)){
            Resource r = resourceService.getResource(rid);
            if(r==null||VWBContext.getCurrentTid()!=r.getTid()||!r.isAvailable()){
                JSONObject object= new JSONObject();
                object.put("错误", "您浏览的文件没有找到！");
                JsonUtil.writeJSONObject(response, object);
                return ;
            }
            itemType = r.getItemType();
        }
        Resource r = resourceService.getResource(rid);
        JSONObject object= new JSONObject();
        object.put("title", r.getTitle());
        JsonUtil.writeJSONObject(response, object);
    }

    private void downloadFileContentFromCLB(HttpServletRequest req, HttpServletResponse res,
                                            FileVersion f, ResourceOperateService acs) {
        AttSaver fs = new AttSaver(res, req,f.getTitle());
        fs.setLength(f.getSize());
        int docID = f.getClbId();
        if(FileTypeUtils.isClbDealImage(f.getTitle())){
            acs.getImageContent(docID, f.getClbVersion(), req.getParameter("imageType"), fs);
        }else{
            if (f.getClbVersion()==0) {
                acs.getContent(docID, fs);
            } else {
                acs.getContent(docID, f.getClbVersion()+"", fs);
            }
        }
        downloadLog( VWBSession.findSession(req).getCurrentUser().getName(), f.getTitle(), f.getClbId(), f.getClbVersion()+"");
    }

    /**
     * 下载日志
     * @param uid
     * @param name
     * @param docId
     * @param version
     */
    private void downloadLog(String uid,String name,int docId,String version){
        LOG.info("uid:"+uid+" download [name="+name+",docId="+docId+",version="+version+"]");
    }
    /*
      private String getClbVersion(Resource f, String version, int rid, int tid, ResourceOperateService service) {
      // 现在下载时用clbVersion，fileVersion只是给用户看的
      int clbVersion = -1;
      if (!CommonUtils.isNull(version)) {
      clbVersion = fileVersionService.getFileVersion(rid, tid, Integer.parseInt(version)).getClbVersion();
      } else {
      clbVersion = f.getClbVersion();
      }
      return clbVersion + "";
      }
    */
    private FileVersion getFileVersion(Resource f,String version){
        FileVersion result = null;
        if("0".equals(version)||StringUtils.isEmpty(version)){
            result = fileVersionService.getLatestFileVersion(f.getRid(), f.getTid());
        }else{
            result = fileVersionService.getFileVersion(f.getRid(), f.getTid(), Integer.parseInt(version));
        }
        return result;
    }

}
