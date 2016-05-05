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
package net.duckling.ddl.service.dbrain.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.duckling.ddl.service.dbrain.RecommendationBean;
import net.duckling.ddl.service.dbrain.RecommendationService;
import net.duckling.ddl.service.dbrain.Vector4UserService;
import net.duckling.ddl.service.dbrain.util.TermVector;
import net.duckling.ddl.service.dbrain.util.Util;
import net.duckling.ddl.service.dbrain.util.Word2VEC;
import net.duckling.ddl.service.devent.Notice;

import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl implements RecommendationService{
	
	//private static final Logger LOG = Logger.getLogger(RecommendationServiceImpl.class);
	
	public List<Notice> getRecommendNotices(List<Notice> candidates, String uid, Word2VEC word2vec) {
		if(word2vec == null){
			return candidates;
		}
		float[] uservec = vector4UserService.getUserVecByPassport(uid);
		List<Notice> result = new ArrayList<Notice>();
		List<RecommendationBean<Notice>> itemList = new ArrayList<RecommendationBean<Notice>>();
		for (Notice item : candidates) {
			float [] itemvec = Util.doc2vec(item.getTarget().getName(), word2vec);
			double score = (uservec == null || itemvec == null) ? 0 :
									TermVector.ComputeCosineSimilarity(uservec, itemvec);
			RecommendationBean<Notice> bean = new RecommendationBean<Notice>();
			bean.setScore(score);
			bean.setItem(item);
			itemList.add(bean);
		}
		sortList(itemList);
		
		for(int i=0; i<itemList.size(); i++){
			RecommendationBean<Notice> item = itemList.get(i);
			result.add(item.getItem());
		}
		
		return result;
	}
	
	/**
	 * 排序函数
	 * 
	 * @param itemlist
	 */
	private void sortList(List<RecommendationBean<Notice>> itemlist) {
		Comparator<RecommendationBean<Notice>> comparator = new Comparator<RecommendationBean<Notice>>() {
			public int compare(RecommendationBean<Notice> vo1,
					RecommendationBean<Notice> vo2) {
				double score1 = vo1.getScore();
				double score2 = vo2.getScore();
				if (score1 > score2) {
					return -1;
				} else if (score1 < score2) {
					return 1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(itemlist, comparator);
	}
	
	private Vector4UserService vector4UserService;
	public void setVector4UserService(Vector4UserService vector4UserService) {
		this.vector4UserService = vector4UserService;
	}
	
}
