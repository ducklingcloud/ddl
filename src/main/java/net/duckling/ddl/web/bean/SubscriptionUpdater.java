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

package net.duckling.ddl.web.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.user.SimpleUser;


/**
 * @date Mar 31, 2011
 * @author xiejj@cnic.cn
 */
public class SubscriptionUpdater extends MessageUpdater{
    private Map<String, SimpleUser> cacheUser;
    private static final int LATEST_SIZE=4;
    public SubscriptionUpdater(Map<String, SimpleUser> cacheUser){
        this.cacheUser = cacheUser;
    }
    public MessageDisplay create(Message message) {
        return SubscriptionDisplay.getInstance(message);
    }
    public void update(MessageDisplay display, Message message) {
        SubscriptionDisplay currDisplay =(SubscriptionDisplay)display;
        String senderId = getAuthorId(message.getBody().getFrom());
        currDisplay.getTimeList().add(message.getBody().getTime());
        currDisplay.getSenderMap().put(senderId,cacheUser.get(senderId));
        currDisplay.getOperationList().add(
            getOperationDescription(currDisplay.getSenderMap()
                                    .get(senderId).getName(), message));
        if(message.getBody().getRemark()!=null) {
            int version = Integer.parseInt(message.getBody().getRemark());
            currDisplay.getDiffList().add(version);
        }
    }
    public void afterMerge(List<MessageDisplay> messages) {
        for(MessageDisplay curr:messages) {
            getLatestRecords((SubscriptionDisplay)curr,LATEST_SIZE);
        }
    }
    public List<Message> beforeMerge(List<Message> messages){
        List<Message> subscriptions = new ArrayList<Message>();
        for (Message current : messages) {
            if(Publisher.isPage(current.getPublisher()))
                subscriptions.add(current);
        }
        return subscriptions;
    }
    private String getAuthorId(String str) {
        if(str!=null && str.contains("(")){
            return str.substring(str.indexOf('(')+1,str.indexOf(')'));
        }else{
            return str;
        }
    }
    private SubscriptionDisplay getLatestRecords(SubscriptionDisplay src,int size){
        List<Date> tempTimeList = new ArrayList<Date>();
        List<String> tempOperList = new ArrayList<String>();
        for(int i=0;i<size;i++) {
            if(i>=src.getTimeList().size()){
                break;
            }
            tempTimeList.add(src.getTimeList().get(i));
            tempOperList.add(src.getOperationList().get(i));
        }
        src.setTimeList(tempTimeList);
        src.setOperationList(tempOperList);
        return null;
    }
    private String getOperationDescription(String realName,Message instance) {
        String result = realName+" ";
        if("PageModified".equals(instance.getBody().getType())){
            result += "修改于";
        }
        if("PageCreated".equals(instance.getBody().getType())){
            result += "新建于";
        }
        return result;
    }
}
