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
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PathName;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.ddl.web.vo.ErrorMsg;
import net.duckling.ddl.web.vo.VoUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件夹编辑
 * @author Brett
 *
 */
@Controller
@RequirePermission(target="team",operation="edit")
@RequestMapping("/api/v1/resource/folders")
public class FolderEditController extends AbstractController {
    //private static final Logger LOG = Logger.getLogger(FolderEditController.class);

    private static final String IF_EXISTED_RETURN = "return"; //已存在返回
    private static final String IF_EXISTED_AUTO = "auto"; //已存在序列自增

    @RequestMapping(method = RequestMethod.POST)
    public void create(@RequestParam(value="path", required=false) String path,
                       @RequestParam("title") String title,
                       @RequestParam(value="ifExisted", required=false) String ifExisted,
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        int tid = getCurrentTid();
        String uid = getCurrentUid(request);
        path = StringUtils.defaultIfBlank(path, PathName.DELIMITER);

        Resource parent = folderPathService.getResourceByPath(tid, path);
        if(parent == null){
            writeError(ErrorMsg.NOT_FOUND, response);
            return;
        }
        Resource result = null;

        List<Resource> list = resourceService.getResourceByTitle(tid, parent.getRid(), LynxConstants.TYPE_FOLDER, title, LynxConstants.STATUS_AVAILABLE);
        //是否已存在
        if(list.size()>0){
            if(IF_EXISTED_RETURN.equals(ifExisted)){
                result = list.get(0);
            }else if(IF_EXISTED_AUTO.equals(ifExisted)){
                result = createFolder(title, parent.getRid(), tid, uid);
            }
        }else{
            result = createFolder(title, parent.getRid(), tid, uid);
        }

        if(result == null){
            writeError(ErrorMsg.EXISTED, response);
            return;
        }
        result.setPath(folderPathService.getPathString(result.getRid()));
        JsonUtil.write(response, VoUtil.getResourceVo(result));
    }

    private Resource createFolder(String title, int parentRid, int tid, String uid){
        title = folderPathService.getResourceName(tid,parentRid,LynxConstants.TYPE_FOLDER,title.trim());
        Resource r = new Resource();
        r.setTid(tid);
        r.setBid(parentRid);
        r.setCreateTime(new Date());
        r.setCreator(uid);
        r.setItemType(LynxConstants.TYPE_FOLDER);
        r.setTitle(title);
        r.setLastEditor(uid);
        r.setLastEditorName(aoneUserService.getUserNameByID(uid));
        r.setLastEditTime(new Date());
        r.setStatus(LynxConstants.STATUS_AVAILABLE);
        int newRid = resourceOperateService.createFolder(r);
        return resourceService.getResource(newRid);
    }

    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private AoneUserService aoneUserService;
}
