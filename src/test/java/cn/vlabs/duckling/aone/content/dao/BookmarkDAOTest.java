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

import net.duckling.ddl.service.resource.Starmark;
import net.duckling.ddl.service.resource.dao.StarmarkDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class BookmarkDAOTest extends BaseTest {
	private StarmarkDAOImpl bookmarkDAO;

	@Before
	public void setUp() {
		bookmarkDAO = f.getBean(StarmarkDAOImpl.class);
	}

	@After
	public void tearDown() {
		bookmarkDAO = null;
	}
	
	private int tid = 2012;
	private String uid = "test case";
	private int id = 1;
	
	@Test
	@Ignore
	public void testCreate(){
		Starmark bookmark = new Starmark();
		bookmark.setTid(tid);
		bookmark.setUid(uid);
		bookmark.setCreateTime(new Date());
		bookmarkDAO.create(bookmark);
	}
	
	@Test
	@Ignore
	public void testGetAllBookmark(){
//		List<Starmark> list = bookmarkDAO.getAllStarmark(tid, uid);
//		for(Starmark bookmark : list)
//			printSingleBookmark(bookmark);
	}
	
	@Test
	@Ignore
	public void testGetBookmarkById(){
		Starmark bookmark = bookmarkDAO.getStarmarkById(id);
		printSingleBookmark(bookmark);
	}
	
	@Test
	@Ignore
	public void testDeleteById(){
		try{
			bookmarkDAO.getStarmarkById(id);
			System.out.println("failed to delete bookmark by id="+id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete bookmark by id="+id);
		}
	}
	
	@Test
	@Ignore
	public void testDeleteAllBookmark(){
		bookmarkDAO.deleteAllStarmark(tid, uid);
		try{
			System.out.println("failed to delete bookmark by tid = "+tid+", uid = "+uid);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete bookmark by tid = "+tid+", uid = "+uid);
		}
	}
	
	private void printSingleBookmark(Starmark bookmark){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+bookmark.getId()+",");
		sb.append("tid:"+bookmark.getTid()+",");
		sb.append("uid:"+bookmark.getUid()+",");
		sb.append("createTime:"+bookmark.getCreateTime()+",");
		System.out.println(sb.toString());
	}
}
