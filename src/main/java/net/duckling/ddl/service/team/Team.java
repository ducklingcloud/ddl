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

package net.duckling.ddl.service.team;

import java.io.Serializable;
import java.util.Date;




/**
 * @date 2011-3-1
 * @author Clive Lee
 */
public class Team implements Serializable {

    private static final long serialVersionUID=1l;

    private Date createTime;

    private String creator;

    private String description;

    private String displayName;

    private int id;

    private String name;

    private String type;

    private String accessType;

    private String defaultMemberAuth;

    private String vmtdn;
    private String teamDefaultView;


    public static final String PESONAL_TEAM = "personal";
    public static final String COMMON_TEAM = "common";
    public static final String CONFERENCE_TEAM = "conference"; //csp 会议专用团队：不可以用户管理、用户不能退出团队、团队数量不作限制。

    public static final String ACCESS_PUBLIC = "public";
    public static final String ACCESS_PRIVATE = "private";
    public static final String ACCESS_PROTECTED = "protected";

    public static final String AUTH_VIEW = "view";
    public static final String AUTH_EDIT = "edit";
    public static final String AUTH_ADMIN = "admin";

    public static final String DEFAULT_TEAM_VIEW_LIST = "list";
    public static final String DEFAULT_TEAM_VIEW_NOTIC = "notice";


    public String getTeamDefaultView() {
        return teamDefaultView;
    }

    public void setTeamDefaultView(String teamDefaultView) {
        this.teamDefaultView = teamDefaultView;
    }

    public boolean isPublicTeam(){
        return ACCESS_PUBLIC.equals(getAccessType());
    }

    public boolean isPersonalTeam(){
        return PESONAL_TEAM.equals(this.type);
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the accessType
     */
    public String getAccessType() {
        return accessType;
    }

    /**
     * @param accessType the accessType to set
     */
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    /**
     * @return the defaultMemberAuth
     */
    public String getDefaultMemberAuth() {
        return defaultMemberAuth;
    }

    /**
     * @param defaultMemberAuth the defaultMemberAuth to set
     */
    public void setDefaultMemberAuth(String defaultMemberAuth) {
        this.defaultMemberAuth = defaultMemberAuth;
    }

    private String prefix;

    private TeamState state;

    public Date getCreateTime() {
        return createTime;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }
    public TeamState getState() {
        return state;
    }
    public boolean isHangup() {
        return TeamState.HANGUP.equals(state);
    }

    public boolean isWorking() {

        boolean flag = TeamState.WORK.equals(state);
        return flag;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setState(TeamState state) {
        this.state = state;
    }

    public String getVmtdn() {
        return vmtdn;
    }

    public void setVmtdn(String vmtdn) {
        this.vmtdn = vmtdn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[teamCode=").append(getName());
        sb.append(",teamName=").append(getDisplayName());
        sb.append(",teamId=").append(getId());
        sb.append(",vmtdn=").append(getVmtdn());
        sb.append(",state=").append(getState());
        sb.append(",type=").append(getType());
        sb.append(",createTime=").append(getCreateTime());
        sb.append(",prefix=").append(getPrefix());
        sb.append(",description=").append(getDescription());
        sb.append("]");
        return sb.toString();
    }
}
