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

import java.util.Date;

import net.duckling.ddl.common.DDLFacade;
import net.duckling.ddl.constant.LynxConstants;
import net.duckling.ddl.service.user.AoneUserService;

public final class ResourceUtils {
	private ResourceUtils(){}
	
	public static Resource createDDoc(int tid,int parentRid,String title,String uid){
		Resource resource = new Resource();
		resource.setTid(tid);
		resource.setBid(parentRid);
		resource.setTitle(title);
		resource.setCreator(uid);
		resource.setLastEditor(uid);
		resource.setLastEditorName(DDLFacade.getBean(AoneUserService.class).getUserNameByID(uid));
		resource.setItemType(LynxConstants.TYPE_PAGE);
		resource.setCreateTime(new Date());
		resource.setLastEditTime(new Date());
		resource.setStatus(LynxConstants.STATUS_AVAILABLE);
		resource.setOrderType(Resource.NO_FOLDER_ORDER_TYPE);
		return resource;
	}
}
