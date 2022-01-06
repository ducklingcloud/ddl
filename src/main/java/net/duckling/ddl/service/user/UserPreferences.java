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
package net.duckling.ddl.service.user;

public class UserPreferences {
    private int id;
    private String uid;
    private String refreshTeamMode;
    private int defaultTeam;
    private String accessHomeMode;

    public static final String REFRESH_TEAM_MODE_DEFAULT = "default";
    public static final String REFRESH_TEAM_MODE_AUTO = "auto";
    public static final String REFRESH_TEAM_MODE_CONFIG = "config";

    public static final String ACCESS_HOME_MODE_COMMON = "common";
    public static final String ACCESS_HOME_MODE_DYNAMIC = "dynamic";
    public static final String ACCESS_HOME_MODE_TAGITEMS = "tagitems";
    public static final String ACCESS_HOME_MODE_STARMARK = "starmark";

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }
    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
    /**
     * @return the refreshTeamMode
     */
    public String getRefreshTeamMode() {
        return refreshTeamMode;
    }
    /**
     * @param refreshTeamMode the refreshTeamMode to set
     */
    public void setRefreshTeamMode(String refreshTeamMode) {
        this.refreshTeamMode = refreshTeamMode;
    }
    /**
     * @return the defaultTeam
     */
    public int getDefaultTeam() {
        return defaultTeam;
    }
    /**
     * @param defaultTeam the defaultTeam to set
     */
    public void setDefaultTeam(int defaultTeam) {
        this.defaultTeam = defaultTeam;
    }
    /**
     * @return the accessHomeMode
     */
    public String getAccessHomeMode() {
        return accessHomeMode;
    }
    /**
     * @param accessHomeMode the accessHomeMode to set
     */
    public void setAccessHomeMode(String accessHomeMode) {
        this.accessHomeMode = accessHomeMode;
    }

    public static UserPreferences buildDefault(String uid){
        UserPreferences userPre = new UserPreferences();
        userPre.setUid(uid);
        userPre.setRefreshTeamMode(REFRESH_TEAM_MODE_DEFAULT);
        userPre.setDefaultTeam(0);
        userPre.setAccessHomeMode(ACCESS_HOME_MODE_COMMON);
        return userPre;
    }

    public static UserPreferences build(String uid, String rtm, int team, String ahm){
        UserPreferences userPre = new UserPreferences();
        userPre.setUid(uid);
        if(null!=rtm && REFRESH_TEAM_MODE_DEFAULT.equals(rtm)){
            userPre.setRefreshTeamMode(REFRESH_TEAM_MODE_DEFAULT);
            userPre.setDefaultTeam(0);
            userPre.setAccessHomeMode(ACCESS_HOME_MODE_COMMON);
        }else{
            userPre.setRefreshTeamMode(rtm);
            userPre.setDefaultTeam(team);
            userPre.setAccessHomeMode(ahm);
        }
        return userPre;
    }
}
