package com.panda.sport.rcs.predict.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.statistics.MatchStatisticsInfoDetailMapper;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.vo.api.request.MatchScoreReqVo;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForMarketReqVo;
import com.panda.sport.rcs.pojo.vo.api.request.QueryBetForPlaceReqVo;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.api.response.BetForMarketResVo;
import com.panda.sport.rcs.pojo.vo.api.response.BetForPlaceResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
import com.panda.sport.rcs.service.IRcsPredictBetOddsService;
import com.panda.sport.rcs.service.IRcsPredictBetStatisService;
import com.panda.sport.rcs.service.IRcsPredictForecastPlayService;
import com.panda.sport.rcs.service.IRcsPredictForecastService;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Forecast 前端控制器
 * </p>
 *
 * @author Kir
 * @since 2021-02-19
 */
@Slf4j
@RestController
@RequestMapping("/forecast")
public class RcsPredictForecastController {

    @Autowired
    private IRcsPredictBetOddsService betOddsService;

    @Autowired
    private IRcsPredictBetStatisService betStatisService;

    @Autowired
    private IRcsPredictForecastPlayService forecastPlayService;

    @Autowired
    private IRcsPredictForecastService forecastService;


    @Autowired
    MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;

    @RequestMapping(value = "queryMatchScore")
    public HttpResponse<String> queryMatchScore(@RequestBody MatchScoreReqVo vo) {
        LambdaQueryWrapper<MatchStatisticsInfoDetail> lambdaQueryWrapper = new QueryWrapper<MatchStatisticsInfoDetail>().lambda();
        lambdaQueryWrapper.select(MatchStatisticsInfoDetail::getT1,MatchStatisticsInfoDetail::getT2);
        lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getStandardMatchId,  vo.getMatchId());
        //        10001 常规
        //        10002 角球
        //        10003 加时
        //        1004 加时点球
        if (vo.getCategorySetId() == null || vo.getCategorySetId() == 10001) {
            lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "match_score");
        } else if (vo.getCategorySetId() == 10002) {
            lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "corner_score");
        } else if (vo.getCategorySetId() == 10003) {
            lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "extra_time_score");
        } else if (vo.getCategorySetId() == 10004) { //暂时用不到
//            lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "penalty_score");
        } else if (vo.getCategorySetId() == 10005) { //暂时用不到
            return HttpResponse.success(redAndYellow(vo));
        }else {//默认
            lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "match_score");
        }

        lambdaQueryWrapper.last(" limit 1 ");

        MatchStatisticsInfoDetail result = matchStatisticsInfoDetailMapper.selectOne(lambdaQueryWrapper);
        if (result == null) {
            log.info("查询赛事比分未查到数据" + vo.getMatchId());
            return HttpResponse.success("0:0");
        }
        Integer t1 = result.getT1();
        if (t1 == null) {
            t1 = 0;
        }
        Integer t2 = result.getT2();
        if (t2 == null) {
            t2 = 0;
        }
        return HttpResponse.success(t1 + ":" + t2);
    }

    private String redAndYellow(MatchScoreReqVo vo) {
        LambdaQueryWrapper<MatchStatisticsInfoDetail> lambdaQueryWrapper = new QueryWrapper<MatchStatisticsInfoDetail>().lambda();
        lambdaQueryWrapper.select(MatchStatisticsInfoDetail::getT1, MatchStatisticsInfoDetail::getT2);
        lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getStandardMatchId, vo.getMatchId());
        lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getFirstNum, 0);
        lambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "red_card_score");
        MatchStatisticsInfoDetail resultHome = matchStatisticsInfoDetailMapper.selectOne(lambdaQueryWrapper);
        log.info("罚牌比分red:" + JSONObject.toJSONString(resultHome));
        Integer t1 = 0;
        Integer t2 = 0;
        if (resultHome != null && resultHome.getT1() != null) {
            t1 = resultHome.getT1();
        }
        t1 = t1 * 2;
        if (resultHome != null && resultHome.getT2() != null) {
            t2 = resultHome.getT2();
        }
        t2 = t2 * 2;

        LambdaQueryWrapper<MatchStatisticsInfoDetail> yellowLambdaQueryWrapper = new QueryWrapper<MatchStatisticsInfoDetail>().lambda();
        yellowLambdaQueryWrapper.select(MatchStatisticsInfoDetail::getT1, MatchStatisticsInfoDetail::getT2);
        yellowLambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getStandardMatchId, vo.getMatchId());
        yellowLambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getFirstNum, 0);
        yellowLambdaQueryWrapper.eq(MatchStatisticsInfoDetail::getCode, "yellow_card_score");
        MatchStatisticsInfoDetail resultAway = matchStatisticsInfoDetailMapper.selectOne(yellowLambdaQueryWrapper);
        log.info("罚牌比分yellow_card_score:" + JSONObject.toJSONString(resultAway));
        if (resultAway != null && resultAway.getT1() != null) {
            t1 += resultAway.getT1();
        }
        if (resultAway != null && resultAway.getT2() != null) {
            t2 += resultAway.getT2();
        }
        log.info("罚牌比分 " + t1 + ":" + t2);
        return t1 + ":" + t2;
    }

    /**
     * 查询 玩法级别 的Forecast
     * @param vo
     * @return
     */
    @RequestMapping(value = "queryForecastPlay", method = RequestMethod.POST)
    public HttpResponse queryForecastPlay(@RequestBody QueryForecastPlayReqVo vo) {
        if(ObjectUtils.isEmpty(vo.getMatchId())){
            return HttpResponse.fail("赛事ID不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getPlayId())){
            return HttpResponse.fail("玩法ID不能为空");
        }
//        LambdaQueryWrapper<RcsPredictForecastPlay> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(!ObjectUtils.isEmpty(vo.getMatchType()), RcsPredictForecastPlay::getMatchType, vo.getMatchType());
//        queryWrapper.eq(RcsPredictForecastPlay::getDataType, 1);
//        queryWrapper.eq(RcsPredictForecastPlay::getMatchId, vo.getMatchId());
//        queryWrapper.eq(RcsPredictForecastPlay::getPlayId, vo.getPlayId());
        List<RcsPredictForecastPlay> list;
        try {
            vo.setDataType(1);
            list = forecastPlayService.selectList(vo);
        }catch (Exception e){
            log.info("查询异常{}", e);
            return HttpResponse.error(500, "查询异常");
        }
        return HttpResponse.success(list);
    }

    /**
     * 查询 坑位级别 的货量信息 与 forecast
     * @param vo
     * @return
     */
    @RequestMapping(value = "queryBetForPlace", method = RequestMethod.POST)
    public HttpResponse queryBetForPlace(@RequestBody QueryBetForPlaceReqVo vo) {
        if(ObjectUtils.isEmpty(vo.getSportId())){
            return HttpResponse.fail("赛种ID不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getMatchId())){
            return HttpResponse.fail("赛事ID不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getPlayId())){
            return HttpResponse.fail("玩法ID不能为空");
        }
        if (vo.getMatchType() == 0) {
            vo.setMatchType(null);
        }
        List<BetForPlaceResVo> list;
        List<BetForPlaceResVo> listAll = new ArrayList<>();
        try {
            if(ObjectUtils.isEmpty(vo.getMatchType())){
                //所有阶段结果集加起来
                list = betOddsService.selectBetForPlace(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), 1, getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]);
                list.addAll(betOddsService.selectBetForPlace(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), 2, getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]));
            }else{
                //返回指定阶段的
                list = betOddsService.selectBetForPlace(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), vo.getMatchType(), getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]);
            }

            LambdaQueryWrapper<RcsPredictForecastPlay> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(!ObjectUtils.isEmpty(vo.getMatchType()), RcsPredictForecastPlay::getMatchType, vo.getMatchType());
            queryWrapper.eq(RcsPredictForecastPlay::getDataType, 2);
            queryWrapper.eq(RcsPredictForecastPlay::getMatchId, vo.getMatchId());
            queryWrapper.eq(RcsPredictForecastPlay::getPlayId, vo.getPlayId());
            queryWrapper.orderByAsc(RcsPredictForecastPlay::getScore);

            QueryForecastPlayReqVo reqVo = new QueryForecastPlayReqVo();
            if(!ObjectUtils.isEmpty(vo.getMatchType())){
                reqVo.setMatchType(vo.getMatchType());
            }
            reqVo.setDataType(2);
            reqVo.setMatchId(vo.getMatchId());
            reqVo.setPlayId(Long.valueOf(vo.getPlayId()));
            reqVo.setScore(1);
            List<RcsPredictForecastPlay> forecastPlayList = forecastPlayService.selectList(reqVo);

            if(!ObjectUtils.isEmpty(forecastPlayList)){
                //如果是早盘，那就没有比分
//                if(vo.getMatchType().equals(1)){
//                    forecastPlayList = this.getPlaceForecast(forecastPlayList, vo.getPlayId(), "0:0", vo.getMatchType());
//                }else{
//                    forecastPlayList = this.getPlaceForecast(forecastPlayList, vo.getPlayId(), vo.getScore(), vo.getMatchType());
//                }
                //判断一下是否包含新的货量数据
                Boolean hasUniqueHashValue = false;
                for(RcsPredictForecastPlay m : forecastPlayList){
                    if(m.getHashUnique() != null){
                        log.info("货量表rcs_predict_bet_odds包含新索引数据");
                        hasUniqueHashValue =true;
                        break;
                    }
                }
                Map<String, List<RcsPredictForecastPlay>> map;
                if(hasUniqueHashValue){
                    map = forecastPlayList.stream().filter(e->e.getHashUnique()!=null).collect(Collectors.groupingBy(bean -> bean.getPlaceNum() + "_" + bean.getMatchType()));
                }else{
                    map = forecastPlayList.stream().collect(Collectors.groupingBy(bean -> bean.getPlaceNum() + "_" + bean.getMatchType()));
                }
                //Map<String, List<RcsPredictForecastPlay>> map = forecastPlayList.stream().collect(Collectors.groupingBy(bean -> bean.getPlaceNum() + "_" + bean.getMatchType()));
                for (BetForPlaceResVo betForPlaceResVo : list) {
                    if(map !=null && !ObjectUtils.isEmpty(map.get(betForPlaceResVo.getDataTypeValue()  + "_" + betForPlaceResVo.getMatchType()))){
                        betForPlaceResVo.setForecastMap(map.get(betForPlaceResVo.getDataTypeValue() + "_" + betForPlaceResVo.getMatchType()).stream().collect(Collectors.toMap(e -> e.getScore().toString(), e -> e.getProfitValue())));
                    }
                }
            }

            //早盘滚球数据汇总
            Map<Integer, List<BetForPlaceResVo>> groupMap = list.stream().collect(Collectors.groupingBy(bean -> bean.getDataTypeValue()));
            for (Map.Entry<Integer, List<BetForPlaceResVo>> entry: groupMap.entrySet()) {
                Integer key = entry.getKey();
                List<BetForPlaceResVo> value = entry.getValue();
                if(value.size()==1){
                    listAll.addAll(value);
                }else if(value.size()==2){
                    BetForPlaceResVo one = value.get(0);
                    BetForPlaceResVo two = value.get(1);
                    BetForPlaceResVo merge = new BetForPlaceResVo();
                    merge.setHomeBetAmount(one.getHomeBetAmount().add(two.getHomeBetAmount()));
                    merge.setHomeBetAmountPay(one.getHomeBetAmountPay().add(two.getHomeBetAmountPay()));
                    merge.setHomeBetAmountComplex(one.getHomeBetAmountComplex().add(two.getHomeBetAmountComplex()));
                    merge.setAwayBetAmount(one.getAwayBetAmount().add(two.getAwayBetAmount()));
                    merge.setAwayBetAmountPay(one.getAwayBetAmountPay().add(two.getAwayBetAmountPay()));
                    merge.setAwayBetAmountComplex(one.getAwayBetAmountComplex().add(two.getAwayBetAmountComplex()));
                    merge.setBetAmountEquilibriumValue(one.getBetAmountEquilibriumValue().add(two.getBetAmountEquilibriumValue()));
                    merge.setBetAmountPayEquilibriumValue(one.getBetAmountPayEquilibriumValue().add(two.getBetAmountPayEquilibriumValue()));
                    merge.setBetAmountComplexEquilibriumValue(one.getBetAmountComplexEquilibriumValue().add(two.getBetAmountComplexEquilibriumValue()));
                    Map<String, BigDecimal> oneForecastMap = one.getForecastMap();
                    Map<String, BigDecimal> twoForecastMap = two.getForecastMap();
                    Map<String, BigDecimal> mergeForecastMap = new HashMap<>();
                    for (Map.Entry<String, BigDecimal> forecastEntry: oneForecastMap.entrySet()) {
                        String forecastkey = forecastEntry.getKey();
                        mergeForecastMap.put(forecastkey, oneForecastMap.get(forecastkey).add(twoForecastMap.get(forecastkey)));
                    }
                    merge.setForecastMap(mergeForecastMap);
                    listAll.add(merge);
                }
            }
//            listAll = list;

            if(!ObjectUtils.isEmpty(listAll)) {
                for (BetForPlaceResVo betForPlaceResVo : listAll) {
                    Map<String, BigDecimal> result2 = new LinkedHashMap<>();
                    betForPlaceResVo.getForecastMap().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));
                    betForPlaceResVo.setForecastMap(result2);
                }
                list = list.stream().filter(e -> e.getHomeBetAmount().compareTo(BigDecimal.ZERO) != 0 || e.getAwayBetAmount().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
            }

        }catch (Exception e){
            log.info("查询异常{}", e);
            return HttpResponse.error(500, "查询异常");
        }
        return HttpResponse.success(listAll);
    }

    /**
     * 查询 盘口级别 的货量信息 与 forecast
     * @param vo
     * @return
     */
    @RequestMapping(value = "queryBetForMarket", method = RequestMethod.POST)
    public HttpResponse queryBetForMarket(@RequestBody QueryBetForMarketReqVo vo) {
        if(ObjectUtils.isEmpty(vo.getSportId())){
            return HttpResponse.fail("赛种ID不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getMatchId())){
            return HttpResponse.fail("赛事ID不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getPlayId())){
            return HttpResponse.fail("玩法ID不能为空");
        }
        if (vo.getMatchType() == 0) {
            vo.setMatchType(null);
        }
        List<BetForMarketResVo> list;
        try {
            if (vo.getPendingType() == 0) {
                if (ObjectUtils.isEmpty(vo.getMatchType())) {
                    //所有阶段结果集加起来
                    list = betStatisService.selectBetForMarket(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), 1, getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]);
                    list.addAll(betStatisService.selectBetForMarket(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), 2, getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]));
                } else {
                    //返回指定阶段的
                    list = betStatisService.selectBetForMarket(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), vo.getMatchType(), getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]);
                }
            } else {
                if (ObjectUtils.isEmpty(vo.getMatchType())) {
                    //所有阶段结果集加起来
                    list = betStatisService.selectPendingBetForMarket(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), 1, getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]);
                    list.addAll(betStatisService.selectPendingBetForMarket(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), 2, getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]));
                } else {
                    //返回指定阶段的
                    list = betStatisService.selectPendingBetForMarket(vo.getMatchId(), vo.getPlayId(), vo.getSportId(), vo.getMatchType(), getFootPlayForecastService(vo.getPlayId())[0], getFootPlayForecastService(vo.getPlayId())[1]);
                }
            }

            List<RcsPredictForecast> forecastList;
            if (vo.getPendingType() == 0) {
                forecastList = forecastService.selectList(vo);
            } else {
                forecastList = forecastService.pendingSelectList(vo);
            }
            if (!ObjectUtils.isEmpty(forecastList)) {
                if (vo.getPendingType() == 0) {
                    Map<String, RcsPredictForecast> forecastMap = new HashMap<>();
                    for (RcsPredictForecast rcsPredictForecast : forecastList) {
                        String mapKey = rcsPredictForecast.getBetScore() + "," + rcsPredictForecast.getMarketId() + "," + rcsPredictForecast.getForecastScore();
                        if (forecastMap.get(mapKey) != null) {
                            rcsPredictForecast.setProfitAmount(forecastMap.get(mapKey).getProfitAmount().add(rcsPredictForecast.getProfitAmount()));
                        }
                        forecastMap.put(mapKey, rcsPredictForecast);
                    }
                    forecastList = new ArrayList<>(forecastMap.values());
                    Map<String, List<RcsPredictForecast>> map = forecastList.stream().collect(Collectors.groupingBy(bean -> bean.getBetScore() + "," + bean.getMarketId()));
                    for (BetForMarketResVo betForMarketResVo : list) {
                        if (!ObjectUtils.isEmpty(map.get(betForMarketResVo.getBetScore() + "," + betForMarketResVo.getMarketId()))) {
                            betForMarketResVo.setForecastMap(map.get(betForMarketResVo.getBetScore() + "," + betForMarketResVo.getMarketId()).stream().collect(Collectors.toMap(t -> t.getForecastScore().toString(), RcsPredictForecast::getProfitAmount)));
                        }
                    }
                } else {
                    buildBetForMarketResVoForecastMap(list, forecastList);
                }
            }
            if(!ObjectUtils.isEmpty(list)){
                for (BetForMarketResVo betForMarketResVo : list) {
                    Map<String, BigDecimal> result2 = new LinkedHashMap<>();
                    betForMarketResVo.getForecastMap().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEachOrdered(x -> result2.put(x.getKey(), x.getValue()));
                    betForMarketResVo.setForecastMap(result2);
                }
                list = list.stream().filter(e -> e.getHomeBetAmount().compareTo(BigDecimal.ZERO) != 0 || e.getAwayBetAmount().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询异常{}", e);
            return HttpResponse.error(500, "查询异常");
        }
        return HttpResponse.success(list);
    }

    private String generateMapKey(String betScore, String marketValueComplete) {
        return String.format("%s,%s", betScore, marketValueComplete);
    }

    private void buildBetForMarketResVoForecastMap(List<BetForMarketResVo> list, List<RcsPredictForecast> forecastList) {
        Map<String, List<RcsPredictForecast>> map = forecastList.stream().collect(Collectors.groupingBy(bean -> bean.getBetScore() + "," + bean.getMarketValueComplete()));
        for (BetForMarketResVo betForMarketResVo : list) {
            List<RcsPredictForecast> rcsPredictForecastList = map.get(betForMarketResVo.getBetScore() + "," + betForMarketResVo.getMarketValueComplete());
            rcsPredictForecastList.sort(Comparator.comparing(RcsPredictForecast::getCreateTime).reversed());
            betForMarketResVo.setForecastMap(rcsPredictForecastList.stream().collect(Collectors.toMap(t -> t.getForecastScore().toString(), RcsPredictForecast::getProfitAmount, (t1, t2) -> t1)));
        }
    }


    @Autowired
    private RedisClient redisClient;

    @GetMapping("/delete/redisKey/{macthId}")
    public HttpResponse deleteRedis(@PathVariable Integer macthId) {
        redisClient.batchDel("rcs:profit:pending:match:" + macthId);
        redisClient.batchDel("rcs:risk:predict:pending:forecast.match_id." + macthId);
        redisClient.batchDel("rcs:risk:predict:pending:betSatis.match_id." + macthId);
        return HttpResponse.success();
    }
    /**
     * 足球  根据玩法 获取处理对应计算forecast的service
     */
    public String[] getFootPlayForecastService(Integer playId) {
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;
        if (Arrays.asList(letPoint).contains(playId)) {
            return new String[]{"1", "2"};
        }

        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return new String[]{"Over", "Under"};
        }

        log.info("玩法ID：{} 不包含forecast。", playId);
        return null;
    }

    /**
     * 根据比分和玩法过滤剩下5条 坑位级别的forecast数据
     */
    public  List<RcsPredictForecastPlay> getPlaceForecast(List<RcsPredictForecastPlay> list, Integer playId, String score, Integer matchType) {
        String[] scores = score.split(":");
        List<RcsPredictForecastPlay> resList = new ArrayList<>();
        Map<Integer, List<RcsPredictForecastPlay>> map = list.stream().collect(Collectors.groupingBy(bean -> bean.getPlaceNum() + matchType));

        //让球
        List<Integer> letPoints = Arrays.asList(ForecastPlayIds.letPoint);
        //大小
        List<Integer> bigSmalls = Arrays.asList(ForecastPlayIds.bigSmall);

        map.forEach((k,v) -> {
            Integer num;
            List<RcsPredictForecastPlay> tempResult = new ArrayList<>();
            if (letPoints.contains(playId)) {
                //让球是主队（前面的）减客队（后面的）的值等于6,就取4,5,6,7,8
                num = Integer.parseInt(scores[0]) - Integer.parseInt(scores[1]);
                if(num == -12){
                    tempResult = v.stream().filter(e ->  e.getScore() >= -12 && e.getScore() <= -8).collect(Collectors.toList());
                }else if(num == -11){
                    tempResult = v.stream().filter(e ->  e.getScore() >= -11 && e.getScore() <= -7).collect(Collectors.toList());
                }else if(num == 12){
                    tempResult = v.stream().filter(e ->  e.getScore() >= 8 && e.getScore() <= 12).collect(Collectors.toList());
                }else if(num == 11){
                    tempResult = v.stream().filter(e ->  e.getScore() >= 7 && e.getScore() <= 11).collect(Collectors.toList());
                }else{
                    int start = num-2;
                    int end  = num+2;
                    tempResult = v.subList(start, end + 1);
                }
            }else if (bigSmalls.contains(playId)) {
                //大小是两个比分加起来如果等于1就取-1,0,1,2,3
                num = Integer.parseInt(scores[0]) + Integer.parseInt(scores[1]);
                if(num == 0){
                    tempResult = v.stream().filter(e ->  e.getScore() >= 0 && e.getScore() <= 4).collect(Collectors.toList());
                }else if(num == 1){
                    tempResult = v.stream().filter(e ->  e.getScore() >= 1 && e.getScore() <= 5).collect(Collectors.toList());
                }else if(num == 23){
                    tempResult = v.stream().filter(e ->  e.getScore() >= 19 && e.getScore() <= 23).collect(Collectors.toList());
                }else if(num == 24){
                    tempResult = v.stream().filter(e ->  e.getScore() >= 20 && e.getScore() <= 24).collect(Collectors.toList());
                }else{
                    int start = num-2;
                    int end  = num+2;
                    tempResult = v.subList(start, end + 1);
                }
            }
            resList.addAll(tempResult);
        });
        return resList;
    }

    /**
     * 根据比分和玩法过滤剩下5条 盘口级别的forecast数据
     */
    public List<RcsPredictForecast> getMarKetForecast(List<RcsPredictForecast> list, Integer playId, String score) {
        String[] scores = score.split(":");
        List<RcsPredictForecast> resList = new ArrayList<>();
        //基准分+盘口值+投注项确认唯一一条数据
        Map<String, List<RcsPredictForecast>> map = list.stream().collect(Collectors.groupingBy(bean -> bean.getBetScore() + "," + bean.getMarketId()+","+bean.getOddsItem()));

        //让球
        List<Integer> letPoints = Lists.newArrayList(ForecastPlayIds.letPoint);
        //大小
        List<Integer> bigSmalls = Lists.newArrayList(ForecastPlayIds.bigSmall);

        map.forEach((k,v) -> {
            Integer num;
            List<RcsPredictForecast> tempResult = new ArrayList<>();
            if (letPoints.contains(playId)) {
                //让球是主队（前面的）减客队（后面的）的值等于6,就取4,5,6,7,8
                num = Integer.parseInt(scores[0]) - Integer.parseInt(scores[1]);
                if(num == -12){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= -12 && e.getForecastScore() <= -8).collect(Collectors.toList());
                }else if(num == -11){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= -11 && e.getForecastScore() <= -7).collect(Collectors.toList());
                }else if(num == 12){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= 8 && e.getForecastScore() <= 12).collect(Collectors.toList());
                }else if(num == 11){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= 7 && e.getForecastScore() <= 11).collect(Collectors.toList());
                }else{
                    int start = num-2;
                    int end  = num+2;
                    tempResult = v.subList(start, end + 1);
                }
            }else if (bigSmalls.contains(playId)) {
                //大小是两个比分加起来如果等于1就取-1,0,1,2,3
                num = Integer.parseInt(scores[0]) + Integer.parseInt(scores[1]);
                if(num == 0){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= 0 && e.getForecastScore() <= 4).collect(Collectors.toList());
                }else if(num == 1){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= 1 && e.getForecastScore() <= 5).collect(Collectors.toList());
                }else if(num == 23){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= 19 && e.getForecastScore() <= 23).collect(Collectors.toList());
                }else if(num == 24){
                    tempResult = v.stream().filter(e ->  e.getForecastScore() >= 20 && e.getForecastScore() <= 24).collect(Collectors.toList());
                }else{
                    int start = num-2;
                    int end  = num+2;
                    tempResult = v.subList(start, end + 1);
                }
            }
            resList.addAll(tempResult);
        });
        return resList;
    }

}
