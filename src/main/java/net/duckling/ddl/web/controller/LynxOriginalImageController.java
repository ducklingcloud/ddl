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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.web.interceptor.access.RequirePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/{teamCode}/originalimage/{rid}")
@RequirePermission(target="team",operation="view")
public class LynxOriginalImageController extends BaseController{

    
    @Autowired
    private FileVersionService fileVersionService;
    @Autowired
    private URLGenerator urlGenerator;
    
	@RequestMapping
	public void getOriginalImage(HttpServletRequest request,HttpServletResponse response,@PathVariable int rid) throws ServletException, IOException{
		String version = request.getParameter("version");
		int v = 0;
		try{
			v = Integer.parseInt(version);
		}catch(Exception e){}
		int tid = VWBContext.getCurrentTid();
		FileVersion f = null;
		if(v<=0){
			f = fileVersionService.getLatestFileVersion(rid, tid);
		}else{
			f =fileVersionService.getFileVersion(rid, tid, v);
		}
		request.setAttribute("title",f.getTitle());
		request.setAttribute("downloadURL", urlGenerator.getURL(tid, UrlPatterns.T_DOWNLOAD, Integer.toString(rid), "type=doc&imageType=original&version="+version));
		request.getRequestDispatcher("/jsp/aone/tag/originalImage.jsp").forward(request, response);
	}
}
