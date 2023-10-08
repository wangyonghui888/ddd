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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BasketballScoreService extends AbstractScoreService {


    private static final List<String> setScores = Arrays.asList("13", "14", "15", "16", "40");
    private static final List<String> periodScores = Arrays.asList("1", "2", "3");

    @Override
    protected void initial() {
        sportId = 2L;
        unFilterScoreData.add("match_score");
        unFilterScoreData.add("period_score");
        unFilterScoreData.add("set_score");
        StatisticsServiceContext.addScoreService(this);
    }

    @Override
    public Map standardScore(Request<StandardScoreDto> request) {
        Long standardMatchId = request.getData().getStandardMatchId();
        Integer periodId = request.getData().getPeriodId();
        List<MatchStatisticsInfoDetail> oldlist = buildScore(request);
        HashMap<String,String> map = new HashMap<>();
        List<MatchStatisticsInfoDetail> list =filterRepeatData(oldlist,request.getData().getScoreTime(),standardMatchId, map, request);
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
        List<MatchStatisticsInfoDetail> list2 =filterRepeatData2(oldlist, request.getData().getScoreTime(), standardMatchId,map,request);
        if(CollectionUtils.isEmpty(list2)){return null;}
        //通知统计下发
        notifyStatic(standardMatchId, list2, request.getLinkId(), null, 1);
        //足球阶段表维护生成
        MatchPeriod matchPeriod = getMatchPeriod(list2, standardMatchId, periodId, request.getLinkId());
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
                String key1 = next1.getKey();
                Map value = next1.getValue();
                MatchStatisticsInfoDetail matchStatisticsInfoDetail = new MatchStatisticsInfoDetail();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(key1, period));
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
                } else if (period.equals("13")) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("14")) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("15")) {
                    matchStatisticsInfoDetail.setFirstNum(3);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("16")) {
                    matchStatisticsInfoDetail.setFirstNum(4);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("40")) {
                    matchStatisticsInfoDetail.setFirstNum(5);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("1")) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("2")) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setSecondNum(0);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else {
                    continue;
                }
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }

        Map<String, Object> allScores = data.getAllScores();
        if(!CollectionUtils.isEmpty(allScores)){
            Map<String, Map<String, Object>> allScoresMap = JSONObject.parseObject(JsonFormatUtils.toJson(allScores), new TypeReference<Map<String, Map<String, Object>>>() {});
            Iterator<Map.Entry<String, Map<String, Object>>> iterator = allScoresMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Map<String, Object>> next = iterator.next();
                String code = next.getKey();
                Map value = next.getValue();
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
                if (code.equals("periodScore")) {
                    matchStatisticsInfoDetail.setFirstNum(0);
                    matchStatisticsInfoDetail.setSecondNum(0);
                } else if (code.equals("periodOneScore")) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setSecondNum(0);
                } else if (code.equals("periodTwoScore")) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setSecondNum(0);
                }
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }
        return matchStatisticsInfoDetails;
    }

    @Override
    protected String maping(String key, String period) {
        String code = null;
        switch (key) {
            case "matchScore":
                if (period.equals("-1")) {
                    code = "match_score";
                } else if (setScores.contains(period)) {
                    code = "set_score";
                } else if (periodScores.contains(period)) {
                    code = "period_score";
                }
                break;
            case "periodOneScore":
            case "periodTwoScore":
                code = "period_score";
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
            for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : matchStatisticsInfoDetails) {
                if ("period_score".equals(matchStatisticsInfoDetail.getCode())) {
                    Integer num = Constants.basketballPeriodMap.get(WordsTools.stringValueOf(periodId));
                    if(null==num){continue;}
                    if(num.intValue()==matchStatisticsInfoDetail.getFirstNum().intValue()){
                        matchPeriod.setPeriodScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                    }
                } else if ("set_score".equals(matchStatisticsInfoDetail.getCode())) {
                    Integer num = Constants.basketballSetMap.get(WordsTools.stringValueOf(periodId));
                    if(null==num){continue;}
                    if(num.intValue()==matchStatisticsInfoDetail.getFirstNum().intValue()){
                        matchPeriod.setSetScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                    }
                } else if ("match_score".equals(matchStatisticsInfoDetail.getCode())) {
                    matchPeriod.setScore(matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                }
            }
        }
        return matchPeriod;
    }



}
