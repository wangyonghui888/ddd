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
 * 融合校验值大于5%，告知风控做赛事级别提前结算关闭
 *
 * @author forever
 */
@Component
@Slf4j
@RocketMQMessageListener(
        topic = "STANDARD_MATCH_PRE_STATUS",
        consumerGroup = "STANDARD_MATCH_PRE_STATUS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class StandardMatchPresStatusConsumer extends RcsConsumer<JSONObject> {

    //通知的value>=该值的时候，需要关闭提前结算
    private static final double CRITICAL = 0.05;

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

            long standardMatchId = msg.getJSONObject("data").getLong("standardMatchId");
            double value = msg.getJSONObject("data").getDoubleValue("value");
            log.info("::{}::STANDARD_MATCH_PRE_STATUS::{}", standardMatchId , msg.getJSONObject("data"));
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.selectById(standardMatchId);
            if (standardMatchInfo == null) {
                log.warn("::{}::STANDARD_MATCH_PRE_STATUS:{},match not exists by id:{}", CommonUtil.getRequestId(), standardMatchId);
                return false;
            }

            int matchType = RcsConstant.isLive(standardMatchInfo.getMatchStatus()) ? 0 : 1;

            RcsTournamentTemplate tournamentTemplate = tournamentTemplateService.queryMatchTemplate(standardMatchInfo.getSportId(), standardMatchId, matchType);
            if (tournamentTemplate == null) {
                log.warn("::{}::STANDARD_MATCH_PRE_STATUS:{}，match template not exists by matchId :", CommonUtil.getRequestId(), standardMatchId);
                return false;
            }
            TournamentTemplateUpdateParam param = new TournamentTemplateUpdateParam();
            param.setId(tournamentTemplate.getId());
            if (value >= CRITICAL) {
                param.setMatchPreStatus(0);
            } else {
                //这里不需要打开，即不需要做任何处理
                //param.setMatchPreStatus(1);
                return true;
            }
            param.setMatchManageId(standardMatchInfo.getMatchManageId());

            matchTemplateModifyService.modifySettleSwitch(param);

        } catch (Exception e) {
            log.error("::{}::STANDARD_MATCH_PRE_STATUS:{},{}", CommonUtil.getRequestId(), e.getMessage(), e);
        }


        return true;
    }
}
