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
import java.util.Set;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.Starmark;
import net.duckling.ddl.util.PaginationBean;


public interface StarmarkDAO {
	/**
	 * 创建星标记录
	 * @param s
	 * @return
	 */
	int create(Starmark s);
	/**
	 * 批量创建星标记录
	 * @param uid
	 * @param tid
	 * @param rids
	 * @return
	 */
	int batchCreate(String uid, int tid, List<Long> rids);
	/**
	 * 删除指定用户在指定团队下的所有星标记录
	 * @param tid
	 * @param uid
	 * @return
	 */
	int deleteAllStarmark(int tid, String uid);
	/**
	 * 删除指定用户所有团队下关于指定指定资源的星标记录
	 * @param rid
	 * @param uid
	 * @return
	 */
	int delete(int rid,String uid);
	/**
	 * 批量删除星标记录
	 * @param uid
	 * @param tid
	 * @param rids
	 * @return
	 */
	int batchDelete(String uid, int tid, List<Long> rids);
	/**
	 * 获取指定id的星标记录
	 * @param id
	 * @return Starmark
	 */
	Starmark getStarmarkById(int id);
	/**
	 * 获取系统中所有的星标记录
	 * @return List<Starmark>
	 */
	List<Starmark> getAllStarmark();
	/**
	 * 获取指定rid集合对应的Starmark对象
	 * @param uid
	 * @param tid
	 * @param rids
	 * @return List<Starmark>
	 */
	List<Starmark> getStarmarkOfRids(String uid, int tid, List<Long> rids);
	
	int batchDeleteResourceStar(int tid,int rid,Set<String> uids);
	
	PaginationBean<Resource> getMyStartFiles(int tid, String uId,int offset, int size, String order, String keyWord);
}
