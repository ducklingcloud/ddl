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

package net.duckling.ddl.service.relaterec.impl;

/**
 * @date 2012-2-28
 * @author xiaomi
 * 用户与页面的相似度
 */
public class UserPageSimilar implements Comparable<UserPageSimilar>{
    private int pid;
    private double similar;
    /**
     * @return the pid
     */
    public int getPid() {
        return pid;
    }
    /**
     * @param pid the pid to set
     */
    public void setPid(int pid) {
        this.pid = pid;
    }
    /**
     * @return the similar
     */
    public double getSimilar() {
        return similar;
    }
    /**
     * @param similar the similar to set
     */
    public void setSimilar(double similar) {
        this.similar = similar;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(UserPageSimilar arg0) {
        // TODO Auto-generated method stub
        if(this.similar < arg0.similar){
            return -1;
        }
        return 1;
    }


}
