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

public class GiftPlan {

    private Map<Integer, Integer> pool;
    private String day;
    private int estimate;

    public int getEstimate() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate = estimate;
    }

    public String getDay() {
        return day;
    }

    public Map<Integer, Integer> getPool() {
        return pool;
    }

    public void setPool(Map<Integer, Integer> pool) {
        this.pool = pool;
    }

    public void setDay(String day) {
        this.day = day;
    }

}
