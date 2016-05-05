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

package net.duckling.ddl.service.mobile.impl;

import java.util.List;

import net.duckling.ddl.service.mobile.DeviceTokenService;
import net.duckling.ddl.service.mobile.IphoneDeviceToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * @date 2013-1-22
 * @author zzb
 */
@Service
public class DeviceTokenServiceImpl implements DeviceTokenService {
	@Autowired
	private DeviceTokenDao tokenDao;
	
	public List<IphoneDeviceToken> getAllDeviceToken() {
		return tokenDao.getAllDeviceToken();
	}
	
	public int insertOrUpdateDeviceToken(IphoneDeviceToken deviceToken) {
		IphoneDeviceToken existDeviceToken = tokenDao.getIphoneDeviceToken(deviceToken.getDeviceToken());
		boolean hasChange = false;
		int result = 0;
		if(existDeviceToken == null) {
			result = tokenDao.insertDeviceToken(deviceToken);
			hasChange = true;
		} else {
			String existUid = existDeviceToken.getUid();
			if(!existUid.equals(deviceToken.getUid())) {
				// 切换用户登录了
				hasChange = true;
			}
			// 更新数据库
			result = tokenDao.updateDeviceToken(existDeviceToken);
		}
		if(hasChange) {
//			deviceTokenList = tokenDao.getAllDeviceToken();
		}
		return result;
	}

	@Override
	public IphoneDeviceToken getTokenByUid(String uid) {
		return tokenDao.getIphoneDeviceTokenByUid(uid);
	}

	
}