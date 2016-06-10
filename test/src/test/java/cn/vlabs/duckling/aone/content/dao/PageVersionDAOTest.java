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

import net.duckling.ddl.service.resource.PageVersion;
import net.duckling.ddl.service.resource.dao.PageVersionDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class PageVersionDAOTest extends BaseTest {
	private PageVersionDAOImpl pageVersionDAO;

	@Before
	public void setUp(){
		pageVersionDAO = f.getBean(PageVersionDAOImpl.class);
	}

	@After
	public void tearDown() {
		pageVersionDAO = null;
	}
	
	private int tid = 2012;
	private int pid = 1;
	private int version = 1;
	private int id = 767;
	
	@Test
	@Ignore
	public void testCreate(){
		PageVersion pageVersion = new PageVersion();
		pageVersion.setTid(tid);
		pageVersion.setVersion(version+1);
		pageVersion.setTitle("test case create title");
		pageVersion.setEditor("test case");
		pageVersion.setEditTime(new Date());
		pageVersion.setContent("test case create content");
		pageVersionDAO.create(pageVersion);
	}
	
	@Test
	@Ignore
	public void testUpdate(){
		PageVersion  pageVersion = pageVersionDAO.getPageVersion(pid, version);
		pageVersion.setContent("test case change content");
		pageVersion.setEditor("test case change");
		pageVersion.setEditTime(new Date());
		pageVersionDAO.update(pageVersion.getId(), pageVersion);
		PageVersion pageVersionUpdate = pageVersionDAO.getPageVersion(pid, version);
		printSinglePageVersion(pageVersionUpdate);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		pageVersionDAO.delete(id);
		try{
			pageVersionDAO.getPageVersionById(id);
			System.out.println("failed to delete page version by id = "+id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete page version by id = "+id);
		}
	}
	
	@Test
	@Ignore
	public void testDeleteAllPageVersion(){
		pageVersionDAO.deleteAllPageVersion(pid, tid);
		List<PageVersion> list = pageVersionDAO.getAllPageVersionByTIDRID(tid, pid);
		if(null == list || list.size()<=0){
			System.out.println("Success to delete all page version with pid = "+pid+", tid = "+tid);
			return;
		}
		System.out.println("Failed to delete all page version with pid = "+pid+", tid = "+tid);
	}
	
	@Test
	@Ignore
	public void testGetLatestPageVersion(){
		PageVersion pageVersion = pageVersionDAO.getLatestPageVersion(pid);
		printSinglePageVersion(pageVersion);
	}
	
	@Test
	@Ignore
	public void testGetAllPageVersionByTIDPID(){
		List<PageVersion> list = pageVersionDAO.getAllPageVersionByTIDRID(tid, pid);
		for(PageVersion item : list)
			printSinglePageVersion(item);
	}
	
	private void printSinglePageVersion(PageVersion pageVersion){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+pageVersion.getId()+",");
		sb.append("rid:"+pageVersion.getRid()+",");
		sb.append("tid:"+pageVersion.getTid()+",");
		sb.append("version:"+pageVersion.getVersion()+",");
		sb.append("title:"+pageVersion.getTitle()+",");
		sb.append("editor:"+pageVersion.getEditor()+",");
		sb.append("editTime:"+pageVersion.getEditTime()+",");
		sb.append("content:"+pageVersion.getContent()+",");
		System.out.println(sb.toString());
	}
}
