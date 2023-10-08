package com.panda.rcs.order.reject.mq.var;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.order.reject.constants.RedisKey;
import com.panda.rcs.order.reject.entity.enums.VarSwitchEnum;
import com.panda.rcs.order.reject.service.MatchInfoService;
import com.panda.rcs.order.reject.utils.SendMessageUtils;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 2576VAR事件延时600s超时封盘
 * @author admin
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_VAR_MATCH_CLOSE",
        consumerGroup = "rcs_var_match_close_group",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsVarMatchCloseConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Resource
    MatchInfoService matchInfoService;
    @Resource
    SendMessageUtils sendMessage;


    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMqPushConsumer) {
        defaultMqPushConsumer.setConsumeThreadMin(64);
        defaultMqPushConsumer.setConsumeThreadMax(126);
    }

    @Override
    public void onMessage(String message) {
        String matchId = JSON.parseObject(message).getString("matchId");
        String sportId = JSON.parseObject(message).getString("sportId");
        String linkId = JSON.parseObject(message).getString("linkId");
        //如果VAR收单开关未开启
        if (!matchInfoService.getVarSwitchStatus(matchId)) {
            return;
        }
        //判定缓存var收单状态是否为1
        String varAcceptKey = String.format(RedisKey.RCS_ORDER_VAR_ACCEPT_STATUS,matchId);
        String varAcceptValue = RcsLocalCacheUtils.getValueInfo(varAcceptKey);
        if((VarSwitchEnum.Open.getCode()).equalsIgnoreCase(varAcceptValue)){
            //修改缓存var收单状态为0
            RcsLocalCacheUtils.timedCache.put(varAcceptKey, VarSwitchEnum.Close.getCode(), 4 * 60 * 60 * 1000);
            //VAR订单发送拒单mq：RCS_VAR_ORDER_REJECT
            matchInfoService.sendVarOrderStatus(linkId, String.valueOf(matchId), String.valueOf(sportId), OrderStatusEnum.ORDER_REJECT.getCode());
            //赛事封盘发送mq：RCS_TRADE_UPDATE_MARKET_STATUS
            JSONObject json = new JSONObject()
                    .fluentPut("tradeLevel", 1)
                    .fluentPut("matchId", matchId)
                    .fluentPut("sportId", 1)
                    .fluentPut("status", 1)
                    .fluentPut("linkedType", 111)
                    .fluentPut("remark", "VAR收单超600S赛事级封盘");

            Request<JSONObject> request = new Request<>();
            request.setData(json);
            request.setLinkId(linkId + "_eventSeal");
            request.setDataSourceTime(System.currentTimeMillis());
            String topic = "RCS_TRADE_UPDATE_MARKET_STATUS";
            String tags = matchId + "_" + sportId;
            String keys = linkId + "_" + matchId + "_" + sportId;
            log.info("::{}::赛事ID:{}::发送VAR收单超600S赛事级封盘消息队列topic={},tags={}", linkId, matchId, topic, tags);
            sendMessage.sendMessage(topic, tags, keys, request);
        }
    }
}
