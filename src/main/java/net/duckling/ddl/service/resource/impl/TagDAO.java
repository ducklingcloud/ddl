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

import net.duckling.ddl.service.resource.Tag;


public interface TagDAO {
    int create(Tag tag);
    int delete(int id);
    int deleteBatch(int[] ids);
    int update(int id, Tag tag);
    Tag getTagById(int id);
    int getTagTitleCount(int tid, String title);

    List<Tag> getAllTag();
    /**
     * 获取指定团队的所有标签
     * @param tid 团队ID
     * @return
     */
    List<Tag> getAllTagInTeam(int tid);
    /**
     * 通过标签的拼音搜索标签
     * @param tid 团队ID
     * @param pinyin 标签的拼音
     * @return 标签集合
     */
    List<Tag> getTagsByPinyin(int tid, String pinyin);
    /**
     * 通过标签名中的汉字搜索标签
     * @param tid 团队ID
     * @param name 标签名中的汉字
     * @return 标签集合
     */
    List<Tag> getTagsByName(int tid, String name);
    List<Tag> getAllTagInTeamAndGroup(int tid, int tagGroupId);
    List<Tag> getAllGroupTagsByTeam(int tid);

    int addTags2Group(int tagGroupId, int[] tgids);
    int removeTagsFromGroup(int tagGroupId, int[] tgids);
    int increaseCount(int tagid, int delta);
    int updateCount(int tagid, int count);
    List<Tag> getNotRelatedTags(int rid, int tid);
    /**
     * 批量获取指定tagId的标签对象集合
     * @param tagids 标签ID集合
     * @return
     */
    List<Tag> getTags(int[] tagids);
    /**
     * 获取指定标签名的标签对象
     * @param tid 团队ID
     * @param title 标签名
     * @return 标签对象
     */
    Tag getTag(int tid, String title);
    int decreaseCount(List<Integer> tagids, int delta);
    /**
     * 根据标签名获取标签信息（同一团队内标签名也是唯一的）
     * @param tid 团队ID
     * @param gid 团队组ID
     * @param title 标签名
     * @return
     */
    Tag getTag(int tid, int gid, String title);
    /**
     * 获取用户姓名标签
     * @param uid 用户ID
     * @return
     */
    Tag getUserNameTag(String uid);
}
