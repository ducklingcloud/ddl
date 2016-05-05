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

import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.FileVersionService;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;
import cn.vlabs.duckling.aone.content.base.TestHelper;

public class FileServiceTest extends BaseTest {
	private FileVersionService fileVersionService;

	@Before
	public void setUp() throws Exception {
		fileVersionService = (FileVersionService)f.getBean("fileVersionService");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private int tid = 2012;
	private int fid = 3;
	
	@Test
	@Ignore
	public void testCreate(){
//		File file = fileService.create(tid, "test case", 0, "test case create file", 0);
//		printSingle(file);
	}
	
	@Test
	@Ignore
	public void testCreateFileVersion(){
//		FileVersion fileVersion = fileService.createFileVersion(fid, tid, "test case", 
//				0, "test case create file Version", 0);
//		printSingle(fileVersion);
	}
	
	@Test
	@Ignore
	public void testUpdateFile(){
////		File file = fileService.getFile(fid, tid);
//		file.setClbId(1);
//		file.setLastEditor("test case update");
//		file.setLastEditTime(new Date());
////		fileService.updateFile(fid, tid, file);
//		File fileUpdate = fileService.getFile(fid, tid);
//		printSingle(fileUpdate);
	}
	
	
	/*@Test
	@Ignore
	public void testRecover(){
		fileService.recover(fid, tid);
		File file = fileService.getFile(fid, tid);
		printSingle(file);
	}*/
	
	@Test
	@Ignore
	public void testGetFileVersion(){
		FileVersion fileVersion = fileVersionService.getFileVersion(fid, tid, 1);
		printSingle(fileVersion);
	}
	
	@Test
	@Ignore
	public void testGetLastestFileVersion(){
		FileVersion fileVersion = fileVersionService.getLatestFileVersion(fid, tid);
		printSingle(fileVersion);
	}
	
	private void printSingle(Object obj){
		try {
			String bundleStr = TestHelper.convert2String(obj);
			System.out.println(bundleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
