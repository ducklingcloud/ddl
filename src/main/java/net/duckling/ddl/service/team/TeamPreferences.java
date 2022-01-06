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

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.duckling.ddl.service.devent.NoticeRule;


/**
 * @date 2011-11-10
 * @author clive
 */
public class TeamPreferences {
    public static final String ID_SPLIT_CHAR=";";
    private int id;
    private String uid;
    private int tid;
    private int sequence;
    private Date personAccess;
    private Date teamAccess;
    private Date monitorAccess;

    private int teamNoticeCount;
    private int personNoticeCount;
    private int monitorNoticeCount;

    private String createdby;
    private Date createtime;
    private Set<Integer> teamEventIds;
    private Set<Integer> personEventIds;
    private Set<Integer> monitorEventIds;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTeamEventIds() {
        return setToString(teamEventIds);
    }
    public Set<Integer> getTeamEventIdsSet() {
        return teamEventIds;
    }
    public void setTeamEventIds(String teamEventIds) {
        this.teamEventIds = stringToSet(teamEventIds);
    }
    public String getPersonEventIds() {
        return setToString(personEventIds);
    }
    public Set<Integer> getPersonEventIdsSet() {
        return personEventIds;
    }
    public void setPersonEventIds(String personEventIds) {
        this.personEventIds = stringToSet(personEventIds);
    }
    public String getMonitorEventIds() {
        return setToString(monitorEventIds);
    }
    public Set<Integer> getMonitorEventIdsSet() {
        return monitorEventIds;
    }
    public void setMonitorEventIds(String monitorEventIds) {
        this.monitorEventIds = stringToSet(monitorEventIds);
    }
    public int getPersonNoticeCount() {
        return personNoticeCount;
    }
    public void setPersonNoticeCount(int personNoticeCount) {
        this.personNoticeCount = personNoticeCount;
    }
    public int getMonitorNoticeCount() {
        return monitorNoticeCount;
    }
    public void setMonitorNoticeCount(int monitorNoticeCount) {
        this.monitorNoticeCount = monitorNoticeCount;
    }

    public int getTeamNoticeCount() {
        return teamNoticeCount;
    }
    public void setTeamNoticeCount(int teamNoticeCount) {
        this.teamNoticeCount = teamNoticeCount;
    }

    public Date getMonitorAccess() {
        return monitorAccess;
    }
    public void setMonitorAccess(Date monitorAccess) {
        this.monitorAccess = monitorAccess;
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
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    public Date getPersonAccess() {
        return personAccess;
    }
    public void setPersonAccess(Date personAccess) {
        this.personAccess = personAccess;
    }
    public Date getTeamAccess() {
        return teamAccess;
    }
    public void setTeamAccess(Date teamAccess) {
        this.teamAccess = teamAccess;
    }
    public String getCreatedby() {
        return createdby;
    }
    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }
    public Date getCreatetime() {
        return createtime;
    }
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    private Set<Integer> stringToSet(String s) {
        Set<Integer> result = new LinkedHashSet<Integer>();
        if (s != null && !"".equals(s)) {
            String[] array = s.split(ID_SPLIT_CHAR);
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null && !"".endsWith(array[i])) {
                    try {
                        result.add(Integer.valueOf(array[i]));
                    } catch (Exception e) {

                    }
                }
            }
        }
        return result;
    }

    private String setToString(Set<Integer> set) {
        if (set == null||set.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append(ID_SPLIT_CHAR);
        }
        sb.delete(sb.length() - ID_SPLIT_CHAR.length(), sb.length());
        return sb.toString();
    }

    public void removeOneNotice(Set<Integer> eventIds,String type){
        for(Integer i : eventIds){
            removeOneNotice(i, type);
        }
    }
    public void removeNocice(int num,String type){
        if (NoticeRule.PERSON_NOTICE.equals(type)) {
            removeSet(personEventIds,num);
            if(personEventIds!=null){
                personNoticeCount = personEventIds.size();
            }
        } else if (NoticeRule.TEAM_NOTICE.equals(type)) {
            removeSet(teamEventIds, num);
            if(teamEventIds!=null){
                teamNoticeCount = teamEventIds.size();
            }
        } else {
            removeSet(monitorEventIds, num);
            if(monitorEventIds!=null){
                monitorNoticeCount = monitorEventIds.size();
            }
        }
    }

    private void removeSet(Set<Integer> set ,int count){
        if(set!=null&&!set.isEmpty()){
            Iterator<Integer> it = set.iterator();
            while(it.hasNext()&&count>0){
                it.next();
                it.remove();
                count--;
            }
        }
    }

    public void removeOneNotice(int eventId,String type){
        if (NoticeRule.PERSON_NOTICE.equals(type)) {
            removeNoticeId(personEventIds, eventId);
            if(personEventIds!=null){
                personNoticeCount = personEventIds.size();
            }
        } else if (NoticeRule.TEAM_NOTICE.equals(type)) {
            removeNoticeId(teamEventIds, eventId);
            if(teamEventIds!=null){
                teamNoticeCount = teamEventIds.size();
            }
        } else {
            removeNoticeId(monitorEventIds, eventId);
            if(monitorEventIds!=null){
                monitorNoticeCount = monitorEventIds.size();
            }
        }
    }

    private void removeNoticeId(Set<Integer> set,Integer i){
        if(set!=null&&!set.isEmpty()){
            set.remove(i);
        }
    }
}
