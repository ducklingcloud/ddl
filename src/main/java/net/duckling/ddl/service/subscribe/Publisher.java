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

/**
 * @date 2011-2-28
 * @author Clive Lee
 */
public class Publisher {

    private int id;

    private String type;

    private String name; //不存数据库字段

    private int rootPage; //不存数据库字段

    private String url;

    private int tid;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final String PERSON_TYPE = "person";

    public static final String PAGE_TYPE = "page";

    public static final String TEAM_TYPE = "team";

    public static final String RECOMMEND_TYPE = "recommend";

    public static final String ATTACH_TYPE = "attach";

    public static final String COMMENT_TYPE = "comment";

    public static final String RECOMMEND_COMMENT = "recommend_comment";

    public static final String FEED_COMMENT = "feed_comment";

    public static final String MY_CREATE_PAGE = "my_create_page";

    public static final String MY_PAGE_COMMENT = "my_page_comment";

    public Publisher() {

    }

    public Publisher(String type, int id) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRootPage() {
        return this.rootPage;
    }

    public void setRootPage(int rootPage) {
        this.rootPage = rootPage;
    }

    public boolean equals(Object other) {
        if (other == null){
            return false;
        }
        if (other == this){
            return true;
        }
        if (other instanceof Publisher) {
            Publisher publisher = (Publisher) other;
            return (type.equals(publisher.type) && id == publisher.getId());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (type.hashCode() + id);
    }

    public static boolean isSubscription(Publisher publisher) {
        return (publisher != null && (Publisher.PAGE_TYPE.equals(publisher
                                                                 .getType()) || Publisher.PERSON_TYPE
                                      .equals(publisher.getType())));
    }

    public static boolean isFeedComment(Publisher publisher) {
        return (publisher != null && Publisher.FEED_COMMENT.equals(publisher
                                                                   .getType()));
    }

    public static boolean isRecommendComment(Publisher publisher) {
        return publisher != null && (Publisher.RECOMMEND_COMMENT.equals(publisher.getType()) || Publisher.MY_PAGE_COMMENT.equals(publisher.getType()));
    }

    public static boolean isTeam(Publisher publisher) {
        return (publisher != null && Publisher.TEAM_TYPE.equals(publisher.getType()));
    }

    public static boolean isPage(Publisher publisher) {
        return (publisher != null && Publisher.PAGE_TYPE.equals(publisher
                                                                .getType()));
    }

    public static boolean isPerson(Publisher publisher) {
        return (publisher != null && Publisher.PERSON_TYPE.equals(publisher
                                                                  .getType()));
    }

    public static boolean isRecommend(Publisher publisher) {
        return (publisher != null && Publisher.RECOMMEND_TYPE.equals(publisher
                                                                     .getType()));
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }


}
