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

import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.impl.ResourceServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;
import cn.vlabs.duckling.aone.content.base.TestHelper;

public class ResourceServiceTest extends BaseTest {
	private IResourceService resourceService;

	@Before
	public void setUp() throws Exception {
		resourceService = f.getBean(ResourceServiceImpl.class);
	}

	@After
	public void tearDown() throws Exception {
		resourceService = null;
	}
	
	private int tid = 2012;
	
	@Test
	@Ignore
	public void testGetPages(){
//		Resource[] list = resourceService.getPages(tid, 0, 10);
//		printList(list);
	}
	
	@Test
	@Ignore
	public void testGetFiles(){
//		Resource[] list = resourceService.getFiles(tid, 0, 10);
//		printList(list);
	}
	
	@Test
	@Ignore
	public void testGetBundles(){
//		Resource[] list = resourceService.getBundles(tid, 0, 10);
//		printList(list);
	}
	
	@Test
	@Ignore
	public void testGetResource(){
		int id = 4;
		Resource resource = resourceService.getResource(id, tid);
		printSingle(resource);
	}
	
	private void printSingle(Object obj){
		try {
			String bundleStr = TestHelper.convert2String(obj);
			System.out.println(bundleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void printList(Resource[] list){
//		for(Resource resource : list)
//			printSingle(resource);
//	}
}
