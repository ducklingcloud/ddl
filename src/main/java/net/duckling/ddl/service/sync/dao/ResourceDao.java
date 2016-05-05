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
package net.duckling.ddl.service.sync.dao;

import java.util.List;

import net.duckling.ddl.service.sync.domain.resource.DFile;
import net.duckling.ddl.service.sync.domain.resource.Resource;

public interface ResourceDao {

	/**
	 * 返回文件夹下的所有文件类型的资源（包括子孙文件夹下的）
	 * 
	 * @param tid
	 * @param folderRid
	 * @param itemType
	 *            []
	 * @return
	 */
	List<Resource> getDescendants(int tid, int folderRid, String itemType);

	List<DFile> getDescendantsChecksum(int tid, int folderRid);
}
