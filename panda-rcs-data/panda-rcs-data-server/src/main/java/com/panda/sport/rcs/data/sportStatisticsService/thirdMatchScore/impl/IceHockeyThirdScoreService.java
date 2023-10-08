package com.panda.sport.rcs.data.sportStatisticsService.thirdMatchScore.impl;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class IceHockeyThirdScoreService extends AbstractThirdScoreService {

    /**
     * 赛事阶段集
     */
    private static final List<String> setScores = Arrays.asList("1", "2", "3", "40", "50");

    @Override
    protected void initial() {
        sportId = 4L;
        unFilterScoreData.add("matchScore");
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
        Map<String, Map<String, Object>> map = data.getScores();
        Iterator<Map.Entry<String, Map<String, Object>>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Map<String, Object>> next = entries.next();
            String period = next.getKey();
            Map<String, Object> subMap = next.getValue();
            Iterator<Map.Entry<String, Object>> iterator = subMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next1 = iterator.next();
                String key = next1.getKey();
                Object value = next1.getValue();
                MatchStatisticsInfoDetailSource matchStatisticsInfoDetail = new MatchStatisticsInfoDetailSource();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setDataSourceCode(dataSourceCode);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(key, period));
                try{
                    if(value instanceof Map){
                        Map sSubMap=(Map)value;
                        matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(sSubMap.get("home")))));
                        matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(sSubMap.get("away")))));
                    }else{
                        matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value))));
                    }
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDTMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                if ("-1".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(0);
                } else if ("1".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if ("2".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if ("3".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(3);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if ("40".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(4);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("50".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(5);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else {
                    continue;
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
                    code = key;
                } else if (setScores.contains(period)) {
                    code = "setScore";
                }
                break;
            default:
                code = key;
                break;
        }
        return code;
    }

}
