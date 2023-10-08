package com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.constant.Constants;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.AbstractScoreService;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class FootballScoreService extends AbstractScoreService {

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
        StatisticsServiceContext.addScoreService(this);
    }

    @Override
    public Map standardScore(Request<StandardScoreDto> request) {
        Long standardMatchId = request.getData().getStandardMatchId();
        Integer periodId = request.getData().getPeriodId();
        List<MatchStatisticsInfoDetail> oldlist = buildScore(request);
        HashMap<String,String> map = new HashMap<>();
        List<MatchStatisticsInfoDetail> list =filterRepeatData(oldlist, request.getData().getScoreTime(), standardMatchId,map, request);
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, SCORE_INSERT_MYSQL_LOCK, standardMatchId);
        if(!CollectionUtils.isEmpty(list)){
            try {
                redissonManager.lock(key);
                matchStatisticsInfoDetailService.batchInsertOrUpdate(list);
            } catch (Exception e) {
                log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(request),e.getMessage(), e);
            }finally {
                redissonManager.unlock(key);
            }
        }
        List<MatchStatisticsInfoDetail> list2 =filterRepeatData2(oldlist, request.getData().getScoreTime(), standardMatchId,map, request);
        if(CollectionUtils.isEmpty(list2)){return null;}
        //通知统计下发
        notifyStatic(standardMatchId, list2, request.getLinkId(), null, 1);
        //足球阶段表维护生成
        MatchPeriod matchPeriod = getMatchPeriod(list2, standardMatchId,periodId, request.getLinkId());
        sendToMongo(request.getLinkId(), matchPeriod);
        return null;
    }

    @Override
    protected List<MatchStatisticsInfoDetail> buildScore(Request<StandardScoreDto> request) {
        ArrayList<MatchStatisticsInfoDetail> matchStatisticsInfoDetails = new ArrayList<>();
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
                MatchStatisticsInfoDetail matchStatisticsInfoDetail = new MatchStatisticsInfoDetail();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(key, period));
                try{
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
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
            String minutesScoreKey=String.format(RCS_DATA_KEY_CACHE_KEY, "minutesScoreFlag", standardMatchId);
            String minutesScoreFlag = redisClient.get(minutesScoreKey);
            if(StringUtils.isBlank(minutesScoreFlag)||"null".equals(minutesScoreFlag)){
                initialMinutesScore(standardMatchId,matchStatisticsInfoDetails,time,request);
                redisClient.setExpiry(minutesScoreKey,1,24*60*60L);
            }
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
                    handlerMinutesScore(value,code,time,null,standardMatchId,matchStatisticsInfoDetails,request);
                    continue;
                }
                MatchStatisticsInfoDetail matchStatisticsInfoDetail = new MatchStatisticsInfoDetail();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(code, null));
                try {
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
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
    private void handlerMinutesScore(Map<String, Map<String, Object>> value, String code, long time, String dataSourceCode, long standardMatchId, ArrayList<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, Request<StandardScoreDto> request) {
        try {
            Iterator<Map.Entry<String, Map<String, Object>>> iterator = value.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map<String, Object>> next = iterator.next();
                String code1 = next.getKey();
                Map value2 = next.getValue();
                MatchStatisticsInfoDetail matchStatisticsInfoDetail = new MatchStatisticsInfoDetail();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(mapingMinutesCode(code,code1, null));
                try {
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value2.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value2.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value2),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),code+JsonFormatUtils.toJson(value),e.getMessage(), e);
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

    @Override
    protected MatchPeriod getMatchPeriod(List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, Long standardMatchId, Integer periodId, String linkId) {
        MatchPeriod matchPeriod = new MatchPeriod();
        long time = System.currentTimeMillis();
        if (!CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
            matchPeriod.setSportId(sportId);
            matchPeriod.setPeriod(periodId);
            matchPeriod.setStandardMatchId(standardMatchId);
            matchPeriod.setCreateTime(time);
            Map<String,Map<String,String>> map0 = new HashMap<>();
            for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : matchStatisticsInfoDetails) {
                if (matchStatisticsInfoDetail.getFirstNum() != 0 || matchStatisticsInfoDetail.getSecondNum() != 0) {
                    if ("set_score".equals(matchStatisticsInfoDetail.getCode())) {
                        Integer num = Constants.footballSetMap.get(WordsTools.stringValueOf(periodId));
                        if(null==num){continue;}
                        if(num.intValue()==matchStatisticsInfoDetail.getFirstNum().intValue()){
                            matchPeriod.setSetScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                        }
                    }
                    continue;
                }
                if ("match_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if ("corner_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setCornerScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if ("red_card_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setRedCardScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if ("yellow_card_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setYellowCardScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if ("yellow_red_card_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setRedCardScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if ("extra_time_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setExtraTimeScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if ("penalty_shootout".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setPenaltyShootout(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }else if(matchStatisticsInfoDetail.getCode().contains("minutesGoalScore")||matchStatisticsInfoDetail.getCode().contains("minutesCornerScore")){
                    int period = null==matchStatisticsInfoDetail.getPeriod()?0:matchStatisticsInfoDetail.getPeriod();
                    Map<String, String> map = map0.get(String.valueOf(period));
                    if(null == map){
                        map = new HashMap<>();
                    }
                    map.put(matchStatisticsInfoDetail.getCode(), matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                    map0.put(matchStatisticsInfoDetail.getCode(),map);
                }
                matchPeriod.setScoreMap(map0);
            }
        }
        return matchPeriod;
    }

    /**
     * 初始化分钟比分
     * @param standardMatchId
     * @param matchStatisticsInfoDetails
     * @param time
     * @param request
     */
    private void initialMinutesScore(Long standardMatchId, ArrayList<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, long time, Request<StandardScoreDto> request) {
        try {
            MatchStatisticsInfoDetail minutesCornerScore_15 = new MatchStatisticsInfoDetail();
            minutesCornerScore_15.setStandardMatchId(standardMatchId);
            minutesCornerScore_15.setCode("minutesCornerScore_15");
            minutesCornerScore_15.setFirstNum(0);
            minutesCornerScore_15.setSecondNum(0);
            minutesCornerScore_15.setT1(0);
            minutesCornerScore_15.setT2(0);
            minutesCornerScore_15.setCreateTime(time);
            MatchStatisticsInfoDetail minutesCornerScore_30 = new MatchStatisticsInfoDetail();
            minutesCornerScore_30.setStandardMatchId(standardMatchId);
            minutesCornerScore_30.setCode("minutesCornerScore_30");
            minutesCornerScore_30.setFirstNum(0);
            minutesCornerScore_30.setSecondNum(0);
            minutesCornerScore_30.setT1(0);
            minutesCornerScore_30.setT2(0);
            minutesCornerScore_30.setCreateTime(time);
            MatchStatisticsInfoDetail minutesCornerScore_45 = new MatchStatisticsInfoDetail();
            minutesCornerScore_45.setStandardMatchId(standardMatchId);
            minutesCornerScore_45.setCode("minutesCornerScore_45");
            minutesCornerScore_45.setFirstNum(0);
            minutesCornerScore_45.setSecondNum(0);
            minutesCornerScore_45.setT1(0);
            minutesCornerScore_45.setT2(0);
            minutesCornerScore_45.setCreateTime(time);
            MatchStatisticsInfoDetail minutesCornerScore_60 = new MatchStatisticsInfoDetail();
            minutesCornerScore_60.setStandardMatchId(standardMatchId);
            minutesCornerScore_60.setCode("minutesCornerScore_60");
            minutesCornerScore_60.setFirstNum(0);
            minutesCornerScore_60.setSecondNum(0);
            minutesCornerScore_60.setT1(0);
            minutesCornerScore_60.setT2(0);
            minutesCornerScore_60.setCreateTime(time);
            MatchStatisticsInfoDetail minutesCornerScore_75 = new MatchStatisticsInfoDetail();
            minutesCornerScore_75.setStandardMatchId(standardMatchId);
            minutesCornerScore_75.setCode("minutesCornerScore_75");
            minutesCornerScore_75.setFirstNum(0);
            minutesCornerScore_75.setSecondNum(0);
            minutesCornerScore_75.setT1(0);
            minutesCornerScore_75.setT2(0);
            minutesCornerScore_75.setCreateTime(time);
            MatchStatisticsInfoDetail minutesCornerScore_90 = new MatchStatisticsInfoDetail();
            minutesCornerScore_90.setStandardMatchId(standardMatchId);
            minutesCornerScore_90.setCode("minutesCornerScore_90");
            minutesCornerScore_90.setFirstNum(0);
            minutesCornerScore_90.setSecondNum(0);
            minutesCornerScore_90.setT1(0);
            minutesCornerScore_90.setT2(0);
            minutesCornerScore_90.setCreateTime(time);
            MatchStatisticsInfoDetail minutesGoalScore_15 = new MatchStatisticsInfoDetail();
            minutesGoalScore_15.setStandardMatchId(standardMatchId);
            minutesGoalScore_15.setCode("minutesGoalScore_15");
            minutesGoalScore_15.setFirstNum(0);
            minutesGoalScore_15.setSecondNum(0);
            minutesGoalScore_15.setT1(0);
            minutesGoalScore_15.setT2(0);
            minutesGoalScore_15.setCreateTime(time);
            MatchStatisticsInfoDetail minutesGoalScore_30 = new MatchStatisticsInfoDetail();
            minutesGoalScore_30.setStandardMatchId(standardMatchId);
            minutesGoalScore_30.setCode("minutesGoalScore_30");
            minutesGoalScore_30.setFirstNum(0);
            minutesGoalScore_30.setSecondNum(0);
            minutesGoalScore_30.setT1(0);
            minutesGoalScore_30.setT2(0);
            minutesGoalScore_30.setCreateTime(time);
            MatchStatisticsInfoDetail minutesGoalScore_45 = new MatchStatisticsInfoDetail();
            minutesGoalScore_45.setStandardMatchId(standardMatchId);
            minutesGoalScore_45.setCode("minutesGoalScore_45");
            minutesGoalScore_45.setFirstNum(0);
            minutesGoalScore_45.setSecondNum(0);
            minutesGoalScore_45.setT1(0);
            minutesGoalScore_45.setT2(0);
            minutesGoalScore_45.setCreateTime(time);
            MatchStatisticsInfoDetail minutesGoalScore_60 = new MatchStatisticsInfoDetail();
            minutesGoalScore_60.setStandardMatchId(standardMatchId);
            minutesGoalScore_60.setCode("minutesGoalScore_60");
            minutesGoalScore_60.setFirstNum(0);
            minutesGoalScore_60.setSecondNum(0);
            minutesGoalScore_60.setT1(0);
            minutesGoalScore_60.setT2(0);
            minutesGoalScore_60.setCreateTime(time);
            MatchStatisticsInfoDetail minutesGoalScore_75 = new MatchStatisticsInfoDetail();
            minutesGoalScore_75.setStandardMatchId(standardMatchId);
            minutesGoalScore_75.setCode("minutesGoalScore_75");
            minutesGoalScore_75.setFirstNum(0);
            minutesGoalScore_75.setSecondNum(0);
            minutesGoalScore_75.setT1(0);
            minutesGoalScore_75.setT2(0);
            minutesGoalScore_75.setCreateTime(time);
            MatchStatisticsInfoDetail minutesGoalScore_90 = new MatchStatisticsInfoDetail();
            minutesGoalScore_90.setStandardMatchId(standardMatchId);
            minutesGoalScore_90.setCode("minutesGoalScore_90");
            minutesGoalScore_90.setFirstNum(0);
            minutesGoalScore_90.setSecondNum(0);
            minutesGoalScore_90.setT1(0);
            minutesGoalScore_90.setT2(0);
            minutesGoalScore_90.setCreateTime(time);
            matchStatisticsInfoDetails.add(minutesCornerScore_15);
            matchStatisticsInfoDetails.add(minutesCornerScore_30);
            matchStatisticsInfoDetails.add(minutesCornerScore_45);
            matchStatisticsInfoDetails.add(minutesCornerScore_60);
            matchStatisticsInfoDetails.add(minutesCornerScore_75);
            matchStatisticsInfoDetails.add(minutesCornerScore_90);
            matchStatisticsInfoDetails.add(minutesGoalScore_15);
            matchStatisticsInfoDetails.add(minutesGoalScore_30);
            matchStatisticsInfoDetails.add(minutesGoalScore_45);
            matchStatisticsInfoDetails.add(minutesGoalScore_60);
            matchStatisticsInfoDetails.add(minutesGoalScore_75);
            matchStatisticsInfoDetails.add(minutesGoalScore_90);
        }catch (Exception e){
            log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(matchStatisticsInfoDetails),e.getMessage(), e);
        }
    }


    public static void main(String[] args) {
        MatchStatisticsInfoDetail matchStatisticsInfoDetail = new MatchStatisticsInfoDetail ();
        matchStatisticsInfoDetail.setSecondNum(1);
        if (matchStatisticsInfoDetail.getFirstNum() != 0 || matchStatisticsInfoDetail.getSecondNum() != 0) {
            System.out.println(1);
        }
    }
}
