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
package net.duckling.ddl.service.resource;

import java.util.List;

import net.duckling.ddl.util.PaginationBean;

public interface FolderPathService {
	/**
	 * 删除rid并删除子目录
	 * @param rid
	 * @return
	 */
	boolean delete(int tid,int rid);
	/**
	 * 创建resource目录关系
	 * @param parentRid
	 * @param rid
	 * @param tid
	 * @return
	 */
	boolean create(int parentRid,int rid,int tid);
	/**
	 * 获取目录路径
	 * @param rid
	 * @return
	 */
	List<FolderPath> getPath(int tid,int rid);
	
	FolderPath get(int tid,int rid, int ancestorRid);
	/**
	 * 获取父目录
	 * @param rid
	 * @return
	 */
	FolderPath getParent(int rid);
	
	List<FolderPath> query(int rid, int length);
	/**
	 * 获取子目录
	 * @param rid
	 * @return
	 */
	List<FolderPath> getChildrenPath(int tid,int rid);
	/**
	 * 获取所有后继节点
	 * @param rid
	 * @return
	 */
	List<FolderPath> getDescendantsPath(int tid,int rid);
	/**
	 * 移动resource
	 * @param tid
	 * @param orginalRid 被移动的resourceRid
	 * @param targetRid 移动到的目标rid
	 * @return
	 */
	boolean move(int tid,int orginalRid,int targetRid);
	

	/**
	 * 查询rid的子元素
	 * @param tid
	 * @param rid
	 * @param orderStr 排序类型
	 * @param begin
	 * @param size
	 * @param keyWord 查询关键字
	 * @return
	 */
	PaginationBean<Resource> getChildren(int tid, int rid, String fileType, String orderStr, int begin,int size, String keyWord);
	
	/**
	 * 查询当前元素的子文件夹
	 * @param tid
	 * @param rid
	 * @return
	 */
	List<Resource> getChildrenFolder(int tid,int rid);
	/**
	 * 获取父目录的Resource
	 * @param rid
	 * @return
	 */
	Resource getParentResource(int rid);
	/**
	 * 获取路径
	 * @param rid
	 * @return
	 */
	List<Resource> getResourcePath(int rid);
	
	/**
	 * 获取资源的字符串路径 如/hello/world/test.doc
	 * @param rid
	 * @return
	 */
	String getPathString(int rid);
	
	/**
	 * 获取目录下的名称，保证名称不重复
	 * @param tid
	 * @param parentRid
	 * @param itemType
	 * @param name
	 * @return
	 */
	String getResourceName(int tid, int parentRid, String itemType,String name);
	
	/**
	 * 获取目录下的名称，保证名称不重复
	 * @param tid
	 * @param parentRid
	 * @param itemType
	 * @param name
	 * @return
	 */
	String getResourceName(int tid, int parentRid, String itemType,String name,int[] filterRid);
	/**
	 * 获取所有子孙节点
	 * @param tid
	 * @param rid
	 * @return
	 */
	List<Resource> getDescendants(int tid,int rid);
	
	List<Resource> getChildren(int tid, int rid);
	List<Resource> getResourceByName(int tid, int parentRid, String itemType, String name);
	List<Resource> getResourceByName(int tid, int parentRid, String name);
	/**
	 * 搜索 ancestorRid 下的所有子孙节点，搜索项包括title，tag，lastEditorName
	 * @param tid
	 * @param ancestorRid
	 * @param keyWord 搜索关键字
	 * @param order
	 * @param begin
	 * @param size
	 * @return
	 */
	PaginationBean<Resource> searchResource(int tid,int ancestorRid,String keyWord,String order,int begin,int size);
	/**
	 * 删除rid所在的path
	 * @param tid
	 * @param rids
	 * @return
	 */
	boolean delete(int tid, List<Integer> rids);
	void updateResourceName(int tid, int destRid, List<Resource> rs);
	void move(int tid, List<Resource> srcResources, int destRid);
	
	/**
	 * 根据资源全路径查询对象
	 * @param path
	 * @return
	 */
	Resource getResourceByPath(int tid, String path);
	Resource getResourceByPath(int tid, PathName path);
	List<Resource> setResourceListPath(List<Resource> list, String parentPath);
	List<Resource> setResourceListPath(List<Resource> list);
}
