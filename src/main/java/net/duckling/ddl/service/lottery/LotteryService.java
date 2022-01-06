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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.duckling.ddl.service.lottery.dao.IDeliveryDao;
import net.duckling.ddl.service.lottery.dao.IDrawResultDao;
import net.duckling.ddl.service.lottery.model.Delivery;
import net.duckling.ddl.service.lottery.model.DrawResponse;
import net.duckling.ddl.service.lottery.model.DrawResult;
import net.duckling.ddl.service.lottery.model.Gift;
import net.duckling.ddl.service.lottery.model.GiftPlan;
import net.duckling.ddl.service.lottery.model.GiftReport;
import net.duckling.ddl.service.lottery.model.Lottery;
import net.duckling.ddl.service.timer.TimerService;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LotteryService {

    /*
     * 三张表
     *
     * daily_lottery date -> seqence_map
     *
     * drawRecord user -> gift
     *
     * dailyIDGenerator date -> nature_seq
     */

    protected static final Logger LOG = Logger.getLogger(LotteryService.class);

    public static final String DAILY_NATURE_ID = "lottery:id:%s";
    public static final String DAILY_LOTTERY_SEQUENCE = "lottery:seq:%s";
    public static final String DAILY_LOTTERY_CHANCE = "lottery:chance:%s";
    public static final String DAILY_LOTTERY_IP = "lottery:ip:%s";

    public static final String LOTTERY_SWITCH = "lottery:switch";

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_NO_CHANCE = 2;
    public static final int STATUS_ACCESS_FORBIDDEN = 3;
    public static final int STATUS_SHUT_DOWN = 4;
    public static final int STATUS_WAIT_LOTTERY = 5;
    public static final int STATUS_FINISH_LOTTERY = 6;

    private JedisPool rdsPool;

    private Lottery lottery;

    private Map<Integer, Gift> giftMap;

    private IDrawResultDao drawResultDao;

    private IDeliveryDao deliveryDao;

    @SuppressWarnings("unused")
    private TimerService timerService;

    public void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }

    public void setDeliveryDao(IDeliveryDao deliveryDao) {
        this.deliveryDao = deliveryDao;
    }

    public void setDrawResultDao(IDrawResultDao drawResultDao) {
        this.drawResultDao = drawResultDao;
    }

    public Lottery getLottery() {
        return lottery;
    }

    public void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }

    public LotteryService(String configPath, String host, int port) {
        JedisPoolConfig conf = new JedisPoolConfig();
        conf.setMinIdle(10);
        conf.setMaxIdle(20);
        this.rdsPool = new JedisPool(conf, host, port);
        this.configPath =  System.getProperty("ddl.root") + configPath;
    }

    public void doInit() {
        this.reloadConfig();
    }

    public void doDestroy() {
        this.rdsPool.destroy();
    }

    public void reloadConfig() {
        LotteryConfigParser p = new LotteryConfigParser(configPath);
        this.lottery = p.loadConfig();
        this.giftMap = this.lottery.buildGiftMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.startTime = sdf.parse(this.lottery.getStartDate());
            this.endTime = sdf.parse(this.lottery.getEndDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String configPath;

    protected int queryLotteryResult(String date, long num) {
        Jedis jdc = rdsPool.getResource();
        String listName = String.format(DAILY_LOTTERY_SEQUENCE, date);
        String str = jdc.lindex(listName, num);
        int result = this.lottery.getDefaultLevel();
        if (str != null) {
            result = Integer.parseInt(str);
        } else {
            if (num == 0) { // 第一个点的人将会触发建立索引操作
                this.autoGenerateTodaySeqence();
                str = jdc.lindex(listName, num);
                LOG.info("Rebuild sequence for " + date);
                result = Integer.parseInt(str);
            } else {
                LOG.error("You should never goto here, a bug must be here");
            }
        }
        rdsPool.returnResource(jdc);
        return result;
    }

    protected void setLotterySwitch(String flag) {
        Jedis jdc = rdsPool.getResource();
        String keyName = LOTTERY_SWITCH;
        jdc.set(keyName, flag);
        rdsPool.returnResource(jdc);
    }

    protected boolean getLotterySwitch() {
        Jedis jdc = rdsPool.getResource();
        String keyName = LOTTERY_SWITCH;
        String str = jdc.get(keyName);
        boolean flag = false;
        if ("ON".equals(str)) {
            flag = true;
        } else {
            flag = false;
        }
        rdsPool.returnResource(jdc);
        return flag;
    }

    protected long getNatureSeqence(String date) {
        Jedis jdc = rdsPool.getResource();
        long id = jdc.incr(String.format(DAILY_NATURE_ID, date));
        id = id - 1;
        rdsPool.returnResource(jdc);
        return id;
    }

    // 查询当前用户的未过滤前获得的奖品
    private Gift getNatureGift(String date, long seq) {
        int level = this.queryLotteryResult(date, seq);
        return this.giftMap.get(level);
    }

    private DrawResponse finishResponse(String date, String user) {
        DrawResponse obj = new DrawResponse();
        obj.setMessage("Sorry, lottery is closed.");
        obj.setResult(null);
        obj.setUser(user);
        obj.setDate(date);
        obj.setStatus(STATUS_FINISH_LOTTERY);
        return obj;
    }

    private DrawResponse waitResponse(String date, String user) {
        DrawResponse obj = new DrawResponse();
        obj.setMessage("Sorry, lottery is not begin.");
        obj.setResult(null);
        obj.setUser(user);
        obj.setDate(date);
        obj.setStatus(STATUS_WAIT_LOTTERY);
        return obj;
    }

    private DrawResponse lotteryShutDown(String date, String user) {
        DrawResponse obj = new DrawResponse();
        obj.setMessage("Lottery service shut down.");
        obj.setResult(null);
        obj.setUser(user);
        obj.setDate(date);
        obj.setStatus(STATUS_SHUT_DOWN);
        return obj;
    }

    private DrawResponse hadDrawedToday(String date, String user) {
        DrawResponse obj = new DrawResponse();
        obj.setMessage("You have no chance to draw today.");
        obj.setResult(null);
        obj.setUser(user);
        obj.setDate(date);
        obj.setStatus(STATUS_NO_CHANCE);
        return obj;
    }

    private DrawResponse invalidIPAccess(String date, String user, String ip) {
        DrawResponse obj = new DrawResponse();
        obj.setMessage("Your ip" + ip + " drawed too many times.");
        obj.setResult(null);
        obj.setUser(user);
        obj.setDate(date);
        obj.setStatus(STATUS_ACCESS_FORBIDDEN);
        return obj;
    }

    private DrawResponse normalDrawResponse(String date, String user, DrawResult dr) {
        DrawResponse obj = new DrawResponse();
        obj.setMessage("Success draw.");
        obj.setResult(dr);
        obj.setUser(user);
        obj.setDate(date);
        obj.setStatus(STATUS_SUCCESS);
        return obj;
    }

    // 当前奖品被调剂后将此奖品插入到列表尾部
    private void swapGiftToTail(String date, Gift rawGift) {
        Jedis jdc = rdsPool.getResource();
        String key = String.format(DAILY_LOTTERY_SEQUENCE, date);
        // 需要一个随机数组混淆一下,一次加100个奖品免得后面都是大奖
        Random rand = new Random();
        int num = rand.nextInt(100);
        String[] array = new String[100];
        for (int i = 0; i < 100; i++) {
            array[i] = this.lottery.getDefaultLevel() + "";
        }
        array[num] = rawGift.getLevel() + "";
        jdc.rpush(key, array);
        rdsPool.returnResource(jdc);
    }

    // 查看用户今天是否抽过奖了
    public boolean hasDrawChance(String date, String user) {
        Jedis jdc = rdsPool.getResource();
        String keyName = String.format(DAILY_LOTTERY_CHANCE, date);
        boolean flag = jdc.sismember(keyName, user);
        rdsPool.returnResource(jdc);
        return !flag;
    }

    // 设置该用户已经抽过奖了
    protected void setUserDrawed(String date, String user) {
        Jedis jdc = rdsPool.getResource();
        String keyName = String.format(DAILY_LOTTERY_CHANCE, date);
        jdc.sadd(keyName, user);
        rdsPool.returnResource(jdc);
    }

    // 增加ip访问次数
    protected void incrIPAccess(String date, String ip) {
        Jedis jdc = rdsPool.getResource();
        String keyName = String.format(DAILY_LOTTERY_IP, date);
        jdc.hincrBy(keyName, ip, 1);
        rdsPool.returnResource(jdc);
    }

    // 计算IP访问次数是否超过了10次
    protected boolean isValidIP(String date, String ip) {
        Jedis jdc = rdsPool.getResource();
        String keyName = String.format(DAILY_LOTTERY_IP, date);
        jdc.hincrBy(keyName, ip, 1);
        String str = jdc.hget(keyName, ip);
        boolean flag = true;
        if (str == null) {
            flag = true;
        } else {
            Integer cnt = Integer.parseInt(str);
            flag = cnt < lottery.getMaxAccessTime();
        }
        rdsPool.returnResource(jdc);
        return flag;
    }

    protected DrawResult recordDrawResult(String date, String user, Gift g) {
        DrawResult dr = new DrawResult();
        dr.setDate(date);
        dr.setUser(user);
        dr.setDrawedTime(new Date());
        dr.setGiftLevel(g.getLevel());
        dr.setLotteryName(this.lottery.getName());
        dr.setGiftName(g.getName());
        int id = drawResultDao.save(dr);
        dr.setId(id);
        return dr;
    }

    // 获得中奖人员的列表
    public List<DrawResult> getGiftedUserList() {
        return drawResultDao.getDailyDrawResult(this.lottery.getName(), this.lottery.getMinReportLevel());
    }

    // 获得所有实物奖名单
    public List<DrawResult> getAllOfDelivery() {
        return drawResultDao.queryAllOfDelivery();
    }

    // 查看抽奖的历史记录
    private Map<String, Gift> lotteryHistory(String user) {
        List<DrawResult> result = drawResultDao.queryByUser(user);
        Map<String, Gift> map = new HashMap<String, Gift>();
        for (DrawResult dr : result) {
            map.put(dr.getDate(), giftMap.get(dr.getGiftLevel()));
        }
        return map;
    }

    // 计算当前用户是否需要调剂
    private boolean shouldShiftGift(String user, Gift curr) {
        // 如果不是高等级的奖品就不调剂了
        if (curr.getLevel() == 0 || curr.getLevel() > this.lottery.getHighLevel()) {
            return false;
        }
        // nuke to die
        if (isBlackList(user)) {
            return true;
        }
        // you already had it, so let it go
        if (hadHighLevelGift(user)) {
            return true;
        }
        return false;
    }

    // 判定该用户是否中过了高等级的奖品
    private boolean hadHighLevelGift(String user) {
        Map<String, Gift> history = this.lotteryHistory(user);
        for (Gift g : history.values()) {
            if (g.getLevel() <= this.lottery.getHighLevel()) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlackList(String user) {
        if (user.endsWith("cstnet.cn")) {
            return true;
        }
        return false;
    }

    // TODO blacklist 配置
    public GiftReport reportGifts(String date) {
        List<DrawResult> drawList = this.drawResultDao.getTodayDrawResult(date);
        Map<Integer, Integer> factMap = new HashMap<Integer, Integer>();
        for (DrawResult dr : drawList) {
            Integer cnt = factMap.get(dr.getGiftLevel());
            if (cnt == null) {
                cnt = 1;
            } else {
                cnt += 1;
            }
            factMap.put(dr.getGiftLevel(), cnt);
        }

        GiftPlan currPlan = this.lottery.findPlanByDate(date);

        Map<Integer, Integer> planMap = currPlan.getPool();
        Map<Integer, Integer> leftMap = new HashMap<Integer, Integer>();
        for (Entry<Integer, Integer> item : planMap.entrySet()) {
            Integer factCount = factMap.get(item.getKey());
            if (factCount == null) {
                factCount = 0;
            }
            int leftCnt = item.getValue() - factCount;
            leftMap.put(item.getKey(), leftCnt);
        }
        GiftReport repr = new GiftReport();
        repr.setDate(date);
        repr.setFact(factMap);
        repr.setLeft(leftMap);
        repr.setPlan(planMap);
        return repr;
    }

    public void generateNextDaySequence(String today, String nextDay) {
        GiftReport repr = this.reportGifts(today);
        LOG.info(repr);
        this.generateSequnce(nextDay, repr.getLeft());
    }

    // 生成中奖自然序列
    public void generateSequnce(String date, Map<Integer, Integer> lastDayLeft) {
        String[] seq = this.lottery.generatorLotterySequence(date, lastDayLeft);
        Jedis jdc = rdsPool.getResource();
        String listName = String.format(DAILY_LOTTERY_SEQUENCE, date);
        jdc.del(listName);
        jdc.rpush(listName, seq);
        rdsPool.returnResource(jdc);
    }

    // 抽奖了哈
    public DrawResponse draw(String date, String user, String ip) {
        Gift realGift = null;

        // 如果开关关闭
        Date now = new Date();
        int flag = this.compareTime(now);
        if (flag == WAIT) {
            return waitResponse(date, user);
        }
        if (flag == CLOSE) {
            return finishResponse(date, user);
        }
        if (!this.getLotterySwitch()) {
            return lotteryShutDown(date, user);
        }

        if (this.lottery.getHasCoolDown() && !hasDrawChance(date, user)) {
            return hadDrawedToday(date, user);
        }
        if (!isValidIP(date, ip)) {
            return invalidIPAccess(date, user, ip);
        }
        // 读取每个用户的自然序列值
        long seq = getNatureSeqence(date);
        // 计算该用户的自然奖品
        Gift rawGift = getNatureGift(date, seq);
        if (rawGift == null) {
            return null;
        }
        // 计算该用户的偏移量
        if (shouldShiftGift(user, rawGift)) {
            int level = this.lottery.getDefaultLevel();
            realGift = giftMap.get(level);
            swapGiftToTail(date, rawGift);
        } else {
            realGift = rawGift;
        }

        // 记录用户的中奖记录
        DrawResult dr = this.recordDrawResult(date, user, realGift);

        // 标记用户今天已经抽过奖了
        this.setUserDrawed(date, user);
        // 增加该ip的访问次数
        this.incrIPAccess(date, ip);
        return normalDrawResponse(date, user, dr);
    }

    public int saveDeliveryInfo(Delivery dv) {
        return this.deliveryDao.save(dv);
    }

    public void updateDelivery(Delivery dv) {
        this.deliveryDao.update(dv);
    }

    public Delivery queryDelivery(String user) {
        return this.deliveryDao.query(user);
    }

    public void stopLottery() {
        this.setLotterySwitch("OFF");
    }

    public void startLottery() {
        this.setLotterySwitch("ON");
    }

    public void resetTodayLottery(String date) {
        Jedis jdc = rdsPool.getResource();
        String[] keys = { String.format(DAILY_LOTTERY_CHANCE, date), String.format(DAILY_LOTTERY_SEQUENCE, date),
            String.format(DAILY_LOTTERY_IP, date), String.format(DAILY_NATURE_ID, date) };
        long d = jdc.del(keys);
        System.out.println(d);
        rdsPool.returnResource(jdc);
    }

    public List<DrawResult> getMyDrawResultList(String user) {
        return this.drawResultDao.queryByUser(user);
    }

    private void autoGenerateTodaySeqence() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(now);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        Date yesterDate = c.getTime();
        String yesterday = sdf.format(yesterDate);
        this.stopLottery();
        this.reloadConfig();
        this.generateNextDaySequence(yesterday, today);
        this.startLottery();
    }

    private Date startTime;

    private Date endTime;

    public static final int OPEN = 1;
    public static final int CLOSE = 2;
    public static final int WAIT = 3;

    public int compareTime(Date d) {
        if (d.after(this.startTime) && d.before(this.endTime)) {
            return OPEN;
        }
        if (d.before(this.startTime)) {
            return WAIT;
        }
        if (d.after(this.endTime)) {
            return CLOSE;
        }
        return OPEN;
    }

    public static void main(String[] args) {
        int total = 1000;
        String[] array = new String[total];
        for(int i = 0;i < total; i++){
            if(i % 5 == 0){
                array[i] = "6";
            }else if (i % 2 == 0){
                array[i] = "7";
            }else{
                array[i] = "0";
            }
        }
        for(int i = 0; i< total; i++){
            System.out.print(array[i] + " ");
        }
    }

}
