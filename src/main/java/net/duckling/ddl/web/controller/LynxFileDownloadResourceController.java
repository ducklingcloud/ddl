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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.file.IPictureService;
import net.duckling.ddl.service.file.Picture;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.ImageUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.file.BaseAttachController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

@Controller
@RequestMapping("/{teamCode}/downloadResource/{rid}")
@RequirePermission(target = "team", operation = "view")
public class LynxFileDownloadResourceController  extends BaseAttachController{

    @Autowired
    private IPictureService pictureService;
    @Autowired
    private BrowseLogService browseLogService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private IResourceService resourceService;

    private FileVersion getClbVersion(Resource f, String version, int tid) {
        // 现在下载时用clbVersion，fileVersion只是给用户看的
        FileVersion clbVersion = null;
        if (!CommonUtils.isNull(version)) {
            clbVersion = fileVersionService.getFileVersion(f.getRid(), tid, Integer.parseInt(version));
        } else {
            clbVersion = fileVersionService.getLatestFileVersion(f.getRid(), tid);
        }
        ;
        return clbVersion;
    }

    @RequestMapping(params = { "type=pdf", "version" })
    @WebLog(method = "downloadPdf", params = "rid")
    public ModelAndView downloadPdf(HttpServletRequest req, HttpServletResponse res, @PathVariable("rid") int rid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource f = resourceService.getResource(rid,tid);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, rid, LynxConstants.TYPE_FILE);

        // add by lvly@2012-07-23 如发现文件已删除，则跳转到删除页面
        if (f == null || LynxConstants.STATUS_DELETE.equals(f.getStatus())) {
            return layout(ELayout.LYNX_MAIN, context, "/jsp/aone/file/fileRemoved.jsp");
        }
        if (f != null && f.isAvailable()) {
            FileVersion fv = getClbVersion(f, req.getParameter("version"),  tid);
            if(FileTypeUtils.isClbDealImage(f.getTitle())){
                getPictureContext(req, res, fv, false);
            }else{
                getPdfContent(req, res, fv.getClbId(), fv.getClbVersion()+"",fv.getTitle(),
                              false);
            }
        }
        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        return null;
    }

    @RequestMapping(params = "type=doc")
    @WebLog(method = "downloadDoc", params = "rid")
    public ModelAndView previewDoc(HttpServletRequest req, HttpServletResponse res, @PathVariable("rid") int rid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, rid, LynxConstants.TYPE_FILE);
        int tid = site.getId();
        Resource dfile = resourceService.getResource(rid,tid);
        // add by lvly@2012-07-23 如发现文件已删除，则跳转到删除页面
        if (dfile == null || LynxConstants.STATUS_DELETE.equals(dfile.getStatus())) {
            return layout(ELayout.LYNX_MAIN, context, "/jsp/aone/file/fileRemoved.jsp");
        }
        if (dfile != null && dfile.isAvailable()) {
            FileVersion fv = getClbVersion(dfile, req.getParameter("version"),  tid);
            if(FileTypeUtils.isClbDealImage(dfile.getTitle())){
                getPictureContext(req, res, fv, false);
            }else{
                getContent(req, res, fv.getClbId(),fv.getClbVersion()+"",fv.getTitle(), false);
            }
        }
        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);

        return null;
    }

    @RequestMapping
    @WebLog(method = "download", params = "rid")
    public ModelAndView download(HttpServletRequest req, HttpServletResponse res, @PathVariable int rid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource dfile = resourceService.getResource(rid,tid);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, rid, LynxConstants.TYPE_FILE);
        // add by lvly@2012-07-23 如发现文件已删除，则跳转到删除页面
        if (dfile == null || LynxConstants.STATUS_DELETE.equals(dfile.getStatus())) {
            return layout(ELayout.LYNX_MAIN, context, "/jsp/aone/file/fileRemoved.jsp");
        }
        // 如果请求要简略图，那就给他简略图
        if (dfile != null && dfile.isAvailable()) {
            if (!CommonUtils.isNull(req.getParameter("simple")) && ImageUtils.isPicture(dfile.getTitle())){
                FileVersion fv = getClbVersion(dfile, req.getParameter("version"),  tid);
                Picture pic= pictureService.getPicture(fv.getClbId(), fv.getClbVersion());
                if(pic==null){
                    getContent(req, res, fv.getClbId(),fv.getClbVersion()+"",fv.getTitle(), false);
                }else{
                    getContent(req, res, pic.getClbId(),fv.getTitle(),
                               "1", false);
                }
            } else {
                FileVersion fv = getClbVersion(dfile, req.getParameter("version"),  tid);
                if(FileTypeUtils.isClbDealImage(dfile.getTitle())){
                    getPictureContext(req, res, fv, true);
                }else{
                    getContent(req, res, fv.getClbId(),fv.getClbVersion()+"",fv.getTitle(), false);
                }
            }
        }

        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);

        return null;
    }

    @RequestMapping(params = "func=cache")
    @WebLog(method = "downloadCache", params = "rid")
    public ModelAndView cache(HttpServletRequest req, HttpServletResponse res, @PathVariable int rid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource dfile = resourceService.getResource(rid,tid);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, rid, LynxConstants.TYPE_FILE);
        // add by lvly@2012-07-23 如发现文件已删除，则跳转到删除页面
        if (dfile != null && LynxConstants.STATUS_DELETE.equals(dfile.getStatus())) {
            return layout(ELayout.LYNX_MAIN, context, "/jsp/aone/file/fileRemoved.jsp");
        }
        if (dfile != null && dfile.isAvailable()) {
            FileVersion fv = getClbVersion(dfile, req.getParameter("version"),  tid);
            if(FileTypeUtils.isClbDealImage(dfile.getTitle())){
                getPictureContext(req, res, fv, true);
            }else{
                getContent(req, res, fv.getClbId(),fv.getClbVersion()+"",fv.getTitle(), true);
            }
        }
        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        return null;
    }
    @RequestMapping(params = "func=getImageStatus")
    @WebLog(method = "getImageStatus", params = "rid")
    public void getImageStatus(HttpServletRequest req, HttpServletResponse resp, @PathVariable int rid){
        String type = req.getParameter("type");
        int version = 0;
        try{
            version = Integer.parseInt(req.getParameter("version"));
        }catch(Exception e){}
        FileVersion fv = null;
        if(version==0){
            fv = fileVersionService.getLatestFileVersion(rid, VWBContext.getCurrentTid());
        }else{
            fv= fileVersionService.getFileVersion(rid, VWBContext.getCurrentTid(), version);
        }
        String status = getImageStatus(fv.getClbId(), fv.getClbVersion()+"", type);
        JsonObject j = new JsonObject();
        j.addProperty("status", status);
        JsonUtil.write(resp,j);
    }

    @RequestMapping(params = "func=folder")
    public void downloadFolder(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String[] rids = req.getParameterValues("rids[]");
        List<Integer> rs = new ArrayList<Integer>();
        for(String r : rids){
            int rid = Integer.parseInt(r);
            rs.add(rid);
        }
        downloadFolder(req, resp, rs);
    }

}
