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

import net.duckling.ddl.service.resource.PageVersion;


public interface PageVersionDAO {
    int create(PageVersion pageVersion);
    int delete(int id);
    /**
     * 恢复删除的版本信息
     * @param pid
     * @param tid
     * @return
     */
    int recoverPafeVersion(int rid ,int tid);
    /**删除页面的版本后，后改为标记，不删除
     * @param pid PageId
     * @param tid teamId
     * @return int 成功删除多少条记录
     *
     * */
    int deleteAllPageVersion(int rid, int tid);
    int update(int id, PageVersion pageVersion);
    PageVersion getPageVersionById(int id);
    PageVersion getPageVersion(int rid, int version);
    PageVersion getLatestPageVersion(int rid);

    List<PageVersion> getAllPageVersionByTIDRID(int tid, int rid);
    List<PageVersion> getVersions(int rid, int tid, int offset, int size);
    List<PageVersion> getUserRecentPages();
}
