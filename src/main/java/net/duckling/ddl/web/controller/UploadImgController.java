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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.Site;
import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSite;
import net.duckling.ddl.exception.NoEnoughSpaceException;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.resource.ResourceOperateService;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @date 2011-4-21
 * @author zhangxiao
 */
@Controller
@RequestMapping("/{teamCode}/uploadImg")
@RequirePermission(target="team", operation="view")
public class UploadImgController extends BaseController{
    
	@Autowired
	private ResourceOperateService resourceOperateService;
    
    
	@RequestMapping
	public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Site site = VWBContext.findSite(request);
		FileVersion attachItem=new FileVersion(); 
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		//下载远程图片
		String url = request.getParameter("url");
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(url);
		String fakeReferer = getFakeReferer(url);//待下载的图片的域名
		if(fakeReferer!=null){
			String thisReferer = request.getHeader("Referer");
			if(thisReferer.contains(fakeReferer)){//如果与编辑器的域名相同，则认为是服务器本地图片
				response.getWriter().write(getReturnXml(site, 0, "已经是服务器本地图片了", attachItem));
				response.flushBuffer();
				return ;
			}
			getMethod.setRequestHeader("Referer", fakeReferer);
		}
		client.executeMethod(getMethod);
		
		//上传图片到服务器，得到本地链接
		
		try {
			attachItem = uploadImg(getMethod, request);
		} catch (NoEnoughSpaceException e) {
			JSONObject j = new JSONObject();
			j.put("result", false);
			j.put("message", e.getMessage());
			j.put("error", e.getMessage());
			JsonUtil.writeJSONObject(response, j);
			return;
		}
		//返回结果
		response.getWriter().write("");
		response.getWriter().write(getReturnXml(site, 1, "下载成功！图片已保存到服务器", attachItem));
		response.flushBuffer();
	}
	private FileVersion uploadImg(GetMethod getMethod, HttpServletRequest request) throws IOException, NoEnoughSpaceException{
        String filename = request.getParameter("filename");
        VWBContext context = VWBContext.createContext(request, UrlPatterns.T_ATTACH);
        int parentRid = 0;
        try{
        	parentRid = Integer.parseInt(request.getParameter("parentRid"));
        }catch(Exception e){}
        return resourceOperateService.upload(context.getCurrentUID(), context.getSite().getId(),parentRid, filename, getMethod.getResponseContentLength(), getMethod.getResponseBodyAsStream());
	}
	private String getReturnXml(Site site,int code, String msg, FileVersion attachItem){
		String downloadUrl = site.getURL(UrlPatterns.T_DOWNLOAD_CACHE, Integer.toString(attachItem.getRid()), null);
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
		sb.append("<data>");
		sb.append("<vwbClbId>");
		sb.append(attachItem.getRid());
		sb.append("</vwbClbId>");
		sb.append("<docId>");
		sb.append(attachItem.getRid());
		sb.append("</docId>");
		sb.append("<code>");
		sb.append(code);
		sb.append("</code>");
		sb.append("<msg>");
		sb.append(msg);
		sb.append("</msg>");
		sb.append("<newurl>");
		sb.append(downloadUrl);
		sb.append("</newurl>");
		sb.append("<fid>");
		sb.append(attachItem.getRid());
		sb.append("</fid>");
		sb.append("<infoURL>");
		sb.append(site.getURL("file", attachItem.getRid()+"",null));
		sb.append("</infoURL>");
		sb.append("<title>");
		sb.append(attachItem.getTitle());
		sb.append("</title>");
		sb.append("<previewURL>");
		sb.append(downloadUrl);
		sb.append("</previewURL>");
		sb.append("<type>IMAGE</type>");
		sb.append("</data>");
		return sb.toString();
	}
	/**
	 * 得到欲下载的图片的域名，用于躲避目标图片的防盗链策略
	 * @param url
	 * @return
	 */
	private String getFakeReferer(String url){
		String domain = null;
		Matcher m = REG.matcher(url);
		if(m.find()){
			domain = m.group(0);
		}
		return domain;
	}
	private static final Pattern REG = Pattern.compile("(http|ftp|https):\\/\\/[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+){0,4}(:[\\d]+){0,1}");
}
