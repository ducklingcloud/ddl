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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.constant.SupportedFileForOfficeViewer;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.web.controller.pan.PanResourceBean;

import org.springframework.util.StringUtils;

import cn.vlabs.clb.api.io.impl.MimeType;

public class LynxResourceUtils {

    @SuppressWarnings("unchecked")
    public static JsonArray getResourceJSON(List<Resource> rs,String uid){
        JsonArray array = new JsonArray();
        for(Resource r:rs){
            JsonObject o = getResourceJson(uid, r);
            array.add(o);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static JsonObject getResourceJson(String uid,Resource r) {
        JsonObject o = new JsonObject();
        o.addProperty("rid", r.getRid());
        o.addProperty("fileName", r.getTitle());
        o.addProperty("itemType", r.getItemType());
        o.addProperty("parentRid", r.getBid());
        o.addProperty("createTime", formatTime(r.getCreateTime()));
        o.addProperty("modofyTime", formatTime(r.getLastEditTime()));
        String userName = DDLFacade.getBean(AoneUserService.class).getUserNameByID(r.getLastEditor());
        o.addProperty("lastEditor",userName);
        o.addProperty("lastEditorUid", r.getLastEditor());
        o.addProperty("isOffice", "false");
        if(r.isFile()||r.isPage()){
            o.addProperty("fileType", r.getFileType());
            URLGenerator urlGenerator = (URLGenerator)DDLFacade.getBean(URLGenerator.class);
            if(SupportedFileForOfficeViewer.isSupportedFile(r.getTitle())){
                o.addProperty("isOffice", "true");
                String url = urlGenerator.getURL(r.getTid(), UrlPatterns.T_PREVIEW_OFFICE, r.getRid()+"", "redirect=redirect&from=web");
                o.addProperty("previewUrl", url);
            }else if(r.isPage()){
                String url = urlGenerator.getURL(r.getTid(), UrlPatterns.T_VIEW_R, r.getRid()+"",null);
                o.addProperty("previewUrl", url);
            }else{
                String url = urlGenerator.getURL(r.getTid(), UrlPatterns.T_PREVIEW_OFFICE, r.getRid()+"", "redirect=redirect&from=web");
                o.addProperty("previewUrl", url);
            }
        }else{
            o.addProperty("fileType", "");
        }

        //TODO
        o.addProperty("parentRid", r.getBid());
        o.add("tags",getTags(r.getTagMap()));
        o.addProperty("star", isUserStar(uid, r.getMarkedUserSet()));
        o.addProperty("lastVersion",r.getLastVersion());
        o.addProperty("status", r.getStatus());
        String contentType = StringUtils.isEmpty(r.getFileType()) ? "" : MimeType.getContentType(r.getFileType());
        o.addProperty("contentType", contentType);
        o.addProperty("size", FileSizeUtils.getFileSize(r.getSize()));
        o.addProperty("shared", r.isShared());
        return o;
    }

    private static String isUserStar(String uid,Set<String> stars){
        if(stars==null){
            return "unchecked";
        }else if(stars.contains(uid)){
            return "checked";
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private static JsonArray getTags(Map<Integer,String> tags){
        JsonArray a = new JsonArray();
        if(tags==null){
            return a;
        }
        for(Entry<Integer,String> e : tags.entrySet()){
            JsonObject o = new JsonObject();
            o.addProperty("tagId", e.getKey());
            o.addProperty("tagValue", e.getValue());
            a.add(o);
        }
        return a;
    }

    private static String formatTime(Date date){
        if(date==null){
            return "";
        }
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return d.format(date);
    }

    @SuppressWarnings("unchecked")
    public static JsonArray getPanResourceJSON(List<PanResourceBean> resources, String uid) {
        JsonArray arr = new JsonArray();
        if(resources!=null)
            for(PanResourceBean b : resources){
                arr.add(getPanResourceJson(b, uid));
            }
        return arr;
    }

    @SuppressWarnings("unchecked")
    public static JsonObject getPanResourceJson(PanResourceBean r, String uid) {
        JsonObject o = new JsonObject();
        o.addProperty("rid", r.getRid());
        o.addProperty("fileName", r.getTitle());
        o.addProperty("itemType", r.getItemType());
        o.addProperty("parentRid", r.getParentPath());
        o.addProperty("createTime", formatTime(r.getCreateTime()));
        o.addProperty("modofyTime", formatTime(r.getModifyTime()));
        if(!StringUtils.isEmpty(r.getLastEditor())){
            String userName = DDLFacade.getBean(AoneUserService.class).getUserNameByID(r.getLastEditor());
            o.addProperty("lastEditor",userName);
            o.addProperty("lastEditorUid", r.getLastEditor());
        }
        if(r.isFile()){
            o.addProperty("fileType", r.getFileType());
        }else{
            o.addProperty("fileType", "");
        }
        o.addProperty("parentRid", r.getParentPath());
        o.addProperty("lastVersion",r.getVersion());
        String contentType = StringUtils.isEmpty(r.getFileType()) ? "" : MimeType.getContentType(r.getFileType());
        o.addProperty("contentType", contentType);
        o.addProperty("size", FileSizeUtils.getFileSize(r.getSize()));
        if(r.isSearchResult()){
            dealSearchResult(o,r);
        }else{
            o.addProperty("searchResult", false);
        }
        o.addProperty("shared", r.getShared());
        return o;
    }

    @SuppressWarnings("unchecked")
    public static JsonArray getPanResourceJsonList(List<PanResourceBean> resources, String uid) {
        JsonArray arr = new JsonArray();
        if(resources!=null)
            for(PanResourceBean b : resources){
                arr.add(getPanResourceJson(b, uid));
            }
        return arr;
    }

    @SuppressWarnings("unchecked")
    private static void dealSearchResult(JsonObject o, PanResourceBean r) {
        o.addProperty("searchResult", true);
        try{
            String p = URLDecoder.decode(r.getParentPath(), "utf-8");
            if(StringUtils.isEmpty(p)||"/".equals(p)){
                o.addProperty("parentPathName", "所有文件");
            }else{
                o.addProperty("parentPathName", p);
            }
        }catch(Exception e){}

        String path = r.getPath();
        int index = path.lastIndexOf("/");
        if(index>0){
            String tmp = path.substring(0, index);
            int be = tmp.lastIndexOf("/");
            if(be!=-1){
                String re = tmp.substring(be+1);
                if("/".equals(re)){
                    o.addProperty("parentName", "所有文件");
                }else{
                    o.addProperty("parentName", re);
                }
                return ;
            }

        }
        o.addProperty("parentName", "所有文件");
    }

    @SuppressWarnings("unchecked")
    public static JsonArray getPanResourceList(List<PanResourceBean> resources, String uid) {
        JsonArray arr = new JsonArray();
        if(resources!=null)
            for(PanResourceBean b : resources){
                arr.add(getPanResource(uid, b));
            }
        return arr;
    }

    @SuppressWarnings("unchecked")
    public static JsonArray getPanResourceListLite(List<PanResourceBean> resources, String uid) {
        JsonArray arr = new JsonArray();
        if(resources!=null)
            for(PanResourceBean b : resources){
                arr.add(getPanResourceLite(uid, b));
            }
        return arr;
    }

    @SuppressWarnings("unchecked")
    public static JsonObject getPanResource(String uid,PanResourceBean r) {
        JsonObject o = new JsonObject();
        o.addProperty("rid", r.getRid());
        o.addProperty("title", r.getTitle());
        o.addProperty("itemType", r.getItemType());
        o.addProperty("bid", r.getParentPath());
        o.addProperty("createTime", formatTime(r.getCreateTime()));
        o.addProperty("lastEditTime", formatTime(r.getModifyTime()));
        if(!StringUtils.isEmpty(r.getLastEditor())){
            String userName = DDLFacade.getBean(AoneUserService.class).getUserNameByID(r.getLastEditor());
            o.addProperty("lastEditorName",userName);
            o.addProperty("creatorName",userName);
            o.addProperty("lastEditor", r.getLastEditor());
            o.addProperty("creator", r.getLastEditor());
        }
        if(r.isFile()){
            o.addProperty("fileType", r.getFileType());
        }else{
            o.addProperty("fileType", "");
        }
        o.addProperty("file", r.isFile());
        o.addProperty("bundle", r.isFolder());
        o.addProperty("folder", r.isFolder());
        o.addProperty("parentRid", r.getParentPath());
        o.addProperty("lastVersion",r.getVersion());
        String contentType = StringUtils.isEmpty(r.getFileType()) ? "" : MimeType.getContentType(r.getFileType());
        o.addProperty("contentType", contentType);
        o.addProperty("size", r.getSize());
        o.addProperty("sizeStr", FileSizeUtils.getFileSize(r.getSize()));
        return o;
    }

    @SuppressWarnings("unchecked")
    public static JsonObject getPanResourceLite(String uid,PanResourceBean r) {
        JsonObject o = new JsonObject();
        o.addProperty("rid", r.getRid());
        return o;
    }
}
