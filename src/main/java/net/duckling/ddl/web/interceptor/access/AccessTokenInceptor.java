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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.common.VWBSession;
import net.duckling.ddl.constant.Attributes;
import net.duckling.ddl.service.authenticate.AuthenticationService;
import net.duckling.ddl.service.oauth.impl.OAuthServiceImpl;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.AuthorizationCodeService;
import net.duckling.ddl.service.user.UserExt;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthProblemException;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.UserInfo;

/**
 * @date 2011-8-30
 * @author xiejj@cnic.cn
 */
public class AccessTokenInceptor extends HandlerInterceptorAdapter{
	private static final Logger LOG = Logger.getLogger(AccessTokenInceptor.class);
	
	public static final String PARAM_ACCESS_TOKEN = "access_token";
	
	private AuthorizationCodeService authcodeService;
	private OAuthServiceImpl oauthService;
	private AuthenticationService authenticationService;
	private AoneUserService aoneUserService;
	public void setAoneUserService(AoneUserService aoneUserService){
		this.aoneUserService = aoneUserService;
	}
	public void setOauthService(OAuthServiceImpl oauthService){
		this.oauthService = oauthService;
	}
	public void setAuthenticationService(AuthenticationService authenticationService){
		this.authenticationService = authenticationService;
	}
	public void setAuthcodeService(AuthorizationCodeService authcodeService){
		this.authcodeService = authcodeService;
	}
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String accessToken = getAccessToken(request);
		if (accessToken!=null && !sameAsSessionSaved(request, accessToken)){
			try{
				UserInfo user = getUserInfoFromUmt(accessToken);
				
				String userId = null;
				String trueName = null;
				if(user==null){
					userId = getUserFromInternalOAuth(accessToken);
				}else{
					userId = user.getCstnetId();
					trueName = user.getTrueName()==null ? userId : user.getTrueName();
				}
				
				if (userId!=null){
					UserExt userExt = aoneUserService.getUserExtInfo(userId);
					UserPrincipal up;
					if (userExt!=null){
						LOG.info("UserExt. { uid:" + userExt.getUid() + "}" );
						up = new UserPrincipal( userExt.getUid(), userExt.getName(), userExt.getUid());
					}else{
						up = new UserPrincipal( userId, trueName, userId);
					}
					
					authenticationService.commitLogin(up, request);
					request.getSession().setAttribute(Attributes.UMT_ACCESS_TOKEN, accessToken);
				}
			}catch(OAuthProblemException e){
				if (request.getRequestURI().indexOf("mobile")!=-1){
					display401(request, response);
				}else{
					authenticationService.invalidateSession(request);
					oauthService.handleException(e, request, response, true);
				}
				return false;
			}
		}
		return true;
	}
	
	public String getCurrentUid(HttpServletRequest request) {
		return VWBSession.getCurrentUid(request);
	}
	
	public int getCurrentTid() {
		return VWBContext.getCurrentTid();
	}
	
	private String getAccessToken(HttpServletRequest request){
		String accessToken = request.getParameter(PARAM_ACCESS_TOKEN);
		if(accessToken!=null){
			return accessToken;
		}
		String auth = request.getHeader("Authorization");
		if(auth!=null && auth.startsWith("Bearer ")){
			return auth.substring(7);
		}
		return null;
	}
	
	private boolean sameAsSessionSaved(HttpServletRequest request,String accessToken){
		HttpSession  session= request.getSession(false);
		if (session!=null){
			return accessToken.equals(session.getAttribute(Attributes.UMT_ACCESS_TOKEN));
		}
		return false;
	}
	private String getUserFromInternalOAuth(String accessToken)
			throws OAuthProblemException {
		OAuthAccessor accessor= oauthService.getAccessor(accessToken);
		String userId = (String) accessor.getProperty("user");
		return userId;
	}
	private UserInfo getUserInfoFromUmt(String accessToken) {
		try{
			AccessToken token = authcodeService.umtAccessTokenValidate(accessToken);
			return token.getUserInfo();
		} catch (cn.vlabs.umt.oauth.common.exception.OAuthProblemException e) {
			return null;
		}
	}
	private void display401(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		request.setAttribute("title", "链接已失效。");
		request.getRequestDispatcher("/jsp/mobile/mobile_401.jsp").forward(request, response);
	}
}
