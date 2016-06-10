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

import java.util.List;

import net.duckling.ddl.service.grid.GridItem;
import net.duckling.ddl.service.grid.IGridService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.vlabs.duckling.BaseTest;
import cn.vlabs.duckling.aone.content.base.TestHelper;

public class GridServiceTest extends BaseTest {
	private IGridService gridService;

	@Before
	public void setUp() throws Exception {
		gridService = f.getBean(IGridService.class);
	}

	@After
	public void tearDown() throws Exception {
		gridService = null;
	}
	
	private int tid = 2012;
	private String uid = "test case";
	
	
	@Test
	public void testGetGridItems(){
		List<GridItem> list = gridService.getTopKGridItem(uid, tid, 10);
		for(GridItem item : list)
			printSingle(item);
	}
	
	private void printSingle(GridItem gridItem){
		try {
			String bundleStr = TestHelper.convert2String(gridItem);
			System.out.println(bundleStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
