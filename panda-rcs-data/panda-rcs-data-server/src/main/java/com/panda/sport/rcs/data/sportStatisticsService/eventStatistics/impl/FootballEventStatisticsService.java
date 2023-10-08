package com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.impl;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.eventStatistics.AbstractEventStatisticsService;
import com.panda.sport.rcs.pojo.MatchPeriod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * 事件比分统计
 * @author Administrator
 */
@Service
@Slf4j
@TraceCrossThread
public class FootballEventStatisticsService extends AbstractEventStatisticsService {

    private static final String  RCS_EVENT_TO_ORDER_TOPIC = "RCS_EVENT_TO_ORDER_TOPIC";

    @Override
    protected void initial() {
        sportId = 1L;
        mongoEvents = Arrays.asList("match_status","goal","canceled_goal");
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
            if(1==size) {
                iStandardMatchInfoService.updateMatchEventParam(data, 1, "RDMEITG_"+request.getLinkId());
                /**时间通知*/
                notifyStatic(data.getStandardMatchId(), null, request.getLinkId(), data.getSecondsFromStart(), -1);
            }
            //足球阶段表维护生成
            MatchPeriod matchPeriod = getMatchPeriod( request.getData().getSportId(), data,request.getLinkId());
            String ifPresent = cache.getIfPresent(String.valueOf(data.getStandardMatchId()));
            if(StringUtils.isBlank(ifPresent)|| mongoEvents.contains(data.getEventCode())||(null!=data.getIsErrorEndEvent()&&1==data.getIsErrorEndEvent())){
                sendToMongo(request.getLinkId(), matchPeriod);
                cache.put(String.valueOf(data.getStandardMatchId()),"1");
            }
            sendToOrder(request.getLinkId(),data);
            return null;
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDMEITG_"+request.getLinkId(), JsonFormatUtils.toJson(request), e.getMessage(),e);
            return null;
        }
    }

    /**
     * 发送到订单
     * @param linkId
     * @param data
     */
    private void sendToOrder(String linkId, MatchEventInfoMessage data) {
        try {
            if("goal".equals(data.getEventCode())||"canceled_goal".equals(data.getEventCode())){
                HashMap<String, String> strMap = new HashMap<>();
                strMap.put("linkId", linkId);
                sendMessage.sendMessage(RCS_EVENT_TO_ORDER_TOPIC, null, linkId, data, strMap);
            }
        }catch (Exception e){
            log.error("::{}::{},{},{}","RDMEITG_"+linkId,JsonFormatUtils.toJson(data),e.getMessage(),e);
        }
    }

    @Override
    protected MatchPeriod getMatchPeriod( Long sportId, MatchEventInfoMessage data,String linkId) {
        MatchPeriod matchPeriod = new MatchPeriod();
        long time = System.currentTimeMillis();
        if (data != null) {
            matchPeriod.setSportId(data.getSportId());
            matchPeriod.setStandardMatchId(data.getStandardMatchId());
            matchPeriod.setPeriod(data.getMatchPeriodId().intValue());
            matchPeriod.setCreateTime(time);
            matchPeriod.setEventCode(data.getEventCode());
            matchPeriod.setEventTime(data.getEventTime());
            matchPeriod.setSecondsFromStart(data.getSecondsFromStart());
        }
        return matchPeriod;
    }


}
