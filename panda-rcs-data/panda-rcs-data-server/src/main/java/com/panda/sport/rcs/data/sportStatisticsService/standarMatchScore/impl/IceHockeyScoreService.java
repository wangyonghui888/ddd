package com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.AbstractScoreService;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import com.panda.sport.rcs.pojo.vo.WsScoreVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class IceHockeyScoreService extends AbstractScoreService {

    private static final String WORD_SECOND_NUM = "secondNum";
    /**
     * 赛事阶段集
     */
    private static final List<String> setScores = Arrays.asList("1", "2", "3", "40", "50");


    @Override
    protected void initial() {
        sportId = 4L;
        unFilterScoreData.add("matchScore");
        unFilterScoreData.add("setScore");
        StatisticsServiceContext.addScoreService(this);
    }

    @Override
    public Map standardScore(Request<StandardScoreDto> request) {
        Long standardMatchId = request.getData().getStandardMatchId();
        Integer periodId = request.getData().getPeriodId();
        List<MatchStatisticsInfoDetail> oldlist = buildScore(request);
        HashMap<String,String> map = new HashMap<>();
        List<MatchStatisticsInfoDetail> list =filterRepeatData(oldlist, request.getData().getScoreTime(), standardMatchId, map, request);
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
                WsScoreVO wsScoreVO = new WsScoreVO();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(key, period));
                buildWsScoreVO(value,wsScoreVO,period,matchStatisticsInfoDetail.getCode(),data.getSecondNum(),request);
                matchStatisticsInfoDetail.setScoreVO(wsScoreVO);
                try{
                    matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value.get("home")))));
                    matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(value.get("away")))));
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                if("setScore".equals(matchStatisticsInfoDetail.getCode())){
                    matchStatisticsInfoDetail.setSecondNum(data.getSecondNum());
                }
                if (period.equals("-1")) {
                    matchStatisticsInfoDetail.setFirstNum(0);
                } else if (period.equals("1")) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("2")) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("3")) {
                    matchStatisticsInfoDetail.setFirstNum(3);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if (period.equals("40")) {
                    matchStatisticsInfoDetail.setFirstNum(4);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if (period.equals("50")) {
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

        return matchStatisticsInfoDetails;
    }

    /**
     * 构建
     * @param value
     * @param wsScoreVO
     * @param period
     * @param key
     * @param secondNum
     * @param request
     */
    private void buildWsScoreVO(Map value, WsScoreVO wsScoreVO, String period, String key, Integer secondNum, Request<StandardScoreDto> request) {
        try {
            if(!"-1".equals(period)){
                wsScoreVO.setPeriod(Integer.valueOf(period));
            }
            if (period.equals("-1")) {
                wsScoreVO.setFirstNum(0);
                wsScoreVO.setSecondNum(0);
            } else if (period.equals("1")) {
                wsScoreVO.setFirstNum(1);
                wsScoreVO.setSecondNum(0);
            } else if (period.equals("2")) {
                wsScoreVO.setFirstNum(2);
                wsScoreVO.setSecondNum(0);
            } else if (period.equals("3")) {
                wsScoreVO.setFirstNum(3);
                wsScoreVO.setSecondNum(0);
            } else if (period.equals("40")) {
                wsScoreVO.setFirstNum(4);
                wsScoreVO.setSecondNum(0);
            } else if (period.equals("50")) {
                wsScoreVO.setFirstNum(5);
                wsScoreVO.setSecondNum(0);
            }
            String score = value.get("home") + ":" + value.get("away");
            if ("matchScore".equals(key)) {
                wsScoreVO.setMatchScore(score);
            } else if ("setScore".equals(key)) {
                wsScoreVO.setSetScore(score);
                wsScoreVO.setSecondNum(secondNum);
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
        }
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

    @Override
    protected MatchPeriod getMatchPeriod(List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, Long standardMatchId, Integer periodId, String linkId) {
        try {
            MatchPeriod matchPeriod = new MatchPeriod();
            long time = System.currentTimeMillis();
            if (!CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
                matchPeriod.setSportId(sportId);
                if (301 == periodId.intValue()) {
                    matchPeriod.setPeriod(1);
                } else if (302 == periodId.intValue()) {
                    matchPeriod.setPeriod(2);
                } else if (32 == periodId.intValue()) {
                    matchPeriod.setPeriod(3);
                } else if (304 == periodId.intValue()) {
                    matchPeriod.setPeriod(11);
                } else {
                    matchPeriod.setPeriod(periodId);
                }
                matchPeriod.setStandardMatchId(standardMatchId);
                matchPeriod.setCreateTime(time);
                if (CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
                    return matchPeriod;
                }
                Map<String,Map<String,String>> map0 = new HashMap<>();
                for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : matchStatisticsInfoDetails) {
                    int period = null==matchStatisticsInfoDetail.getPeriod()?0:matchStatisticsInfoDetail.getPeriod();
                    Map<String, String> map = map0.get(String.valueOf(period));
                    if(null == map){
                        map = new HashMap<>();
                    }
                    map.put(matchStatisticsInfoDetail.getCode(), matchStatisticsInfoDetail.getT1() + ":" + matchStatisticsInfoDetail.getT2());
                    map0.put(String.valueOf(period),map);
                }
                matchPeriod.setScoreMap(map0);
            }
            return matchPeriod;
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSG_"+linkId,JsonFormatUtils.toJson(matchStatisticsInfoDetails),e.getMessage(), e);
        }
        return null;
    }
}




