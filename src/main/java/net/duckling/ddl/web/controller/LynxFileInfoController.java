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

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.export.FileNotFoundException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.itemtypemap.ItemTypeMappingService;
import net.duckling.ddl.service.itemtypemap.ItemTypemapping;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.share.ShareFileAccessService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.util.WebParamUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.clb.api.SupportedFileFormatForOnLineViewer;

/**
 * @date 2011-6-1
 * @author Clive Lee
 */

@Controller
@RequestMapping("/{teamCode}/file/{fid}")
@RequirePermission(target = "team", operation = "view")
public class LynxFileInfoController extends BaseController {
    @Autowired
    private AoneMailService aonemailService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private ShareFileAccessService shareFileAccessService;
    @Autowired
    private BrowseLogService browseLogService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private ItemTypeMappingService itemTypeMappingService;

    private static final Logger LOG =
            Logger.getLogger(LynxFileInfoController.class);
    private boolean validateDeleteAuth(VWBContext context, int rid,
                                       String resourceType) {
        String u = context.getCurrentUID();
        if (authorityService.teamAccessability(
                VWBContext.getCurrentTid(),
                VWBSession.findSession(context.getHttpRequest()),
                AuthorityService.ADMIN)) {
            return true;
        } else {
            Resource r = resourceService.getResource(
                rid, VWBContext.getCurrentTid());
            if (r != null && u.equals(r.getCreator())) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping
    @WebLog(method = "showFileInfo", params = "fid")
    public ModelAndView viewFile(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @PathVariable("fid") int fid) {
        int tid = VWBContext.getCurrentTid();
        ItemTypemapping i = itemTypeMappingService
                .getItemTypeMapping(tid, fid, LynxConstants.TYPE_FILE);
        if (i == null) {
            notFound(request, response, true);
        }
        ModelAndView mv = new ModelAndView(
            new RedirectView(
                urlGenerator.getURL(
                    tid, UrlPatterns.T_VIEW_R, i.getRid() + "",null)));
        return mv;
    }

    private boolean isAdmin(VWBContext context, int tid) {
        String user = context.getCurrentUID();
        if (user == null || user.length() == 0) {
            return false;
        }
        return Team.AUTH_ADMIN.equals(
            authorityService.getTeamAuthority(tid, user));
    }

    private String getFileExtend(String filename, long size) {
        if (MimeType.isImage(filename)) {
            return "IMAGE";
        } else if (PlainTextHelper.isSupported(MimeType.getSuffix(filename))
                   && size < LynxConstants.MAXFILESIZE_CODEREVIEW) {
            // 文件超过给定大小时不直接显示
            return "TEXT";
        }
        return "FILE";
    }

    private int getFileId(HttpServletRequest request, String fidStr,int tid) {
        int result = 0;
        try {
            result = Integer.parseInt(fidStr);
        } catch (Exception e) {
            LOG.error("非法的文件请求URL:" + request.getRequestURI(), e);
            VWBContext context =
                    VWBContext.createContext(request, UrlPatterns.T_FILE);
            String teamHomeURL =
                    urlGenerator.getURL(tid,UrlPatterns.T_TEAM_HOME, null,null);
            throw new FileNotFoundException(
                0, context.getSite().getSiteName(), teamHomeURL);
        }
        return result;
    }

    private int getCurrentVersion(HttpServletRequest request) {
        String versionStr = request.getParameter("version");
        int version = 0;
        try {
            if (versionStr != null) {
                version = Integer.parseInt(versionStr);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Could not parse parameter version, use latest version.");
        }
        return version;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params = "func=deleteFileRef")
    @RequirePermission(target = "team", operation = "edit")
    public void deleteFileRef(HttpServletRequest request,
                              HttpServletResponse response,
                              @PathVariable("rid") int rid) {
        int pid = Integer.parseInt(request.getParameter("pid"));
        fileVersionService.deleteFileAndPageRefer(
            rid, pid, VWBContext.getCurrentTid());
        JsonObject object = new JsonObject();
        object.addProperty("docid", rid);
        JsonUtil.write(response, object);
    }

    @RequestMapping(params = "func=removeValidate")
    public void removeValidate(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable("fid") int fid) {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_FILE);
        JsonObject obj = new JsonObject();
        if (!validateDeleteAuth(context, fid, LynxConstants.TYPE_FILE)) {
            obj.addProperty("status", false);
        } else {
            obj.addProperty("status", true);
        }
        JsonUtil.write(response, obj);
    }

    /**
     * 恢复文件版本
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(params = "func=recoverFileVersion")
    public void recoverFileVersion(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @PathVariable("fid") int fid)
            throws IOException {
        int version = Integer.parseInt(request.getParameter("version"));
        int tid = Integer.parseInt(request.getParameter("tid"));
        resourceOperateService.recoverFileVersion(
            tid, fid, version,VWBSession.getCurrentUid(request));
        response.sendRedirect(urlGenerator.getURL(
            tid,UrlPatterns.T_FILE, fid + "", null));
    }

    @RequestMapping(params = "func=moveToTrash")
    @RequirePermission(target = "team", operation = "edit")
    public ModelAndView moveToTrash(HttpServletRequest request,
                                    @PathVariable("rid") int rid)
            throws UnsupportedEncodingException {
        VWBContext context = VWBContext.createContext(request,
                                                      UrlPatterns.T_FILE);
        int tid = context.getTid();
        FileVersion dfileVersion =
                fileVersionService.getLatestFileVersion(rid, tid);
        fileVersionService.deleteRefer(rid, tid);
        resourceOperateService.deleteResource(
            tid, dfileVersion.getRid(),context.getCurrentUID());
        int bid = WebParamUtil.getIntegerValue(request, "bid");
        ModelAndView mv = null;
        if (bid == 0) {
            String url = urlGenerator.getURL(
                tid, UrlPatterns.T_FILE, rid + "", "func=removedSuccess");
            mv = new ModelAndView(new RedirectView(url));
            mv.addObject("fileName",
                         URLEncoder.encode(dfileVersion.getTitle(), "UTF-8"));
        } else {
            String url = urlGenerator.getURL(
                tid, UrlPatterns.T_BUNDLE, bid +"", null);
            mv = new ModelAndView(new RedirectView(url));
        }
        return mv;
    }

    @RequestMapping(params = "func=removedSuccess")
    @RequirePermission(target = "team", operation = "edit")
    public ModelAndView removedSuccess(HttpServletRequest request)
            throws UnsupportedEncodingException {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        String fileName =
                URLDecoder.decode(request.getParameter("fileName"), "UTF-8");
        ModelAndView mv =
                layout(ELayout.LYNX_MAIN, context,
                       "/jsp/aone/file/removeFileSuccess.jsp");
        mv.addObject("fileName", fileName);
        return mv;
    }

    @RequestMapping(params = "func=shareExistFile")
    public ModelAndView shareExistFile(HttpServletRequest request,
                                       @PathVariable("rid") int rid) {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        String uid = context.getCurrentUID();
        int tid = context.getSite().getId();
        FileVersion dfileVersion =
                fileVersionService.getLatestFileVersion(rid, tid);
        Resource r = resourceService.getResource(rid);
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context,
                                 "/jsp/aone/file/shareExistFile.jsp");
        mv.addObject("uid", uid);
        mv.addObject("name", context.getCurrentUserName());
        mv.addObject("fileVersion", dfileVersion);
        mv.addObject("file", r);
        return mv;
    }

    @RequestMapping(params = "func=submitShareExistFile")
    public ModelAndView submitShareFile(HttpServletRequest request)
            throws UnsupportedEncodingException {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        // Site site = context.getSite();
        int tid = context.getTid();
        String uid = context.getCurrentUID();
        String userName = context.getCurrentUserName();
        int validOfDays = Integer.parseInt(request.getParameter("validOfDays"));
        String clbIds = request.getParameter("clbId");
        String fileNames = request.getParameter("fileName");
        int fid = Integer.parseInt(request.getParameter("fid"));
        String message = request.getParameter("message");
        String encodeURL = shareFileAccessService.getPublicFileURL(
            VWBContext.getCurrentTid(), Integer.parseInt(clbIds),
            fid, validOfDays, uid);
        String fileURLs = urlGenerator.getAbsoluteURL(
            tid, UrlPatterns.DIRECT, encodeURL, null);
        String friendEmails = request.getParameter("targetEmails");
        String[] shareMails = friendEmails.split(",");
        for (int i = 0; i < shareMails.length; i++) {
            aonemailService.sendAccessFileMail(
                new String[] { fileNames }, new String[] { fileURLs },
                userName, shareMails[i], message);
        }
        aonemailService.sendShareSuccessMailWithoutActivation(
            uid, userName, new String[] { fileURLs },
            new String[] { fileNames }, shareMails);
        String url = urlGenerator.getURL(
            tid,UrlPatterns.T_FILE, fid + "", "");
        ModelAndView mv = new ModelAndView(new RedirectView(url));
        mv.addObject("fid", fid);
        mv.addObject("fileName",
                     java.net.URLEncoder.encode(fileNames, "UTF-8"));
        mv.addObject("fileURL", fileURLs);
        mv.addObject("func", "shareExistFileSuccess");
        return mv;
    }

    @RequestMapping(params = "func=shareExistFileSuccess")
    public ModelAndView shareExistFileSuccess(HttpServletRequest request)
            throws UnsupportedEncodingException {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        ModelAndView mv = layout(ELayout.LYNX_MAIN,
                                 context,
                                 "/jsp/aone/file/shareExistFileSuccess.jsp");
        mv.addObject("fileName",
                     java.net.URLDecoder.decode(
                         request.getParameter("fileName"), "UTF-8"));
        mv.addObject("fid", request.getParameter("fid"));
        mv.addObject("fileURL", request.getParameter("fileURL"));
        return mv;
    }

    @RequestMapping(params = "func=onlineViewer")
    public ModelAndView onlineViewer(HttpServletRequest request,
                                     @PathVariable("rid") int rid) {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        ModelAndView mv = layout(ELayout.LYNX_MAIN,
                                 context, "/jsp/aone/file/onlineViewer.jsp");
        int tid = context.getSite().getId();

        Resource dfile = resourceService.getResource(rid);
        if (dfile==null) {
            return layout(ELayout.LYNX_MAIN,
                          context, "/jsp/aone/file/fileRemoved.jsp");
        }
        int version = getCurrentVersion(request);
        FileVersion currentVersion;
        if (version > 0) {
            currentVersion = fileVersionService.getFileVersion(rid, tid, version);
        } else {
            currentVersion = fileVersionService.getLatestFileVersion(rid, tid);
        }
        if (version > 0) {
            mv.addObject("downloadURL",
                         urlGenerator.getURL(tid, "download", Integer.toString(rid),
                                             "type=doc&version=" + version));
        } else {
            mv.addObject("downloadURL", urlGenerator.getURL(
                tid, "download", Integer.toString(rid), "type=doc"));
        }

        String strFilename = currentVersion.getTitle();
        int index = strFilename.lastIndexOf('.');
        if (index != -1 && strFilename.length() > (index + 1)) {
            String strFileType = strFilename.substring(index + 1);
            if ("pdf".equals(strFileType.toLowerCase())) {
                // String str=(context.getSite().getURL("download",
                // Integer.toString(fid),
                // "type=doc&version="+currentVersion.getVersion()));
                mv.addObject("pdfviewerURL", urlGenerator.getURL(
                    tid, "download", Integer.toString(rid),
                    "type=doc&version="+ currentVersion.getVersion()));
            } else {
                mv.addObject("pdfviewerURL", urlGenerator.getURL(
                    tid, "download", Integer.toString(rid),
                    "type=pdf&version="+ currentVersion.getVersion()));
            }
            mv.addObject("strFileType", "pdf");
        }
        mv.addObject("pageTitle", dfile.getTitle());
        mv.addObject("backURL",
                     urlGenerator.getURL(tid, "file", Integer.toString(rid), ""));
        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(
            tid, rid, context.getCurrentUID(), context.getCurrentUserName(),
            LynxConstants.TYPE_FILE);
        return mv;
    }

    @RequestMapping(params = "func=pdfTransform")
    public void sendPdfTransformEvent(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @PathVariable("fid") int rid) {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        int tid = context.getSite().getId();
        int version = getCurrentVersion(request);
        FileVersion currentVersion;
        if (version > 0) {
            currentVersion = fileVersionService.getFileVersion(rid, tid, version);
        } else {
            currentVersion = fileVersionService.getLatestFileVersion(rid, tid);
        }
        resourceOperateService.sendPdfTransformEvent(
            currentVersion.getClbId(), "" + currentVersion.getClbVersion());
        JsonUtil.write(response, new JsonObject());
    }

    private boolean isSupportedFileType(String fileType) {
        if (null == fileType || "".equals(fileType)) {
            return false;
        }
        if (SupportedFileFormatForOnLineViewer.isSupported(fileType)) {
            return true;
        }
        if (File.isPictureFileTypeForSearch(fileType)) {
            return true;
        }
        return false;
    }


    @RequestMapping(params = "func=shareFileToOthers")
    public void shareFileToOthers(HttpServletRequest request,
                                  HttpServletResponse response) {
        VWBContext context =
                VWBContext.createContext(request, UrlPatterns.T_FILE);
        int tid = context.getTid();
        String uid = context.getCurrentUID();
        String userName = context.getCurrentUserName();
        int validOfDays = Integer.parseInt(request.getParameter("validOfDays"));
        int rid=Integer.parseInt(request.getParameter("rid"));
        Resource resource = resourceService.getResource(rid);
        String fileNames = resource.getTitle();
        FileVersion fileVersion = fileVersionService.getFileVersion(
            rid, tid, resource.getLastVersion());
        int clbIds=fileVersion.getClbId();
        String message = request.getParameter("message");
        String encodeURL = shareFileAccessService.getPublicFileURL(
            VWBContext.getCurrentTid(),clbIds, rid, validOfDays, uid);
        String fileURLs = urlGenerator.getAbsoluteURL(
            tid, UrlPatterns.DIRECT, encodeURL, null);
        String friendEmails = request.getParameter("targetEmails");
        String[] shareMails = friendEmails.split(",");
        for (int i = 0; i < shareMails.length; i++) {
            aonemailService.sendAccessFileMail(
                new String[] { fileNames }, new String[] { fileURLs },
                userName, shareMails[i], message);
        }
        aonemailService.sendShareSuccessMailWithoutActivation(
            uid, userName, new String[] { fileURLs },
            new String[] { fileNames },shareMails);

        JsonObject object = new JsonObject();
        object.addProperty("status", "success");
        object.addProperty("itemType", resource.getItemType());
        object.addProperty("fileURL", fileURLs);
        object.addProperty("friendEmails", friendEmails);
        object.addProperty("fileName", fileNames);
        JsonUtil.write(response, object);
    }

}
