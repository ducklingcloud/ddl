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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.MessageBody;
import net.duckling.ddl.service.subscribe.MessageService;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.subscribe.Subscription;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @date 2011-3-1
 * @author Clive Lee
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDAO messageDao;
    @Autowired
    private SubscriptionServiceImpl subscriptionService;

    private static final int SEVENDAYS = 7;

    // 获得当前用户
    @Override
    public List<Message> getMessage(int tid, String userId) {
        Date end = new Date();
        Date start = this.coupleDaysAgo(SEVENDAYS, end);
        return messageDao.getMessage(tid,userId, start, end);
    }
    @Override
    public void saveAttachmentMessage(int tid, MessageBody message, String userName){
        List<Subscription> subMap = subscriptionService.getPersonSubscribers(tid,userName);
        messageDao.createMessage(tid,message, subMap);
    }
    @Override
    public void savePageMessage(int tid,MessageBody message, int pageId) {
        if (subscriptionService!=null){//当初始化时不发送页面创建信息
            List<Subscription> subMap = subscriptionService.getPageSubscribers(tid,pageId);
            subMap.addAll(subscriptionService.getPersonSubscribers(tid,message.getFrom()));
            messageDao.createMessage(tid, message, subMap);
        }
    }
    @Override
    public void removePageMessages(int tid, int pageId){
        messageDao.removePagemessages(tid, pageId);
    }
    @Override
    public void saveFeedCommentMessage(int tid,MessageBody body,int pageId,int commentId) {
        //将comment发生更新发送给所有订阅者
        List<Subscription> subList = subscriptionService.getPageSubscribers(tid,pageId);
        for(Subscription sub:subList) {
            sub.getPublisher().setType(Publisher.FEED_COMMENT);
            sub.getPublisher().setId(commentId);
        }
        messageDao.createMessage(tid, body, subList);
    }

    @Override
    public void saveRecommendCommentMessage(int tid, MessageBody body,Publisher publisher,String userId){
        //将对特定人的回复以推荐的形式发送出去
        publisher.setType(publisher.getType());
        messageDao.createMessage(tid, body, publisher, new String[]{userId});
    }

    @Override
    public void saveRecommend(int tid, MessageBody body, int pageId, String[] userIds) {
        Publisher publisher = new Publisher();
        publisher.setId(pageId);
        publisher.setType(Publisher.RECOMMEND_TYPE);
        messageDao.createMessage(tid, body, publisher, userIds);
    }

    public void setMessageDao(MessageDAO messageDao) {
        this.messageDao = messageDao;
    }

    public void setSubscriptionService(SubscriptionServiceImpl ss) {
        this.subscriptionService = ss;
    }

    @Override
    public void updateMessageStatus(int tid,int pageId,String userId,String messageType) {
        String tempMessageType = messageType;
        if("subscription".equals(tempMessageType)){
            tempMessageType = "page";
        }
        messageDao.updateStatus(tid,userId, pageId,tempMessageType);
    }

    private Date coupleDaysAgo(int before, Date end) {
        Date today = DateUtils.truncate(end, Calendar.DAY_OF_MONTH);
        return DateUtils.addDays(today, -before);
    }

    private int retrieveNumber(String key,Map<String,Integer> map){
        return (map.get(key)!=null)?map.get(key):0;
    }

    @Override
    public int[] countMessages(int tid, String user) {
        Date start = coupleDaysAgo(SEVENDAYS,new Date());

        Map<String,Integer> countMap = messageDao.countNewMessages(tid, user, start);

        int recCount =  retrieveNumber(Publisher.RECOMMEND_TYPE,countMap);
        int recCommentCount = retrieveNumber(Publisher.RECOMMEND_COMMENT,countMap);
        int myPageCommentCount = retrieveNumber(Publisher.MY_PAGE_COMMENT,countMap);

        int subPageCount = retrieveNumber(Publisher.PAGE_TYPE,countMap);
        int subPersonCount = retrieveNumber(Publisher.PERSON_TYPE,countMap);
        int subCommentCount = retrieveNumber(Publisher.FEED_COMMENT,countMap);

        int totalRecCount = recCount + recCommentCount+myPageCommentCount;
        int totalSubCount = subPageCount+subPersonCount+subCommentCount;
        int totalCount = totalRecCount + totalSubCount;
        return new int[] {totalRecCount,totalSubCount,totalCount};
    }

    @Override
    public void updateRecommendTypeMessages(String user,int tid) {
        messageDao.updateTypeMessageStatus(user, Publisher.RECOMMEND_COMMENT,tid);
        messageDao.updateTypeMessageStatus(user, Publisher.RECOMMEND_TYPE,tid);
        messageDao.updateTypeMessageStatus(user, Publisher.MY_PAGE_COMMENT,tid);
    }

    @Override
    public void updateFeedTypeMessages(String user,int tid) {
        messageDao.updateTypeMessageStatus(user, Publisher.FEED_COMMENT,tid);
        messageDao.updateTypeMessageStatus(user, Publisher.PAGE_TYPE,tid);
        messageDao.updateTypeMessageStatus(user, Publisher.PERSON_TYPE,tid);
    }
}
