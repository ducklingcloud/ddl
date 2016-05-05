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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duckling.ddl.service.tobedelete.PageContentRender;
import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.ResourceQuery;
import net.duckling.ddl.util.TeamQuery;


public interface IResourceService {
	
	/**
	 * 获取资源树
	 * @param tid
	 * @param folderRid 根目录rid
	 * @return
	 */
	Resource getTree(int tid, int folderRid);
	
	Resource getResource(int rid, int tid);
	/**
	 * 获取resource不用管状态
	 * @param rid
	 * @return
	 */
	Resource getResource(int rid);
	List<Resource> getUnBundleResource(int bid, int tid, String keyword, int offset, int size);
	List<Resource> getResource(TeamQuery query);
	List<Resource> getResource(List<Integer> rids);
	PaginationBean<Resource> query(ResourceQuery q);
	/**
	 * 批量更新Resource对象的星标内容，不涉及Bundle的操作
	 * @param resList Resource集合，其中各资源已包含最新的星标集
	 */
	void updateMarkedUserSet(List<Resource> resList);
	/**
	 * 取出当前Bundle以及Bundle内所有资源的星标内容，并用这些星标内容更新Bundle以及Bundle内所有资源的星标内容。
	 * 只涉及markedUserSet字段的操作。
	 * @param bid Bundle的ID
	 * @param tid 团队ID
	 */
	void mergeAndUpdateMarkedUserSetOfBundle(int bid, int tid,List<Integer> itemRids);
	/**
	 * 从Bundle及Bundle内所有资源的星标集合中删除指定用户的星标。
	 * 只涉及markedUserSet字段的操作
	 * @param bid
	 * @param tid
	 * @param uid
	 */
	void removeMarkedUserFromBundle(int bid, int tid, String uid,List<Integer> rids);
	/**
	 * 批量更新资源集合中所有资源的bid字段
	 * @param bid 更新后的bid值
	 * @param rids 资源ID集合
	 */
	void updateBid(int bid,List<Long> rids);
	/**
	 *从a1_resource中删除单个资源，不做其他任何操作；
	 *删除资源，改为更新标记位置 add by lvly@2012-07-20
	 *@param fid 
	 *@param tId teamId
	 *@param type 类型，page，file等
	 * 
	 * */
	void delete(int fid, int tid, String type);
	/**
	 * 批量删除资源，并不真正删除，仅更新资源的状态。
	 * @param tid 团队ID
	 * @param rids 资源的rid集合
	 */
	void batchDelete(int tid, List<Integer> rids);
	/**
	 * 获取ids集合中所有资源
	 * @param ids 资源的rid集合
	 * @return List&lt;Resource&gt;资源集合
	 */
	List<Resource> getResourcesBySphinxID(List<Long> ids);
	/**
	 * 获取指定用户在指定团队下的所有星标资源
	 * @param uid 用户ID
	 * @param tid 团队ID
	 * @return 资源集合
	 */
	List<Resource> getStarmarkResource(String uid,int tid);
	/**
	 * 获取指定资源集合中的所有星标组成的集合（不重复）
	 * @param rids 资源的rid集合
	 * @return 不重复的星标集合
	 */
	Set<String> getStarmarkOfResources(List<Long> rids);
	int create(Resource res);
	int update(Resource res);
	void update(List<Resource> res);
	
	SimpleResource getSimpleResource(int rid,String itemType,int tid);
	SimpleResource getSimpleResource(int tid, int rid);
	
	/**
	 * 批量更新资源的TagMap
	 * @param resList 资源列表，其中各资源已包含最新的TagMap
	 */
	void updateResourceTagMap(List<Resource> resList);
	/**
	 * 取出当前Bundle以及内部所有资源的tagMap并集，
	 * 并用其更新Bundle以及Bundle内所有资源的TagMap
	 * @param bid Bundle的ID
	 * @param tid 团队ID
	 */
	void mergeAndUpdateTagMapOfBundle(int bid, int tid, List<Integer> itemRids);
	/**
	 * 将指定标签从Bundle本身及Bundle内所有资源的tagMap删除
	 * @param bid Bundle的ID
	 * @param tid 团队ID
	 * @param tagId 标签的ID
	 */
	void removeTagFromTagMapOfBundle(int bid, int tid, int tagId, List<Integer> itemRids);
	
	void updateBundleFileType(int bid, int tid);
	
	List<Resource> queryReferableFiles(String keyword, int tid);
	List<Resource> queryReferableFiles(String keyword, int[] tid,int offset,int size);
	
	int queryReferableFilesCount(String keyword, int[] tid);
	/**
	 * 对 <b>新建的Bundle</b>，更新Bundle及Bundle内所有资源的markedUserSet和tagMap，<br/>
	 * 建立Bundle以及Bundle内所有资源与所有Tag的关联关系(a1_tag_item)。
	 * @param bid Bundle ID
	 * @param tid 团队ID
	 * @param bundleItemRids 新Bundle内的所有资源的Rid集合，这些资源与Bundle的关联关系已建立。即<br/>
	 * a1_bundle_item已存在相应记录
	 */
	void updateNewBundleTagAndStarmark(int bid,int tid, int[] bundleItemRids);
	/**
	 * 对 <b>已有的Bundle</b>，更新Bundle及Bundle内所有资源的markedUserSet和tagMap，<br/>
	 * 建立所有资源与所有Tag的关联关系(a1_tag_item)。
	 * @param bid Bundle ID
	 * @param tid 团队ID
	 * @param bundleItemRids 新添加进Bundle的资源ID集合，这些资源与Bundle的关联关系已建立。即<br/>
	 * a1_bundle_item已存在相应记录
	 */
	void updateExistBundleTagAndStarmark(int bid, int tid, int[] bundleItemRids);
	/**
	 * 批量更新资源的排序字段：时间和标题
	 * @param itemResList
	 */
	void updateOrderColumn(List<Resource> itemResList);
	/**
	 * 批量获取resource
	 * @param itemIds
	 * @param id
	 * @param itemType
	 * @return
	 */
	List<Resource> getResources(Collection<Integer> rids, int id, String itemType);
	Map<Integer,Resource> getResourceMap(List<Resource> resourceList);
	Map<Integer,Resource> getResourceMapByRids(List<Integer> rids);
//	/**
//	 * 恢复资源
//	 * @param tid
//	 * @param itemId
//	 * @param itemType
//	 */
//	void recoverResource(int tid,int itemId,String itemType);
	
	Map<String,List<Integer>> getResourceTypeMap(int tid,List<Integer> rids);
	
	List<Resource> getDDoc(int tid,List<Integer> rids);
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
	/**
	 * 查询团队下的文件
	 * @param tid
	 * @param type 类型报告doc,ppt,xls,pdf,picture,file,page
	 * @param offset
	 * @param size
	 * @param order
	 * @return
	 */
	PaginationBean<Resource> getResourceByFileType(int tid,String type,int offset,int size,String order,String keyWord);
	List<Resource> getFileByTitle(int tid,int parentRid,String title);
	List<Resource> getResourceByTitle(int tid,int parentRid,String type,String title);
	List<Resource> getResourceByTitle(int tid,int parentRid,String type,String title,String status);
	
	
	List<Resource> fetchDPageBasicListByPageIncrementId(List<Long> rid);
	List<PageContentRender> fetchDPageContentByIncrementId(List<Long> rids);
	
	/**
	 * 团队最近修改的文档
	 * @param tid
	 * @param offset
	 * @param size
	 * @param order TODO
	 * @param keyWord
	 * @return
	 */
	PaginationBean<Resource> getTeamRecentChange(int tid,int offset, int size, String order, String keyWord);
	
	void updateTagMap(int newRid, Tag tag) ;
	/**
	 * 获取团队所有resource大小
	 * @param tid
	 * @return
	 */
	long getTeamResourceSize(int tid);
	/**
	 * 更新memcache中team SIZE
	 * @param tid
	 * @return
	 */
	long updateTeamResSize(int tid);
	void resetTeamResSize(int tid);
	/**
	 * 更改resource的状态
	 * @param rids
	 * @param status
	 * @param tid
	 */
	void updateResourceStatus(Collection<Integer> rids, String status, int tid);
	
	int getTeamResourceAmount(int tid);
}
