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
package net.duckling.ddl.web.bean;

public class SimpleResourceKey {
    private int rid;
    private String itemType;
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getRid() {
        return rid;
    }
    public void setRid(int rid) {
        this.rid = rid;
    }
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SimpleResourceKey)){
            return false;
        }
        SimpleResourceKey s = (SimpleResourceKey)obj;
        if(rid!=s.rid){
            return false;
        }
        if(itemType==s.itemType||(itemType!=null&&itemType.equals(s.itemType))){
            return true;
        }
        return false;
    }
    @Override
    public int hashCode() {
        int result = rid;
        result = result*31+(itemType==null?0:itemType.hashCode());
        return result;
    }
}
