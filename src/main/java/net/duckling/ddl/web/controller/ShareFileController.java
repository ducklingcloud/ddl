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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.file.FileStorage;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.mail.AoneMailService;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.share.ShareFileAccessService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.Activation;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.EmailUtil;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.JsonUtil;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.vlabs.commons.principal.UserPrincipal;

/**
 * @date 2011-8-17
 * @author Clive Lee
 */
@Controller
@RequestMapping("/system/shareFile")
public class ShareFileController extends BaseController {
    @Autowired
    private TeamService teamService;
    @Autowired
    private AoneMailService aonemailService;
    @Autowired
    private ShareFileAccessService shareFileAccessService;
    @Autowired
    private FileStorage storage;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private URLGenerator urlGenerator;

    @RequestMapping
    public ModelAndView prepare(HttpServletRequest request) {
        VWBContext context = getVWBContext(request);
        ModelAndView mv = layout(".aone.portal",context, "/jsp/aone/team/share/shareFile.jsp");
        return mv;
    }

    @RequestMapping(method=RequestMethod.POST, params="func=uploadTempFiles") //Support for IE
    public void uploadTempFiles(@RequestParam("qqfile") MultipartFile uplFile, HttpServletRequest request,HttpServletResponse response) throws IOException{
        String filename = uplFile.getOriginalFilename();
        createTempFile(request,response, filename,uplFile.getSize(),uplFile.getInputStream());
    }

    @RequestMapping(params = "func=uploadTempFiles", headers = { "X-File-Name" }) //Support for FireFox
    public void uploadTempFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = getFileNameFromHeader(request);
        createTempFile(request, response, filename,request.getContentLength(),request.getInputStream());
    }

    @RequestMapping(params="func=submitShare")
    public ModelAndView submitShare(HttpServletRequest request) throws UnsupportedEncodingException {
        VWBContext context = getVWBContext(request);
        String url = urlGenerator.getURL(UrlPatterns.SHARE_FILE_SUCCESS,null,null);
        ModelAndView mv = new ModelAndView(new RedirectView(url));
        try{
            userShareFile(request,mv);
        }catch(RuntimeException e){
            ModelAndView errorMV = layout(".aone.portal",context, "/jsp/aone/team/share/shareFileError.jsp");
            errorMV.addObject("message",e.getMessage());
            return errorMV;
        }
        return mv;
    }

    @RequestMapping(params="func=isExistRegister")
    public void isExistRegister(HttpServletRequest request,HttpServletResponse response){
        String email =  request.getParameter("newRegister");
        boolean flag = aoneUserService.isExistAoneRegister(email);
        JsonObject json = new JsonObject();
        json.addProperty("result", flag);
        json.addProperty("message", "Hello");
        JsonUtil.write(response, json);
    }

    @RequestMapping(params="func=isPasswordCorrect")
    public void isPasswordCorrect(HttpServletRequest request,HttpServletResponse response){
        String password = request.getParameter("password");
        String uid = request.getParameter("newRegister");
        UserPrincipal p = aoneUserService.getUMTUser(uid, password);
        JsonObject json = new JsonObject();
        if(p!=null){
            json.addProperty("result", true);
            json.addProperty("message",p.getDisplayName());
        }else{
            json.addProperty("result", false);
            json.addProperty("message", "");
        }
        JsonUtil.write(response, json);
    }

    private void userShareFile(HttpServletRequest request,ModelAndView mv) throws UnsupportedEncodingException{
        String[] fileNames = request.getParameterValues("fileName");
        boolean flag = Boolean.parseBoolean(request.getParameter("isFirst"));
        String user = request.getParameter("newRegister");
        String userName = request.getParameter("name");
        String friendEmails = request.getParameter("targetEmails");
        if(!EmailUtil.isValidEmail(user)||!EmailUtil.isValidEmail(friendEmails)){
            throw new RuntimeException("使用的邮箱不规范！");
        }
        VWBContext context = getVWBContext(request);
        //Step.1.为新用户创建新团队
        int tid = findUserDefaultTeam(user,userName);
        //Step.2.移动已经上传的临时文件到刚创建的团队
        String[] fileURLs = moveTempFileToDefaultTeam(request,tid);
        //Step.3.发送分享者账户激活邮件并提示文档已经成功分享
        sendShareSuccessMail(context,user,userName,fileURLs,fileNames,flag,new String[]{friendEmails});
        //Step.4.发送给被分享者分享邮件
        sendAccessFileMail(friendEmails, userName, context, fileNames, fileURLs,request.getParameter("message"));
        mv.addObject("fileURLs", fileURLs);
        if(fileNames!=null&&fileNames.length!=0){
            for(int i=0;i<fileNames.length;i++){
                fileNames[i] = java.net.URLEncoder.encode(fileNames[i],"UTF-8");
            }
        }
        mv.addObject("fileNames", fileNames);
        mv.addObject("isFirst", flag);
        mv.addObject("tid", tid);
    }

    private int findUserDefaultTeam(String user,String userName){
        return teamService.getPersonalTeam(user, userName);
    }

    private String[] moveTempFileToDefaultTeam(HttpServletRequest request,int tid){
        String user = request.getParameter("newRegister");
        int validOfDays = Integer.parseInt(request.getParameter("validOfDays"));
        String[] clbIds = request.getParameterValues("clbId");
        String[] fileNames = request.getParameterValues("fileName");
        String[] sizes = request.getParameterValues("size");
        String[] fileURLs = new String[clbIds.length];
        VWBContext.setCurrentTid(tid);
        for(int i=0;i<clbIds.length;i++) {
            FileVersion fileVersion = resourceOperateService.referExistFileByClbId(tid,0, user, Integer.parseInt(clbIds[i]),1, fileNames[i],Long.parseLong(sizes[i]));
            String encodeURL = shareFileAccessService.getPublicFileURL(tid, Integer.parseInt(clbIds[i]), fileVersion.getRid(), validOfDays, user);
            fileURLs[i] = urlGenerator.getAbsoluteURL(UrlPatterns.DIRECT, encodeURL, null);
        }
        VWBContext.setCurrentTid(-1);
        return fileURLs;
    }

    private void sendAccessFileMail(String targetEmails,String name, VWBContext context,
                                    String[] fileNames, String[] fileURLs,String message) {
        String[] shareMails = targetEmails.split(",");
        for(int i=0;i<shareMails.length;i++) {
            aonemailService.sendAccessFileMail(fileNames, fileURLs, name, shareMails[i],message);
        }
    }

    private void sendShareSuccessMail(VWBContext context,String user,String userName,String[] fileURLs,String[] fileNames,boolean flag,String[] shareUser) {
        if(flag){
            String password = getRandomPassword();
            Activation instance = Activation.getInstance(user, userName, password);
            int id = aoneUserService.saveActivation(instance);
            instance.setId(id);
            instance.setDisplayURL(EncodeUtil.getDisplayURL(instance));
            String activationURL =  urlGenerator.getAbsoluteURL(UrlPatterns.ACTIVITION, instance.getDisplayURL(),null);
            aonemailService.sendShareSuccessMail(instance, activationURL, fileURLs, fileNames);
        }else{
            aonemailService.sendShareSuccessMailWithoutActivation(user, userName, fileURLs, fileNames,shareUser);
        }
    }

    private VWBContext getVWBContext(HttpServletRequest request) {
        return VWBContext.createContext(request,UrlPatterns.ADMIN);
    }

    private String getRandomPassword() {
        return EncodeUtil.generateRandomLoginPassword();
    }

    @SuppressWarnings("unchecked")
    private void createTempFile(HttpServletRequest request,HttpServletResponse response, String filename,long size,InputStream is) {
        try {
            int clbId = storage.createFile(filename,size, is);
            JsonObject object = new JsonObject();
            object.addProperty("success", true);
            object.addProperty("clbId", clbId+"");
            object.addProperty("fileName", filename);
            object.addProperty("size", size+"");
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            JsonUtil.write(response, object);
        } catch (Throwable e) {
            LOGGER.error("Unable upload attachment.", e);
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
                LOGGER.error(ignored);
            }
        }
    }

    private String getFileNameFromHeader(HttpServletRequest request) {
        String filename = request.getHeader("X-File-Name");
        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Your system doesn't support utf-8 character encode. so sucks.");
        }
        return filename;
    }
}
