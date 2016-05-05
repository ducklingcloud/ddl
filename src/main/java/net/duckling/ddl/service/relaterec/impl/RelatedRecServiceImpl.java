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

package net.duckling.ddl.service.relaterec.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.duckling.ddl.service.relaterec.DGrid;
import net.duckling.ddl.service.relaterec.DGridDisplay;
import net.duckling.ddl.service.relaterec.DGridItem;
import net.duckling.ddl.service.relaterec.DGridItemDisplay;
import net.duckling.ddl.service.relaterec.RelateRecordService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @date 2012-2-10
 * @author zhuzhishi
 */
@Service
public class RelatedRecServiceImpl implements RelateRecordService{
    
    @Autowired
	private RedisDAO redisDao;
    
    @Autowired
	private IResourceService resourceService;
	
	/**
	 * 根据当前页面、用户兴趣模型、页面相似页面集进行页面推荐
	 * @param tid
	 * @param uid
	 * @param pid
	 * @param num    推荐页面数目
	 * @return
	 */
	@Override
	public DGridDisplay getRelatedRecOfPage(int tid,String uid, int pid ,int num){		
		DGridDisplay dGridDisplay = new DGridDisplay();
		
		DGrid dGrid = new DGrid();
		dGrid.setTitle("分享");
		dGrid.setType("rec");

		List<DGridItemDisplay> gridItemList = new ArrayList<DGridItemDisplay>();
		
		dGridDisplay.setGrid(dGrid);
		dGridDisplay.setGridItemList(gridItemList);
		
		if(!redisDao.isConnect()){
			return dGridDisplay;
		}
		
		Vector<PageSimilar> vector = redisDao.getPageSimilar(tid,pid);
		
		if(vector==null || vector.isEmpty()){
			redisDao.addPageToUpdate(tid, pid);
			return dGridDisplay;
		}
		
		UserVSM user = redisDao.getUserLongVSM(tid,uid);
		
		UserPageSimilar[] ups = new UserPageSimilar[vector.size()];
		
		for(int i=0; i < ups.length; ++i){
			UserPageSimilar userSimilar = new UserPageSimilar();
			
			userSimilar.setPid(vector.get(i).getId());
			
			double sim = vector.get(i).getSimilar();			
			FeatureWeight[] page = redisDao.getPageVSM(tid,vector.get(i).getId());
			
			sim += computerSimilar(user,page);
			
			userSimilar.setSimilar(sim);
			
			ups[i] = userSimilar;
		}
		
		if(ups.length > num){
		
			for(int i=0; i<num; ++i){
				UserPageSimilar sim = ups[i];
				int k = i;
				
				for(int j=i+1; j<vector.size();++j){
					if(sim.compareTo(ups[j]) < 0){
						sim = ups[j];
						k = j;
					}
				}
				
				ups[k] = ups[i];
				ups[i] = sim;
				
			}
		}
			
		for(int i=0; i<num && i<ups.length; ++i){
			DGridItemDisplay gridItemDisplay = new DGridItemDisplay();
			DGridItem gridItem = new DGridItem();
			gridItem.setResourceId(ups[i].getPid());
			gridItem.setResourceType("DPage");
			gridItem.setTid(tid);
			gridItemDisplay.setItem(gridItem);		
			Resource resource = resourceService.getResource(ups[i].getPid(), tid);  
			gridItemDisplay.setTitle(resource.getTitle());
			gridItemList.add(gridItemDisplay);
		}	
		return dGridDisplay;
	}
	/**
	 * 计算用户兴趣模型和页面的相似度
	 */
	private double computerSimilar(UserVSM user,FeatureWeight[] page){
		double sim = 0;
		if(user==null){
			return sim;
		}
		
		for(FeatureWeight fw : page){
			
			Double wt = user.getVector().get(fw.getIndex());   
			
			if(wt != null){  
				sim += wt * fw.getWeight(); 
			}
		}
		
		return sim ;
	}
  }

