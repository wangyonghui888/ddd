package com.panda.sport.rcs.data.sync;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.impl.FootballEventStatisticsService;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import com.panda.sport.rcs.pojo.dto.RcsBroadCastDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 异常结束事件
 *
 * @author v
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_EVENT_INFO_ERROR_END",
        consumerGroup = "RCS_DATA_MATCH_EVENT_INFO_ERROR_END_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoErrorEndConsumer extends RcsConsumer<Request<MatchEventInfoMessage>> {

    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;
    @Autowired
    FootballEventStatisticsService footballEventStatisticsService;


    @Override
    protected String getTopic() {
        return "MATCH_EVENT_INFO_ERROR_END";
    }

    @Override
    public Boolean handleMs(Request<MatchEventInfoMessage> requests) {
        try {
            MatchEventInfoMessage data = requests.getData();
            if (null == data||1!=data.getSportId()) {
                return true;
            }
            data.setMatchPeriodId(null);
            data.setExtraInfo(null);
            data.setEventCode("error_end_event");
            footballEventStatisticsService.standardScore(requests);
            sendMessage.sendMessage("FAKE_MATCH_EVENT", null, requests.getLinkId(), requests);

            MatchPeriod matchPeriod = new MatchPeriod();
            matchPeriod.setSportId(data.getSportId());
            matchPeriod.setStandardMatchId(data.getStandardMatchId());
            matchPeriod.setIsErrorEndEvent(data.getIsErrorEndEvent());
            sendMessage.sendMessage(MqConstants.MATCH_PERIOD_CHANGE, null, requests.getLinkId(), matchPeriod);

            if(1==data.getIsErrorEndEvent().intValue()){
                RcsBroadCastDTO cast = new RcsBroadCastDTO();
                cast.setSportId(data.getSportId().longValue());
                cast.setMsgType(4);
                RcsBroadCast broad = new RcsBroadCast();
                broad.setMsgType(4);
                broad.setExtendsField(data.getStandardMatchId().toString());
                broad.setStatus(NumberUtils.INTEGER_ONE);
                broad.setMsgId(requests.getLinkId());
                cast.setRcsBroadCast(broad);
                sendMessage.sendMessage("risk_msg_alarm", null, requests.getLinkId(), cast);
            }
        } catch (Exception e) {
            log.error( "::{}::,{},{},{}","RDMEIEEG_"+requests.getLinkId(),JsonFormatUtils.toJson(requests),e.getMessage() , e);
        }
        return true;
    }

}
