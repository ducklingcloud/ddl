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

import java.util.Map;

import net.duckling.ddl.service.user.SimpleUser;


/**
 * @date Apr 1, 2011
 * @author xiejj@cnic.cn
 */
public class MultiSenderDisplay extends MessageDisplay {

    private Map<String,SimpleUser> senderMap;
    private int pageId;
    private String title;
    /**
     * @return the senderMap
     */
    public Map<String, SimpleUser> getSenderMap() {
        return senderMap;
    }

    /**
     * @param senderMap the senderMap to set
     */
    public void setSenderMap(Map<String, SimpleUser> senderMap) {
        this.senderMap = senderMap;
    }

    /**
     * @return the pageId
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * @param pageId the pageId to set
     */
    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
