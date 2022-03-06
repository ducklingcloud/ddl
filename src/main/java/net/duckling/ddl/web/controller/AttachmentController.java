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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.SupportedFileForOfficeViewer;
import net.duckling.ddl.service.authenticate.AuthenticationService;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.util.Base64;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.PdfStatus;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.web.bean.AuthorizationCode;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// import cn.cnic.cerc.dlog.client.DLogClient;
// import cn.cnic.cerc.dlog.client.WebLog;
// import cn.cnic.cerc.dlog.client.WebLogResolver;
// import cn.cnic.cerc.dlog.domain.LogBean;
import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.UserInfo;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;

/**
 * 邮箱附件预览
 */
@Controller
@RequestMapping("/{teamCode}/preview/{rid}")
public class AttachmentController extends BaseController{
    private static final String PDF_UNSUPPORTED = "unsupported";
    private static final String PDF_ORIGINAL = "original_pdf";
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IGridService gridService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private BrowseLogService browseLogService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AuthorizationCodeService authorizationCodeService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private DucklingProperties properties;

    // @Autowired
    // private WebLogResolver webLogResolver;
    // private DLogClient  dLogClient ;

    protected static final Logger LOG = Logger.getLogger(AttachmentController.class);

    @RequestMapping
    public ModelAndView display(HttpServletRequest request,HttpServletResponse response, @PathVariable("rid")Integer rid) throws IOException, ServletException {
        String redirect = request.getParameter("redirect");
        if(StringUtils.isEmpty(redirect)){
            String url = request.getRequestURL().toString() +"?"+request.getQueryString()+ "&redirect=redirect";
            request.setAttribute("url", url);
            request.setAttribute("noHref", "no");
            request.getRequestDispatcher("/jsp/aone/hash/dealHashRequest.jsp").forward(request, response);
            return null;
        }

        if(checkUserConflict(request,response)){
            return null;
        }

        Resource resource = resourceService.getResource(rid);
        if(resource==null||resource.getTid()!=VWBContext.getCurrentTid()||resource.isDelete()){
            ModelAndView m = dealResourceRemove(request, resource);
            if(m!=null){
                return m;
            }
            notFound(request, response, true);
            return null;
        }
        ModelAndView mv = null;
        if(resource.isFile()){
            mv = dealFile(request,response ,resource);
        }else{
            notFound(request, response, true);
            return null;
        }
        mv.addObject("pageType", "list");
        if(resource.isAvailable()){
            mv.addObject("resourePath", getParentPath(resource));
        }
        mv.addObject("teamUrl", urlGenerator.getURL(resource.getTid(),UrlPatterns.T_LIST, null,null));
        mv.addObject("teamHome",urlGenerator.getURL(resource.getTid(),UrlPatterns.T_TEAM_HOME, null,null));
        addMyTeam(request, mv);
        return mv;
    }

    /**
     * 检查DDL是否已经有用户登录
     * @param request
     * @return
     * @throws IOException
     * @throws ServletException
     */
    private boolean checkUserConflict(HttpServletRequest request,HttpServletResponse resp) throws IOException, ServletException{
        String code = request.getParameter("code");
        AuthorizationCode ac = authorizationCodeService.getCode(code);
        String uid = VWBSession.getCurrentUid(request);
        int tid = VWBContext.getCurrentTid();
        boolean userConflict = true;
        if(ac!=null&&ac.isAvailable()){
            if(!ac.getUid().equals(uid)){
                updateLoginUser(request);
            }
            userConflict = false;
        }else{
            if(VWBSession.findSession(request).isAuthenticated()){
                String auth = authorityService.getTeamAuthority(tid, uid);
                if("forbid".equals(auth)){
                    String url = urlGenerator.getAbsoluteURL(UrlPatterns.LOGIN, null, null)+"?"+Attributes.REQUEST_URL+"="+URLEncoder.encode(request.getRequestURL().toString(), "utf-8");
                    VWBSession.findSession(request).setAttribute(Attributes.REQUEST_URL,url);
                    request.setAttribute("logout", "/system/logout");
                    request.getRequestDispatcher("/jsp/aone/tag/resousePreviewError.jsp").forward(request, resp);
                }else{
                    userConflict = false;
                }
            }else{
                VWBSession.findSession(request).setAttribute(Attributes.REQUEST_URL,request.getRequestURL().toString());
                resp.sendRedirect("/system/login");
            }

        }
        return userConflict;
    }

    /**
     * 更新DDL用户登录
     * @param request
     * @return
     */
    private void updateLoginUser(HttpServletRequest request){
        //注销当前ddl登录用户
        authenticationService.invalidateSession(request);
        String code = request.getParameter("code");
        AuthorizationCode ac = authorizationCodeService.getCode(code);
        try {
            AccessToken token = authorizationCodeService.umtAccessTokenValidate(ac.getAccessToken());
            UserInfo u = token.getUserInfo();
            UserPrincipal user = new UserPrincipal(u.getCstnetId(),getUserName(u.getCstnetId(),u),u.getCstnetId(),u.getType());
            List<Principal> set = new ArrayList<Principal>();
            set.add(user);

            VWBSession vwbsession = VWBSession.findSession(request);
            vwbsession.setPrincipals(set);
            request.getSession().setAttribute(Attributes.UMT_ACCESS_TOKEN, token.getAccessToken());
            LOG.info("Successfully authenticated user:"+u.getCstnetId()+";access_token:"+token);
        } catch (OAuthProblemException e) {
            LOG.error("", e);
        }
    }

    private String getUserName(String uid,UserInfo u){
        String name = aoneUserService.getUserNameByID(uid);
        if(StringUtils.isEmpty(name)){
            name = u.getTrueName();
        }
        if(StringUtils.isEmpty(name)){
            name = getNameFromEmail(uid);
        }
        return name;
    }

    private String getNameFromEmail(String uid){
        int i = uid.indexOf("@");
        if(i>0){
            return uid.substring(0, i);
        }else{
            return uid;
        }
    }

    /**
     * resource已删除通知页面
     * @param request
     * @param resource
     * @return
     */
    private ModelAndView dealResourceRemove(HttpServletRequest request,Resource resource){
        if(resource!=null&&LynxConstants.STATUS_DELETE.equals(resource.getStatus())){
            String uid = VWBSession.getCurrentUid(request);
            VWBContext context = VWBContext.createContext(request,UrlPatterns.T_PAGE, resource.getRid(),LynxConstants.TYPE_PAGE);
            ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/errors/resourceRemove.jsp");
            boolean recoverFlag = false;
            if ((resource.getCreator().equals(uid) || isAdmin(uid, VWBContext.getCurrentTid()))) {
                if(resource.isFolder()){
                    recoverFlag = false;
                    //                  ResourceDirectoryTrash  t = resourceDirectoryTrashService.getResoourceTrash(resource.getRid());
                    //                  if(t!=null){
                    //                      recoverFlag = true;
                    //                  }
                }else{
                    recoverFlag = true;
                }
            }
            mv.addObject("recoverFlag", recoverFlag);
            mv.addObject("resource", resource);
            mv.addObject("teamCode", VWBContext.getCurrentTeamCode());
            return mv;
        }else{
            return null;
        }
    }

    /**
     * 文件浏览处理
     * @param request
     * @param response
     * @param resource
     * @return
     */
    //@WebLog(method = "preview", params = "rid,from")
    public ModelAndView dealFile(HttpServletRequest request, HttpServletResponse response, Resource resource) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE, resource.getRid(), LynxConstants.TYPE_FILE);
        String uid = context.getCurrentUID();
        int tid = context.getSite().getId();
        int rid = resource.getRid();
        ModelAndView mv = layout(".aone.attachment", context, "/jsp/aone/tag/attachmentView.jsp");
        FileVersion currentVersion =getFileVersion(request,rid, tid);

        mv.addObject("resource", resource);
        Set<String> starmark = resource.getMarkedUserSet();
        if (null != starmark && starmark.contains(context.getCurrentUID())) {
            mv.addObject("starmark", true);
        } else {
            mv.addObject("starmark", false);
        }
        currentVersion.setEditor(aoneUserService.getUserNameByID(currentVersion.getEditor()));
        mv.addObject("deleteFileURL",urlGenerator.getURL(tid,UrlPatterns.T_FILE, rid + "", "func=moveToTrash&bid=0"));
        mv.addObject("validateURL", urlGenerator.getURL(tid,UrlPatterns.T_FILE, rid + "", "func=removeValidate"));
        mv.addObject("cid",  currentVersion.getClbId());
        mv.addObject("curVersion", currentVersion);
        mv.addObject("latestVersion", currentVersion.getVersion());
        mv.addObject("sizeShort", FileSizeUtils.getFileSize(currentVersion.getSize()));
        mv.addObject("fileExtend", getFileExtend(currentVersion.getTitle(), currentVersion.getSize()));
        String downType = "type=doc";
        downType+="&version=" + currentVersion.getVersion();
        mv.addObject("downloadURL", urlGenerator.getURL(tid, "download", Integer.toString(rid), downType));
        gridService.clickItem(uid, tid, rid, LynxConstants.TYPE_FILE);
        String strFilename = currentVersion.getTitle();
        int index = strFilename.lastIndexOf('.');
        fileOnlineShow(mv, currentVersion, strFilename, index);
        browseLogService.resourceVisited(tid, rid, uid, context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        String enableDConvert = context.getContainer().getProperty(KeyConstants.DCONVERT_SERVICE_ENABLE);
        mv.addObject("enableDConvert", Boolean.valueOf(enableDConvert));
        mv.addObject("uid", context.getCurrentUID());
        mv.addObject("isOffice", SupportedFileForOfficeViewer.isOfficeFile(currentVersion.getTitle()));
        mv.addObject("isPreview", isPreview(currentVersion));
        mv.addObject("clbPreviewUrl", properties.get("duckling.clb.url")+"/wopi/p?accessToken="+getClbToken(currentVersion.getClbId(), currentVersion.getClbVersion(),properties));
        LOG.info("uid:"+uid+" preview resource rid="+rid);
        addPreviewLog(request);
        return mv;
    }

    private void addPreviewLog(HttpServletRequest request){
        // try{
        //     DLogClient client = getDLogClient();
        //     LogBean aLog = new LogBean();
        //     aLog.setHost(request.getLocalAddr());
        //     aLog.setMethod("preview");
        //     SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //     String time = format.format(new Date());
        //     aLog.setTime(time);
        //     Map<String,String> params = webLogResolver.buildFixedParameters(request);
        //     params.put("from", request.getParameter("from"));
        //     aLog.setOption(params);
        //     LinkedList<LogBean> ls = new LinkedList<LogBean>();
        //     ls.add(aLog);
        //     client.sendLogData(client.prepareData(ls));
        // }catch(Exception e){
        //     LOG.error("", e);
        // }
    }

    private boolean isPreview(FileVersion fv){
        if(fv.getTitle().toLowerCase().endsWith("pdf")){
            return true;
        }
        return SupportedFileForOfficeViewer.isSupportedFile(fv.getTitle())&&fv.getSize()<52428800;
    }


    private FileVersion getFileVersion(HttpServletRequest request,int rid,int tid){
        String from = request.getParameter("from");
        if("web".equals(from)){
            String version = request.getParameter("version");
            if(StringUtils.isEmpty(version)){
                return fileVersionService.getLatestFileVersion(rid, tid);
            }else{
                int ver = Integer.parseInt(version);
                return fileVersionService.getFileVersion(rid, tid, ver);

            }
        }else{
            //邮件预览直接预览版本1
            return fileVersionService.getFileVersion(rid, tid, 1);
        }
    }


    private List<Resource> getParentPath(Resource resource){
        int pRid = resource.getBid();
        if(pRid == 0){
            return Collections.emptyList();
        }
        List<Resource> result = folderPathService.getResourcePath(pRid);
        if(result == null){
            return Collections.emptyList();
        }
        return result;
    }

    public void fileOnlineShow(ModelAndView mv, FileVersion currentVersion, String strFilename, int index) {
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
                pdfstatus = resourceOperateService.queryPdfStatus(currentVersion.getClbId(), "" + currentVersion.getClbVersion());
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

    private boolean isAdmin(String user,int tid){
        if(user==null||user.length()==0){
            return false;
        }
        return Team.AUTH_ADMIN.equals( authorityService.getTeamAuthority(tid, user));
    }

    private ModelAndView addMyTeam(HttpServletRequest request, ModelAndView mv){
        VWBContext context = getVWBContext(request);
        int myTeamId = teamService.getPersonalTeamNoCreate(context.getCurrentUID());
        String myTeamCode = teamService.getTeamNameFromEmail(context.getCurrentUID());
        mv.addObject("myTeamId",myTeamId);
        mv.addObject("myTeamCode",myTeamCode);
        return mv;
    }
    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request, UrlPatterns.T_VIEW_FILE);
    }

    public static String getClbToken(int docId,int version,Properties properties){
        HttpClient client = getHttpClient();
        PostMethod method = new PostMethod(getClbTokenUrl(properties));
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        method.addParameter("appname", properties.getProperty("duckling.clb.aone.user"));
        method.addParameter("docid", docId+"");
        method.addParameter("version", version+"");
        try {
            method.addParameter("password",Base64.encodeBytes(properties.getProperty("duckling.clb.aone.password").getBytes("utf-8")));
        } catch (IllegalArgumentException | UnsupportedEncodingException e) {

        }
        try {
            int status  = client.executeMethod(method);
            String responseString = null;
            if(status<400){
                responseString = method.getResponseBodyAsString();
                JsonObject j = new Gson().fromJson(responseString, JsonObject.class);
                String st = j.getAsJsonPrimitive("status").getAsString();
                if("failed".equals(st)){
                    LOG.error("获取clb token失败！");
                    return null;
                }else{
                    return j.getAsJsonPrimitive("pf").getAsString();
                }
            }else{
                LOG.error("STAUTS:"+status+";MESSAGE:"+responseString);
            }
        } catch (HttpException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        }
        return null;
    }
    private static String getClbTokenUrl(Properties properties){
        return properties.getProperty("duckling.clb.url")+"/wopi/fetch/accessToken";
    }

    private static HttpClient httpClient;
    private static HttpClient getHttpClient(){
        if(httpClient==null){
            synchronized (LOGGER) {
                if(httpClient==null){
                    httpClient = new HttpClient();
                    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
                }
            }
        }
        return httpClient;
    }

    // private DLogClient getDLogClient(){
    //     if(dLogClient==null){
    //         synchronized (LOGGER) {
    //             if(dLogClient==null){
    //                 dLogClient = new DLogClient(properties.getProperty("duckling.dlog.application.name"), properties.getProperty("duckling.dlog.server"));
    //             }
    //         }
    //     }
    //     return dLogClient;
    // }

}
