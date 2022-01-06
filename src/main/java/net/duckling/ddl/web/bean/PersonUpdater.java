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
import net.duckling.ddl.service.subscribe.MessageBody;
import net.duckling.ddl.service.subscribe.Publisher;
import net.duckling.ddl.service.url.URLGenerator;
import net.duckling.ddl.service.url.UrlPatterns;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.web.bean.PersonDisplay.Activity;

/**
 * @date Apr 1, 2011
 * @author xiejj@cnic.cn
 */
public class PersonUpdater extends MessageUpdater {
    public static final String EVENT_PAGE_CREATED = "PageCreated";
    public static final String EVENT_PAGE_MODIFIED = "PageModified";
    public static final String EVENT_PAGE_VISITED = "PageVisited";
    public static final String EVENT_ATTACH_CREATED = "AttachCreated";
    public static final String EVENT_ATTACH_UPDATED = "AttachUpdated";
    public static final String EVENT_TEAM_MEMBER_ADDED = "TeamMemberAdded";
    private Map<String, SimpleUser> cacheUser;
    private static final int ACTION_SIZE = 10;
    private URLGenerator urlGenerator;

    public PersonUpdater(URLGenerator urlGenerator,
                         Map<String, SimpleUser> cacheUser) {
        this.cacheUser = cacheUser;
        this.urlGenerator = urlGenerator;
    }

    @Override
    public List<Message> beforeMerge(List<Message> messages) {
        ArrayList<Message> filtered = new ArrayList<Message>();
        for (Message message : messages) {
            if (Publisher.isPerson(message.getPublisher())) {
                filtered.add(message);
            }
        }
        return filtered;
    }

    private void filterPersonActions(PersonDisplay src, int size) {
        List<Activity> acitonList = new ArrayList<Activity>();
        for (int i = 0; i < size; i++) {
            if (i >= src.getActions().size()) {
                break;
            }
            acitonList.add(src.getActions().get(i));
        }
        src.setActions(acitonList);
    }

    @Override
    public void afterMerge(List<MessageDisplay> messages) {
        for (MessageDisplay curr : messages) {
            filterPersonActions((PersonDisplay) curr, ACTION_SIZE);
        }
    }

    @Override
    public Object getMessageKey(Message message) {
        return message.getPublisher().getId();
    }

    @Override
    public MessageDisplay create(Message instance) {
        PersonDisplay result = new PersonDisplay();
        result.setCreateTime(instance.getBody().getTime());
        result.setReceiver(instance.getUserId());
        result.setType(instance.getPublisher().getType());
        result.setStatus(instance.getStatus());
        SimpleUser user = cacheUser.get(instance.getBody().getFrom());
        result.setFrom(user);
        return result;
    }

    @Override
    public void update(MessageDisplay display, Message message) {
        PersonDisplay person = (PersonDisplay) display;
        String action = getActionDescription(message.getBody().getType());
        MessageBody body = message.getBody();
        if (isAttachMessage(body)) {
            person.addAction(action, message.getBody().getTitle(),
                             body.getDigest(), message.getBody().getTime());
        } else {
            String url = urlGenerator.getURL(body.getTeamId(),
                                             UrlPatterns.T_PAGE, body.getRid() + "", null);
            person.addAction(action, message.getBody().getTitle(), url, message
                             .getBody().getTime());
        }
    }

    private boolean isAttachMessage(MessageBody body) {
        return EVENT_ATTACH_CREATED.equals(body.getType())
                || EVENT_ATTACH_UPDATED.equals(body.getType());
    }

    private String getActionDescription(String origin) {
        if (EVENT_PAGE_CREATED.equals(origin)) {
            return "创建了协作文档";
        }
        if (EVENT_PAGE_MODIFIED.equals(origin)) {
            return "修改了协作文档";
        }
        if (EVENT_ATTACH_CREATED.equals(origin) || "attach".equals(origin)) {
            return "上传了附件";
        }
        if (EVENT_ATTACH_UPDATED.equals(origin)) {
            return "更新了附件";
        }
        return "未知";
    }
}
