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

package net.duckling.ddl.service.devent;

import java.util.Date;

import net.duckling.ddl.util.DateUtil;


/**
 * @date 2011-10-31
 * @author clive
 */
public class Notice {

    private int id;
    private int tid;
    private int eventId;
    private DEntity actor;
    private DAction operation;
    private DEntity target;
    private String message;
    private DEntity relative;
    private Date occurTime;
    private String recipient;
    private String addition;
    private String reason;
    private String noticeType;
    private int targetVersion;

    //----------Extend Fields-------------//
    private String additionDisplay;
    private String noticeStatus;
    public String getOccurTimeStr(){
        if(occurTime==null){
            return "";
        }else{
            return DateUtil.getTime(this.occurTime);
        }
    }

    /**
     * @return the noticeStatus
     */
    public String getNoticeStatus() {
        return noticeStatus;
    }
    /**
     * @param noticeStatus the noticeStatus to set
     */
    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }
    /**
     * @return the additionDisplay
     */
    public String getAdditionDisplay() {
        return additionDisplay;
    }
    /**
     * @param additionDisplay the additionDisplay to set
     */
    public void setAdditionDisplay(String additionDisplay) {
        this.additionDisplay = additionDisplay;
    }
    /**
     * @return the targetVersion
     */
    public int getTargetVersion() {
        return targetVersion;
    }
    /**
     * @param targetVersion the targetVersion to set
     */
    public void setTargetVersion(int targetVersion) {
        this.targetVersion = targetVersion;
    }
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
     * @return the tid
     */
    public int getTid() {
        return tid;
    }
    /**
     * @param tid the tid to set
     */
    public void setTid(int tid) {
        this.tid = tid;
    }
    /**
     * @return the eventId
     */
    public int getEventId() {
        return eventId;
    }
    /**
     * @param eventId the eventId to set
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    /**
     * @return the actor
     */
    public DEntity getActor() {
        return actor;
    }
    /**
     * @param actor the actor to set
     */
    public void setActor(DEntity actor) {
        this.actor = actor;
    }
    /**
     * @return the operation
     */
    public DAction getOperation() {
        return operation;
    }
    /**
     * @param operation the operation to set
     */
    public void setOperation(DAction operation) {
        this.operation = operation;
    }
    /**
     * @return the target
     */
    public DEntity getTarget() {
        return target;
    }
    /**
     * @param target the target to set
     */
    public void setTarget(DEntity target) {
        this.target = target;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * @return the relative
     */
    public DEntity getRelative() {
        return relative;
    }
    /**
     * @param relative the relative to set
     */
    public void setRelative(DEntity relative) {
        this.relative = relative;
    }
    /**
     * @return the occurTime
     */
    public Date getOccurTime() {
        return occurTime;
    }
    /**
     * @param occurTime the occurTime to set
     */
    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }
    /**
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }
    /**
     * @param recipient the recipient to set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    /**
     * @return the addition
     */
    public String getAddition() {
        return addition;
    }
    /**
     * @param addition the addition to set
     */
    public void setAddition(String addition) {
        this.addition = addition;
    }
    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
    /**
     * @return the noticeType
     */
    public String getNoticeType() {
        return noticeType;
    }
    /**
     * @param noticeType the noticeType to set
     */
    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tid=").append(tid).append(";eventId=").append(eventId);
        sb.append(";recipient=").append(recipient);
        sb.append(";actor=[").append(actor);
        sb.append("];operation=[").append(operation);
        sb.append("];target=[").append(target);
        sb.append("];reason=").append(reason);
        sb.append(";message=").append(message);
        sb.append(";occurTime=").append(occurTime);
        sb.append(";addtion=").append(addition);
        sb.append(";relative=[").append(relative);
        sb.append("];");
        return sb.toString();
    }

}
