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

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.resource.TagItem;
import net.duckling.ddl.util.PaginationBean;

/**标签和资源关联关系的数据库操作对象*/
public interface TagItemDAO {
    /**
     * 创建标签和资源rid的关联关系，插入时会判断是否存在
     * @param tagItem
     * @return
     */
    int create(TagItem tagItem);

    int delete(int id);
    int deleteByTagId(int tgid);
    int deleteByTagIds(int[] tgids);
    /**
     * 删除团队内指定标签和若干资源的关联关系
     * @param tid 团队ID
     * @param tgid 标签ID
     * @param rids 资源ID集合
     * @return
     */
    int removeItems(int tid, int tgid, List<Integer> rids);

    int update(int id, TagItem tagItem);
    /**
     * 给一系列资源打上同一个标签
     * @param tid 团队ID
     * @param tagId 标签ID
     * @param rids 资源ID集合
     * @return
     */
    int batchUpdateWithTag(int tid, int tagId, List<Long> rids);

    TagItem getTagItemById(int id);

    List<TagItem> getItemsInTags(int[] tgids, int offset, int size);
    /**
     * 获取标签ID所关联资源的tagItem
     * @param tgid 标签ID
     * @return List<TagItem>
     */
    List<TagItem> getItemsInTag(int tgid);
    /**
     * 删除指定资源集合内所有资源与tagId指定标签的关联关系(tagItem)
     * @param rids 资源ID
     * @param tagId 标签ID
     */
    void deleteTagItem(List<Integer> rids, Integer tagId);

    boolean isItemHasTag(int rid, int existTagId);

    List<TagItem> getAllTagItem();
    /**
     * 获得团队下与rid指定资源相关的tagItem项
     * @param tid 团队ID
     * @param rid 资源ID
     * @return List<TagItem>
     */
    List<TagItem> getAllTagItemOfRid(int tid, int rid);
    /**
     * 删除团队下与rid所指定资源相关的tagItem项
     * @param tid 团队ID
     * @param rid 资源ID
     */
    void deleteAllTagItemOfRid(int tid, int rid);
    /**
     * 计算包含指定Tag的资源数，被计算在内的资源为不包含在
     * Bundle内的资源（bid=0）
     * @param tid
     * @param tagId
     * @return 标签的计数
     */
    int getTagCount(int tid, int tagId);

    PaginationBean<Resource> getTeamTagFiles(int tid, int tagId, int begin, int maxPageSize, String order, String keyWord);

    PaginationBean<Resource> getTeamTagFiles(int tid, Collection<Integer> tagIds, int begin, int maxPageSize, String order, String keyWord);
}
