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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class CommonExceptionResolver extends SimpleMappingExceptionResolver{
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {

		// Expose ModelAndView for chosen error view.
		String viewName = determineViewName(ex, request);
		if (viewName != null) {
			//是不是异步请求
			if((request.getHeader("accept")!=null && request.getHeader("accept").indexOf("application/json")>-1)
					|| (request.getHeader("X-Requested-With")!=null && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest")>-1)){
				//JSON格式返回 
	            PrintWriter out = null;
	            try {  
	                out = response.getWriter();
	                out.write(ex.getMessage());
	            } catch (IOException e) {
	            	logger.error(e);
	            }finally{
	            	out.flush();
	            	out.close();
	            }
	            return new ModelAndView();  
	        }else{
	        	// Apply HTTP status code for error views, if specified.  
	            // Only apply it if we're processing a top-level request.  
	            Integer statusCode = determineStatusCode(request, viewName);  
	            if (statusCode != null) {  
	                applyStatusCodeIfPossible(request, response, statusCode);  
	                return getModelAndView(viewName, ex, request);  
	            }
	        }  
	        return null;  
		}
		else {
			return null;
		}
	}
	
}
