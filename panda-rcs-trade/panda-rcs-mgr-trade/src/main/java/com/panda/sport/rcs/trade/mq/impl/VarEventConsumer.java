package com.panda.sport.rcs.trade.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.trade.mq.RcsConsumer;
import com.panda.sport.rcs.trade.param.TournamentTemplateUpdateParam;
import com.panda.sport.rcs.trade.util.CommonUtil;
import com.panda.sport.rcs.trade.wrapper.StandardMatchInfoService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsMatchTemplateModifyService;
import com.panda.sport.rcs.trade.wrapper.tourTemplate.IRcsTournamentTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * var事件通知业务关闭提前结算
 * @author abel
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "RCS_VAR_EVENT_HANDLER",
        consumerGroup = "RCS_VAR_EVENT_HANDLER_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class VarEventConsumer extends RcsConsumer<JSONObject> {


    @Autowired
    private IRcsTournamentTemplateService tournamentTemplateService;
    @Resource
    private StandardMatchInfoService standardMatchInfoService;
    @Resource
    private IRcsMatchTemplateModifyService matchTemplateModifyService;
    @Override
    protected String getTopic() {
        return "RCS_VAR_EVENT_HANDLER";
    }

    @Override
    protected Boolean handleMs(JSONObject msg) {
        try {

            long standardMatchId = msg.getLong("standardMatchId");
            String linkedId = msg.getString("linkedId");
            log.info("{}::赛事级提前结算MQ::{}::var事件", linkedId, standardMatchId);
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectById(standardMatchId);
            if (standardMatchInfo == null) {
                log.info("{}::赛事级提前结算MQ::{}::var事件::赛事不存在", linkedId, standardMatchId);
                return false;
            }

            int matchType = RcsConstant.isLive(standardMatchInfo.getMatchStatus()) ? 0 : 1;

            RcsTournamentTemplate tournamentTemplate = tournamentTemplateService.queryByMatchId(standardMatchId, matchType);
            if (tournamentTemplate == null) {
                log.info("{}::赛事级提前结算MQ::{}::var事件::赛事模板不存在", linkedId, standardMatchId);
                return false;
            }
            matchTemplateModifyService.sendMatchPreStatus(tournamentTemplate, linkedId);

        } catch (Exception e) {
            log.error("{}::赛事级提前结算MQ::{}::var事件", CommonUtil.getRequestId(), e.getMessage(), e);
        }


        return true;
    }

}
