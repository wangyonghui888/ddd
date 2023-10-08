package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.task.wrapper.MatchPeriodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * 赛事比赛阶段 match_period更新
 *
 * @author black
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "TRADE_CONFIG_CHANGE",
        consumerGroup = "rcs_task_TRADE_CONFIG_CHANGE",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
public class TradeConfigConsumer implements RocketMQListener<RcsTradeConfig> {


    @Autowired
    MatchServiceImpl matchService;

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MatchPeriodService matchPeriodService;

    @Override
    public void onMessage(RcsTradeConfig tradeConfig) {
        Long matchId = null;
        try {
            matchId = Long.parseLong(tradeConfig.getMatchId());
            log.info("::{}::TRADE_CONFIG_CHANGE接收参数:{}" ,matchId, JSONObject.toJSONString(tradeConfig));
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId));
            MatchMarketLiveBean one = mongotemplate.findOne(query, MatchMarketLiveBean.class);
            if (one != null) {
                Update update = new Update();
                if (tradeConfig.getDataSource() != null)
                    update.set("tradeType", tradeConfig.getDataSource());
                if (tradeConfig.getStatus() != null)
                    update.set("operateMatchStatus", tradeConfig.getStatus());
                if(tradeConfig.getRiskManagerCode() != null)
                    update.set("riskManagerCode", tradeConfig.getRiskManagerCode());
                if(tradeConfig.getMatchPreStatusRisk() != null)
                    update.set("matchPreStatusRisk", tradeConfig.getMatchPreStatusRisk());
                if(tradeConfig.getPendingOrderStatus()!=null)
                    update.set("pendingOrderStatus",tradeConfig.getPendingOrderStatus());
                matchService.updateMongo(query,update);
            }else {
                log.info("::{}::TRADE_CONFIG_CHANGE错误:mongodb赛事不存在" ,matchId);
            }
        } catch (Exception e) {
            log.error("::{}::TRADE_CONFIG_CHANGE处理异常:{}",matchId,JSONObject.toJSONString(tradeConfig), e);
        }
    }
}
