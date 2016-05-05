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
package net.duckling.ddl.web.vo;

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.service.resource.Resource;
import net.duckling.ddl.service.share.ShareResource;
import net.duckling.ddl.service.team.Team;
import net.duckling.ddl.util.PaginationBean;

/**
 * 显示层对象转换
 * @author Brett
 *
 */
public class VoUtil {
	
	public static TeamVo getTeamVo(Team team){
		TeamVo vo = new TeamVo();
		vo.setTeamCode(team.getName());
		vo.setAccessType(team.getAccessType());
		vo.setDisplayName(team.getDisplayName());
		vo.setDescription(team.getDescription());
		vo.setCreateTime(team.getCreateTime());
		vo.setCreator(team.getCreator());
		return vo;
	}
	
	public static ShareResourceVo getShareResourceVo(ShareResource shareResource){
		ShareResourceVo vo = new ShareResourceVo();
		vo.setTitle(shareResource.getTitle());
		vo.setShareUrl(shareResource.getShareUrl());
		return vo;
	}
	
	public static ResourceVo getResourceVo(Resource r){
		ResourceVo vo = new ResourceVo();
		vo.setTid(r.getTid());
		vo.setTitle(r.getTitle());
		vo.setPath(r.getPath());
		vo.setSize(r.getSize());
		vo.setVersion(r.getLastVersion());
		vo.setFolder(r.isFolder());
		vo.setLastEditor(r.getLastEditor());
		vo.setLastEditorName(r.getLastEditorName());
		vo.setLastEditTime(vo.getLastEditTime());
		return vo;
	}
	
	public static PaginationVo<ResourceVo> getResourcePaginationVo(PaginationBean<Resource> pagination){
		PaginationVo<ResourceVo> vo = new PaginationVo<ResourceVo>();
		vo.setBegin(pagination.getBegin());
		vo.setLimit(pagination.getSize());
		vo.setTotal(pagination.getTotal());
		vo.setData(getResourceVoList(pagination.getData()));
		return vo;
	}
	
	public static List<ResourceVo> getResourceVoList(List<Resource> resourceList){
		List<ResourceVo> list = new ArrayList<ResourceVo>();
		if(resourceList == null){
			return list;
		}
		for(Resource item :resourceList){
			list.add(getResourceVo(item));
		}
		return list;
	}
}
