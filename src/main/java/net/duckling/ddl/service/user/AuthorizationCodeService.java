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
package net.duckling.ddl.service.user;

import cn.vlabs.umt.oauth.AccessToken;
import cn.vlabs.umt.oauth.common.exception.OAuthProblemException;
import net.duckling.ddl.web.bean.AuthorizationCode;

/**
 * 校验code的管理
 * @author zhonghui
 *
 */
public interface AuthorizationCodeService {
	void create(AuthorizationCode code);
	AuthorizationCode getCode(String code);
	/**
	 * 检验access_token并获取用户信息
	 * @param accessToken
	 * @return
	 * @throws OAuthProblemException
	 */
	AccessToken umtAccessTokenValidate(String accessToken) throws OAuthProblemException;
	/**
	 * 使用RefreshToken获取AccessToken
	 * @param refreshtoken
	 * @return
	 * @throws OAuthProblemException
	 */
	AccessToken umtRefreshToken(String refreshtoken) throws OAuthProblemException ;
	
	/**
	 * 通过用户名和密码获取accessToken
	 * @param userName
	 * @param password
	 * @return
	 * @throws OAuthProblemException
	 */
	AccessToken umtPasswordAccessToken(String userName,String password) throws OAuthProblemException;
}
