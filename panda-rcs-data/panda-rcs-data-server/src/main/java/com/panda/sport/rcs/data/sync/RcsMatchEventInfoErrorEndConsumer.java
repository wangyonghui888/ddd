package com.panda.sport.rcs.data.sync;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 风控异常结束事件
 *
 * @author v
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_EVENT_INFO_ERROR_END_BY_RCS",
        consumerGroup = "RCS_DATA_MATCH_EVENT_INFO_ERROR_END_BY_RCS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class RcsMatchEventInfoErrorEndConsumer extends RcsConsumer<Request<MatchEventInfoMessage>> {

    @Autowired
    protected MatchEventInfoErrorEndConsumer matchEventInfoErrorEndConsumer;



    @Override
    protected String getTopic() {
        return "MATCH_EVENT_INFO_ERROR_END_BY_RCS";
    }

    @Override
    public Boolean handleMs(Request<MatchEventInfoMessage> requests) {
        try {
            matchEventInfoErrorEndConsumer.handleMs(requests);
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDMEIEEBRG_"+requests.getLinkId() ,JsonFormatUtils.toJson(requests), e.getMessage(),e);
        }
        return true;
    }

}
