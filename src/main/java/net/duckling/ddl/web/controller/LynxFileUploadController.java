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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.FileUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.bean.AttachmentItem;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


/**
 * @date 2011-5-30
 * @author Clive Lee
 */

@Controller
@RequestMapping("/{teamCode}/upload")
@RequirePermission(target="team",operation="edit")
public class LynxFileUploadController extends BaseController {
    private static final Logger LOG = Logger.getLogger(LynxFileUploadController.class);

    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private FolderPathService folderPathService;
    //浏览器上传单个文件最大限制
    //@Value("${ddl.uploadByBrowser.sizeLimit}")
    //private long sizeLimit;

    @RequestMapping(params="func=prepareUploadPage")
    public ModelAndView prepareUploadPage(HttpServletRequest request) {
        VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        Integer cid = Integer.parseInt(request.getParameter("cid"));
        ModelAndView mv = layout(ELayout.LYNX_MAIN, vwbcontext, "/jsp/aone/collection/uploadFiles.jsp");
        mv.addObject("cid", cid);
        return mv;
    }

    @RequestMapping(method=RequestMethod.POST, params="func=updateFile")
    public void updatePageFile(@RequestParam("qqfile") MultipartFile uplFile, HttpServletRequest request,HttpServletResponse response) throws IOException {
        VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        int rid = Integer.parseInt(request.getParameter("rid"));
        updateDocument(uplFile.getOriginalFilename(), uplFile.getInputStream(), uplFile.getSize(),rid,
                       getParentRid(request), vwbcontext,response);
    }

    @RequestMapping(params="func=updateFile",headers= {"X-File-Name"})
    public void updatePageFile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        int rid = Integer.parseInt(request.getParameter("rid"));
        updateDocument(getFileNameFromHeader(request), request.getInputStream(), request.getContentLength(),rid,
                       getParentRid(request), vwbcontext,response);
    }

    @RequestMapping(params="func=uploadFiles", headers={"X-File-Name"}) //Support for Firefox
    public void uploadFiles(HttpServletRequest request,HttpServletResponse response) throws IOException  {
        VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        createDocument(getFileNameFromHeader(request), request.getInputStream(), request.getContentLength(),getParentRid(request), vwbcontext, response);
    }

    @RequestMapping(method=RequestMethod.POST, params="func=uploadFiles") //Support for IE
    public void uploadFiles(@RequestParam("qqfile") MultipartFile uplFile, HttpServletRequest request,HttpServletResponse response) throws IOException{
        VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        createDocument(uplFile.getOriginalFilename(), uplFile.getInputStream(), uplFile.getSize(),getParentRid(request), vwbcontext, response);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params="func=searchReferableFiles")
    public void searchReferableFiles(HttpServletRequest request,HttpServletResponse response) {
        String term = request.getParameter("term");
        String[] filters = request.getParameterValues("existReferFiles[]");
        int tid = VWBContext.getCurrentTid();
        term  = term.replace("'", "\\'");
        //FIXME:由于Sphinx查询的时候的分词的问题，所以在这里使用数据库的模糊查询，可能存在性能上的问题，请注意
        List<Resource> resList = resourceService.queryReferableFiles(term, tid);
        Collection<Resource> results = filterExistIds(resList,filters);
        JsonArray array = new JsonArray();
        if(results!=null&&results.size()!=0){
            for(Resource res : results){
                JsonObject json = new JsonObject();
                json.addProperty("title", res.getTitle());
                json.addProperty("rid", res.getRid());
                array.add(json);
            }
        }else{
            JsonObject json = new JsonObject();
            json.addProperty("title", "没有找到含\""+term+"\"的文件");
            json.addProperty("fid", 0);
            array.add(json);
        }
        JsonUtil.write(response, array);
    }

    private Collection<Resource> filterExistIds(List<Resource> resList ,String[] filters){//去重复
        if(filters==null||filters.length==0){
            return resList;
        }
        Map<String,Resource> map = new HashMap<String,Resource>();
        for(Resource res : resList){
            map.put(res.getRid()+"", res);
        }
        for(String id:filters){
            if(map.containsKey(id)){
                map.remove(id);
            }
        }
        return map.values();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params="func=referFiles")
    public void referFiles(HttpServletRequest request,HttpServletResponse response,
                           @RequestParam("fids[]")int[] fids,@RequestParam("rid")Integer rid,
                           @RequestParam("title")String title,@RequestParam("bid")Integer bid){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        int tid = context.getSite().getId();
        Resource page = resourceService.getResource(rid);
        page.setTitle(title);
        JsonObject json = new JsonObject();

        fileVersionService.referTo(rid,tid,fids);
        json.addProperty("bid", bid);
        JsonUtil.write(response, json);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(params="func=referExistFiles")
    public void referExistFiles(HttpServletRequest request,HttpServletResponse response,
                                @RequestParam("fids[]")int[] fids,@RequestParam("pid")Integer pid,
                                @RequestParam("title")String title){
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        int tid = context.getSite().getId();
        fileVersionService.referTo(pid,tid,fids);
        JsonObject json = new JsonObject();
        List<FileVersion> verList = fileVersionService.getLatestFileVersions(fids, tid);
        JsonArray array = new JsonArray();
        if(null!=verList && verList.size()>0){
            for(FileVersion ver : verList){
                array.add(wrapJSON(tid, ver));
            }
        }
        json.add("referFiles", array);
        JsonUtil.write(response, json);
    }

    private void updateDocument(String fileName,InputStream in, long fileSize,int rid, int parentRid, VWBContext vwbcontext, HttpServletResponse response) throws IOException {
        if(StringUtil.illTitle(response, fileName)){
            return;
        }
        String uid = vwbcontext.getCurrentUID();
        int tid = VWBContext.getCurrentTid();
        Resource r = resourceService.getResource(rid);
        try {
            String newFilename = folderPathService.getResourceName(tid, r.getBid(), LynxConstants.TYPE_FILE, fileName,new int[]{r.getRid()});
            FileVersion fv = resourceOperateService.updateCLBFile(rid, tid, uid, newFilename, fileSize, in, null);
            Resource resource = resourceService.getResource(fv.getRid());
            writeUploadJSONResult(vwbcontext.getTid(),uid,response, fv,resource);
        } catch (NoEnoughSpaceException e) {
            writeNoEnoughSpaceError(uid, e.getTid(), fileName, "更新", response);
        } finally{
            in.close();
        }
    }

    private void createDocument(String fileName,InputStream in, long fileSize, int parentRid, VWBContext vwbcontext, HttpServletResponse response) throws IOException {
        if(StringUtil.illTitle(response, fileName)){
            return;
        }
        String uid = vwbcontext.getCurrentUID();
        int tid = vwbcontext.getSite().getId();
        try {
            FileVersion fv = resourceOperateService.upload(uid,tid, parentRid,fileName, fileSize, in);
            Resource resource=resourceService.getResource(fv.getRid());
            writeUploadJSONResult(vwbcontext.getTid(),uid,response, fv,resource);
        } catch (NoEnoughSpaceException e) {
            writeNoEnoughSpaceError(uid, e.getTid(), fileName, "上传", response);
        } finally{
            in.close();
        }
    }

    private int getParentRid(HttpServletRequest request){
        int i = 0;
        try{
            i = Integer.parseInt(request.getParameter("parentRid"));
        }catch(Exception  e){}
        return i;
    }

    @SuppressWarnings("unchecked")
    private void writeNoEnoughSpaceError(String uid,int tid, String fileName, String operate, HttpServletResponse response){
        LOG.warn("user "+uid+" update file "+fileName+" to team(tid="+tid+") no enough space");
        JsonObject j = new JsonObject();
        j.addProperty("result", false);
        j.addProperty("error", operate + "文件失败，您的空间已满。如需扩容，请联系管理员vlab@cnic.cn");
        response.setStatus(HttpServletResponse.SC_OK);
        JsonUtil.write(response, j);
    }

    //  private boolean checkSizeLimit(long fileSize){
    //  if(fileSize>sizeLimit || fileSize<=0 ){
    //      return false;
    //  }
    //  return true;
    //  }

    //  @SuppressWarnings("unchecked")
    //  private void writeTooLargeFileError(HttpServletResponse response){
    //      JsonObject j = new JsonObject();
    //      j.put("result", false);
    //      j.put("isItemError", true);
    //      j.put("error", "超过"+SizeUtil.getFormatSize(sizeLimit)+"限制");
    //      response.setStatus(HttpServletResponse.SC_OK);
    //      JsonUtil.write(response, j);
    //  }

    @SuppressWarnings("deprecation")
    private void writeNoPermissionError(HttpServletResponse response){
        JsonObject j = new JsonObject();
        j.addProperty("result", false);
        j.addProperty("error", "您没有上传权限.");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        JsonUtil.write(response, j);
    }


    @SuppressWarnings("deprecation")
    private void writeUploadJSONResult(int tid, String uid,HttpServletResponse response, FileVersion item,Resource resource) {
        JsonObject result = wrapJSON(tid, item);
        response.setStatus(HttpServletResponse.SC_OK);
        JsonObject jsonResource=LynxResourceUtils.getResourceJson(uid,resource);
        result.add("resource", jsonResource);
        JsonUtil.write(response, result);
    }

    @SuppressWarnings("deprecation")
    private JsonObject wrapJSON(int tid, FileVersion item) {
        AttachmentItem attachItem = AttachmentItem.convertFromAttachment(item);
        JsonObject result = new Gson().toJsonTree(attachItem).getAsJsonObject();
        result.addProperty("success", true);
        result.addProperty("fileExtend", FileUtil.getFileExt(attachItem.getTitle()));
        result.addProperty("infoURL", urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, Integer.toString(item.getRid()),null));
        result.addProperty("previewURL", urlGenerator.getURL(tid,"download", Integer.toString(item.getRid()), null));
        return result;
    }

    private String getFileNameFromHeader(HttpServletRequest request) {
        String filename = request.getHeader("X-File-Name");
        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Your system doesn't support utf-8 character encode. so sucks.");
        }
        return filename;
    }

    protected void writeResponse(HttpServletResponse response, String message) {
        try {
            response.setContentType("text/xml");
            response.setCharacterEncoding("utf-8");
            Writer writer = response.getWriter();
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @OnDeny("uploadFiles")
    public void onDeny(String method, HttpServletRequest request, HttpServletResponse response){
        LOG.warn("[method:"+ method +"] permission denied.");
        writeNoPermissionError(response);
    }
}
