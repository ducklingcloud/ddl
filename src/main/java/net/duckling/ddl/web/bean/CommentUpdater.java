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
import java.util.List;
import java.util.Map;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.user.SimpleUser;


/**
 * @date Mar 31, 2011
 * @author xiejj@cnic.cn
 */
public class CommentUpdater extends MessageUpdater{
    private Map<String, SimpleUser> cacheUser;
    private boolean feedComment;

    public CommentUpdater(Map<String, SimpleUser> cacheUser, boolean feedComment){
        this.cacheUser = cacheUser;
        this.feedComment =feedComment;
    }
    public MessageDisplay create(Message message) {
        return CommentDisplay.getInstance(message);
    }
    public void update(MessageDisplay display, Message instance) {
        CommentDisplay currDisplay =(CommentDisplay)display;
        //存推荐的评论
        String senderId = instance.getBody().getFrom();
        currDisplay.getSenderMap().put(senderId, cacheUser.get(senderId));
        currDisplay.getContentList().add(instance.getBody().getDigest());
        currDisplay.getTimeList().add(instance.getBody().getTime());
    }
    @Override
    public void afterMerge(List<MessageDisplay> results) {
        //DO nothing
    }
    @Override
    public List<Message> beforeMerge(List<Message> messages) {
        List<Message> comments = new ArrayList<Message>();
        for (Message current : messages) {
            if (feedComment){
                if(Publisher.isFeedComment(current.getPublisher())){
                    comments.add(current);
                }
            }else{
                if(Publisher.isRecommendComment(current.getPublisher())){
                    comments.add(current);
                }
            }
        }
        return comments;
    }
}
