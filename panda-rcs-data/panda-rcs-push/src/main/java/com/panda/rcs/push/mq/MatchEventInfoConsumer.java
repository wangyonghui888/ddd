package com.panda.rcs.push.mq;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.rcs.push.service.MatchEventService;
import com.panda.sport.rcs.pojo.dto.MatchEventInfoDTO;
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

import java.util.List;

/**
 * @Description: 赛事事件
 * Topic=MATCH_EVENT_INFO
 * Group=RCS_PUSH_MATCH_EVENT_INFO_GROUP
 * 对应指令 -> 30003
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "MATCH_EVENT_INFO",
        consumerGroup = "RCS_PUSH_MATCH_EVENT_INFO_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoConsumer implements RocketMQListener<Request<List<MatchEventInfoDTO>>>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private MatchEventService matchEventService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(64);
        defaultMQPushConsumer.setConsumeThreadMax(96);
    }

    @Override
    public void onMessage(Request<List<MatchEventInfoDTO>> msg) {
        if (msg == null) {
            return;
        }

        List<MatchEventInfoDTO> datas = msg.getData();
        if (datas == null) {
            return;
        }

        log.info("::{}::,::{}::赛事事件消费->{}", msg.getLinkId(), msg.getData().get(0).getStandardMatchId(), msg.getData().size() == 1 ? JSONObject.toJSON(msg) : "日志太大，不输出");

        try {
            for (MatchEventInfoDTO data : datas) {
                if (data.getSourceType() == 0) {
                    continue;
                }
                Request<MatchEventInfoDTO> request = new Request<>();
                request.setDataSourceCode(msg.getDataSourceCode());
                request.setDataSourceTime(msg.getDataSourceTime());
                request.setDataType(msg.getDataType());
                request.setGlobalId(msg.getLinkId());
                request.setLinkId(msg.getLinkId());
                request.setData(data);

                MatchEventInfoDTO eventDto = request.getData();

                matchEventService.handlerMatchEvent(eventDto, request.getLinkId());
            }
        } catch (Exception e){
            log.error("::{}::赛事标准事件消费数据，异常信息：", msg.getLinkId(), e);
        }
    }
}
