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

package net.duckling.ddl.service.relaterec;

/**
 * @date 2011-8-29
 * @author Clive Lee
 */
public class DGridItem {
    private int id;
    private int tid;
    private int gid;
    private int resourceId;
    private String resourceType;
    private int sequence;


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
     * @return the gid
     */
    public int getGid() {
        return gid;
    }
    /**
     * @param gid the gid to set
     */
    public void setGid(int gid) {
        this.gid = gid;
    }
    /**
     * @return the resourceId
     */
    public int getResourceId() {
        return resourceId;
    }
    /**
     * @param resourceId the resourceId to set
     */
    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
    /**
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }
    /**
     * @param resourceType the resourceType to set
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    /**
     * @return the sequence
     */
    public int getSequence() {
        return sequence;
    }
    /**
     * @param sequence the sequence to set
     */
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }



}
