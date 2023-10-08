package com.panda.sport.rcs.task.mq.impl;

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
 * 赛事比赛阶段 match_period更新
 *
 * @author black
 */
@Component
@Slf4j
@TraceCrossThread
@RocketMQMessageListener(
        topic = "ORDER_UNSETTLE_SCHEDULE",
        consumerGroup = "rcs_task_ORDER_UNSETTLE_SCHEDULE",
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
public class OrderUnsettleScheduleConsumer implements RocketMQListener<MongoMsgDTO> {

    @Autowired
    MessageCenterConsumer messageCenterConsumer;

    @Override
    public void onMessage(MongoMsgDTO mongoMsgDTO) {
        log.info("OrderUnsettleScheduleConsumer接收参数:" + JsonFormatUtils.toJson(mongoMsgDTO));
        try {
            RcsBroadCastDTO cast = new RcsBroadCastDTO();
            cast.setSportId(mongoMsgDTO.getSportId().longValue());
            cast.setMsgType(MsgTypeEnum.ORDER_UNSETTLE.getMsgType());
            RcsBroadCast broad = new RcsBroadCast();
            broad.setMsgType(MsgTypeEnum.ORDER_UNSETTLE.getMsgType());
            broad.setExtendsField(mongoMsgDTO.getMatchId().toString());
            broad.setExtendsField2(mongoMsgDTO.getMatchManageId().toString());
            broad.setStatus(NumberUtils.INTEGER_ONE);
            broad.setMsgId(mongoMsgDTO.getMatchManageId().toString());
            broad.setAddition1(String.valueOf(mongoMsgDTO.getMatchUnsettledOrder2()));
            cast.setRcsBroadCast(broad);
            messageCenterConsumer.handleMs(cast,null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
