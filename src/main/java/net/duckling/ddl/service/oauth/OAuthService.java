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
package net.duckling.ddl.service.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

public interface OAuthService {

	void validateMessage(OAuthMessage requestMessage, OAuthAccessor accessor);

	void markAsAuthorized(OAuthAccessor accessor, String userId,
			String screenName);

	void handleException(Exception e, HttpServletRequest request,
			HttpServletResponse response, boolean sendBody) throws IOException,
			ServletException;

	OAuthConsumerExt getConsumer(OAuthMessage requestMessage)
			throws IOException, OAuthProblemException;

	OAuthAccessor getAccessor(String consumer_token)
			throws OAuthProblemException;

	OAuthAccessor getAccessor(OAuthMessage requestMessage) throws IOException,
			OAuthProblemException;

	void generateRequestToken(OAuthAccessor accessor);

	void generateAccessToken(OAuthAccessor accessor);

}
