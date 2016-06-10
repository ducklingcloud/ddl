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
package cn.vlabs.duckling.aone.content.service;

import java.util.List;

import net.duckling.ddl.service.navbar.INavbarService;
import net.duckling.ddl.service.navbar.NavbarItem;
import net.duckling.ddl.service.navbar.impl.NavbarServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;
import cn.vlabs.duckling.aone.content.base.TestHelper;

public class NavbarServiceTest extends BaseTest {
	private INavbarService navbarService;

	@Before
	public void setUp() throws Exception {
		navbarService = f.getBean(NavbarServiceImpl.class);
	}

	@After
	public void tearDown()  {
		navbarService = null;
	}
	
	private String uid = "test case";
	private int tid = 2012;
	private int id = 1;
	
	
	@Test
	public void testGetNavbarItems(){
		List<NavbarItem> list = navbarService.getNavbarItems(uid, tid);
		for(NavbarItem item : list)
			printSingle(item);
	}
	
	@Test
	public void testDelete(){
		navbarService.delete(id);
	}
	
	private void printSingle(NavbarItem item){
		try {
			String bundleStr = TestHelper.convert2String(item);
			System.out.println(bundleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
