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

import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.dao.TagGroupDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class TagGroupDAOTest extends BaseTest {
	private TagGroupDAOImpl tagGroupDAO;

	@Before
	public void setUp() throws Exception {
		tagGroupDAO = f.getBean(TagGroupDAOImpl.class);
	}

	@After
	public void tearDown() {
		tagGroupDAO = null;
	}
	
	private int tid = 2012;
	private int id = 1;
	
	@Test
	@Ignore
	public void testCreate(){
		TagGroup tagGroup = new TagGroup();
		tagGroup.setTid(tid);
		tagGroup.setTitle("test case create title");
		tagGroup.setCreator("test case");
		tagGroup.setSequence(1);
		tagGroupDAO.create(tagGroup);
	}
	
	@Test 
	@Ignore
	public void testGetTagGroupById(){
		TagGroup tagGroup = tagGroupDAO.getTagGroupById(id);
		printSingleTagGroup(tagGroup);
	}
	
	@Test
	@Ignore
	public void testUpdate(){
		TagGroup tagGroup = tagGroupDAO.getTagGroupById(id);
		tagGroup.setTitle("test case change title");
		tagGroupDAO.update(id, tagGroup);
		TagGroup tagGroupUpdate = tagGroupDAO.getTagGroupById(id);
		printSingleTagGroup(tagGroupUpdate);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		tagGroupDAO.delete(id);
		try{
			tagGroupDAO.getTagGroupById(id);
			System.out.println("failed to delete tag group by id="+id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete tag group by id="+id);
		}
	}
	
	@Test
	@Ignore
	public void testGetAllTagInGroup(){
		List<TagGroup> list = tagGroupDAO.getAllTagGroupByTid(tid);
		for(TagGroup tagGroup : list)
			printSingleTagGroup(tagGroup);
	}
	
	private void printSingleTagGroup(TagGroup tagGroup){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+tagGroup.getId()+",");
		sb.append("tid:"+tagGroup.getTid()+",");
		sb.append("title:"+tagGroup.getTitle()+",");
		sb.append("creator:"+tagGroup.getCreator()+",");
		sb.append("sequence:"+tagGroup.getSequence()+",");
		System.out.println(sb.toString());
	}
}
