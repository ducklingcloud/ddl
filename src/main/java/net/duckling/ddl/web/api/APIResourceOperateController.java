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
package net.duckling.ddl.web.api;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainer;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.copy.UserCopyCount;
import net.duckling.ddl.service.copy.UserCopyCountService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.AbstractRecommendContrller;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 移动端resource操作
 * @author zhonghui
 *
 */
@Controller
@RequestMapping("/api/resourceOperate")
public class APIResourceOperateController extends AbstractRecommendContrller{

    private static final Logger LOG = Logger.getLogger(APIResourceOperateController.class);

    private final static String EXISTED_RETURN = "return"; //若文件夹存在，直接返回

    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private UserCopyCountService userCopyCountService;

    @RequestMapping(params="func=move")
    @RequirePermission(target="team", operation="edit")
    public void moveResource(HttpServletRequest request,HttpServletResponse response,@RequestParam("originalRid")int originalRid,
                             @RequestParam("targetRid")int targetRid){
        JSONObject result = new JSONObject();
        if (originalRid == targetRid) { // 1.不能移动到自身
            result.put("result", false);
            result.put("message", "不能将文件夹移动到自身");
            JsonUtil.writeJSONObject(response, result);
            return ;
        }
        if (isMovingToParent(originalRid, targetRid)) {
            result.put("result", false);
            result.put("message", "您要移动的文件已经存在于目标路径");
            JsonUtil.writeJSONObject(response, result);
            return ;
        }
        int tid = VWBContext.getCurrentTid();
        // 2.不能移动到自身的子文件夹中
        List<Resource> descendants = folderPathService.getDescendants(tid, originalRid);
        for(Resource descendant : descendants) {
            if (targetRid == descendant.getRid()) {
                result.put("result", false);
                result.put("message", "不能将文件夹移动到自身的子文件夹中");
                JsonUtil.writeJSONObject(response, result);
                return ;
            }
        }
        try {
            List<Resource> nodes = folderPathService.getResourcePath(originalRid);
            String originalPath = nodes.get(nodes.size() - 1).getTitle();
            nodes = folderPathService.getResourcePath(targetRid);
            String targetPathString = "全部文件";
            for (Resource resource : nodes) {
                targetPathString += "/" + resource.getTitle();
            }
            resourceOperateService.moveResource(tid, targetRid, originalRid,VWBSession.getCurrentUid(request));
            result.put("result", true);
            result.put("message", "文档“" + originalPath + "”成功移动到到目录“" + targetPathString + "”");
            JsonUtil.writeJSONObject(response, result);
        } catch (RuntimeException re) {
            result.put("result", false);
            result.put("message", "移动失败");
            JsonUtil.writeJSONObject(response, result);
        }
    }

    @RequestMapping(params="func=copy")
    @RequirePermission(target="team", operation="edit")
    public void copyFileTo(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam("originalRid")int originalRid,
                           @RequestParam("targetRid")int targetRid) {
        JSONObject result = new JSONObject();
        if (originalRid == targetRid) { // 1.不能复制到自身
            result.put("result", false);
            result.put("message", "不能将文件夹复制到自身");
            JsonUtil.writeJSONObject(response, result);
            return ;
        }
        int tid = VWBContext.getCurrentTid();
        int targetTid = getDestTid(request, tid);
        if(!haveTeamEditeAuth(targetTid, VWBSession.getCurrentUid(request))){
            result.put("result", false);
            result.put("message", "您没有权限移动此文件夹");
            JsonUtil.writeJSONObject(response, result);
            return ;
        }
        // 2.不能复制到自身的子文件夹中
        List<Resource> descendants = folderPathService.getDescendants(tid, originalRid);
        for(Resource descendant : descendants) {
            if (targetRid == descendant.getRid()) {
                result.put("result", false);
                result.put("message", "不能将文件夹复制到其子目录中");
                JsonUtil.writeJSONObject(response, result);
                return ;
            }
        }
        CopyCount c = validateCopyCount(request, descendants,"copy");
        if(c!=null){
            result.put("result", false);
            result.put("message", c.getErrorMessage());
            JsonUtil.writeJSONObject(response, result);
            return;
        }
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
        String uid = context.getCurrentUID();
        try {
            List<Resource> nodes = folderPathService.getResourcePath(originalRid);
            String originalPath = nodes.get(nodes.size() - 1).getTitle();
            nodes = folderPathService.getResourcePath(targetRid);
            String targetPathString = "全部文件";
            for (Resource resource : nodes) {
                targetPathString += "/" + resource.getTitle();
            }

            resourceOperateService.copyResource(targetTid, targetRid,tid, originalRid, uid);
            result.put("result", true);
            result.put("message", "文档“" + originalPath + "”成功复制到目录“" + targetPathString + "”");
            JsonUtil.writeJSONObject(response, result);
        } catch (RuntimeException re) {
            result.put("result", false);
            result.put("message", "复制失败");
            JsonUtil.writeJSONObject(response, result);
        }
    }

    @RequestMapping(params="func=rename")
    @RequirePermission(target="team", operation="edit")
    public void rename(HttpServletRequest request,HttpServletResponse response,@RequestParam("rid")int rid,@RequestParam("fileName")String fileName){
        JSONObject result = new JSONObject();
        if(StringUtils.isEmpty(fileName)){
            result.put("result", false);
            result.put("message", "文件名不能为空");
        }else{
            String uid = VWBSession.getCurrentUid(request);
            boolean r = resourceOperateService.renameResource(VWBContext.getCurrentTid(), rid, uid, fileName);
            if(!r){
                result.put("result", false);
            }else{
                Resource resource = resourceService.getResource(rid);
                result.put("result", true);
                result.put("resource", JsonUtil.getJSONObjectFromResource(resource));
            }
        }

        JsonUtil.writeJSONObject(response, result);
    }

    @RequestMapping(params="func=createFolder")
    @RequirePermission(target="team", operation="edit")
    public void createFolder(HttpServletRequest request,HttpServletResponse response,@RequestParam("parentRid")int parentRid,@RequestParam("fileName")String fileName){
        JSONObject result = new JSONObject();
        if(StringUtils.isEmpty(fileName)){
            result.put("result", false);
            result.put("message", "文件名不能为空");
            JsonUtil.writeJSONObject(response, result);
            return;
        }
        if(StringUtil.illCharCheck(request, response, "fileName")){
            return;
        }

        String uid = VWBSession.getCurrentUid(request);
        int tid = VWBContext.getCurrentTid();
        Resource r = null;

        //如果存在重名直接返回
        if(EXISTED_RETURN.equals(request.getParameter("existed"))){
            //判断是否有重名
            List<Resource> rs = folderPathService.getResourceByName(tid, parentRid, LynxConstants.TYPE_FOLDER, fileName);
            if(rs.size()>0){
                r = rs.get(0);
            }
        }

        if(r==null){
            r = new Resource();
            r.setTid(tid);
            r.setBid(parentRid);
            r.setCreateTime(new Date());
            r.setCreator(uid);
            r.setItemType(LynxConstants.TYPE_FOLDER);
            r.setTitle(fileName);
            r.setLastEditor(uid);
            r.setLastEditorName(aoneUserService.getUserNameByID(uid));
            r.setLastEditTime(new Date());
            r.setStatus(LynxConstants.STATUS_AVAILABLE);
            resourceOperateService.createFolder(r);
        }

        result.put("result", true);
        result.put("resource", JsonUtil.getJSONObjectFromResource(r));
        JsonUtil.writeJSONObject(response, result);
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
                c.setErrorMessage("您一天只能复制" + copyLimit + "个文件");
                LOG.warn("用户"+uid+"复制数量超过" + copyLimit + "条");
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
        LOG.info("用户"+uid+"在tid="+VWBContext.getCurrentTid()+";teamCode="+VWBContext.getCurrentTeamCode()+";一次复制了"+count+"个文件");
        return null;
        //      if(count<500){
        //          return null;
        //      }else{
        //          CopyCount c = new CopyCount();
        //          c.setStatus(false);
        //          String name = "";
        //          if("copy".equals(type)){
        //              name = "复制";
        //          }else{
        //              name = "移动";
        //          }
        //          c.setErrorMessage("您一次最多只能"+name+"500个文件");
        //          return c;
        //      }
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

    private boolean isMovingToParent(int originalRid, int targetRid) {
        Resource resource = resourceService.getResource(originalRid);
        return (resource != null && resource.getBid() == targetRid);
    }
    private boolean haveTeamEditeAuth(int tid,String uid){
        return authorityService.haveTeamEditeAuth(tid, uid);
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
}
