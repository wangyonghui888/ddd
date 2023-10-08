package com.panda.sport.rcs.trade.mq.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.mongo.MatchMarketLiveBean;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.trade.mq.RcsConsumer;
import com.panda.sport.rcs.trade.service.RcsSpecEventConfigService;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 赛事特殊事件初始化
 * */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_MATCH_SPECEVENT_SYNC_PUSH",
        consumerGroup = "RCS_MATCH_SPECEVENT_SYNC_PUSH_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchSpecEventSyncPushConsumer extends RcsConsumer<MatchMarketLiveBean> {

    @Autowired
    private RcsSpecEventConfigService rcsSpecEventConfigService;

    @Override
    protected String getTopic() {
        return "RCS_MATCH_SPECEVENT_SYNC_PUSH";
    }

    @Override
    protected Boolean handleMs(MatchMarketLiveBean matchMarketLiveBean) {
        if(null == matchMarketLiveBean){
            return true;
        }
        rcsSpecEventConfigService.pushMatchSpecEventStatus(matchMarketLiveBean.getMatchId());
        return true;
    }
}
