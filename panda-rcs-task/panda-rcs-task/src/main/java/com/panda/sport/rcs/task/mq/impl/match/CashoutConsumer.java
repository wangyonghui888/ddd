package com.panda.sport.rcs.task.mq.impl.match;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.ConfigCashOut;
import com.panda.sport.rcs.pojo.odd.CashoutMarketMessage;
import com.panda.sport.rcs.pojo.odd.CashoutMatchMessage;
import com.panda.sport.rcs.task.mq.bean.DataRealTimeMessageBean;
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
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 赛事比赛阶段 match_period更新
 *
 * @author black
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_PRE_RESULT",
        consumerGroup = "rcs_task_STANDARD_MARKET_PRE_RESULT",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
public class CashoutConsumer implements RocketMQListener<String> {


    @Autowired
    MatchServiceImpl matchService;

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public void onMessage(String message) {
        try {
            DataRealTimeMessageBean<CashoutMatchMessage> msg = JSONObject.parseObject(message, new TypeReference<DataRealTimeMessageBean<CashoutMatchMessage>>() {
            });
            log.info("STANDARD_MARKET_PRE_RESULT接收参数:{}", message);
            CashoutMatchMessage cashoutMatchMessage = msg.getData();
            Long matchId = cashoutMatchMessage.getStandardMatchInfoId();

            List<CashoutMarketMessage> cashoutMarketMessages = cashoutMatchMessage.getMarketPreResultMessages();
            if (!CollectionUtils.isEmpty(cashoutMarketMessages)) {
                Map<Long, List<CashoutMarketMessage>> playCashOut = cashoutMarketMessages.stream().collect(Collectors.groupingBy(vo -> vo.getMarketCategoryId()));
                for (Long categoryId : playCashOut.keySet()) {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(categoryId));
                    MarketCategory category = mongotemplate.findOne(query, MarketCategory.class);
                    boolean present = playCashOut.get(categoryId).stream().filter(vo -> vo.getCashOutStatus().equals(1)).findAny().isPresent();
                    Integer cashOutStatus = present ? 1 : 0;

                    boolean updateCashOutStatus = false;
                    if (category != null) {
                        Integer outStatus = category.getCashOutStatus();
                        if (outStatus == null || !cashOutStatus.equals(outStatus)) {
                            updateCashOutStatus = true;
                        }
                    } else {
                        updateCashOutStatus = true;
                    }
                    if (updateCashOutStatus) {
                        ConfigCashOut dto = new ConfigCashOut();
                        dto.setMatchId(matchId);
                        dto.setMarketCategoryId(categoryId);
                        dto.setCashOutStatus(cashOutStatus);
                        producerSendMessageUtils.sendMessage("RCS_TOUR_TEMPLATE_CASHOUT_TOPIC", String.valueOf(dto.getCashOutStatus()), matchId + "_" + categoryId, dto);
                    }

                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
