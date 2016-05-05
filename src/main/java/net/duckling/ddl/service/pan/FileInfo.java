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
package net.duckling.ddl.service.pan;

import java.io.Serializable;

import net.duckling.meepo.api.PanAcl;

import org.apache.commons.lang.StringUtils;

public class FileInfo implements Serializable{
    
    private String sn;
    private String fileName;
    private long size;
    private String remotePath;
    private String uid;
    private String umtToken;
    private long version;

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileInfo(String sn, String fileName, String remotePath, long size,String uid, String umtToken, long version) {
        this.sn = sn;
        this.remotePath = remotePath;
        this.fileName = fileName;
        this.size = size;
        this.uid = uid;
        this.umtToken = umtToken;
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUmtToken() {
        return umtToken;
    }

    public void setUmtToken(String umtToken) {
        this.umtToken = umtToken;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



    public String getExt() {
        return StringUtils.substring(this.getFileName().toLowerCase(), this.getFileName().lastIndexOf(".") + 1);
    }


    public long getSize() {
        return this.size;
    }

    public String getSHA256(){
        return "";
    }

    public String getOwaServerUrl(String owaServerDomain, String mode, String wopiSrc,  String accessToken) {
        OwaConfig oc = OwaConfig.getInstance();
        String tempMode = mode;
        if (StringUtils.isEmpty(mode)) {
            tempMode = "view";
        }
        String path =  "http://" + owaServerDomain + oc.getServiceUrl(tempMode, this.getExt()) + "WOPISrc=" + wopiSrc +"&access_Token=" + accessToken;
        return path;
    }
    
    public PanAcl getPanAcl(){
        PanAcl acl = new PanAcl();
        acl.setUid(uid);
        acl.setUmtToken(umtToken);
        return acl;
    }

}
