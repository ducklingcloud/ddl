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
package net.duckling.ddl.service.search;

import java.io.IOException;
import java.util.List;



/**
 * @author Lizexin 2012-09-01
 *分析搜索的dlog日志
 */
public interface SearchLogAnalysis {
	/**
	 * @param 开始日期
	 * @param 结束日期
	 * @return 是否分析成功
	 * @throws IOException
	 */
	Boolean saveLog(String start,String end) throws IOException;
	/**
	 * 分析每次搜索后的点击流
	 */
	void anyasisLog();
	/**
	 * @return 返回所有的文档记录，按照uid，keyword分组
	 */
	List<DocWeightRecord> getDocWeight(String keyword);
	/**
	 * @param keyword
	 * @param uid
	 * @return 当前查询用户针对当前关键词，得到用来优化的用户的文档id-weight的list。
	 */
	List<WeightPair> getOwnDocWeight(String keyword,String uid);
	/**
	 * @param rid 文档编号的列表
	 * @return 这些文档由浏览产生的权重
	 */
	List<WeightPair> getViewDocWeight(List<Long> rids);
}
