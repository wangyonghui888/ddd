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
 * 关闭赛事提前结算
 * @author abel
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "RCS_STANDARD_MATCH_PRE_CLOSE",
        consumerGroup = "RCS_STANDARD_MATCH_PRE_CLOSE_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class StandardMatchPresCloseConsumer extends RcsConsumer<JSONObject> {


    @Autowired
    private IRcsTournamentTemplateService tournamentTemplateService;
    @Resource
    private StandardMatchInfoService standardMatchInfoService;
    @Resource
    private IRcsMatchTemplateModifyService matchTemplateModifyService;

    @Override
    protected String getTopic() {
        return "STANDARD_MATCH_PRE_STATUS";
    }

    @Override
    protected Boolean handleMs(JSONObject msg) {
        try {

            long standardMatchId = msg.getLong("standardMatchId");
            String linkedId = msg.getString("linkedId");
            log.info("::{}::{}::RCS_STANDARD_MATCH_PRE_CLOSE::{}", linkedId, standardMatchId, msg.getJSONObject("data"));
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectById(standardMatchId);
            if (standardMatchInfo == null) {
                log.warn("::{}::{}::STANDARD_MATCH_PRE_STATUS,match not exists by id", linkedId, standardMatchId);
                return false;
            }

            int matchType = RcsConstant.isLive(standardMatchInfo.getMatchStatus()) ? 0 : 1;

            RcsTournamentTemplate tournamentTemplate = tournamentTemplateService.queryByMatchId(standardMatchId, matchType);
            if (tournamentTemplate == null) {
                log.warn("::{}::{}::STANDARD_MATCH_PRE_STATUS->match template not exists by matchId.::matchType:{}", linkedId, standardMatchId, matchType);
                return false;
            }
            TournamentTemplateUpdateParam param = new TournamentTemplateUpdateParam();
            param.setId(tournamentTemplate.getId());
            param.setMatchPreStatus(0);
            param.setMatchManageId(standardMatchInfo.getMatchManageId());

            matchTemplateModifyService.modifySettleSwitch(param);

        } catch (Exception e) {
            log.error("::{}::STANDARD_MATCH_PRE_STATUS:{},{}", CommonUtil.getRequestId(), e.getMessage(), e);
        }


        return true;
    }

}
