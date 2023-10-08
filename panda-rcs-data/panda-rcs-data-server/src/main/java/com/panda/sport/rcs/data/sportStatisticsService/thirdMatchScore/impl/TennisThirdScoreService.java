package com.panda.sport.rcs.data.sportStatisticsService.thirdMatchScore.impl;

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
public class TennisThirdScoreService extends AbstractThirdScoreService {

    @Override
    protected void initial() {
        sportId = 5L;
        unFilterScoreData.add("matchScore");
/*        unFilterScoreData.add("breakSuccessCount-0-0");
        unFilterScoreData.add("scoreNumber-0-0");
        unFilterScoreData.add("breakSuccessRate-0-0");
        unFilterScoreData.add("breakPointCount-0-0");
        unFilterScoreData.add("servesFaultCount-0-0");
        unFilterScoreData.add("servesScoredCount-0-0");*/
        unFilterScoreData.add("currentScore");
        unFilterScoreData.add("qiangScore");
        unFilterScoreData.add("setScore");
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
                if("setScore".equals(matchStatisticsInfoDetail.getCode())){
                    if(null!=data.getSecondNum()){
                        matchStatisticsInfoDetail.setSecondNum(data.getSecondNum());
                    }
                }
                if (period.equals("-1")) {
                    matchStatisticsInfoDetail.setFirstNum(0);
                } else if (period.equals("8")) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("9")) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("10")) {
                    matchStatisticsInfoDetail.setFirstNum(3);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("11")) {
                    matchStatisticsInfoDetail.setFirstNum(4);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if (period.equals("12")) {
                    matchStatisticsInfoDetail.setFirstNum(5);
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
                MatchStatisticsInfoDetailSource matchStatisticsInfoDetail = new MatchStatisticsInfoDetailSource();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setDataSourceCode(dataSourceCode);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(code, null));
                try {
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                matchStatisticsInfoDetails.add(matchStatisticsInfoDetail);
            }
        }
        return matchStatisticsInfoDetails;
    }


    @Override
    protected String maping(String key, String period) {
        String code = null;
        switch (key) {
            default:
                code = key;
                break;
        }
        return code;
    }

}
