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

package net.duckling.ddl.service.export;


/**
 * @date 2011-8-10
 * @author Clive Lee
 */
public class FileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int fid;

    private String teamName;

    private String redirectURL;

    /**
     * @return the fid
     */
    public int getFid() {
        return fid;
    }

    /**
     * @param fid the fid to set
     */
    public void setFid(int fid) {
        this.fid = fid;
    }

    /**
     * @return the teamName
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * @param teamName the teamName to set
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * @return the redirectURL
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * @param redirectURL the redirectURL to set
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public FileNotFoundException(int fid,String teamName,String redirectURL) {
        this.fid = fid;
        this.teamName = teamName;
        this.redirectURL = redirectURL;
    }
    public FileNotFoundException(int fid,String teamName,String redirectURL,String message) {
        super(message);
        this.fid = fid;
        this.teamName = teamName;
        this.redirectURL = redirectURL;
    }

    public String toString() {
        return "没有找到编号为" + fid + "对应的文件";
    }

}
