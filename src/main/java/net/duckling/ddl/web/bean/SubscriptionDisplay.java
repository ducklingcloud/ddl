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
public class SubscriptionDisplay extends MultiSenderDisplay {

    private List<Integer> diffList;
    private List<String> operationList; //xxx:修改了 BB:新建了
    private List<Date> timeList;
    private String digest;


    public List<Integer> getDiffList() {
        return diffList;
    }

    public void setDiffList(List<Integer> diffList) {
        this.diffList = diffList;
    }

    public List<String> getOperationList() {
        return operationList;
    }

    public void setOperationList(List<String> operationList) {
        this.operationList = operationList;
    }

    public List<Date> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<Date> timeList) {
        this.timeList = timeList;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public static MessageDisplay getInstance(Message instance) {
        SubscriptionDisplay result = new SubscriptionDisplay();
        result.setCreateTime(instance.getBody().getTime());
        result.setPageId(instance.getBody().getRid());
        result.setDigest(instance.getBody().getDigest());
        result.setReceiver(instance.getUserId());
        result.setTitle(instance.getBody().getTitle());
        result.setType(MessageDisplay.SUBSCRIPTION);
        result.setStatus(instance.getStatus());
        result.setDiffList(new ArrayList<Integer>());
        result.setOperationList(new ArrayList<String>());
        result.setTimeList(new ArrayList<Date>());
        result.setSenderMap(new HashMap<String,SimpleUser>());
        return result;
    }

}
