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
package net.duckling.ddl.service.resource.impl;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.duckling.common.DucklingProperties;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.exception.MessageException;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PipeTaskStatus;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.ResourcePipeAgentService;
import net.duckling.ddl.service.resource.SubTask;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.web.bean.FileTypeHelper;
import net.duckling.ddl.web.controller.pan.PanResourceBean;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Service
public class ResourcePipeAgentServiceImpl implements ResourcePipeAgentService{
    @Autowired
    private DucklingProperties properties;

    private HttpClient httpClient;

    @Autowired
    private IPanService panService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;

    private static final Logger LOGGER = Logger.getLogger(ResourcePipeAgentServiceImpl.class);

    @Override
    public String meepo2Team(List<PanResourceBean> beans, int targetRid, String teamCode, String uid, String accessToken) throws IOException, MessageException {
        PanAcl acl = new PanAcl();
        acl.setUid(uid);
        acl.setUmtToken(accessToken);

        JsonObject obj = new JsonObject();
        obj.addProperty("taskType", "m2d");
        obj.addProperty("sourceTeam", "meepo");
        obj.addProperty("targetTeam", teamCode);
        obj.addProperty("uploadTargetFolder", targetRid+"");
        obj.addProperty("umtToken", accessToken);
        obj.addProperty("username", uid);
        JsonArray arr = new JsonArray();
        obj.add("metas", arr);
        int tid = teamService.getTeamByName(teamCode).getId();

        for(PanResourceBean bean :beans){
            if(bean.isFolder()){
                try {
                    Map<String, MeePoMeta> allMap = panService.tree(acl, bean.getPath());
                    Map<String,Resource> folderMap = createFolderAll(allMap, uid, tid, targetRid, bean.getPath());
                    for(String key : allMap.keySet()){
                        MeePoMeta item = allMap.get(key);
                        if(item.isDir){
                            Boolean isSelected = bean.getPath().equals(key) ? true : false;
                            Resource folder = folderMap.get(item.restorePath);
                            arr.add(getMeta(item.restorePath,item.name,item.size, String.valueOf(folder.getRid()), true, isSelected));
                        }else{
                            PathNamePair pair = parsePathName(item.restorePath);
                            Resource folder = folderMap.get(pair.getPath());
                            arr.add(getMeta(item.restorePath,item.name,item.size, String.valueOf(folder.getRid()), false, false));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    return null;
                }
            }else{
                arr.add(getMeta(decode(bean.getRid()),bean.getTitle(),bean.getSize(), "", false, true));
            }
        }

        //验证团队空间是容量是否足够
        List<Resource> resList = new ArrayList<Resource>();
        for(int i=0; i < arr.size(); i++){
            JsonObject item = arr.get(i).getAsJsonObject();
            Resource r = new Resource();
            r.setSize(Long.parseLong(item.get("fileSize").getAsString()));
            resList.add(r);
        }
        if(!teamSpaceSizeService.validateTeamSize(tid, resList)){
            throw new MessageException("团队空间已满不能进行复制");
        }

        JsonObject result = writeJson(obj);
        return result.get("pid").getAsString();
    }

    @Override
    public String team2Meepo(List<Resource> beans,String sourceTeamCode, String targetRid, String uid, String accessToken) throws IOException{
        JsonObject obj = new JsonObject();
        obj.addProperty("taskType", "d2m");
        obj.addProperty("sourceTeam", sourceTeamCode);
        obj.addProperty("targetTeam", "meepo");
        obj.addProperty("uploadTargetFolder", targetRid+"");
        obj.addProperty("umtToken", accessToken);
        obj.addProperty("username", uid);
        JsonArray arr = new JsonArray();
        obj.add("metas", arr);
        for(Resource bean :beans){
            arr.add(getMeta(bean.getRid()+"",bean.getTitle(),bean.getSize(),null,null,true));
        }
        JsonObject result = writeJson(obj);
        return result.get("pid").getAsString();
    }

    @Override
    public PipeTaskStatus query(String pipeTaskId) throws IOException {
        String result = queryStatus(pipeTaskId);
        //System.out.println(">>" + result);
        return parseResult(result,pipeTaskId);
    }

    @Override
    public int caculateResourceCount(List<PanResourceBean> beans, PanAcl acl){
        int i=0;
        for(PanResourceBean bean :beans){
            if(bean.isFolder()){
                Map<String, MeePoMeta> metaMap = null;
                try {
                    metaMap = panService.tree(acl, bean.getPath());
                } catch (MeePoException e) {
                    LOGGER.error(e.getMessage());
                }
                i=i+metaMap.size();
            }else{
                i++;
            }

        }
        return i;
    }

    /**
     * 预先创建好所有文件夹
     * @param metaMap
     * @param uid
     * @param tid
     * @param rootRid
     * @return
     */
    private Map<String,Resource> createFolderAll(Map<String, MeePoMeta> metaMap, String uid, int tid, int rootRid, String selectedPath){
        LinkedHashMap<String, MeePoMeta> folderMetaMap = sortMeta(metaMap);
        Map<String,Resource> resourceMap = new HashMap<String, Resource>();

        for(String key : folderMetaMap.keySet()){
            PathNamePair pair = parsePathName(key);
            Resource parentFolder = resourceMap.get(pair.getPath());
            int bid = parentFolder == null ? rootRid : parentFolder.getRid();
            boolean autoRename = key.equals(selectedPath) ? true : false;
            Resource r = createFolder(uid, tid, bid, pair.getName(), autoRename);
            resourceMap.put(key, r);
        }
        return resourceMap;
    }

    private PathNamePair parsePathName(String pathName){
        int lastPos = pathName.lastIndexOf("/");
        String path = pathName.substring(0, lastPos);
        String folderName = pathName.substring(lastPos+1);
        return new PathNamePair(path, folderName);
    }

    private Resource createFolder(String uid, int tid, int bid, String folderName, boolean autoRename){
        if(autoRename){
            List<Resource> resList = resourceService.getFileByTitle(tid, bid, folderName);
            if(resList.size()>0){
                folderName = resourceOperateService.getSerialFileName(tid, bid, folderName);
            }
        }
        Resource r = new Resource();
        r.setTid(tid);
        r.setBid(bid);
        r.setCreateTime(new Date());
        r.setCreator(uid);
        r.setItemType(LynxConstants.TYPE_FOLDER);
        r.setTitle(folderName);
        r.setLastEditor(uid);
        r.setLastEditorName(aoneUserService.getUserNameByID(uid));
        r.setLastEditTime(new Date());
        r.setStatus(LynxConstants.STATUS_AVAILABLE);
        int rid = resourceOperateService.createFolder(r);
        r.setRid(rid);
        return r;
    }

    private LinkedHashMap<String, MeePoMeta> sortMeta(Map<String, MeePoMeta> meta){
        List<String> keyList = new ArrayList<String>();
        for(String key : meta.keySet()){
            MeePoMeta item = meta.get(key);
            if(item.isDir){
                keyList.add(key);
            }
        }
        Collections.sort(keyList);

        LinkedHashMap<String, MeePoMeta> folderMeta = new LinkedHashMap<String, MeePoMeta>();
        for(String key : keyList){
            folderMeta.put(key, meta.get(key));
        }

        return folderMeta;
    }


    private JsonObject getMeta(String path,String title, long size, String uploadTargetFolder, Boolean isFolder, Boolean isSelected) {
        JsonObject obj = new JsonObject();
        obj.addProperty("filePath", path);
        obj.addProperty("fileName", title);
        obj.addProperty("fileSize", size+"");
        if(uploadTargetFolder!=null){
            obj.addProperty("uploadTargetFolder", uploadTargetFolder);
        }
        if(isFolder!=null){
            obj.addProperty("isFolder", isFolder);
        }
        if(isSelected!=null){
            obj.addProperty("isSelected", isSelected);
        }
        return obj;
    }

    private String decode(String s){
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private PipeTaskStatus parseResult(String result,String taskId) {
        PipeTaskStatus r = null;
        try {
            JsonObject obj = new Gson().fromJson(result, JsonObject.class);
            JsonObject status = obj.get("subTasksStats").getAsJsonObject();
            r = new PipeTaskStatus();
            r.setStatus(obj.get("pipeStatus").getAsString());
            r.setTaskId(taskId);
            r.setFailed(status.get("failed").getAsInt());
            r.setProcessing(status.get("processing").getAsInt() +
                            status.get("waiting").getAsInt());
            r.setSuccess(status.get("success").getAsInt());
            r.setTotal(status.get("total").getAsInt());

            JsonObject subTaskStatMap = obj.get("subTaskStatMap").getAsJsonObject();
            Map<String, JsonObject> map = new HashMap<String, JsonObject>();
            for (String key : subTaskStatMap.keySet()) {
                map.put(key, subTaskStatMap.get(key).getAsJsonObject());
            }

            SubTask st = null;
            JsonArray arr = obj.get("subTasks").getAsJsonArray();
            List<SubTask> subTaskList = new ArrayList<SubTask>();
            Gson gson = new Gson();
            for (int i=0; i<arr.size(); i++) {
                JsonObject item = gson.fromJson(arr.get(i).getAsString(),
                                                JsonObject.class);
                st = new SubTask();
                String fileName = "";
                if ("yes".equals(item.get("selectFlag").getAsString())) {
                    st.setId(item.get("id").getAsString());
                    st.setPath(item.get("path").getAsString());
                    if ("folder".equals(item.get("type").getAsString())) {
                        fileName = getPath(item.get("filename").getAsString());
                        st.setFileType("Folder");
                        st.setItemType("");
                    }else{
                        fileName = item.get("filename").getAsString();
                        st.setItemType("DFile");
                        st.setFileType(FileTypeHelper.getFileExt(fileName));
                    }
                    st.setFilename(fileName);
                    st.setSize(FileSizeUtils.getFileSize(Long.valueOf(item.get("size").getAsString())));
                    JsonObject selected = map.get(st.getPath());

                    st.setSubTotal(selected.get("total").getAsInt());
                    st.setSubSuccess(selected.get("success").getAsInt());
                    st.setSubFailed(selected.get("failed").getAsInt());
                    if(st.getSubTotal()==st.getSubSuccess()){
                        st.setStatus("success");
                    }else if(st.getSubTotal()==(st.getSubSuccess()+st.getSubFailed())){
                        st.setStatus("failed");
                    }
                    subTaskList.add(st);
                }
            }
            r.setSubTaskList(subTaskList);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return r;
    }

    private JsonObject writeJson(JsonObject json) throws IOException{
        PostMethod method = new PostMethod(getQueryDomain()+"/pipe");
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
        Header h = new Header();
        h.setName("content-type");
        h.setValue("application/json");
        method.addRequestHeader(h);
        StringRequestEntity body = new StringRequestEntity(json.toString());
        method.setRequestEntity(body);
        try {
            int status = getHttpClient().executeMethod(method);
            if (status >= 200 && status < 300){
                String result = getResponseBody(method.getResponseBodyAsStream());
                return new Gson().fromJson(result, JsonObject.class);
            }else{
                return null;
            }
        } catch (HttpException e) {
            new IOException(e);
        } catch (JsonParseException e) {
            new IOException(e);
        }finally{
            method.releaseConnection();
        }
        return null;
    }

    private String getQueryDomain(){
        return properties.getProperty("pipe.agent.domain");
    }


    private HttpClient getHttpClient(){
        if(httpClient==null){
            initHttpClient();
        }
        return httpClient;
    }

    private synchronized void  initHttpClient() {
        if(httpClient==null){
            httpClient =  new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        }
    }

    private String queryStatus(String taskId) throws IOException{
        GetMethod method = new GetMethod();
        method.setPath(getQueryDomain()+"/query?pipeTaskId="+taskId);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
        Header h = new Header();
        h.setName("content-type");
        h.setValue("application/json");
        method.addRequestHeader(h);
        try {
            int status = getHttpClient().executeMethod(method);
            if (status >= 200 && status < 300){
                return getResponseBody(method.getResponseBodyAsStream());
            }else{
                return "";
            }
        } catch (HttpException e) {
            new IOException(e);
        } catch (IOException e) {
            throw e;
        }finally{
            method.releaseConnection();
        }
        return "";
    }

    /**
     * getResponseBodyAsString的替代方法
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String getResponseBody(InputStream inputStream) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer = new StringBuffer();
        String str= "";
        while((str = br.readLine()) != null){
            stringBuffer.append(str );
        }
        return stringBuffer.toString();
    }

    private String getPath(String path){
        int pos = path.lastIndexOf('/');
        if(pos == -1){
            return path;
        }
        return path.substring(pos+1);
    }
}

class PathNamePair{
    private String path;
    private String name;
    public PathNamePair(String path, String name){
        this.path = path;
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
