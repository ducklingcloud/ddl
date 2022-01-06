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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.copy.ICopyService;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageHelper;
import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.PageVersionService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceBuilder;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamPreferenceService;
import net.duckling.ddl.service.team.TeamPreferences;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.controller.CopyController.CopyTeam;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api/pageCopy")
@RequirePermission(target="team", operation="view")
public class APIPageCopyController extends APIBaseController {
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ICopyService copyService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamPreferenceService teamPreferenceService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private FolderPathService folderPathService;
    @Autowired
    private EventDispatcher eventDispatcher;
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private PageVersionService pageVersionService;

    private static final Logger log = Logger.getLogger(APIPageCopyController.class);

    @SuppressWarnings("unchecked")
    @RequestMapping("/teams")
    public void teamCanEdit(@RequestParam("rid")int rid, HttpServletRequest request, HttpServletResponse response){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_COPY_PATTERNS);
        List<TeamPreferences> prefList = teamPreferenceService.getAllTeamPrefs(context.getCurrentUID());
        List<CopyTeam> teamList = new ArrayList<CopyTeam>();
        for (TeamPreferences p : prefList) {
            String auth=authorityService.getTeamAuthority(p.getTid(), context.getCurrentUID());
            if(Team.AUTH_ADMIN.equals(auth)||Team.AUTH_EDIT.equals(auth)){
                boolean needCover=VWBContext.getCurrentTid()==p.getTid()?false:copyService.isNeedCover(rid, p.getTid());
                teamList.add(new CopyTeam(teamService.getTeamByID(p.getTid()),needCover));
            }
        }
        JSONObject jsonObj = new JSONObject();
        String api = request.getParameter("api");
        jsonObj.put("api", api);
        JSONArray jsonArray = JsonUtil.getJSONArrayFromList(teamList);
        jsonObj.put("records", jsonArray);
        JsonUtil.writeJSONObject(response, jsonObj);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/copy")
    public void copy(@RequestParam("cover")String coverStrs,@RequestParam("toTids")String toTidsStrs,
                     @RequestParam("version") int version, HttpServletRequest request, HttpServletResponse response){
        boolean succ = true;
        if(coverStrs == null || coverStrs.length() < 1 || toTidsStrs == null || toTidsStrs.length() < 1) {
            succ = false;
        } else {
            String[] coverStr = coverStrs.split(",");
            String[] toTidsStr = toTidsStrs.split(",");
            VWBContext context = VWBContext.createContext(request, UrlPatterns.T_COPY_PATTERNS);
            String uid = context.getCurrentUID();
            int tid = VWBContext.getCurrentTid();

            // 转化数组
            boolean[] cover = new boolean[coverStr.length];
            int[] toTids = new int[toTidsStr.length];
            for(int i=0; i<coverStr.length; i++) {
                String coverTemp = coverStr[i];
                coverTemp = APICommonUtil.jsonStringReplace(coverTemp);
                cover[i] = Boolean.parseBoolean(coverTemp);
            }
            for(int j=0; j<toTidsStr.length; j++) {
                String tidTemp = toTidsStr[j];
                tidTemp = APICommonUtil.jsonStringReplace(tidTemp);
                toTids[j] = Integer.parseInt(tidTemp);
            }
            //          try{
            int rid = getRid(request,tid);
            if(version==-1){
                version = getLastVersion(rid);
            }
            //              copyService.doCopy(rid, version, tid, toTids, cover, uid);
            teamCopy(rid, version, tid, toTids, uid);
            //          }catch(CopyException e){
            //              log.error("Do Copy Error:", e);
            //              succ = false;
            //          }
        }
        JSONObject jsonObj = new JSONObject();
        String api = request.getParameter("api");
        jsonObj.put("api", api);
        jsonObj.put("succ", succ);
        JsonUtil.writeJSONObject(response, jsonObj);
    }
    //-------------------------------临时添加团队间复制开始----------------------------------
    private void teamCopy(int rid,int version,int tid,int[] toTids,String uid){
        for(int toTid : toTids){
            teamCopy(rid, version, tid, toTid, uid);
        }
    }
    private Resource teamCopy(int srcRid,int version,int tid,int toTid,String uid){
        List<Resource> descendants = folderPathService.getDescendants(tid, srcRid);
        ResourceFolder f = parseFolderTree(descendants,srcRid);
        int destRid = 0;
        Resource dest = coypResourceFodler(toTid,destRid,f,uid);
        eventDispatcher.sendResourceCopyEvent(toTid, dest, destRid);
        return dest;
    }
    private Resource coypResourceFodler(int toTid, int parentRid, ResourceFolder f, String uid) {
        Resource desc = null;
        if(f!=null){
            Resource src = f.getResource();
            if(src.isFolder()){
                desc = copyFolder(parentRid,toTid, src, uid);
            }else if(src.isFile()){
                desc = copyFile(parentRid,toTid, src, uid);
            }else if(src.isPage()){
                desc = copyPage(parentRid,toTid,src,uid);
            }
            if(desc != null){
                for(ResourceFolder rf : f.getChildren()){
                    coypResourceFodler(toTid, desc.getRid(), rf, uid);
                }
                if(!src.getTitle().equals(desc.getTitle())){
                    renameResourceVersion(desc);
                }
            }
        }
        return desc;
    }
    private Resource copyPage(int parentRid,int toTid, Resource src, String uid) {
        Resource desc = ResourceBuilder.getNewPage(toTid, parentRid, uid, src.getTitle(),src.getSize());
        desc.setSize(src.getSize());
        PageVersion srcPage = pageVersionService.getLatestPageVersion(src.getRid());
        if(srcPage==null){
            return null;
        }
        PageVersion p = PageHelper.createPageVersion(desc, srcPage.getContent());
        createPage(desc, p);
        return desc;
    }
    public void createPage(Resource r ,PageVersion pageVersion){
        r.setLastVersion(pageVersion.getVersion());
        createResource(r);
        pageVersion.setRid(r.getRid());
        pageVersionService.create(pageVersion);
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
    }
    private void renameResourceVersion(Resource r){
        if(r.isFile()){
            FileVersion fv = fileVersionService.getLatestFileVersion(r.getRid(), r.getTid());
            fv.setTitle(r.getTitle());
            fileVersionService.update(fv.getId(), fv);
        }else if(r.isPage()){
            PageVersion pv = pageVersionService.getLatestPageVersion(r.getRid());
            pv.setTitle(r.getTitle());
            pageVersionService.update(pv.getId(), pv);
        }
    }
    private Resource copyFile(int parentRid,int toTid, Resource src, String uid) {
        Resource desc = ResourceBuilder.getNewFile(toTid, parentRid, uid, src.getTitle(), src.getFileType(),src.getSize());
        FileVersion v = fileVersionService.getLatestFileVersion(src.getRid(), src.getTid());
        if(v == null){
            return null;
        }
        FileVersion f  = createFileVersion(desc, v.getClbId(), v.getClbVersion(),v.getSize());
        createFile(desc, f);
        return desc;
    }
    public void createFile(Resource r,FileVersion fileVersion){
        r.setLastVersion(fileVersion.getVersion());
        createResource(r);
        fileVersion.setRid(r.getRid());
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
        fileVersionService.create(fileVersion);
    }
    private FileVersion createFileVersion(Resource file,int clbId,int clbVersion,long size){
        int version = file.getLastVersion()+1;
        boolean isInitVer = file.getLastVersion() == LynxConstants.INITIAL_VERSION;
        Date date = isInitVer?file.getCreateTime():(new Date());
        FileVersion fileVersion = new FileVersion();
        fileVersion.setClbVersion(clbVersion);
        fileVersion.setRid(file.getRid());
        fileVersion.setTid(file.getTid());
        fileVersion.setVersion(version);
        fileVersion.setClbId(clbId);
        fileVersion.setSize(size);
        fileVersion.setTitle(file.getTitle());
        fileVersion.setEditor(file.getLastEditor());
        fileVersion.setEditTime(date);
        return fileVersion;
    }

    private Resource copyFolder(int parentRid,int toTid,Resource r,String uid){
        Resource dest = ResourceBuilder.getNewFolder(toTid, parentRid, uid, r.getTitle(),r.getSize());
        createFolder(dest);
        return dest;
    }
    public void createFolder(Resource r){
        createResource(r);
        folderPathService.create(r.getBid(), r.getRid(), r.getTid());
    }
    /**
     * 创建resource，并保证名称唯一
     * @param r
     */
    private void createResource(Resource r){
        String title = folderPathService.getResourceName(r.getTid(), r.getBid(), r.getItemType(), r.getTitle());
        r.setTitle(title);
        resourceService.create(r);
    }
    private ResourceFolder parseFolderTree(List<Resource> rs,int rootRid){
        ResourceFolder result = null;
        if(rs!=null){
            Map<Integer,ResourceFolder> map = new HashMap<Integer,ResourceFolder>();
            Collections.reverse(rs);
            for(Resource r : rs){
                ResourceFolder rf = new ResourceFolder(r);
                map.put(r.getRid(), rf);
                ResourceFolder parent = map.get(r.getBid());
                if(parent!=null){
                    parent.addChildren(rf);
                }
                if(rootRid==r.getRid()){
                    result = rf;
                }
            }
        }
        return result;
    }
    static class ResourceFolder{
        private List<ResourceFolder> children;
        private Resource resource;
        private int rid;
        private String type;
        ResourceFolder(Resource r){
            resource = r;
            rid = r.getRid();
            type = r.getItemType();
            children = new ArrayList<ResourceFolder>();
        }
        public void addChildren(ResourceFolder child) {
            children.add(child);
        }
        public List<ResourceFolder> getChildren() {
            return children;
        }
        public Resource getResource() {
            return resource;
        }
        public int getRid() {
            return rid;
        }
        public String getType() {
            return type;
        }
    }

    //-------------------------------临时添加团队间复制结束----------------------------------





    private int getLastVersion(int rid) {
        return resourceService.getResource(rid).getLastVersion();
    }

    private int getRid(HttpServletRequest request,int tid){
        String rid = request.getParameter("rid");
        if(StringUtils.isNotEmpty(rid)){
            try{
                return Integer.parseInt(rid);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        String itemId = request.getParameter("itemId");
        String itemType = request.getParameter("itemType");
        if(StringUtils.isEmpty(itemId)||StringUtils.isEmpty(itemType)){
            throw new RuntimeException("缺乏需求参数");
        }
        int id = Integer.parseInt(itemId);
        Resource r = resourceService.getResource(id, tid);
        if(r!=null){
            return r.getRid();
        }else{
            throw new RuntimeException("需求参数错误itemId="+itemId+";itemType="+itemType);
        }
    }

}
