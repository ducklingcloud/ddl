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
package net.duckling.ddl.service.lottery.model;

import java.util.Map;

public class GiftReport {

    private String date;
    private Map<Integer, Integer> plan;
    private Map<Integer, Integer> fact;
    private Map<Integer, Integer> left;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<Integer, Integer> getPlan() {
        return plan;
    }

    public void setPlan(Map<Integer, Integer> plan) {
        this.plan = plan;
    }

    public Map<Integer, Integer> getFact() {
        return fact;
    }

    public void setFact(Map<Integer, Integer> fact) {
        this.fact = fact;
    }

    public Map<Integer, Integer> getLeft() {
        return left;
    }

    public void setLeft(Map<Integer, Integer> left) {
        this.left = left;
    }

    public String toString() {
        return "Gift Report " + date + " \n" + "Plan:" + plan + "\n" + "Fact:" + fact + "\n" + "Left:" + left;
    }

}
