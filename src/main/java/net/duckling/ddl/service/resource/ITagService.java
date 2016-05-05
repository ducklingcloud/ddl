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

import net.duckling.ddl.util.PaginationBean;
import net.duckling.ddl.util.TeamQuery;

/**涉及到标签的服务类*/
public interface ITagService {
	/**
	 * 新建标签
	 * @param tag 需要保存的Tag对象
	 * @return
	 */
	int createTag(Tag tag);
	/**
	 * 更新标签
	 * @param tag 包含最新状态的标签对象
	 * @return
	 */
	int updateTag(Tag tag);
	/**
	 * 通过标签ID删除标签（标签ID是唯一的）
	 * @param tgid 标签ID
	 * @return
	 */
	int deleteTag(int tgid);
	/**
	 * 通过标签ID获取标签信息
	 * @param tgid 标签ID
	 * @return
	 */
	Tag getTag(int tgid);
	/**
	 * 根据标签名获取标签信息（同一团队内标签名也是唯一的）
	 * @param tid 团队ID
	 * @param title 标签名
	 * @return
	 */
	Tag getTag(int tid, String title);
	/**
	 * 根据标签名获取标签信息（同一团队内标签名也是唯一的）
	 * @param tid 团队ID
	 * @param gid 团队组ID
	 * @param title 标签名
	 * @return
	 */
	Tag getTag(int tid,int gid, String title);
	/**
	 * 获取用户的姓名标签
	 * @param uid 用户ID
	 * @return
	 */
	Tag getUserNameTag(String uid);
	/**
	 * 获取指定团队内标签名title的标签个数
	 * @param tid 团队ID
	 * @param title 标签名
	 * @return
	 */
	int getTagTitleCount(int tid, String title);
	
	/**
	 * 给一个资源打上一系列的标签，并修改a1_tag_item和标签计数，
	 * 不涉及Bundle的操作。<b>若资源已存在该标签，则不会重复产生a1_tag_item记录。</b>
	 * @param tid
	 * @param tagIds
	 * @param rid
	 * @return
	 */
	int addItems(int tid, List<Integer> tagIds, int rid);
	
	/**
	 * 给一个资源打上一系列的标签，并修改a1_tag_item和标签计数，
	 * 不涉及Bundle的操作。<b>若资源已存在该标签，则不会重复产生a1_tag_item记录。</b>
	 * @param tid
	 * @param tagIds
	 * @param rids
	 * @return
	 */
	int addItems(int tid, List<Integer> tagIds, List<Long> rids);
	/**
	 * 删除团队内若干资源与某标签的关联关系(a1_tag_item)
	 * @param tid 团队ID
	 * @param tgid 标签ID
	 * @param rids 资源ID集合
	 * @return
	 */
	int removeItems(int tid, int tgid, List<Integer> rids);
//	int removeTagItemsByTgids(int[] tgids);
	
	
	List<TagItem> getTagItems(int tgid);
	List<TagItem> getTagItems(int[] tgids);
	List<TagItem> getTagItems(int[] tgids,int offset,int size);
	
	int createTagGroup(TagGroup tagGroup);
	int updateTagGroup(int tagGroupId, TagGroup tagGroup);
	int deleteTagGroup(int tagGroupId);
	int addTags2Group(int tagGroupId, int[] tgids);
	int removeTagFromGroup(int tagGroupId, int[] tgids);
//	int removeTagGroup(int tid, int tagGroupId);
	int getTagGroupTitleCount(int tid, String title);
	
	/**查找名称类似于title的群组
	 * @author lvly
	 * @since 2012-07-30
	 * @param tid teamId
	 * @param title tagGroup.title
	 * @return count 匹配数量
	 * */
	TagGroup getTagGroupLikeTitle(int tid,String title);
	
	
	/**获得指定团队的tag列表,返回tag按照count降序排列,这些tag不在TagGroup中
	 * @param tid teamId
	 * */
	List<Tag> getTagsNotInGroupForTeam(int tid);
	
	List<Tag> getTagsByGroupId(int groupId,int tid);
	
	/**获得指定团队的所有TagGroupRender,一个TagGroup对象下面若干个Tag对象*/
	List<TagGroupRender> getTagGroupsForTeam(int tid);
	TagGroup getTagGroupById(int tagGroupId);
	/**根据查询参数返回资源条目*/
	List<TagItem> query(TeamQuery q);
	
	/**Tag计数增加*/
	int increaseCount(int tagid,int delta);
	/**Tag计数减少*/
	int decreaseCount(List<Integer> tagids, int delta);
	
	/**获得当前rid未包含的Tag*/
	List<Tag> getNotRelatedTags(int rid,int tid);
	
	Map<String, List<Tag>> getTagGroupMap(int tid);
	
	boolean isItemHasTag(int rid, int existTagId);
	void updateTagGroupsOrder(Integer[] tgids);
	
	
	List<Tag> getTags(int[] tagids);
	List<Tag> getTags(List<Integer> tagids);
	/**
	 * 获取指定团队的所有标签
	 * @param tid
	 * @return
	 */
	List<Tag> getTagsForTeam(int tid);
	/**
	 * 通过拼音搜索标签
	 * @param tid 团队ID
	 * @param pinyin 标签名的拼音
	 * @return
	 */
	List<Tag> getTagsByPinyin(int tid, String pinyin);
	/**
	 * 通过汉字搜索标签
	 * @param tid 团队ID
	 * @param name 标签名中的汉字
	 * @return
	 */
	List<Tag> getTagsByName(int tid, String name);
	/**
	 * 删除某个资源相关的Tag记录
	 * @param tid 团队ID
	 * @param rid 资源ID
	 */
	void removeAllTagItemsOfRid(int tid, int rid);
	/**
	 * 计算包含指定Tag的资源数，被计算在内的资源为不包含在
	 * Bundle内的资源（bid=0）
	 * @param tid 团队ID
	 * @param tagId 标签ID
	 * @return
	 */
	int getTagCount(int tid, int tagId);
	/**
	 * 计算指定Tag的计数，并更新a1_tag中的count字段
	 * @param tid 团队ID
	 * @param tagId 标签ID
	 */
	void updateTagCount(int tid, int tagId);
	
	void updateTagCountByRid(int tid,int rid);
	
	void deleteTagItems(List<Integer> rids, Integer tagId);
	
	List<TagItem> getItems(int tagId);
	
	public int batchUpdateWithTag(int tid, int tagId, List<Long> newRids);
	
	public void deleteTagByTagIds(int[] tgids);
	
	public void deleteTagItemByTagIds(int[] tagids);
	PaginationBean<Resource> getTeamTagFiles(int tid, int tagId, int begin, int maxPageSize, String order, String keyWord);
	PaginationBean<Resource> getTeamTagFiles(int tid ,Collection<Integer> tagIds,int begin,int maxPageSize,String order, String keyWord);
	Tag getNameTag(int tid,String uid,String userName);
	void addItem(int tid, int tagId, int rid);
}
