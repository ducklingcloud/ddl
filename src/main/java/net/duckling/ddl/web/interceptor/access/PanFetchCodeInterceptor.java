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
package net.duckling.ddl.web.interceptor.access;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.share.PanShareResource;
import net.duckling.ddl.service.share.PanShareResourceService;
import net.duckling.ddl.util.ShareRidCodeUtil;
import net.duckling.meepo.api.IPanService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.meepotech.sdk.PanShareInfo;

public class PanFetchCodeInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	private PanShareResourceService panShareResourceService;
	@Autowired
	private IPanService panService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(isNotFilter(request)){
			return true;
		}
		int id = getShareId(request);
		PanShareResource pan = panShareResourceService.get(id);
		if(pan==null||pan.isDelete()){
			sendToDoEerror(request, response, "shareNull");
			return false;
		}
		try{
			PanShareInfo meta = panService.getShareMeta(pan.getPanShareId());
			if(meta==null){
				sendToDoEerror(request, response, "resourceDelete");
				return false;
			}
		}catch(Exception e){
			sendToDoEerror(request, response, "resourceDelete");
			return false;
		}
		if(StringUtils.isEmpty(pan.getPassword())){
			return true;
		}
		
		Set<Integer> codes =(Set<Integer>) request.getSession().getAttribute("pan_share_id");
		if(codes!=null&&codes.contains(id)){
			return true;
		}
		sendToDoEerror(request, response, "noAuth");
		return false;
	}
	
	
	private int getShareId(HttpServletRequest request){
		String requestURI = request.getRequestURI();
		int index = requestURI.lastIndexOf("/");
		String ridS = requestURI.substring(index+1);
		return ShareRidCodeUtil.decode(ridS);
	}
	
	private boolean isNotFilter(HttpServletRequest request){
		String func = request.getParameter("func");
		if(StringUtils.isNotEmpty(func)){
			return "checkcode".equals(func)||"doError".equals(func);
			
		}
		return false;
	}
	private void sendToDoEerror(HttpServletRequest request,HttpServletResponse response,String errorCode) throws ServletException, IOException{
		String requestURI = request.getRequestURI();
		String content = request.getContextPath();
		if(StringUtils.isNotEmpty(content)){
			requestURI = requestURI.substring(content.length());
		}
		request.getRequestDispatcher(requestURI+"?func=doError&error="+errorCode).forward(request, response);
	}
}
