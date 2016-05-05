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
package net.duckling.ddl.service.grid.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.duckling.ddl.service.grid.GridItem;
import net.duckling.ddl.service.grid.IGridService;
import net.duckling.ddl.service.resource.IResourceService;
import net.duckling.ddl.service.resource.Resource;
import net.duckling.falcon.api.cache.ICacheService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GridServiceImpl implements IGridService {
    
    private static final String GRID_SOCRE = "grid-score";

	private static final double DECREASE_FACTOR = 0.80;
	
	private static final int GRID_STACK_SIZE = 30;
	
	private static final Logger LOG = Logger.getLogger(GridServiceImpl.class);
	private static final long MAX_TIME_ALIVE = 900;
	private static final double SCORE_PER_CLICK = 2;
	private static final double SCORE_PER_EDIT = 3;
	private static final double SCORE_PER_MARK = 5;
	private static final double[] SOCRE_OF_PIN = new double[]{
		1000,999,998,997,996,995,994,993,992,991,990,989,988,987,986
	};
	private static final int TIME_NO_MILISECOND = 1000;
	private static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    return e1.getValue().compareTo(e2.getValue());
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
	
	@Autowired
	private GridGroupDAO gridGroupDAO;
	@Autowired
	private ICacheService memcachedService;
	
	@Autowired
    private IResourceService resourceService;

    private void addGridGroup(GridGroup gg){
		int ggid = gridGroupDAO.addGroup(gg);
		gg.setId(ggid);
		setGridGroupToCache(gg.getTid(),gg.getUid(), gg);
	}
	
	private void changeAllScore(String uid,int tid,double delta){
		GridGroup gg = getGridGroup(uid,tid);
		boolean hasChanged = false;
		if(gg.getGridItemMap()!=null){
		    for(GridItem item:gg.getGridItemMap().values()){
		        if(!item.isFixed()){
		            item.setScore(item.getScore()*delta);
		            hasChanged = true;	
		        }
		    }
		    if(hasChanged){
		        updateGridGroup(gg);
		    }
		}
	}
	
	private void decreaseScore(GridItem item,double delta,boolean newFixedFlag){
		if(!item.isFixed()){
			item.setScore(item.getScore()-delta);
			item.setFixed(newFixedFlag);
			updateGridItem(item.getUid(), item.getTid(), item);
		}
	}

	/**
	 * map中Bundle及Bundle内资源同时出现时，剔除掉Bundle内资源的条目
	 * @param map 记录九宫格的Map
	 * @param tid 团队ID
	 * @return
	 */
    private Map<String, GridItem> filter(GridGroup gg, int tid, String uid){
		Map<String, GridItem> map = gg.getGridItemMap();
		Map<String, GridItem> result = new HashMap<String, GridItem>();
		if(null != map && !map.isEmpty()){
			for(Map.Entry<String, GridItem> entry : map.entrySet()){
				String[] keys = entry.getKey().split("#");
				if(keys.length<=1){
					LOG.error("error key in grid map! key = "+entry.getKey());
					Map<String, GridItem> newMap = new HashMap<String, GridItem>(map);
					newMap.remove(entry.getKey());
					gg.setGridItemMap(newMap);
					updateGridGroup(gg);
					continue;
				}
				int rid = Integer.parseInt(keys[0]);
				String itemType = keys[1];
				Resource res = resourceService.getResource(rid, tid);
				if (null == res || ( map.containsKey(res.getRid() + "#" + res.getItemType()))) {
					continue;
				} else {
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}
	
//    private boolean loadDataSuccuss(String uid, int tid) {
//        GridGroup o = gridGroupDAO.getGridGroup(uid, tid);
//        if (o != null) {
//            setGridGroupToCache(tid, o.getUid(), o);
//            return true;
//        }
//        return false;
//    }
//	
//	private boolean isCacheNotHit(GridGroup gg){
//		return getGridGroupFromCache(gg.getTid(),gg.getUid())==null;
//	}
	
	private GridGroup getGridGroup(String uid, int tid) {
        GridGroup gg = getGridGroupFromCache(tid, uid);
        if (gg == null || gg.getGridItemMap()==null||gg.getGridItemMap().isEmpty()) {
            gg = gridGroupDAO.getGridGroup(uid, tid);
            if (gg != null) {
                setGridGroupToCache(tid, gg.getUid(), gg);
            }else{
                gg = new GridGroup(uid, tid);
                addGridGroup(gg);
            }
        }
        return gg;
    }
	
	private GridGroup getGridGroupFromCache(int tid,String uid){
	    return (GridGroup)memcachedService.get(GRID_SOCRE+"."+tid+"."+uid);
	}
	
    private GridItem getGridItem(String uid, int tid, int rid, String itemType) {
        GridItem item = new GridItem();
        item.setRid(rid);
        item.setItemType(itemType);
        GridGroup gg = getGridGroup(uid, tid);
        GridItem temp = gg.getGridItemMap().get(getGridItemKey(item));
        if (temp == null) {
            item.setTid(tid);
            item.setUid(uid);
            item.setTid(tid);
            item.setScore(0.0);
            return item;
        }
        return temp;
    }
	
	private String getGridItemKey(GridItem item){
		return item.getRid()+"#"+item.getItemType();
	}
	
	private void increaseScore(GridItem item, double delta,boolean newFixedFlag) {
		if(!item.isFixed()){
			item.setScore(item.getScore()+delta);
			item.setFixed(newFixedFlag);
			updateGridItem(item.getUid(), item.getTid(), item);
		}
	}
	
	private boolean isExpired(GridGroup gg){
		return Math.abs(new Date().getTime() - gg.getLastEditTime().getTime())/TIME_NO_MILISECOND > MAX_TIME_ALIVE ;
	}
	
    private void setGridGroupToCache(int tid, String uid,GridGroup gg){
	    memcachedService.set(GRID_SOCRE+"."+tid+"."+uid, gg);
	}

	private void updateGridGroup(GridGroup gg){
		if(isExpired(gg)){
			gg.setLastEditTime(new Date());
			gridGroupDAO.updateGroup(gg);
		}
		setGridGroupToCache(gg.getTid(),gg.getUid(), gg);
	}
	
	private void updateGridItem(String uid,int tid,GridItem item){
		GridGroup gg = getGridGroup(uid,tid);
		Map<String,GridItem> scoreMap = gg.getGridItemMap();
		SortedSet<Map.Entry<String, GridItem>> sortedEntries = entriesSortedByValues(gg.getGridItemMap());
		boolean hasChanged = false;
		if(sortedEntries==null||sortedEntries.size()==0){
			scoreMap.put(getGridItemKey(item), item);
			hasChanged = true;
		}else{
			if(scoreMap.size()<GRID_STACK_SIZE){
				scoreMap.put(getGridItemKey(item), item);
				hasChanged = true;
			}else{
				GridItem minScoreItem = sortedEntries.last().getValue();
				if(item.getScore()>minScoreItem.getScore()){
					scoreMap.put(getGridItemKey(item), item);
					scoreMap.remove(minScoreItem);
					hasChanged = true;
				}
			}
		}
		if(hasChanged) {
			updateGridGroup(gg);
		}
	}
	
    @Override
	public void clickItem(String uid,int tid,int rid,String itemType) {
		GridItem item = getGridItem(uid,tid,rid,itemType);
		increaseScore(item,SCORE_PER_CLICK,false);
	}

	@Override
	public void decreaseItemScore(String uid,int tid) {
		changeAllScore(uid,tid,DECREASE_FACTOR);
	}
	@Override
	public void editItem(String uid,int tid,int rid,String itemType) {
		GridItem item = getGridItem(uid,tid,rid,itemType);
		increaseScore(item,SCORE_PER_EDIT,false);
	}

	@Override
	public List<GridItem> getTopKGridItem(String uid, int tid, int k) {
		GridGroup gg = this.getGridGroup(uid, tid);
//		Map<String, GridItem> map = filter(gg,tid, uid);
		SortedSet<Map.Entry<String, GridItem>> set = entriesSortedByValues(gg.getGridItemMap());
		int maxSize = k;
		if(k>set.size()){
			maxSize = set.size();
		}
		Iterator<Map.Entry<String, GridItem>> iter = set.iterator();
		List<GridItem> results = new ArrayList<GridItem>();
		int i = 0;
		while(iter.hasNext()){
			if(i<maxSize){
				results.add(iter.next().getValue());
				i++;
			}else{
				break;
			}
		}
		return results;
	}

	@Override
	public boolean kickout(String uid, int tid, int rid, String itemType) {
		GridItem item = getGridItem(uid,tid,rid,itemType);
		String key = this.getGridItemKey(item);
		GridGroup gg = this.getGridGroup(uid, tid);
		if(gg.getGridItemMap().containsKey(key)){
			gg.getGridItemMap().remove(key);
			setGridGroupToCache(tid, gg.getUid(), gg);
			return true;
		}
		return false;
	}

	@Override
	public void markItem(String uid,int tid,int rid,String itemType) {
		GridItem item = getGridItem(uid,tid,rid,itemType);
		increaseScore(item,SCORE_PER_MARK,false);
	}
	
	@Override
	public void pin(String uid,int tid,int rid,String itemType,int level) {
		GridItem item = getGridItem(uid,tid,rid,itemType);
		increaseScore(item,SOCRE_OF_PIN[level],true);
	}

	public void setGridGroupDAO(GridGroupDAO gridGroupDAO) {
		this.gridGroupDAO = gridGroupDAO;
	}

	public void setMemcachedService(ICacheService memcachedService) {
        this.memcachedService = memcachedService;
    }

	@Override
	public void unpin(String uid,int tid,int rid,String itemType,int level) {
		GridItem item = getGridItem(uid,tid,rid,itemType);
		item.setFixed(false);
		decreaseScore(item,SOCRE_OF_PIN[level],false);
	}

}
