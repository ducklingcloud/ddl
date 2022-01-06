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
 * 团队申请者
 * @author Yangxp
 * @since 2012-11-13
 */
public class TeamApplicant implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String STATUS_REJECT = "reject";
    public static final String STATUS_ACCEPT = "accept";
    public static final String STATUS_WAITING = "waiting";
    public static final String I_KNOW = "YES";
    public static final String I_DIDNT_KNOW = "NO";

    private int id;
    private String uid;
    private int tid;
    private String status;
    private String reason;
    private Date applyTime;
    private String iKnow;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Date getApplyTime() {
        return applyTime;
    }
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
    public String getiKnow() {
        return iKnow;
    }
    public void setiKnow(String iKnow) {
        this.iKnow = iKnow;
    }

    public static TeamApplicant build(String uid, int tid,
                                      String status, String reason, boolean iKnow){
        String iKnowStr = (iKnow)?I_KNOW:I_DIDNT_KNOW;
        TeamApplicant ta = new TeamApplicant();
        ta.setUid(uid);
        ta.setTid(tid);
        ta.setStatus(status);
        ta.setReason(reason);
        ta.setiKnow(iKnowStr);
        return ta;
    }
}
