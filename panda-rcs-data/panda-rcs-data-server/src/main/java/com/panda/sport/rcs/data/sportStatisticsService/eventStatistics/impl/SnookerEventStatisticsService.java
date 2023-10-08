package com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.impl.StandardMatchInfoServiceImpl;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.AbstractEventStatisticsService;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.MatchPeriod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;


/**
 * 事件比分统计
 * @author Administrator
 */
@Service
@Slf4j
@TraceCrossThread
public class SnookerEventStatisticsService extends AbstractEventStatisticsService {


    private static final String SNOOKER_TIME_CORRECTION = "snookerTimeCorrection";

    //private List<String> margianUpdateEventList = Arrays.asList("time_startstop");

    @Override
    protected void initial() {
        sportId = SportIdEnum.SNOOKER.getId();
        mongoEvents = Arrays.asList("match_status","ball_possession","snooker_score_change","time_startstop");
        StatisticsServiceContext.addEventStaticsService(this);
    }

    @Override
    public Map standardScore(Request<MatchEventInfoMessage> request,int size) {
        try {
            MatchEventInfoMessage data = request.getData();
            if (data.getSportId() != sportId.intValue()) {
                return null;
            }
            boolean b = eventTimeFilter(data,request.getLinkId());
            if(!b){
                return null;
            }
            boolean snookerPeriodJuge = false;
            //只取size ==1 的正常数据  过往全量数据不从这走
            String key1 = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, data.getStandardMatchId());
            String oldPeriod = redisClient.hGet(key1, "period");
            String time445 = redisClient.hGet(key1, "time445");
            if(1==size){
                iStandardMatchInfoService.updateMatchEventParam(data,1, "RDMEITG_"+request.getLinkId());
            }
            /*** 如果mongo事件进行发送***/
            snookerPeriodJuge = null!=data.getMatchPeriodId()&& StandardMatchInfoServiceImpl.passPeriod.contains(data.getMatchPeriodId().intValue());
            String ifPresent = cache.getIfPresent(String.valueOf(data.getStandardMatchId()));
            if (StringUtils.isBlank(ifPresent) || mongoEvents.contains(data.getEventCode())||snookerPeriodJuge) {
                MatchPeriod matchPeriod = getMatchPeriod(data.getSportId(), data,request.getLinkId());
                sendToMongo(request.getLinkId(), matchPeriod);
                cache.put(String.valueOf(data.getStandardMatchId()), "1");
            }
            boolean time445b =false;
            try {
                if((StringUtils.isBlank(time445)||"null".equals(time445))&&445!=data.getMatchPeriodId().intValue()){
                    time445b =true;
                }else{
                    time445b = System.currentTimeMillis() - Long.valueOf(time445) > 1*60*1000 &&  System.currentTimeMillis() - Long.valueOf(time445) < 5*60*1000 &&445!=data.getMatchPeriodId().intValue();
                }
            }catch (Exception e){
                log.info("::{}::445问题{}","RDMEITG_"+request.getLinkId()+"_"+data.getStandardMatchId(),data.getStandardMatchId());
            }
            if ((445!=data.getMatchPeriodId().intValue()&&oldPeriod.equals("445"))||time445b) {
                //BG没有timeout_over  ， 使用time_start重置处理
                MatchEventInfoMessage tempEventData = JSONObject.parseObject(JSONObject.toJSONString(data), MatchEventInfoMessage.class);
                tempEventData.setEventCode("timeout_over");
                sendMessage.sendMessage("RCS_TIME_MARGAIN", null, request.getLinkId(), tempEventData);
            }else if(445==data.getMatchPeriodId().intValue()){
                MatchEventInfoMessage tempEventData = JSONObject.parseObject(JSONObject.toJSONString(data), MatchEventInfoMessage.class);
                tempEventData.setEventCode("timeout");
                sendMessage.sendMessage("RCS_TIME_MARGAIN", null, request.getLinkId(), tempEventData);
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
            matchPeriod.setSetNum(data.getFirstNum());
            matchPeriod.setCreateTime(time);
            if(StringUtils.isNotBlank(data.getAddition3())){
                matchPeriod.setServesFirst(data.getAddition3());
            }
            //斯洛克特殊处理 ---- start ----如果网球缓存有timeout 则不下发事件code到mongo
            String eventFlag = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, SNOOKER_TIME_CORRECTION, data.getStandardMatchId()));
            if (StringUtils.isNotBlank(eventFlag) && "time_startstop_0".equals(eventFlag)) {
                isSetEventCode = false;
                log.info("::{}::斯洛克timout-flag-2:{}","RDMEITG_"+linkId+"_"+data.getStandardMatchId(), data.getStandardMatchId());
            }
            //斯洛克特殊处理 ---- end
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
        }
        return matchPeriod;
    }


}
