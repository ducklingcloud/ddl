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

package net.duckling.ddl.service.browselog;

import java.util.List;


/**
 * @date Mar 8, 2011
 * @author xiejj@cnic.cn
 */
public interface BrowseLogService {
	int getVisitCount(int rid);

	List<BrowseLog> getVisitor(int rid, int count);

	List<BrowseStat> getTopPageView(int tid, int length, int daysAgo);

	void resourceVisited(int tid, int rid, String uid,String userName, String item_type) ;
}