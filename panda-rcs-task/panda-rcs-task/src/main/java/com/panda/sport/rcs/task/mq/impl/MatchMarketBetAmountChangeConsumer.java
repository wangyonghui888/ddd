package com.panda.sport.rcs.task.mq.impl;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.MatchMarketOddsVo;
import com.panda.sport.rcs.mongo.MatchMarketVo;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 赛事盘口赔率改变更新mongodb
 * 期望值处理
 *
 * @author black
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "ORDER_AMOUNT_CHANGE_TOPIC",
        consumerGroup = "rcs_task_ORDER_AMOUNT_CHANGE_TOPIC",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class MatchMarketBetAmountChangeConsumer implements RocketMQListener<RealTimeVolumeBean>{

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    private MatchServiceImpl matchServiceImpl;

    @Autowired
    private RedissonManager redissonManager;

    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(20, 40, 60l, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));

    private static Map<String, RealTimeVolumeBean> allMatchIdSet = new ConcurrentHashMap<String, RealTimeVolumeBean>(1000);

    private static boolean isStart = false;

    @Override
    public void onMessage(RealTimeVolumeBean realTimeVolumeBean) {
        try {
            if (realTimeVolumeBean != null) {
                allMatchIdSet.put(String.valueOf(realTimeVolumeBean.getPlayOptionsId()), realTimeVolumeBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("allMatchIdSet 当前剩余数量：{},matchId:{}", allMatchIdSet.size(), realTimeVolumeBean.getMatchId());
        }
    }

    private void handle(RealTimeVolumeBean realTimeVolumeBean) {
        String matchId = null;
        String lock = "";
        try {
            log.info("接收实货量更新投注项ID:{}", JsonFormatUtils.toJson(realTimeVolumeBean));
            matchId = String.valueOf(realTimeVolumeBean.getMatchId());
            lock = String.format("MONGODB_MARKET_%s_%s", matchId, realTimeVolumeBean.getPlayId());
            redissonManager.lock(lock);
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId).and("id").is(realTimeVolumeBean.getPlayId()));
            MarketCategory category = mongotemplate.findOne(query, MarketCategory.class);
            if (category == null || category.getMatchMarketVoList() == null) {
                return;
            } else {
                for (MatchMarketVo tempVo : category.getMatchMarketVoList()) {
                    if (!tempVo.getId().equals(realTimeVolumeBean.getMatchMarketId())) continue;
                    for (MatchMarketOddsVo vo : tempVo.getOddsFieldsList()) {
                        if (vo.getId().equals(realTimeVolumeBean.getPlayOptionsId())) {
                            vo.setBetAmount(realTimeVolumeBean.getSumMoney());
                            vo.setProfitValue(realTimeVolumeBean.getProfitValue());
                            vo.setBetNum(realTimeVolumeBean.getBetOrderNum());
                            log.info("修改实货量投注项ID:{}", JsonFormatUtils.toJson(vo));
                            break;
                        }
                    }
                    break;
                }
                matchServiceImpl.updateMongodbOdds(category);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redissonManager.unlock(lock);
            /*去除更新赛事表里的玩法list
            if (matchId != null) {
                MatchMarketLiveUpdateBean updateBean = new MatchMarketLiveUpdateBean(Long.parseLong(matchId));
                producerSendMessageUtils.sendMessage(MatchMarketLiveUpdateConsumer.MATCH_MARKET_LIVE_UPDATE_TOPIC, updateBean);
            }*/
        }
    }

    private static Long currentTime = System.currentTimeMillis();

    private void execute() {
        if (allMatchIdSet.size() <= 0) return;

        log.info("MatchMarketBetAmountChangeConsumer allMatchIdSet 丢入线程池：{}", allMatchIdSet.size());
        for (Iterator<Map.Entry<String, RealTimeVolumeBean>> it = allMatchIdSet.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RealTimeVolumeBean> item = it.next();
            it.remove();
            pool.execute(() -> {
                log.info("MatchMarketBetAmountChangeConsumer allMatchIdSet 开始执行：{},matchId:{}", allMatchIdSet.size(), item.getKey());
                handle(item.getValue());
                log.info("MatchMarketBetAmountChangeConsumer allMatchIdSet 结束执行：{},matchId:{}", allMatchIdSet.size(), item.getKey());
            });
        }
    }

    public void startUpdateThread() {
        log.info("启动缓存更新 DimensionStatistics");
        if (isStart) return;

        isStart = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (System.currentTimeMillis() - currentTime > 500l) {
                            currentTime = System.currentTimeMillis();//放在前面赋值是因为怕执行时间过长
                            execute();
                        } else if (allMatchIdSet.size() >= 100) {
                            currentTime = System.currentTimeMillis();//放在前面赋值是因为怕执行时间过长
                            execute();
                        }

                        Thread.currentThread().sleep(300l);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }).start();
    }
}
