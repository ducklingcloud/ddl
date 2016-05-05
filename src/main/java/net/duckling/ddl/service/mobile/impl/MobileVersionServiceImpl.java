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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import net.duckling.ddl.service.mobile.MobileVersionService;
import net.duckling.ddl.service.mobile.MobileVersion;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MobileVersionServiceImpl implements MobileVersionService{
	private static Logger LOG=Logger.getLogger(MobileVersionServiceImpl.class);
	@Resource(name="androidNoticeHandler")
	private NoticeHandler androidNoitceHandler;
	@Resource(name="iosNoticeHandler")
	private NoticeHandler iosNoticeHandler;
	@Autowired
	private MobileVersionDAO mobileVersionDAO;
	private MobileNoticeThread thread;
	@Override
	public void create(MobileVersion version) {
		mobileVersionDAO.create(version);
	}
    @PreDestroy
	public void destroy(){
		thread.interrupt();
		thread.exit();
		try {
			thread.join();
		} catch (InterruptedException e) {
			LOG.error("exit mobile notice handle thread failed.", e);
		}
	}

	@Override
	public MobileVersion getLatestVersionByType(String type) {
		return mobileVersionDAO.getLatestVersionByType(type);
	}

	@PostConstruct
	public void init(){
		thread = new MobileNoticeThread();
		thread.addNoticeHandler(iosNoticeHandler);
		thread.addNoticeHandler(androidNoitceHandler);
		thread.start();
	}

}
