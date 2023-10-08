package com.panda.sport.rcs.mgr.mq.impl;
import com.alibaba.fastjson.JSONArray;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.mq.bean.HideOrderRatioVo;
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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  kris
 * @Description :  大数据下发的用户动态藏单比例数据
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "dynamic_hidden_order_ratio",
        consumerGroup = "dynamic_hidden_order_ratio",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.ORDERLY)
public class DynamicHideOrderRatioConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    RedisClient redisClient;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(16);
        defaultMQPushConsumer.setConsumeThreadMax(24);
    }

    @Override
    public void onMessage(String data) {
        if (StringUtils.isNotBlank(data)){
            try {
                List<HideOrderRatioVo> list = JSONArray.parseArray(data,HideOrderRatioVo.class);
                if (list != null && list.size() > 0){
                    for (HideOrderRatioVo vo:list){
                        log.info("::{}::动态藏单比例，sportId:{},dynamicHiddenRatio:{}","hideRatio"+vo.getUserId(),vo.getSportId(),vo.getDynamicHiddenRatio());
                        //动态藏单比例key
                        String key = String.format(RcsConstant.RCS_DYNAMIC_HIDE_ORDER_RATE,vo.getUserId(),vo.getSportId());
                        //动态藏单利率
                        BigDecimal ratio = vo.getDynamicHiddenRatio().divide(new BigDecimal(100));
                        redisClient.setExpiry(key,ratio,Constants.RCS_DYNAMIC_HIDE_ORDER_RATE_EXPIRT);
                    }
                }
            } catch (Exception e) {
                log.error("动态藏单比例数据下发异常,data:{}",data,e);
            }
        }
    }
}
