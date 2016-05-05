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

import java.util.List;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.user.SimpleUser;
import net.duckling.ddl.service.user.UserExt;

/**
 * @date Mar 31, 2011
 * @author xiejj@cnic.cn
 */
public abstract class MessageUpdater {
    public abstract MessageDisplay create(Message message);

    public abstract void update(MessageDisplay display, Message message);

    protected SimpleUser convertToSimpleUser(UserExt ext) {
        SimpleUser su = new SimpleUser();
        su.setUid(ext.getUid());
        su.setName(ext.getName());
        su.setId(ext.getId());
        return su;
    }

    @SuppressWarnings("unused")
    public void afterMerge(List<MessageDisplay> results) {

    }

    public abstract List<Message> beforeMerge(List<Message> messages);

    public Object getMessageKey(Message message) {
        return message.getBody().getRid();
    }
}
