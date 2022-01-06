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
package net.duckling.ddl.web.api.rest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PathName;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.ddl.web.vo.ErrorMsg;
import net.duckling.ddl.web.vo.VoUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequirePermission(target="team",operation="edit")
@RequestMapping("/api/v1/resource")
public class FileEditController extends AbstractController {
    private static final Logger LOG = Logger.getLogger(FileEditController.class);
    private static final String IF_EXISTED_UPDATE = "update";

    @RequestMapping(value="/files", method = RequestMethod.POST)
    public void upload(@RequestParam(value="path", required=false) String path,
                       @RequestParam("file") MultipartFile file,
                       @RequestParam(value="ifExisted", required=false) String ifExisted,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uid = getCurrentUid(request);
        int tid = getCurrentTid();
        path = StringUtils.defaultIfBlank(path, PathName.DELIMITER);

        Resource parent = folderPathService.getResourceByPath(tid, path);
        if(parent == null){
            writeError(ErrorMsg.NOT_FOUND, response);
            return;
        }

        List<Resource> list = resourceService.getResourceByTitle(tid, parent.getRid(), LynxConstants.TYPE_FILE, file.getOriginalFilename());
        if(list.size()>0 && !IF_EXISTED_UPDATE.equals(ifExisted)){
            writeError(ErrorMsg.EXISTED, response);
            return;
        }

        FileVersion fv = null;
        try {
            fv = resourceOperateService.upload(uid, super.getCurrentTid(),
                                               parent.getRid(), file.getOriginalFilename(), file.getSize(), file.getInputStream());
        } catch (NoEnoughSpaceException e) {
            writeError(ErrorMsg.NO_ENOUGH_SPACE, response);
            return;
        }

        Resource resource = resourceService.getResource(fv.getRid());
        resource.setPath(folderPathService.getPathString(resource.getRid()));
        JsonUtil.write(response, VoUtil.getResourceVo(resource));
    }

    @RequestMapping(value="/fileShared", method = RequestMethod.POST)
    public void fileShared(@RequestParam(value="path", required=false) String path,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam(value="ifExisted", required=false) String ifExisted,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uid = getCurrentUid(request);
        int tid = getCurrentTid();
        path = StringUtils.defaultIfBlank(path, PathName.DELIMITER);

        LOG.info("file shared start... {uid:" + uid +",tid:" + tid +",path:" + path +",fileName:" + file.getOriginalFilename() +",ifExisted:" + ifExisted +"}");

        Resource parent = folderPathService.getResourceByPath(tid, path);
        if(parent == null){
            LOG.error("path not found. {tid:"+ tid +", path:" + path +"}");
            writeError(ErrorMsg.NOT_FOUND, response);
            return;
        }

        List<Resource> list = resourceService.getResourceByTitle(tid, parent.getRid(), LynxConstants.TYPE_FILE, file.getOriginalFilename());
        if(list.size()>0 && !IF_EXISTED_UPDATE.equals(ifExisted)){
            LOG.error("file existed. {ifExisted:" + ifExisted +",fileName:" + file.getOriginalFilename() +"}");
            writeError(ErrorMsg.EXISTED, response);
            return;
        }

        FileVersion fv = null;
        try {
            fv = resourceOperateService.upload(uid, getCurrentTid(),
                                               parent.getRid(), file.getOriginalFilename(), file.getSize(), file.getInputStream());
        } catch (NoEnoughSpaceException e) {
            LOG.error("no enough space. {tid:"+ tid +",size:" + file.getSize() +",fileName:" + file.getOriginalFilename() +"}");
            writeError(ErrorMsg.NO_ENOUGH_SPACE, response);
            return;
        }

        ShareResource sr = shareResourceService.get(fv.getRid());
        if(sr==null){
            sr = createShareResource(fv.getRid(), uid);
        }else{
            sr.setLastEditor(uid);
            sr.setLastEditTime(new Date());
            shareResourceService.update(sr);
        }
        sr.setTitle(fv.getTitle());
        sr.setShareUrl(sr.generateShareUrl(urlGenerator));

        LOG.info("file uploaded and shared successfully. {tid:"+ tid +",path:" + path +",title:" + fv.getTitle() +"}");
        JsonUtil.write(response, VoUtil.getShareResourceVo(sr));
    }

    private ShareResource createShareResource(int rid, String uid) {
        ShareResource sr;
        sr = new ShareResource();
        sr.setRid(rid);
        sr.setTid(getCurrentTid());
        sr.setShareUid(uid);
        sr.setCreateTime(new Date());
        sr.setLastEditor(uid);
        sr.setLastEditTime(new Date());
        shareResourceService.add(sr);
        return sr;
    }

    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private ShareResourceService shareResourceService;
    @Autowired
    private FolderPathService folderPathService;
}
