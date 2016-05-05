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

package net.duckling.ddl.service.authority;

/**
 * @date 2011-5-27
 * @author Clive Lee
 */
public class TeamAcl {

    private String tid;
    private String teamName;
    private String uid;
    private String userName;
    private String auth;

    /**
     * @return the tid
     */
    public String getTid() {
	return tid;
    }

    /**
     * @param tid
     *            the tid to set
     */
    public void setTid(String tid) {
	this.tid = tid;
    }

    /**
     * @return the teamName
     */
    public String getTeamName() {
	return teamName;
    }

    /**
     * @param teamName
     *            the teamName to set
     */
    public void setTeamName(String teamName) {
	this.teamName = teamName;
    }

    /**
     * @return the uid
     */
    public String getUid() {
	return uid;
    }

    /**
     * @param uid
     *            the uid to set
     */
    public void setUid(String uid) {
	this.uid = uid;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
	return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

    /**
     * @return the auth
     */
    public String getAuth() {
	return auth;
    }

    /**
     * @param auth
     *            the auth to set
     */
    public void setAuth(String auth) {
	this.auth = auth;
    }

}
