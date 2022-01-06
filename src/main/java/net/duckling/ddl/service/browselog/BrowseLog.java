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

package net.duckling.ddl.service.browselog;

import java.util.Date;

/**
 * @date 2011-2-28
 * @author Clive Lee
 */
public class BrowseLog {
    private Date browseTime;
    private String displayName;

    private int rid;

    private String itemType;

    private int tid;

    private String trackingId;
    private String userId;

    public BrowseLog() {
        this.setBrowseTime(new Date());
    }

    public Date getBrowseTime() {
        return browseTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRid() {
        return rid;
    }

    public String getItemType() {
        return itemType;
    }

    public int getTid() {
        return tid;
    }

    public String getTrackingId() {
        if (trackingId == null) {
            trackingId = userId + "-" + rid;
        }
        return trackingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setBrowseTime(Date browseTime) {
        if (browseTime != null) {
            this.browseTime = browseTime;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
