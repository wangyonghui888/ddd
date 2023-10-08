package com.panda.sport.rcs.limit.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :  lithan
 * @Description :  限额缓存清理
 * @Date: 2020-10-15 14:46:43
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "RCS_LIMIT_CACHE_CLEAR_TOPIC",
        consumerGroup = "RCS_LIMIT_CACHE_CLEAR_TOPIC",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class LimitCacheClearConsumer implements RocketMQListener<LimitCacheClearVo>, RocketMQPushConsumerLifecycleListener {


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(256);
    }

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private StandardMatchInfoMapper matchInfoMapper;

    @Autowired
    StandardSportTournamentMapper tournamentMapper;

    @Autowired
    RedisClient redisClient;

    public LimitCacheClearConsumer() {
//        super("RCS_LIMIT_CACHE_CLEAR_TOPIC", "");
    }


    @Override
    public void onMessage(LimitCacheClearVo data) {
        try {
            log.info("限额缓存清理更新 ：{}", JSONObject.toJSONString(data));
            Integer sportId = data.getSportId();
            Integer dataType = data.getDataType();
            Integer matchId = data.getMatchId();


            Map<String, Object> map = new HashMap<>();
            map.put("sportId", sportId);
            map.put("dataType", dataType);
            map.put("val", data.getVal());
            if (dataType == 4 || dataType == 2) {
                map.put("val2", data.getVal2());
                map.put("val3", data.getVal3());
            }
            map.put("matchId", matchId);
            map.put("matchType", data.getMatchType());
            map.put("businessId", data.getBusinessId());
            map.put("tournamentLevel", data.getTournamentLevel());
            map.put("playId", data.getTournamentLevel());

            //通知sdk 清除缓存
            producerSendMessageUtils.sendMessage("rcs_limit_cache_clear_sdk,," + dataType, map);
            log.info("限额缓存清理 通知sdk删除缓存 完成 ：{}", JSONObject.toJSONString(map));

            if (data.getDataType() == 3) {
                //用户画像兼容
                final String RCS_MATCH_USER_SINGLE_LIMIT_KEY = "rcs_portrait_match_user_single_limit";
                final String RCS_MATCH_USER_SINGLE_LIMIT_MATCH = "match_id.%s.match_type.%s";
                String earlyKey = String.format(RCS_MATCH_USER_SINGLE_LIMIT_MATCH, data.getMatchId(), 1);
                String liveKey = String.format(RCS_MATCH_USER_SINGLE_LIMIT_MATCH, data.getMatchId(), 2);
                redisClient.hashRemove(RCS_MATCH_USER_SINGLE_LIMIT_KEY, earlyKey);
                redisClient.hashRemove(RCS_MATCH_USER_SINGLE_LIMIT_KEY, liveKey);
                log.info("用户画像限额缓存清理 通知sdk删除缓存 完成 ：{}", JSONObject.toJSONString(map));
            }
        } catch (Exception e) {
            log.error("限额缓存清理 MQ异常：{}{}", e.getMessage(), e);
        }
        return;
    }
}
