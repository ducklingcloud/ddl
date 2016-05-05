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

import net.duckling.ddl.service.search.DocWeightRecord;
import net.duckling.ddl.service.search.WeightPair;
/**
 * @author li zexin 2012-09-01
 *用来操作表a1_search_docweight，更新文档权重，和实时查询中文档相关度获取
 */

public interface SearchDocWeightDAO {
	/**
	 * @param keyword 查询词 
	 * @return 对于用户uid查询的keyword所有文档的的权重值
	 */
	List<DocWeightRecord> getDocWeight(String keyword);
	/**
	 * @param keyword
	 * @param uid
	 * @return
	 */
	List<WeightPair> getOwnDocWeight(String keyword,String uid);
	/**
	 * 更新根据搜索记录产生的文档的权重值，返回插入记录的条数
	 */
	int updateDocWeight();
	/**
	 * @param rid 文档编号的列表
	 * @return 这些文档由浏览产生的权重
	 */ 
	List<WeightPair> getViewDocWeight(List<Long> rids);
	

}
