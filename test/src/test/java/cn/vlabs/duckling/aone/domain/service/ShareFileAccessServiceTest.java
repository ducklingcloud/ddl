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

package cn.vlabs.duckling.aone.domain.service;

import java.util.Date;

import junit.framework.Assert;

import net.duckling.ddl.service.share.ShareFileAccess;
import net.duckling.ddl.service.share.ShareFileAccessService;
import net.duckling.ddl.service.share.impl.ShareFileAccessServiceImpl;

import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

/**
 * @date 2011-10-11
 * @author clive
 */
public class ShareFileAccessServiceTest extends BaseTest{

	private ShareFileAccess s = new ShareFileAccess();
	private ShareFileAccessService ss ;
	@Before
	public void setUp()  {
		//s.setCreateTime(TimeUtils.parseDateString("2011-09-15 13:00:00"));
		s.setValidOfDays(30);
		ss = f.getBean(ShareFileAccessServiceImpl.class);
	}
	
	@Test
	public void testIsValidRequest()   {
		s.setCreateTime(new Date());
		Assert.assertEquals(true, ss.isValidRequest(s));
		s.setCreateTime(new Date(System.currentTimeMillis()+31*24*60*60*1000));
		Assert.assertEquals(true, ss.isValidRequest(s));
	}

	

}
