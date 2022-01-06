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
package net.duckling.ddl.web.controller.team;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class TeamForm {
    @NotEmpty(message="团队网址不能为空")
    @Pattern(regexp="^[a-z0-9\\-]+$",message="团队网址只能包括小写英文字母、数字和中划线 ")
    @Size(min=2,message="输入的团队网址不能少于2个字符")
    private String teamId;
    @NotEmpty(message="团队名称不能为空")
    @Size(max=100,message="团队名称不能超过100个字符")
    @Pattern(regexp="[^<>:\"\\|?\\*/\\\\]+",message="请不要输入非法字符:?\\ /*<>|\":")
    private String teamName;
    private String teamDescription;
    private String accessType;
    private String defaultMemberAuth;

    private String auth;

    public String getAuth() {
        if(auth!=null&&!"".equals(auth)){
            return auth;
        }else{
            return defaultMemberAuth;
        }
    }
    public void setAuth(String auth) {
        this.auth = auth;
    }
    public String getTeamId() {
        return teamId;
    }
    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public String getTeamDescription() {
        return teamDescription;
    }
    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }
    public String getAccessType() {
        return accessType;
    }
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
    public String getDefaultMemberAuth() {
        return defaultMemberAuth;
    }
    public void setDefaultMemberAuth(String defaultMemberAuth) {
        this.defaultMemberAuth = defaultMemberAuth;
    }

}
