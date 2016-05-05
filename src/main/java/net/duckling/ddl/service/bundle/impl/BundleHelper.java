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
package net.duckling.ddl.service.bundle.impl;

import java.util.ArrayList;
import java.util.List;

import net.duckling.ddl.service.bundle.BundleItem;


public final class BundleHelper {
	public static List<Long> getRidsFromBundleItems(List<BundleItem> items){
		List<Long> results = new ArrayList<Long>();
		if(null != items && !items.isEmpty()){
			for(BundleItem item : items){
				results.add((long)item.getRid());
			}
		}
		return results;
	}
	public static List<Integer> getIntRidsFromBundleItems(List<BundleItem> items){
		List<Integer> results = new ArrayList<Integer>();
		if(null != items && !items.isEmpty()){
			for(BundleItem item : items){
				results.add(item.getRid());
			}
		}
		return results;
	}
	
	public static BundleItem buildBundleItem(int tid, int rid, int bid){
		BundleItem item = new BundleItem();
		item.setBid(bid);
		item.setRid(rid);
		item.setTid(tid);
		item.setSequence(0);
		return item;
	}
}
