package com.panda.sport.rcs.data.sportStatisticsService.eventStatistics;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.sportStatisticsService.AbstractSuperStatisticsService;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.dto.StatisticsNotifyDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.rcs.data.context.statics.impl
 * @date: 2020-08-19 16:51
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class AbstractEventStatisticsService extends AbstractSuperStatisticsService implements IEventServiceHandle {
    @Autowired
    protected IStandardMatchInfoService iStandardMatchInfoService;
    /*** 影响赛事比分统计指标的事件编码 ***/
    protected List<String> mongoEvents = new ArrayList<>();

    protected static Cache<String, String> cache = Caffeine.newBuilder()
            .initialCapacity(10000)
            .maximumSize(500000)
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();
    private static final String  COMPARE_EVENT_TIME = "compareEventTime";


    /**
     * 得到 matchPeriod
     * @param sportId
     * @param data
     * @return
     */
    protected abstract MatchPeriod getMatchPeriod(Long sportId, MatchEventInfoMessage data,String linkId);

    /***
     *
     * @param matchId
     * @param matchStatisticsInfoDetails
     * @param linkId
     * @param seconds
     * @param channel     -1:   0:
     * @return void
     * @Description 给前端的其他模块发送统计结果
     * @Author V
     * @Date 16:00 2020/8/19
     **/
    protected void notifyStatic(Long matchId, List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, String linkId, Long seconds, Integer channel) {
        //通知统计下发
        StatisticsNotifyDTO statisticsNotifyDTO = new StatisticsNotifyDTO();
        statisticsNotifyDTO.setStandardMatchId(matchId);
        statisticsNotifyDTO.setChannel(channel);
        statisticsNotifyDTO.setGlobalId(linkId);
        if (seconds != null) {statisticsNotifyDTO.setSecondsFromStart(seconds);}
        if (!CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
            statisticsNotifyDTO.setList(matchStatisticsInfoDetails);
        }
        sendMessage.sendMessage(MqConstants.WS_STATISTICS_NOTIFY_TOPIC, null, linkId, statisticsNotifyDTO);
    }

    /**
     * 发送到mongo
     * @param linkId
     * @param matchPeriod
     */
    protected void sendToMongo(String linkId, MatchPeriod matchPeriod) {
        HashMap<String, String> strMap = new HashMap<>();
        strMap.put("linkId", linkId);
        sendMessage.sendMessage(MqConstants.MATCH_PERIOD_CHANGE, null, linkId, matchPeriod, strMap);
        log.info("::{}::matchPeriod存入MQ消息队列:{}","RDMEITG_"+linkId+"_"+matchPeriod.getStandardMatchId(), JsonFormatUtils.toJson(matchPeriod));
    }


    @Override
    public Map standardScore(Request<MatchEventInfoMessage> request) {
        return null;
    }

    /**
     * 事件时间比较
     * @param data
     * @return
     */
    protected boolean eventTimeFilter(MatchEventInfoMessage data,String linkId) {
        try {
            if(null == data || null ==  data.getEventTime()){return true;}
            String key = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, data.getStandardMatchId());
            String compareEventTime = redisClient.hGet(key, COMPARE_EVENT_TIME);
            if(StringUtils.isBlank(compareEventTime)||"null".equals(compareEventTime)){
                redisClient.hSet(key,COMPARE_EVENT_TIME,String.valueOf(data.getEventTime()));
                redisClient.expireKey(key,2 * 24 * 60 * 60);
                return true;
            }
            Long oldTime = Long.valueOf(compareEventTime);
            if(data.getEventTime()>oldTime){
                redisClient.hSet(key,COMPARE_EVENT_TIME,String.valueOf(data.getEventTime()));
                redisClient.expireKey(key,2 * 24 * 60 * 60);
                return true;
            }else{
                log.info("::{}::事件时对比过期{},{},{}","RDMEITG_"+linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId(),data.getEventTime(),oldTime);
                return false;
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDMEITG_"+linkId,JsonFormatUtils.toJson(data),e.getMessage(), e);
        }
        return true;
    }
}
