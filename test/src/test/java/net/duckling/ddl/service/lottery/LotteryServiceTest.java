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

import net.duckling.ddl.service.lottery.dao.DrawResultDaoImpl;
import net.duckling.ddl.service.lottery.dao.IDeliveryDao;
import net.duckling.ddl.service.lottery.dao.IDrawResultDao;

import org.junit.BeforeClass;
import org.junit.Test;

public class LotteryServiceTest {

    private static LotteryService ls = null;
    private static IDeliveryDao delivery = null;
    private static IDrawResultDao draw = null;

    @BeforeClass
    public static void setUp() throws Exception {
        ls = new LotteryService("/Users/clive/Desktop/lottery.yml", "10.10.2.7", 6379);
        draw = new DrawResultDaoImpl();
        ls.setDeliveryDao(delivery);
        ls.setDrawResultDao(draw);
    }

    String date = "2014-11-04";
    
    @Test
    public void testGenerate(){
        ls.reloadConfig();
        String[] array = ls.getLottery().generatorLotterySequence(date, null);
        int total = array.length;
        for(int i = 0; i< total; i++){
            System.out.print(array[i] + " ");
        }
    }

    // @Test
    public void test() {
        ls.generateSequnce(date, null);
        System.out.println(ls.queryLotteryResult(date, 1));
        System.out.println(ls.queryLotteryResult(date, 2));
        System.out.println(ls.queryLotteryResult(date, 3000));
        System.out.println(ls.queryLotteryResult(date, 8000));
        System.out.println(ls.queryLotteryResult(date, 8001));
    }

    // @Test
    public void testNatureSeq() {
        for (int i = 0; i < 100; i++) {
            System.out.println(ls.getNatureSeqence(date));
        }
    }

//    @Test
    public void testDraw() {
        String[] users = { "liji@cnic.cn", "liji@cstnet.cn", "hello@ict.cn", "what@cstnet.cn" };
        ls.draw(date, users[0], "127.0.0.1");
        ls.draw(date, users[1], "127.0.0.1");
        ls.draw(date, users[2], "127.0.0.1");
        ls.draw(date, users[3], "127.0.0.2");
    }

}
