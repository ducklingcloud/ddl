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

public class TagItem {

    private int id;
    private int tid;
    private int tgid;
    private int rid;

    /**
     * @return the rid
     */
    public int getRid() {
        return rid;
    }
    /**
     * @param rid the rid to set
     */
    public void setRid(int rid) {
        this.rid = rid;
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
     * @return the tgid
     */
    public int getTgid() {
        return tgid;
    }
    /**
     * @param tgid the tgid to set
     */
    public void setTgid(int tgid) {
        this.tgid = tgid;
    }
}
