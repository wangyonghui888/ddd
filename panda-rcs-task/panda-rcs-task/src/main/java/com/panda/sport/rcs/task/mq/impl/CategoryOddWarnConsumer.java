package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.panda.sport.rcs.task.mq.bean.MatchCategoryUpdateBean;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 滚球数据源超过一分钟未下发数据的玩法新增报警机制
 *
 * @author enzo
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_ODDS_WARNING_RISK",
        consumerGroup = "rcs_task_MATCH_ODDS_WARNING_RISK",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class CategoryOddWarnConsumer implements RocketMQListener<JSONObject>{

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    private RedissonManager redissonManager;

    @Autowired
    MatchServiceImpl matchService;

    @Override
    public void onMessage(JSONObject msg) {

        log.info("CategoryOddWarnConsumer接收参数:{}", JsonFormatUtils.toJson(msg));
        String matchId = msg.getString("standardMatchId");
        if (StringUtils.isBlank(matchId)) return ;

        Long marketCategoryId = msg.getLongValue("marketCategoryId");
        boolean sign = msg.getBooleanValue("sign");
        try {
            Query query = new Query();
            Criteria criteria = Criteria.where("matchId").is(matchId);
            if (!ObjectUtils.isEmpty(marketCategoryId)) {
                criteria.and("id").is(marketCategoryId);
            }
            query.addCriteria(criteria);
            Update update = new Update();
            update.set("warningSign", sign);
            mongotemplate.updateFirst(query, update, MarketCategory.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
