package com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.impl;

import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.sportStatisticsService.StatisticsServiceContext;
import com.panda.sport.rcs.data.sportStatisticsService.standarMatchScore.AbstractScoreService;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.dto.StandardScoreDto;
import com.panda.sport.rcs.pojo.vo.WsScoreVO;
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
public class BaseballScoreService extends AbstractScoreService {

    public static List<String> pre5Period = Arrays.asList("401","421","402","422","403","423","404","424","405","425","406","426","407","427","408","428","409","429","410");

    @Override
    protected void initial() {
        sportId = 3L;
        unFilterScoreData.add("firstBase");
        unFilterScoreData.add("secondBase");
        unFilterScoreData.add("thirdBase");
        unFilterScoreData.add("setScore");
        unFilterScoreData.add("matchScore");
        StatisticsServiceContext.addScoreService(this);
    }

    @Override
    public Map standardScore(Request<StandardScoreDto> request) {
        Long standardMatchId = request.getData().getStandardMatchId();
        Integer periodId = request.getData().getPeriodId();
        List<MatchStatisticsInfoDetail> oldlist = buildScore(request);
        pre5Handle(oldlist,periodId);
        HashMap<String,String> map = new HashMap<>();
        List<MatchStatisticsInfoDetail> list = filterBaseballRepeatData(oldlist, request.getData().getScoreTime(), standardMatchId, map,request);
        String key = String.format(RCS_DATA_KEY_CACHE_KEY, SCORE_INSERT_MYSQL_LOCK, standardMatchId);
        if(!CollectionUtils.isEmpty(list)){
            try {
                redissonManager.lock(key);
                matchStatisticsInfoDetailService.batchInsertOrUpdate(list);
            } catch (Exception e) {
                log.error("::{}::{},{}","RDSMSG_"+request.getLinkId(),e.getMessage(), e);
            }finally {
                redissonManager.unlock(key);
            }
        }
        List<MatchStatisticsInfoDetail> list2 = filterBaseballRepeatData2(oldlist, request.getData().getScoreTime(), standardMatchId,map,request);
        if(CollectionUtils.isEmpty(list2)){return null;}
        //通知统计下发
        notifyStatic(standardMatchId, list2, request.getLinkId(), null, 1);
        //足球阶段表维护生成
        MatchPeriod matchPeriod = getMatchPeriod(list2, standardMatchId,periodId, request.getLinkId());
        sendToMongo(request.getLinkId(), matchPeriod);
        return null;
    }

    /**
     * 前5分处理
     * @param oldlist
     * @param periodId
     */
    private void pre5Handle(List<MatchStatisticsInfoDetail> oldlist, Integer periodId) {
        if(CollectionUtils.isEmpty(oldlist)||!pre5Period.contains(String.valueOf(periodId))){return;}
        List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails = BeanCopyUtils.deepCopyPropertiesOfList(oldlist, MatchStatisticsInfoDetail.class);
        for (MatchStatisticsInfoDetail matchStatisticsInfoDetail : matchStatisticsInfoDetails) {
            if(0!=matchStatisticsInfoDetail.getFirstNum()){continue;}
            matchStatisticsInfoDetail.setFirstNum(505);
            matchStatisticsInfoDetail.setPeriod(505);
            oldlist.add(matchStatisticsInfoDetail);
        }
    }

    @Override
    protected List<MatchStatisticsInfoDetail> buildScore(Request<StandardScoreDto> request) {
        ArrayList<MatchStatisticsInfoDetail> matchStatisticsInfoDetails = new ArrayList<>();
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
                MatchStatisticsInfoDetail matchStatisticsInfoDetail = new MatchStatisticsInfoDetail();
                WsScoreVO wsScoreVO = new WsScoreVO();
                matchStatisticsInfoDetail.setCreateTime(time);
                matchStatisticsInfoDetail.setStandardMatchId(standardMatchId);
                matchStatisticsInfoDetail.setCode(maping(key, period));
                buildWsScoreVO(value,wsScoreVO,period,key,data.getSecondNum(),request.getLinkId());
                matchStatisticsInfoDetail.setScoreVO(wsScoreVO);
                try{
                    if(value instanceof Map){
                        Map sSubMap=(Map)value;
                        matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(sSubMap.get("home")))));
                        matchStatisticsInfoDetail.setT2(Math.round(Float.parseFloat(String.valueOf(sSubMap.get("away")))));
                    }else{
                        matchStatisticsInfoDetail.setT1(Math.round(Float.parseFloat(String.valueOf(value))));
                    }
                } catch (Exception e) {
                    log.error("::{}::{},{},{}","RDSMSG_"+request.getLinkId(),JsonFormatUtils.toJson(value),e.getMessage(), e);
                }
                matchStatisticsInfoDetail.setFirstNum(0);
                matchStatisticsInfoDetail.setSecondNum(0);
                if ("-1".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(0);
                } else if ("401".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(1);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if ("402".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(2);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if ("403".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(3);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                } else if ("404".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(4);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("405".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(5);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("406".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(6);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("407".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(7);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("408".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(8);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("409".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(9);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("410".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(10);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("411".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(11);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("412".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(12);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("413".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(13);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("414".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(14);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("415".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(15);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("416".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(16);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("417".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(17);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("418".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(18);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41910".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(19);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42010".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(20);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41911".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(21);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42011".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(22);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41912".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(23);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42012".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(24);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41913".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(25);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42013".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(26);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41914".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(27);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42014".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(28);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41915".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(29);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42015".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(30);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41916".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(31);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42016".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(32);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41917".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(33);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42017".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(34);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41918".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(35);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42018".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(36);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41919".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(37);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42019".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(38);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("41920".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(39);
                    matchStatisticsInfoDetail.setPeriod(Integer.valueOf(period));
                }else if ("42020".equals(period)) {
                    matchStatisticsInfoDetail.setFirstNum(40);
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
     * @param linkId
     */
    private void buildWsScoreVO(Object value, WsScoreVO wsScoreVO, String period, String key, Integer secondNum, String linkId) {
        try {
            if ("-1".equals(period)) {
                wsScoreVO.setFirstNum(0);
            } else if ("401".equals(period)) {
                wsScoreVO.setFirstNum(1);
            } else if ("402".equals(period)) {
                wsScoreVO.setFirstNum(2);
            } else if ("403".equals(period)) {
                wsScoreVO.setFirstNum(3);
            } else if ("404".equals(period)) {
                wsScoreVO.setFirstNum(4);
            } else if ("405".equals(period)) {
                wsScoreVO.setFirstNum(5);
            } else if ("406".equals(period)) {
                wsScoreVO.setFirstNum(6);
            } else if ("407".equals(period)) {
                wsScoreVO.setFirstNum(7);
            } else if ("408".equals(period)) {
                wsScoreVO.setFirstNum(8);
            } else if ("409".equals(period)){
                wsScoreVO.setFirstNum(9);
            } else if ("410".equals(period)) {
                wsScoreVO.setFirstNum(10);
            } else if ("411".equals(period)) {
                wsScoreVO.setFirstNum(11);
            } else if ("412".equals(period)) {
                wsScoreVO.setFirstNum(12);
            } else if ("413".equals(period)) {
                wsScoreVO.setFirstNum(13);
            } else if ("414".equals(period)) {
                wsScoreVO.setFirstNum(14);
            } else if ("415".equals(period)) {
                wsScoreVO.setFirstNum(15);
            } else if ("416".equals(period)) {
                wsScoreVO.setFirstNum(16);
            } else if ("417".equals(period)) {
                wsScoreVO.setFirstNum(17);
            } else if ("418".equals(period)) {
                wsScoreVO.setFirstNum(18);
            } else if ("41910".equals(period)) {
                wsScoreVO.setFirstNum(19);
            } else if ("42010".equals(period)) {
                wsScoreVO.setFirstNum(20);
            } else if ("41911".equals(period)) {
                wsScoreVO.setFirstNum(21);
            } else if ("42011".equals(period)) {
                wsScoreVO.setFirstNum(22);
            } else if ("41912".equals(period)) {
                wsScoreVO.setFirstNum(23);
            } else if ("42012".equals(period)) {
                wsScoreVO.setFirstNum(24);
            } else if ("41913".equals(period)) {
                wsScoreVO.setFirstNum(25);
            } else if ("42013".equals(period)) {
                wsScoreVO.setFirstNum(26);
            } else if ("41914".equals(period)) {
                wsScoreVO.setFirstNum(27);
            } else if ("42014".equals(period)) {
                wsScoreVO.setFirstNum(28);
            } else if ("41915".equals(period)) {
                wsScoreVO.setFirstNum(29);
            } else if ("42015".equals(period)) {
                wsScoreVO.setFirstNum(30);
            } else if ("41916".equals(period)) {
                wsScoreVO.setFirstNum(31);
            } else if ("42016".equals(period)) {
                wsScoreVO.setFirstNum(32);
            } else if ("41917".equals(period)) {
                wsScoreVO.setFirstNum(33);
            } else if ("42017".equals(period)) {
                wsScoreVO.setFirstNum(34);
            } else if ("41918".equals(period)) {
                wsScoreVO.setFirstNum(35);
            } else if ("42018".equals(period)) {
                wsScoreVO.setFirstNum(36);
            } else if ("41919".equals(period)) {
                wsScoreVO.setFirstNum(37);
            } else if ("42019".equals(period)) {
                wsScoreVO.setFirstNum(38);
            } else if ("41920".equals(period)) {
                wsScoreVO.setFirstNum(39);
            } else if ("42020".equals(period)) {
                wsScoreVO.setFirstNum(40);
            }
            String score;
            if(value instanceof Map){
                Map sSubMap=(Map)value;
                score = sSubMap.get("home") + ":" + sSubMap.get("away");
            }else{
                score = String.valueOf(value);

            }
            if ("matchScore".equals(key)) {
                wsScoreVO.setMatchScore(score);
            } else if ("setScore".equals(key)) {
                wsScoreVO.setSetScore(score);
                wsScoreVO.setSecondNum(secondNum);
            }
        } catch (Exception e) {
            log.error("::{}::{},{},{}","RDSMSG_"+linkId, JsonFormatUtils.toJson(value),e.getMessage(), e);
        }
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

    @Override
    protected MatchPeriod getMatchPeriod(List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails, Long standardMatchId, Integer periodId, String linkId) {
        try {
            MatchPeriod matchPeriod = new MatchPeriod();
            long time = System.currentTimeMillis();
            if (!CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
                matchPeriod.setSportId(sportId);
                if (421 == periodId.intValue()) {
                    matchPeriod.setPeriod(401);
                } else if (422 == periodId.intValue()) {
                    matchPeriod.setPeriod(402);
                } else if (423 == periodId.intValue()) {
                    matchPeriod.setPeriod(403);
                } else if (424 == periodId.intValue()) {
                    matchPeriod.setPeriod(404);
                } else if (425 == periodId.intValue()) {
                    matchPeriod.setPeriod(405);
                } else if (426 == periodId.intValue()) {
                    matchPeriod.setPeriod(406);
                } else if (427 == periodId.intValue()) {
                    matchPeriod.setPeriod(407);
                } else if (428 == periodId.intValue()) {
                    matchPeriod.setPeriod(408);
                } else if (429 == periodId.intValue()) {
                    matchPeriod.setPeriod(409);
                } else if (430 == periodId.intValue()) {
                    matchPeriod.setPeriod(410);
                } else if (431 == periodId.intValue()) {
                    matchPeriod.setPeriod(411);
                } else if (432 == periodId.intValue()) {
                    matchPeriod.setPeriod(412);
                } else if (433 == periodId.intValue()) {
                    matchPeriod.setPeriod(413);
                } else if (434 == periodId.intValue()) {
                    matchPeriod.setPeriod(414);
                } else if (435 == periodId.intValue()) {
                    matchPeriod.setPeriod(415);
                } else if (436 == periodId.intValue()) {
                    matchPeriod.setPeriod(416);
                } else if (437 == periodId.intValue()) {
                    matchPeriod.setPeriod(417);
                } else if (438 == periodId.intValue()) {
                    matchPeriod.setPeriod(418);
                } else if (43910 == periodId.intValue()) {
                    matchPeriod.setPeriod(41910);
                } else if (43810 == periodId.intValue()) {
                    matchPeriod.setPeriod(42010);
                } else if (43911 == periodId.intValue()) {
                    matchPeriod.setPeriod(41911);
                } else if (43811 == periodId.intValue()) {
                    matchPeriod.setPeriod(42011);
                } else if (43912 == periodId.intValue()) {
                    matchPeriod.setPeriod(41912);
                } else if (43812 == periodId.intValue()) {
                    matchPeriod.setPeriod(42012);
                } else if (43913 == periodId.intValue()) {
                    matchPeriod.setPeriod(41913);
                } else if (43813 == periodId.intValue()) {
                    matchPeriod.setPeriod(42013);
                } else if (43914 == periodId.intValue()) {
                    matchPeriod.setPeriod(41914);
                } else if (43814 == periodId.intValue()) {
                    matchPeriod.setPeriod(42014);
                } else if (43915 == periodId.intValue()) {
                    matchPeriod.setPeriod(41915);
                } else if (43815 == periodId.intValue()) {
                    matchPeriod.setPeriod(42015);
                } else if (43916 == periodId.intValue()) {
                    matchPeriod.setPeriod(41916);
                } else if (43816 == periodId.intValue()) {
                    matchPeriod.setPeriod(42016);
                } else if (43917 == periodId.intValue()) {
                    matchPeriod.setPeriod(41917);
                } else if (43817 == periodId.intValue()) {
                    matchPeriod.setPeriod(42017);
                } else if (43918 == periodId.intValue()) {
                    matchPeriod.setPeriod(41918);
                } else if (43818 == periodId.intValue()) {
                    matchPeriod.setPeriod(42018);
                } else if (43919 == periodId.intValue()) {
                    matchPeriod.setPeriod(41919);
                } else if (43819 == periodId.intValue()) {
                    matchPeriod.setPeriod(42019);
                }else if (43920 == periodId.intValue()) {
                    matchPeriod.setPeriod(41920);
                }else if (100 == periodId.intValue()) {
                    matchPeriod.setPeriod(42020);
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
            log.error("::{}::{},{},{}","RDSMSG_"+linkId, JsonFormatUtils.toJson(matchStatisticsInfoDetails),e.getMessage(), e);
        }
        return null;
    }
}




