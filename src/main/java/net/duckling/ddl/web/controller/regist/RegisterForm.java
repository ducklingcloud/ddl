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
package net.duckling.ddl.web.controller.regist;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;


public class RegisterForm {

    @NotEmpty(message="Email不能为空")
    @Email(message="不符合Eamil格式")
    private String uid ;
    @NotEmpty(message="名称不能为空")
    private String name ;
    @NotEmpty(message="密码不能为空")
    @Size(min=6,message="密码不能少于六位")
    private String password=null;
    private String passwordAgain=null;
    private String joinGroupName ;

    public String getPasswordAgain() {
        return passwordAgain;
    }
    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getJoinGroupName() {
        return joinGroupName;
    }
    public void setJoinGroupName(String joinGroupName) {
        this.joinGroupName = joinGroupName;
    }

}
