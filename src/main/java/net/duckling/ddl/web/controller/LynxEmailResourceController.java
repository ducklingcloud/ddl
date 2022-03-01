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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContainerImpl;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.authority.TeamAcl;
import net.duckling.ddl.service.devent.EventDispatcher;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.mail.EmailAttachment;
import net.duckling.ddl.service.mail.EmailAttachmentService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TeamSpaceSizeService;
import net.duckling.ddl.service.share.ShareFileAccess;
import net.duckling.ddl.service.share.ShareFileAccessService;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamMemberService;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.ClientValidator;
import net.duckling.ddl.util.EmailUtil;
import net.duckling.ddl.util.IdentifyingCode;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.web.controller.file.BaseAttachController;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.vlabs.clb.api.SupportedFileFormatForOnLineViewer;

/**
 * 用于处理记录email产生的信息
 * @author zhonghui
 *
 */
@RequestMapping("/system/emailresource")
@Controller
public class LynxEmailResourceController extends BaseAttachController {
    private static final String ATTACH_NAME = "邮件附件";

    @Autowired
    private TeamMemberService teamMemberService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private TeamSpaceSizeService teamSpaceSizeService;
    private final static Logger LOG = Logger.getLogger(LynxEmailResourceController.class);
    /** 操作成功     */
    public final static int NORMAL = 0;
    /** 无权限进行操作 */
    public final static int NO_AUTH = 1;
    /** email格式错误 */
    public final static int EMAIL_ERROR=2;
    /** clb格式错误 */
    public final static int CLB_ID_ERROR = 3;
    /** 运行格式错误 */
    public final static int ERROR = 4;

    public final static String STATUS_CODE = "statusCode";
    public final static String ERROR_MESSAGE = "message";
    public final static String ATTACHMENT_URL = "attachmentURL";
    private final static String EMAIL_RESOURCE_TAG_NAME = "邮件附件";
    private static byte[] clientKey=null;
    private static String downloadDomain ;

    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private ResourceOperateService resourceOperateService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private AoneUserService aoneUserService;
    @Autowired
    private ShareFileAccessService shareFileAccessService;
    @Autowired
    private EmailAttachmentService emailAttachmentService;
    @Autowired
    private URLGenerator urlGenerator;

    @Autowired
    private EventDispatcher eventDispatcher;

    /**
     * 处理email客户端的分享附件
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    @RequestMapping
    public void dealEmailFile(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
        request.setCharacterEncoding("utf-8");
        if(!ClientValidator.validate(request)){
            writeSenderError(response, NO_AUTH,"","");
            return ;
        }
        String fileName  = "";
        if(StringUtils.isNotEmpty(request.getParameter("fileName"))){
            fileName = URLDecoder.decode(request.getParameter("fileName"),"UTF-8");
        }
        String userEmail = request.getParameter("email");
        String mid = request.getParameter("mid");
        if(!EmailUtil.isValidEmail(userEmail)){
            writeSenderError(response, EMAIL_ERROR,"",fileName);
            return;
        }
        try{
            Integer.parseInt(request.getParameter("clbId"));
            Long.parseLong(request.getParameter("fileSize"));
        }catch(RuntimeException e){
            writeSenderError(response, CLB_ID_ERROR,"",fileName);
            return;
        }
        int tid = getSaveAttaTid(request, userEmail);
        try{
            FileVersion fileVersion = saveFile(tid,  request);
            addFileTag(tid, fileVersion, userEmail);
            String attachmentURL = VWBContainerImpl.findContainer().getBaseURL()+urlGenerator.getURL(tid,UrlPatterns.T_VIEW_R, fileVersion.getRid()+"",null);

            emailAttachmentService.createEmailAttach(createEmailAttachment(userEmail, mid, fileVersion));
            eventDispatcher.sendFileUploadEvent(fileVersion.getTitle(), fileVersion.getRid(), userEmail, tid);
            if(SupportedFileFormatForOnLineViewer.isSupported(MimeType.getSuffix(fileVersion.getTitle()))){
                resourceOperateService.sendPdfTransformEvent(fileVersion.getClbId(),fileVersion.getClbVersion()+"");
            }

            LOG.info(request.getRemoteHost()+"的客户端往ddl的"+fileVersion.getTid()+"团队"+userEmail+"用户存储了文件["+fileVersion.getTitle()+"]文件大小为"+fileVersion.getSize());
            writeSenderError(response, NORMAL,attachmentURL,fileVersion.getTitle());
        }catch(RuntimeException e){
            LOG.error("保存email客户端的分享附件错误",e);
            writeSenderError(response, ERROR,"",fileName);
        }
    }

    private EmailAttachment createEmailAttachment(String userEmail, String mid, FileVersion fileVersion) {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setCreateTime(new Date());
        attachment.setCreator(userEmail);
        attachment.setTid(fileVersion.getTid());
        attachment.setRid(fileVersion.getRid());
        attachment.setMid(mid);
        attachment.setTitle(fileVersion.getTitle());
        return attachment;
    }

    /**
     * 附件保存的teamId
     * @param request
     * @param userEmail
     * @return
     */
    private int getSaveAttaTid(HttpServletRequest request, String userEmail) {
        int tid = 0;
        try{
            int teamId = Integer.parseInt(request.getParameter("teamId"));
            if(teamId<=0){
                tid = getUserDefaultTeam(request, userEmail);
            }else{
                if(teamMemberService.checkTeamValidity(userEmail, teamId)){
                    tid = teamId;
                }else{
                    tid = getUserDefaultTeam(request, userEmail);
                }
            }
        }catch(Exception e){
            tid = getUserDefaultTeam(request, userEmail);
        }

        return tid;
    }

    /**
     * 获取用户默认团队信息
     * @param request
     * @param userEmail
     * @return
     */
    private int getUserDefaultTeam(HttpServletRequest request, String userEmail) {
        int tid;
        String userName = request.getParameter("emailName");
        VWBContext context = getVWBContext(request);
        if(StringUtils.isEmpty(userName)){
            userName = userEmail;
        }
        tid = findUserDefaultTeam(context, userEmail, userName);
        return tid;
    }

    /**
     * 给添加的文件加一个邮件附件标签
     * @param fileVersion
     * @param user
     */
    private void addFileTag(int tid,FileVersion fileVersion,String user){
        Tag tag = tagService.getTag(fileVersion.getTid(),EMAIL_RESOURCE_TAG_NAME);
        if(tag == null){
            tag = new Tag();
            tag.setCreateTime(new Date());
            tag.setCreator(user);
            tag.setGroupId(0);
            tag.setTid(tid);
            tag.setCount(0);
            tag.setTitle(EMAIL_RESOURCE_TAG_NAME);
            int tgid = tagService.createTag(tag);
            tag.setId(tgid);
        }
        IResourceService rs = resourceService;
        Resource res = rs.getResource(fileVersion.getRid());
        Map<Integer,String> tagMap = res.getTagMap();
        if(tagMap==null || tagMap.isEmpty()){
            tagMap = new HashMap<Integer,String>();
        }
        tagMap.put(tag.getId(), tag.getTitle());
        res.setTagMap(tagMap);
        rs.updateResourceTagMap(Arrays.asList(new Resource[]{res}));
        tagService.addItem(res.getTid(), tag.getId(), fileVersion.getRid());
    }

    /**
     * 将给定的clbId号存入用户选定团队
     * @param tid 存入团队ID
     * @param context
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private FileVersion saveFile(int tid,HttpServletRequest request) throws UnsupportedEncodingException{
        String userEmail = request.getParameter("email");
        String fileName = URLDecoder.decode(request.getParameter("fileName"),"UTF-8");
        String clbId = request.getParameter("clbId");
        String size = request.getParameter("fileSize");
        //      fileName = getFileName(tid,fileName);
        int parentRid = getEmailAttachResource(tid, userEmail);
        FileVersion fileVersion = resourceOperateService.referExistFileByClbId(tid, parentRid,userEmail, Integer.parseInt(clbId), 1,fileName,Long.parseLong(size));
        return fileVersion;
    }

    /**
     * 获取团队中邮件附件保存的目录，如果没有就自动创建一个
     * @param tid
     * @param uid
     * @return
     */
    private int getEmailAttachResource(int tid,String uid){
        List<Resource> rs = resourceService.getResourceByTitle(tid, 0, LynxConstants.TYPE_FOLDER, ATTACH_NAME);
        if(rs==null||rs.isEmpty()){
            Resource r = new Resource();
            r.setTid(tid);
            r.setBid(0);
            r.setCreateTime(new Date());
            r.setCreator(uid);
            r.setItemType(LynxConstants.TYPE_FOLDER);
            r.setTitle(ATTACH_NAME);
            r.setLastEditor(uid);
            r.setLastEditorName(aoneUserService.getUserNameByID(uid));
            r.setLastEditTime(new Date());
            r.setStatus(LynxConstants.STATUS_AVAILABLE);
            resourceOperateService.createFolder(r);
            return r.getRid();
        }else{
            return rs.get(0).getRid();
        }
    }


    /**
     * 获取文件名称，如果团队中已存在这样的名称，就在名称后加上（n）；
     * @param tid
     * @param fileName
     * @return
     */
    private String getFileName(int tid, String fileName) {
        if(StringUtils.isEmpty(fileName)){
            return fileName;
        }
        String queryName = getQueryName(fileName,"%");
        List<Resource> fs = resourceService.getFileByStartName(tid, queryName);
        if(fs==null||fs.isEmpty()){
            return fileName;
        }
        int max = 0;
        String reg = getPattenName(fileName, "\\((\\d+)\\)");
        try{
            Pattern pattern = Pattern.compile(reg);
            for(Resource file : fs){
                Matcher ma = pattern.matcher(file.getTitle());
                if(ma.matches()){
                    String id = ma.group(1);
                    try{
                        int tmp = Integer.parseInt(id);
                        if(tmp>max){
                            max=tmp;
                        }
                    }catch(RuntimeException e){

                    }
                }
            }

        }catch(RuntimeException e){
            LOG.error("",e);
            return fileName;
        }
        max++;
        return getQueryName(fileName,"("+max+")" );
    }
    private static String getPattenName(String fileName,String add) {
        int index = fileName.lastIndexOf(".");
        if(index<=0){
            return transfFileName(fileName)+add;
        }else{
            return transfFileName(fileName.substring(0, index))+add+fileName.substring(index);
        }
    }
    private static String transfFileName(String s){
        StringBuilder sb = new StringBuilder();
        String ss = "(){}[]*$^+|?.\\";
        for(int i =0;i<s.length();i++){
            char a = s.charAt(i);
            if(ss.contains(a+"")){
                sb.append("\\"+a);
            }else{
                sb.append(a);
            }
        }
        return sb.toString();
    }

    private String getQueryName(String fileName,String add) {
        int index = fileName.lastIndexOf(".");
        if(index<=0){
            return fileName+add;
        }else{
            return fileName.substring(0, index)+add+fileName.substring(index);
        }
    }

    private VWBContext getVWBContext(HttpServletRequest p_request) {
        return VWBContext.createContext(p_request,UrlPatterns.MYSPACE);
    }

    private int findUserDefaultTeam(VWBContext context,String user,String userName){
        return teamService.getPersonalTeam(user, userName);
    }

    private void writeSenderError(HttpServletResponse response,int errorCode,String attachmentURL,String fileName){
        switch (errorCode) {
            case NORMAL:
                writeError(response, errorCode, "上传正确",attachmentURL,fileName);
            case NO_AUTH:
                writeError(response, errorCode, "权限错误",attachmentURL,fileName);
                break;
            case EMAIL_ERROR:
                writeError(response, errorCode, "给定email校验错误",attachmentURL,fileName);
                break;
            case CLB_ID_ERROR:
                writeError(response, errorCode, "给定的clbId格式错误",attachmentURL,fileName);
                break;
            case ERROR :
                writeError(response, errorCode, "服务器处理过程错误",attachmentURL,fileName);
                break;
            default:
                writeError(response, errorCode, "服务器处理过程错误",attachmentURL,fileName);
                break;
        }

    }

    @SuppressWarnings("deprecation")
    private void writeError(HttpServletResponse response,int errorCode,String message,String attachmentURL,String fileName){
        JsonObject o = new JsonObject();
        o.addProperty(STATUS_CODE, errorCode);
        o.addProperty(ERROR_MESSAGE, message);
        o.addProperty(ATTACHMENT_URL, attachmentURL);
        o.addProperty("fileName", fileName);
        JsonUtil.write(response, o);
    }

    /**
     * 查询附件是否已经存在
     * @param reqeust
     * @param response
     */
    @RequestMapping(params="func=findEmailAttachment")
    public void findEmailAttachment(HttpServletRequest request,HttpServletResponse response){
        if(!ClientValidator.validate(request)){
            writeQueryJSON(2, "未能获取查询权限", response,null);
            return;
        }
        String mid = request.getParameter("mid");
        String uid = request.getParameter("email");
        if(StringUtils.isEmpty(mid)||StringUtils.isEmpty(uid)){
            writeQueryJSON(3, "输入参数错误", response,null);
            return;
        }
        int tid = Integer.parseInt(request.getParameter("teamId"));
        if(tid<=0){
            tid = getSaveAttaTid(request, uid);
        }
        //      String fileName = request.getParameter("fileName");
        //      List<Resource> files = emailAttachmentService.getFileByTidAndTitle(fileName, tid);

        List<Resource> files = emailAttachmentService.getFileByEmailMidAndUid(mid, tid, uid);
        String fileSize = request.getParameter("fileSize");
        try{
            Team t = teamService.getTeamByID(tid);
            if(!t.isPersonalTeam()){
                long size = Long.parseLong(fileSize);

                if(!teamSpaceSizeService.validateTeamSize(tid, size)){
                    writeQueryJSON(5, "文档库空间已满！", response,null);
                    return ;
                }
            }
        }catch(Exception e){}

        if(files==null||files.isEmpty()){
            writeQueryJSON(1, "此mid="+mid+"还未存入文档库", response,null);
        }else{
            Resource file = files.get(0);
            writeQueryJSON(4, "该附件已存入文档库", response,urlGenerator.getAbsoluteURL(tid,UrlPatterns.T_VIEW_R, file.getRid()+"",null));
        }
    }

    @SuppressWarnings("deprecation")
    private void writeQueryJSON(int code,String message,HttpServletResponse response,String attachmentURL){
        JsonObject o = new JsonObject();
        o.addProperty(STATUS_CODE, code);
        o.addProperty(ERROR_MESSAGE, message);
        o.addProperty("attachmentURL", attachmentURL);
        JsonUtil.write(response, o);
    }

    @RequestMapping(params="func=findUserPersonTeamFiles")
    public void findUserPersonTeamFiles(HttpServletRequest request,HttpServletResponse response) throws IOException{

        JsonObject obj = new JsonObject();
        String uid = validateEmailAuth(request, obj);
        if(StringUtils.isEmpty(uid)){
            jsonCallBack(request, response, obj);
            return;
        }
        VWBContext context = getVWBContext(request);
        List<Team> teams = teamService.getUserTeamOrderByUser(uid);
        if(teams==null||teams.isEmpty()){
            findUserDefaultTeam(context, uid, uid);
            teams = teamService.getUserTeamOrderByUser(uid);
        }
        int currentPage = 0;
        String cp = request.getParameter("offer");
        if(!StringUtils.isEmpty(cp)){
            currentPage = Integer.parseInt(cp)-1;
        }
        int[] tids = null;
        int tid = 0;
        String t = request.getParameter("tid");
        boolean b = true;
        if(!StringUtils.isEmpty(t)&&!t.equals("0")){
            tids =new int[]{ Integer.parseInt(t)};
            tid = Integer.parseInt(t);
            b = false;
        }else{
            tids = getTeamIds(teams);
        }
        //context = VWBContext.createContext(tids[0], request, UrlPatterns.TEAM, null);
        int count = emailAttachmentService.getTeamFileCount( tids);
        List<Resource> files = emailAttachmentService.getFileByTid(tids,currentPage*10,10);
        List<FileVersion> versions = getFileVersionsFromFile(files,tids);
        obj.addProperty("total", count%10==0?count/10:count/10+1);
        obj.addProperty("page",currentPage+1);
        obj.addProperty("currentTeam", tid);
        obj.add("message", writeFileUrl(versions,request,context,teams,b));
        addUserTeams(teams,obj);

        jsonCallBack(request, response, obj);
    }
    private List<FileVersion> getFileVersionsFromFile(List<Resource> files, int[] tids) {
        if(files==null||files.isEmpty()){
            return Collections.emptyList();
        }
        List<RidAndTidBean> bean  = new ArrayList<RidAndTidBean>();
        for(Resource f : files){
            RidAndTidBean be = new RidAndTidBean();
            be.setRid(f.getRid());
            be.setTid(f.getTid());
            bean.add(be);
        }
        return getFileVersions(bean,  tids);
    }
    private void jsonCallBack(HttpServletRequest request, HttpServletResponse response, JsonObject o)
            throws IOException {
        String jsoncallback=request.getParameter("callback");
        response.getWriter().append(jsoncallback+"("+o.toString()+")");
    }

    private void addUserTeams(List<Team> teams, JsonObject o) {
        JsonArray array = new JsonArray();
        for(Team team : teams){
            JsonObject obj = new JsonObject();
            obj.addProperty("teamName", team.getDisplayName());
            obj.addProperty("teamId", team.getId());
            array.add(obj);
        }
        o.add("teams", array);
    }

    private List<FileVersion> getFileVersions(List<RidAndTidBean> files, int[] tids) {
        if(files==null||files.isEmpty()){
            return Collections.emptyList();
        }
        List<FileVersion> result = new ArrayList<FileVersion>();
        Map<Integer,List<Integer>> map = new HashMap<Integer,List<Integer>>();
        for(RidAndTidBean b : files){
            List<Integer> fids = map.get(b.getTid());
            if(fids==null){
                fids = new ArrayList<Integer>();
                map.put(b.getTid(), fids);
            }
            fids.add(b.getRid());
        }
        for(Entry<Integer,List<Integer>>re: map.entrySet()){
            int tid = re.getKey();
            List<Integer> fidls = re.getValue();
            if(fidls!=null&&!fidls.isEmpty()){
                int[] ff = getFid(fidls);
                result.addAll(getFileVersions(ff, tid));
            }
        }
        Map<String,FileVersion> fmap = new HashMap<String,FileVersion>();
        for(FileVersion fs : result){
            fmap.put(fs.getRid()+":"+fs.getTid(), fs);
        }
        List<FileVersion> fvs = new ArrayList<FileVersion>();
        for(RidAndTidBean f :files){
            FileVersion fv = fmap.get(f.getRid()+":"+f.getTid());
            if(fv!=null){
                fvs.add(fv);
            }
        }
        return fvs;
    }

    private int[] getFid(List<Integer> fs){
        int [] result = new int[fs.size()];
        for(int i =0;i<fs.size();i++){
            result[i] = fs.get(i);
        }
        return result;
    }

    private List<FileVersion> getFileVersions(int[] fids, int tid) {
        if(fids==null||fids.length==0){
            return Collections.emptyList();
        }
        return  fileVersionService.getLatestFileVersions(fids, tid);
    }

    @RequestMapping(params="func=searchReferableFiles")
    public void searchReferableFiles(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JsonObject obj = new JsonObject();
        String uid = validateEmailAuth(request, obj);
        if(StringUtils.isEmpty(uid)){
            jsonCallBack(request, response, obj);
            return;
        }
        VWBContext context = getVWBContext(request);
        int tid = Integer.parseInt(request.getParameter("tid"));
        int []tids = null;
        List<Team> teams = teamService.getUserTeamOrderByUser(uid);
        boolean b = true;
        if(tid==0){
            tids = getTeamIds(teams);
        }else{
            b = false;
            tids = new int[]{tid};
        }
        int currentPage = 0;
        String cp = request.getParameter("offer");
        if(!StringUtils.isEmpty(cp)){
            currentPage = Integer.parseInt(cp)-1;
            if(currentPage<0){
                currentPage = 0;
            }
        }
        String term = request.getParameter("term");
        term  = term.replace("'", "\\'");
        int count = resourceService.queryReferableFilesCount(term, tids);

        //由于Sphinx查询的时候的分词的问题，所以在这里使用数据库的模糊查询，可能存在性能上的问题，请注意
        List<Resource> resList =resourceService.queryReferableFiles(term, tids,currentPage,10);
        List<FileVersion> versions = getFileVersionFromResource(resList,tids);
        JsonObject result = new JsonObject();
        result.add("message", writeFileUrl(versions, request, context,teams,b));
        result.addProperty("total", count%10==0?count/10:count/10+1);
        result.addProperty("page",currentPage+1);
        result.addProperty("currentTeam", tid);
        result.addProperty("searchWord",request.getParameter("term"));
        addUserTeams(teams,result);
        jsonCallBack(request, response, result);
    }

    private List<FileVersion> getFileVersionFromResource(List<Resource> rs ,int []tids){
        if(rs==null||rs.isEmpty()){
            return Collections.emptyList();
        }
        List<RidAndTidBean> fidTid = new ArrayList<RidAndTidBean>();
        for(Resource r : rs){
            RidAndTidBean bean = new RidAndTidBean();
            bean.setRid(r.getRid());
            bean.setTid(r.getTid());
            fidTid.add(bean);
        }
        return getFileVersions(fidTid, tids);
    }


    public int[] getTeamIds(List<Team> teams) {
        int[] tids;
        tids = new int[teams.size()];
        for(int i=0;i<teams.size();i++){
            tids[i] = teams.get(i).getId();
        }
        return tids;
    }


    private JsonArray writeFileUrl(List<FileVersion> versions, HttpServletRequest request ,
                                   VWBContext context,List<Team> teams,boolean haveTeamName) {
        JsonArray a = new JsonArray();
        Map<Integer,Team> teamMap = new HashMap<Integer,Team>();
        for(Team team : teams){
            teamMap.put(team.getId(), team);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/M/d HH:mm");
        for(FileVersion v : versions){
            JsonObject obj = new JsonObject();
            obj.addProperty("fileName", v.getTitle());
            obj.addProperty("fileSize", getFileSize(v.getSize()));
            obj.addProperty("size", v.getSize());
            obj.addProperty("url", getDownloadUrl(v, context));
            String url = urlGenerator.getURL(v.getTid(),UrlPatterns.T_VIEW_R, v.getRid()+"",null);
            obj.addProperty("viewUrl",VWBContainerImpl.findContainer().getBaseURL()+url);
            obj.addProperty("createTime", format.format(v.getEditTime()));
            if(haveTeamName){
                obj.addProperty("teamName", teamMap.get(v.getTid()).getDisplayName());
            }else{
                obj.addProperty("teamName", "");
            }
            a.add(obj);
        }
        return a;
    }

    private String getFileSize(long size){
        if(size/1024==0){
            return size+" B";
        }else if(size/(1024*1024)==0){
            return size/1024+" KB";
        }else{
            long s = size/(1024*1024);
            long v = (size/(1024*1024))/10;
            if(v>0){
                return s+"."+v+" MB";
            }else{
                return s+" MB";
            }
        }

    }

    private String getDownloadUrl(FileVersion version,VWBContext context){
        return getDownloadDomain(context)+"/system/emailresource?func=downloadfile&rid="+version.getRid()+"&teamId="+version.getTid();
    }

    private static String getDownloadDomain(VWBContext context){
        if(StringUtils.isEmpty(downloadDomain)){
            initDownloadDomain(context);
        }
        return downloadDomain;
    }
    private static synchronized void initDownloadDomain(VWBContext context){
        if(StringUtils.isEmpty(downloadDomain)){
            String domain = context.getContainer().getProperty("ddl.coremail.download.domain");
            if(StringUtils.isEmpty(domain)){
                domain = VWBContainerImpl.findContainer().getBaseURL();
            }
            downloadDomain=domain;
        }
    }

    /**
     * 获取用户的所有team，如果用户未注册则帮其建立个人空间
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(params="func=getUserAllTeam")
    public void getUserAllTeam(HttpServletRequest request,HttpServletResponse response) throws IOException{
        JsonObject obj  = new JsonObject();
        String uid = validateEmailAuth(request, obj);
        if(StringUtils.isEmpty(uid)){
            jsonCallBack(request, response, obj);
            return ;
        }

        List<Team> teams = teamService.getUserTeamOrderByUser(uid);
        if(teams==null){
            getUserDefaultTeam(request, uid);
            teams = teamService.getAllUserTeams(uid);
        }
        List<TeamAcl> teamAcl = authorityService.getUserAllTeamAcl(uid);
        if(teamAcl!=null&&!teamAcl.isEmpty()){
            Map<String,TeamAcl> map = new HashMap<String,TeamAcl>();
            for(TeamAcl acl : teamAcl){
                map.put(acl.getTid(), acl);
            }
            List<Team> old = teams;
            teams = new ArrayList<Team>();
            for(Team t : old){
                if(t.isPersonalTeam()){
                    teams.add(t);
                }else{
                    TeamAcl acl = map.get(t.getId()+"");
                    //团体有编辑权限
                    if(acl!=null&&!"view".equals(acl.getAuth())){
                        teams.add(t);
                    }
                }
            }
        }
        addUserTeams(teams,obj);

        jsonCallBack(request, response, obj);
    }

    @RequestMapping(params="func=getFetchFileCode")
    public void getFetchFileCode(HttpServletRequest request,HttpServletResponse response) throws IOException{
        JsonObject obj  = new JsonObject();
        String uid = validateEmailAuth(request, obj);
        if(StringUtils.isEmpty(uid)){
            jsonCallBack(request, response, obj);
            return ;
        }

        String dUrl = request.getParameter("downloadUrl");
        Map<String,String> queryParam = getQueryParam(dUrl);
        int rid = Integer.parseInt(queryParam.get("rid"));
        int tid = Integer.parseInt(queryParam.get("teamId"));
        FileVersion file = fileVersionService.getLatestFileVersion(rid, tid);
        ShareFileAccess share = new ShareFileAccess();
        share.setClbId(file.getClbId());
        share.setRid(rid);
        share.setFid(rid);
        share.setTid(tid);
        share.setCreateTime(new Date());
        share.setUid(uid);
        share.setValidOfDays(30);
        share.setFetchFileCode(IdentifyingCode.getRandomCode(6));
        String encodeURL = shareFileAccessService.getPublicFileURL(share);
        String fileURLs = urlGenerator.getAbsoluteURL(UrlPatterns.DIRECT,encodeURL, null);
        obj.addProperty("fileName", file.getTitle());
        obj.addProperty("fetchFileCode", share.getFetchFileCode());
        obj.addProperty("fileURL", fileURLs);
        jsonCallBack(request, response, obj);
    }


    private Map<String, String> getQueryParam(String dUrl) {
        Map<String,String> result = new HashMap<String,String>();
        int index = dUrl.indexOf("?");
        if(index<0){
            return result;
        }
        String[] params =dUrl.substring(index+1).split("&");
        for(String s : params){
            String [] keyValue = s.split("=");
            result.put(keyValue[0], keyValue[1]);
        }

        return result;
    }

    @RequestMapping(params="func=downloadfile")
    public void downloadfile(HttpServletRequest request,HttpServletResponse response) throws IOException{
        int rid = 0;
        int tid = 0;
        try{
            rid = Integer.parseInt(request.getParameter("rid"));
            tid = Integer.parseInt(request.getParameter("teamId"));
        }catch(Exception e){
            response.setStatus(415);
            return ;
        }
        if(!ClientValidator.validate(request)){
            response.setStatus(401);
            LOG.info("rid:"+rid+";tid"+tid+";host:"+request.getRemoteAddr()+";pattern:"
                     + ClientValidator.getClientIpPattern(request) + ";IP校验错误");
            return;
        }

        int version = 0;
        try{
            version = Integer.parseInt(request.getParameter("version"));
        }catch(Exception e){};
        FileVersion fv = null;
        if(version<=0){
            fv = fileVersionService.getLatestFileVersion(rid, tid);
        }else{
            fv = fileVersionService.getFileVersion(rid, tid, version);
        }

        if (fv != null) {
            getContent(request, response, fv.getClbId(),fv.getClbVersion()+"",fv.getTitle(), false);
        }else{
            response.setStatus(404);
        }
    }

    /**
     * 获取用户的email，并进行安全认证
     * @param request
     * @param response
     * @return
     */
    private String validateEmailAuth(HttpServletRequest request,JsonObject obj){
        String email = null;
        String auth = request.getParameter("auth");
        try {
            email = getAuthEmail(auth);
        } catch (InvalidKeyException e) {
            obj.addProperty("errorMessage", "您的安全认证出现问题，请重试！");
        } catch (ParseException e) {
            obj.addProperty("errorMessage", "您的安全认证格式不对，请重试！");
        } catch (Exception e) {
            obj.addProperty("errorMessage", "您的安全认证出现问题，请重试！");
        }
        if (StringUtils.isEmpty(email)) {
            obj.addProperty("errorMessage", "您的安全认证过期，请重试！");
            return null;
        }

        return email;
    }

    private String getAuthEmail(String auth) throws ParseException, InvalidKeyException{
        try {
            String decode = decodeAuth(auth);
            JsonObject obj = new Gson().fromJson(decode, JsonObject.class);
            String email = obj.get("email").getAsString();
            String date = obj.get("data").getAsString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(date);
            if(notExpired(d)){
                return email;
            }else{
                return null;
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                 | BadPaddingException | UnsupportedEncodingException e) {
            throw new InvalidKeyException(e);
        }

    }

    private boolean notExpired(Date d) {
        long now = System.currentTimeMillis();
        long dd = d.getTime();
        //有效期前后30分钟
        if(Math.abs((now-dd))<(1000*60*30)){
            return true;
        }
        return false;
    }

    private static String decodeAuth(String auth) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        SecretKeySpec spec = new SecretKeySpec(getKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, spec);
        byte[] result = cipher.doFinal(Base64.decodeBase64(auth));
        return new String(result,"UTF-8");
    }

    private static byte[] getKey(){
        if(clientKey==null){
            initClientKey();
        }
        return clientKey;
    }

    private synchronized static void initClientKey() {
        if(clientKey==null){
            try {
                InputStream in = LynxEmailResourceController.class.getResourceAsStream("/ddlclientkey");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
                String key = reader.readLine();
                reader.close();
                clientKey = Base64.decodeBase64(key);
            } catch (UnsupportedEncodingException e) {
                LOG.error("",e);
            } catch (IOException e) {
                LOG.error("",e);
            }
        }
    }

    private static class RidAndTidBean{
        private int rid;
        private int tid;
        public int getRid() {
            return rid;
        }
        public void setRid(int rid) {
            this.rid = rid;
        }
        public int getTid() {
            return tid;
        }
        public void setTid(int tid) {
            this.tid = tid;
        }
    }
}
