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

import net.duckling.ddl.service.user.SimpleUser;


/**
 * @date Apr 1, 2011
 * @author xiejj@cnic.cn
 */
public class PersonDisplay extends MessageDisplay {
    public static class Activity{
        private String act;
        private String title;
        private String url;
        private Date time;
        public void setAct(String act) {
            this.act = act;
        }
        public String getAct() {
            return act;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getUrl() {
            return url;
        }
        public void setTime(Date time) {
            this.time = time;
        }
        public Date getTime() {
            return time;
        }
    }

    private List<Activity> actions=new ArrayList<Activity>();
    private SimpleUser user;
    public List<Activity> getActions(){
        return this.actions;
    }
    public void setActions(List<Activity> actions){
        this.actions = actions;
    }
    public void addAction(String action, String description, String url, Date time){
        Activity activity = new Activity();
        activity.setAct(action);
        activity.setTime(time);
        activity.setTitle(description);
        activity.setUrl(url);
        this.actions.add(activity);
    }

    public void setFrom(SimpleUser user) {
        this.user = user;
    }
    public SimpleUser getFrom() {
        return user;
    }
}
