package com.panda.sport.rcs.data.sync;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.IEventServiceHandle;
import com.panda.sport.rcs.data.utils.RcsDataRedis;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.MarketUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 事件比分统计
 *
 * @author v
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.MATCH_EVENT_INFO_TOPIC,
        consumerGroup = "RCS_DATA_MATCH_EVENT_INFO_TOPIC_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MatchEventInfoConsumer extends RcsConsumer<Request<List<MatchEventInfoMessage>>> {

    @Autowired
    protected RcsDataRedis redisClient;

    @Autowired
    private RDSProducerSendMessageUtils producerSendMessageUtils;

    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;

    private static final String DATA_SOURCE_CODE = "dataSourceCode";
    /**
     * 延迟事件缓存flag
     */
    private static final String EVENT_LIST_FLAG = "eventListFlag";
    /**
     * 延迟事件缓存列表
     */
    private static final String EVENT_DELAY_LIST = "eventDelayList";
    /**
     * 切换事件源标记
     */
    private boolean switchEventSourceFlag = false;

    public static List sportIds = Arrays.asList(1, 2, 3, 4, 5, 7, 8, 9, 10);

    public static final String EVENT_CACHE_KEY = "event:cache:key:match:";

    @Override
    protected String getTopic() {
        return MqConstants.MATCH_EVENT_INFO_TOPIC;
    }

    @Override
    public Boolean handleMs(Request<List<MatchEventInfoMessage>> requests) {
        try {
            //log.info("MatchEventInfoConsumer收到消息:{}，paramsMap：{}", JsonFormatUtils.toJson(requests), JSONObject.toJSONString(paramsMap));
            List<MatchEventInfoMessage> datas = requests.getData();
            if (CollectionUtils.isEmpty(datas)) {
                return true;
            }
            MatchEventInfoMessage matchEventInfoMessage = datas.get(0);
            if (!sportIds.contains(matchEventInfoMessage.getSportId().intValue())) {
                return true;
            }

            //看有没有在执行全量  如果在执行全量将datas存入缓存
            /*String eventDelayListKey = String.format(RCS_DATA_KEY_CACHE_KEY, EVENT_DELAY_LIST, datas.get(0).getStandardMatchId());
            log.info("eventDelayListKey{}",eventDelayListKey);
            String eventListFlagKey = String.format(RCS_DATA_KEY_CACHE_KEY, EVENT_LIST_FLAG, datas.get(0).getStandardMatchId());
            log.info("eventListFlagKey{}",eventListFlagKey);
            String eventListFlag = redisClient.get(eventListFlagKey);
            TypeReference<List<MatchEventInfoMessage>> typeReference = new TypeReference<List<MatchEventInfoMessage>>() {};

            if(StringUtils.isNotBlank(eventListFlag)) {
                List list = redisClient.getObj3(eventDelayListKey, typeReference);
                log.info("list-eventDelayListKey-1:{},id:{}",JsonFormatUtils.toJson(list),datas.get(0).getStandardMatchId());
                if(CollectionUtils.isEmpty(list)){
                    list = new ArrayList();
                }
                List<MatchEventInfoMessage> data = requests.getData();
                data.addAll(list);
                log.info("list-eventDelayListKey-2:{},id:{}",JsonFormatUtils.toJson(data),datas.get(0).getStandardMatchId());
                redisClient.setExpiry(eventDelayListKey,data,120L);
                return true;
            }

            String dataSourceCodeKey = String.format(RCS_DATA_KEY_CACHE_KEY, DATA_SOURCE_CODE, datas.get(0).getStandardMatchId());
            String dataSourceCode = DataCache.dataSourceMarkCache.get(dataSourceCodeKey, key -> redisClient.get(key));

            if(StringUtils.isBlank(dataSourceCode)){
                redisClient.setExpiry(dataSourceCodeKey, datas.get(0).getDataSourceCode(), 14400L);
            }else{
                if(!dataSourceCode.equals(datas.get(0).getDataSourceCode())){
                    //切换数据源 清队标记 重新统计
                    log.info("event切换事件源{}",datas.get(0).getDataSourceCode());
                    redisClient.setExpiry(eventListFlagKey, 1, 20L);
                    switchEventSourceFlag=true;
                    redisClient.setExpiry(dataSourceCodeKey, datas.get(0).getDataSourceCode(), 14400L);
                }
            }

            //读缓存看缓存中有没有未处理数据 如果有加上去处理
            List<MatchEventInfoMessage> list = redisClient.getObj3(eventDelayListKey, typeReference);
            log.info("list-eventDelayListKey-3:{},id:{}",JsonFormatUtils.toJson(list),datas.get(0).getStandardMatchId());
            if(CollectionUtils.isNotEmpty(list)){
                datas.addAll(list);
                redisClient.delete(eventDelayListKey);
            }*/
            List<MatchEventInfoMessage> newDatas = datas.stream().sorted((a, b) -> a.getEventTime().compareTo(b.getEventTime())).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(newDatas)) {
                //将赛事最后的事件缓存
                String eventCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, EVENT_CACHE_KEY, datas.get(0).getStandardMatchId());
                if(!redisClient.exist(eventCacheKey)){
                    //B03 O01赛事第一次收到事件自动开盘
                    beEventOpen(matchEventInfoMessage, requests.getLinkId());
                }
                redisClient.setExpiry(eventCacheKey, newDatas.get(0), 24*60*60L);
            }

            execute(requests, newDatas);
            //取消标记
           /* if(switchEventSourceFlag) {
                redisClient.delete(eventListFlagKey);
            }*/
        } catch (Exception e) {
            log.error("::{}::{},{},{}", "RDMEITG_" + requests.getLinkId(), JsonFormatUtils.toJson(requests), e.getMessage(), e);
        }
        return true;
    }

    /**
     * 执行数据
     *
     * @param requests
     * @param datas
     */
    private void execute(Request<List<MatchEventInfoMessage>> requests, List<MatchEventInfoMessage> datas) {
        if (CollectionUtils.isEmpty(datas)) return;
        Request<MatchEventInfoMessage> request = new Request<>();
        MatchEventInfoMessage lastData = datas.get(datas.size() - 1);
        log.info("::{}::MatchEventInfoMessage", "RDMEITG_" + requests.getLinkId() + "_" + lastData.getStandardMatchId());
        request = new Request<>();
        request.setDataSourceCode(requests.getDataSourceCode());
        request.setDataSourceTime(requests.getDataSourceTime());
        request.setDataType(requests.getDataType());
        request.setGlobalId(requests.getLinkId());
        request.setLinkId(requests.getLinkId());
        request.setData(lastData);
        /*** 按照运动种类获取统计服务 ***/
        IEventServiceHandle serviceHandle = StatisticsServiceContext.getEventStaticsService(datas.get(0).getSportId());
        serviceHandle.standardScore(request, datas.size());
    }

    /**
     * O01和B03电子赛事第一次收到事件后判断有赔率则自动开盘
     *
     * @param data
     */
    private void beEventOpen(MatchEventInfoMessage data, String linkid) {
        String dataSourceCode = data.getDataSourceCode();
        if(!("OD".equals(dataSourceCode) || "BE".equals(dataSourceCode))){
            return;
        }
        String eventCacheKey = String.format(RCS_DATA_KEY_CACHE_KEY, MarketOddsConsumer.HAS_ODDS_CACHE_KEY, data.getStandardMatchId());
        boolean existsOdds = redisClient.exist(eventCacheKey);
        if(!existsOdds){
            log.info("{}::电子赛事自动开盘::{}::无赔率不开盘", linkid, data.getStandardMatchId());
            return;
        }
        log.info("{}::电子赛事自动开盘::{}::赛事开盘", linkid, data.getStandardMatchId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeLevel", TradeLevelEnum.MATCH.getLevel());
        jsonObject.put("sportId", data.getSportId());
        jsonObject.put("matchId", data.getStandardMatchId());
        jsonObject.put("status", TradeStatusEnum.OPEN.getStatus());
        jsonObject.put("remark", "电子赛事收到事件，赛事开盘");
        jsonObject.put("linkedType", "68");
        Request<JSONObject> requestDTO = new Request<>();
        requestDTO.setData(jsonObject);
        requestDTO.setLinkId(MarketUtils.getLinkId("_PERIOD_06_BE_OPEN"));
        requestDTO.setDataSourceTime(System.currentTimeMillis());
        producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", data.getStandardMatchId() + "_ELECTRON_OPEN", requestDTO.getLinkId(), requestDTO);
    }
}
