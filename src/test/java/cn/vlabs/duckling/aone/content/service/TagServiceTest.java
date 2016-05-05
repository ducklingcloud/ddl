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

import java.util.Date;
import java.util.List;

import net.duckling.ddl.service.resource.ITagService;
import net.duckling.ddl.service.resource.Tag;
import net.duckling.ddl.service.resource.TagGroup;
import net.duckling.ddl.service.resource.TagGroupRender;
import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.service.resource.impl.TagServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;
import cn.vlabs.duckling.aone.content.base.TestHelper;

public class TagServiceTest extends BaseTest {
	private ITagService tagService;

	@Before
	public void setUp() throws Exception {
		tagService = f.getBean(ITagService.class);
	}

	@After
	public void tearDown() throws Exception {
		tagService = null;
	}
	
	private int tid = 2012;
	private int tgid = 89;
	
	@Test
	public void testCreateTag(){
		Tag tag = new Tag();
		tag.setTid(tid);
		tag.setTitle("title from tagService");
		tag.setGroupId(0);
		tag.setCreator("tagService test case");
		tag.setCreateTime(new Date());
		tag.setCount(0);
		tagService.createTag(tag);
	}
	
	@Test
	public void testGetTag(){
		Tag tag = tagService.getTag(tgid);
		printSingle(tag);
	}
	
	@Test
	public void testUpdateTag(){
		Tag tag = new Tag();
		tag.setTid(tid);
		tag.setTitle("title from tagService");
		tag.setGroupId(0);
		tag.setCreator("tagService test case");
		tag.setCreateTime(new Date());
		tag.setCount(0);
		int tgid =tagService.createTag(tag);
		tag = tagService.getTag(tgid);
		tag.setGroupId(1);
		tag.setTitle("test case update tag");
		tag.setCount(1);
		tagService.updateTag(tag);
		Tag tagUpdate = tagService.getTag(tgid);
		printSingle(tagUpdate);
	}
	
	@Test
	public void testDeleteTag(){
		tagService.deleteTag(tgid);
		try{
			tagService.getTag(tgid);
			System.out.println("failed to delete tag by tgid = "+tgid);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete tag by tgid = "+tgid);
		}
	}
	
//	@Test
//	public void testAddItems(){
//		TagItem[] items = createTagItems();
//		int[] rids = new int[items.length];
//		for(int i=0; i<items.length; i++){
//			rids[i]=items[i].getRid();
//		}
//		tagService.addItems(tid,1,rids);
//		List<TagItem> list = tagService.getTagItems(tgid);
//		printList(list);
//	}
	
	private int[] tgids = new int[]{88,89};
	
	@Test
	public void testGetTagItems2(){
		List<TagItem> list = tagService.getTagItems(tgids);
		printList(list);
	}
	
	@Test
	public void testGetTagItems3(){
		List<TagItem> list = tagService.getTagItems(tgids, 0, 3);
		printList(list);
	}
	
	@Test
	public void testCreateTagGroup(){
		TagGroup tagGroup = new TagGroup();
		tagGroup.setTid(tid);
		tagGroup.setTitle("test case");
		tagGroup.setCreator("test case");
		tagGroup.setSequence(0);
		tagService.createTagGroup(tagGroup);
	}
	
	private int tagGroupId = 2;
	
	@Test
	public void testDeleteTagGroup(){
		tagService.deleteTagGroup(tagGroupId);
	}
	
	@Test
	public void testUpdateTagGroup(){
		TagGroup tagGroup = new TagGroup();
		tagGroup.setTid(tid);
		tagGroup.setTitle("update group");
		tagGroup.setCreator("test case");
		tagGroup.setSequence(1);
		tagService.updateTagGroup(tagGroupId, tagGroup);
	}
	
	@Test
	public void testAddTags2Group(){
		tagService.addTags2Group(tagGroupId, tgids);
	}
	
	@Test
	public void testRemoveTagsFromGroup(){
		tagService.removeTagFromGroup(tagGroupId, tgids);
	}
	
	@Test
	public void testGetTagsForTeam(){
		List<Tag> list = tagService.getTagsNotInGroupForTeam(tid);
		printListTag(list);
	}
	
	@Test
	public void testGetTagGroupsForTeam(){
		List<TagGroupRender> list = tagService.getTagGroupsForTeam(tid);
		printListTagGroupRender(list);
	}
	
	private void printSingle(Object obj){
		try {
			String objStr = TestHelper.convert2String(obj);
			System.out.println(objStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void printList(List<TagItem> list){
		for(TagItem item : list)
			printSingle(item);
	}
	
	private void printListTag(List<Tag> list){
		for(Tag item : list)
			printSingle(item);
	}
	
	private void printListTagGroupRender(List<TagGroupRender> list){
		for(TagGroupRender item : list){
			TagGroup tagGroup = item.getGroup();
			printSingle(tagGroup);
			printListTag(item.getTags());
		}
	}
	
	@SuppressWarnings("unused")
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
