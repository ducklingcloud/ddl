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

import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.file.dao.FileVersionDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class FileVersionDAOTest extends BaseTest {
	private FileVersionDAOImpl fileVersionDAO;

	@Before
	public void setUp() throws Exception {
		fileVersionDAO = f.getBean(FileVersionDAOImpl.class);
	}

	@After
	public void tearDown() {
		fileVersionDAO = null;
	}
	
	@Test
	@Ignore
	public void testCreate(){
		FileVersion version = new FileVersion();
		version.setRid(1);
		version.setTid(2012);
		version.setVersion(1);
		version.setClbId(0);
		version.setSize(1024);
		version.setTitle("test case set title");
		version.setEditor("test case");
		version.setEditTime(new Date());
		fileVersionDAO.create(version);
	}
	
	private int id = 827;
	private int tid = 2012;
	private int fid = 1;
	private int version = 1;
	
	@Test
	@Ignore
	public void testGetFileVersionById(){
		FileVersion fileVersion = fileVersionDAO.getFileVersionById(id);
		printSingleFileVersion(fileVersion);
	}
	
	@Test
	@Ignore
	public void testGetFileVersion(){
		FileVersion fileVersion = fileVersionDAO.getFileVersion(fid, tid, version);
		printSingleFileVersion(fileVersion);
	}
	
	@Test
	@Ignore
	public void testGetLatestFileVersion(){
		FileVersion fileVersion = fileVersionDAO.getLatestFileVersion(fid, tid);
		printSingleFileVersion(fileVersion);
	}
	
	@Test
	@Ignore
	public void testUpdate(){
		FileVersion fileVersion = fileVersionDAO.getFileVersion(fid, tid, version);
		fileVersion.setClbId(1);
		fileVersion.setTitle("test change title");
		fileVersion.setEditTime(new Date());
		fileVersionDAO.update(fileVersion.getId(), fileVersion);
		FileVersion fileVersionUpdate = fileVersionDAO.getFileVersion(fid, tid, version);
		printSingleFileVersion(fileVersionUpdate);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		fileVersionDAO.delete(id);
		try{
			fileVersionDAO.getFileVersionById(id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete file version with id="+id);
		}
	}
	
	@Test
	@Ignore
	public void testDelete2(){
		fileVersionDAO.delete(fid, tid, version);
		try{
			fileVersionDAO.getFileVersion(fid, tid, version);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete file version with fid="+fid+
					", tid="+tid+", version="+version);
		}
	}
	
	@Test
	@Ignore
	public void testDeleteAllFileVersion(){
		fileVersionDAO.deleteAllFileVersion(fid, tid);
		try{
			fileVersionDAO.getFileVersion(fid, tid, 1);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete file version with fid="+fid+
					", tid="+tid+", version="+1);
		}
	}
	
	private void printSingleFileVersion(FileVersion fileVersion){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+fileVersion.getId()+",");
		sb.append("rid:"+fileVersion.getRid()+",");
		sb.append("tid:"+fileVersion.getTid()+",");
		sb.append("version:"+fileVersion.getVersion()+",");
		sb.append("clbId:"+fileVersion.getClbId()+",");
		sb.append("size:"+fileVersion.getSize()+",");
		sb.append("title:"+fileVersion.getTitle()+",");
		sb.append("editor:"+fileVersion.getEditor()+",");
		sb.append("editTime:"+fileVersion.getEditTime());
		System.out.println(sb.toString());
	}
}
