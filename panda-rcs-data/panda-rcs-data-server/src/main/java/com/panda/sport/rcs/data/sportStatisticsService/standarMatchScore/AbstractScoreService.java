package com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.MatchStatisticsInfoDetailService;
import com.panda.sport.rcs.data.sportStatisticsService.AbstractSuperStatisticsService;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import com.panda.sport.rcs.pojo.dto.StatisticsNotifyDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author V
 */
@Slf4j
public abstract class AbstractScoreService extends AbstractSuperStatisticsService implements IScoreServiceHandle {

    private static final  String SOURCE_TIME_FLAG = "sourceTimeFlag";
    protected static final  String SCORE_INSERT_MYSQL_LOCK = "scoreInsertMysqlLock";

    @Autowired
    protected MatchStatisticsInfoDetailService matchStatisticsInfoDetailService;
    /**
     * getMatchPeriod
     *
     * @param matchStatisticsInfoDetails
     * @param periodId
     * @param linkId
     * @return
     */
    protected abstract MatchPeriod getMatchPeriod(List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, Long standardMatchId, Integer periodId, String linkId);

    /**
     * 适配比分
     *
     * @param request
     * @return
     */
    protected abstract List<MatchStatisticsInfoDetail> buildScore(Request<StandardScoreDto> request);

    /**
     * 给前端的其他模块发送统计结果
     *
     * @param matchId
     * @param matchStatisticsInfoDetails
     * @param linkId
     * @param seconds
     * @param channel
     */
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
     * sendToMongo
     *
     * @param linkId
     * @param matchPeriod
     */
    protected void sendToMongo(String linkId, MatchPeriod matchPeriod) {
        try {
            String val = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, "TIME", matchPeriod.getStandardMatchId()));
            String jsonStr = JsonFormatUtils.toJson(matchPeriod);
            redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, "TIME", matchPeriod.getStandardMatchId()), jsonStr, 7200L);
            //如果修改成功
            if (null == val || !val.equals(jsonStr)) {
                HashMap<String, String> strMap = new HashMap<>();
                strMap.put("linkId", linkId);
                sendMessage.sendMessage(MqConstants.MATCH_PERIOD_CHANGE, null, linkId, matchPeriod, strMap);
            }
            log.info("::{}::Mq-标准赛事统计信息推送成功存库" ,"RDSMSG_"+linkId+"_"+matchPeriod.getStandardMatchId());
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSG_"+linkId,JsonFormatUtils.toJson(matchPeriod),e.getMessage(), e);
        }
    }

    /**
     * code 映射
     * @param key
     * @param period
     * @return
     */
    protected String maping(String key, String period) {
        String code = null;
        switch (key) {
            default:
                code = key;
                break;
        }
        return code;
    }

    /**
     * 过滤重复数据
     * @param oldlist
     * @param sourceTime
     * @param standardMatchId
     * @param map
     * @param request
     * @return
     */
    protected List<MatchStatisticsInfoDetail> filterRepeatData(List<MatchStatisticsInfoDetail> oldlist, Long sourceTime, Long standardMatchId, HashMap<String, String> map, Request<StandardScoreDto> request) {
        List<MatchStatisticsInfoDetail> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldlist)) {
            return list;
        }
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, "scoreFlag", standardMatchId);
        String time = redisClient.hGet(key, SOURCE_TIME_FLAG);
        map.put("scoreFlagTime",time);
        //时间是否过期
        if (StringUtils.isNotBlank(time)) {
            if (sourceTime.longValue() < Long.valueOf(time).longValue()) {
                return list;
            }
        }
        redisClient.hSet(key, SOURCE_TIME_FLAG, String.valueOf(sourceTime));
        for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : oldlist) {
            StringBuilder tyrStb = new StringBuilder().append(
                    matchStatisticsInfoDetail.getStandardMatchId()).append("-")
                    .append(matchStatisticsInfoDetail.getCode()).append("-")
                    .append(matchStatisticsInfoDetail.getFirstNum()).append("-")
                    .append(matchStatisticsInfoDetail.getSecondNum());
            if(filterScoreData(tyrStb,request)){continue;}
            String data = redisClient.hGet(key, tyrStb.toString());
            if (StringUtils.isBlank(data)) {
                list.add(matchStatisticsInfoDetail);
                redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
            }else{
                MatchStatisticsInfoDetail detail = JSONObject.parseObject(data, MatchStatisticsInfoDetail.class);
                if (!(detail.getT1().intValue() == matchStatisticsInfoDetail.getT1().intValue() && detail.getT2().intValue() == matchStatisticsInfoDetail.getT2().intValue())) {
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }
            }
        }
        redisClient.expireKey(key, 3600);
        return list;
    }



    /**
     * 过滤重复数据 透传第二次
     * @param oldlist
     * @param sourceTime
     * @param standardMatchId
     * @param map
     * @param request
     * @return
     */
    protected List<MatchStatisticsInfoDetail> filterRepeatData2(List<MatchStatisticsInfoDetail> oldlist, Long sourceTime, Long standardMatchId, HashMap<String, String> map, Request<StandardScoreDto> request) {
        List<MatchStatisticsInfoDetail> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldlist)) {
            return list;
        }
        String time = map.get("scoreFlagTime");
        //时间是否过期
        if (StringUtils.isNotBlank(time)) {
            if (sourceTime.longValue() < Long.valueOf(time).longValue()) {
                return list;
            }
        }
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, "scoreFlag2", standardMatchId);
        for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : oldlist) {
            StringBuilder tyrStb = new StringBuilder().append(
                    matchStatisticsInfoDetail.getStandardMatchId()).append("-")
                    .append(matchStatisticsInfoDetail.getCode()).append("-")
                    .append(matchStatisticsInfoDetail.getFirstNum()).append("-")
                    .append(matchStatisticsInfoDetail.getSecondNum());
            if(filterScoreData(tyrStb, request)){continue;}
            String data = redisClient.hGet(key, tyrStb.toString());
            if (StringUtils.isBlank(data)) {
                list.add(matchStatisticsInfoDetail);
                matchStatisticsInfoDetail.setTimes(1);
                redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
            }else{
                MatchStatisticsInfoDetail detail = JSONObject.parseObject(data, MatchStatisticsInfoDetail.class);
                if (!(detail.getT1().intValue() == matchStatisticsInfoDetail.getT1().intValue() && detail.getT2().intValue() == matchStatisticsInfoDetail.getT2().intValue())) {
                    matchStatisticsInfoDetail.setTimes(1);
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }else if (null==detail.getTimes()||detail.getTimes()<5){
                    Integer times=null==detail.getTimes()?0:detail.getTimes();
                    matchStatisticsInfoDetail.setTimes(times+1);
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }
            }
        }
        redisClient.expireKey(key, 3600);
        return list;
    }

    private boolean filterScoreData(StringBuilder tyrStb, Request<StandardScoreDto> request) {
        try{
            return !WordsTools.listWordContains(unFilterScoreData,tyrStb.toString());
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(tyrStb),e.getMessage(), e);
            return true;
        }
    }

    /**
     * 过滤重复数据
     * @param oldlist
     * @param sourceTime
     * @param standardMatchId
     * @param map
     * @param request
     * @return
     */
    protected List<MatchStatisticsInfoDetail> filterBaseballRepeatData(List<MatchStatisticsInfoDetail> oldlist, Long sourceTime, Long standardMatchId, HashMap<String, String> map, Request<StandardScoreDto> request) {
        List<MatchStatisticsInfoDetail> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldlist)) {
            return list;
        }
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, "scoreFlag", standardMatchId);
        String time = redisClient.hGet(key, SOURCE_TIME_FLAG);
        map.put("scoreFlagTime",time);
        //时间是否过期
        if (StringUtils.isNotBlank(time)) {
            if (sourceTime.longValue() < Long.valueOf(time).longValue()) {
                return list;
            }
        }
        redisClient.hSet(key, SOURCE_TIME_FLAG, String.valueOf(sourceTime));
        for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : oldlist) {
            StringBuilder tyrStb = new StringBuilder().append(
                    matchStatisticsInfoDetail.getStandardMatchId()).append("-")
                    .append(matchStatisticsInfoDetail.getCode()).append("-")
                    .append(matchStatisticsInfoDetail.getFirstNum()).append("-")
                    .append(matchStatisticsInfoDetail.getSecondNum());
            if(filterScoreData(tyrStb, request)){continue;}
            String data = redisClient.hGet(key, tyrStb.toString());
            if (StringUtils.isBlank(data)) {
                list.add(matchStatisticsInfoDetail);
                redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
            }else{
                MatchStatisticsInfoDetail detail = JSONObject.parseObject(data, MatchStatisticsInfoDetail.class);
                boolean isNon = null == matchStatisticsInfoDetail.getT2();
                if (!(detail.getT1().intValue() == matchStatisticsInfoDetail.getT1().intValue() && (isNon||detail.getT2().intValue() == matchStatisticsInfoDetail.getT2().intValue()))) {
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }
            }
        }
        redisClient.expireKey(key, 3600);
        return list;
    }


    /**
     * 过滤重复数据 透传第二次
     * @param oldlist
     * @param sourceTime
     * @param standardMatchId
     * @param map
     * @param request
     * @return
     */
    protected List<MatchStatisticsInfoDetail> filterBaseballRepeatData2(List<MatchStatisticsInfoDetail> oldlist, Long sourceTime, Long standardMatchId, HashMap<String, String> map, Request<StandardScoreDto> request) {
        List<MatchStatisticsInfoDetail> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldlist)) {
            return list;
        }
        String time = map.get("scoreFlagTime");
        //时间是否过期
        if (StringUtils.isNotBlank(time)) {
            if (sourceTime.longValue() < Long.valueOf(time).longValue()) {
                return list;
            }
        }
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, "scoreFlag2", standardMatchId);
        for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : oldlist) {
            StringBuilder tyrStb = new StringBuilder().append(
                    matchStatisticsInfoDetail.getStandardMatchId()).append("-")
                    .append(matchStatisticsInfoDetail.getCode()).append("-")
                    .append(matchStatisticsInfoDetail.getFirstNum()).append("-")
                    .append(matchStatisticsInfoDetail.getSecondNum());
            if(filterScoreData(tyrStb, request)){continue;}
            String data = redisClient.hGet(key, tyrStb.toString());
            if (StringUtils.isBlank(data)) {
                list.add(matchStatisticsInfoDetail);
                matchStatisticsInfoDetail.setTimes(1);
                redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
            }else{
                MatchStatisticsInfoDetail detail = JSONObject.parseObject(data, MatchStatisticsInfoDetail.class);
                boolean isNon = null == matchStatisticsInfoDetail.getT2();
                if (!(detail.getT1().intValue() == matchStatisticsInfoDetail.getT1().intValue() &&(isNon || detail.getT2().intValue() == matchStatisticsInfoDetail.getT2().intValue()))) {
                    matchStatisticsInfoDetail.setTimes(1);
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }else if (null==detail.getTimes()||detail.getTimes()<5){
                    Integer times=null==detail.getTimes()?0:detail.getTimes();
                    matchStatisticsInfoDetail.setTimes(times+1);
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }
            }
        }
        redisClient.expireKey(key, 3600);
        return list;
    }
}
