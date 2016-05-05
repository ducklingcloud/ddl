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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.pan.PanAclUtil;
import net.duckling.ddl.util.FileSizeUtils;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.util.StringUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;
import net.duckling.meepo.api.IPanService;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.MeePoMeta;

@Controller
@RequestMapping("/api/pan/upload")
@RequirePermission(authenticated=true)
public class APIPanUploadController {
	@Autowired
    private IPanService service;
    
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public void file(@RequestParam("file") MultipartFile uplFile,
			HttpServletRequest request, HttpServletResponse response) throws MeePoException {
		String fileName = uplFile.getOriginalFilename();
		String parentPath = decode(request.getParameter("parentRid"));
		boolean serialName = Boolean.valueOf(StringUtil.getValue(request.getParameter("serialName"), "false"));
		
		long size = uplFile.getSize();
        String remotePath = getRemotePath(parentPath, fileName);
		
        JSONObject j = new JSONObject();
        try {
			UploadResult re = updateFile(request, uplFile.getInputStream(), size, remotePath, serialName);
			if(re.status){
				j.put("success", true);
				j.put("rid", encode(re.getMeta().restorePath));
			}else{
				j.put("success", false);
				j.put("message", re.getMessage());
			}
		} catch (IOException e) {
			e.printStackTrace();
			j.put("success", false);
			j.put("message", e.getMessage());
			j.put("error", e.getMessage());
		}
        JsonUtil.writeJSONObject(response, j);
	}
	
	private String getRemotePath(String parentPath, String fileName) {
        if (parentPath == null || parentPath.length() == 0 || "/".equals(parentPath)) {
            return "/" + fileName;
        } else {
            if (!parentPath.startsWith("/")) {
                parentPath = "/" + parentPath;
            }
            if (parentPath.endsWith("/")) {
                return parentPath + fileName;
            } else {
                return parentPath + "/" + fileName;
            }
        }
    }
	private String decode(String s) {
		if("0".equals(s)){
			return "/";
		}
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
	
	private static String encode(String s){
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	private UploadResult updateFile(HttpServletRequest request, InputStream in, long size, String remotePath, Boolean serialName) {
        UploadResult re = new UploadResult();
        MeePoMeta meta = null;
        try {
        	if(FileSizeUtils.ONE_MB*50>size){
        		meta = service.upload(PanAclUtil.getInstance(request), in, size, remotePath, true);
        	}else{
        		meta = service.uploadChunkedFile(PanAclUtil.getInstance(request), in, size, remotePath, true);
        	}
            if (meta == null) {
                re.status = false;
                re.setMessage("上传出现问题");
            } else {
                re.setStatus(true);
                re.setMeta(meta);
            }
        } catch (MeePoException e) {
            re.setStatus(false);
            re.setMessage(e.getMessage());
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(b);
            e.printStackTrace(pw);
            pw.flush();
            re.setStackTrace(new String(b.toByteArray()));
        }
        return re;
    }
	
	private static class UploadResult {
        private boolean status;
        private int errorCode;
        private String message;
        private MeePoMeta meta;
        private String stackTrace;

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public MeePoMeta getMeta() {
            return meta;
        }

        public void setMeta(MeePoMeta meta) {
            this.meta = meta;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }
    }
	
}
