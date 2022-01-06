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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Lottery {

    private String name;
    private String startDate;
    private String endDate;
    private List<Gift> gifts;
    private int minReportLevel;
    private List<GiftPlan> dailyGiftPools;
    /**
     * @description 可重复中奖的等级
     */
    private int highLevel;
    /**
     * @description 调剂后的默认安慰奖
     */
    private int defaultLevel;

    /**
     * @description 每个ip能够访问的最大次数
     */
    private int maxAccessTime;
    /**
     * @description 是否有cd时间,测试时将其设置为false,线上一定为true
     */
    private boolean hasCoolDown;

    public int getMinReportLevel() {
        return minReportLevel;
    }

    public void setMinReportLevel(int minReportLevel) {
        this.minReportLevel = minReportLevel;
    }

    public int getMaxAccessTime() {
        return maxAccessTime;
    }

    public void setMaxAccessTime(int maxAccessTime) {
        this.maxAccessTime = maxAccessTime;
    }

    public boolean getHasCoolDown() {
        return hasCoolDown;
    }

    public void setHasCoolDown(boolean hasCoolDown) {
        this.hasCoolDown = hasCoolDown;
    }

    public int getHighLevel() {
        return highLevel;
    }

    public void setHighLevel(int highLevel) {
        this.highLevel = highLevel;
    }

    public int getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(int defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public List<GiftPlan> getDailyGiftPools() {
        return dailyGiftPools;
    }

    public void setDailyGiftPools(List<GiftPlan> dailyGiftPools) {
        this.dailyGiftPools = dailyGiftPools;
    }

    public List<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public GiftPlan findPlanByDate(String date) {
        GiftPlan currPlan = null;
        for (GiftPlan p : this.dailyGiftPools) {
            if (p.getDay().equals(date)) {
                currPlan = p;
            }
        }
        return currPlan;
    }

    private Map<Integer, Integer> mergePool(Map<Integer, Integer> planPool, Map<Integer, Integer> lastDayLeft) {
        if (lastDayLeft == null) {
            return planPool;
        }
        Map<Integer, Integer> merged = new HashMap<Integer, Integer>();
        for (Entry<Integer, Integer> item : planPool.entrySet()) {
            Integer leftCnt = lastDayLeft.get(item.getKey());
            if (leftCnt == null || leftCnt <= 0) {
                leftCnt = 0;
            }
            merged.put(item.getKey(), item.getValue() + leftCnt);
        }
        return merged;
    }

    private int getTotalCount(Map<Integer, Integer> lastDayPool) {
        int result = 0;
        if (lastDayPool == null) {
            return result;
        }
        for (Entry<Integer, Integer> item : lastDayPool.entrySet()) {
            int val = item.getValue();
            if (val <= 0) {
                val = 0;
            }
            result += val;
        }
        return result;
    }

    /**
     * @description 生成抽奖序列
     * @param date
     *            要生成的抽奖日期
     * @param lastDayLeft
     *            上一天留下来的奖品列表
     * @return 字符串数组 [0,1,2,3] 表示 第1,2,3,4个抽奖人中的是0,1,2,3等奖; 0等奖表示未中奖
     */
    public String[] generatorLotterySequence(String date, Map<Integer, Integer> lastDayLeft) {
        GiftPlan currPlan = this.findPlanByDate(date);
        if (currPlan == null) {
            return null;
        }

        Map<Integer, Integer> mergedPool = this.mergePool(currPlan.getPool(), lastDayLeft);
        int total = currPlan.getEstimate() + this.getTotalCount(lastDayLeft);
        String[] seqlist = new String[total];
        for (int i = 0; i < total; i++) {
            seqlist[i] = "0";
        }
        Random rand = new Random();
        Set<Integer> exist = new HashSet<Integer>();

        // generator sequence map
        for (Entry<Integer, Integer> item : mergedPool.entrySet()) {
            int level = item.getKey();
            int quantity = item.getValue();
            int tmp = quantity;
            while (tmp > 0) {
                int num = rand.nextInt(total);
                if (exist.contains(num)) {
                    continue;
                } else {
                    exist.add(num);
                    seqlist[num] = level + "";
                    tmp--;
                }
            }
        }

        return seqlist;
    }

    public Map<Integer, Gift> buildGiftMap() {
        Map<Integer, Gift> map = new HashMap<Integer, Gift>();
        for (Gift g : this.getGifts()) {
            map.put(g.getLevel(), g);
        }
        return map;
    }

}
