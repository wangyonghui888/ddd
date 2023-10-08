package com.panda.sport.rcs.data.sync;

import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mapper.CommonMapper;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.IScoreServiceHandle;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 比分统计
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_SCORES",
        consumerGroup = "RCS_DATA_STANDARD_MATCH_SCORES_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class StandardMatchScoreConsumer extends RcsConsumer<Request<StandardScoreDto>> {

    @Autowired
    CommonMapper commonMapper;

    @Override
    protected String getTopic() {
        return "STANDARD_MATCH_SCORES";
    }

    @Override
    public Boolean handleMs(Request<StandardScoreDto> request) {
        try {
            StandardScoreDto data = request.getData();
            if (data == null) {
                return true;
            }
            /*** 按照运动种类获取统计服务 ***/
            IScoreServiceHandle scoreService = StatisticsServiceContext.getScoreService(data.getSportId());
            if (null == scoreService) {
                return true;
            }
            scoreService.standardScore(request);
        } catch (Exception e) {
            log.error("::{}::{},{},{}" ,"RDSMSG_"+request.getLinkId(), JsonFormatUtils.toJson(request) , e.getMessage(), e);
        }
        return true;
    }





}
