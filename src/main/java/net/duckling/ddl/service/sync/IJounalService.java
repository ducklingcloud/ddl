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
package net.duckling.ddl.service.sync;

import java.util.List;

public interface IJounalService {
	long add(int tid, long fid, long fver,String device, String operation, boolean isDir, String path);
	
	/**
	 * @param tid
	 * @param fid
	 * @param fver
	 * @param device
	 * @param operation
	 * @param path
	 * @param toPath 移动文件的目标路径
	 * @return
	 */
	long add(int tid, long fid, long fver,String device, String operation, boolean isDir, String path, String toPath);
	
	Jounal assmebleJounal(int tid, long fid, long fver,String device, String operation,boolean isDir, String path, String toPath);
	
	/**
	 * 批量添加操作日志
	 * @param jounalList
	 * @return
	 */
	boolean addBatch(List<Jounal> jounalList);
	
	/**
	 * 获取资源的字符串路径 如/hello/world/test.doc
	 * @param rid
	 * @return
	 */
	String getPathString(int rid);
	
	List<Jounal> list(int tid, long jid);
	
	/**
	 * 获取团队最新jid
	 * @param tid
	 * @return
	 */
	int getLatestJid(int tid);
}
