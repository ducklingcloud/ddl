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

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.duckling.ddl.service.grid.GridItem;
import net.duckling.ddl.service.grid.dao.GridGroupDAOImpl;
import net.duckling.ddl.service.grid.impl.GridGroup;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;

public class GridGroupDAOTest extends BaseTest  {
	
	private GridGroupDAOImpl gdao;
	@Before
	public void setUp() {
		gdao = f.getBean(GridGroupDAOImpl.class);
	}

	@After
	public void tearDown() {
		gdao = null;
	}
	
   private static HashMap<String,GridItem> getMockScoreMap(int tid){
    	HashMap<String,GridItem> map = new HashMap<String,GridItem>();
    	GridItem[] array = new GridItem[]{
			new GridItem(tid,"liji@cnic.cn",3,"DPage",2.0,false),
			new GridItem(tid,"liji@cnic.cn",4,"DPage",2.0,false),
			new GridItem(tid,"liji@cnic.cn",5,"DPage",2.0,false),
			new GridItem(tid,"liji@cnic.cn",6,"DPage",0.85,false),
			new GridItem(tid,"liji@cnic.cn",7,"DPage",0.95,false),
			new GridItem(tid,"liji@cnic.cn",8,"DPage",0.15,false),
			new GridItem(tid,"liji@cnic.cn",8,"DFile",0.15,false),
			new GridItem(tid,"liji@cnic.cn",8,"Bundle",0.15,false)
    	};
    	for(int i=0;i<array.length;i++){
    		map.put(array[i].getRid()+"#"+array[i].getItemType(), array[i]);
    	}
    	return map;
    }
   
   private static HashMap<String,GridItem> getUpdateScoreMap(int tid){
	   	HashMap<String,GridItem> map = new HashMap<String,GridItem>();
	   	GridItem[] array = new GridItem[]{
				new GridItem(tid,"admin@cnic.cn",3,"DPage",0.35,false),
				new GridItem(tid,"admin@cnic.cn",4,"DPage",0.45,false),
				new GridItem(tid,"admin@cnic.cn",5,"DPage",0.75,false),
				new GridItem(tid,"admin@cnic.cn",6,"DPage",0.85,false),
				new GridItem(tid,"admin@cnic.cn",7,"DPage",0.95,false),
	   	};
	   	for(int i=0;i<array.length;i++){
	   		map.put(array[i].getRid()+"#"+array[i].getItemType(), array[i]);
	   		System.out.println();
	   	}
	   	return map;
   }

   
   private static <K,V extends Comparable<? super V>>
   SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
       SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
           new Comparator<Map.Entry<K,V>>() {
               @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                   return e1.getValue().compareTo(e2.getValue());
               }
           }
       );
       sortedEntries.addAll(map.entrySet());
       return sortedEntries;
   }
   
	@Test
	public void testAddGroup() {
	    int tid = 1;
		GridGroup gg = new GridGroup();
		gg.setLastEditTime(new Date());
		gg.setTid(tid);
		gg.setUid("liji@cnic.cn");
		gg.setGridItemMap(getMockScoreMap(tid));
		SortedSet<Map.Entry<String, GridItem>> ss = entriesSortedByValues(gg.getGridItemMap());
		for(Map.Entry<String, GridItem> entry:ss){
			print(entry.getValue());
		}
		System.out.println("First:");
		print(ss.first().getValue());
		System.out.println("Last:");
		print(ss.last().getValue());
	}
	
	private void print(GridItem item){
		System.out.println(item.getScore()+","+item.getRid()+","+item.getItemType());
	}

	@Test
	public void testUpdateGroup() {
	    int tid = 1;
		GridGroup gg = new GridGroup();
		gg.setLastEditTime(new Date());
		gg.setTid(tid);
		gg.setUid("liji@cnic.cn");
		gg.setGridItemMap(getUpdateScoreMap(tid));
		gdao.updateGroup(gg);
	}

	@Test
	public void testDeleteGroup() {
//		fail("Not yet implemented");
	}

	public void testGetGridGroup() {
	    int tid = 1;
		GridGroup gg = gdao.getGridGroup("liji@cnic.cn", tid);
		for(GridItem item:gg.getGridItemMap().values()){
			System.out.println(item.getUid());
		}
		Assert.assertEquals(5, gg.getGridItemMap().size());
	}

}
