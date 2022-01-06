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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.SupportedFileForOfficeViewer;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.PdfStatus;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.MyURLEncoder;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.MeePoRevision;

@Controller
@RequirePermission(authenticated = true)
public class PanPreviewController extends BaseController {

    private static final String PDF_UNSUPPORTED = "unsupported";
    private static final String PDF_ORIGINAL = "original_pdf";
    @Autowired
    private IPanService panService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private DucklingProperties properties;
    @Autowired
    private AoneUserService aoneUserService;

    protected static final Logger LOG = Logger.getLogger(PanPreviewController.class);

    @WebLog(method = "panpreview", params = "rid,path")
    @RequestMapping("/pan/preview")
    public ModelAndView dealFile(@RequestParam("path") String path, HttpServletRequest request) throws MeePoException,
            UnsupportedEncodingException {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.DASHBOARD);
        ModelAndView mv = layout(".pan.preview", context, "/jsp/pan/pan_preview.jsp");
        PanAcl acl = PanAclUtil.getInstance(request);
        MeePoMeta mm = panService.ls(acl, path, true);
        if (mm == null) {
            LOG.error("you access file not exist");
            mv.addObject("fileNotExist", "true");
            return mv;
        }
        long version = PanUtils.getVersion(request);
        MeePoRevision tv = findTargetRevision(acl,path,version);
        long realSize = (tv == null) ? mm.size : tv.size;
        mv.addObject("sizeShort", FileSizeUtils.getFileSize(realSize));
        mv.addObject("fileExtend", getFileExtend(mm.name, realSize));
        String strFilename = mm.name;
        int index = strFilename.lastIndexOf('.');
        fileOnlineShow(mv, strFilename, index);
        mv.addObject("enableDConvert", true);
        mv.addObject("filename", mm.name);
        mv.addObject("version", version);
        mv.addObject("uid", acl.getUid());
        mv.addObject("isOffice", SupportedFileForOfficeViewer.isOfficeFile(mm.name));
        mv.addObject("isPreview", isPreview(mm.name, mm.size));
        String verStr = "";
        if(version>0){
            verStr = "&version="+version;
        }
        mv.addObject("clbPreviewUrl", properties.getProperty("duckling.baseURL") + "/wopi/p?remotePath=" + MyURLEncoder.encodeURIComponent(path) +verStr);
        mv.addObject("itemType", "DFile");
        mv.addObject("remotePath", URLEncoder.encode(path, "UTF-8"));
        mv.addObject("downloadURL", getDownloadURL(path, version));
        SimpleUser user = aoneUserService.getSimpleUserByUid(acl.getUid());
        mv.addObject("resource", MeePoMetaToPanBeanUtil.transfer(mm, user));
        LOG.info("uid:" + acl.getUid() + " preview resource remotePath=" + path);
        return mv;
    }

    private String getDownloadURL(String path, long version) throws UnsupportedEncodingException {
        return urlGenerator.getURL(UrlPatterns.PAN_DOWNLOAD, URLEncoder.encode(path, "UTF-8"), "version=" + version);
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

    private boolean isPreview(String name, long size) {
        if (name.toLowerCase().endsWith("pdf")) {
            return true;
        }
        return SupportedFileForOfficeViewer.isSupportedFile(name) && size < 52428800;
    }

    public void fileOnlineShow(ModelAndView mv, String strFilename, int index) {
        String strFileType = null;
        if (index != -1 && strFilename.length() > (index + 1)) {
            strFileType = strFilename.substring(index + 1);
        }
        if (null != strFileType) {
            strFileType = strFileType.toLowerCase();
            String pdfstatus = PdfStatus.SOURCE_NOT_FOUND.toString();// 表示该类型文档的PDF不存在
            boolean supported = SupportedFileForOfficeViewer.isSupported(strFileType);
            if ("pdf".equals(strFileType)) {
                pdfstatus = PDF_ORIGINAL;
            } else if (supported) {
                pdfstatus = PDF_UNSUPPORTED;
            } else {
                pdfstatus = PDF_UNSUPPORTED;// 表示不支持该类型文档的在线显示
            }
            if (pdfstatus == PDF_UNSUPPORTED && isSupportedFileType(strFileType)) { // 剔除图片的无法转换信息
                strFileType = "img";
            }
            mv.addObject("strFileType", PlainTextHelper.convert2BrushClassFileType(strFileType));
            mv.addObject("pdfstatus", pdfstatus);
            mv.addObject("supported", supported);
        }
    }

    private boolean isSupportedFileType(String fileType) {
        if (null == fileType || "".equals(fileType)) {
            return false;
        }
        if (SupportedFileForOfficeViewer.isSupported(fileType)) {
            return true;
        }
        if (File.isPictureFileTypeForSearch(fileType)) {
            return true;
        }
        return false;
    }

    private String getFileExtend(String filename, long size) {
        if (MimeType.isImage(filename)) {
            return "IMAGE";
        } else if (PlainTextHelper.isSupported(MimeType.getSuffix(filename))
                   && size < LynxConstants.MAXFILESIZE_CODEREVIEW) {// 文件超过给定大小时不直接显示
            return "TEXT";
        }
        return "FILE";
    }

    @WebLog(method = "PanPdfpreview", params = "rid,path")
    @RequestMapping("/pan/pdfpreview")
    public ModelAndView onlineViewer(@RequestParam("path") String path, HttpServletRequest request)
            throws MeePoException, UnsupportedEncodingException {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/aone/file/onlineViewer.jsp");
        long version = PanUtils.getVersion(request);
        PanAcl acl = PanAclUtil.getInstance(request);
        MeePoMeta mm = panService.ls(acl, path, true);
        String strFilename = mm.name;
        int index = strFilename.lastIndexOf('.');
        if (index != -1 && strFilename.length() > (index + 1)) {
            String strFileType = strFilename.substring(index + 1);
            if ("pdf".equals(strFileType.toLowerCase())) {
                mv.addObject("pdfviewerURL", getDownloadURL(path, version));
            }
            mv.addObject("strFileType", "pdf");
        }
        mv.addObject("offPresentationMode", request.getParameter("offPresentationMode"));
        mv.addObject("pageTitle", mm.name);
        mv.addObject("backURL", urlGenerator.getURL(1, "file", path, ""));
        return mv;
    }

}
