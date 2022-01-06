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
package net.duckling.ddl.service.search.impl;

import java.util.List;

import net.duckling.ddl.service.search.SearchReocrd;



/**
 * @author lizexin
 *主要操作a1_searchedlog表和a1_searchlog表，分析dlog搜索日志，存储得出的分析结果
 */
public interface SearchLogDAO {
    /**
     * 将dlog中生成初步的搜索日志存储到mysqlo中
     * @param recordlist 一个record对应dlog中的一条日志
     */
    void creatSearchLog(final List<SearchReocrd> recordList);
    /**
     *从searchlog表中取出所有记录
     * @return recordlist
     */
    List<SqlSearchRecord> getLog();
    /**
     *进一步处理dlog，生成点击序列存储到searchedlog中
     * @param recordlist
     */
    void creatSearchedLog(final List<SqlSearchRecord> recordList);

}
