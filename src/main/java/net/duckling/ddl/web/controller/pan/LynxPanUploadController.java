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
package net.duckling.ddl.web.controller.pan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequirePermission(authenticated = true)
@RequestMapping("/pan/upload")
public class LynxPanUploadController {
    private static final Logger LOG = Logger.getLogger(LynxPanUploadController.class);

    @Autowired
    private IPanService service;
    @Autowired
    private AoneUserService aoneUserService;
    @WebLog(method = "PanUploadFiles", params = "parentRid")
    @RequestMapping(params = "func=uploadFiles", headers = { "X-File-Name" })
    public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = getFileNameFromHeader(request);
        if(StringUtil.illTitle(response, fileName)){
            return;
        }
        String uid = VWBSession.getCurrentUid(request);
        long size = request.getContentLength();
        String parentPath = decode(request.getParameter("parentRid"));
        String remotePath = getRemotePath(parentPath, fileName);
        UploadResult re = updateFile(request, request.getInputStream(), size, remotePath);
        dealResult(response, re, uid);
    }

    private String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    @WebLog(method = "PanUploadFilesIE", params = "parentRid")
    @RequestMapping(method = RequestMethod.POST, params = "func=uploadFiles")
    public void updatePageFile(@RequestParam("qqfile") MultipartFile uplFile, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String fileName = uplFile.getOriginalFilename();
        String uid = VWBSession.getCurrentUid(request);
        long size = uplFile.getSize();
        String parentPath = decode(request.getParameter("parentRid"));
        String remotePath = getRemotePath(parentPath, fileName);
        UploadResult re = updateFile(request, uplFile.getInputStream(), size, remotePath);
        dealResult(response, re, uid);
    }

    private void dealResult(HttpServletResponse response, UploadResult re, String uid) {
        JsonObject result = new JsonObject();
        if (re.isStatus()) {
            SimpleUser user = aoneUserService.getSimpleUserByUid(uid);
            PanResourceBean bean = MeePoMetaToPanBeanUtil.transfer(re.getMeta(),user);
            result.addProperty("success", true);
            result.addProperty("fileExtend", bean.getFileType());
            result.addProperty("infoURL", "");
            result.addProperty("previewURL", "");
            result.add("resource", LynxResourceUtils.getPanResourceJson(bean, uid));
            LOG.info(uid+" upload file"+ re.getMeta().restorePath);
        } else {
            result.addProperty("success", false);
            result.addProperty("message", re.getMessage());
            result.addProperty("error", re.getMessage());

        }
        JsonUtil.write(response, result);
    }

    private String getRemotePath(String parentPath, String fileName) {
        if (parentPath == null || parentPath.length() == 0 || "/".equals(parentPath)) {
            return "/" + fileName;
        } else {
            if (!parentPath.startsWith("/")) {
                parentPath = "/" + parentPath;
            }
            if (parentPath.endsWith("/")) {
                return parentPath + fileName;
            } else {
                return parentPath + "/" + fileName;
            }
        }
    }

    private UploadResult updateFile(HttpServletRequest request, InputStream in, long size, String remotePath) {
        UploadResult re = new UploadResult();
        MeePoMeta meta = null;
        try {
            if(FileSizeUtils.ONE_MB*20>size){
                meta = service.upload(PanAclUtil.getInstance(request), in, size, remotePath, false);
            }else{
                meta = service.uploadChunkedFile(PanAclUtil.getInstance(request), in, size, remotePath, false);
            }
            if (meta == null) {
                re.status = false;
                re.setMessage("上传出现问题");
            } else {
                re.setStatus(true);
                re.setMeta(meta);
            }
        } catch (MeePoException e) {
            re.setStatus(false);
            re.setMessage(e.getMessage());
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(b);
            e.printStackTrace(pw);
            pw.flush();
            re.setStackTrace(new String(b.toByteArray()));
        }
        return re;
    }

    private String getFileNameFromHeader(HttpServletRequest request) {
        String filename = request.getHeader("X-File-Name");
        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Your system doesn't support utf-8 character encode. so sucks.");
        }
        return filename;
    }

    private static class UploadResult {
        private boolean status;
        private int errorCode;
        private String message;
        private MeePoMeta meta;
        private String stackTrace;

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public MeePoMeta getMeta() {
            return meta;
        }

        public void setMeta(MeePoMeta meta) {
            this.meta = meta;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }
    }
}
