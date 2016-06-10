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
import net.duckling.ddl.service.tobedelete.Page;
import net.duckling.ddl.service.tobedelete.PageDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

public class PageDAOTest extends BaseTest {
	private PageDAOImpl pageDAO;

	@Before
	public void setUp() {
		pageDAO = f.getBean(PageDAOImpl.class);
	}

	@After
	public void tearDown() {
		pageDAO = null;
	}
	
	private int tid = 2012;
	private int pid = 1;
	
	@Test
	@Ignore
	public void testCreate(){
		Page page = new Page();
		page.setTid(tid);
		page.setPid(pid);
		page.setStatus(LynxConstants.STATUS_AVAILABLE);
		page.setTitle("test case create title");
		page.setCreateTime(new Date());
		page.setCreator("test case");
		page.setLastEditor("test case");
		page.setLastEditTime(new Date());
		page.setLastVersion(LynxConstants.INITIAL_VERSION);
		pageDAO.create(page);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		pageDAO.delete(pid, tid);
		Page page = pageDAO.getPage(pid, tid);
		printSinglePage(page);
	}
	
	/*@Test
	@Ignore
	public void testRecover(){
		pageDAO.recover(pid, tid);
		Page page = pageDAO.getPage(pid, tid);
		printSinglePage(page);
	}*/
	
	@Test
	@Ignore
	public void testUpdate(){
		Page page = pageDAO.getPage(pid, tid);
		page.setTitle("test case change title");
		page.setLastEditor("test case change editor");
		page.setLastEditTime(new Date());
		page.setLastVersion(2);
		pageDAO.update(pid, tid, page);
		Page pageUpdate = pageDAO.getPage(pid, tid);
		printSinglePage(pageUpdate);
	}
	
	@Test
	@Ignore
	public void testGetPagesOfTeam(){
		List<Page> list = pageDAO.getPagesOfTeam(tid, 0, 10);
		for(Page page : list){
			printSinglePage(page);
		}
	}
	
	private void printSinglePage(Page page){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+page.getId()+",");
		sb.append("pid:"+page.getPid()+",");
		sb.append("tid:"+page.getTid()+",");
		sb.append("status:"+page.getStatus()+",");
		sb.append("title:"+page.getTitle()+",");
		sb.append("createTime:"+page.getCreateTime()+",");
		sb.append("creator:"+page.getCreator()+",");
		sb.append("lastEditor:"+page.getLastEditor()+",");
		sb.append("lastEditTime:"+page.getLastEditTime()+",");
		sb.append("lastVersion:"+page.getLastVersion());
		System.out.println(sb.toString());
	}
}
