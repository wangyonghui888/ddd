package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import com.panda.sport.rcs.pojo.dto.MongoMsgDTO;
import com.panda.sport.rcs.pojo.dto.RcsBroadCastDTO;
import com.panda.sport.rcs.task.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送类
 *
 * @author black
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "SIMPLE_DTL_MSG_SENDER",
        consumerGroup = "SIMPLE_DTL_MSG_SENDER",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class SimpleDTLMsgSenderConsumer  implements RocketMQListener<String> {
    @Autowired
    MessageCenterConsumer messageCenterConsumer;
    @Override
    public void onMessage(String msg) {
        log.info("SimpleDTLMsgSenderConsumer接收参数:" + msg);
        try {
            JSONObject msgJs = JSONObject.parseObject(msg);
            RcsBroadCastDTO cast = new RcsBroadCastDTO();
            cast.setSportId(msgJs.getLong("sportId"));
            cast.setMsgType(msgJs.getInteger("msgType"));
            RcsBroadCast broad = new RcsBroadCast();
            broad.setMsgType(msgJs.getInteger("msgType"));
            broad.setExtendsField(msgJs.getString("matchId"));
            broad.setExtendsField2(msgJs.getString("matchManageId"));
            broad.setStatus(NumberUtils.INTEGER_ONE);
            broad.setMsgId(msgJs.getString("matchManageId"));
            broad.setAddition1(msgJs.getString("matchUnsettledOrder2"));
            broad.setContent(msgJs.getString("content"));
            cast.setRcsBroadCast(broad);
            messageCenterConsumer.handleMs(cast,null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
