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
package net.duckling.ddl.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.duckling.ddl.common.VWBContext;
import net.duckling.ddl.service.devent.DEntity;
import net.duckling.ddl.service.devent.Notice;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.space.SpaceGainedService;
import net.duckling.ddl.web.bean.SimpleResourceKey;
import net.duckling.ddl.web.controller.BaseController;
import net.duckling.meepo.api.IPanService;
import net.duckling.meepo.api.PanAcl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.meepotech.sdk.MeePoException;
import com.meepotech.sdk.PanLinkList;

public abstract class AbstractSpaceController extends BaseController {
	@Autowired
	private IResourceService resourceService;
	@Autowired
	protected SpaceGainedService spaceGainedService;
	@Autowired
	private IPanService panService;
	
	private static Logger LOG = Logger.getLogger(AbstractSpaceController.class);

	private void addSimpleDis(Map<SimpleResourceKey, Resource> result,
			List<Resource> rs) {
		if (rs == null) {
			return;
		}
		for (Resource r : rs) {
			SimpleResourceKey s = new SimpleResourceKey();
			s.setRid(r.getRid());
			s.setItemType(r.getItemType());
			result.put(s, r);
		}

	}

	/**
	 * 将notices中的对象转换为resource
	 * 
	 * @param context
	 * @param notices
	 * @return
	 */
	protected Map<SimpleResourceKey, Resource> getResourceMap(int tid, List<Notice> notices) {
		Map<String, Set<Integer>> ids = new HashMap<String, Set<Integer>>();
		for (Notice n : notices) {
			DEntity entity = n.getTarget();
			if (entity == null) {
				continue;
			}
			Integer id = null;
			try {
				id = Integer.parseInt(entity.getId());
			} catch (Exception e) {
				LOG.warn("notice的targetId出现错误" + n.getId(), e);
				continue;
			}
			Set<Integer> setId = ids.get(entity.getType());
			if (setId == null) {
				setId = new HashSet<Integer>();
				ids.put(entity.getType(), setId);
			}
			setId.add(id);
		}
		Map<SimpleResourceKey, Resource> result = new HashMap<SimpleResourceKey, Resource>();
		for (Entry<String, Set<Integer>> it : ids.entrySet()) {
			Set<Integer> i = it.getValue();
			if (i == null || i.isEmpty()) {
				continue;
			}
			List<Resource> rs = resourceService.getResources(i,
					tid, it.getKey());
			addSimpleDis(result, rs);
		}

		return result;
	}
	
	/**
	 * 将notices中的对象转换为resource
	 * 
	 * @param context
	 * @param notices
	 * @return
	 */
	protected Map<String, Resource> getResourceRidKeyMap(
			VWBContext context, List<Notice> notices) {
		List<Integer> rids=new ArrayList<Integer>();
		for (Notice n : notices) {
			DEntity entity = n.getTarget();
			if (entity == null) {
				continue;
			}
			Integer id = null;
			try {
				id = Integer.parseInt(entity.getId());
			} catch (Exception e) {
				LOG.warn("notice的targetId出现错误" + n.getId(), e);
				continue;
			}
			rids.add(id);
		}
		Map<Integer,Resource> result=resourceService.getResourceMapByRids(rids);
		Map<String,Resource> temp=new HashMap<String,Resource>();
		for(Entry<Integer, Resource>entry:result.entrySet()){
			temp.put(entry.getKey().toString(), entry.getValue());
		}
		return temp;
	}
	
	/**
	 * 判断是否登录过meepo盘
	 * @param acl
	 * @return
	 * @throws MeePoException
	 */
//	private boolean isComputerLogined(PanAcl acl, Date activityBeginTime){
//		boolean result = false;
//		PanLinkList linkList = null;
//		try {
//			linkList = panService.devices(acl);
//			PanLink [] linkArr = linkList.getLinks();
//			if(linkArr!=null){
//				for(PanLink item : linkArr){
//					//不是WEB登陆的即为客户端登陆，登陆时间在活动开始时间之后
//					if(!item.getDevice().equals("WEB") && item.getCreated_millis() > activityBeginTime.getTime() ){
//						result = true;
//						break;
//					}
//				}
//			}
//		} catch (MeePoException e) {
//			LOG.error("access meepo error(isComputerLogined). uid:" + acl.getUid() +", message:" + e.getMessage());
//		}
//		return result;
//	}
	/**
	 * 判断是否登录过meepo盘
	 * @param acl
	 * @param beginTime
	 * @return
	 */
	private boolean isComputerLogined(PanAcl acl, Date beginTime){
		PanLinkList linkList = null;
		try {
			linkList = panService.devices(acl);
		} catch (MeePoException e) {
			LOG.error("access meepo error(isComputerLogined). uid:" + acl.getUid() +", message:" + e.getMessage());
		}
		//至少获取3次登录记录算用户登录过，因为api调用会登录一次
		if(linkList.getTotal() > 3){
			return true;
		}
		return false;
	}
	
}
