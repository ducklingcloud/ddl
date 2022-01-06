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

package net.duckling.ddl.service.invitation;

import java.util.Date;

import net.duckling.ddl.util.AoneTimeUtils;
import net.duckling.ddl.util.EncodeUtil;
import net.duckling.ddl.util.StatusUtil;


/**
 * @date 2011-6-16
 * @author Clive Lee
 */
public class Invitation {

    private int id;
    private String encode;
    private String inviter;
    private String inviterName;
    private String invitee;
    private int teamId;
    private String teamName; //冗余字段
    private String teamDisplayName; //冗余字段
    private String inviteTime;
    private String acceptTime;
    private String status;
    private String displayURL;
    private String message;

    public String getTeamDisplayName() {
        return teamDisplayName;
    }
    public void setTeamDisplayName(String teamDisplayName) {
        this.teamDisplayName = teamDisplayName;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getInviterName() {
        return inviterName;
    }
    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviteTime() {
        return inviteTime;
    }
    public void setInviteTime(String inviteTime) {
        this.inviteTime = inviteTime;
    }
    public String getAcceptTime() {
        return acceptTime;
    }
    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }
    public int getTeamId() {
        return teamId;
    }
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEncode() {
        return encode;
    }
    public void setEncode(String encode) {
        this.encode = encode;
    }
    public String getInviter() {
        return inviter;
    }
    public void setInviter(String inviter) {
        this.inviter = inviter;
    }
    public String getInvitee() {
        return invitee;
    }
    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDisplayURL() {
        return displayURL;
    }
    public void setDisplayURL(String displayURL) {
        this.displayURL = displayURL;
    }

    public static Invitation getInstance(String inviter,String invitee,String teamName,int teamId) {
        Invitation instance = new Invitation();
        instance.setInviter(inviter);
        instance.setInvitee(invitee);
        instance.setTeamName(teamName);
        instance.setTeamId(teamId);
        instance.setEncode(EncodeUtil.generateEncode());
        instance.setStatus(StatusUtil.WAITING);
        instance.setInviteTime(AoneTimeUtils.formatToDateTime(new Date()));
        return instance;
    }

}
