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

import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.dao.TagDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class TagDAOTest extends BaseTest {
	private TagDAOImpl tagDAO;

	@Before
	public void setUp() throws Exception {
		tagDAO = f.getBean(TagDAOImpl.class);
	}

	@After
	public void tearDown() {
		tagDAO = null;
	}
	
	private int tid = 2012;
	private int tgid = 1;
	private int id = 86;
	
	@Test
	@Ignore
	public void testCreate(){
		Tag tag = new Tag();
		tag.setTid(tid);
		tag.setTitle("test case create title");
		tag.setCreator("test case");
		tag.setCount(0);
		tag.setGroupId(tgid);
		tag.setCreateTime(new Date());
		tagDAO.create(tag);
	}
	
	@Test
	@Ignore
	public void testGetTagById(){
		Tag tag = tagDAO.getTagById(id);
		printSingleTag(tag);
	}
	
	@Test
	@Ignore
	public void testUpdate(){
		Tag tag = tagDAO.getTagById(id);
		tag.setTitle("test case change title");
		tagDAO.update(id, tag);
		Tag tagUpdate = tagDAO.getTagById(id);
		printSingleTag(tagUpdate);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		tagDAO.delete(id);
		try{
			tagDAO.getTagById(id);
			System.out.println("failed to delete tag by id="+id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete tag by id="+id);
		}
	}
	
	@Test
	@Ignore
	public void testGetAllTag(){
		List<Tag> list = tagDAO.getAllTag();
		printTagList(list);
	}
	
	@Test
	@Ignore
	public void testGetAllTagInTeam(){
		List<Tag> list = tagDAO.getAllTagInTeam(tid);
		printTagList(list);
	}
	
	@Test
	@Ignore
	public void testGetAllTagInGroup(){
		List<Tag> list = tagDAO.getAllGroupTagsByTeam(tgid);
		printTagList(list);
	}
	
	@Test
	@Ignore
	public void testGetAllTagInTeamAndGroup(){
		List<Tag> list = tagDAO.getAllTagInTeamAndGroup(tid, tgid);
		printTagList(list);
	}
	
	private void printSingleTag(Tag tag){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+tag.getId()+",");
		sb.append("tid:"+tag.getTid()+",");
		sb.append("title:"+tag.getTitle()+",");
		sb.append("creator:"+tag.getCreator()+",");
		sb.append("count:"+tag.getCount()+",");
		sb.append("groupId:"+tag.getGroupId()+",");
		sb.append("createTime:"+tag.getCreateTime());
		System.out.println(sb.toString());
	}
	
	private void printTagList(List<Tag> list){
		for(Tag tag : list)
			printSingleTag(tag);
	}
}
