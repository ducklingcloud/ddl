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
package net.duckling.ddl.service.lottery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.duckling.ddl.service.lottery.model.GiftPlan;

public class LotteryStrategy {

    public static void main(String[] args) {
        LotteryStrategy td = new LotteryStrategy();
        td.test();
    }

    /*
     * 每个ip限制10次 pool 个人空间同步版100M 2000个 团队空间100M 400个 马克杯 80个 小米电源 60个 小米健康手环
     * 30个 小米路由器mini 20个 小米手机4 1个
     */

    public String[] names = { "个人空间同步版100M", "团队空间100M", "乐扣马克杯", "小米电源", "小米健康手环", "小米路由器mini", "小米手机4" };
    public int[] counts = { 2000, 400, 6, 4, 2, 1, 0 };

    public GiftPlan initGiftPool() {
        GiftPlan pool = new GiftPlan();
        pool.setPool(new HashMap<Integer, Integer>());
        pool.setDay("2014-10-23");
        for (int i = 0; i < 7; i++) {
            // Gift g = new Gift();
            // g.setLevel(7 - i);
            // g.setName(names[i]);
            // g.setCount(counts[i]);
            pool.getPool().put(7 - i, counts[i]);
        }
        return pool;
    }

    public int[] drawMap(int total, GiftPlan pool) {
        int[] sequence = new int[total];
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = 0;
        }
        Random rand = new Random();
        Set<Integer> exist = new HashSet<Integer>();
        for (int j = 0; j < pool.getPool().size(); j++) {
            int tmp = pool.getPool().get(j);
            while (tmp > 0) {
                int num = rand.nextInt(total);
                if (exist.contains(num)) {
                    continue;
                } else {
                    exist.add(num);
                    sequence[num] = j;
                    tmp--;
                }
            }
        }
        return sequence;
    }


    public void test() {
        GiftPlan pool = this.initGiftPool();
        int total = 3000;
        int[] result = this.drawMap(total, pool);
        for (int i = 0; i < result.length; i++) {
            if ((i + 1) % 80 == 0) {
                System.out.println();
            }
            System.out.print(result[i] + " ");
        }
    }

}
