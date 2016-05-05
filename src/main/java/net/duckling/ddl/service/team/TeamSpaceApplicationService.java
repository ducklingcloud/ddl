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
package net.duckling.ddl.service.team;

import java.util.List;

public interface TeamSpaceApplicationService {
	int add(long newSize, long originalSize, String uid, int tid, String applicationType);
	/**
	 * 分配空间到团队
	 * @param uid
	 * @param size
	 * @param tid
	 */
	long updateSpaceAllocate(String uid, long size, int tid);

	/**
	 * 把所有用户未分配的空间分配到个人空间
	 * @return 总用户数
	 */
	int updateSpaceAllocateAll();
	
	/**
	 * 所有用户的未分配空间分配到个人空间
	 */
	public void updateSpaceAllocateAllUser();
	List<TeamSpaceApplication> queryByTid(int tid);
	void delete(int id);
}
