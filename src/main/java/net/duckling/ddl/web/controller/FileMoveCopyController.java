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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.exception.MessageException;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.copy.UserCopyCount;
import net.duckling.ddl.service.copy.UserCopyCountService;
import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PipeTaskStatus;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.ResourcePipeAgentService;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.api.pan.APIPanOperateLogController;
import net.duckling.ddl.web.controller.pan.MeePoMetaToPanBeanUtil;
import net.duckling.ddl.web.controller.pan.PanResourceBean;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.falcon.api.cache.ICacheService;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import cn.cnic.cerc.dlog.client.WebLog;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;
import com.meepotech.sdk.MeePoUsage;
import com.meepotech.sdk.PanMeta;


/**
 * @author zhoukang
 * @since 2013-11-13
 * 移动和复制操作返回json对象。
 * 一定存在的字段有"state"字段和"msg"字段
 * "state"字段表示操作的状态：
 * (1)0表示成功;
 * (2)1表示不符合操作要求，操作取消，给出警告信息；
 * (3)表示出现运行时错误
 *
 * "msg"里面是需要显示的消息。
 */

@Controller
@RequestMapping("{teamCode}/fileManager")
@RequirePermission(target = "team", operation = "view")
public class FileMoveCopyController {

    private static final Logger LOG = Logger.getLogger(FileMoveCopyController.class);

    //@WebLog(method = "moveFileTo", params = "targetRid,originalRid")
    @RequestMapping(params="func=move")
    public void moveFileTo(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("originalRid")int originalRid,
                           @RequestParam("targetRid")int targetRid) {
        try {
            if(!haveTeamEditeAuth(VWBContext.getCurrentTid(), VWBSession.getCurrentUid(request))){
                writeResponse(response, MessageException.ERROR, "您没有权限移动此文件夹");
                return ;
            }
            if (isMovingToParent(originalRid, targetRid)) {
                writeResponse(response, MessageException.WARNING, "您要移动的文件已经存在于目标路径");
                return ;
            }
            if (originalRid == targetRid) { // 1.不能移动到自身
                writeResponse(response, MessageException.ERROR, "不能将文件夹移动到自身");
                return ;
            }

            int tid = VWBContext.getCurrentTid();
            // 2.不能移动到自身的子文件夹中
            List<Resource> descendants = folderPathService.getDescendants(tid, originalRid);
            for(Resource descendant : descendants) {
                if (targetRid == descendant.getRid()) {
                    writeResponse(response, MessageException.WARNING, "不能将文件夹移动到其子目录中");
                    return ;
                }
            }
            List<Resource> nodes = folderPathService.getResourcePath(originalRid);
            String originalPath = nodes.get(nodes.size() - 1).getTitle();
            nodes = folderPathService.getResourcePath(targetRid);
            String targetPathString = (targetRid == 0) ? "全部文件" : nodes.get(nodes.size() - 1).getTitle();
            String uid = VWBSession.getCurrentUid(request);
            resourceOperateService.moveResource(tid, targetRid, originalRid,uid);
            LOG.info("用户uid="+uid+"将rid="+originalRid+"移动到rid="+targetRid);
            String url = urlGenerator.getURL(tid, UrlPatterns.T_VIEW_R, targetRid+"", null);
            writeResponse(response, MessageException.SUCCESS, "“" + originalPath + "”成功移至文件夹 <a href=\"" + url + "\">" + targetPathString + "</a>");
        } catch (RuntimeException re) {
            writeResponse(response, MessageException.ERROR, "移动失败");
            throw re;
        }

    }

    private boolean haveTeamEditeAuth(int tid,String uid){
        return authorityService.haveTeamEditeAuth(tid, uid);
    }
    //@WebLog(method = "copyFileTo", params = "targetRid,originalRid")
    @RequestMapping(params="func=copy")
    public void copyFileTo(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("originalRid")String originalRidStr,
                           @RequestParam("targetRid")String targetRidStr) {
        copySelected(request, response, originalRidStr, targetRidStr);
    }

    //@WebLog(method = "copySelected", params = "targetRid,originalRids")
    @RequestMapping(params="func=copySelected")
    public void copySelected(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("originalRids")String originalRidsString,
                             @RequestParam("targetRid")String targetRidStr) {

        if(isPan2Pan(request)){
            try {
                pan2pan("", originalRidsString, targetRidStr, PanAclUtil.getInstance(request), request, response);
            } catch (MessageException e) {
                writeResponse(response, e.getStatus(), e.getMessage());
            }
            return;
        }

        if(isPanCopy(request)){
            try {
                dealPanAndTeamCopy(request,response);
            } catch (MessageException e) {
                writeResponse(response, e.getStatus(), e.getMessage());
            }
            return;
        }

        int targetRid = Integer.parseInt(targetRidStr);
        JsonObject result=new JsonObject();
        try {
            int tid = VWBContext.getCurrentTid();
            int targetTid =getDestTid(request, tid);
            if(!haveTeamEditeAuth(targetTid, VWBSession.getCurrentUid(request))){
                writeResponse(response, MessageException.ERROR, "无法复制到目标团队，您在目标团队无编辑权限");
                return ;
            }
            String[] originalRidsStrings = originalRidsString.split(",");
            List<Integer> originalRids = new ArrayList<Integer>();
            for (String originalRid : originalRidsStrings) {
                originalRids.add(Integer.valueOf(originalRid));
            }
            int count = 0;
            List<Resource> rs = new LinkedList<Resource>();
            for (int originalRid : originalRids) {
                if (originalRid == targetRid) { // 1.不能复制到自身
                    writeResponse(response, MessageException.ERROR, "不能将文件夹复制到自身");
                    return;
                }
                // 2.不能复制到自身的子文件夹中
                List<Resource> descendants = folderPathService.getDescendants(tid, originalRid);
                for(Resource descendant : descendants) {
                    if (targetRid == descendant.getRid()) {
                        writeResponse(response, MessageException.ERROR, "不能将文件夹复制到其子目录中");
                        return ;
                    }
                    if(descendant.isFolder()){
                        writeResponse(response, MessageException.ERROR, "不能复制文件夹");
                        return ;
                    }
                }
                rs.addAll(descendants);
                count =count+descendants.size();
            }
            if(!teamSpaceSizeService.validateTeamSize(targetTid, rs)){
                writeResponse(response, MessageException.ERROR, "团队空间已满不能进行复制");
                return ;
            }
            CopyCount c = validateCopyCount(request, count,"copy");
            if(c!=null){
                writeResponse(response, MessageException.ERROR, c.getErrorMessage());
                return;
            }
            VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
            String uid = context.getCurrentUID();
            List<Resource> nodes = folderPathService.getResourcePath(targetRid);
            String targetPathString = (targetRid == 0) ? "全部文件" : nodes.get(nodes.size() - 1).getTitle();

            List<Resource> resultList=resourceOperateService.copyResource(targetTid, targetRid,tid, originalRids, uid);
            String url = urlGenerator.getURL(targetTid, UrlPatterns.T_VIEW_R, targetRid+"", null);
            JsonArray array = LynxResourceUtils.getResourceJSON(resultList,uid);
            result.addProperty("state", MessageException.SUCCESS);
            result.addProperty("msg", "已成功复制到文件夹 <a href=\"" + url + "\">" + targetPathString + "</a>");
            result.add("resourceList", array);
            JsonUtil.write(response, result);
        } catch (RuntimeException re) {
            result.addProperty("state", MessageException.ERROR);
            result.addProperty("msg", "复制失败");
            JsonUtil.write(response, result);
            throw re;
        }
    }
    //@WebLog(method = "moveSelected", params = "targetRid,originalRids")
    @RequestMapping(params="func=moveSelected")
    public void moveSelected(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("originalRids")String originalRidsString,
                             @RequestParam("targetRid")int targetRid) {
        try {
            if(!haveTeamEditeAuth(VWBContext.getCurrentTid(), VWBSession.getCurrentUid(request))){
                writeResponse(response, MessageException.ERROR, "您没有权限移动此文件夹");
                return ;
            }
            int tid = VWBContext.getCurrentTid();
            String[] originalRidsStrings = originalRidsString.split(",");
            List<Integer> originalRids = new ArrayList<Integer>();
            for (String originalRid : originalRidsStrings) {
                originalRids.add(Integer.valueOf(originalRid));
            }
            if (originalRids.size() == 0) {
                writeResponse(response, MessageException.ERROR, "没有要移动的文件");
                return;
            }

            // 要移动的可能不仅仅是同级文档，所以需要循环判断
            List<Integer> ridsToMove = new ArrayList<Integer>();
            for (int originalRidIndex = 0; originalRidIndex < originalRids.size(); originalRidIndex++) {
                if (!isMovingToParent(originalRids.get(originalRidIndex), targetRid)) {
                    ridsToMove.add(originalRids.get(originalRidIndex));
                }
            }
            //          boolean partialMove = false;
            if (ridsToMove.size() != originalRids.size()) {
                originalRids = ridsToMove;
                //              partialMove = true;
            }
            if (originalRids.size() == 0) {
                writeResponse(response, MessageException.WARNING, "文档已存在于目标路径中");
                return ;
            }
            for (int originalRid : originalRids) {
                if (originalRid == targetRid) { // 1.不能移动到自身
                    writeResponse(response, MessageException.ERROR, "不能将文件夹移动到自身");
                    return ;
                }
                // 2.不能移动到自身的子文件夹中
                List<Resource> descendants = folderPathService.getDescendants(tid, originalRid);
                for(Resource descendant : descendants) {
                    if (targetRid == descendant.getRid()) {
                        writeResponse(response, MessageException.ERROR, "不能将文件夹移动到其子目录中");
                        return ;
                    }
                }
            }
            List<Resource> nodes = folderPathService.getResourcePath(targetRid);
            String targetPathString = (targetRid == 0) ? "全部文件" : nodes.get(nodes.size() - 1).getTitle();
            String uid = VWBSession.getCurrentUid(request);
            resourceOperateService.moveResource(tid, targetRid, originalRids,uid);
            LOG.info("用户uid="+uid+"将rid="+originalRids+"移动到rid="+targetRid);
            String url = urlGenerator.getURL(tid, UrlPatterns.T_VIEW_R, targetRid+"", null);
            //          if (partialMove) { //部分文档已移至目标文件夹，部分文档已存在于目标路径中
            //              writeResponse(response, SUCCESS, "已成功移至文件夹 <a href=\"" + url + "\">" + targetPathString + "</a>， 部分文档已存在于目标路径中");
            //          } else {
            //              writeResponse(response, SUCCESS, "已成功移至文件夹 <a href=\"" + url + "\">" + targetPathString + "</a>");
            //          }
            writeResponse(response, MessageException.SUCCESS, "已成功移至文件夹 <a href=\"" + url + "\">" + targetPathString + "</a>");
        } catch (RuntimeException re) {
            writeResponse(response, MessageException.ERROR, "移动失败");
            throw re;
        }
    }
    //@WebLog(method = "list", params = "originalRid,rid")
    @RequestMapping(params="func=list")
    public void list(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam("rid") String ridStr,
                     @RequestParam("originalRid") String originalRidStr) {
        if(isPanQuery(request)){
            dealPanList(request, ridStr, originalRidStr, response);
            return;
        }
        int rid = getRid(ridStr,0);
        int originalRid = getRid(originalRidStr, -1);
        if (rid == 0) { // 根文件夹需要特殊处理
            JsonArray rootArray = new JsonArray();
            JsonObject rootJsonObject = new JsonObject();
            rootJsonObject.addProperty("data", "全部文件");
            JsonObject attr = new JsonObject();
            attr.addProperty("rid", "node_0");
            rootJsonObject.add("attr", attr);
            // 开始写root的子文件（夹）
            JsonArray childrenJson = new JsonArray();
            int tid = VWBContext.getCurrentTid();
            List<Resource> childrenList = folderPathService.getChildrenFolder(tid, rid);
            if (childrenList.size() != 0) { // root有子文件夹
                int ridToDeal = -1;
                if (originalRid != -1) {
                    Resource originalRes=resourceService.getResource(originalRid);
                    if(originalRes.getTid()==tid){
                        List<Resource> parentResources = folderPathService.getResourcePath(originalRid);
                        if(parentResources.size() > 1) {
                            //每一层目录都有展开
                            ridToDeal = parentResources.get(0).getRid();
                            Resource lastResource = parentResources.get(parentResources.size() - 2);
                            JsonArray lastJsonArray = getChildrenJSONArray(lastResource.getRid());
                            //由深向浅逐级加入目录
                            for (int index = parentResources.size() - 2; index >= 0 ; index--) {
                                Resource resource = parentResources.get(index);
                                JsonObject tmpObject = resourceToJSONObject(resource, true);
                                tmpObject.add("children", lastJsonArray);
                                if (index == 0) {
                                    //最后将顶级目录加入列表
                                    childrenJson.add(tmpObject);
                                } else {
                                    lastJsonArray = getChildrenJSONArray(parentResources.get(index - 1).getRid(), resource.getRid());
                                    lastJsonArray.add(tmpObject);
                                }
                            }
                        }
                    }
                }
                for (Resource child : childrenList) {
                    if (child.getRid() == ridToDeal) {
                        // 跳过该目录
                        continue;
                    }
                    childrenJson.add(resourceToJSONObject(child, false));
                }
                rootJsonObject.add("children", childrenJson);
                rootJsonObject.addProperty("state", "open");
                attr.addProperty("rel", "folder");
            } else { // root没有子文件夹
                attr.addProperty("rel", "default");
            }

            rootArray.add(rootJsonObject);
            JsonUtil.write(response, rootArray);
        } else {
            JsonArray childrenJson = getChildrenJSONArray(rid);
            JsonUtil.write(response, childrenJson);
        }
    }

    @RequestMapping(params="func=queryTask")
    public void queryTask(@RequestParam("taskId")String taskId,@RequestParam("queryTime")String queryTime,HttpServletResponse response){
        PipeTaskStatus status;
        try {
            status = resourcePipeAgentService.query(taskId);
            JsonObject msg = new JsonObject();
            dealStatus(status, msg);
            msg.addProperty("queryTime", queryTime);
            JsonUtil.write(response, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pan2pan(String originalRid, String originalRids, String targetRidStr, PanAcl acl, HttpServletRequest request, HttpServletResponse response)
            throws MessageException{

        checkPan2PanTarget(originalRids, targetRidStr);

        JsonObject result=new JsonObject();
        List<PanResourceBean> beans = getPanOriginalResource(originalRid, originalRids, acl);
        if(beans.size() == 0){
            new MessageException("请选择文件", MessageException.WARNING);
        }
        int count = resourcePipeAgentService.caculateResourceCount(beans, acl);
        CopyCount c = validateCopyCount(request, count,"copy");
        if(c!=null){
            throw new MessageException(c.getErrorMessage(), MessageException.ERROR);
        }
        if("0".equals(targetRidStr)){
            targetRidStr = encode("/");
        }
        String targetRid = decode(targetRidStr);

        //验证空间是否够用
        long total = 0;
        for(PanResourceBean r : beans){
            total+=r.getSize();
        }
        checkPanSpace(acl,total);

        List<PanResourceBean> resourceList = new ArrayList<PanResourceBean>();
        SimpleUser user = aoneUserService.getSimpleUserByUid(acl.getUid());
        for(PanResourceBean item : beans){
            PanMeta meta = null;
            try {
                meta = panService.copy(
                    acl, item.getPath(), (targetRid.endsWith("/") ?
                                          targetRid : targetRid +"/")
                    + item.getTitle());
            } catch (MeePoException e) {
                result.addProperty("state", MessageException.ERROR);
                String errMsg = "复制操作失败.";
                try {
                    JsonObject errJson = new Gson().fromJson(
                        e.getMessage(), JsonObject.class);
                    errMsg = errJson.get("user_message").getAsString();
                } catch (JsonParseException e1) {
                    LOG.debug("Failed to parse error message.", e1);
                }
                result.addProperty("msg", errMsg);
                JsonUtil.write(response, result);
                return;
            }
            resourceList.add(MeePoMetaToPanBeanUtil.transfer(meta, user));
        }
        result.addProperty("state", MessageException.SUCCESS);
        String url = urlGenerator.getAbsoluteURL(UrlPatterns.PAN_VIEW, targetRidStr , null);
        result.addProperty("msg", "已成功复制到文件夹 <a href=\"" + url + "\">" + getTargetName(targetRid) + "</a>");
        if(StringUtils.isEmpty(originalRid)){
            result.add("resourceList", LynxResourceUtils.getPanResourceJsonList(resourceList,acl.getUid()));
        }else{
            result.add("resource", LynxResourceUtils.getPanResourceJson(resourceList.get(0), acl.getUid()));
        }

        JsonUtil.write(response, result);
        return;
    }

    private boolean checkPan2PanTarget(String originalRidsString, String targetRid) throws MessageException{
        String[] originalRidsArr = originalRidsString.split(",");
        targetRid = decode(targetRid);
        for(String s : originalRidsArr){
            String rid = decode(s);
            if (rid.equals(targetRid)) {
                throw new MessageException("不能将文件夹移动到自身", MessageException.WARNING);
            }
            if (isDescendant(rid, targetRid)) {
                throw new MessageException("不能将文件夹移动到其子目录中", MessageException.WARNING);
            }
        }
        return true;
    }

    private boolean isDescendant(String originalRid, String targetRid) {
        return targetRid.startsWith(originalRid+"/");
    }

    private int getRid(String ridStr,int de) {
        int rid = de;
        try{
            rid = Integer.parseInt(ridStr);
        }catch(Exception e){}
        return rid;
    }

    private JsonObject resourceToJSONObject(Resource resource, boolean open) {
        JsonObject result = new JsonObject();
        int rid = resource.getRid();
        String title = resource.getTitle();
        result.addProperty("data", title);
        JsonObject attr = new JsonObject();
        attr.addProperty("rid", "node_" + rid);
        attr.addProperty("rel", "folder");
        result.add("attr", attr);
        if (open) {
            result.addProperty("state", "open");
        } else {
            result.addProperty("state", "closed");
        }
        return result;
    }

    private CopyCount validateCopyCount(HttpServletRequest request,List<Resource> descendants,String type){
        int count =0;
        if(descendants!=null){
            count = descendants.size();
        }
        return validateCopyCount(request, count,type);
    }
    private CopyCount validateCopyCount(HttpServletRequest request,int count,String type){

        VWBContainer container = VWBContext.createContext(request,"plain").getContainer();
        int copyLimit  = Integer.parseInt(container.getProperty("ddl.resource.copyLimit"));
        String uid = VWBSession.getCurrentUid(request);
        UserCopyCount uc = userCopyCountService.getUserCopyCount(uid);
        if(uc!=null){
            if(uc.getCount()>copyLimit){
                CopyCount c = new CopyCount();
                c.setErrorMessage("您一天只能复制"+ copyLimit +"个文件");
                LOG.warn("用户"+uid+"复制数量超过"+ copyLimit +"条");
                return c;
            }
        }
        userCopyCountService.updateCopyCount(uid, count);

        if(!validateCopyFrequency(uid, request)){
            CopyCount c = new CopyCount();
            c.setErrorMessage("您复制速度太快，请休息会！");
            LOG.warn("用户"+uid+"复制速度太快");
            return c;
        }
        //LOG.info("用户"+uid+"在tid="+VWBContext.getCurrentTid()+";teamCode="+VWBContext.getCurrentTeamCode()+";一次复制了"+count+"个文件");
        return null;
    }

    private boolean validateCopyFrequency(String uid,HttpServletRequest request){
        Date d = (Date)request.getSession().getAttribute("lastCopyDate");
        request.getSession().setAttribute("lastCopyDate", new Date());
        if(d==null){
            return true;
        }else{
            //保证两次提交时间间隔1秒
            return (d.getTime()+1000)<System.currentTimeMillis();
        }
    }
    private JsonArray getChildrenJSONArray(int rid) {
        JsonArray childrenJson = new JsonArray();
        int tid = VWBContext.getCurrentTid();
        List<Resource> childrenList = folderPathService.getChildrenFolder(tid, rid);
        for (Resource child : childrenList) {
            childrenJson.add(resourceToJSONObject(child, false));
        }
        return childrenJson;
    }

    private JsonArray getChildrenJSONArray(int rid, int ignoreRid) {
        JsonArray childrenJson = new JsonArray();
        int tid = VWBContext.getCurrentTid();
        List<Resource> childrenList = folderPathService.getChildrenFolder(tid, rid);
        for (Resource child : childrenList) {
            if (child.getRid() == ignoreRid) {
                continue;
            }
            childrenJson.add(resourceToJSONObject(child, false));
        }
        return childrenJson;
    }

    private static void writeResponse(HttpServletResponse response, int state, String message) {
        JsonObject msg = new JsonObject();
        msg.addProperty("state", state);
        msg.addProperty("msg", message);
        JsonUtil.write(response, msg);
    }

    private boolean isMovingToParent(int originalRid, int targetRid) {
        Resource resource = resourceService.getResource(originalRid);
        return (resource != null && resource.getBid() == targetRid);
    }

    private static class CopyCount{
        private boolean status;
        private String errorType;
        private String errorMessage;
        public boolean isStatus() {
            return status;
        }
        public void setStatus(boolean status) {
            this.status = status;
        }
        public String getErrorType() {
            return errorType;
        }
        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private boolean isPanCopy(HttpServletRequest request){
        if(isTeam2Pan(request.getParameter("targetTid"))){
            return true;
        }
        if(isPan2Team(request.getParameter("originalRid"), request.getParameter("originalRids"))){
            return true;
        }
        return false;
    }

    private boolean isPan2Pan(HttpServletRequest request){
        if(isTeam2Pan(request.getParameter("targetTid")) && isPan2Team(request.getParameter("originalRid"), request.getParameter("originalRids"))){
            return true;
        }
        return false;
    }

    private boolean isTeam2Pan(String targetTid){
        if("pan".equals(targetTid)){
            return true;
        }
        return false;
    }

    private boolean isPan2Team(String originalRid, String originalRids){
        String code = "";
        if(!StringUtils.isEmpty(originalRid)){
            code = originalRid;
        }else if(!StringUtils.isEmpty(originalRids)){
            code = originalRids;
        }
        String rid = decode(code);
        if(rid.contains("/")){
            return true;
        }
        return false;
    }

    private String checkTeamSpace(int teamId, List<PanResourceBean> beans){
        List<Resource> resList = new ArrayList<Resource>();
        for(PanResourceBean item : beans){
            Resource r = new Resource();
            r.setSize(item.getSize());
            resList.add(r);
        }
        if(!teamSpaceSizeService.validateTeamSize(teamId, resList)){
            return "团队空间已满不能进行复制";
        }
        return "";
    }

    private void checkPanSpace(PanAcl acl, long total) throws MessageException{
        MeePoUsage usage = null;
        try {
            usage = panService.usage(acl);
        } catch (MeePoException e) {
            throw new MessageException(e.getMessage());
        }
        long free = usage.quota - usage.used;

        if(total > free){
            throw new MessageException("个人空间同步版已满不能进行复制", MessageException.WARNING);
        }
    }

    private String dealPanAndTeamCopy(HttpServletRequest request,HttpServletResponse response) throws MessageException {
        PanAcl acl = PanAclUtil.getInstance(request);
        String tar = request.getParameter("targetTid");
        String taskId = null;
        if (isTeam2Pan(tar)){
            //team到pan
            List<Resource> beans = getOriginalResource(request);
            if(beans.size() == 0){
                throw new MessageException("协作文档不支持复制到个人空间同步盘.", MessageException.WARNING);
            }

            CopyCount c = validateCopyCount(request, beans, "copy");
            if(c!=null){
                throw new MessageException(c.getErrorMessage(), MessageException.ERROR);
            }

            //验证空间是否够用
            long total = 0;
            for(Resource r : beans){
                total+=r.getSize();
            }

            checkPanSpace(acl,total);

            String teamCode = VWBContext.getCurrentTeamCode();
            String targetRid = decode(request.getParameter("targetRid"));
            //pan的根路径为/
            if("0".equals(targetRid)){
                targetRid="/";
            }
            try {
                taskId = resourcePipeAgentService.team2Meepo(beans, teamCode, targetRid, acl.getUid(), acl.getUmtToken());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{


            //pan到team
            cacheService.set(APIPanOperateLogController.TOKEN_CACHE_PREFIX + acl.getUid(), acl.getUmtToken());

            int tid = Integer.parseInt(tar);
            List<PanResourceBean> beans = getPanOriginalResource(request.getParameter("originalRid"), request.getParameter("originalRids"), acl);
            if(beans.size() == 0){
                return "请选择文件.";
            }

            int count = resourcePipeAgentService.caculateResourceCount(beans, acl);
            CopyCount c = validateCopyCount(request, count,"copy");
            if(c!=null){
                throw new MessageException(c.getErrorMessage(), MessageException.ERROR);
            }

            int targetRid = getRid(request.getParameter("targetRid"),0);
            String teamCode = teamService.getTeamByID(tid).getName();
            try {
                taskId = resourcePipeAgentService.meepo2Team(beans, targetRid, teamCode, acl.getUid(), acl.getUmtToken());
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }

        PipeTaskStatus status;
        try {
            status = resourcePipeAgentService.query(taskId);
            JsonObject msg = new JsonObject();
            msg.addProperty("state", MessageException.SUCCESS);
            msg.addProperty("type", "meepoCopy");
            msg.addProperty("msg", "");
            dealStatus(status, msg);
            JsonUtil.write(response, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<PanResourceBean> getPanOriginalResource(String originalRid, String originalRids, PanAcl acl) {
        List<PanResourceBean> result = new ArrayList<PanResourceBean>();
        String or = decode(originalRid);
        String originalRidsStr = originalRids;
        if(!StringUtils.isEmpty(or)){
            try {
                MeePoMeta meta = panService.ls(acl, or, false);
                result.add(MeePoMetaToPanBeanUtil.transfer(meta,null));
            } catch (MeePoException e) {
                e.printStackTrace();
            }

        }else if(!StringUtils.isEmpty(originalRidsStr)){
            String[] ss = originalRidsStr.split(",");
            List<String> ors = new ArrayList<String>();
            for(String s:ss){
                if(!StringUtils.isEmpty(s)){
                    String de = decode(s);
                    if(!StringUtils.isEmpty(de)){
                        ors.add(de);
                    }
                }
            }
            for(String s : ors){
                try {
                    MeePoMeta meta = panService.ls(acl, s, false);
                    result.add(MeePoMetaToPanBeanUtil.transfer(meta,null));
                } catch (MeePoException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void dealStatus(PipeTaskStatus status,JsonObject obj) {
        if(status!=null){
            obj.addProperty("total", status.getTotal());
            obj.addProperty("success", status.getSuccess());
            obj.addProperty("waiting", status.getWaiting());
            obj.addProperty("processing", status.getProcessing());
            obj.addProperty("failed", status.getFailed());
            obj.addProperty("taskId", status.getTaskId());
            obj.addProperty("status", status.getStatus());
            obj.add("subTasks", new Gson().toJsonTree(status.getSubTaskList()));
        }
    }

    private List<Resource> getOriginalResource(HttpServletRequest request){
        List<Resource> result = new ArrayList<Resource>();
        String originalRidStr = request.getParameter("originalRid");
        String originalRidsStr = request.getParameter("originalRids");
        if(!StringUtils.isEmpty(originalRidStr)){
            Resource r = resourceService.getResource(Integer.parseInt(originalRidStr));
            result.add(r);
        }else if(!StringUtils.isEmpty(originalRidsStr)){
            String[] originalRidsStrings = originalRidsStr.split(",");
            List<Integer> originalRids = new ArrayList<Integer>();
            for (String originalRid : originalRidsStrings) {
                originalRids.add(Integer.valueOf(originalRid));
            }
            List<Resource> rs = resourceService.getResource(originalRids);
            for(Resource r : rs){
                if(r.isFile()){
                    result.add(r);
                }
            }
        }
        return result;
    }

    private void dealPanList(HttpServletRequest request,String rid,String originalRid,HttpServletResponse response){
        if("0".equals(rid) || "/".equals(rid)){
            // 根文件夹需要特殊处理
            JsonArray rootArray = new JsonArray();
            JsonObject rootJsonObject = new JsonObject();
            rootJsonObject.addProperty("data", "全部文件");
            JsonObject attr = new JsonObject();
            attr.addProperty("rid", "node_0");
            rootJsonObject.add("attr", attr);
            // 开始写root的子文件（夹）
            JsonArray childrenJson = new JsonArray();
            try {
                MeePoMeta meta = panService.ls(PanAclUtil.getInstance(request), "/", true);
                if(meta.contents!=null&&meta.contents.length>0){
                    String originalFirstName = "";
                    if(!"-1".equals(originalRid)){
                        originalFirstName = getOriginalFirstName(originalRid);
                        if(originalRid.contains("/"))
                            addChildrenDir(childrenJson, rid, originalRid, request);
                    }
                    for(MeePoMeta me : meta.contents){
                        if(me.restorePath.equals(originalFirstName)||!me.isDir){
                            continue;
                        }
                        childrenJson.add(resourceToJSONObject(me, false));
                    }
                    rootJsonObject.add("children", childrenJson);
                    rootJsonObject.addProperty("state", "open");
                    attr.addProperty("rel", "folder");
                }else{
                    attr.addProperty("rel", "default");
                }
                rootArray.add(rootJsonObject);
                JsonUtil.write(response, rootArray);
            } catch (MeePoException e) {
            }
        }else{
            PanAcl acl = PanAclUtil.getInstance(request);
            MeePoMeta children;
            try {
                children = panService.ls(acl, decode(rid), true);
                JsonArray childrenJson = getChildrenJSONArray(children.contents);
                JsonUtil.write(response, childrenJson);
            } catch (MeePoException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPanQuery(HttpServletRequest request){
        try{
            Integer.parseInt(request.getParameter("targetTid"));
            return false;
        }catch(Exception e){}
        String url = request.getRequestURI();
        return url.contains("/pan/");

    }

    private void addChildrenDir(JsonArray childrenJson, String rid,
                                String originalRid,HttpServletRequest request)
            throws MeePoException{
        PanAcl acl = PanAclUtil.getInstance(request);
        MeePoMeta children = panService.ls(acl, originalRid, true);
        JsonArray lastJsonArray = getChildrenJSONArray(children.contents);
        List<String> path = getPanPath(originalRid);
        for (int index = path.size() - 2; index >= 0 ; index--) {
            MeePoMeta resource = panService.ls(acl, path.get(index), true);
            JsonObject tmpObject = resourceToJSONObject(resource, true);
            tmpObject.add("children", lastJsonArray);
            if (index == 0) {
                childrenJson.add(tmpObject);
            } else {
                lastJsonArray = getChildrenJSONArray(resource.contents);
                lastJsonArray.add(tmpObject);
            }
        }
    }

    private List<String> getPanPath(String originalRid) {
        List<String> path = new ArrayList<String>();
        String[] ps = originalRid.split("/");
        StringBuilder sb = new StringBuilder();
        for(String s: ps){
            sb.append("/").append(s);
            path.add(sb.toString());
        }

        return path;
    }

    private JsonArray getChildrenJSONArray(MeePoMeta[] contents) {
        JsonArray result = new JsonArray();
        if(contents!=null){
            for(MeePoMeta me : contents){
                if(me.isDir)
                    result.add(resourceToJSONObject(me, false));

            }
        }
        return result;
    }

    private JsonObject resourceToJSONObject(MeePoMeta me, boolean open) {
        PanResourceBean resource = MeePoMetaToPanBeanUtil.transfer(me, null);
        JsonObject result = new JsonObject();
        String rid = resource.getRid();
        String title = resource.getTitle();
        result.addProperty("data", title);
        JsonObject attr = new JsonObject();
        attr.addProperty("rid", "node_" + rid);
        attr.addProperty("rel", "folder");
        result.add("attr", attr);
        if (open) {
            result.addProperty("state", "open");
        } else {
            result.addProperty("state", "closed");
        }
        return result;
    }

    private String getPanFilename(String panRid) {
        if(panRid!=null && panRid.length()>0){
            int pos = panRid.lastIndexOf("/");
            if(pos>0){
                return panRid.substring(pos);
            }else{
                return panRid;
            }
        }
        return null;
    }

    private String getOriginalFirstName(String originalRid) {
        if(originalRid!=null&&originalRid.length()>0){
            int index = originalRid.indexOf("/", 1);
            if(index>0){
                return originalRid.substring(0, index);
            }else{
                return "/";
            }
        }
        return null;
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

    private String getTargetName(String name){
        if(StringUtils.isEmpty(name) || "/".equals(name)){
            return "所有文件";
        }else{
            int pos = name.lastIndexOf("/");
            return name.substring(pos+1);
        }
    }

    private String encode(String s){
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    private String decode(String s){
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (Exception e) {
            return "";
        }
    }

    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private UserCopyCountService userCopyCountService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    @Autowired
    private IPanService panService;
    @Autowired
    private ResourcePipeAgentService resourcePipeAgentService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private ICacheService cacheService;
    @Autowired
    private AoneUserService aoneUserService;
}
