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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.exception.HasDeletedException;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.util.UrlCoder;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 手机客户端上传图片接口
 * 
 * @date 2011-10-13
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/api/upload")
@RequirePermission(target = "team", operation = "edit")
public class APIUploadController extends BaseController {

	private static final Logger LOGGER = Logger
			.getLogger(APIUploadController.class);
	private static final String PHOTO_HTML = "<p><a linktype='file' class='file' href=\"{0}\"><img src=\"{1}\"></a></p><p>{3}</p>";
	private static final String SOUND_HTML = "<p><a linktype='file' class='file' href=\"{0}\">{2}</a></p><p>{3}</p>";
	@Autowired
	private ResourceOperateService operateService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST, params = "func=update")
	public void update(@RequestParam("file") MultipartFile uplFile,
			@RequestParam("rid") int rid, HttpServletRequest request,
			HttpServletResponse response) {
		VWBContext vwbcontext = VWBContext.createContext(request,
				UrlPatterns.T_ATTACH);
		int tid = VWBContext.getCurrentTid();
		String uid = vwbcontext.getCurrentUID();
		String filename = uplFile.getOriginalFilename();
		long size = uplFile.getSize();
		InputStream in = null;
		try {
			in = uplFile.getInputStream();
			FileVersion fileVersion = operateService.updateCLBFile(rid, tid,
					uid, filename, size, in, null);
			JSONObject obj = new JSONObject();
			obj.put("result", true);
			obj.put("error", "NoError");
			obj.put("rid", fileVersion.getRid());
			obj.put("version", fileVersion.getVersion());
			JsonUtil.writeJSONObject(response, obj);
			response.flushBuffer();
		} catch (IOException e) {
			JsonUtil.writeResult(response, "result", false);
			LOGGER.error("upload picture from mobile failed.", e);
		} catch (NoEnoughSpaceException e) {
			printFailResponse(response, e);
		} catch (HasDeletedException e){
			printFailResponse(response, e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
	@RequestMapping(method = RequestMethod.POST)
	public void file(@RequestParam("file") MultipartFile uplFile,
			HttpServletRequest request, HttpServletResponse response) {
		String func = request.getParameter("func");
		boolean serialName = Boolean.valueOf(StringUtil.getValue(request.getParameter("serialName"), "false"));
		String folderName = request.getParameter("folderName");
		String htmlTemplate = "audio".equals(func) ? SOUND_HTML : PHOTO_HTML;
		try {
			doUpload(uplFile, request, response, htmlTemplate, serialName, folderName);
		} catch (NoEnoughSpaceException e) {
			printFailResponse(response, e);
		}
	}

	/**
	 * @param uplFile
	 * @param request
	 * @param response
	 * @param htmlTemplate
	 */
	@SuppressWarnings("unchecked")
	private void doUpload(MultipartFile uplFile, HttpServletRequest request,
			HttpServletResponse response, String htmlTemplate, boolean serialName, String folderName)
			throws NoEnoughSpaceException {
		
		JSONObject j = new JSONObject();
		
		String folder = StringUtils.isEmpty(folderName)? null : UrlCoder.decode(folderName);
		try {
			VWBContext vwbcontext = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
			int p = 0;
			try {
				p = Integer.parseInt(request.getParameter("parentRid"));
			} catch (Exception e) {
			}
			// 创建图片
			String uid = vwbcontext.getCurrentUID();
			int tid = VWBContext.getCurrentTid();
			FileVersion item = operateService.upload(uid, tid, p, uplFile.getOriginalFilename(), uplFile.getSize(),
						uplFile.getInputStream(), true, true, serialName, folder);
			
			if (item != null) {
				j.put("rid", item.getRid());
				j.put("success", true);
			} else {
				j.put("success", false);
			}
		} catch (IOException e) {
			j.put("success", false);
			LOGGER.error("upload picture from mobile failed.", e);
		} finally {
			try {
				uplFile.getInputStream().close();
			} catch (IOException ignored) {
			}
		}
		JsonUtil.writeJSONObject(response, j);
	}

	@SuppressWarnings("unchecked")
	private void printFailResponse(HttpServletResponse response,
			NoEnoughSpaceException e) {
		JSONObject j = new JSONObject();
		j.put("result", false);
		j.put("message", e.getMessage());
		j.put("error", "noEnoughSpace");
		JsonUtil.writeJSONObject(response, j);
	}

	@SuppressWarnings("unchecked")
	private void printFailResponse(HttpServletResponse response,
			HasDeletedException e) {
		JSONObject j = new JSONObject();
		j.put("result", false);
		j.put("message", e.getMessage());
		j.put("error", "deleted");
		JsonUtil.writeJSONObject(response, j);
	}
}