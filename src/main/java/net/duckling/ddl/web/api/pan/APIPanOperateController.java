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
package net.duckling.ddl.web.api.pan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.controller.LynxResourceUtils;
import net.duckling.ddl.web.controller.pan.MeePoMetaToPanBeanUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequestMapping("/api/pan/resourceOperate")
@RequirePermission(authenticated=true)
public class APIPanOperateController {
    private static final Logger LOG = Logger.getLogger(APIPanOperateController.class);
    private static final int SUCCESS = 0;
    private static final int WARNING = 1;
    private static final int ERROR = 2;

    private final static String EXISTED_RETURN = "return"; //若文件夹存在，直接返回

    @Autowired
    private IPanService service;
    @Autowired
    private AoneUserService aoneUserService;

    @RequestMapping(params="func=move")
    public void moveResource(HttpServletRequest request,HttpServletResponse response,@RequestParam("originalRid")String originalRid,
                             @RequestParam("targetRid")String targetRid){
        originalRid = decode(originalRid);
        targetRid = decode(targetRid);
        if (isMovingToParent(originalRid, targetRid)) {
            writeResponse(response, WARNING, "您要移动的文件已经存在于目标路径");
            return ;
        }
        if (originalRid.equals(targetRid)) { // 1.不能移动到自身
            writeResponse(response, ERROR, "不能将文件夹移动到自身");
            return ;
        }
        if(isMovingToDescendant(originalRid,targetRid)){
            writeResponse(response, WARNING, "不能将文件夹移动到其子目录中");
            return ;
        }
        try {
            MeePoMeta  org = service.ls(PanAclUtil.getInstance(request), originalRid, false);
            boolean result = service.mv(PanAclUtil.getInstance(request), originalRid, targetRid);
            if(result){
                //              String url = urlGenerator.getAbsoluteURL(UrlPatterns.PAN_VIEW,encode(targetRid) , null);
                int index = targetRid.lastIndexOf("/")+1;
                String targetPathString = targetRid.substring(index);
                writeResponse(response, SUCCESS, "“" + org.name + "”成功移至文件夹 " + getMoveTargName(targetPathString) );
            }else{
                writeResponse(response, ERROR, "移动失败");
            }
        } catch (MeePoException e) {
            writeResponse(response, ERROR, "移动失败");
            LOG.error("", e);
        }

    }

    @RequestMapping(params="func=rename")
    public void rename(HttpServletRequest request,HttpServletResponse response,@RequestParam("rid")String rid,@RequestParam("fileName")String fileName){
        if(StringUtils.isEmpty(fileName)){
            JSONObject result = new JSONObject();
            result.put("result", false);
            result.put("message", "文件名不能为空");
            JsonUtil.writeJSONObject(response, result);
        }else{
            rid = decode(rid);
            String newRid = getNewPath(rid, fileName);
            String uid = VWBSession.getCurrentUid(request);
            JSONObject o = new JSONObject();
            boolean result = false;
            String message = null;
            PanAcl acl = PanAclUtil.getInstance(request);
            try {
                result = service.rename(acl, rid, newRid);
                if (!result) {
                    message = "文件名已经存在";
                }
            } catch (MeePoException e) {
                result = false;
                message = e.getMessage();
            }
            if (result) {
                try {
                    MeePoMeta meta = service.ls(acl, newRid, false);
                    SimpleUser user = aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request));
                    o.put("resource", LynxResourceUtils.getPanResource(uid, MeePoMetaToPanBeanUtil.transfer(meta,user)));
                } catch (MeePoException e) {
                }
                LOG.info(uid +" rename " +rid+ " to "+ fileName);
            }
            o.put("result", result);
            o.put("message", message);
            JsonUtil.writeJSONObject(response, o);
        }
    }

    @RequestMapping(params="func=createFolder")
    public void createFolder(HttpServletRequest request,HttpServletResponse response,@RequestParam("parentRid")String parentRid,@RequestParam("fileName")String fileName){
        String uid = VWBSession.getCurrentUid(request);
        if(StringUtils.isEmpty(fileName)){
            printResult(false, "文件名不能为空", response);
            return;
        }
        if(StringUtil.illCharCheck(request, response, "fileName")){
            return;
        }

        parentRid = decode(parentRid);
        if (!StringUtils.isEmpty(parentRid)) {
            if(parentRid.endsWith("/")){
                fileName = parentRid + fileName;
            }else{
                fileName = parentRid +"/"+ fileName;
            }
        }

        PanAcl acl = PanAclUtil.getInstance(request);
        String message = null;
        try {
            //文件夹已存在
            if (!service.mkdir(acl, fileName)) {
                //文件夹存在直接返回文件夹信息
                if(!EXISTED_RETURN.equals(request.getParameter("existed"))){
                    printResult(false, "文件夹名称已存在", "errorCode", "errorName", response);
                    return;
                }
            }
        } catch (MeePoException e) {
            LOG.error("", e);
            message = e.getMessage();
            printResult(false, message, response);
            return;
        }

        try {
            MeePoMeta meta = service.ls(acl, fileName, false);
            SimpleUser user = aoneUserService.getSimpleUserByUid(VWBSession.getCurrentUid(request));

            LOG.info(uid+" create folder "+fileName);
            printResult(true, message, "resource",
                        LynxResourceUtils.getPanResource(uid, MeePoMetaToPanBeanUtil.transfer(meta,user)), response);

        } catch (MeePoException e) {
            LOG.error(e.getMessage());
        }
        return;
    }

    private boolean isMovingToDescendant(String originalRid, String targetRid) {
        return targetRid.startsWith(originalRid+"/");
    }
    private String getMoveTargName(String name){
        if(StringUtils.isEmpty(name)){
            return "所有文件";
        }else{
            return name;
        }
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    private static void writeResponse(HttpServletResponse response, int state, String message) {
        JSONObject msg = new JSONObject();
        boolean r = state==0?true:false;
        msg.put("result", r);
        msg.put("message", message);
        JsonUtil.writeJSONObject(response, msg);
    }

    private boolean isMovingToParent(String originalRid, String targetRid) {
        int index = originalRid.lastIndexOf("/");
        String orPar = originalRid.substring(0,index);
        return targetRid.equals(orPar);
    }

    private String decode(String s) {
        if("0".equals(s)){
            return "/";
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private String getNewPath(String oldPath, String fileName) {
        if (StringUtils.isEmpty(oldPath) || "/".equals(oldPath)) {
            return "/" + fileName;
        } else {
            int index = oldPath.lastIndexOf("/");
            if (index == -1) {
                return "/" + fileName;
            }
            if (index == oldPath.length() - 1) {
                return oldPath + fileName;
            }
            return oldPath.substring(0, index) + "/" + fileName;
        }
    }

    private void printResult(boolean result, String message, HttpServletResponse response){
        printResult(result, message, null, null, response);
    }

    private void printResult(boolean result, String message, String optKey, Object optVal,  HttpServletResponse response){
        JSONObject o = new JSONObject();
        o.put("result", result);
        o.put("message", message);
        if(optKey!=null){
            o.put(optKey, optVal);
        }
        JsonUtil.writeJSONObject(response, o);
    }

}
