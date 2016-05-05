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

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.service.resource.dao.TagItemDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class TagItemDAOTest extends BaseTest {
	private TagItemDAOImpl tagItemDAO;

	@Before
	public void setUp() throws Exception {
		tagItemDAO = f.getBean(TagItemDAOImpl.class);
	}

	@After
	public void tearDown() {
		tagItemDAO = null;
	}
	
	private int tid = 2012;
	private int tgid = 1;
	private int id = 200;
	private int[] tgids = new int[]{1,2,3};
	
	@Test
	@Ignore
	public void testCreate(){
		TagItem tagItem = new TagItem();
		tagItem.setTid(tid);
		tagItem.setTgid(tgid);
		tagItemDAO.create(tagItem);
	}
	
	@Test
	@Ignore
	public void testGetTagItemById(){
		TagItem tagItem = tagItemDAO.getTagItemById(id);
		printSingleTagItem(tagItem);
	}
	
	@Test
	@Ignore
	public void testUpdate(){
		TagItem tagItem = tagItemDAO.getTagItemById(id);
		tagItemDAO.update(id, tagItem);
		TagItem tagItemUpdate = tagItemDAO.getTagItemById(id);
		printSingleTagItem(tagItemUpdate);
	}
	
	@Test
	@Ignore
	public void testBatchUpdateWithTag(){
		TagItem[] items = createTagItems();
		List<Long> rids = new ArrayList<Long>();
		for(int i=0; i<items.length; i++){
			rids.add((long)items[i].getRid());
		}
		tagItemDAO.batchUpdateWithTag(tid,1,rids);
	}
	
	@Test
	@Ignore
	public void testGetItemsInTags(){
		List<TagItem> list = tagItemDAO.getItemsInTags(tgids, 0, 10);
		for(TagItem item:list)
			printSingleTagItem(item);
	}
	
	@Test
	@Ignore
	public void testDelete(){
		tagItemDAO.delete(id);
		try{
			tagItemDAO.getTagItemById(id);
			System.out.println("failed to delete tag item by id="+id);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete tag item by id="+id);
		}
	}
	
	@Test
	@Ignore
	public void testDeleteByTagId(){
		int[] tgidsTemp = new int[]{tgid};
		tagItemDAO.deleteByTagId(tgid);
		List<TagItem> list = tagItemDAO.getItemsInTags(tgidsTemp, 0, 10);
		if(null == list || list.size()<=0){
			System.out.println("success to delete tag item by tgid="+tgid);
			return;
		}
		System.out.println("failed to delete tag item by tgid="+tgid);
	}
	
	private void printSingleTagItem(TagItem tagItem){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+tagItem.getId()+",");
		sb.append("tid:"+tagItem.getTid()+",");
		sb.append("tgid:"+tagItem.getTgid()+",");
		System.out.println(sb.toString());
	}
	
	private TagItem[] createTagItems(){
		TagItem[] items = new TagItem[5];
		for(int i=0;i<5;i++){
			TagItem item = new TagItem();
			item.setTid(tid);
			item.setTgid(tgid);
			items[i]=item;
		}
		return items;
	}
}
