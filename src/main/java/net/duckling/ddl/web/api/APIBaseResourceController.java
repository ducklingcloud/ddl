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

package net.duckling.ddl.web.api;

import java.util.Set;

import net.duckling.ddl.service.resource.Resource;

import org.apache.commons.lang.StringUtils;

/**
 * Resource基础的Controller
 * @date 2012-12-8
 * @author zzb@cnic.cn
 */
public class APIBaseResourceController extends APIBaseController {

	protected String getResourceType(Resource resource) {
		String type = "default";
		if(resource != null) {
			if(!resource.isFile()) {
				type = resource.getItemType();
			} else {
				type = resource.getFileType();
			}
		}
		if(!StringUtils.isNotEmpty(type)) type = "default";
		return type;
	}
	
	protected boolean isCurrentUserMarked(String user, Set<String> markedSet) {
		boolean marked = false;
		if(StringUtils.isNotEmpty(user) && markedSet != null) {
			for(String markedName : markedSet) {
				if(user.equals(markedName)) {
					marked = true;
					break;
				}
			}
		}
		return marked;
	}

}
