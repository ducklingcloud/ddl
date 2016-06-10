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

import java.util.List;

import net.duckling.ddl.service.navbar.NavbarItem;
import net.duckling.ddl.service.navbar.dao.NavbarItemDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class NavbarItemDAOTest extends BaseTest {
	private NavbarItemDAOImpl navbarDAO;

	@Before
	public void setUp() throws Exception {
		navbarDAO = f.getBean(NavbarItemDAOImpl.class);
	}

	@After
	public void tearDown() {
		navbarDAO = null;
	}
	
	private int tid = 2012;
	private String uid = "test case change";
	private int id = 1;
	
	@Test
	public void testCreate(){
		NavbarItem navbarItem = new NavbarItem();
		navbarItem.setTid(tid);
		navbarItem.setUid(uid);
		navbarItem.setSequence(0);
		navbarItem.setTitle("title");
		navbarDAO.create(navbarItem);
	}
	
	@Test
	public void testGetNavbarItemById(){
		NavbarItem navbarItem = new NavbarItem();
		navbarItem.setTid(tid);
		navbarItem.setUid(uid);
		navbarItem.setSequence(0);
		navbarItem.setTitle("title");
		int id =navbarDAO.create(navbarItem);
		navbarItem = navbarDAO.getNavbarItemById(id);
		printSingleNavbarItem(navbarItem);
	}
	
	@Test
	public void testUpdate(){
		NavbarItem navbarItem = new NavbarItem();
		navbarItem.setTid(tid);
		navbarItem.setUid(uid);
		navbarItem.setSequence(0);
		navbarItem.setTitle("title");
		int id =navbarDAO.create(navbarItem);
		navbarItem = navbarDAO.getNavbarItemById(id);
		navbarItem.setUid("test case change");
		NavbarItem navbarItemUpdate = navbarDAO.getNavbarItemById(id);
		printSingleNavbarItem(navbarItemUpdate);
	}
	
	@Test
	public void testGetAllNavbarItemByUidTid(){
		List<NavbarItem> list = navbarDAO.getAllNavbarItemByUidTid(uid, tid);
		for(NavbarItem item : list)
			printSingleNavbarItem(item);
	}
	
	@Test
	public void testDeleteById(){
		navbarDAO.delete(id);
		try{
			navbarDAO.getNavbarItemById(id);
			System.out.println("failed to delete navbar item by id = "+id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete navbar item by id = "+id);
		}
	}
	
	private void printSingleNavbarItem(NavbarItem item){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+item.getId()+",");
		sb.append("uid:"+item.getUid()+",");
		sb.append("tid:"+item.getTid()+",");
		sb.append("sequence:"+item.getSequence());
		System.out.println(sb.toString());
	}
}
