package com.panda.sport.rcs.data.sportStatisticsService.statistics;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.sportStatisticsService.AbstractSuperStatisticsService;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.dto.MatchStatisticsInfoDTO;
import com.panda.sport.rcs.pojo.dto.StatisticsNotifyDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author V
 */
@Slf4j
public abstract class AbstractStatisticsService extends AbstractSuperStatisticsService implements IStatisticsServiceHandel {

    @Autowired
    IStandardMatchInfoService iStandardMatchInfoService;

    @Override
    public Map standardScore(Request<MatchStatisticsInfoDTO> request) {
        try {
            MatchStatisticsInfoDTO oData = request.getData();
            MatchStatisticsInfoDTO data = BeanCopyUtils.deepCopyProperties(oData, MatchStatisticsInfoDTO.class);
            if (data.getSportId() != sportId.intValue()) {
                return null;
            }
            //uof整合
            uofIntegrate(data, request);
            //通知统计下发
            //notifyStatic(data.getStandardMatchId(), request.getLinkId(), null, 2);
            return null;
        } catch (Exception e) {
            log.error("::{}::{},{},{}" ,"RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request) ,e.getMessage(), e);
            return null;
        }
    }


    /**
     * getMatchPeriod
     *
     * @param data
     * @param dataSourceTime
     * @param request
     * @return
     */
    protected MatchPeriod getMatchPeriod(MatchStatisticsInfoDTO data, Long dataSourceTime, Request<MatchStatisticsInfoDTO> request) {
        MatchPeriod matchPeriod = new MatchPeriod();
        matchPeriod.setSportId(data.getSportId());
        matchPeriod.setStandardMatchId(data.getStandardMatchId());
        matchPeriod.setPeriod(data.getPeriod());
        matchPeriod.setEventCode("uof");
        matchPeriod.setEventTime(dataSourceTime);
        servesFirst(data,matchPeriod,request);
        if (sportId == 2||sportId == 5) {
            matchPeriod.setSecondsFromStart(data.getRemainingTime());
        } else {
            matchPeriod.setSecondsFromStart(data.getSecondsMatchStart());
        }
        return matchPeriod;
    }

    //设定发球方
    private void servesFirst(MatchStatisticsInfoDTO data, MatchPeriod matchPeriod, Request<MatchStatisticsInfoDTO> request){
        try {
            List<MatchStatisticsInfoDetail> matchStatisticsInfoDetailList = data.getMatchStatisticsInfoDetailList();
            if (CollectionUtils.isEmpty(matchStatisticsInfoDetailList)) {
                return;
            }
            for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : matchStatisticsInfoDetailList) {
                if (matchStatisticsInfoDetail.getCode().equals("serving_side")) {
                    if (1 == matchStatisticsInfoDetail.getFirstNum().intValue()) {
                        matchPeriod.setServesFirst("home");
                    }else if (2 == matchStatisticsInfoDetail.getFirstNum().intValue()) {
                        matchPeriod.setServesFirst("away");
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}" ,"RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(matchPeriod) ,e.getMessage(), e);
        }
    }

    /**
     * 给前端的其他模块发送统计结果
     *
     * @param matchId
     * @param linkId
     * @param seconds
     * @param channel
     */
    protected void notifyStatic(Long matchId, String linkId, Long seconds, Integer channel,Integer period) {
        //通知统计下发
        StatisticsNotifyDTO statisticsNotifyDTO = new StatisticsNotifyDTO();
        statisticsNotifyDTO.setStandardMatchId(matchId);
        statisticsNotifyDTO.setPeriod(period);
        statisticsNotifyDTO.setChannel(channel);
        statisticsNotifyDTO.setGlobalId(linkId);
        if (seconds != null) {statisticsNotifyDTO.setSecondsFromStart(seconds);}
        sendMessage.sendMessage(MqConstants.WS_STATISTICS_NOTIFY_TOPIC, null, linkId, statisticsNotifyDTO);
    }

    /**
     * sendToMongo
     *
     * @param request
     * @param matchPeriod
     */
    protected void sendToMongo(Request<MatchStatisticsInfoDTO> request, MatchPeriod matchPeriod) {
        HashMap<String, String> strMap = new HashMap<>();
        strMap.put("linkId", request.getLinkId());
        log.info("::{}::matchPeriod存入MQ消息队列:{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request));
        sendMessage.sendMessage(MqConstants.MATCH_PERIOD_CHANGE, null, request.getLinkId(), matchPeriod, strMap);
        log.info("::{}::Mq-标准赛事uof信息推送mongo:{}","RDSMSG_"+request.getLinkId(), JsonFormatUtils.toJson(matchPeriod));
    }


    /**
     * uof整合
     *
     * @param data
     * @param request
     * @return
     */
    protected int uofIntegrate(MatchStatisticsInfoDTO data, Request<MatchStatisticsInfoDTO> request) {
        try {
            //if redis 缓存中间有数据 进行返回
            long nowTime = System.currentTimeMillis();
            String key = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, data.getStandardMatchId());
            String redisLastEventInTime = redisClient.hGet(key,"lastEventInTime");
            String isESport = redisClient.hGet(key,"isESport");
            log.info("::{}::uofIntegrate2:{}:{}","RDSMSG_"+request.getLinkId(),redisLastEventInTime,nowTime);
            if (StringUtils.isNotBlank(redisLastEventInTime)) {
                boolean pass = false;
                if(nowTime-Long.valueOf(redisLastEventInTime)<2*60*1000){
                    pass = true;
                }
                if(StringUtils.isNotBlank(isESport)){
                    pass = false;
                    if(nowTime-Long.valueOf(redisLastEventInTime)<20*1000){
                        pass = true;
                    }
                }
                if(pass){
                    return 0;
                }
            }
            log.info("::{}::uofIntegrate3","RDSMSG_"+request.getLinkId());
            //整合数据到赛事表
            Long secondsFromStart = 0L;
            if (sportId == 1) {
                secondsFromStart = data.getSecondsMatchStart();
            } else if (sportId == 2||sportId == 5) {
                secondsFromStart = data.getRemainingTime();
            }
            if(secondsFromStart == null ) {secondsFromStart = 0L;}

            MatchEventInfoMessage matchEventInfoMessage = new MatchEventInfoMessage();
            matchEventInfoMessage.setStandardMatchId(data.getStandardMatchId());
            matchEventInfoMessage.setEventCode("uof");
            matchEventInfoMessage.setSecondsFromStart(secondsFromStart);
            matchEventInfoMessage.setEventTime(request.getDataSourceTime());
            matchEventInfoMessage.setMatchPeriodId(data.getPeriod().longValue());
            matchEventInfoMessage.setSportId(data.getSportId());
            iStandardMatchInfoService.updateMatchEventParam(matchEventInfoMessage,2, request.getLinkId());
            //时间推送到前端
            notifyStatic(data.getStandardMatchId(), request.getLinkId(), secondsFromStart, -1,data.getPeriod());
            //缓存赛事信息
            redisClient.hSet(key,"period",String.valueOf(data.getPeriod()));
            redisClient.hSet(key,"lastUofInTime",String.valueOf(nowTime));
            redisClient.expireKey(key,2 * 24 * 60 * 60);
            //mongo维护
            MatchPeriod matchPeriod = getMatchPeriod(data, request.getDataSourceTime(),request);
            sendToMongo(request, matchPeriod);
            sendFakeEventToWs(matchEventInfoMessage,matchPeriod,request);
        } catch (Exception e) {
            log.error("::{}::{},{},{}" ,"RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request) ,e.getMessage(), e);
        }
        return 0;
    }

    private void sendFakeEventToWs(MatchEventInfoMessage matchEventInfoMessage, MatchPeriod matchPeriod, Request<MatchStatisticsInfoDTO> dataRequest){
        try {
            if (StringUtils.isBlank(matchPeriod.getServesFirst())&& 5==sportId) {return;}
            matchEventInfoMessage.setAddition3(matchPeriod.getServesFirst());
            matchEventInfoMessage.setSourceType(0);
            Request<MatchEventInfoMessage> request = new Request<>();
            request.setDataSourceCode(dataRequest.getDataSourceCode());
            request.setDataSourceTime(dataRequest.getDataSourceTime());
            request.setDataType(dataRequest.getDataType());
            request.setGlobalId(dataRequest.getLinkId());
            request.setLinkId(dataRequest.getLinkId());
            request.setData(matchEventInfoMessage);
            sendMessage.sendMessage("FAKE_MATCH_EVENT", null, dataRequest.getLinkId(), request);
        } catch (Exception e) {
            log.error("::{}::{},{},{}" ,"RDSMSG_"+dataRequest.getLinkId(),JsonFormatUtils.toJson(matchEventInfoMessage) ,e.getMessage(), e);
        }
    }
}
