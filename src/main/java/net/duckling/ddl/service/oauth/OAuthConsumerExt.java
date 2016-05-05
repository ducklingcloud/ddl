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

import net.oauth.OAuthConsumer;

/**
 * 扩展OAuthConsumer
 * @date 2011-8-29
 * @author xiejj@cnic.cn
 */
public class OAuthConsumerExt extends OAuthConsumer {
	private static final long serialVersionUID = -350707016188146128L;
	public OAuthConsumerExt(String callbackURL, String consumerKey,
            String consumerSecret) {
		super(callbackURL, consumerKey, consumerSecret, null);
		enabled=true;
	}

	private boolean enabled;
	public boolean isEnabled(){
		return this.enabled;
	}
	public void setEnable(boolean enable){
		this.enabled=enable;
	}
	public boolean isUseXAuth() {
		return useXAuth;
	}
	public void setUseXAuth(boolean useXAuth) {
		this.useXAuth = useXAuth;
	}

	private boolean useXAuth;
}
