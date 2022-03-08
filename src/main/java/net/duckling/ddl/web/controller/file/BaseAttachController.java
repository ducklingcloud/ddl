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

package net.duckling.ddl.web.controller.file;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.file.AttSaver;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.file.ZipAttSaver;
import net.duckling.ddl.service.file.ZipResourceTree;
import net.duckling.ddl.service.file.impl.CLBFileStorage;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.util.Browser;
import net.duckling.ddl.web.bean.ClbUrlTypeBean;
import net.duckling.ddl.web.bean.NginxAgent;
import net.duckling.ddl.web.controller.BaseController;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.vlabs.clb.api.AccessForbidden;
import cn.vlabs.clb.api.CLBException;
import cn.vlabs.clb.api.ResourceNotFound;
import cn.vlabs.clb.api.document.MetaInfo;

/**
 * @date 2011-8-12
 * @author xiejj@cnic.cn
 */
public class BaseAttachController extends BaseController{

    private static final Logger LOG = Logger.getLogger(BaseAttachController.class);

    private static class AttachErrorHandler implements DownloadEventHandler {
        public void onException(HttpServletResponse res, CLBException e)
                throws IOException {
            res.addDateHeader("Last-Modified", -1);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        public void onForbidden(HttpServletResponse res, AccessForbidden e)
                throws IOException {
            res.addDateHeader("Last-Modified", -1);
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

        public void onNotFound(HttpServletResponse res, ResourceNotFound e)
                throws IOException {
            res.addDateHeader("Last-Modified", -1);
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    private static class CachableErrorHandler implements DownloadEventHandler {
        public void onException(HttpServletResponse res, CLBException e)
                throws IOException {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        public void onForbidden(HttpServletResponse res, AccessForbidden e)
                throws IOException {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

        public void onNotFound(HttpServletResponse res, ResourceNotFound e)
                throws IOException {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private interface DownloadEventHandler {
        void onException(HttpServletResponse res, CLBException e)
                throws IOException;

        void onForbidden(HttpServletResponse res, AccessForbidden e)
                throws IOException;

        void onNotFound(HttpServletResponse res, ResourceNotFound e)
                throws IOException;
    }

    private static final int ATTACH_MAX_AGE = 0;
    private static final int CACHABLE_MAX_AGE = 28800;
    private static final Logger LOGGER = Logger.getLogger(BaseAttachController.class);
    private static final DownloadEventHandler ATTACH_HANDLER =  new  AttachErrorHandler();
    private static final DownloadEventHandler CACHE_HANDLER =  new  CachableErrorHandler();

    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private FileVersionService fileVersionService;

    protected void getContent(Site site,HttpServletRequest req, HttpServletResponse res,int docID, String version,String fileName, boolean useCache) throws IOException{
        downloadFileContentFromCLB(req, res, docID, version,fileName,useCache);
        downloadLog(VWBSession.getCurrentUid(req),fileName,docID,version);
    }

    protected void getContent(HttpServletRequest req, HttpServletResponse res, int docID, String version,String fileName,
                              boolean useCache) throws IOException {
        downloadFileContentFromCLB(req, res, docID, version,fileName, useCache);
        downloadLog(VWBSession.getCurrentUid(req),fileName,docID,version);
    }
    protected void getPictureContext(HttpServletRequest req,HttpServletResponse resp,FileVersion f,boolean useCache) throws IOException{
        String type = req.getParameter("imageType");
        downloadPictureContentFromCLB(req, resp, f, type, useCache);
        downloadLog(VWBSession.getCurrentUid(req),f.getTitle(),f.getClbId(),f.getClbVersion()+"");
    }

    private void downloadPictureContentFromCLB(
        HttpServletRequest req, HttpServletResponse resp, FileVersion f,
        String type, boolean useCache)
            throws IOException {
        String version = f.getClbVersion() + "";
        int clbVersion = f.getClbVersion();
        MetaInfo meta = resourceOperateService.getMetaInfo(
            f.getClbId(), version);
        try {
            // 附件在浏览器上保存的最长时间
            if (isModified(meta, req)) {
                if (NginxAgent.isNginxMode()) {
                    ClbUrlTypeBean url = resourceOperateService
                            .getImageDirevtURL(f.getClbId(),
                                               clbVersion + "", type);
                    if (url == null || StringUtils.isEmpty(url.getUrl())) {
                        resp.setStatus(404);
                    } else {
                        if (!url.isStatus()) {
                            String u = req.getContextPath() +"/"+
                                    VWBContext.getCurrentTeamCode()
                                    +"/downloadResource/"+ f.getRid()
                                    +"?type=doc&imageType=original&version="+
                                    f.getVersion();
                            resp.sendRedirect(u);
                        } else {
                            NginxAgent.setRedirectUrl(
                                req, resp, f.getTitle(), meta.size, url.getUrl());
                        }
                    }
                } else {
                    setModifiedHeader(CACHABLE_MAX_AGE, meta, resp);
                    AttSaver fs = new AttSaver(resp, req, f.getTitle());
                    try {
                        resourceOperateService.getImageContent(
                            f.getClbId(), clbVersion, type, fs);
                    } catch (ResourceNotFound e) {
                        resp.setStatus(404);
                        if (type.equals(CLBFileStorage.IMAGE_TYPE_FIXSMALL)) {
                            LOGGER.warn(f +" has no thumbnail(small image).");
                        } else {
                            LOGGER.error(f +" type="+ type +" not found.");
                        }
                    }
                }
            } else {
                sendNotModifiedHeader(
                    useCache ? CACHABLE_MAX_AGE : ATTACH_MAX_AGE,
                    meta, resp);
            }
        } catch (Exception e) {
            dealException(e, f.getClbId(), useCache, resp);
        }
    }

    protected void getPdfContent(HttpServletRequest req, HttpServletResponse res, int docID, String version,
                                 String fileName, boolean useCache) throws IOException {
        downloadPDFContentFromCLB(req, res, docID, version, fileName, useCache);
        downloadLog(VWBSession.getCurrentUid(req), fileName, docID, version);
    }

    private void downloadFileContentFromCLB(HttpServletRequest req, HttpServletResponse resp, int docID, String version,
                                            String fileName, boolean useCache) throws IOException {
        int age = useCache ? CACHABLE_MAX_AGE : ATTACH_MAX_AGE;
        try {
            MetaInfo meta = getResourceMeta(docID, version);
            if (fileName == null) {
                fileName = meta.getFilename();
            } else {
                meta.setFilename(fileName);
            }
            if (NginxAgent.isNginxMode()) {
                String url = resourceOperateService.getDirectURL(docID, version, false);
                NginxAgent.setRedirectUrl(req, resp, fileName, meta.size, url);
                return;
            }
            // 附件在浏览器上保存的最长时间
            if (isModified(meta, req)) {
                setModifiedHeader(age, meta, resp);
                AttSaver fs = new AttSaver(resp, req, meta.getFilename());
                resourceOperateService.getContent(docID, version, fs);
            } else {
                sendNotModifiedHeader(age, meta, resp);
            }
            LOGGER.info(meta);
        } catch (Exception e) {
            dealException(e, docID, useCache, resp);
        }
    }

    private void downloadPDFContentFromCLB(HttpServletRequest req, HttpServletResponse resp, int docID, String version,
                                           String fileName, boolean useCache) throws IOException {
        try {
            MetaInfo meta = getResourceMeta(docID, version);
            if (fileName == null) {
                fileName = meta.getFilename();
            }
            if (NginxAgent.isNginxMode()) {
                String url = resourceOperateService.getDirectURL(docID, version, true);
                NginxAgent.setRedirectUrl(req, resp, fileName, meta.size, url);
                return;
            }

            AttSaver fs = new AttSaver(resp, req, fileName);
            resourceOperateService.getPdfContent(docID, version, fs);
        } catch (Exception e) {
            dealException(e, docID, useCache, resp);
        }
    }

    private void dealException(Exception e,int docID,boolean useCache,HttpServletResponse res) throws IOException{
        DownloadEventHandler handler = getEventHandler(useCache);
        if(e instanceof AccessForbidden){
            LOGGER.warn("对ID为" + docID + "文档所对应的PDF文档的访问被拒绝！");
            handler.onForbidden(res, ((AccessForbidden)e));
        }else if(e instanceof ResourceNotFound){
            LOGGER.warn("没有找到ID为" + docID + "的文档所对应的PDF文档！");
            handler.onNotFound(res, ((ResourceNotFound)e));
        }else if(e instanceof CLBException){
            LOGGER.warn("获取ID为" + docID + "的文档所对应的PDF文档时，返回CLBException错误！");
            handler.onException(res, ((CLBException)e));
        }else{
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }


    public MetaInfo getResourceMeta(int docID, String version) {
        MetaInfo meta = null;
        if(null==version){
            meta = resourceOperateService.getMetaInfo(docID);
        }else{
            meta = resourceOperateService.getMetaInfo(docID,version);
        }
        return meta;
    }

    public DownloadEventHandler getEventHandler(boolean useCache) {
        DownloadEventHandler handler;
        if (useCache) {
            handler = CACHE_HANDLER;
        } else {
            handler = ATTACH_HANDLER;
        }
        return handler;
    }

    /**
     * 下载日志
     *
     * @param uid
     * @param name
     * @param docId
     * @param version
     */
    private void downloadLog(String uid, String name, int docId, String version) {
        LOGGER.info("uid:" + uid + " download [name=" + name + ",docId=" + docId + ",version=" + version + "]");
    }

    private boolean isModified(MetaInfo meta, HttpServletRequest request) {
        long browserModifyTime = request.getDateHeader("If-Modified-Since");
        try {
            return (browserModifyTime != meta.lastUpdate.getTime());
        } catch (Exception e) {
            return true;
        }
    }

    private void setModifiedHeader(int age, MetaInfo meta, HttpServletResponse res) {
        if (meta.size > 0) {
            res.setHeader("Content-Length", meta.size + "");
        }
        try {
            res.addDateHeader("last-modified", meta.lastUpdate.getTime());
        } catch (Exception e) {
        }
    }

    private void sendNotModifiedHeader(int age, MetaInfo meta, HttpServletResponse res) {
        res.addDateHeader("Last-Modified", meta.lastUpdate.getTime());
        LOGGER.debug("读取缓存文档" + meta.docid);
        res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }

    public String getImageStatus(int clbId, String version, String type) {
        return resourceOperateService.getImageStatus(clbId, version, type);
    }


    protected void downloadFolder(HttpServletRequest req,HttpServletResponse resp,List<Integer> rids) throws IOException{
        ZipResourceTree root = new ZipResourceTree();
        root.setPath("");
        List<Resource> rs = resourceService.getResource(rids);
        for(Resource r : rs){
            if(!r.isPage()){
                dealResource(root, r);
            }
        }
        String zipName = "";
        if(rs.size()==1){
            zipName = rs.get(0).getTitle()+".zip";
        }else{
            zipName = "【DDL下载】"+rs.get(0).getTitle()+"文件等.zip";
        }
        addDownFolderHeader(req,resp,zipName);
        ZipOutputStream zipOut = new ZipOutputStream(resp.getOutputStream(),Charset.forName("GBK"));
        downloadFolder(zipOut,root);
        zipOut.close();

    }

    private void addDownFolderHeader(HttpServletRequest req,HttpServletResponse resp, String zipName) {
        resp.setContentType("application/zip");
        resp.setHeader("Content-Disposition", Browser.encodeFileName(req.getHeader("USER-AGENT"), zipName));
    }

    private void downloadFolder(ZipOutputStream zipOut, ZipResourceTree root) {
        Resource r = root.getResource();
        if(r!=null && r.isFile()){
            ZipAttSaver fs = new ZipAttSaver(root.getPath(),zipOut);
            FileVersion fv =fileVersionService.getFileVersion(r.getRid(), r.getTid(), r.getLastVersion());
            if(fv!=null){
                resourceOperateService.getContent(fv.getClbId(), fv.getClbVersion()+"", fs);
            }
        }else if(root.getChildren()!=null){ //含有子文件的文件夹
            for(ZipResourceTree z : root.getChildren()){
                downloadFolder(zipOut, z);
            }
        }else if(r!=null && r.isFolder()){ //空文件夹
            ZipEntry en = new ZipEntry(root.getPath()+"/");
            try {
                zipOut.putNextEntry(en);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dealResource(ZipResourceTree root ,Resource r){
        if(r.isFolder()){
            List<Resource> rs = folderPathService.getDescendants(r.getTid(), r.getRid());
            //按层次由浅到深将加入resource加入目录树
            Collections.reverse(rs);
            ZipResourceTree curr = new  ZipResourceTree();
            curr.setParent(root);
            root.addChild(curr);
            curr.setResource(r);
            Map<Integer,ZipResourceTree> map = new HashMap<Integer,ZipResourceTree>();
            map.put(r.getRid(), curr);
            for(Resource resource : rs){
                if(resource.isPage()|| resource.getRid() == r.getRid()){
                    continue;
                }
                ZipResourceTree rt = new  ZipResourceTree();
                map.put(resource.getRid(), rt);
                rt.setResource(resource);
                ZipResourceTree p = map.get(resource.getBid());
                rt.setParent(p);
                if(p!=null){
                    p.addChild(rt);
                }
            }
        }else if(r.isFile()){
            ZipResourceTree curr = new  ZipResourceTree();
            curr.setParent(root);
            root.addChild(curr);
            curr.setResource(r);
        }
    }


}
