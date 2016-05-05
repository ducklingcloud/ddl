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

package net.duckling.ddl.service.subscribe.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.duckling.ddl.service.subscribe.NotifyPolicy;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.Subscription;
import net.duckling.ddl.service.subscribe.SubscriptionService;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.AoneUserService;
import net.duckling.ddl.service.user.UserExt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @date 2011-2-28
 * @author Clive Lee
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    
    @Autowired
	private SubscriptionDAO subscriptionDao;
    @Autowired
    private URLGenerator urlGenerator;
    @Autowired
    private AoneUserService aoneUserService;

	@Override
	public List<String> getTeamMemberFeedList(String user,int tid){
		return subscriptionDao.getTeamMemberFeed( user, tid);
	}

	private Pattern pattern = Pattern.compile( ".*\\((.*)\\)");
	private String extractUserId(String author){
		if (author!=null && author.indexOf('(')!=-1){
			Matcher matcher = pattern.matcher(author);
			if (matcher.matches()){
				return matcher.group(1);
			}
		}
		return author;
	}
	@Override
	public List<Subscription> getPageSubscribers(int tid, int rid) {
		return subscriptionDao.findPageSubscirption(tid, rid);
	}
	
	@Override
	public List<Subscription> getSubscriptionByUserId(int tid,String userId,String type){
		List<Subscription> results = subscriptionDao.getSubscriptionByUserId(tid,userId,type);
		for(Subscription s:results){
			s.getPublisher().setUrl(urlGenerator.getURL(tid, UrlPatterns.T_VIEW_R, s.getPublisher().getId()+"", null));
		}
		return results;
	}
	
	@Override
	public List<Subscription> getPersonSubscribers(int tid, String author){
		UserExt ext = aoneUserService.getUserExtInfo(extractUserId(author));
		if (ext!=null){
			return subscriptionDao.findSubscriptions(tid, new Publisher(Publisher.PERSON_TYPE, ext.getId()));
		}
		return null;
	}

	//添加关注
	@Override
	public int addMyCreatePageFeedRecord(String user,int rid) {
		Publisher publisher = new Publisher();
		publisher.setType(Publisher.MY_CREATE_PAGE);
		publisher.setId(rid);
		return subscriptionDao.save(getSingleFeed(user,publisher));
	}
	
	@Override
	public int addSingleFeedRecord(String feeder,Publisher publisher) {
		return subscriptionDao.save(getSingleFeed(feeder,publisher));
	}
	
	@Override
	public void addBatchFeedRecords(String currUser, Publisher[] publishers) {
		subscriptionDao.batchSave(getFeedArray(currUser, publishers));
	}

	//移除关注
	@Override
	public void removeSubscription(String currUser, Publisher[] publishers) {
		Subscription[] subArray = getFeedArray(currUser, publishers);
		subscriptionDao.delete(subArray);
	}
	
	@Override
	public void removeSubscription(int feedId) {
		Subscription deleteItem = new Subscription();
		deleteItem.setId(feedId);
		subscriptionDao.delete(deleteItem);
	}

	public void setSubscriptionDao(SubscriptionDAO subscriptionDao) {
		this.subscriptionDao = subscriptionDao;
	}

	private Subscription[] getFeedArray(String currUser, Publisher[] pubs) {
		Subscription[] array = new Subscription[pubs.length];
		for (int i = 0; i < pubs.length; i++){
			array[i] = getSingleFeed(currUser, pubs[i]);
		}
		return array;
	}

	private Subscription getSingleFeed(String currUser, Publisher pub) {
		NotifyPolicy pagePolicy = new NotifyPolicy();
		pagePolicy.setPolicy("message");
		Subscription subRecord = new Subscription();
		subRecord.setUserId(currUser);
		subRecord.setNotifyPolicy(pagePolicy);
		subRecord.setPublisher(pub);
		subRecord.setTid(pub.getTid());
		return subRecord;
	}

	@Override
	public boolean isFeedPerson(String user, int id) {
		return subscriptionDao.isFeedPerson(user,id);
	}

	@Override
	public void removePageSubscribe(int tid, int rid) {
		subscriptionDao.removePageSubscribe(tid, rid);
	}

	@Override
	public void removePersonSubscription(int tid, String uid,int uxid) {
		subscriptionDao.removeSubscriptionAboutPerson(tid, uid,uxid);
	}
}
