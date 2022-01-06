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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.common.DucklingProperties;
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
import org.json.JSONArray;
import org.json.JSONObject;
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

        JSONObject obj = new JSONObject();
        obj.put("taskType", "m2d");
        obj.put("sourceTeam", "meepo");
        obj.put("targetTeam", teamCode);
        obj.put("uploadTargetFolder", targetRid+"");
        obj.put("umtToken", accessToken);
        obj.put("username", uid);
        JSONArray arr = new JSONArray();
        obj.put("metas", arr);
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
                            arr.put(getMeta(item.restorePath,item.name,item.size, String.valueOf(folder.getRid()), true, isSelected));
                        }else{
                            PathNamePair pair = parsePathName(item.restorePath);
                            Resource folder = folderMap.get(pair.getPath());
                            arr.put(getMeta(item.restorePath,item.name,item.size, String.valueOf(folder.getRid()), false, false));
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    return null;
                }
            }else{
                arr.put(getMeta(decode(bean.getRid()),bean.getTitle(),bean.getSize(), "", false, true));
            }
        }

        //验证团队空间是容量是否足够
        List<Resource> resList = new ArrayList<Resource>();
        for(int i=0; i < arr.length(); i++){
            JSONObject item = arr.getJSONObject(i);
            Resource r = new Resource();
            r.setSize(Long.parseLong(item.getString("fileSize")));
            resList.add(r);
        }
        if(!teamSpaceSizeService.validateTeamSize(tid, resList)){
            throw new MessageException("团队空间已满不能进行复制");
        }

        JSONObject result = writeJson(obj);
        return result.getString("pid");
    }

    @Override
    public String team2Meepo(List<Resource> beans,String sourceTeamCode, String targetRid, String uid, String accessToken) throws IOException{
        JSONObject obj = new JSONObject();
        obj.put("taskType", "d2m");
        obj.put("sourceTeam", sourceTeamCode);
        obj.put("targetTeam", "meepo");
        obj.put("uploadTargetFolder", targetRid+"");
        obj.put("umtToken", accessToken);
        obj.put("username", uid);
        JSONArray arr = new JSONArray();
        obj.put("metas", arr);
        for(Resource bean :beans){
            arr.put(getMeta(bean.getRid()+"",bean.getTitle(),bean.getSize(),null,null,true));
        }
        JSONObject result = writeJson(obj);
        return result.getString("pid");
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


    private JSONObject getMeta(String path,String title, long size, String uploadTargetFolder, Boolean isFolder, Boolean isSelected) {
        JSONObject obj = new JSONObject();
        obj.put("filePath", path);
        obj.put("fileName", title);
        obj.put("fileSize", size+"");
        if(uploadTargetFolder!=null){
            obj.put("uploadTargetFolder", uploadTargetFolder);
        }
        if(isFolder!=null){
            obj.put("isFolder", isFolder);
        }
        if(isSelected!=null){
            obj.put("isSelected", isSelected);
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
            JSONObject obj = new JSONObject(result);
            JSONObject status = obj.getJSONObject("subTasksStats");
            r = new PipeTaskStatus();
            r.setStatus(obj.getString("pipeStatus"));
            r.setTaskId(taskId);
            r.setFailed(status.getInt("failed"));
            r.setProcessing(status.getInt("processing")+status.getInt("waiting"));
            r.setSuccess(status.getInt("success"));
            r.setTotal(status.getInt("total"));

            JSONObject subTaskStatMap = obj.getJSONObject("subTaskStatMap");

            JSONArray nameArr = subTaskStatMap.names();
            Map<String, JSONObject> map = new HashMap<String, JSONObject>();
            if(nameArr!=null){
                for(int i=0; i<nameArr.length(); i++){
                    JSONObject item = subTaskStatMap.getJSONObject(nameArr.getString(i));
                    map.put(nameArr.getString(i), item);
                }
            }

            SubTask st = null;
            JSONArray arr = obj.getJSONArray("subTasks");
            List<SubTask> subTaskList = new ArrayList<SubTask>();
            for(int i=0; i<arr.length(); i++){
                JSONObject item = new JSONObject(arr.getString(i));
                st = new SubTask();
                String fileName = "";
                if("yes".equals(item.getString("selectFlag"))){
                    st.setId(item.getString("id"));
                    st.setPath(item.getString("path"));
                    if("folder".equals(item.getString("type"))){
                        fileName = getPath(item.getString("filename"));
                        st.setFileType("Folder");
                        st.setItemType("");
                    }else{
                        fileName = item.getString("filename");
                        st.setItemType("DFile");
                        st.setFileType(FileTypeHelper.getFileExt(fileName));
                    }
                    st.setFilename(fileName);
                    st.setSize(FileSizeUtils.getFileSize(Long.valueOf(item.getString("size"))));
                    JSONObject selected = map.get(st.getPath());

                    st.setSubTotal(selected.getInt("total"));
                    st.setSubSuccess(selected.getInt("success"));
                    st.setSubFailed(selected.getInt("failed"));
                    if(st.getSubTotal()==st.getSubSuccess()){
                        st.setStatus("success");
                    }else if(st.getSubTotal()==(st.getSubSuccess()+st.getSubFailed())){
                        st.setStatus("failed");
                    }
                    subTaskList.add(st);
                }
            }
            r.setSubTaskList(subTaskList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return r;
    }

    private JSONObject writeJson(JSONObject json) throws IOException{
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
                return new JSONObject(result);
            }else{
                return null;
            }
        } catch (HttpException e) {
            new IOException(e);
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
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
