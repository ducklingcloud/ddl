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
package net.duckling.ddl.service.search;

/**
 * @author lizexin 2012-09-01
 *
 */
public class SearchReocrd {
    private String bid;
    private String fid;
    private String ip;
    private String uid;
    private String host;
    private String time;
    private String keyword;
    private String tid;
    private String method;
    private String referer;
    private String cururl;
    private String type ;
    private String pid  ;
    private String rank;
    private String flag;
    private String client;
    private String href;
    private int seq;
    private String date;
    private String oper_name ;

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getTid() {
        if(tid == null ||tid.isEmpty() || tid.contains("null") ||tid.contains("NULL")){
            return "0";
        }
        return tid;
    }
    public void setTid(String tid) {
        this.tid = tid;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getReferer() {
        return referer;
    }
    public void setReferer(String referer) {
        this.referer = referer;
    }
    public String getCururl() {
        return cururl;
    }
    public void setCururl(String cururl) {
        this.cururl = cururl;
    }

    public String getOpername() {
        if(oper_name == null || oper_name.isEmpty()){
            return "N";
        }
        return oper_name;

    }

    public String getPid() {
        if(pid == null||pid.isEmpty()||pid.contains("NULL")){
            return "-1";
        }
        if(pid.contains("?")){
            return pid.substring(0, pid.indexOf("?"));
        }
        else{
            return pid;
        }
    }
    public void setPid(String pid) {
        if(pid == null){
            this.pid="-1";
        }
        else{
            this.pid = pid;
        }
    }
    public String getRank() {
        if(rank == null ||rank.isEmpty()||rank.contains("null")||rank.contains("NULL")){
            return "-1";
        }
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getType() {
        if(type == null){
            return this.type;
        }
        if(type.equals("file")){
            return "DFile";
        }
        if(type.equals("page")){
            return "DPage";
        }
        else
            return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFlag() {
        return flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getClient() {
        return client;
    }
    public void setClient(String client) {
        this.client = client;
    }
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }
    public int getSeq() {
        return seq;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getOper_name() {
        return oper_name;
    }
    public void setOper_name(String oper_name) {
        this.oper_name = oper_name;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getBid() {
        return bid;
    }
    public void setBid(String bid) {
        this.bid = bid;
    }
    public String getFid() {
        return fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }


}
