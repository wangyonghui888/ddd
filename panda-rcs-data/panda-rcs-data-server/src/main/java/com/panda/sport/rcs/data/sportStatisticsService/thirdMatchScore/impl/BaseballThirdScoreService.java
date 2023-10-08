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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class BaseballThirdScoreService extends AbstractThirdScoreService {

    @Override
    protected void initial() {
        sportId = 3L;
        unFilterScoreData.add("firstBase");
        unFilterScoreData.add("secondBase");
        unFilterScoreData.add("thirdBase");
        unFilterScoreData.add("setScore");
        unFilterScoreData.add("matchScore");
        StatisticsServiceContext.addThirdScoreService(this);
    }

    @Override
    public Map standardScore(Request<StandardScoreDto> request) {
        Long standardMatchId = request.getData().getStandardMatchId();
        String dataSourceCode = request.getData().getDataSourceCode();
        List<MatchStatisticsInfoDetailSource> oldlist = buildScore(request, dataSourceCode);
        List<MatchStatisticsInfoDetailSource> list =filterBaseballRepeatData(oldlist, request.getDataSourceTime(), standardMatchId, dataSourceCode,request);
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
