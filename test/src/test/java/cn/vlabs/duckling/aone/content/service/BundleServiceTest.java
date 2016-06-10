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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.bundle.Bundle;
import net.duckling.ddl.service.bundle.BundleItem;
import net.duckling.ddl.service.bundle.IBundleService;
import net.duckling.ddl.service.bundle.impl.BundleServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;
import cn.vlabs.duckling.aone.content.base.TestHelper;

public class BundleServiceTest extends BaseTest {
	private IBundleService bundleService;

	@Before
	public void setUp() throws Exception {
		bundleService = f.getBean(BundleServiceImpl.class);
	}

	@After
	public void tearDown() throws Exception{
		bundleService = null;
	}
	
	private int tid = 9;
	private int bid = 1;
	
	@Test
	@Ignore
	public void testCreateBundle(){
		String title = "empty";
		bundleService.createBundle(title, tid, null,null);
	}
	
	@Test
	@Ignore
	public void testGetBundle(){
		Bundle bundle = bundleService.getBundle(bid, tid);
		printSingleBundle(bundle);
	}
	
	@Test
	@Ignore
	public void testUpdateBundle(){
		Bundle bundle = bundleService.getBundle(bid, tid);
		bundle.setTitle("test update bundle via bundleService");
		bundleService.updateBundle(bundle);
		Bundle bundleUpdate = bundleService.getBundle(bid, tid);
		printSingleBundle(bundleUpdate);
	}
	
//	@Test
//	@Ignore
//	public void testDeleteBundle(){
//		bundleService.disbandBundle(bid, tid);
//		Bundle bundle = bundleService.getBundle(bid, tid);
//		printSingleBundle(bundle);
//	}
	
//	private BundleItem[] createBundleItems(){
//		BundleItem[] items = new BundleItem[2];
//		int[] itemids = new int[]{176,172};
//		for(int i=0;i<2;i++){
//			BundleItem item = new BundleItem();
//			item.setBid(bid);
//			item.setTid(tid);
//			item.setRid(itemids[i]);
//			item.setSequence(i);
//			items[i] = item;
//		}
//		return items;
//	}
	
	@Test
	@Ignore
	public void testAddBundleItems(){
//		BundleItem[] items = createBundleItems();
//		bundleService.addBundleItems(bid, tid, items);
	}
	
	@Test
	@Ignore
	public void testGetBundleItems(){
		List<BundleItem> list = bundleService.getBundleItems(bid, tid);
		printBundleItems(list);
	}
	
	@Test
	@Ignore
	public void testRemoveBundleItems(){
		List<BundleItem> list = bundleService.getBundleItems(bid, tid);
		if(null == list || list.size()<=0){
			System.out.println("success to delete bundle items from bundle with id="+bid+", tid="+tid);
			return;
		}else{
			System.out.println("failed to delete bundle items from bundle with id="+bid+", tid="+tid);
			printBundleItems(list);
		}
	}
	
	@Test
	//@Ignore
	public void testReorderBundleItems(){
		Map<Integer, Integer> orderMap = new HashMap<Integer, Integer>();
		orderMap.put(15, 2);
		orderMap.put(16, 0);
		orderMap.put(17, 1);
		bundleService.reorderBundleItems(bid, tid, orderMap);
	}
	
	private void printSingleBundle(Bundle bundle){
		try {
			String bundleStr = TestHelper.convert2String(bundle);
			System.out.println(bundleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void printBundleItems(List<BundleItem> list){
		StringBuilder sb = new StringBuilder();
		for(BundleItem item : list){
			sb.append("id:"+item.getId()+",");
			sb.append("tid:"+item.getTid()+",");
			sb.append("bid:"+item.getBid()+",");
			sb.append("rid:"+item.getRid()+",");
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
}
