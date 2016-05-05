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
package net.duckling.ddl.service.file.impl;

import java.util.List;

import net.duckling.ddl.service.file.DFileRef;

public interface FileReferenceDAO {
	/**
	 * 或取页面关联的文件
	 * @param pageRid
	 * @param tid
	 * @return
	 */
	List<DFileRef> getPageReferences(int pageRid, int tid);
	
	/**
	 * 或取文件关联的页面
	 * @param fileRid
	 * @param tid
	 * @return
	 */
	List<DFileRef> getFileReferences(int fileRid, int tid);
	
	/**
	 * 保存页面关联的文件
	 * @param refArray
	 */
	void referTo(DFileRef[] refArray);

	/**
	 * 删除页面相关联的文件
	 * @param pid
	 * @param tid
	 */
	void deletePageRefer(int pid, int tid);
	/**
	 * 删除文件相关的页面
	 * @param fid
	 * @param tid
	 */
	void deleteFileRefer(int fid, int tid);
	
	
	void deleteFileAndPageRefer(int fid, int pid, int tid);
	
	
	
}
