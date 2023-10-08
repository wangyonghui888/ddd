package com.panda.sport.rcs.data.sportStatisticsService.thirdMatchScore.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.thirdMatchScore.AbstractThirdScoreService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetailSource;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class FootballThirdScoreService extends AbstractThirdScoreService {

    @Override
    protected void initial() {
        sportId = 1L;
        //unFilterScoreData.add("attacks_score-0-0");
        unFilterScoreData.add("corner_score");
        //unFilterScoreData.add("dangerous_attack_score-0-0");
        unFilterScoreData.add("freeKickScore-0-0");
        unFilterScoreData.add("match_score");
        unFilterScoreData.add("offside-0-0");
        unFilterScoreData.add("shot-0-0");
        unFilterScoreData.add("penalty_score");
        unFilterScoreData.add("periodScore");
        unFilterScoreData.add("red_card_score");
        unFilterScoreData.add("set_score");
        unFilterScoreData.add("shot-0-0");
        unFilterScoreData.add("shot_off_target_score-0-0");
        unFilterScoreData.add("shot_on_target_score-0-0");
        unFilterScoreData.add("yellow_card_score");
        unFilterScoreData.add("extra_time_score");
        unFilterScoreData.add("penalty_shootout");
        unFilterScoreData.add("penalty_score");
        unFilterScoreData.add("minutesGoalScore");
        unFilterScoreData.add("minutesCornerScore");
        StatisticsServiceContext.addThirdScoreService(this);
    }

    @Override
    public Map standardScore(Request<StandardScoreDto> request) {
        Long standardMatchId = request.getData().getStandardMatchId();
        String dataSourceCode = request.getData().getDataSourceCode();
        List<MatchStatisticsInfoDetailSource> oldlist = buildScore(request, dataSourceCode);
        HashMap<String,String> map = new HashMap<>();
        List<MatchStatisticsInfoDetailSource> list =filterRepeatData(oldlist, request.getDataSourceTime(), standardMatchId, dataSourceCode, map,request);
        String key = String.format(RCS_DATA_KEY_CACHE_KEY,THIRD_SCORE_INSERT_MYSQL_LOCK, standardMatchId);
        if(!CollectionUtils.isEmpty(list)){
            try {
                redissonManager.lock(key);
                matchStatisticsInfoDetailSourceService.batchInsertOrUpdate(list,dataSourceCode);
            } catch (Exception e) {
                log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request),e.getMessage(), e);
            }finally {
                redissonManager.unlock(key);
            }
        }
        if(CollectionUtils.isEmpty(list)){return null;}
        //通知统计下发
        notifyStatic(standardMatchId, list, request.getLinkId(), dataSourceCode);
        return null;
    }

    @Override
    protected List<MatchStatisticsInfoDetailSource> buildScore(Request<StandardScoreDto> request, String dataSourceCode) {
        ArrayList<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetails = new ArrayList<>();
        StandardScoreDto data = request.getData();
        long time = System.currentTimeMillis();
        Long standardMatchId = data.getStandardMatchId();
        Map<String, Map<String, Object>> scores = data.getScores();
        Map<String, Map<String, Map>> map = JSONObject.parseObject(JsonFormatUtils.toJson(scores), new TypeReference<Map<String, Map<String, Map>>>(){});
        Iterator<Map.Entry<String, Map<String, Map>>> entries = map.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, Map<String, Map>> next = entries.next();
            String period = next.getKey();
            Map<String, Map> subMap = next.getValue();
            Iterator<Map.Entry<String, Map>> iterator = subMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map> next1 = iterator.next();
                String key = next1.getKey();
                Map value = next1.getValue();
                MatchStatisticsInfoDetailSource matchStatisticsInfoDetail = new MatchStatisticsInfoDetailSource();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setDataSourceCode(dataSourceCode);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(key, period));
                try {
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                if (period.equals("-1")) {
                    matchStatisticsInfoDetail.setFirstNum(0);
                    matchStatisticsInfoDetail.setSecondNum(0);
                } else if (period.equals("6")) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("7")) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("41")) {
                    matchStatisticsInfoDetail.setFirstNum(3);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("42")) {
                    matchStatisticsInfoDetail.setFirstNum(4);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }
                /*else if(period.equals("50")){
                    if(key.equals("goal")){
                        matchStatisticsInfoDetail.setFirstNum(0);
                        matchStatisticsInfoDetail.setSecondNum(0);
                        matchStatisticsInfoDetail.setCode("penalty_shootout");
                    }
                }*/ else {
                    continue;
                }
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }

        Map<String, Object> allScores = data.getAllScores();
        if(!CollectionUtils.isEmpty(allScores)) {
            Map<String, Map<String, Object>> allScoresMap = JSONObject.parseObject(JsonFormatUtils.toJson(allScores), new TypeReference<Map<String, Map<String, Object>>>() {
            });
            Iterator<Map.Entry<String, Map<String, Object>>> iterator = allScoresMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map<String, Object>> next = iterator.next();
                String code = next.getKey();
                Map value = next.getValue();
                if(CollectionUtils.isEmpty(value)){continue;};
                //足于分钟处理
                if("minutesGoalScore".equals(code)||"minutesCornerScore".equals(code)){
                    handlerMinutesScore(value,code,time,dataSourceCode,standardMatchId,matchStatisticsInfoDetails,request);
                    continue;
                }
                MatchStatisticsInfoDetailSource matchStatisticsInfoDetail = new MatchStatisticsInfoDetailSource();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setDataSourceCode(dataSourceCode);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(code, null));
                try {
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value.get("away")))));
                } catch (Exception e) {
                    log.error(e.getMessage() + JSON.toJSON(value), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }
        return matchStatisticsInfoDetails;
    }


    /**
     * 处理分钟分数
     * @param value
     * @param matchStatisticsInfoDetails
     * @param request
     */
    private void handlerMinutesScore(Map<String, Map<String, Object>> value, String code, long time, String dataSourceCode, long standardMatchId, ArrayList<MatchStatisticsInfoDetailSource> matchStatisticsInfoDetails, Request<StandardScoreDto> request) {
        try {
            Iterator<Map.Entry<String, Map<String, Object>>> iterator = value.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map<String, Object>> next = iterator.next();
                String code1 = next.getKey();
                Map value2 = next.getValue();
                MatchStatisticsInfoDetailSource matchStatisticsInfoDetail = new MatchStatisticsInfoDetailSource();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setDataSourceCode(dataSourceCode);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(mapingMinutesCode(code,code1, null));
                try {
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value2.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value2.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }catch (Exception e) {
            log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),code+JsonFormatUtils.toJson(value),e.getMessage(), e);
        }
    }

    private String mapingMinutesCode(String code, String code1, Object o1) {
        String code3 = null;
        switch (code1) {
            case "60899":
                code3 = code+"_15";
                break;
            case "61799":
                code3 = code+"_30";
                break;
            case "62699":
                code3 = code+"_45";
                break;
            case "73599":
                code3 = code+"_60";
                break;
            case "74499":
                code3 = code+"_75";
                break;
            case "75399":
                code3 = code+"_90";
                break;
        }
        return code3;
    }

    @Override
    protected String maping(String key, String period) {
        String code = null;
        switch (key) {
            case "goal":
                if (period.equals("-1")) {
                    code = "match_score";
                } else {
                    code = "set_score";
                }
                break;
            case "redCard":
                code = "red_card_score";
                break;
            case "yellowCard":
                code = "yellow_card_score";
                break;
            case "penaltyAwarded":
                code = "penalty_score";
                break;
            case "corner":
                code = "corner_score";
                break;
            case "attack":
                code = "attacks_score";
                break;
            case "dangerousAttack":
                code = "dangerous_attack_score";
                break;
            case "possession":
                code = "possession";
                break;
            case "shotOn":
                code = "shot_on_target_score";
                break;
            case "shotOff":
                code = "shot_off_target_score";
                break;
            case "overtimeScore":
                code = "extra_time_score";
                break;
            case "penaltyShootout":
                code = "penalty_shootout";
                break;
            default:
                code = key;
                break;
        }
        return code;
    }

}
