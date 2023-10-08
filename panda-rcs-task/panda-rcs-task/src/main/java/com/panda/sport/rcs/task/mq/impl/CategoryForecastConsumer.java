package com.panda.sport.rcs.task.mq.impl;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mongo.PredictForecastVo;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.task.mq.bean.MatchCategoryUpdateBean;
import com.panda.sport.rcs.task.service.MatchServiceImpl;
import com.panda.sport.rcs.vo.ForecastMqVo;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 玩法forecast
 *
 * @author enzo
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "rcs_predict_forecast_play_mongo",
        consumerGroup = "rcs_task_rcs_predict_forecast_play_mongo",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class CategoryForecastConsumer implements RocketMQListener<ForecastMqVo>{

    @Autowired
    MongoTemplate mongotemplate;

    @Autowired
    MatchServiceImpl matchService;


    @Override
    public void onMessage(ForecastMqVo msg) {

        log.info("CategoryForecastConsumer接收参数:{}", JsonFormatUtils.toJson(msg));

        Long matchId = msg.getMatchId();

        Integer matchType = msg.getMatchType();

        if (matchId == null) return ;

        if (matchService.isLive(matchId) && matchType.equals(1)) return ;

        Long categoryId = msg.getPlayId();

        List<PredictForecastVo> forecastVos = msg.getList();
        if (CollectionUtils.isEmpty(forecastVos)) return ;

        List<PredictForecastVo> forecast =null;
        if(Arrays.asList(2L,18L,114L,122L,127L,332L,335L,240L,307L,309L).contains(categoryId)){
            forecast = forecastVos.stream().sorted(Comparator.comparing(PredictForecastVo::getScore)).collect(Collectors.toList());
        }else {
            forecast = forecastVos.stream().sorted(Comparator.comparing(PredictForecastVo::getScore).reversed()).collect(Collectors.toList());
        }
        try {
            Query query = new Query();
            Criteria criteria = Criteria.where("matchId").is(String.valueOf(matchId));
            if (!ObjectUtils.isEmpty(categoryId)) {
                criteria.and("id").is(categoryId);
            }
            query.addCriteria(criteria);
            Update update = new Update();
            update.set("forecast", forecast);
            mongotemplate.updateFirst(query, update, MarketCategory.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
