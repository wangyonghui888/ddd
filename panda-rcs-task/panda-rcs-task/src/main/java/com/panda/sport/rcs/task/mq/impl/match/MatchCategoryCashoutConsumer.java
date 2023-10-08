package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.ConfigCashOut;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        topic = "RCS_TOUR_TEMPLATE_CASHOUT_TOPIC",
        consumerGroup = "rcs_task_RCS_TOUR_TEMPLATE_CASHOUT_TOPIC",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
public class MatchCategoryCashoutConsumer implements RocketMQListener<ConfigCashOut> {


    @Autowired
    MatchServiceImpl matchService;

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    private TradeConfigConsumer configConsumer;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public void onMessage(ConfigCashOut dto) {
        try {
            log.info("RCS_TOUR_TEMPLATE_CASHOUT_TOPIC接收参数:{}", JSONObject.toJSONString(dto));
            Integer categoryPreStatus = dto.getCategoryPreStatus();
            Integer matchPreStatus = dto.getMatchPreStatus();
            Integer pendingOrderStatus = dto.getPendingOrderStatus();

            Long matchId = dto.getMatchId();
            Long categoryId = dto.getMarketCategoryId();
            Integer cashOutStatus = dto.getCashOutStatus();
            if (matchPreStatus != null || pendingOrderStatus != null) {
                RcsTradeConfig config = new RcsTradeConfig();
                config.setMatchId(String.valueOf(matchId));
                if (matchPreStatus != null) config.setMatchPreStatusRisk(matchPreStatus);
                if (pendingOrderStatus != null) config.setPendingOrderStatus(pendingOrderStatus);
                configConsumer.onMessage(config);
            }
            if (categoryPreStatus != null || cashOutStatus != null) {
                try {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(categoryId));
                    MarketCategory category = mongotemplate.findOne(query, MarketCategory.class);
                    if (category != null) {
                        if (categoryPreStatus != null) category.setCategoryPreStatus(categoryPreStatus);
                        if (cashOutStatus != null) category.setCashOutStatus(cashOutStatus);
                        matchService.updateMongodbOdds(category, false, "RCS_TOUR_TEMPLATE_CASHOUT_TOPIC" + matchId + "_" + categoryId);
                    } else {
                        category = new MarketCategory();
                        category.setMatchId(String.valueOf(matchId));
                        category.setId(categoryId);
                        category.setUpdateTime(DateUtils.parseDate(System.currentTimeMillis(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
                        if (categoryPreStatus != null) category.setCategoryPreStatus(categoryPreStatus);
                        if (cashOutStatus != null) category.setCashOutStatus(cashOutStatus);
                        mongotemplate.save(category);
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
