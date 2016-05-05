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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.DucklingProperties;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.KeyConstants;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.SupportedFileForOfficeViewer;
import net.duckling.ddl.service.authority.AuthorityService;
import net.duckling.ddl.service.browselog.BrowseLogService;
import net.duckling.ddl.service.copy.ICopyService;
import net.duckling.ddl.service.file.DFileRef;
import net.duckling.ddl.service.file.FileSaver;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.relaterec.DGridDisplay;
import net.duckling.ddl.service.relaterec.RelateRecordService;
import net.duckling.ddl.service.render.DPageRendable;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.resource.FolderPath;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.PageRender;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.impl.SubscriptionServiceImpl;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.service.team.TeamService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.util.Browser;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.FileUtil;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.PdfStatus;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.web.bean.DFileRefView;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import cn.cnic.cerc.dlog.client.WebLog;
import cn.vlabs.clb.api.SupportedFileFormatForOnLineViewer;

@Controller
@RequestMapping("/{teamCode}/r/{rid}")
@RequirePermission(target = "team", operation = "view")
public class LynxDDocController extends BaseController{
	private static final String VIEW_TEMPLATE = ELayout.LYNX_PAGE;
	private static final String PDF_UNSUPPORTED = "unsupported";
	private static final String PDF_ORIGINAL = "original_pdf";
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IGridService gridService;
	@Autowired
	private ICopyService copyService;
	@Autowired
	private RelateRecordService relateRecService;
	@Autowired
	private AuthorityService authorityService;
	@Autowired
	private ResourceOperateService resourceOperateService;
	@Autowired
	private URLGenerator urlGenerator;
	@Autowired
	private BrowseLogService browseLogService;
	@Autowired
	private FileVersionService fileVersionService;
	@Autowired
	private FolderPathService folderPathService;
	@Autowired
	private SubscriptionServiceImpl subscriptionService;
	@Autowired
	private AoneUserService aoneUserService;
//	@Autowired
//	private ResourceDirectoryTrashService resourceDirectoryTrashService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private RenderingService renderingService;
	//@Value("${duckling.baseAddress}")
  	//private String baseAddress;
	@Autowired
	private DucklingProperties config;

	protected static final Logger LOG = Logger.getLogger(LynxPageController.class);
	
	@RequestMapping
	@WebLog(method="pageView",params="rid")
	public ModelAndView display(HttpServletRequest request,HttpServletResponse response, @PathVariable("rid")Integer rid) {
		if(rid==0){
			//文件夹浏览模式
			return new ModelAndView(new RedirectView(urlGenerator.getAbsoluteURL(VWBContext.getCurrentTid(), UrlPatterns.T_LIST, null, null)));
		}
		Resource resource = resourceService.getResource(rid);
		if(resource==null||resource.getTid()!=VWBContext.getCurrentTid()||resource.isDelete()){
			ModelAndView m = dealResourceRemove(request, resource);
			if(m!=null){
				return m;
			}
			notFound(request, response, true);
			return null;
		}
		int tid = resource.getTid();
		ModelAndView mv = null;
		if(resource.isPage()){
			mv = dealDDoc(request, response, resource);
		}else if(resource.isFile()){
			mv = dealFile(request,response ,resource);
		}else if(resource.isFolder()){
			return dealFolder(request,response,resource);
		}else{
			notFound(request, response, true);
			return null;
		}
		mv.addObject("pageType", "list");
		if(resource.isAvailable()){
			mv.addObject("resourePath", getParentPath(resource));
		}
		mv.addObject("teamUrl", urlGenerator.getURL(tid,UrlPatterns.T_LIST, null,null));
		mv.addObject("teamHome",urlGenerator.getURL(tid,UrlPatterns.T_TEAM_HOME, null,null));
		
		addMyTeam(request, mv);
		return mv;
	}
	
	/**
	 * 或取转换pdf文件处理状态
	 * @param request
	 * @param response
	 * @param rid
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(params="func=getPdfStatus")
	public void getPdfStatus(HttpServletRequest request,HttpServletResponse response, @PathVariable("rid")Integer rid) {
		VWBContext.createContext(request, UrlPatterns.T_FILE, rid, LynxConstants.TYPE_FILE);
		int tid = VWBContext.getCurrentTid();
		FileVersion currentVersion = fileVersionService.getLatestFileVersion(rid, tid);
		String pdfstatus = resourceOperateService.queryPdfStatus(currentVersion.getClbId(), "" + currentVersion.getClbVersion());
		
		JSONObject j = new JSONObject();
		j.put("pdfstatus", pdfstatus);
		JsonUtil.writeJSONObject(response, j);
	}
	
	/**
	 * resource已删除通知页面
	 * @param request
	 * @param resource
	 * @return
	 */
	private ModelAndView dealResourceRemove(HttpServletRequest request,Resource resource){
		if(resource!=null&&LynxConstants.STATUS_DELETE.equals(resource.getStatus())){
			String uid = VWBSession.getCurrentUid(request);
			VWBContext context = VWBContext.createContext(request,UrlPatterns.T_PAGE, resource.getRid(),LynxConstants.TYPE_PAGE);
			ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/errors/resourceRemove.jsp");
			boolean recoverFlag = false;
			if ((resource.getCreator().equals(uid) || isAdmin(uid, VWBContext.getCurrentTid()))) {
				if(resource.isFolder()){
					recoverFlag = false;
//					ResourceDirectoryTrash  t = resourceDirectoryTrashService.getResoourceTrash(resource.getRid());
//					if(t!=null){
//						recoverFlag = true;
//					}
				}else{
					recoverFlag = true;
				}
			}
			mv.addObject("recoverFlag", recoverFlag);
			mv.addObject("resource", resource);
			mv.addObject("teamCode", VWBContext.getCurrentTeamCode());
			return mv;
		}else{
			return null;
		}
	}
	
	private ModelAndView dealFolder(HttpServletRequest request, HttpServletResponse response, Resource resource) {
		 List<FolderPath> path = folderPathService.getPath(resource.getTid(), resource.getRid());
		 StringBuilder sb = new StringBuilder();
		 for(FolderPath p : path){
			 if(p.getAncestorRid()!=0){
				 sb.append("/"+p.getAncestorRid());
			 }
		 }
		 String url = urlGenerator.getAbsoluteURL(resource.getTid(),UrlPatterns.T_LIST, null, null);
		 try {
			 url = url+"#path="+URLEncoder.encode(sb.toString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return new ModelAndView(new RedirectView(url));
	}

	private List<Resource> getParentPath(Resource resource){
		int pRid = resource.getBid();
		if(pRid == 0){
			return Collections.emptyList();
		}
		List<Resource> result = folderPathService.getResourcePath(pRid);
		if(result == null){
			return Collections.emptyList();
		}
		return result;
	} 
	/**
	 * 文件浏览处理
	 * @param request
	 * @param response
	 * @param resource
	 * @return
	 */
	private ModelAndView dealFile(HttpServletRequest request, HttpServletResponse response, Resource resource) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE, resource.getRid(), LynxConstants.TYPE_FILE);
        String uid = context.getCurrentUID();
        int tid = context.getSite().getId();
        int rid = resource.getRid();
        ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/tag/fileView.jsp");
        int version = getCurrentVersion(request);
        FileVersion currentVersion;
        if (version > 0) {
            currentVersion = fileVersionService.getFileVersion(rid, tid, version);
        } else {
            currentVersion = fileVersionService.getLatestFileVersion(rid, tid);
        }
        mv.addObject("resource", resource);
        Set<String> starmark = resource.getMarkedUserSet();
        if (null != starmark && starmark.contains(context.getCurrentUID())) {
            mv.addObject("starmark", true);
        } else {
            mv.addObject("starmark", false);
        }
        mv.addObject("editorName", aoneUserService.getUserNameByID(currentVersion.getEditor()));
        mv.addObject("deleteFileURL",urlGenerator.getURL(tid,UrlPatterns.T_FILE, rid + "", "func=moveToTrash&bid=0"));
        mv.addObject("validateURL", urlGenerator.getURL(tid,UrlPatterns.T_FILE, rid + "", "func=removeValidate"));
        mv.addObject("cid",  currentVersion.getClbId());
        mv.addObject("copyLog", copyService.getCopyedDisplay(resource.getRid(), currentVersion.getVersion()));
        mv.addObject("curVersion", currentVersion);
        mv.addObject("latestVersion", currentVersion.getVersion());
        mv.addObject("sizeShort", FileSizeUtils.getFileSize(currentVersion.getSize()));
        mv.addObject("fileExtend", getFileExtend(currentVersion.getTitle(), currentVersion.getSize()));
        request.setAttribute(LynxConstants.PAGE_TITLE, currentVersion.getTitle());
        String downType = "type=doc";
		if (version > 0) {
			downType+="&version=" + version;
		} 
		mv.addObject("downloadURL", urlGenerator.getURL(tid, "download", Integer.toString(rid), downType));
        // Load Version List
        List<FileVersion> versionList = fileVersionService.getFileVersions(rid, tid);
        mv.addObject("versionList", versionList);
        loadRefViewList(mv, rid, tid, currentVersion.getTitle());
        gridService.clickItem(uid, tid, rid, LynxConstants.TYPE_FILE);
        String strFilename = currentVersion.getTitle();
        int index = strFilename.lastIndexOf('.');
        fileOnlineShow(mv, currentVersion, strFilename, index);
        browseLogService.resourceVisited(tid, rid, uid, context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        String enableDConvert = context.getContainer().getProperty(KeyConstants.DCONVERT_SERVICE_ENABLE);
        mv.addObject("enableDConvert", Boolean.valueOf(enableDConvert));
        mv.addObject("uid", context.getCurrentUID());
        return mv;
	}

	public void fileOnlineShow(ModelAndView mv, FileVersion currentVersion, String strFilename, int index) {
		String strFileType = null;
        if (index != -1 && strFilename.length() > (index + 1)) {
            strFileType = strFilename.substring(index + 1);
        }
        if (null != strFileType) {
        	strFileType = strFileType.toLowerCase();
            String pdfstatus = PdfStatus.SOURCE_NOT_FOUND.toString();// 表示该类型文档的PDF不存在
            boolean supported = SupportedFileFormatForOnLineViewer.isSupported(strFileType);
            if ("pdf".equals(strFileType)) {
                pdfstatus = PDF_ORIGINAL;
            } else if (supported) {
                pdfstatus = resourceOperateService.queryPdfStatus(currentVersion.getClbId(), "" + currentVersion.getClbVersion());
            } else {
                pdfstatus = PDF_UNSUPPORTED;// 表示不支持该类型文档的在线显示
            }
            if (pdfstatus == PDF_UNSUPPORTED && isSupportedFileType(strFileType)) { // 剔除图片的无法转换信息
                strFileType = "img";
            }
            mv.addObject("strFileType", PlainTextHelper.convert2BrushClassFileType(strFileType));

            mv.addObject("pdfstatus", pdfstatus);
            mv.addObject("supported", supported);
            mv.addObject("officeSupported", SupportedFileForOfficeViewer.isSupported(strFileType));
        }
	}
	
	private boolean isSupportedFileType(String fileType) {
        if (null == fileType || "".equals(fileType)) {
            return false;
        }
        if (SupportedFileFormatForOnLineViewer.isSupported(fileType)) {
            return true;
        }
        if (net.duckling.ddl.service.tobedelete.File.isPictureFileTypeForSearch(fileType)) {
            return true;
        }
        return false;
    }
	private int getCurrentVersion(HttpServletRequest request) {
        String versionStr = request.getParameter("version");
        int version = 0;
        try {
            if (versionStr != null) {
                version = Integer.parseInt(versionStr);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Could not parse parameter version, use latest version.");
        }
        return version;
    }
    private String getFileExtend(String filename, long size) {
        if (MimeType.isImage(filename)) {
            return "IMAGE";
        } else if (PlainTextHelper.isSupported(MimeType.getSuffix(filename))
                && size < LynxConstants.MAXFILESIZE_CODEREVIEW) {// 文件超过给定大小时不直接显示
            return "TEXT";
        }
        return "FILE";
    }
    
    /**
     * ddoc浏览处理，直接返回页面浏览页
     * @param request
     * @param response
     * @param resource
     * @return
     */
	private ModelAndView dealDDoc(HttpServletRequest request,HttpServletResponse response,Resource resource){
		int rid = resource.getRid();
		VWBContext context = VWBContext.createContext(request,UrlPatterns.T_PAGE, rid,LynxConstants.TYPE_PAGE);
		//add by lvly@2012-07-20
		if(resource!=null&&!StringUtils.isBlank(resource.getStatus())&&LynxConstants.STATUS_DELETE.equals(resource.getStatus())||(resource.isUnpublish()&&!resource.getCreator().equals(context.getCurrentUID()))){
			ModelAndView mv = layout(ELayout.LYNX_MAIN, context, "/jsp/aone/page/pageRemoved.jsp");
			if(resource.getCreator().equals(context.getCurrentUID())||isAdmin(VWBSession.getCurrentUid(request), resource.getTid())){
				mv.addObject("recoverFlag", true);
				mv.addObject("rid", resource.getRid());
				String uid = VWBSession.getCurrentUid(request);
				mv.addObject("adminUnpublish", isAdmin(uid, resource.getTid())&&resource.isUnpublish()&&!resource.getCreator().equals(uid));
				mv.addObject("itemType", resource.getItemType());
			}
			return mv;
		}
		PageRender render = null;
		try{
			render = getPageRender(rid,context);
		}catch(NullPointerException e){
			notFound(request, response, true);
			return null;
		}
		Resource meta = render.getMeta();
		String uid = context.getCurrentUID();
		int tid = context.getSite().getId();
		if(meta==null){
			notFound(request, response, true);
			return null;
		}
		int latestVersion = meta.getLastVersion();
		int version = getRequestVersion(request);
		if (VWBContext.LATEST_VERSION != version && meta.getLastVersion() != version) {
			render = resourceOperateService.getPageRender(meta.getTid(),meta.getRid(),version);
			meta = render.getMeta();
		}
		gridService.clickItem(uid, tid, rid, LynxConstants.TYPE_PAGE);
		ModelAndView mv = layout(VIEW_TEMPLATE, context, new DPageRendable(VWBContext.getCurrentTid(), meta.getRid(), version));
		loadRefViewList(mv, rid, tid, "");
		loadRelatedRecPagesList(mv,context,rid);
		mv.addObject("autoClosepageFlage", request.getParameter("autoClosepageFlage"));
		mv.addObject("resource", resource);
		mv.addObject("pageMeta",meta);
		mv.addObject("pageDetail",render.getDetail());
		mv.addObject("editor",  aoneUserService.getUserNameByID(render.getDetail().getEditor()));
		mv.addObject("version", render.getDetail().getVersion());
		mv.addObject("copyLog",copyService.getCopyedDisplay(resource.getRid(), meta.getLastVersion()));
		mv.addObject("latestVersion",latestVersion);
		mv.addObject("requestVersion", version);
		mv.addObject("uid", context.getCurrentUID());
		mv.addObject("rid", rid);
		mv.addObject("parentRid", resource.getBid());
		request.setAttribute(LynxConstants.PAGE_TITLE, render.getDetail().getTitle());
		Set<String> starmark = resource.getMarkedUserSet();
		if(null!=starmark && starmark.contains(context.getCurrentUID())){
			mv.addObject("starmark", true);
		}else{
			mv.addObject("starmark", false);
		}
		List<Subscription> subs=subscriptionService.getPageSubscribers(tid, rid);
		boolean subFlag=false;
		if(null!=subs&&!subs.isEmpty()){
			for(Subscription sub:subs){
				if(StringUtils.equals(sub.getUserId(), uid)){
					subFlag=true;
					break;
				}
			}
		}
		mv.addObject("subFlag", subFlag);
		context.setResource(resource);
		browseLogService.resourceVisited(tid, rid, uid, context.getCurrentUserName(), LynxConstants.TYPE_PAGE);
		return mv;
	}
	
	
	/**
	 * Load Reference List
	 * @param mv
	 * @param rid
	 * @param tid
	 * @param fileName
	 */
	private void loadRefViewList(ModelAndView mv, int rid, int tid, String fileName) {
        List<DFileRef> refList = fileVersionService.getPageReferences(rid, tid);
        List<DFileRefView> refViewList = new ArrayList<DFileRefView>();
        if (refList != null && refList.size() > 0) {
            for (DFileRef ref : refList) {
                DFileRefView refview = new DFileRefView();
                if (ref.getPageRid() > 0) {
                    Resource page = resourceService.getResource(ref.getFileRid());
                    if (page != null) {
                        refview.setPageName(page.getTitle());
                    } else {
                        LOG.error("page出现不一致情况记录：文件rid=" + rid + "tid=" + tid + "被page ：pid=" + ref.getPageRid() + "Tid="
                                + ref.getTid() + "引用，但此文件为空");
                    }
                }
                refview.setDfileRef(ref);
                refview.setFileName(fileName);
                refViewList.add(refview);
            }
        }
        mv.addObject("refView", refViewList);
    }
	
    private boolean isAdmin(String user,int tid){
		if(user==null||user.length()==0){
			return false;
		}
		return Team.AUTH_ADMIN.equals( authorityService.getTeamAuthority(tid, user));
	}
    private void loadRelatedRecPagesList(ModelAndView mv,VWBContext context,int pid){
    	int num = 5;
    	int tid = VWBContext.getCurrentTid();  
    	String uid = context.getCurrentUID();
    	DGridDisplay dGridDisplay = relateRecService.getRelatedRecOfPage(tid, uid, pid, num);
    	mv.addObject("relatedGrids", dGridDisplay);
    }
    
   
    private int getRequestVersion(HttpServletRequest request) {
		String version = request.getParameter("version");
		if (version != null) {
			try {
				return Integer.parseInt(version);
			} catch (NumberFormatException e) {
				LOG.warn(e.getMessage(),e);
			}
		}
		return VWBContext.LATEST_VERSION;
	}
    
    private PageRender getPageRender(int rid, VWBContext context) {
		int tid = context.getSite().getId();
        return resourceOperateService.getPageRender(tid, rid);
	}
    
    @RequestMapping(params = "func=onlineViewer")
    public ModelAndView onlineViewer(HttpServletRequest request, @PathVariable("rid") int rid) {
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE);
        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/aone/file/onlineViewer.jsp");
        int tid = context.getSite().getId();
        
        Resource dfile = resourceService.getResource(rid);
        if (dfile==null) {
            return layout(ELayout.LYNX_BLANK, context, "/jsp/aone/file/fileRemoved.jsp");
        }
        int version = getCurrentVersion(request);
        FileVersion currentVersion;
        if (version > 0) {
            currentVersion = fileVersionService.getFileVersion(rid, tid, version);
        } else {
            currentVersion = fileVersionService.getLatestFileVersion(rid, tid);
        }
        if (version > 0) {
            mv.addObject("downloadURL",
                    urlGenerator.getURL(tid, "download", Integer.toString(rid), "type=doc&version=" + version));
        } else {
            mv.addObject("downloadURL", urlGenerator.getURL(tid, "download", Integer.toString(rid), "type=doc"));
        }

        String strFilename = currentVersion.getTitle();
        int index = strFilename.lastIndexOf('.');
        if (index != -1 && strFilename.length() > (index + 1)) {
            String strFileType = strFilename.substring(index + 1);
            if ("pdf".equals(strFileType.toLowerCase())) {
                // String str=(context.getSite().getURL("download",
                // Integer.toString(fid),
                // "type=doc&version="+currentVersion.getVersion()));
                mv.addObject(
                        "pdfviewerURL",
                        urlGenerator.getURL(tid, "download", Integer.toString(rid), "type=doc&version="
                                + currentVersion.getVersion()));
            } else {
                mv.addObject(
                        "pdfviewerURL",
                        urlGenerator.getURL(tid, "download", Integer.toString(rid), "type=pdf&version="
                                + currentVersion.getVersion()));
            }
            mv.addObject("strFileType", "pdf");
        }
        mv.addObject("offPresentationMode", request.getParameter("offPresentationMode"));
        mv.addObject("pageTitle", dfile.getTitle());
        mv.addObject("backURL", urlGenerator.getURL(tid, "file", Integer.toString(rid), ""));
        // add by lvly@2012-07-23 记下载次数
        browseLogService.resourceVisited(tid, rid, context.getCurrentUID(), context.getCurrentUserName(), LynxConstants.TYPE_FILE);
        return mv;
    }
    
	@RequestMapping(params="func=exportPdf")
	public void exportPdf(HttpServletRequest request,HttpServletResponse response, @PathVariable("rid")Integer rid) {
		VWBContext context = VWBContext.createContext(request,UrlPatterns.T_PAGE, rid,LynxConstants.TYPE_PAGE);
		int tid = VWBContext.getCurrentTid();
		Team team = teamService.getTeamByID(tid);
		Resource r = resourceService.getResource(rid);
		response.setHeader("Content-disposition", Browser.encodeFileName(request.getHeader("USER-AGENT"), r.getTitle()+".pdf"));
		response.setContentType("application/pdf");
		
		PageRender render = resourceOperateService.getPageRender(tid, rid);
		String html = renderingService.getHTML(context, render);
		StringBuffer buf = new StringBuffer();
		List<String> imagePathList = new ArrayList<String>();
        buf.append("<html><head><title>"+r.getTitle()+"</title>");
        buf.append("<link type=\"text/css\" rel=\"stylesheet\" href=\""+ context.getBaseURL() +"/jsp/aone/css/css.css\" />");
        buf.append("<style type=\"text/css\"> *{ font-family: simsun; } </style>");
        buf.append("</head>");
        buf.append("<body style=\"margin:0;\">");
        buf.append("<div id=\"DCT_viewcontent\">");
        html = processImagePath(html, team.getName(), imagePathList, request);
        buf.append(html);
        buf.append("</div>");
        buf.append("</body></html>");
        
        Document document = new Document();
        PdfWriter writer;
		try {
			String fontPath = getFontPath("simsun.ttf");
			XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(fontPath);
			fontImp.register(fontPath, "simsun");
			FontFactory.setFontImp(fontImp);
			
			writer = PdfWriter.getInstance(document, response.getOutputStream());
			document.open();
	        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(buf.toString().getBytes(LynxConstants.UTF8)), LynxConstants.UTF8);
	        XMLWorkerHelper.getInstance().parseXHtml(writer, document, isr); 
		} catch (DocumentException e) {
			LOG.error("export pdf error. {rid:" + r.getRid() + "} : " + e.getMessage());
		} catch (IOException e) {
			LOG.error("export pdf Error. {rid:" + r.getRid() + "} : " + e.getMessage());
		}finally{
			try {document.close();}
			catch(Exception e){LOG.warn(e.getMessage());};
		}
		
		//删除本地临时文件
		for(String path : imagePathList){
			FileUtil.deleteFile(path);
		}
	}
	
	/**
	 * 图片的uri路径替换为本地绝对地址路径
	 * @param html
	 * @param cachePath
	 * @param teamName
	 * @param imagePathList
	 * @return
	 */
	private String processImagePath(String html, String teamName, List<String> imagePathList, HttpServletRequest request){
		String cachePath = getImageCachePath(request);
		String baseAddress = config.getProperty("duckling.baseAddress");
		String patternString = "src=\"(("+ baseAddress + request.getContextPath() +"/|/)"+teamName+"/downloadResource/(\\d+))\"";
		 
	    Pattern pattern = Pattern.compile(patternString);
	    Matcher matcher = pattern.matcher(html);
	    StringBuffer sb = new StringBuffer();
	    while(matcher.find()) {
	    	String imageUri = matcher.group(1);
	    	int rid;
	    	try{
				rid = Integer.valueOf(matcher.group(3));
			}catch(NumberFormatException e){
				LOG.warn("parse rid error.{uri:"+imageUri+"} :" + e.getMessage());
				continue;
			};
	    	String path = createCacheImage(rid, cachePath);
	        matcher.appendReplacement(sb, "src=\""+ path.replace("\\", "/") + "\"");
	        imagePathList.add(path);
	    }
	    matcher.appendTail(sb);
	    return sb.toString();
	}
	
	private String getImageCachePath(HttpServletRequest request){
		return request.getSession().getServletContext().getRealPath("/WEB-INF/cache/pdf_export");
	}
	
	private String getFontPath(String fontFileName){
		return Thread.currentThread().getContextClassLoader().getResource("/").getPath()+File.separator+"fonts"+File.separator+fontFileName;
	}
	
	/**
	 * 从clb获取图片，并存储到本地临时目录
	 * @param rid
	 * @param cachePath
	 * @return
	 */
	private String createCacheImage(int rid, String cachePath){
		int tid = VWBContext.getCurrentTid();
		FileVersion fv = fileVersionService.getLatestFileVersion(rid, tid);
		if(fv==null){
			return null;
		}
		String filePath = cachePath + File.separator+ fv.getRid()+ FileUtil.POINT + FileUtil.getFileExt(fv.getTitle());
		FileSaver fs = new FileSaver(filePath);
		resourceOperateService.getImageContent(fv.getClbId(), 0, null, fs);
		return filePath;
	}

    private ModelAndView addMyTeam(HttpServletRequest request, ModelAndView mv){
    	String uid = VWBSession.getCurrentUid(request);
    	int myTeamId = teamService.getPersonalTeamNoCreate(uid);
		String myTeamCode = teamService.getTeamNameFromEmail(uid);
		mv.addObject("myTeamId",myTeamId);
		mv.addObject("myTeamCode",myTeamCode);
		return mv;
    }

}
