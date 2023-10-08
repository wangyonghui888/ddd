package com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.MatchStatisticsInfoDetailService;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.AbstractEventStatisticsService;
import com.panda.sport.rcs.data.utils.MarketUtils;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.MatchPeriod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class BasketballEventStatisticsService extends AbstractEventStatisticsService {

    private static final String BASKETBALL_TIME_CORRECTION = "basketballTimeCorrection";

    private List<String> margianUpdateEventList = Arrays.asList("timeout", "timeout_over", "time_start");
    
    @Resource
    private MatchStatisticsInfoDetailService matchStatisticsInfoDetailService;
    
    @Autowired
    private RDSProducerSendMessageUtils producerSendMessageUtils;
    

    @Override
    @PostConstruct
    public void initial() {
        sportId = 2L;
        mongoEvents = Arrays.asList("timeout", "timeout_over", "time_start", "time_start_0", "time_start_1");
        StatisticsServiceContext.addEventStaticsService(this);
    }

    @Override
    public Map standardScore(Request<MatchEventInfoMessage> request,int size) {
        try {
            MatchEventInfoMessage data = request.getData();
            if (data.getSportId() != sportId.intValue()) {
                return null;
            }
            //只取size ==1 的正常数据  过往全量数据不从这走
            if(1==size){
                iStandardMatchInfoService.updateMatchEventParam(data,1, "RDMEITG_"+request.getLinkId());
                /**时间通知*/
                notifyStatic(data.getStandardMatchId(), null, request.getLinkId(), data.getSecondsFromStart(), -1);
            }
            /*** 如果mongo事件进行发送***/
            MatchPeriod matchPeriod = getMatchPeriod(data.getSportId(), data,request.getLinkId());
            String ifPresent = cache.getIfPresent(String.valueOf(data.getStandardMatchId()));
            if (StringUtils.isBlank(ifPresent) || mongoEvents.contains(data.getEventCode())) {
                sendToMongo(request.getLinkId(), matchPeriod);
                cache.put(String.valueOf(data.getStandardMatchId()), "1");
            }
            if (margianUpdateEventList.contains(data.getEventCode())) {
                if ("time_start".equals(data.getEventCode()) && "1".equals(data.getExtraInfo())) {
                    //BG没有timeout_over  ， 使用time_start重置处理
                    MatchEventInfoMessage tempEventData = JSONObject.parseObject(JSONObject.toJSONString(data), MatchEventInfoMessage.class);
                    tempEventData.setEventCode("timeout_over");
                    sendMessage.sendMessage("RCS_TIME_MARGAIN", null, request.getLinkId(), tempEventData);
                }
                //短的暂停不在使用
//                if ("time_start".equals(data.getEventCode()) && "0".equals(data.getExtraInfo())) {
//                    //BG没有timeout_over  ， 使用time_start重置处理
//                    MatchEventInfoMessage tempEventData = JSONObject.parseObject(JSONObject.toJSONString(data), MatchEventInfoMessage.class);
//                    tempEventData.setEventCode("timeout");
//                    sendMessage.sendMessage("RCS_TIME_MARGAIN", null, request.getLinkId(), tempEventData);
//                }
                if (!"time_start".equals(data.getEventCode())) {
                    sendMessage.sendMessage("RCS_TIME_MARGAIN", null, request.getLinkId(), data);
                }
            }
            if("match_status".equals(data.getEventCode()) && data.getMatchPeriodId() == 100L){
                //如果全场结束后比分不是平分触发赛事级别关盘-异步处理
                Integer homeScore = data.getT1();
                Integer awayScore = data.getT2();
                if(Objects.isNull(homeScore) || Objects.isNull(awayScore)){
                    log.error("赛事：{}获取阶段：{} 的比分是null",data.getStandardMatchId(),data.getMatchPeriodId());
                    return null;
                }
                if(!homeScore.equals(awayScore)){
                    //发送赛事级别封盘消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tradeLevel", TradeLevelEnum.MATCH.getLevel());
                    jsonObject.put("sportId", SportIdEnum.BASKETBALL.getId());
                    jsonObject.put("matchId", data.getStandardMatchId());
                    jsonObject.put("status", TradeStatusEnum.CLOSE.getStatus());
                    jsonObject.put("remark", "全场结束，赛事关盘");
                    Request<JSONObject> requestDTO = new Request<>();
                    requestDTO.setData(jsonObject);
                    requestDTO.setLinkId(MarketUtils.getLinkId("_PERIOD_100_CLOSE"));
                    requestDTO.setDataSourceTime(System.currentTimeMillis());
                    log.info("赛事：{}全场结束,全场比分={}:{}，赛事关盘",data.getStandardMatchId(),homeScore,awayScore);
                    producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", data.getStandardMatchId() + "_PERIOD_100_CLOSE", requestDTO.getLinkId(), requestDTO);
                }

            }
            return null;
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDMEITG_"+request.getLinkId(), JsonFormatUtils.toJson(request), e.getMessage(),e);
            return null;
        }
    }

    @Override
    protected MatchPeriod getMatchPeriod(Long sportId, MatchEventInfoMessage data,String linkId) {
        MatchPeriod matchPeriod = new MatchPeriod();
        long time = System.currentTimeMillis();
        boolean isSetEventCode = true;
        if (data != null) {
            matchPeriod.setSportId(data.getSportId());
            matchPeriod.setStandardMatchId(data.getStandardMatchId());
            matchPeriod.setPeriod(data.getMatchPeriodId().intValue());
            matchPeriod.setCreateTime(time);
            //篮球特殊处理 ---- start ----如果篮球缓存有timeout 则不下发事件code到mongo
            String eventFlag = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, BASKETBALL_TIME_CORRECTION, data.getStandardMatchId()));
            if (StringUtils.isNotBlank(eventFlag) && "timeout".equals(eventFlag)) {
                isSetEventCode = false;
                log.info("::{}::篮球赛事timout-flag-2:{}", "RDMEITG_"+linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId());
            }
            //篮球特殊处理 ---- end
            if (isSetEventCode) {
                matchPeriod.setEventCode(data.getEventCode());
                matchPeriod.setEventTime(data.getEventTime());
                matchPeriod.setSecondsFromStart(data.getSecondsFromStart());
            }
            if (mongoEvents.contains(data.getEventCode())) {
                matchPeriod.setEventCode(data.getEventCode());
                matchPeriod.setEventTime(data.getEventTime());
                matchPeriod.setSecondsFromStart(data.getSecondsFromStart());
            }
            if ("time_start".equals(data.getEventCode())) {
                //不在把timeStart事件算入暂停处理，只是倒计时需要，其余业务需求不在把这个事件当做暂停
                if ("0".equals(data.getExtraInfo())) {
                    matchPeriod.setEventCode("time_start_0");
                } else if ("1".equals(data.getExtraInfo())) {
                    matchPeriod.setEventCode("time_start_1");
                }
                matchPeriod.setEventTime(data.getEventTime());
                matchPeriod.setSecondsFromStart(data.getSecondsFromStart());
            }
        }
        return matchPeriod;
    }

}
