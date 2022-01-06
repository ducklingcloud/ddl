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

import net.duckling.ddl.util.PaginationBean;

public interface IStarmarkService {
    /**
     * 添加星标：添加指定用户在指定团队下给指定资源打的星标，
     * 若该资源是Bundle或属于Bundle则对整个Bundle进行级联操作。
     * 此操作涉及到a1_starmark和a1_resource
     * @param uid 用户ID
     * @param rid 资源ID
     * @param tid 团队ID
     */
    void addStarmark(String uid, int rid,int tid);
    /**
     * 移除星标：移除指定用户在指定团队下给指定资源打的星标，
     * 若该资源是Bundle或属于Bundle则对整个Bundle进行级联操作。
     * 此操作涉及到a1_starmark和a1_resource
     * @param uid 用户ID
     * @param rid 资源ID
     * @param tid 团队ID
     */
    void removeStarmark(String uid,int rid,int tid);

    void removeResourceStarmark(Resource r);

    /**
     * 我的星标文档
     * @param tid
     * @param uId
     * @param offset
     * @param size
     * @param order
     * @param keyWord TODO
     * @return
     */
    PaginationBean<Resource> getMyStartFiles(int tid,String uId,int offset,int size,String order, String keyWord);
}
