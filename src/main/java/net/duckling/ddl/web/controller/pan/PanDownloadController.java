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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.pan.ResponseHeaderUtils;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.MeePoRevision;

@Controller
@RequirePermission(authenticated = true)
@RequestMapping("/pan/download")
public class PanDownloadController {
    private static final Logger LOG = Logger.getLogger(PanDownloadController.class);
    @Autowired
    private IPanService panService;

    private void sendPreviewDoc(String filename, long size, String remotePath, long version,
                                HttpServletRequest request, HttpServletResponse response) {
        OutputStream os = null;
        long p0 = System.currentTimeMillis();
        long tmpSize = size;
        try {
            response.setCharacterEncoding("utf-8");
            String headerValue = ResponseHeaderUtils.buildResponseHeader(request, filename, true);
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", headerValue);
            response.setContentLength((int) size);
            response.setHeader("Content-Length", tmpSize + "");
            os = response.getOutputStream();
            panService.download(PanAclUtil.getInstance(request), remotePath,version, os);
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        } catch (MeePoException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            IOUtils.closeQuietly(os);
            long p1 = System.currentTimeMillis();
            LOG.info("Download document[name:" + filename + ",size:" + tmpSize + "] use time " + (p1 - p0));
        }

    }
    @WebLog(method = "PanDownload", params = "path")
    @RequestMapping
    public void download(@RequestParam("path") String path, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(path)) {
            LOG.info("Path param should never be empty.");
            return;
        }
        try {
            PanAcl acl = PanAclUtil.getInstance(request);
            MeePoMeta meta = panService.ls(acl, path, true);
            if (meta != null) {
                long realSize  = meta.size;
                long version = PanUtils.getVersion(request);
                if(version > 0){
                    MeePoRevision tv = findTargetRevision(acl,path,version);
                    realSize = (tv == null) ? meta.size : tv.size;
                }
                sendPreviewDoc(meta.name, realSize, path, version, request, response);
            }
        } catch (MeePoException e) {
            LOG.error("", e);
        }
    }

    private MeePoRevision findTargetRevision(PanAcl acl, String path, long version) throws MeePoException{
        MeePoRevision tv = null;
        if (version != 0) {
            MeePoRevision[] revisions = panService.revisions(acl, path);
            for (MeePoRevision re : revisions) {
                if (re.version == version) {
                    tv = re;
                    break;
                }
            }
        }
        return tv;
    }

    /**
     *
     * @param path
     * @param size 的取值为 xs:32x32, s:64x64, m:128x128, l:640x480, xl:1024x768
     * @param request
     * @param response
     */
    @WebLog(method = "PanThumbnails", params = "path,size")
    @RequestMapping("/thumbnails")
    public void thumbnails(@RequestParam("path") String path, @RequestParam("size") String size,
                           HttpServletRequest request, HttpServletResponse response) {
        OutputStream os = null;
        try {
            PanAcl acl = PanAclUtil.getInstance(request);
            MeePoMeta meta = panService.ls(acl, path, true);
            response.setCharacterEncoding("utf-8");
            String headerValue = ResponseHeaderUtils.buildResponseHeader(request, meta.name, true);
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", headerValue);
            os = response.getOutputStream();
            panService.thumbnails(acl, path, size, os);
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        } catch (MeePoException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

}
