package com.panda.sport.rcs.data.sportStatisticsService.thirdMatchScore;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.MatchStatisticsInfoDetailSourceService;
import com.panda.sport.rcs.data.sportStatisticsService.AbstractSuperStatisticsService;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import com.panda.sport.rcs.pojo.dto.ThirdStatisticsNotifyDTO;
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
public abstract class AbstractThirdScoreService extends AbstractSuperStatisticsService implements IScoreThirdServiceHandle {

    private static final  String THIRD_SOURCE_TIME_FLAG = "thirdSourceTimeFlag";
    private static final  String WS_THIRD_STATISTICS_NOTIFY_TOPIC = "WS_THIRD_STATISTICS_NOTIFY_TOPIC";
    protected static final  String THIRD_SCORE_INSERT_MYSQL_LOCK = "thirdScoreInsertMysqlLock";

    @Autowired
    protected MatchStatisticsInfoDetailSourceService matchStatisticsInfoDetailSourceService;

    /**
     * 适配比分
     *
     * @param request
     * @param dataSourceCode
     * @return
     */
    protected abstract List<MatchStatisticsInfoDetailSource> buildScore(Request<StandardScoreDto> request, String dataSourceCode);

    /**
     * 给前端的其他模块发送统计结果
     * @param matchId
     * @param matchStatisticsInfoDetails
     * @param linkId
     * @param dataSourceCode
     */
    protected void notifyStatic(Long matchId, List<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetails, String linkId, String dataSourceCode) {
        //通知统计下发
        ThirdStatisticsNotifyDTO statisticsNotifyDTO = new ThirdStatisticsNotifyDTO();
        statisticsNotifyDTO.setStandardMatchId(matchId);
        statisticsNotifyDTO.setGlobalId(linkId);
        statisticsNotifyDTO.setDataSourceCode(dataSourceCode);
        if (CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
            return;
        }
        statisticsNotifyDTO.setMatchStatisticsInfoDetailList(matchStatisticsInfoDetails);
        sendMessage.sendMessage(WS_THIRD_STATISTICS_NOTIFY_TOPIC, null, linkId, statisticsNotifyDTO);
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
     * @param dataSourceCode
     * @param request
     * @return
     */
    protected List<MatchStatisticsInfoDetailSource> filterRepeatData(List<MatchStatisticsInfoDetailSource> oldlist, Long sourceTime, Long standardMatchId, String dataSourceCode, HashMap<String, String> map, Request<StandardScoreDto> request) {
        List<MatchStatisticsInfoDetailSource> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldlist)) {
            return list;
        }
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, "thirdScoreFlag", standardMatchId);
        String time = redisClient.hGet(key, THIRD_SOURCE_TIME_FLAG);
        //时间是否过期
        if (StringUtils.isNotBlank(time)) {
            if (sourceTime.longValue() < Long.valueOf(time).longValue()) {
                return list;
            }
        }
        for (MatchStatisticsInfoDetailSource matchStatisticsInfoDetail : oldlist) {
            StringBuilder tyrStb = new StringBuilder()
                    .append(dataSourceCode).append("-")
                    .append(matchStatisticsInfoDetail.getStandardMatchId()).append("-")
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
        redisClient.hSet(key, THIRD_SOURCE_TIME_FLAG, String.valueOf(sourceTime));
        redisClient.expireKey(key, 3600);
        return list;
    }

    private boolean filterScoreData(StringBuilder tyrStb, Request<StandardScoreDto> request) {
        try{
            return !WordsTools.listWordContains(unFilterScoreData,tyrStb.toString());
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),JsonFormatUtils.toJson(tyrStb),e.getMessage(), e);
            return true;
        }
    }

    /**
     * 过滤重复数据
     * @param oldlist
     * @param sourceTime
     * @param standardMatchId
     * @param dataSourceCode
     * @param request
     * @return
     */
    protected List<MatchStatisticsInfoDetailSource> filterBaseballRepeatData(List<MatchStatisticsInfoDetailSource> oldlist, Long sourceTime, Long standardMatchId, String dataSourceCode, Request<StandardScoreDto> request) {
        List<MatchStatisticsInfoDetailSource> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(oldlist)) {
            return list;
        }
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, "thirdScoreFlag", standardMatchId);
        String time = redisClient.hGet(key, THIRD_SOURCE_TIME_FLAG);
        //时间是否过期
        if (StringUtils.isNotBlank(time)) {
            if (sourceTime.longValue() < Long.valueOf(time).longValue()) {
                return list;
            }
        }
        for (MatchStatisticsInfoDetailSource matchStatisticsInfoDetail : oldlist) {
            StringBuilder tyrStb = new StringBuilder()
                    .append(dataSourceCode).append("-")
                    .append(matchStatisticsInfoDetail.getStandardMatchId()).append("-")
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
                if (!(detail.getT1().intValue() == matchStatisticsInfoDetail.getT1().intValue())) {
                    list.add(matchStatisticsInfoDetail);
                    redisClient.hSet(key, tyrStb.toString(), JsonFormatUtils.toJson(matchStatisticsInfoDetail));
                }
            }
        }
        redisClient.hSet(key, THIRD_SOURCE_TIME_FLAG, String.valueOf(sourceTime));
        redisClient.expireKey(key, 3600);
        return list;
    }

}
