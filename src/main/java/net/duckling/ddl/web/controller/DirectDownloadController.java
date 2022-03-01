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
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.export.FileNotFoundException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.itemtypemap.ItemTypeMappingService;
import net.duckling.ddl.service.itemtypemap.ItemTypemapping;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.share.ShareFileAccess;
import net.duckling.ddl.service.share.ShareFileAccessService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.AoneTimeUtils;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.SizeUtil;
import net.duckling.ddl.web.controller.file.BaseAttachController;

import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * @date 2011-8-18
 * @author Clive Lee
 */
@Controller
public class DirectDownloadController extends BaseAttachController {

    @Autowired
    private BrowseLogService browseLogService;
    @Autowired
    private ShareFileAccessService shareFileAccessService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ItemTypeMappingService itemTypeMappingService;
    @Autowired
    private AoneUserService aoneUserService;
    @RequestMapping("/direct/{downloadURL}")
    public ModelAndView directDownload(HttpServletRequest request, @PathVariable("downloadURL") String downloadURL) {
        String[] tempArray = EncodeUtil.getDecodeArray(downloadURL);
        int tid = Integer.parseInt(tempArray[0]);
        int rid = getRid(tempArray);
        Resource resource=resourceService.getResource(rid,tid);
        VWBContext.setCurrentTid(tid);
        VWBContext context = getVWBContext(request);
        ModelAndView mv = layout(".aone.portal",context, "/jsp/aone/team/share/downloadShareFile.jsp");
        context.getContainer().getSite(tid);
        ShareFileAccess s = shareFileAccessService.parseShareAccess(downloadURL);
        if(null == s||resource==null){
            throw new FileNotFoundException(rid, null, context.getBaseURL()+"/jsp/aone/errors/fileError.jsp");
        }
        if (!shareFileAccessService.isValidRequest(s)) {
            return sendExpiredException(context, s);
        }
        /*Resource file = null;
          try{
          file = resourceService.getResource(item.getRid());
          }catch(EmptyResultDataAccessException e){
          //throw new FileNotFoundException(fid, null, context.getContainer().getURL(UrlPatterns.DASHBOARD, "","", true));
          throw new FileNotFoundException(fid, null, context.getBaseURL()+"/jsp/aone/errors/fileError.jsp");
          }*/
        FileVersion fileVersion = fileVersionService.getLatestFileVersion(rid, tid);
        String ownerName = aoneUserService.getUserNameByID(s.getUid());
        String currUid = context.getCurrentUID();
        if(StringUtils.isNotEmpty(s.getFetchFileCode())){
            mv.addObject("validateCode", "validateCode");
        }
        mv.addObject("status", resource.getStatus());
        mv.addObject("isMySelf",fileVersion.getEditor().equals(currUid));
        mv.addObject("rid", rid);
        mv.addObject("tid",tid);
        mv.addObject("downloadURL", downloadURL);
        mv.addObject("fileVersion", fileVersion);
        mv.addObject("fileSize",SizeUtil.getFormatSize(fileVersion.getSize()));
        mv.addObject("fileOwnerName", ownerName);
        mv.addObject("shareCreateTime", AoneTimeUtils.formatToDateTime(s.getCreateTime()));
        mv.addObject("validOfDays",s.getValidOfDays());
        mv.addObject("restOfDays",shareFileAccessService.getRestOfVaildDays(s));
        mv.addObject("isLogin", context.getVWBSession().isAuthenticated());
        //add by lvly@2012-07-23  记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        return mv;
    }
    private int getRid(String[] tempArray){
        if(tempArray.length>=5&&"rid".equalsIgnoreCase(tempArray[4])){
            return Integer.parseInt(tempArray[2]);
        }else{
            int tid = Integer.parseInt(tempArray[0]);
            int fid = Integer.parseInt(tempArray[2]);
            ItemTypemapping i = itemTypeMappingService.getItemTypeMapping(tid, fid, LynxConstants.TYPE_FILE);
            if(i!=null){
                return i.getRid();
            }else{
                return fid;
            }
        }
    }

    private ModelAndView sendExpiredException(VWBContext context, ShareFileAccess s) {
        ModelAndView mv = layout(".aone.portal",context, "/jsp/aone/team/share/downloadShareFileError.jsp");
        String ownerName = aoneUserService.getUserExtInfo(s.getUid()).getName();
        mv.addObject("fileOwnerName", ownerName);
        mv.addObject("isLogin", context.getVWBSession().isAuthenticated());
        mv.addObject("shareCreateTime", AoneTimeUtils.formatToDateTime(s.getCreateTime()));
        FileVersion fileVersion = fileVersionService.getLatestFileVersion(s.getFid(), s.getTid());
        if(null == fileVersion){
            mv.addObject("notExist",true);
            return mv;
        }
        mv.addObject("fileVersion", fileVersion);
        mv.addObject("fileSize",SizeUtil.getFormatSize(fileVersion.getSize()));
        return mv;
    }

    @RequestMapping("/direct/validateCode")
    public void validateCode(HttpServletRequest request,HttpServletResponse response){
        String url = request.getParameter("url");
        String code = request.getParameter("code");
        String[] tempArray = EncodeUtil.getDecodeArray(url);
        int tid = Integer.parseInt(tempArray[0]);
        VWBContext.setCurrentTid(tid);
        ShareFileAccess s = shareFileAccessService.parseShareAccess(url);
        JsonObject obj = new JsonObject();
        if(StringUtils.equals(s.getFetchFileCode(), code)){
            obj.addProperty("status", true);
        }else{
            obj.addProperty("status", false);
        }
        JsonUtil.write(response, obj);
    }

    @RequestMapping("/direct/{downloadURL}/download")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable String downloadURL) throws IOException{
        String[] tempArray = EncodeUtil.getDecodeArray(downloadURL);
        int tid = Integer.parseInt(tempArray[0]);
        int rid=getRid(tempArray);
        VWBContext.setCurrentTid(tid);
        VWBContext context = getVWBContext(request);
        Site site = context.getContainer().getSite(tid);
        Resource resource=resourceService.getResource(rid,tid);
        if(resource==null){
            throw new FileNotFoundException(rid, "", site.getFrontPage());
        }
        ShareFileAccess s = shareFileAccessService.parseShareAccess(downloadURL);
        if (!shareFileAccessService.isValidRequest(s)) {
            sendExpiredException(context, s);
        }
        FileVersion fileVersion = fileVersionService.getLatestFileVersion(rid, tid);
        //是否需要文件提取码
        if(StringUtils.isNotEmpty(s.getFetchFileCode())){
            String code = request.getParameter("validateCode");
            if(!StringUtils.equals(code, s.getFetchFileCode())){
                Team team = teamService.getTeamByID(site.getId());
                String teamHomeURL = site.getFrontPage();
                throw new FileNotFoundException(fileVersion.getRid(),team.getDisplayName(),teamHomeURL);
            }
        }
        if (fileVersion!=null) {
            if (ShareFileAccessService.OLD_VALID_PASSWORD.equals(tempArray[3])) {
                getContent(site, request, response, Integer.parseInt(tempArray[1]), fileVersion.getClbVersion() + "",fileVersion.getTitle(),
                           false);
            } else {
                getContent(site, request, response, fileVersion.getClbId(), fileVersion.getClbVersion() + "",fileVersion.getTitle(), false);
            }
        } else {
            Team team = teamService.getTeamByID(site.getId());
            String teamHomeURL = site.getFrontPage();
            throw new FileNotFoundException(rid, team.getDisplayName(), teamHomeURL);
        }
        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        VWBContext.setCurrentTid(-1);
    }

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request,UrlPatterns.SHARE_FILE);
    }

}
