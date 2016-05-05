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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.dao.BundleItemDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class BundleItemDAOTest extends BaseTest {
	private BundleItemDAOImpl bundleItemDAO;

	@Before
	public void setUp() throws Exception {
		bundleItemDAO = f.getBean(BundleItemDAOImpl.class);
	}

	@After
	public void tearDown() {
		bundleItemDAO = null;
	}
	
	private int bid = 1;
	private int tid = 2012;
	private int id = 472;
	
	@Test
	public void testCreate(){
		BundleItem item = new BundleItem();
		item.setBid(bid);
		item.setTid(tid);
		item.setRid(1);
		item.setSequence(Integer.MAX_VALUE);
		bundleItemDAO.create(item);
	}
	
	@Test
	public void testGetBundleItem(){
		BundleItem item = bundleItemDAO.getBundleItemById(id);
		if(item!=null){
			printBundleItem(item);
		}
	}
	
	@Test
	public void testDeleteBundleItem(){
		bundleItemDAO.getBundleItemById(id);
		bundleItemDAO.delete(id);
		try{
			BundleItem item = bundleItemDAO.getBundleItemById(id);
			System.out.println("delete failed");
			if(item!=null){
				printBundleItem(item);
			}
		}catch(EmptyResultDataAccessException e){
			System.out.println("delete successfully");
		}
	}
	
	@Test
	public void testUpdateBundleItem(){
		BundleItem item = bundleItemDAO.getBundleItemById(id);
		if(item!=null){
			item.setSequence(0);
			bundleItemDAO.update(id, item);
			BundleItem itemUpdate = bundleItemDAO.getBundleItemById(id);
			printBundleItem(itemUpdate);
		}
	}
	
	@Test
	public void testGetBundleItemsByTIDBID(){
		List<BundleItem> list = bundleItemDAO.getBundleItemsByBidTid(bid, tid);
		printBundleItems(list);
	}
	
	@Test
	public void testAddItems2Bundle(){
		Bundle bundle = new Bundle();
		bundle.setBid(bid);
		bundle.setTid(tid);
		BundleItem[] items = createBundleItems();
		bundleItemDAO.addItemsToBundle(bid, tid, items);
	}
	
	@Test
	public void testDeleteAllItemInBundle(){
		bundleItemDAO.deleteAllItemInBundle(tid, bid);
		List<BundleItem> list = bundleItemDAO.getBundleItemsByBidTid(bid, tid);
		if(null == list || list.size()<=0){
			System.out.println("success to delete all items in bundle!");
		}else{
			System.out.println("failed to delete all items in bundle!");
			printBundleItems(list);
		}
	}
	
	@Test
	public void testDeleteItemsWithoutIds(){
		bundleItemDAO.deleteItemsWithoutIds(bid, tid, new int[]{1});
	}
	
	@Test
	public void testDeleteItemsWithIds(){
		int[] ids = new int[]{7,8,9,10,11};
		bundleItemDAO.deleteItemsWithIds(ids);
	}
	
	@Test
	public void testReorderBundleItems(){
		Map<Integer, Integer> orderMap = new HashMap<Integer, Integer>();
		orderMap.put(15, 2);
		orderMap.put(16, 0);
		orderMap.put(17, 1);
		bundleItemDAO.reorderBundleItems(bid, tid, orderMap);
	}
	
	public void printBundleItem(BundleItem item){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+item.getId()+",");
		sb.append("bid:"+item.getBid()+",");
		sb.append("tid:"+item.getTid()+",");
		sb.append("rid:"+item.getRid()+",");
		sb.append("sequence:"+item.getSequence()+"\n");
		System.out.println(sb.toString());
	}
	
	private void printBundleItems(List<BundleItem> list){
		StringBuilder sb = new StringBuilder();
		for(BundleItem item : list){
			sb.append("id:"+item.getId()+",");
			sb.append("tid:"+item.getTid()+",");
			sb.append("bid:"+item.getBid()+",");
			sb.append("rid:"+item.getRid()+",");
			sb.append("sequence:"+item.getSequence()+"\n");
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	private BundleItem[] createBundleItems(){
		BundleItem[] items = new BundleItem[5];
		for(int i=0;i<5;i++){
			BundleItem item = new BundleItem();
			item.setBid(bid);
			item.setTid(tid);
			item.setRid(i);
			item.setSequence(i);
			items[i]=item;
		}
		return items;
	}
	
//	private BundleItem[] createBundleItemsWithIds(){
//		BundleItem[] items = new BundleItem[5];
//		for(int i=0;i<5;i++){
//			BundleItem item = new BundleItem();
//			item.setId(i+483);
//			item.setBid(0);
//			item.setTid(tid);
//			item.setRid(i);
//			item.setSequence(i);
//			items[i]=item;
//		}
//		return items;
//	}
}
