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
package net.duckling.ddl.service.search.impl;

import java.util.List;

import net.duckling.ddl.service.search.UserAnalysis;
import net.duckling.ddl.service.search.WeightPair;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAnalysisImpl implements UserAnalysis {
	private static final Logger LOG = Logger.getLogger(UserAnalysisImpl.class);
	@Autowired
	private UserAnalysisDAO userAnalysisDAO;
	
	public void analysisUser(){
		//用户兴趣
		List<UserInterestRecord> viewRecord = preprocessInterest();
		userAnalysisDAO.creatUserInterest(viewRecord);
		//用户相似
		userAnalysisDAO.creatUserSim();
	}

	private List<UserInterestRecord> preprocessInterest() {
		List<UserInterestRecord> viewRecord = userAnalysisDAO.getBrowse();//只考虑用户浏览日志
		//UserInterestRecord记录每一个用户对应一个用户的关注度得分（浏览过改用户的文档树）
		int size = viewRecord.size();
		LOG.info("user analysis raw data count is " + size);		
		//for循环处理interet，去掉括号 ，提取uid
		for(int i = 0;i<size;i++){
			String tempi = viewRecord.get(i).getInterst();
			if(tempi.contains("(")&&tempi.contains(")")){
				viewRecord.get(i).setInterst(tempi.substring((tempi.indexOf("(")+1), tempi.indexOf(")")));
			}
		}
		return viewRecord;
	} 

	public List<String> getInterest(String uid){
		List<String> interests = userAnalysisDAO.getInterest(uid);
		if(interests == null ||interests.isEmpty()){
			return null;
		}
		List<String> finalinterest = interests.subList(0, (interests.size()/getDivisor(interests.size(),1.5)));
		return finalinterest;		
	}
	
	public double getFactor(List<UserInterestRecord> interest,int index){
		if(interest == null || interest.isEmpty()|| index>=interest.size() || index<=-1){
			LOG.info("getFactor error");
			return 1.0;
		}
		double finalscore = 1;
		double totalscore = 1;
		for(int i=0;i<interest.size();i++ ){
			totalscore += interest.get(i).getScore();
		}
		finalscore = 10*(interest.get(index).getScore()/totalscore);
		return finalscore;
	}
	
	public List<WeightPair> getInterestDocWeight(String keyword, String uid){
		List<WeightPair> weightPairList = userAnalysisDAO.getInterestDocWeight(keyword, uid);
		return weightPairList;
	}
	public List<String> getSim(String uid){
		List<String> sims = userAnalysisDAO.getSim(uid);
		if(sims == null ||sims.isEmpty()){
			return null;
		}
		List<String> finalsim = sims.subList(0, (sims.size()/getDivisor(sims.size(),1.5)));
		return finalsim;	
	}
	public List<WeightPair> getSimDocWeight(String keyword, String uid){
		List<WeightPair> weightPairList = userAnalysisDAO.getSimDocWeight(keyword, uid);
		return weightPairList;
	}
	private int getDivisor(int value,double base){
		return value<3?value:(int)( 2*((Math.log(value)+1)/(Math.log(base)+1)));
	}
	
	public UserAnalysisDAO getUserAnalysisDAO() {
		return userAnalysisDAO;
	}

	public void setUserAnalysisDAO(UserAnalysisDAO userAnalysisDAO) {
		this.userAnalysisDAO = userAnalysisDAO;
	}

}
