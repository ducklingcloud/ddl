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

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.util.DateUtil;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.falcon.api.cache.ICacheService;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.meepotech.sdk.MeePoException;

/**
 * 同步盘操作日志
 * @author Brett
 *
 */
@Controller
@RequestMapping("/api/pan/log")
public class APIPanOperateLogController {
    public final static String TOKEN_CACHE_PREFIX = "panOperateLog."; //缓存操作者的token，上传日志文件需要.
    public final static String TASK_TYPE_D2M = "d2m";
    public final static String TASK_TYPE_M2D = "m2d";
    public final static String SEPARATOR = ":";

    /**
     * 生成团队空间和meepo盘双向复制失败的日志文件。并上传到复制源文件夹下
     * @param request
     * @param response
     * @throws ParseException
     * @throws NoEnoughSpaceException
     * @throws MeePoException
     */
    @RequestMapping(method = RequestMethod.POST)
    public void log(HttpServletRequest request, HttpServletResponse response) throws ParseException, MeePoException{
        String pipeTaskStatus = request.getParameter("pipeTaskStatus");
        SubTasksStats stats = parseSubTasksStats(pipeTaskStatus);
        Map<String, List<SubTask>> mapList = getSubTaskMap(stats.getSubTaskList(), stats.getType());

        for(String key : mapList.keySet()){

            List<SubTask> subTaskList  = mapList.get(key);
            //从meepo盘到团队空间
            if(TASK_TYPE_M2D.equals(stats.getType())){
                ResourceFolder targetFolder = getResourceFolder(Integer.parseInt(stats.getTargetFolder()));
                String content = getFailedLog(subTaskList, stats.getTotal(),stats.getSourceTeam(),stats.getTargetTeam(), key, targetFolder.getFolder());
                byte[] bytes = content.getBytes();
                PanAcl acl = new PanAcl();
                acl.setUid(stats.getUser());
                Object token = cacheService.get(TOKEN_CACHE_PREFIX + stats.getUser());
                if(token == null){
                    writeResponse(response, false, "umt token get failed from cache. user:" + stats.getUser());
                    LOG.info("umt token get failed from cache. user:" + stats.getUser());
                    return;
                }
                acl.setUmtToken(String.valueOf(token));
                InputStream inputStream = new ByteArrayInputStream(bytes);

                panService.upload(acl, inputStream, bytes.length, getFolder(key)+getFailedFilename(), true);
            }else{
                //从团队空间到meepo盘
                ResourceFolder sourceFolder = parseResourceFolder(key);
                String content = getFailedLog(subTaskList, stats.getTotal(),stats.getSourceTeam(),stats.getTargetTeam(), sourceFolder.getFolder(), stats.getTargetFolder());
                byte[] bytes = content.getBytes();

                InputStream inputStream = new ByteArrayInputStream(bytes);
                try {
                    operateService.upload(stats.getUser(), sourceFolder.getTid(), sourceFolder.getBid(), getFailedFilename(), bytes.length, inputStream, false, false, true, null);
                } catch (NoEnoughSpaceException e) {
                    writeResponse(response, false, e.getMessage());
                    return;
                }
            }

            LOG.info("pan operated log, pipeId: "+ stats.getPipeId());
            writeResponse(response, true, "executed success, pipeId:" + stats.getPipeId());
        }
    }

    private static void writeResponse(HttpServletResponse response, boolean result, String message) {
        JsonObject msg = new JsonObject();
        msg.addProperty("result", result);
        msg.addProperty("message", message);
        JsonUtil.write(response, msg);
    }

    /**
     * 日志内容
     * @return
     */
    private String getFailedLog(List<SubTask> subTaskList,int total, String sourceTeam, String targetTeam, String sourceFolder,String targetFolder){
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtil.getCurrentTime("yyyy年MM月dd日 HH时mm分,复制失败.\n\r"))
                .append("从").append(getTeamDesc(sourceTeam)).append("复制文件到")
                .append(getTeamDesc(targetTeam)).append(",目标路径为:\"").append(targetFolder)
                .append("\".失败文件列表：\n\r");

        for(int i=0; i<subTaskList.size(); i++){
            SubTask item = subTaskList.get(i);
            sb.append(i+1).append(". 文件路径: \"").append(sourceFolder).append(item.getFilename())
                    .append("\", 大小: \"").append(FileSizeUtils.getFileSize(Long.valueOf(item.size))).append("\";\n\r");
        }

        sb.append("\n\r请尝试重新复制.");
        return sb.toString();
    }

    /**
     * 相同目录的子任务map
     * @return
     */
    private Map<String, List<SubTask>> getSubTaskMap(List<SubTask> list, String taskType){
        Map<String, List<SubTask>> result = new HashMap<String, List<SubTask>>();
        for(SubTask item : list){
            List<SubTask> subList = null;
            String key = TASK_TYPE_M2D.equals(taskType) ? getFolder(item.getPath()) :
                    getResourceFolderString(getResourceFolder(Integer.valueOf(item.getPath())));
            if(result.get(key)==null){
                subList = new ArrayList<SubTask>();
                result.put(key, subList);
            }else{
                subList = result.get(key);
            }
            subList.add(item);
        }
        return result;
    }

    private ResourceFolder getResourceFolder(int rid){
        List<Resource> pathList = folderPathService.getResourcePath(rid);
        String folder = "/";
        for(int i=0; i<pathList.size()-1; i++){
            Resource item = pathList.get(i);
            folder += item.getTitle() + "/";
        }
        Resource r = pathList.get(pathList.size()-1);
        if(r.isFolder()){
            folder += r.getTitle() + "/";
        }
        ResourceFolder result = new ResourceFolder();
        result.setTid(r.getTid());
        result.setBid(r.getBid());
        result.setFolder(folder);
        return result;
    }

    private String getResourceFolderString(ResourceFolder folder){
        return folder.getTid() + SEPARATOR + folder.getBid() + SEPARATOR + folder.getFolder();
    }

    private ResourceFolder parseResourceFolder(String key){
        String [] tmp = key.split(SEPARATOR);
        ResourceFolder result = new ResourceFolder();
        result.setTid(Integer.valueOf(tmp[0]));
        result.setBid(Integer.valueOf(tmp[1]));
        result.setFolder(tmp[2]);
        return result;
    }

    private String getFolder(String path) {
        int pos = path.lastIndexOf("/");
        if (pos == -1) {
            return "/";
        }

        String parentPath = path.substring(0, pos);
        if (!parentPath.startsWith("/")) {
            parentPath = "/" + parentPath;
        }
        if (parentPath.endsWith("/")) {
            return parentPath;
        } else {
            return parentPath + "/";
        }
    }

    private String getTeamDesc(String teamName){
        String result = null;
        String panName = "\"个人空间同步版\"";
        if(teamName.equals("meepo")){
            result = panName;
        }else{
            Team t = teamService.getTeamByName(teamName);
            if(t.isPersonalTeam()){
                result = "\"个人空间\"";
            }else{
                result = "\"" + t.getDisplayName()+"\"团队";
            }
        }
        return result;
    }

    private String getFailedFilename(){
        return "[系统生成]复制文件失败日志"+DateUtil.getCurrentTime("yyyyMMddHHmmss") + ".txt";
    }

    /**
     * json转换为 java对象
     * @param desc
     * @return
     * @throws ParseException
     */
    private SubTasksStats parseSubTasksStats(String desc) throws ParseException {
        SubTasksStats stats = new SubTasksStats();
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(desc, JsonObject.class);
        JsonObject statsObj = obj.getAsJsonObject("subTasksStats");
        stats.setTotal(statsObj.get("total").getAsInt());
        stats.setFailed(statsObj.get("failed").getAsInt());
        stats.setPipeId(obj.get("pipeId").getAsInt());
        stats.setType(obj.get("type").getAsString());
        stats.setUser(obj.get("user").getAsString());
        stats.setSourceTeam(obj.get("sourceTeam").getAsString());
        stats.setTargetTeam(obj.get("targetTeam").getAsString());
        stats.setTargetFolder(obj.get("targetFolder").getAsString());
        if(stats.getFailed()>0){
            JsonArray subTasks = obj.getAsJsonArray("subTasks");
            for(int i=0; i < subTasks.size(); i++){
                JsonObject item = gson.fromJson(subTasks.get(i).getAsString(),
                                                JsonObject.class);
                if("failed".equals(item.get("status").getAsString())){
                    stats.getSubTaskList().add(assembleSubTask(item));
                }
            }
        }

        return stats;
    }

    private SubTask assembleSubTask(JsonObject obj){
        SubTask result = new SubTask();
        result.setId(obj.get("id").getAsInt());
        result.setPath(obj.get("path").getAsString());
        result.setFilename(obj.get("filename").getAsString());
        result.setSize(obj.get("size").getAsString());
        result.setStatus(obj.get("status").getAsString());
        return result;
    }

    private class ResourceFolder{
        private int tid;
        private int bid;
        private String folder;

        public int getTid() {
            return tid;
        }
        public void setTid(int tid) {
            this.tid = tid;
        }
        public int getBid() {
            return bid;
        }
        public void setBid(int bid) {
            this.bid = bid;
        }
        public String getFolder() {
            return folder;
        }
        public void setFolder(String folder) {
            this.folder = folder;
        }
    }

    /**
     * 操作状态信息
     * @author Brett
     *
     */
    private class SubTasksStats{
        private int total;
        private int failed;
        private int pipeId;
        private String type;
        private String user;
        private String sourceTeam;
        private String targetTeam;
        private String targetFolder;

        List<SubTask> subTaskList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTargetFolder() {
            return targetFolder;
        }

        public void setTargetFolder(String targetFolder) {
            this.targetFolder = targetFolder;
        }

        public List<SubTask> getSubTaskList() {
            if(subTaskList==null){
                subTaskList = new ArrayList<SubTask>();
            }
            return subTaskList;
        }

        public void setSubTaskList(List<SubTask> subTaskList) {
            this.subTaskList = subTaskList;
        }

        public int getPipeId() {
            return pipeId;
        }

        public void setPipeId(int pipeId) {
            this.pipeId = pipeId;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getSourceTeam() {
            return sourceTeam;
        }

        public void setSourceTeam(String sourceTeam) {
            this.sourceTeam = sourceTeam;
        }

        public String getTargetTeam() {
            return targetTeam;
        }

        public void setTargetTeam(String targetTeam) {
            this.targetTeam = targetTeam;
        }
    }

    /**
     * 操作子任务信息
     * @author Brett
     *
     */
    private class SubTask{
        private int id;
        private String path;
        private String filename;
        private String size;
        private String status;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getSize() {
            return size;
        }
        public void setSize(String size) {
            this.size = size;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getFilename() {
            return filename;
        }
        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

    private static final Logger LOG = Logger.getLogger(APIPanOperateLogController.class);
    @Autowired
    private ResourceOperateService operateService;
    @Autowired
    private IPanService panService;
    @Autowired
    private ICacheService cacheService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private TeamService teamService;
}
