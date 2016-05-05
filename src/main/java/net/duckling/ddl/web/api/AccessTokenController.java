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
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.authenticate.AuthenticationService;
import net.duckling.ddl.service.oauth.OAuthConsumerExt;
import net.duckling.ddl.service.oauth.impl.OAuthServiceImpl;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.vlabs.commons.principal.UserPrincipal;

/**
 * 获取AcessToken的地方
 * @date 2011-8-29
 * @author xiejj@cnic.cn
 */
@Controller
@RequestMapping("/oauth/access_token")
public class AccessTokenController {
	private static final Logger LOG = Logger.getLogger(AccessTokenController.class);
	@Autowired
	private AuthenticationService authenicateService;
	@Autowired
	private OAuthServiceImpl oauthService;
	@RequestMapping
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
		try{
            OAuthMessage requestMessage = OAuthServlet.getMessage(request, null);
            
            OAuthAccessor accessor;
            // if is xauth, verify user's credential.
            if (isXAuth(requestMessage)){
            	OAuthConsumerExt consumer = oauthService.getConsumer(requestMessage);
            	if (consumer.isUseXAuth()&&consumer.isEnabled()){
	            	accessor = new OAuthAccessor(consumer);
	            	UserPrincipal user = verifyUserCredential(requestMessage);
	            	if (user!=null){
	            		String userId=requestMessage.getParameter(OAuth.XAUTH_USERANME);
	            		oauthService.markAsAuthorized(accessor, userId, user.getDisplayName());
	            	}else{
	            		throw new OAuthProblemException(OAuth.Problems.XAUTH_USER_VERIFY_FAILED);
	            	}
            	}else{
            		throw new OAuthProblemException(OAuth.Problems.PERMISSION_DENIED);
            	}
            }else{
            	accessor = oauthService.getAccessor(requestMessage);
            }
            
            oauthService.validateMessage(requestMessage, accessor);
        	// make sure token is authorized
        	if (!Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
        		OAuthProblemException problem = new OAuthProblemException("permission_denied");
        		throw problem;
        	}
            
            // generate access token and secret
        	oauthService.generateAccessToken(accessor);
            
            response.setContentType("text/plain");
            OutputStream out = response.getOutputStream();
            OAuth.formEncode(OAuth.newList("oauth_token", accessor.accessToken,
                                           "oauth_token_secret", accessor.tokenSecret,
                                           "screen_name",(String)accessor.getProperty("screenName")),
                             out);
            out.close();
            
        } catch (Exception e){
        	LOG.info("手机oauth认证错误",e);
        	oauthService.handleException(e, request, response, true);
        }
    }
    private UserPrincipal verifyUserCredential(OAuthMessage requestMessage) throws OAuthProblemException, IOException{
    	requestMessage.requireParameters(OAuth.XAUTH_USERANME, OAuth.XAUTH_PASSWORD);
    	String userName=requestMessage.getParameter(OAuth.XAUTH_USERANME);
    	String password=requestMessage.getParameter(OAuth.XAUTH_PASSWORD);
    	return authenicateService.login(userName, password);
    }
    
    private boolean isXAuth(OAuthMessage requestMessage) throws IOException{
    	return (OAuth.XAUTH_MODE_CLIENT.equals(requestMessage.getParameter(OAuth.XAUTH_MODE)));
    }
}
