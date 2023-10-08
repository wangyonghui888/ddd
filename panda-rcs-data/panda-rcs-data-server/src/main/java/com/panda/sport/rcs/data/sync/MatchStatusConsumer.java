package com.panda.sport.rcs.data.sync;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchStatusMessage;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.service.RcsMatchCollectionService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.STANDARD_MATCH_STATUS,
        consumerGroup = "RCS_DATA_STANDARD_MATCH_STATUS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchStatusConsumer extends RcsConsumer<Request<StandardMatchStatusMessage>> {

    @Autowired
    IStandardMatchInfoService standardMatchInfoService;
    @Autowired
    RcsMatchCollectionService rcsMatchCollectionService;

    @Override
    protected String getTopic() {
        return MqConstants.STANDARD_MATCH_STATUS;
    }


    @Override
    public Boolean handleMs(Request<StandardMatchStatusMessage> request) {
        try {
            log.info("::{}::Mq-StandardMatchStatus存入MQ消息队列", "RDSMSG_"+request.getLinkId()+"_"+request.getData().getStandardMatchId()+"_"+request.getData().getMatchStatus());
            HashMap<String, String> strMap = new HashMap<>();
            strMap.put("linkId",request.getLinkId());
            StandardMatchStatusMessage standardMatchStatusDTO = request.getData();
            StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
            standardMatchInfo.setId(standardMatchStatusDTO.getStandardMatchId());
            standardMatchInfo.setMatchStatus(standardMatchStatusDTO.getMatchStatus());
            if(3==standardMatchStatusDTO.getMatchStatus().intValue()){
                standardMatchInfo.setEndTime(request.getDataSourceTime());
            }
            standardMatchInfoService.updateMatchStatus(standardMatchInfo,"RDSMSG_"+request.getLinkId());
            log.info("::{}::Mq-标准赛事状态信息推送成功存库", "RDSMSG_"+request.getLinkId()+"_"+request.getData().getStandardMatchId());
        } catch (Exception e) {
            log.error("::{}::Mq-StandardMatchStatus存入MQ消息队列错误:{},{},{}", "RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request),e.getMessage(), e);
            return false;
        }
        return true;
    }
}
