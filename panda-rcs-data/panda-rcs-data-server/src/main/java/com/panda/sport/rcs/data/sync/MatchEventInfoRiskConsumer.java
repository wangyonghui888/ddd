package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.service.ITOrderDetailExtService;
import com.panda.sport.rcs.data.service.MatchEventInfoService;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.pojo.enums.MatchEventConfigEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.MATCH_EVENT_INFO_TO_RISK,
        consumerGroup = "RCS_DATA_SERVER_MATCH_EVENT_INFO_TO_RISK_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoRiskConsumer extends RcsConsumer<Request<List<MatchEventInfo>>> {

    /**
     * var事件
     */
    private  static final Set<String> VAR_SET = new HashSet<>(Arrays.asList("possible_video_assistant_referee", "video_assistant_referee", "possible_var", "var_reason", "var_reviewing"));
    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;
    private static final String VAR_EVENT_CACHE_KEY = "VAR:EVENT:CACHE:KEY:MATCHID";
    @Autowired
    MatchEventInfoService matchEventInfoService;
    @Autowired
    IStandardMatchInfoService iStandardMatchInfoService;
    @Autowired
    ITOrderDetailExtService orderDetailExtService;
    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;
    @Autowired
    private RcsDataRedis redisClient;

    @Override
    protected String getTopic() {
        return MqConstants.MATCH_EVENT_INFO_TO_RISK;
    }

    @Override
    @Trace
    public Boolean handleMs(Request<List<MatchEventInfo>> rRequests) {
        try {
            log.info("::{}::MatchEventInfoMessageMQ消息队列", "RDMEITRG_" + rRequests.getLinkId());
            List<MatchEventInfo> datas = rRequests.getData();
            String linkId = rRequests.getLinkId();
            for (MatchEventInfo matchEventInfo : datas) {
                if (matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_BC.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_SR.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_BG.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_RB.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_PD.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_KO.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_V02.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_BE.getCode()) ||
                        matchEventInfo.getDataSourceCode().equalsIgnoreCase(MatchEventConfigEnum.EVENT_SOURCE_PD_TWO.getCode())) {
                    matchEventInfo.setCreateTime(System.currentTimeMillis());
                    matchEventInfoService.insertOrUpdate(matchEventInfo);
                    //根据事件决定订单是接受 还是拒绝
                    orderDetailExtService.batchUpdateOrderExt(matchEventInfo, linkId);
                    try {
                        orderDetailExtService.specEventHandler(matchEventInfo, linkId);
                    } catch (Exception e) {
                        log.error("::{}::{},{},{}", "RDMEITRG_" + rRequests.getLinkId(), JsonFormatUtils.toJson(rRequests), e.getMessage(), e);
                    }
                    sendAoEvent(matchEventInfo, rRequests.getLinkId());
                    varEventHandler(matchEventInfo ,linkId);
                } else {
                    log.info("::{}::其他数据源数据不处理", "RDMEITRG_" + rRequests.getLinkId() + "_" + rRequests.getData().get(0).getStandardMatchId());
                }
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}", "RDMEITRG_" + rRequests.getLinkId(), JsonFormatUtils.toJson(rRequests), e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * ao玩法自动开盘逻辑
     *
     * @param matchEventInfo
     * @param linkId
     */
    private void sendAoEvent(MatchEventInfo matchEventInfo, String linkId) {
        if (!SportIdEnum.isFootball(matchEventInfo.getSportId())) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("matchId", matchEventInfo.getStandardMatchId());
            sendMessage.sendMessage("RCS_AO_AUTO_OPEN_MARKET_TOPIC", null, matchEventInfo.getStandardMatchId() + "", jsonObject);
        } catch (Exception e) {
            log.error("::{}::{},{}", "RDMEITRG_" + linkId, JsonFormatUtils.toJson(matchEventInfo), e.getMessage(), e);
        }
    }

    /**
     * 1.当var事件过来时需要通知业务关闭提前结算
     * 2.当非var事件时，需要将因为var事件关闭的提前结算打开
     * @param matchEventInfo
     */
    private void varEventHandler(MatchEventInfo matchEventInfo, String linkid){
        log.info("{}::{}::var事件处理::{}", linkid, matchEventInfo.getStandardMatchId(), matchEventInfo.getEventCode());
        if(!SportIdEnum.isFootball(matchEventInfo.getSportId())){
            return;
        }
        String cacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, VAR_EVENT_CACHE_KEY, matchEventInfo.getStandardMatchId());

        if(VAR_SET.contains(matchEventInfo.getEventCode().toLowerCase())) {
            log.info("{}::{}::var事件处理::var事件提前结算关闭::{}", linkid, matchEventInfo.getStandardMatchId(), matchEventInfo.getEventCode());
            redisClient.setExpiry(cacheKey, System.currentTimeMillis(), 24*60*60L);
            sendVarEventMq(matchEventInfo, linkid);
            return;
        }
        //如果非var事件需要打开因为var事件关闭的提前结算打开
        if(!redisClient.exist(cacheKey)){
            //没有因为var事件关闭的提前结算的情况直接不处理
           return;
        }
        log.info("{}::{}::var事件处理::var事件关闭提前结算::{}", linkid, matchEventInfo.getStandardMatchId(), matchEventInfo.getEventCode());
        //删掉key后表示没有var事件关闭提前结算
        redisClient.delete(cacheKey);
        sendVarEventMq(matchEventInfo, linkid);
    }

    /**
     * 发送mq再trade项目通知业务
     * @param matchEventInfo
     * @param linkid
     */
    private void sendVarEventMq(MatchEventInfo matchEventInfo, String linkid){
        JSONObject json = new JSONObject();
        json.put("standardMatchId",matchEventInfo.getStandardMatchId());
        json.put("linkedId",linkid);
        sendMessage.sendMessage("RCS_VAR_EVENT_HANDLER", matchEventInfo.getStandardMatchId() +" ", linkid, json);
    }

}
