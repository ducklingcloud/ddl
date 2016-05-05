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
package net.duckling.ddl.service.resource.impl;

import java.util.List;

import net.duckling.ddl.service.resource.FolderPath;

/**
 * 
 * @author zhonghui
 *
 */
public interface FolderPathDAO {
	boolean insertBatch(List<FolderPath> paths);
	/**
	 * 创建一个path
	 * @param folder
	 * @return
	 */
	boolean create(FolderPath folder);
	/**
	 * 删除一个folder并删除其子目录
	 * @param folder
	 * @return
	 */
	boolean delete(FolderPath folder);
	/**
	 * 删除一个folder并删除其子目录
	 * @param rid
	 * @return
	 */
	boolean deleteByRid(int rid);
	boolean deleteByRids(List<Integer> rids);
	boolean delete(List<FolderPath> rids);
	/**
	 * 获取目录路径
	 * @param rid
	 * @return
	 */
	List<FolderPath> getPath(int tid,int rid);
	
	/**
	 * 获取一个路径节点
	 * @param tid
	 * @param rid
	 * @param ancestorRid
	 * @return
	 */
	FolderPath get(int tid,int rid,int ancestorRid);
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
	List<FolderPath> getChildren(int tid,int rid);
	List<FolderPath> getAncestor(int tid,List<Integer> orginalRids);
	List<FolderPath> getAncestor(int tid,int rid);
	List<FolderPath> getDescendants(int tid,int rid);
	List<FolderPath> getDescendants(int tid, List<Integer> orginalRids);
	/**
	 * 删除rid所在的path
	 * @param tid
	 * @param rids
	 * @return
	 */
	boolean delete(int tid, List<Integer> rids);
}
