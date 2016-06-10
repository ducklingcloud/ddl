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
package cn.vlabs.duckling.aone.content.dao;

import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.tobedelete.File;
import net.duckling.ddl.service.tobedelete.FileDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

public class FileDAOTest extends BaseTest {

	private FileDAOImpl fileDAO;
	
	@Before
	public void setUp() {
		fileDAO = f.getBean(FileDAOImpl.class);
	}

	@After
	public void tearDown() {
		fileDAO = null;
	}
	
	@Test
	@Ignore
	public void testCreate(){
		File file = new File();
		file.setClbId(0);
		file.setStatus(LynxConstants.STATUS_AVAILABLE);
		file.setFid(0);
		file.setTid(2012);
		file.setTitle("test case for file title");
		file.setCreateTime(new Date());
		file.setCreator("test case");
		file.setLastEditor("test case");
		file.setLastEditTime(new Date());
		file.setLastVersion(LynxConstants.INITIAL_VERSION);
		fileDAO.create(file);
	}
	
	private int fid = 1;
	private int tid = 2012;
	
	@Test
	@Ignore
	public void testDelete(){
		fileDAO.delete(fid, tid);
		File deleteFile = fileDAO.getFile(fid, tid);
		printSingleFile(deleteFile);
	}
	
	/*@Test
	@Ignore
	public void testRecover(){
		fileDAO.recover(fid, tid);
		File recoverFile = fileDAO.getFile(fid, tid);
		printSingleFile(recoverFile);
	}*/
	
	@Test
	@Ignore
	public void testUpdate(){
		File file = fileDAO.getFile(fid, tid);
		file.setClbId(1);
		file.setTitle("test update change title");
		file.setLastEditor("test case change");
		file.setLastEditTime(new Date());
		file.setLastVersion(1);
		fileDAO.update(fid, tid, file);
		File updateFile = fileDAO.getFile(fid, tid);
		printSingleFile(updateFile);
	}
	
	@Test
	@Ignore
	public void testGet(){
		File file = fileDAO.getFile(fid, tid);
		printSingleFile(file);
	}
	
	@Test
	@Ignore
	public void testGetFilesOfTeam(){
		List<File> list = fileDAO.getFilesOfTeam(tid, 0, 10);
		printListFile(list);
	}
	
	private void printSingleFile(File file){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+file.getId()+",");
		sb.append("clbId:"+file.getClbId()+",");
		sb.append("status:"+file.getStatus()+",");
		sb.append("fid:"+file.getFid()+",");
		sb.append("tid:"+file.getTid()+",");
		sb.append("title:"+file.getTitle()+",");
		sb.append("createTime:"+file.getCreateTime()+",");
		sb.append("creator:"+file.getCreator()+",");
		sb.append("lastEditor:"+file.getLastEditor()+",");
		sb.append("lastEditTime:"+file.getLastEditTime()+",");
		sb.append("lastVersion:"+file.getLastVersion());
		System.out.println(sb.toString());
	}
	
	private void printListFile(List<File> list){
		for(File file : list){
			printSingleFile(file);
		}
	}
}
