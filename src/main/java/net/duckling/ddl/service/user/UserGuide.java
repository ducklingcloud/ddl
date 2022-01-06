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
package net.duckling.ddl.service.user;

public class UserGuide {

    public static final String MODULE_DASHBOARD = "dashboard";
    public static final String MODULE_TEAM = "team";

    private int id;
    private String uid;
    private String module;
    private int step;
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
     * @return the uid
     */
    public String getUid() {
        return uid;
    }
    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }
    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
    }
    /**
     * @return the step
     */
    public int getStep() {
        return step;
    }
    /**
     * @param step the step to set
     */
    public void setStep(int step) {
        this.step = step;
    }

    public static UserGuide build(String uid, String module){
        UserGuide ug = new UserGuide();
        ug.setUid(uid);
        ug.setModule(module);
        ug.setStep(0);
        return ug;
    }
}
