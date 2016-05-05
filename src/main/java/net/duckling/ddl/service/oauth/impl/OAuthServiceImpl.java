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

package net.duckling.ddl.service.oauth.impl;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.duckling.ddl.service.oauth.OAuthConsumerExt;
import net.duckling.ddl.service.oauth.OAuthService;
import net.duckling.ddl.service.timer.JobTask;
import net.duckling.ddl.service.timer.TimerService;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * OAuth相关的服务
 * 
 * @date 2011-8-29
 * @author xiejj@cnic.cn
 */
public class OAuthServiceImpl implements OAuthService {
	private class CleanTask implements JobTask {
		private int timeToLive;

		public CleanTask(int timeToLive) {
			this.timeToLive = timeToLive;
		}

		public void execute(Date scheduledDate) {
			Date deadLine = DateUtils.addMinutes(scheduledDate, -timeToLive);
			accessorDAO.removetTimeOut(deadLine);
		}
	}

	private AccessorDAO accessorDAO;
	private ConsumerDAO consumerDAO;

	private CleanTask task;
	private TimerService timerService;

	public void destroy() {
		timerService.removeTask("OAuthClean");
	}

	@Override
	public void generateAccessToken(OAuthAccessor accessor) {

		// generate oauth_token and oauth_secret
		String consumerKey = (String) accessor.consumer.getProperty("name");
		// generate token and secret based on consumer_key

		// for now use md5 of name + current time as token
		String token_data = consumerKey + System.nanoTime();
		String token = DigestUtils.md5Hex(token_data);

		String oldRequestToken = accessor.requestToken;
		// first remove the accessor from cache
		accessor.requestToken = null;
		accessor.accessToken = token;

		AccessorPo po;
		if (oldRequestToken != null) {
			po = accessorDAO.getAccessor(oldRequestToken);
			po.copyDateFrom(accessor);
			accessorDAO.updateAccessor(po);
		} else {
			po = new AccessorPo();
			po.copyDateFrom(accessor);
			accessorDAO.createAccessor(po);
		}
	}

	@Override
	public void generateRequestToken(OAuthAccessor accessor) {

		// generate oauth_token and oauth_secret
		String consumerKey = (String) accessor.consumer.getProperty("name");
		// generate token and secret based on consumer_key

		// for now use md5 of name + current time as token
		String tokenData = consumerKey + System.nanoTime();
		String token = DigestUtils.md5Hex(tokenData);
		// for now use md5 of name + current time + token as secret
		String secretData = consumerKey + System.nanoTime() + token;
		String secret = DigestUtils.md5Hex(secretData);

		accessor.requestToken = token;
		accessor.tokenSecret = secret;
		accessor.accessToken = null;

		AccessorPo accessorPo = new AccessorPo();
		accessorPo.copyDateFrom(accessor);
		// add to the local cache
		accessorDAO.createAccessor(accessorPo);
	}

	/**
	 * Get the access token and token secret for the given oauth_token.
	 */
	@Override
	public OAuthAccessor getAccessor(OAuthMessage requestMessage)
			throws IOException, OAuthProblemException {
		// try to load from local cache if not throw exception
		String consumerToken = requestMessage.getToken();
		return getAccessor(consumerToken);
	}

	@Override
	public OAuthAccessor getAccessor(String consumer_token)
			throws OAuthProblemException {
		OAuthAccessor accessor = null;
		AccessorPo accessorPo = accessorDAO.getAccessor(consumer_token);
		if (accessorPo != null) {
			OAuthConsumer consumer = consumerDAO.getConsumer(accessorPo
					.getConsumerKey());
			accessor = new OAuthAccessor(consumer);
			accessorPo.copyDateTo(accessor);
		}

		if (accessor == null) {
			OAuthProblemException problem = new OAuthProblemException(
					"token_expired");
			throw problem;
		}

		return accessor;
	}

	@Override
	public OAuthConsumerExt getConsumer(OAuthMessage requestMessage)
			throws IOException, OAuthProblemException {

		OAuthConsumerExt consumer = null;
		// try to load from local cache if not throw exception
		String consumerKey = requestMessage.getConsumerKey();

		consumer = consumerDAO.getConsumer(consumerKey);

		if (consumer == null || !consumer.isEnabled()) {
			OAuthProblemException problem = new OAuthProblemException(
					"token_rejected");
			throw problem;
		}

		return consumer;
	}

	@Override
	public void handleException(Exception e, HttpServletRequest request,
			HttpServletResponse response, boolean sendBody) throws IOException,
			ServletException {
		String realm = (request.isSecure()) ? "https://" : "http://";
		realm += request.getLocalName();
		OAuthServlet.handleException(response, e, realm, sendBody);
	}

	public void init() {
		timerService.addMinutelyTask("OAuthClean", task);
	}

	@Override
	public void markAsAuthorized(OAuthAccessor accessor, String userId,
			String screenName) {
		accessor.setProperty("user", userId);
		accessor.setProperty("screenName", screenName);
		accessor.setProperty("authorized", Boolean.TRUE);
		if (accessor.requestToken != null) {
			AccessorPo po = accessorDAO.getAccessor(accessor.requestToken);
			po.copyDateFrom(accessor);
			accessorDAO.updateAccessor(po);
		}
	}

	public void setAccessorDAO(AccessorDAO accessorDAO) {
		this.accessorDAO = accessorDAO;
	}

	public void setConsumerDAO(ConsumerDAO consumerDAO) {
		this.consumerDAO = consumerDAO;
	}

	public void setTimerService(TimerService timerService) {
		this.timerService = timerService;
	}

	public void setTimeToLive(int timeToLive) {
		task = new CleanTask(timeToLive);
	}

	@Override
	public void validateMessage(OAuthMessage requestMessage,
			OAuthAccessor accessor) {
		// VALIDATOR.validateMessage(requestMessage, accessor);
	}

}
