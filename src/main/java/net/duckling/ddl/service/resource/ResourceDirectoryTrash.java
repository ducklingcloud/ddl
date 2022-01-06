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
package net.duckling.ddl.service.resource;

import java.util.Date;

public class ResourceDirectoryTrash {
    private int id;
    private int rid;
    private int tid;
    private String uid;
    private Date deleteDate;
    private ResourceDirectoryTree ancestors;
    private ResourceDirectoryTree descendants;
    public int getRid() {
        return rid;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }
    public int getTid() {
        return tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public Date getDeleteDate() {
        return deleteDate;
    }
    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }
    public ResourceDirectoryTree getAncestors() {
        return ancestors;
    }
    public void setAncestors(ResourceDirectoryTree ancestors) {
        this.ancestors = ancestors;
    }
    public ResourceDirectoryTree getDescendants() {
        return descendants;
    }
    public void setDescendants(ResourceDirectoryTree descendants) {
        this.descendants = descendants;
    }

    public static ResourceDirectoryTrash build(int rid,int tid,String uid ,ResourceDirectoryTree ancestors,ResourceDirectoryTree descendants){
        ResourceDirectoryTrash r = new ResourceDirectoryTrash();
        r.setRid(rid);
        r.setTid(tid);
        r.setUid(uid);
        r.setDeleteDate(new Date());
        r.setAncestors(ancestors);
        r.setDescendants(descendants);
        return r;
    }

}
