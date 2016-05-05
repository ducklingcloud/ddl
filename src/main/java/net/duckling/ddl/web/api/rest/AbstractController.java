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

package net.duckling.ddl.web.api.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.util.JsonUtil;
import net.duckling.ddl.web.interceptor.access.AccessTokenInceptor;
import net.duckling.ddl.web.interceptor.access.OnDeny;
import net.duckling.ddl.web.vo.ErrorMsg;

/**
 * Controller基础类
 */
public abstract class AbstractController {
	private static final Logger LOG = Logger.getLogger(AbstractController.class);
	
	@OnDeny("*")
	public void onDeny(String method, HttpServletRequest request, HttpServletResponse response){
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		LOG.error("forbidden. {uri:" + request.getRequestURI() + ", uid:" + getCurrentUid(request) 
				+ ", tid:" + getCurrentTid() + " ,teamCode:" + request.getParameter("teamCode") + " ,access_token:"+getAccessToken(request) + "}" );
		JsonUtil.write(response, ErrorMsg.NEED_PERMISSION);
	}
	
	private String getAccessToken(HttpServletRequest request){
		String accessToken = request.getParameter(AccessTokenInceptor.PARAM_ACCESS_TOKEN);
		if(accessToken!=null){
			return accessToken;
		}
		String auth = request.getHeader("Authorization");
		if(auth!=null && auth.startsWith("Bearer ")){
			return auth.substring(7);
		}
		return null;
	}
	
	@ExceptionHandler  
    public void exp(HttpServletRequest request, HttpServletResponse response, Exception ex) {  
        if(ex instanceof MissingServletRequestParameterException) {
        	MissingServletRequestParameterException me = (MissingServletRequestParameterException)ex;
        	ErrorMsg msg = ErrorMsg.MISSING_PARAMETER;
        	LOG.error("Required parameter ["+ me.getParameterName() +"] is missing.", ex);
        	msg.setMsg("Required parameter ["+ me.getParameterName() +"] is missing.");
        	writeError(msg, response);
        }else if(ex instanceof TypeMismatchException){
        	TypeMismatchException te = (TypeMismatchException)ex;
        	ErrorMsg msg = ErrorMsg.TYPE_MISMATCH;
        	LOG.error("Value ["+ te.getValue() +"] of type is mismatch.", ex);
        	
        	msg.setMsg("Value ["+ te.getValue() +"] of type is mismatch.");
        	writeError(msg, response);
        }else{
        	LOG.error("unkonwn error.", ex);
        	writeError(ErrorMsg.UNKNOW_ERROR, response);
        }
    }  
	
	public String getCurrentUid(HttpServletRequest request) {
		return VWBSession.getCurrentUid(request);
	}
	
	public String getCurrentUsername(HttpServletRequest request) {
		return VWBSession.getCurrentUidName(request);
	}
	
	public int getCurrentTid() {
		return VWBContext.getCurrentTid();
	}
	
	public void writeError(ErrorMsg msg, int status, HttpServletResponse response){
		response.setStatus(status);
		JsonUtil.write(response, msg);
	}
	
	public void writeError(ErrorMsg msg, HttpServletResponse response){
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		JsonUtil.write(response, msg);
	}
}
