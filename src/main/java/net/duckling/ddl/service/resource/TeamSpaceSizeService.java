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
package net.duckling.ddl.service.resource;

import java.util.List;

import net.duckling.ddl.exception.NoEnoughSpaceException;

public interface TeamSpaceSizeService {
	TeamSpaceSize getTeamSpaceSize(int tid);
	/**
	 * 更新memcache中team SIZE
	 * @param tid
	 * @return
	 */
	long updateTeamResSize(int tid);
	/**
	 * 移除cache中记录信息
	 * @param tid
	 */
	void resetTeamResSize(int tid);
	boolean validateTeamSize(int tid);
	boolean validateTeamSize(int tid,long size);
	boolean validateTeamSize(int tid,List<Resource> rs);
	
	void validateTeamSizes(int tid,long size) throws NoEnoughSpaceException;
}
