package com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.impl;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.impl.StandardMatchInfoServiceImpl;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.AbstractEventStatisticsService;
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
public class BaseballEventStatisticsService extends AbstractEventStatisticsService {


    private static final String BASEBALL_TIME_CORRECTION = "baseballTimeCorrection";



    @Override
    protected void initial() {
        sportId = 3L;
        mongoEvents = Arrays.asList("match_status","run_scored","batter_advances_to_base_x","runner_advances_to_base_x","who_throws_the_first_pitch");
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
            //只取size ==1 的正常数据  过往全量数据不从这走
            String key1 = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, data.getStandardMatchId());
            //棒球局暂停
            boolean baseballPeriodJuge = false;
            String oldPeriod = redisClient.hGet(key1, "period");
            iStandardMatchInfoService.updateMatchEventParam(data,1, "RDMEITG_"+request.getLinkId());
            /*** 如果mongo事件进行发送***/
            baseballPeriodJuge = StringUtils.isNotBlank(oldPeriod)&& StandardMatchInfoServiceImpl.BASEBALL_PUASE.contains(oldPeriod);
            String ifPresent = cache.getIfPresent(String.valueOf(data.getStandardMatchId()));
            if (StringUtils.isBlank(ifPresent) || mongoEvents.contains(data.getEventCode())||baseballPeriodJuge) {
                MatchPeriod matchPeriod = getMatchPeriod(data.getSportId(), data,request.getLinkId());
                sendToMongo(request.getLinkId(), matchPeriod);
                cache.put(String.valueOf(data.getStandardMatchId()), "1");
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
            matchPeriod.setSetNum(data.getSecondNum());
            matchPeriod.setCreateTime(time);
            if("who_throws_the_first_pitch".equals(data.getEventCode())){
                matchPeriod.setServesFirst(data.getHomeAway());
            }
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
