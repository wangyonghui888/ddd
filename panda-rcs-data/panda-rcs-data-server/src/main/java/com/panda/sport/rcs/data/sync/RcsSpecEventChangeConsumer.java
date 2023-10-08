package com.panda.sport.rcs.data.sync;

import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMatchMarketMessageDTO;
import com.panda.sport.rcs.data.service.ITOrderDetailExtService;
import com.panda.sport.rcs.pojo.vo.MatchSpecEventSwitchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 前端手动确认切换危险事件
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "RCS_MATCH_SPEC_EVENT_CHANGE",
        consumerGroup = "RCS_MATCH_SPEC_EVENT_CHANGE_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsSpecEventChangeConsumer extends RcsConsumer<Request<MatchSpecEventSwitchVo>> {

    @Resource
    private ITOrderDetailExtService orderDetailExtService;

    @Override
    protected String getTopic() {
        return "RCS_MATCH_SPEC_EVENT_CHANGE";
    }

    @Override
    protected Boolean handleMs(Request<MatchSpecEventSwitchVo> msg) {

        log.info("::{}::RCS_MATCH_SPEC_EVENT_CHANGE->{}", msg.getLinkId(), JsonFormatUtils.toJson(msg));
        try {
            orderDetailExtService.changeSpecEvent(msg.getData().getEventCode(), msg.getData().getMatchId(),
                    msg.getLinkId(), RcsConstant.HOME_POSITION.equalsIgnoreCase(msg.getData().getCurrHomeAway()));
        } catch (Exception e) {
            log.info("::{}::RCS_MATCH_SPEC_EVENT_CHANGE->{}", msg.getLinkId(), JsonFormatUtils.toJson(msg), e);
        }
        return true;
    }
}
