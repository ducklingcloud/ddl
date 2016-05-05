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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.SimpleResource;
import net.duckling.ddl.service.tobedelete.PageContentRender;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.TeamQuery;


public interface ResourceDAO {
	int create(Resource resource);
	/**
	 * 根据rid, tid删除指定资源，即更新资源状态
	 * 为"delete"
	 * @param rid
	 * @param tid
	 * @return
	 */
	int delete(int rid, int tid);
	/**
	 * 批量删除资源，即更新资源状态为"delete"
	 * @param rids 资源的rid集合
	 * @return
	 */
	int batchDelete(List<Integer> rids);
	int update(Resource resource);
	Resource getResourceById(int id, int tid);
	Resource getResource(int sphinxId);
	List<Resource> getUnBundleResource(int bid, int tid, String title, int offset, int size);
	List<Resource> getResourceByTypeAndTid(int tid, String type);
	List<Resource> query(TeamQuery q);
	/**
	 * 批量更新Resource的TagMap
	 * @param resList
	 */
	void updateTagMap(List<Resource> resList);
	/**
	 * 批量更新a1_resource表中markedUserSet字段
	 * @param resList 待更新的资源集
	 * @return 1
	 */
	int updateMarkedUserSet(List<Resource> resList);
	List<Resource> getResourceByRids(List<Long> ids);
	List<Resource> getStarmarkResource(String uid, int tid);
	/**
	 * 批量更新资源集合中资源的bid字段
	 * @param bid 用于更新的bid值
	 * @param rids 资源ID集合
	 * @return
	 */
	int updateBid(int bid, List<Long> rids);
	List<SimpleResource> getSimpleResourceByTeam(int tid);
	Set<String> getStarmarkOfResources(List<Long> rids);
	
	List<Resource> getAllResource();
	String getAllBundleItemFileType(int bid, int tid);
	void updateBundleFileType(int bid, int tid, String fileType);
	List<Resource> queryReferableFiles(String keyword, int tid);
	/**
	 * 批量更新资源的排序字段：时间和标题
	 * @param itemResList
	 * @return
	 */
	void updateOrderColumn(List<Resource> itemResList);
	/**
	 * 批量获取resource
	 * @param rids
	 * @param id
	 * @param itemType
	 * @return
	 */
	List<Resource> getResources(Collection<Integer> rids, int id, String itemType);
	int queryReferableFilesCount(String keyword, int[] tid);
	List<Resource> queryReferableFiles(String keyword, int[] tid, int offset, int size);
	List<Resource> getDDoc(int tid, List<Integer> rids);
	List<Resource> getFileByStartName(int tid, String name);
	
	/**
	 * 我创建的文档
	 * @param tid
	 * @param uId
	 * @param offset
	 * @param size
	 * @param order
	 * @param keyWord
	 * @return
	 */
	PaginationBean<Resource> getMyCreatedFiles(int tid,String uId,int offset,int size,String order, String keyWord);
	
	/**
	 * 我最近查看的文档
	 * @param tid
	 * @param uId
	 * @param offset
	 * @param size
	 * @param order
	 * @return
	 */
	PaginationBean<Resource> getMyRecentFiles(int tid,String uId,int offset,int size,String order);
	List<Resource> getFileByTitle(int tid, int parentRid, String title);
	List<Resource> getResourceByTitle(int tid, int parentRid, String type, String title);
	List<Resource> getResourceByTitle(int tid, int parentRid, String type, String title,String status);
	List<Resource> fetchDPageBasicListByPageIncrementId(List<Long> pageIds);
	List<PageContentRender> fetchDPageContentByIncrementId(List<Long> rids);
	/**
	 * 团队最近修改的文档
	 * @param tid
	 * @param offset
	 * @param size
	 * @param keyWord
	 * @param order 
	 * @return
	 */
	PaginationBean<Resource> getTeamRecentChange(int tid,int offset, int size, String keyWord, String order);
	PaginationBean<Resource> getResourceByFileType(int tid, String type, int offset, int size, String order,String keyWord);
	PaginationBean<Resource> query(ResourceQuery q);
	void update(List<Resource> res);
	int getTeamResourceAmount(int tid);
	
	/**
	 * 获取团队所有resource大小
	 * @param tid
	 * @return
	 */
	long getTeamResourceSize(int tid);
	void updateResourceStatus(Collection<Integer> rids, String status);
	
	/**
	 * 更新分享状态
	 * @param rid
	 * @param shared
	 */
	void updateShared(Integer rid, boolean shared);
	
}
