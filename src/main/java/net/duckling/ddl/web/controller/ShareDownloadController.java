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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.FileTypeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.ShareResourceDownloadCountUtil;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.ddl.util.UserAgentUtil;
import net.duckling.ddl.web.bean.FileTypeHelper;
import net.duckling.ddl.web.controller.file.BaseAttachController;

import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.yaml.snakeyaml.util.UriEncoder;

import cn.cnic.cerc.dlog.client.DLogClient;
import cn.cnic.cerc.dlog.client.WebLog;
import cn.cnic.cerc.dlog.client.WebLogResolver;
import cn.cnic.cerc.dlog.domain.LogBean;

/**
 * 邮箱附件预览
 */
@Controller
@RequestMapping("f/{rid}")
public class ShareDownloadController extends BaseAttachController{
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private DucklingProperties properties;
    @Autowired
    private WebLogResolver webLogResolver;
    private DLogClient  dLogClient ;

    @Autowired
    private ShareResourceService shareResourceService;


    protected static final Logger LOG = Logger.getLogger(ShareDownloadController.class);

    private static final String SHARE_SESSION_KEY  = "share_rid";
    private static final int SUCCESS = 0;
    private static final int ERROR = 2;

    @RequestMapping
    public ModelAndView display(HttpServletRequest request,HttpServletResponse response, @PathVariable("rid")String ridCode) throws IOException, ServletException {
        int rid = ShareRidCodeUtil.decode(ridCode);
        Resource resource = resourceService.getResource(rid);
        ModelAndView mv = null;
        if(resource==null||resource.isDelete()){
            notFound(request, response, true);
            return null;
        }
        int tid = resource.getTid();
        ShareResource shareResource = shareResourceService.get(rid);
        //验证资源是否存在
        if(shareResource == null){
            notFound(request, response, true);
            return null;
        }

        String userName = aoneUserService.getUserNameByID(shareResource.getShareUid());
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE, resource.getRid(), LynxConstants.TYPE_FILE);
        //验证是否有提取码
        if(shareResource.getPassword() != null ){
            if(request.getSession().getAttribute(SHARE_SESSION_KEY)==null ||!((Set<Integer>)request.getSession().getAttribute(SHARE_SESSION_KEY)).contains(rid)){
                mv = layout(".share.download", context, "/jsp/aone/file/shareDownloadCode.jsp");
                mv.addObject("ridCode", ridCode);
                mv.addObject("shareUserName",userName);
                return mv;
            }
        }
        resource = inFolderFile(request, resource);

        if(resource.isFile()){
            mv = dealFile(request, resource);

            int currentRid = 0;
            if(request.getParameter("rid") == null){
                currentRid = rid;
            }else{
                currentRid = Integer.parseInt(request.getParameter("rid"));
            }
            mv.addObject("downloadURL", urlGenerator.getURL(UrlPatterns.RESOURCE_SHARE, null, null) + "/" + ridCode + "?func=download&imageType=original&rid="+currentRid);
        }else if(resource.isFolder()){
            mv = dealFolder(request, resource);
        }else{
            notFound(request, response, true);
            return null;
        }

        mv.addObject("pageType", "list");
        if(resource.isAvailable()){
            mv.addObject("resourePath", getParentPath(resource));
        }

        mv.addObject("shareUserName",userName);
        mv.addObject("shareResource",shareResource);
        mv.addObject("resource", resource);
        mv.addObject("ridCode", ridCode);
        myTeamToModel(context, mv);
        mv.addObject("teamUrl", urlGenerator.getURL(tid,UrlPatterns.T_LIST, null,null));
        mv.addObject("teamHome",urlGenerator.getURL(tid,UrlPatterns.T_TEAM_HOME, null,null));
        mv.addObject("shareUrl", getShareUrl(ridCode));

        mv.addObject("teamAclMap", getTeamAclMap(context.getCurrentUID()));
        return mv;
    }

    /**
     * 获取用户所有团队权限map对象
     * @param uid
     * @return
     */
    private Map<String, String> getTeamAclMap(String uid){
        Map<String, String> result = new HashMap<String, String>();
        List<TeamAcl> aclList = authorityService.getUserAllTeamAcl(uid);
        for(TeamAcl item : aclList){
            result.put(item.getTid(), item.getAuth());
        }
        return result;
    }

    /**
     * 保存分享文件
     * @param request
     * @param response
     * @param originalRidStr
     * @param targetRidStr
     */
    @WebLog(method = "copyFileTo", params = "targetRid")
    @RequestMapping(params="func=copy")
    public void copyFileTo(HttpServletRequest request, HttpServletResponse response,
                           @PathVariable("rid")String originalRidStr,
                           @RequestParam("targetRid")String targetRidStr) {
        int originalRid = ShareRidCodeUtil.decode(originalRidStr);
        int targetRid = Integer.parseInt(targetRidStr);
        JsonObject result=new JsonObject();
        try {
            Resource originalRes = resourceService.getResource(originalRid);
            int tid = originalRes.getTid();
            int targetTid = getDestTid(request, tid);

            if (originalRes == null) {
                writeResponse(response, ERROR,"文件已被删除.");
                return;
            }

            if (originalRid == targetRid) { // 1.不能复制到自身
                writeResponse(response, ERROR,"不能将文件夹复制到自身");
                return;
            }
            // 2.不能复制到自身的子文件夹中
            List<Resource> descendants = folderPathService.getDescendants(tid, originalRid);
            for(Resource descendant : descendants) {
                if (targetRid == descendant.getRid()) {
                    writeResponse(response, ERROR, "不能将文件夹复制到其子目录中");
                    return ;
                }
                if(descendant.isFolder()){
                    writeResponse(response, ERROR, "不能复制文件夹");
                    return ;
                }
            }
            if(!teamSpaceSizeService.validateTeamSize(targetTid, descendants)){
                writeResponse(response, ERROR, "团队空间已满不能进行复制");
                return ;
            }

            VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
            String uid = context.getCurrentUID();
            List<Resource> nodes = folderPathService.getResourcePath(originalRid);
            String originalPath = nodes.get(nodes.size() - 1).getTitle();
            nodes = folderPathService.getResourcePath(targetRid);
            String targetPathString = (targetRid == 0) ? "全部文件" : nodes.get(nodes.size() - 1).getTitle();

            Resource resource = resourceOperateService.copyResource(targetTid, targetRid, tid,originalRid, uid);
            String url = urlGenerator.getURL(targetTid, UrlPatterns.T_VIEW_R, targetRid+"", null);
            JsonObject resourceJSON=LynxResourceUtils.getResourceJson(uid, resource);
            result.addProperty("state", SUCCESS);
            result.addProperty("msg", "“" + originalPath + "” " +
                       getLocaleMessage(request, "ddl.tip.t15") + " <a href=\"" + url + "\">" + targetPathString + "</a>");
            result.add("resource", resourceJSON);
            JsonUtil.write(response, result);
        } catch (RuntimeException re) {
            result.addProperty("state", ERROR);
            result.addProperty("msg", "复制失败");
            JsonUtil.write(response, result);
            throw re;
        }
    }

    /**
     * 检查加密文件提取码是否正确
     * @param request
     * @param response
     * @param ridCode
     * @param code
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method=RequestMethod.POST, params="func=checkcode")
    public void checkcode(HttpServletRequest request,HttpServletResponse response,@PathVariable("rid")String ridCode,
                          @RequestParam("code")String code){
        int rid = ShareRidCodeUtil.decode(ridCode);
        ShareResource shareRes = shareResourceService.get(rid);

        JsonObject json = new JsonObject();
        json.addProperty("result", "");
        if(shareRes!=null && shareRes.getPassword().equalsIgnoreCase(code.trim())){
            Set<Integer> fetchCodes = (Set<Integer>)request.getSession().getAttribute(SHARE_SESSION_KEY);
            if(fetchCodes==null){
                fetchCodes = new HashSet<Integer>();
                request.getSession().setAttribute(SHARE_SESSION_KEY, fetchCodes);
            }
            fetchCodes.add(rid);
            json.addProperty("result", "ok");
        } else if(shareRes==null){
            json.addProperty("result", "ok");
        }
        JsonUtil.write(response, json);
    }

    /**
     * 文件浏览处理
     * @param request
     * @param resource
     * @return
     */
    @WebLog(method = "preview", params = "rid,from")
    public ModelAndView dealFile(HttpServletRequest request, Resource resource) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE, resource.getRid(), LynxConstants.TYPE_FILE);
        int rid = resource.getRid();
        int tid = resource.getTid();
        ModelAndView mv = getShareLayout(request, context);
        FileVersion currentVersion =getFileVersion(request,rid, tid);
        currentVersion.setEditor(aoneUserService.getUserNameByID(currentVersion.getEditor()));
        mv.addObject("sizeShort", FileSizeUtils.getFileSize(currentVersion.getSize()));
        mv.addObject("fileViewName", FileTypeHelper.getName(currentVersion.getTitle()));
        mv.addObject("version", currentVersion.getVersion());
        addPreviewLog(request);
        return mv;
    }

    /**
     * 文件夹浏览处理
     * @param request
     * @param resource
     * @return
     */
    @WebLog(method = "preview", params = "rid,from")
    public ModelAndView dealFolder(HttpServletRequest request, Resource resource) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE, resource.getRid(), LynxConstants.TYPE_FILE);
        ModelAndView mv = getShareLayout(request, context);
        mv.addObject("fileViewName", "folder");
        addPreviewLog(request);
        return mv;
    }

    private ModelAndView getShareLayout(HttpServletRequest request, VWBContext context){
        return UserAgentUtil.isMobile(request) ?
                layout("mobile.share.download", context, "/WEB-INF/views/mobile/resource/shareDownload.jsp") :
                layout(".share.download", context, "/jsp/aone/file/shareDownload.jsp");
    }

    private ModelAndView getShareCodeLayout(HttpServletRequest request, VWBContext context){
        return UserAgentUtil.isMobile(request) ?
                layout("mobile.share.download", context, "/WEB-INF/views/mobile/resource/shareDownloadCode.jsp") :
                layout(".share.download", context, "/jsp/aone/file/shareDownloadCode.jsp");
    }

    private ModelAndView myTeamToModel(VWBContext context, ModelAndView mv){
        String myTeamCode = teamService.getTeamNameFromEmail(context.getCurrentUID());
        Team t = teamService.getTeamByName(myTeamCode);
        if(t!=null){
            mv.addObject("myTeamId",t.getId());
        }
        mv.addObject("myTeamCode",myTeamCode);
        mv.addObject("teamCode", myTeamCode);
        return mv;
    }

    private void addPreviewLog(HttpServletRequest request){
        try{
            DLogClient client = getDLogClient();
            LogBean aLog = new LogBean();
            aLog.setHost(request.getLocalAddr());
            aLog.setMethod("preview");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            aLog.setTime(time);
            Map<String,String> params = webLogResolver.buildFixedParameters(request);
            params.put("from", request.getParameter("from"));
            aLog.setOption(params);
            LinkedList<LogBean> ls = new LinkedList<LogBean>();
            ls.add(aLog);
            client.sendLogData(client.prepareData(ls));
        }catch(Exception e){
            LOG.error("", e);
        }
    }


    private FileVersion getFileVersion(HttpServletRequest request,int rid,int tid){
        String version = request.getParameter("version");
        if(StringUtils.isEmpty(version)){
            return fileVersionService.getLatestFileVersion(rid, tid);
        }else{
            int ver = Integer.parseInt(version);
            return fileVersionService.getFileVersion(rid, tid, ver);

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

    private DLogClient getDLogClient(){
        if(dLogClient==null){
            synchronized (LOG) {
                if(dLogClient==null){
                    dLogClient = new DLogClient(properties.getProperty("duckling.dlog.application.name"), properties.getProperty("duckling.dlog.server"));
                }
            }
        }
        return dLogClient;
    }

    private boolean isValidate(HttpServletRequest request,ShareResource shareRes){
        if(StringUtils.isEmpty(shareRes.getPassword())){
            return true;
        }else{
            if(request.getSession().getAttribute(SHARE_SESSION_KEY)==null){
                return false;
            }
            return ((Set<Integer>)request.getSession().getAttribute(SHARE_SESSION_KEY)).contains(shareRes.getRid());

        }
    }

    @RequestMapping(params = "func=onlineViewer")
    public ModelAndView onlineViewer(HttpServletRequest request, @PathVariable("rid") String ridCode) {
        int rid = ShareRidCodeUtil.decode(ridCode);
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/aone/file/onlineViewer.jsp");

        int currentRid = 0;
        if(request.getParameter("rid") == null){
            currentRid = rid;
        }else{
            currentRid = Integer.parseInt(request.getParameter("rid"));
        }

        mv.addObject("pdfviewerURL", urlGenerator.getURL(UrlPatterns.RESOURCE_SHARE, null, null) + "/" + ridCode + "?func=download&rid="+currentRid);

        return mv;
    }

    @RequestMapping(params="func=download")
    public void download(HttpServletRequest request,HttpServletResponse response,@PathVariable("rid")String ridCode) throws IOException{
        ShareResourceDownloadCountUtil.count(request);
        int rid = ShareRidCodeUtil.decode(ridCode);
        ShareResource shareRes = shareResourceService.get(rid);
        Resource r = resourceService.getResource(rid);
        int downLoadRid = Integer.parseInt(request.getParameter("rid"));
        Resource dr = resourceService.getResource(downLoadRid);
        if(shareRes!=null&&r!=null){
            if(!isValidate(request, shareRes)){
                return;
            }
            FileVersion fv = fileVersionService.getFileVersion(dr.getRid(), dr.getTid(), dr.getLastVersion());

            if(FileTypeUtils.isClbDealImage(dr.getTitle())){
                getPictureContext(request, response, fv, false);
            }else{
                getContent(request, response, fv.getClbId(),fv.getClbVersion()+"",dr.getTitle(), false);
            }
        }
        shareRes.setDownloadCount(shareRes.getDownloadCount()+1);
        shareResourceService.update(shareRes);
    }

    @RequestMapping(params = "func=getImageStatus")
    @WebLog(method = "getImageStatus", params = "rid")
    public void getImageStatus(HttpServletRequest req, HttpServletResponse resp, @PathVariable("rid")String ridCode){
        String status = "error";
        String type = req.getParameter("type");
        int rid = ShareRidCodeUtil.decode(ridCode);
        ShareResource shareRes = shareResourceService.get(rid);
        Resource r = resourceService.getResource(rid);
        Resource dr = resourceService.getResource(Integer.parseInt(req.getParameter("rid")));
        if(shareRes!=null&&r!=null){
            if(!isValidate(req, shareRes)){
                return;
            }
            FileVersion fv = fileVersionService.getFileVersion(dr.getRid(), dr.getTid(), dr.getLastVersion());
            status = getImageStatus(fv.getClbId(), fv.getClbVersion()+"", type);
        }

        JsonObject j = new JsonObject();
        j.addProperty("status", status);
        JsonUtil.write(resp,j);
    }

    @RequestMapping(params="func=downloads")
    public void downloads(HttpServletRequest request,HttpServletResponse response,@PathVariable("rid")String ridCode) throws IOException{
        ShareResourceDownloadCountUtil.count(request);
        int rid = ShareRidCodeUtil.decode(ridCode);
        ShareResource shareRes = shareResourceService.get(rid);
        Resource r = resourceService.getResource(rid);
        String[] rids = request.getParameterValues("rids");
        if(shareRes!=null&&r!=null){
            if(!isValidate(request, shareRes)){
                return;
            }
            List<Integer> ridA = new ArrayList<Integer>();
            for(String rr: rids){
                ridA.add(Integer.parseInt(rr));
            }
            downloadFolder(request, response, ridA);
        }
        shareRes.setDownloadCount(shareRes.getDownloadCount()+1);
        shareResourceService.update(shareRes);
    }


    @RequestMapping(params="func=query")
    public void query(HttpServletRequest request,HttpServletResponse response,@PathVariable("rid")String ridCode){
        int shareRid = ShareRidCodeUtil.decode(ridCode);
        int currentRid = getCurrentRid(request);
        Resource r = resourceService.getResource(shareRid);
        PaginationBean<Resource> resources = null;
        String queryType =request.getParameter("sortType");

        //是否为分享目录的子目录
        if(currentRid > 0){
            queryType=StringUtils.isEmpty(queryType)?"":queryType;
            ResourceQuery rq = ResourceQuery.buildForQuery(request,queryType);
            rq.setTid(r.getTid());
            resources = resourceService.query(rq);
        }else{
            currentRid = 0;
        }

        //分享根目录
        if(currentRid==0){
            resources = new PaginationBean<Resource>();
            resources.setBegin(0);
            resources.setSize(1);
            resources.setTotal(1);
            List<Resource> rs = new ArrayList<Resource>();
            rs.add(r);
            resources.setData(rs);
            currentRid = -1;
        }

        JsonObject j = buildQueryResultJson( shareRid,currentRid, resources,queryType);
        String tokenKey=request.getParameter("tokenKey");
        j.addProperty("tokenKey", tokenKey);
        j.addProperty("order", queryType);
        JsonUtil.write(response, j);
    }

    private static int getCurrentRid(HttpServletRequest request){
        String r = request.getParameter("rid");
        int rid = 0;
        if(StringUtils.isEmpty(r)){
            String path = request.getParameter("path");
            if(!StringUtils.isEmpty(path)){
                String[] s=path.split("/");
                try{
                    rid = Integer.parseInt(s[s.length-1]);
                }catch(Exception e){}
            }
        }else{
            rid = Integer.parseInt(r);
        }
        return rid;
    }

    private JsonObject buildQueryResultJson(int shareRid,int rid,PaginationBean<Resource> resources,String queryType) {
        if(resources==null){
            resources=new PaginationBean<Resource>();
        }

        JsonObject j = new JsonObject();
        j.addProperty("total", resources.getTotal());
        if(rid>0){
            j.add("currentResource", LynxResourceUtils.getResourceJson(null, resourceService.getResource(rid)));
            j.add("path", getNavPath(shareRid, rid));
        }
        j.addProperty("nextBeginNum", resources.getNextStartNum());
        j.add("children", LynxResourceUtils.getResourceJSON(resources.getData(), null));
        j.addProperty("loadedNum",resources.getLoadedNum());
        j.addProperty("size", resources.getSize());

        return j;
    }

    private JsonArray getNavPath(int shareRid,int curentRid){
        if(curentRid==-1){
            return  new JsonArray();
        }else{
            List<Resource> r = folderPathService.getResourcePath(curentRid);
            Iterator<Resource> it = r.iterator();
            while(it.hasNext()){
                Resource re = it.next();
                if(re.getRid()==shareRid){
                    break;
                }else{
                    it.remove();
                }
            }
            return LynxResourceUtils.getResourceJSON(r, null);
        }
    }

    @RequestMapping(params="func=doError")
    public ModelAndView doError(HttpServletRequest request,HttpServletResponse response,@PathVariable("rid")String ridCode){
        String error = request.getParameter("error");
        int rid = ShareRidCodeUtil.decode(ridCode);
        ModelAndView mv = null;
        Resource resource = resourceService.getResource(rid);
        VWBContext context = VWBContext.createContext(request, LynxConstants.TYPE_FILE);
        if("shareNull".equals(error)){
            mv = getShareCodeLayout(request, context);
            mv.addObject("type", "shareNull");
            mv.addObject("resource", resource);
            return mv;
        }
        ShareResource shareResource = shareResourceService.get(rid);

        String userName = aoneUserService.getUserNameByID(shareResource.getShareUid());
        if("resourceDelete".equals(error)){
            mv = getShareCodeLayout(request, context);
            mv.addObject("type", "resourceDelete");
            mv.addObject("resource",resource);
            mv.addObject("shareUserName",userName);
            return mv;
        }

        if("noAuth".equals(error)){
            context = VWBContext.createContext(request, UrlPatterns.T_FILE, resource.getRid(), LynxConstants.TYPE_FILE);
            mv = getShareCodeLayout(request, context);;
            mv.addObject("shareUserName",userName);
            mv.addObject("ridCode", ridCode);
            mv.addObject("type", "noAuth");
        }
        return mv;
    }

    private int getDestTid(HttpServletRequest r,int tid){
        String tar = r.getParameter("targetTid");
        if(StringUtils.isEmpty(tar)){
            return tid;
        }else{
            try{
                return Integer.parseInt(tar);
            }catch(Exception e){
                return tid;
            }
        }
    }

    private static void writeResponse(HttpServletResponse response, int state, String message) {
        JsonObject msg = new JsonObject();
        msg.addProperty("state", state);
        msg.addProperty("msg", message);
        JsonUtil.write(response, msg);
    }

    private Resource inFolderFile(HttpServletRequest request,Resource resource){
        String r = request.getParameter("rid");
        if(StringUtils.isEmpty(r)){
            return resource;
        }else{
            int rid = Integer.parseInt(r);
            return resourceService.getResource(rid);
        }
    }

    /**
     * 获取分享链接
     * @param ridCode
     * @return
     */
    private String getShareUrl(String ridCode){
        return UriEncoder.encode(properties.getProperty("duckling.baseAddress")+"/"+UrlPatterns.T_VIEW_R+"/" + ridCode);
    }

}
