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
import java.util.HashMap;
import java.util.List;

import net.duckling.ddl.service.subscribe.Message;
import net.duckling.ddl.service.user.SimpleUser;


/**
 * @date 2011-3-23
 * @author Clive Lee
 */
public class CommentDisplay extends MultiSenderDisplay {
    //对comment的展现类
    private List<String> contentList; //所有评论内容
    private List<Date> timeList;    //评论时间

    /**
     * @return the contentList
     */
    public List<String> getContentList() {
        return contentList;
    }
    /**
     * @param contentList the contentList to set
     */
    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }
    /**
     * @return the timeList
     */
    public List<Date> getTimeList() {
        return timeList;
    }
    /**
     * @param timeList the timeList to set
     */
    public void setTimeList(List<Date> timeList) {
        this.timeList = timeList;
    }

    public static MessageDisplay getInstance(Message instance) {
        CommentDisplay result = new CommentDisplay();
        result.setCreateTime(instance.getBody().getTime());
        result.setPageId(instance.getBody().getRid());
        result.setReceiver(instance.getUserId());
        result.setTitle(instance.getBody().getTitle());
        result.setType(instance.getPublisher().getType());
        result.setStatus(instance.getStatus());
        result.setContentList(new ArrayList<String>());
        result.setTimeList(new ArrayList<Date>());
        result.setSenderMap(new HashMap<String,SimpleUser>());
        return result;
    }

}
