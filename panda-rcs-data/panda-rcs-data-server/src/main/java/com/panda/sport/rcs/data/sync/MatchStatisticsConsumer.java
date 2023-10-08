package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mapper.CommonMapper;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.statistics.IStatisticsServiceHandel;
import com.panda.sport.rcs.data.utils.MarketUtils;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.dto.MatchStatisticsInfoDTO;
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
        topic = MqConstants.STANDARD_MATCH_STATISTICS,
        consumerGroup = "RCS_DATA_STANDARD_MATCH_STATISTICS_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchStatisticsConsumer extends RcsConsumer<Request<MatchStatisticsInfoDTO>> {

    @Autowired
    CommonMapper commonMapper;

    @Autowired
    private RDSProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private RcsDataRedis redisClient;
    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;
    /**
     * 赛事是否有统计事件下发缓存key
     */
    public static final String HAS_STATISTICS_CACHE_KEY = "has:statistics:match:";

    @Override
    protected String getTopic() {
        return MqConstants.STANDARD_MATCH_STATISTICS;
    }

    @Override
    public Boolean handleMs(Request<MatchStatisticsInfoDTO> request) {
        try {
            log.info("::{}::" ,"RDSMSG_"+request.getLinkId()+"_"+request.getData().getStandardMatchId());
            MatchStatisticsInfoDTO data = request.getData();
            if (data == null) {
                return true;
            }

            /*** B03电子篮球-收到统计信息、自动开盘 ***/
            String dataSourceCode = data.getDataSourceCode();
            Long sportId = data.getSportId();
            //缓存赛事是否有统计事件下发 bug-44113 篮球BE没有事件，在统计中开盘
            String statisticsCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, HAS_STATISTICS_CACHE_KEY, data.getStandardMatchId());
            if(!redisClient.exist(statisticsCacheKey)){
                beEventOpen(sportId, dataSourceCode, data.getStandardMatchId(), request.getLinkId());
            }
            redisClient.setExpiry(statisticsCacheKey, System.currentTimeMillis(), 24*60*60L);
            /*** 按照运动种类获取统计服务 ***/
            IStatisticsServiceHandel serviceHandle = StatisticsServiceContext.getStaticsService(data.getSportId());
            if (null == serviceHandle) {
                return true;
            }
            serviceHandle.standardScore(request);
        } catch (Exception e) {
            log.error("::{}::{},{},{}" ,"RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request) ,e.getMessage(), e);
        }
        return true;
    }

    /**
     * BE篮球收到统计事件后开盘
     *
     * @param sportId
     */
    private void beEventOpen(Long sportId ,String dataSourceCode, Long matchId, String linkid) {
        if(!("OD".equals(dataSourceCode) || "BE".equals(dataSourceCode))){
            return;
        }
        String eventCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, MarketOddsConsumer.HAS_ODDS_CACHE_KEY, matchId);
        boolean existsOdds = redisClient.exist(eventCacheKey);
        if(!existsOdds){
            log.info("{}::电子赛事自动开盘::{}::无赔率不开盘", linkid, matchId);
            return;
        }
        log.info("{}::电子赛事自动开盘::{}::赛事开盘", linkid, matchId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeLevel", TradeLevelEnum.MATCH.getLevel());
        jsonObject.put("sportId", sportId);
        jsonObject.put("matchId", matchId);
        jsonObject.put("status", TradeStatusEnum.OPEN.getStatus());
        jsonObject.put("remark", "电子赛事收到事件，赛事开盘");
        jsonObject.put("linkedType", "68");
        Request<JSONObject> requestDTO = new Request<>();
        requestDTO.setData(jsonObject);
        requestDTO.setLinkId(MarketUtils.getLinkId("_PERIOD_06_BE_OPEN"));
        requestDTO.setDataSourceTime(System.currentTimeMillis());
        producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", matchId + "_ELECTRON_OPEN", requestDTO.getLinkId(), requestDTO);
    }
}
