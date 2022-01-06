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

package net.duckling.ddl.service.subscribe;

import java.util.Date;




/**
 * @date 2011-2-28
 * @author Clive Lee
 */
public class Message extends TimedMessage{

    public static final Integer NEW_STATUS = 0;
    public static final Integer OLD_STATUS = 1;

    private int id;

    private MessageBody body;

    private Publisher publisher;

    private int status;

    private String userId;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public MessageBody getBody() {
        return body;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public int getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    public boolean isNewMessage(){
        return (this.status ==NEW_STATUS);
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Date getLastModified() {
        return body.getTime();
    }
}
