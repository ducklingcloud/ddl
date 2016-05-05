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
import java.util.Date;
import java.util.List;

import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.file.FileVersion;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.dao.ResourceDAOImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import cn.vlabs.duckling.BaseTest;

public class ResourceDAOTest extends BaseTest {
	private ResourceDAOImpl resourceDAO;

	@Before
	public void setUp() throws Exception {
		resourceDAO = f.getBean(ResourceDAOImpl.class);
	}

	@After
	public void tearDown() {
		resourceDAO = null;
	}
	
	private int id = 1;
	private int tid = 2012;
	
	@Test
	public void testCreate(){
		Resource resource = new Resource();
		resource.setTid(tid);
		resource.setTitle("test case create resource");
		resource.setCreateTime(new Date());
		resource.setCreator("test case");
		resource.setLastEditor("test case");
		resource.setLastEditTime(new Date());
		resource.setOrderDate(new Date());
		resource.setLastVersion(LynxConstants.INITIAL_VERSION);
		resourceDAO.create( resource);
	}
	public int create(int tid,String type){
		Resource r = resourceDAO.getResourceById(100,tid);
		if(r!=null){
			return r.getRid();
		}
		Resource resource = new Resource();
		resource.setTid(tid);
		resource.setItemType(type);
		resource.setTitle("test case create resource");
		resource.setCreateTime(new Date());
		resource.setCreator("test case");
		resource.setLastEditor("test case");
		resource.setLastEditTime(new Date());
		resource.setOrderDate(new Date());
		resource.setLastVersion(LynxConstants.INITIAL_VERSION);
		return resourceDAO.create( resource);
	}
	
	@Test
	public void testGetResourceById(){
		create(tid,LynxConstants.TYPE_BUNDLE);
		Resource resource = resourceDAO.getResourceById(100, tid);
		printSingleResource(resource);
	}
	
	@Test
	public void testGetResourceByTIDTYPE(){
		List<Resource> list = resourceDAO.getResourceByTypeAndTid(tid, LynxConstants.TYPE_BUNDLE);
		for(Resource resource : list)
			printSingleResource(resource);
	}
	
	@Test
	public void testUpdate(){
		create(tid, LynxConstants.TYPE_BUNDLE);
		Resource resource = resourceDAO.getResourceById(100, tid);
		resource.setLastEditTime(new Date());
		resource.setTitle("test case change title");
		resource.setLastVersion(2);
		resourceDAO.update(resource);
		Resource resourceUpdate = resourceDAO.getResourceById(100, tid);
		printSingleResource(resourceUpdate);
	}
	
	@Test
	public void testDelete(){
		resourceDAO.delete(id, tid);
		try{
			resourceDAO.getResourceById(id, tid);
			System.out.println("failed to delete resource by id="+id+", tid="+tid);
		}catch(EmptyResultDataAccessException e){
			System.out.println("success to delete resource by id="+id+", tid="+tid);
		}
	}
	
	@Test
	public void testGetFileSizeByRids(){
		List<Long> rids = new ArrayList<Long>();
		rids.add((long)169);
		rids.add((long)170);
		rids.add((long)171);
//		List<FileVersion> fileVersions = resourceDAO.getFileSizeByRids(rids);
//		for(FileVersion version : fileVersions)
//			printSingleFileVersion(version);
	}
	
	public void printSingleResource(Resource resource){
		StringBuilder sb = new StringBuilder();
		sb.append("rid:"+resource.getRid()+",");
		sb.append("tid:"+resource.getTid()+",");
		sb.append("item_type:"+resource.getItemType()+",");
		sb.append("title:"+resource.getTitle()+",");
		sb.append("createTime:"+resource.getCreateTime()+",");
		sb.append("creator:"+resource.getCreator()+",");
		sb.append("sphinxId:"+resource.getRid()+",");
		sb.append("lastEditor:"+resource.getLastEditor()+",");
		sb.append("lastEditTime:"+resource.getLastEditTime()+",");
		sb.append("lastVersion:"+resource.getLastVersion());
		System.out.println(sb.toString());
	}
	
	@SuppressWarnings("unused")
    private void printSingleFileVersion(FileVersion fileVersion){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+fileVersion.getId()+",");
		sb.append("rid:"+fileVersion.getRid()+",");
		sb.append("tid:"+fileVersion.getTid()+",");
		sb.append("version:"+fileVersion.getVersion()+",");
		sb.append("clbId:"+fileVersion.getClbId()+",");
		sb.append("size:"+fileVersion.getSize()+",");
		sb.append("title:"+fileVersion.getTitle()+",");
		sb.append("editor:"+fileVersion.getEditor()+",");
		sb.append("editTime:"+fileVersion.getEditTime());
		System.out.println(sb.toString());
	}
}
