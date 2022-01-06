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
import net.duckling.ddl.service.itemtypemap.ItemTypeMappingService;
import net.duckling.ddl.service.itemtypemap.ItemTypemapping;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.CommonUtils;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.ImageUtils;
import net.duckling.ddl.web.controller.file.BaseAttachController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

/**
 * @date 2011-8-12
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/{teamCode}/download/{fid}")
@RequirePermission(target = "team", operation = "view")
public class LynxFileDownloadController extends BaseAttachController {

    @Autowired
    private IPictureService pictureService;
    @Autowired
    private BrowseLogService browseLogService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ItemTypeMappingService itemTypeMappingService;
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
    private Resource getResource(int tid,int fid){
        ItemTypemapping i =itemTypeMappingService.getItemTypeMapping(tid, fid, LynxConstants.TYPE_FILE);
        if(i==null){
            return null;
        }
        return resourceService.getResource(i.getRid());
    }

    @RequestMapping(params = { "type=pdf", "version" })
    @WebLog(method = "downloadPdf", params = "fid")
    public ModelAndView downloadPdf(HttpServletRequest req, HttpServletResponse res, @PathVariable("fid") int fid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource f =getResource( tid,fid);

        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, f.getRid(), LynxConstants.TYPE_FILE);
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
        browseLogService.resourceVisited(tid, f.getRid(), context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        return null;
    }

    @RequestMapping(params = "type=doc")
    @WebLog(method = "downloadDoc", params = "rid")
    public ModelAndView previewDoc(HttpServletRequest req, HttpServletResponse res, @PathVariable("fid") int fid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource dfile = getResource(tid,fid);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, dfile.getRid(), LynxConstants.TYPE_FILE);
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
        browseLogService.resourceVisited(tid, dfile.getRid(), context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);

        return null;
    }

    @RequestMapping
    public ModelAndView download(HttpServletRequest req, HttpServletResponse res, @PathVariable("fid") int fid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource dfile = getResource(tid,fid);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, dfile.getRid(), LynxConstants.TYPE_FILE);
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
        browseLogService.resourceVisited(tid, dfile.getRid(), context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);

        return null;
    }

    @RequestMapping(params = "func=cache")
    public ModelAndView cache(HttpServletRequest req, HttpServletResponse res, @PathVariable("fid") int fid)
            throws IOException {
        Site site = VWBContext.findSite(req);
        int tid = site.getId();
        Resource dfile = getResource(tid,fid);
        VWBContext context = VWBContext.createContext(req, UrlPatterns.T_FILE, dfile.getRid(), LynxConstants.TYPE_FILE);
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
        browseLogService.resourceVisited(tid, dfile.getRid(), context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        return null;
    }

}
