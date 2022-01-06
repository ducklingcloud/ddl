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

import net.duckling.ddl.service.resource.TagGroup;

/**标签组数据库操作对象*/
public interface TagGroupDAO {
    int create(TagGroup tagGroup);
    int delete(int id);
    int update(int id, TagGroup tagGroup);
    TagGroup getTagGroupById(int id);
    int getTagGroupTitleCount(int tid, String title);
    /**查找名称类似于title的群组
     * @author lvly
     * @since 2012-07-30
     * @param tid teamId
     * @param title tagGroup.title
     * @return count 匹配数量
     * */
    TagGroup getTagGroupLikeTitle(int tid,String title);

    List<TagGroup> getAllTagGroupByTid(int tid);
    void updateSequence(Integer[] tgids);
}
