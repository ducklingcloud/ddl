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
package net.duckling.ddl.service.tobedelete;

import java.util.List;



public interface PageDAO {
	int create(Page page);
	/**
	 * 删除页面，即更新状态标记位
	 * @param pid 页面ID
	 * @param tid 团队ID
	 * @return
	 */
	int delete(int pid, int tid);
	/**
	 * 批量删除页面，即更新状态标记位
	 * @param tid 团队ID
	 * @param pids 页面ID集合
	 * @return
	 */
	int batchDelete(int tid, List<Integer> pids);
	int update(int pid, int tid, Page page);
	int update(Page page);
	Page getPage(int pid, int tid);
	
	List<Page> getPagesOfTeam(int tid, int offset, int size);
	List<Page> fetchDPageBasicListByPageIncrementId(List<Long> sphinxIds);
	List<PageContentRender> fetchDPageContentByIncrementId(List<Long> sphinxIds);
	
	List<Page> getRecentUserCreatePages(int tid, String uid, int offset, int size);
	List<Page> getRecentUserEditPages(int tid, String uid, int offset, int size);
	List<Page> searchResourceByTitle(int tid, String title);
	List<Page> getPage(List<Integer> pids, int tid);
}
