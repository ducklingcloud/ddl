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
package net.duckling.ddl.web.controller.pan;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.constant.ELayout;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.constant.SupportedFileForOfficeViewer;
import net.duckling.ddl.service.pan.ResponseHeaderUtils;
import net.duckling.ddl.service.render.JSPRendable;
import net.duckling.ddl.service.render.Rendable;
import net.duckling.ddl.service.render.RenderingService;
import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.MimeType;
import net.duckling.ddl.util.PdfStatus;
import net.duckling.ddl.util.PlainTextHelper;
import net.duckling.ddl.util.ShareResourceDownloadCountUtil;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.meepo.api.IPanService;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.PanShareInfo;
import com.meepotech.sdk.PanMeta;


@Controller
@RequestMapping("ff/{id}")
public class PanShareDownloadController {
	private static final String PDF_UNSUPPORTED = "unsupported";
    private static final String PDF_ORIGINAL = "original_pdf";
	private static final Logger LOG = Logger.getLogger(PanShareDownloadController.class);
	private static final String PAN_SHARE_SESSION_KEY = "pan_share_id";
	@Autowired
    private IPanService panService;
	@Autowired
	private PanShareResourceService panShareResourceService;
	@Autowired
    private RenderingService farenderingService;
	@Autowired
	private AoneUserService aoneUserService;
	@Autowired
	private URLGenerator urlGenerator;
	
	@RequestMapping
	public ModelAndView display(HttpServletRequest request,HttpServletResponse response, @PathVariable("id")String idCode) {
		int id = ShareRidCodeUtil.decode(idCode);
		PanShareResource pr = panShareResourceService.get(id);
		ModelAndView mv = dealFile(request,pr);
		SimpleUser user = aoneUserService.getSimpleUserByUid(pr.getShareUid());
		PanResourceBean bean = getPanResource(pr.getPanShareId());
		mv.addObject("shareUserName",user.getName());
		mv.addObject("shareUid", user.getUid());
		mv.addObject("shareResource",pr);
		mv.addObject("resource", bean);
		mv.addObject("sizeShort", FileSizeUtils.getFileSize(bean.getSize()));
		mv.addObject("fileExtend", getFileExtend(bean.getTitle(), bean.getSize()));
		mv.addObject("isPreview", isPreview(bean.getTitle(), bean.getSize()));
		 mv.addObject("filename", bean.getTitle());
		 fileOnlineShow(mv,bean.getTitle(),bean.getTitle().lastIndexOf('.'));
		 mv.addObject("clbPreviewUrl", urlGenerator.getBaseUrl() + "/wopi/s?panShareId=" + pr.getPanShareId());
		return mv;
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
	
	public void fileOnlineShow(ModelAndView mv, String strFilename, int index) {
        String strFileType = null;
        if (index != -1 && strFilename.length() > (index + 1)) {
            strFileType = strFilename.substring(index + 1);
        }
        if (null != strFileType) {
            strFileType = strFileType.toLowerCase();
            String pdfstatus = PdfStatus.SOURCE_NOT_FOUND.toString();// 表示该类型文档的PDF不存在
            boolean supported = SupportedFileForOfficeViewer.isSupported(strFileType);
            if ("pdf".equals(strFileType)) {
                pdfstatus = PDF_ORIGINAL;
            } else if (supported) {
                pdfstatus = PDF_UNSUPPORTED;
            } else {
                pdfstatus = PDF_UNSUPPORTED;// 表示不支持该类型文档的在线显示
            }
            if (pdfstatus == PDF_UNSUPPORTED && isSupportedFileType(strFileType)) { // 剔除图片的无法转换信息
                strFileType = "img";
            }
            mv.addObject("strFileType", PlainTextHelper.convert2BrushClassFileType(strFileType));
            mv.addObject("pdfstatus", pdfstatus);
            mv.addObject("supported", supported);
        }
    }
	 private boolean isSupportedFileType(String fileType) {
	        if (null == fileType || "".equals(fileType)) {
	            return false;
	        }
	        if (SupportedFileForOfficeViewer.isSupported(fileType)) {
	            return true;
	        }
	        if (File.isPictureFileTypeForSearch(fileType)) {
	            return true;
	        }
	        return false;
	    }

	private boolean isPreview(String name, long size) {
		if (name.toLowerCase().endsWith("pdf")) {
			return true;
		}
		return SupportedFileForOfficeViewer.isSupportedFile(name) && size < 52428800;
	}

	private PanResourceBean getPanResource(String panShareId){
		try {
		    PanShareInfo meta = panService.getShareMeta(panShareId);
			PanResourceBean bean = new PanResourceBean();
			bean.setCreateTime(new Date(meta.getCreated_millis()));
			bean.setFileType(MeePoMetaToPanBeanUtil.getFileType(meta.getName()));
			bean.setItemType(LynxConstants.TYPE_FILE);
			bean.setPath(meta.getPath());
			bean.setRid(meta.getPath());
			bean.setSize(meta.getMeta().getBytes());
			bean.setSizeStr(meta.getMeta().getSize());
			bean.setTitle(meta.getName());
			return bean;
		} catch (MeePoException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ModelAndView dealFile(HttpServletRequest request, PanShareResource resource) {
		VWBContext context = VWBContext.createContext(request, UrlPatterns.T_TEAM_HOME);
		ModelAndView mv = layout(".share.download", context, "/jsp/pan/pan_share_download.jsp");
		mv.addObject("downloadURL", getDownloadUrl(resource.getId()));
		return mv;
	}

	private String getDownloadUrl(int i) {
		return urlGenerator.getBaseUrl()+"/ff/"+ShareRidCodeUtil.encode(i)+"?func=download";
	}

	protected ModelAndView layout(String template, VWBContext context, String jsp) {
		return layout(template, context, new JSPRendable(jsp, 65536));
	}

	protected ModelAndView layout(String template, VWBContext context, Rendable content) {
		ModelAndView mv = new ModelAndView(template);
		if (content == null) {
			content = farenderingService.createRendable(VWBContext.getCurrentTid(), context.getResource().getRid());
		}
		mv.addObject("content", content);
		if (context!=null&&context.getSite() != null) {
			mv.addObject("teamCode", context.getSite().getTeamContext());
		}
		return mv;
	}
	
	
	
	
	
	@RequestMapping(params = "func=download")
	public void download(@PathVariable("id") String idCode, HttpServletRequest request, HttpServletResponse response) {
		ShareResourceDownloadCountUtil.count(request);
		if (StringUtils.isEmpty(idCode)) {
			LOG.info("Path param should never be empty.");
			return;
		}
		try {
			int id = ShareRidCodeUtil.decode(idCode);
			PanShareResource pr = panShareResourceService.get(id);
			if (pr != null) {
			    PanShareInfo meta = panService.getShareMeta(pr.getPanShareId());
				sendPreviewDoc(meta.getName(), meta.getMeta().getBytes(), pr.getPanShareId(), request, response);
			}
			pr.setDownloadCount(pr.getDownloadCount()+1);
			panShareResourceService.update(pr);
		} catch (MeePoException e) {
			LOG.error("", e);
		}
		
	}

	 @RequestMapping(params="func=pdf")
	    public ModelAndView onlineViewer(@PathVariable("id") String idCode, HttpServletRequest request)
	            throws MeePoException, UnsupportedEncodingException {
	        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_FILE);
	        ModelAndView mv = layout(ELayout.LYNX_BLANK, context, "/jsp/aone/file/onlineViewer.jsp");
	        int id = ShareRidCodeUtil.decode(idCode);
			PanShareResource pr = panShareResourceService.get(id);
			if (pr != null) {
				PanShareInfo meta = panService.getShareMeta(pr.getPanShareId());
				String strFilename = meta.getName();
				int index = strFilename.lastIndexOf('.');
				if (index != -1 && strFilename.length() > (index + 1)) {
					String strFileType = strFilename.substring(index + 1);
					if ("pdf".equals(strFileType.toLowerCase())) {
						mv.addObject("pdfviewerURL", urlGenerator.getBaseUrl()+"/ff/"+idCode+"?func=download");
					}
					mv.addObject("strFileType", "pdf");
				}
				mv.addObject("offPresentationMode", request.getParameter("offPresentationMode"));
				mv.addObject("pageTitle", meta.getName());
				mv.addObject("backURL", urlGenerator.getURL(1, "file", pr.getSharePath(), ""));
			}
			return mv;
	    }
	
	private void sendPreviewDoc(String filename, long size, String panShareId, HttpServletRequest request,
			HttpServletResponse response) {
		OutputStream os = null;
		long p0 = System.currentTimeMillis();
		long tmpSize = size;
		try {
			response.setCharacterEncoding("utf-8");
			String headerValue = ResponseHeaderUtils.buildResponseHeader(request, filename, true);
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", headerValue);
			response.setContentLength((int) size);
			response.setHeader("Content-Length", tmpSize + "");
			os = response.getOutputStream();
			panService.getShareContent(panShareId, null, os);
		} catch (UnsupportedEncodingException e) {
			LOG.error("", e);
		} catch (MeePoException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			IOUtils.closeQuietly(os);
			long p1 = System.currentTimeMillis();
			LOG.info("Download document[name:" + filename + ",size:" + tmpSize + "] use time " + (p1 - p0));
		}

	}
	
	/**
	 * 检查加密文件提取码是否正确
	 * @param request
	 * @param response
	 * @param ridCode
	 * @param code
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST, params="func=checkcode")
	public void checkcode(HttpServletRequest request,HttpServletResponse response,@PathVariable("id")String idCode,
			@RequestParam("code")String code){
		int id = ShareRidCodeUtil.decode(idCode);
		PanShareResource shareRes = panShareResourceService.get(id);
		
		JSONObject json = new JSONObject();
		json.put("result", "");
		if(shareRes!=null && shareRes.getPassword().equalsIgnoreCase(code.trim())){
			Set<Integer> fetchCodes = (Set<Integer>)request.getSession().getAttribute(PAN_SHARE_SESSION_KEY);
			if(fetchCodes==null){
				fetchCodes = new HashSet<Integer>();
				request.getSession().setAttribute(PAN_SHARE_SESSION_KEY, fetchCodes);
			}
			fetchCodes.add(id);
			json.put("result", "ok");
		}
		
		JsonUtil.writeJSONObject(response, json);
	}
	 @RequestMapping(params="func=doError")
	    public ModelAndView doError(HttpServletRequest request,HttpServletResponse response,@PathVariable("id")String idCode){
	    	String error = request.getParameter("error");
	    	int id = ShareRidCodeUtil.decode(idCode);
	    	ModelAndView mv = null;
	    	VWBContext context = VWBContext.createContext(request, LynxConstants.TYPE_FILE);
	    	if("shareNull".equals(error)){
	    		mv = layout(".share.download", context, "/jsp/aone/file/shareDownloadCode.jsp");
	    		mv.addObject("type", "shareNull");
	    		return mv;
	    	}
	    	PanShareResource shareResource = panShareResourceService.get(id);
	    	String userName = aoneUserService.getUserNameByID(shareResource.getShareUid());
	    	if("resourceDelete".equals(error)){
	    		mv = layout(".share.download", context, "/jsp/aone/file/shareDownloadCode.jsp");
	    		mv.addObject("type", "resourceDelete");
	    		mv.addObject("shareUserName",userName);
	    		return mv;
	    	}
	    	if("noAuth".equals(error)){
	    		mv = layout(".share.download", context, "/jsp/aone/file/shareDownloadCode.jsp");
	    		mv.addObject("shareUserName",userName);
	    		mv.addObject("ridCode", idCode);
	    		mv.addObject("type", "noAuth");
	    	}
	    	return mv;
	    }
	
}
