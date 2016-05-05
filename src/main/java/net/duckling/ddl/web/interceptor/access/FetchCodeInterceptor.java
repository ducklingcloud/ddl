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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.resource.FolderPath;
import net.duckling.ddl.service.resource.FolderPathService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.share.ShareResourceService;
import net.duckling.ddl.util.ShareRidCodeUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FetchCodeInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private ShareResourceService shareResourceService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private FolderPathService folderPathService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(isNotFilter(request)){
			return true;
		}
		int rid = getShareRid(request);
		ShareResource sr = shareResourceService.get(rid);
		if(sr==null){
			sendToDoEerror(request, response, "shareNull");
			return false;
		}
		Resource r = resourceService.getResource(rid);
		if(r==null||r.isDelete()){
			sendToDoEerror(request, response, "resourceDelete");
			return false;
		}
		
		//验证分享提取码
		if(!StringUtils.isEmpty(sr.getPassword())){
			Set<Integer> codes =(Set<Integer>) request.getSession().getAttribute("share_rid");
			if(codes==null || !codes.contains(rid)){
				sendToDoEerror(request, response, "noAuth");
				return false;
			}
		}
		
		List<Integer> currentId = getCurrentRid(request);
		//是否是分享目录下的文件
		if(!currentId.isEmpty()){
			for(Integer i : currentId){
				if(!isSubFolder(i,rid, r.getTid())){
					sendToDoEerror(request, response, "shareNull");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
     * 判断是否是分享文件夹的子目录
     * @param shareRid
     * @param currentRid
     * @return
     */
    private boolean isSubFolder(int currentRid, int shareRid, int tid){
    	List<FolderPath> pathList =  folderPathService.getPath(tid, currentRid);
    	boolean result = false;
    	for(FolderPath item : pathList){
    		if(item.getAncestorRid() == shareRid){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }
	
	private int getShareRid(HttpServletRequest request){
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
	
	private static List<Integer> getCurrentRid(HttpServletRequest request){
		List<Integer> result = new ArrayList<Integer>();
		String r = request.getParameter("rid");
		int rid = 0;
		if(StringUtils.isEmpty(r)){
			String path = request.getParameter("path");
			if(!StringUtils.isEmpty(path)){
				String[] s=path.split("/");
				try{
					rid = Integer.parseInt(s[s.length-1]);
					result.add(rid);
				}catch(Exception e){}
			}else{
				String[] rids = request.getParameterValues("rids");
				if(rids!=null){
					for(String ri : rids){
						try{
							rid = Integer.parseInt(ri);
							result.add(rid);
						}catch(Exception e){}
					}
				}
				
			}
		}else{
			rid = Integer.parseInt(r);
			result.add(rid);
		}
		return result;
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
