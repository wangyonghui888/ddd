package com.panda.sport.rcs.data.calc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.data.calc.service.MarketValueCalculatorService;
import com.panda.sport.rcs.data.mapper.RcsOddsConvertMappingMyMapper;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketOddsMessageDTO;
import com.panda.sport.rcs.data.service.RcsOddsConvertMappingService;
import com.panda.sport.rcs.data.service.RcsStandardPlaceRefService;
import com.panda.sport.rcs.data.service.RcsTradeConfigService;
import com.panda.sport.rcs.data.service.StandardSportMarketOddsService;
import com.panda.sport.rcs.data.service.StandardSportMarketService;
import com.panda.sport.rcs.data.utils.BigDecimalUtils;
import com.panda.sport.rcs.data.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.data.utils.MarketUtils;
import com.panda.sport.rcs.data.utils.OddsConvertUtils;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.enums.TradeModeEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.RcsOddsConvertMappingMy;
import com.panda.sport.rcs.pojo.config.BuildMarketConfigDto;
import com.panda.sport.rcs.pojo.config.BuildMarketPlayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : A+模式构建盘口
 * @Author : Paca
 * @Date : 2021-01-13 14:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class MarketValueCalcServiceImpl implements MarketValueCalculatorService {

    public static final String RCS_MARKET_STATUS_CONFIG = "rcs:marketStatusConfig:%s:%s";
    private static final String HOME_ODDS = "homeOdds";
    private static final String AWAY_ODDS = "awayOdds";
    private static final String HOME_ORIGINAL_ODDS = "homeOriginalOdds";
    private static final String AWAY_ORIGINAL_ODDS = "awayOriginalOdds";

    private Map<Double, String> oddsMap;

    private Map<String, String> europeOddsMap;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsOddsConvertMappingMyMapper rcsOddsConvertMappingMyMapper;

    @Autowired
    private RcsStandardPlaceRefService rcsStandardPlaceRefService;

    @Autowired
    private RDSProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsOddsConvertMappingService rcsOddsConvertMappingService;

    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketOddsApi tradeMarketOddsApi;

    @Override
    public void buildMarketList(StandardMarketMessageDTO market, BuildMarketConfigDto config) {
        Long matchId = market.getStandardMatchInfoId();
        Long playId = market.getMarketCategoryId();
        Map<Integer, BigDecimal> placeWaterDiffMap = config.getPlaceWaterDiffMap();
        Map<Integer, BigDecimal> placeSpreadMap = config.getPlaceSpreadMap();
        // 数据源主盘口值
        BigDecimal mainMv = new BigDecimal(market.getAddition1()).stripTrailingZeros();
        Integer marketCount = config.getMarketCount();
        // 相邻盘口差值
        BigDecimal marketNearDiff = config.getMarketNearDiff();
        // 主盘口值 + 盘口差
        BigDecimal newMainMv = getNewMainMv(playId, mainMv, config.getMarketHeadGap().stripTrailingZeros(), config.getMarketAdjustRange().stripTrailingZeros());
        List<BigDecimal> marketValueList = MarketUtils.generateMarketValues(playId, marketCount, newMainMv, marketNearDiff);
        List<Map<String, BigDecimal>> malayOddsList = MarketUtils.generateMalayOddsList(playId, marketCount, placeSpreadMap, config.getMarketNearOddsDiff(),marketValueList);

        String redisCacheKey = String.format("rcs:dataserver:odds:risk:%s:%s", matchId, playId);

        boolean isFirst = false;
        // A+切换标志，不为空表示切换到A+模式，为空表示A+模式下数据源有变化
        String redisCacheVal = redisClient.get(redisCacheKey);
        if (StringUtils.isBlank(redisCacheVal) && !config.isClearFlag()) {
            // 不需要计算赔率，从DB读取
            replaceOdds(matchId, playId, malayOddsList);
        } else {
            redisClient.delete(redisCacheKey);
            isFirst = true;
            for (int i = 0; i < malayOddsList.size(); i++) {
                int placeNum = i + 1;
                Map<String, BigDecimal> malayOddsMap = malayOddsList.get(i);
                // 水差 = 位置水差，上盘赔率减水差，下盘赔率加水差
                BigDecimal waterDiff = placeWaterDiffMap.getOrDefault(placeNum, BigDecimal.ZERO);
                BigDecimal homeOdds = malayOddsMap.get(RcsConstant.HOME_POSITION).subtract(waterDiff);
                BigDecimal awayOdds = malayOddsMap.get(RcsConstant.AWAY_POSITION).add(waterDiff);
                malayOddsMap.put(RcsConstant.HOME_POSITION, MarketUtils.checkMalayOdds(homeOdds));
                malayOddsMap.put(RcsConstant.AWAY_POSITION, MarketUtils.checkMalayOdds(awayOdds));
                malayOddsMap.put("home_market_diff_value", waterDiff.negate());
                malayOddsMap.put("away_market_diff_value", waterDiff);
                log.info("计算水差后赔率：waterDiff={},malayOddsMap={}", waterDiff, malayOddsMap);
            }
            log.info("赔率计算水差后：{}", malayOddsList);
        }

        Map<Integer, Integer> placeStatusMap = getMarketPlaceStatusFromRedis(matchId, playId);
        List<StandardMarketDTO> marketDTOList = new ArrayList<>(marketCount);
        for (int i = 0; i < marketCount; i++) {
            int placeNum = i + 1;
            Integer placeStatus = placeStatusMap.getOrDefault(placeNum, TradeStatusEnum.OPEN.getStatus());
            BigDecimal marketValue = marketValueList.get(i).stripTrailingZeros();
            Map<String, BigDecimal> malayOddsMap = malayOddsList.get(i);

            StandardMarketDTO newMarket = MarketUtils.toStandardMarketDTO(market);
            newMarket.setOddsValue(marketValue.toPlainString());
            newMarket.setAddition1(marketValue.toPlainString());
            if (RcsConstant.BENCHMARK_SCORE.contains(playId)) {
                // 基准分
                newMarket.setAddition2(marketValue.toPlainString());
            }
            if (Basketball.Main.getHandicapPlayIds().contains(playId)) {
                newMarket.setAddition5(market.getAddition1());
            }
            newMarket.setPlaceNum(placeNum);
            newMarket.setMarketHeadGap(config.getMarketHeadGap().doubleValue());
            Integer status = market.getStatus();
            if (TradeStatusEnum.isOpen(market.getThirdMarketSourceStatus())) {
                status = placeStatusMap.getOrDefault(placeNum, status);
            }
            newMarket.setStatus(status);
            newMarket.setPlaceNumStatus(placeStatus);
            newMarket.setThirdMarketSourceStatus(market.getThirdMarketSourceStatus());
            newMarket.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);

            List<StandardMarketOddsMessageDTO> marketOddsList = market.getMarketOddsList();
            if (CollectionUtils.isNotEmpty(marketOddsList)) {
                List<StandardMarketOddsDTO> newMarketOddsList = new ArrayList<>(marketOddsList.size());
                for (StandardMarketOddsMessageDTO marketOdds : marketOddsList) {
                    String oddsType = marketOdds.getOddsType();
                    StandardMarketOddsDTO marketOddsDTO = MarketUtils.toStandardMarketOddsDTO(marketOdds);
                    marketOddsDTO.setMarketDiffValue(null);
                    marketOddsDTO.setNameExpressionValue(MarketUtils.getNameExpressionValue(oddsType, marketValue));
                    if (OddsTypeEnum.isHomeOddsType(oddsType)) {
                        marketOddsDTO.setOddsValue(getConvertedOdds(malayOddsMap.get(RcsConstant.HOME_POSITION)));
                        BigDecimal marketDiffValue = malayOddsMap.get("home_market_diff_value");
                        if (marketDiffValue != null) {
                            marketOddsDTO.setMarketDiffValue(marketDiffValue.doubleValue());
                        }
                    } else if (OddsTypeEnum.isAwayOddsType(oddsType)) {
                        marketOddsDTO.setOddsValue(getConvertedOdds(malayOddsMap.get(RcsConstant.AWAY_POSITION)));
                        BigDecimal marketDiffValue = malayOddsMap.get("away_market_diff_value");
                        if (marketDiffValue != null) {
                            marketOddsDTO.setMarketDiffValue(marketDiffValue.doubleValue());
                        }
                    }
                    marketOddsDTO.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
                    newMarketOddsList.add(marketOddsDTO);
                }
                newMarket.setMarketOddsList(newMarketOddsList);
            }
            marketDTOList.add(newMarket);
        }
        // 调用融合RPC接口
        StandardMatchMarketDTO standardMatchMarketDTO = new StandardMatchMarketDTO();
        standardMatchMarketDTO.setStandardMatchInfoId(matchId);
        standardMatchMarketDTO.setMarketList(marketDTOList);
        putTradeMarketOdds(standardMatchMarketDTO, isFirst, market.getThirdMarketSourceStatus());

        if (Basketball.Main.getHandicapPlayIds().contains(playId) && MarketUtils.isSealCheck(marketValueList)) {
            // 让球玩法只要出现0或±0.5的球头，独赢玩法自动封盘，独赢玩法盘口级封盘
            basketballWinSeal(matchId, Basketball.Main.getWinAloneByHandicap(playId));
        }
    }

    private void replaceOdds(Long matchId, Long playId, List<Map<String, BigDecimal>> malayOddsList) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("matchId", matchId);
        params.put("playId", playId);
        List<Map<String, Object>> dbOddsList = rcsStandardPlaceRefService.queryOddsByPlaceNumAndPlayId(params);
        if (CollectionUtils.isNotEmpty(dbOddsList)) {
            Map<String, List<Map<String, Object>>> dbOddsMap = dbOddsList.stream().collect(Collectors.groupingBy(item -> String.valueOf(item.get("place_num"))));
            for (int j = 1; j <= Math.min(dbOddsMap.size(), malayOddsList.size()); j++) {
                Map<String, BigDecimal> malayOddsMap = malayOddsList.get(j - 1);
                List<Map<String, Object>> oddsList = dbOddsMap.get(String.valueOf(j));
                oddsList.forEach(map -> {
                    String oddsType = String.valueOf(map.get("odds_type"));
                    String oddsValue = String.valueOf(map.get("odds_value"));
                    BigDecimal marketDiffValue = toBigDecimal(map.get("market_diff_value"), BigDecimal.ZERO);
                    if (OddsTypeEnum.isHomeOddsType(oddsType)) {
                        malayOddsMap.put(RcsConstant.HOME_POSITION, getConvertedOddsByEurope(oddsValue));
                        malayOddsMap.put("home_market_diff_value", marketDiffValue);
                    } else if (OddsTypeEnum.isAwayOddsType(oddsType)) {
                        malayOddsMap.put(RcsConstant.AWAY_POSITION, getConvertedOddsByEurope(oddsValue));
                        malayOddsMap.put("away_market_diff_value", marketDiffValue);
                    }
                });
            }
        }
        log.info("赔率替换后：{}", malayOddsList);
    }

    private BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            String s = String.valueOf(value);
            if (StringUtils.isNotBlank(s)) {
                return new BigDecimal(s).stripTrailingZeros();
            }
        } catch (Exception e) {
        }
        return defaultValue;
    }

    @Override
    public void basketballWinSeal(Long matchId, Long winPlayId) {
        // 独赢玩法盘口级封盘
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeLevel", TradeLevelEnum.MARKET.getLevel());
        jsonObject.put("sportId", SportIdEnum.BASKETBALL.getId());
        jsonObject.put("matchId", matchId);
        jsonObject.put("playId", winPlayId);
        jsonObject.put("placeNum", NumberUtils.INTEGER_ONE);
        jsonObject.put("status", TradeStatusEnum.SEAL.getStatus());
        jsonObject.put("linkedType", 4);
        jsonObject.put("remark", "让球出现0或±0.5的球头，独赢封盘");
        Request<JSONObject> request = new Request<>();
        request.setData(jsonObject);
        request.setLinkId(MarketUtils.getLinkId("A+"));
        request.setDataSourceTime(System.currentTimeMillis());
        producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", matchId + "_A+", request.getLinkId(), request);
    }


    /**
     * 调用融合RPC接口，操盘标准盘口及赔率数据处理
     *
     * @param standardMatchMarketDTO
     * @param isFirst
     * @param thirdMarketSourceStatus
     */
    private Response putTradeMarketOdds(StandardMatchMarketDTO standardMatchMarketDTO, boolean isFirst, Integer thirdMarketSourceStatus) {
        return DataRealtimeApiUtils.handleApi(standardMatchMarketDTO, new DataRealtimeApiUtils.ApiCall() {
            @Override
            @Trace
            public <R> Response<R> callApi(Request request) {
                if (isFirst) {
                    String linkId = request.getLinkId();
                    request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_" + "super_one");
                    log.info("重置linkid：old：{}，new:{}", linkId, request.getLinkId());

                    String key = String.format("rcs:dataserver:model:change:super:a:%s", standardMatchMarketDTO.getStandardMatchInfoId());
                    String val = String.format("%s;%s", request.getLinkId(), thirdMarketSourceStatus);
                    redisClient.setExpiry(key, val, 60L);
                }

                return tradeMarketOddsApi.putTradeMarketOdds(request);
            }
        });
    }

    private BigDecimal getConvertedOddsByEurope(String oddsValue) {
        if (MapUtils.isEmpty(europeOddsMap)) {
            QueryWrapper<RcsOddsConvertMappingMy> queryWrapper = new QueryWrapper<>();
            List<RcsOddsConvertMappingMy> list = rcsOddsConvertMappingMyMapper.selectList(queryWrapper);
            europeOddsMap = list.stream().collect(Collectors.toMap(RcsOddsConvertMappingMy::getEurope, RcsOddsConvertMappingMy::getMalaysia));
        }
        BigDecimal oddsValueNew = new BigDecimal(oddsValue).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE));
        String fieldOddsValue = europeOddsMap.get(String.format("%.2f", oddsValueNew));
        if (StringUtils.isBlank(fieldOddsValue)) {
            fieldOddsValue = NumberUtils.INTEGER_ZERO.toString();
        }
        return new BigDecimal(fieldOddsValue);
    }

    private int getConvertedOdds(BigDecimal malayOdds) {
        malayOdds = MarketUtils.checkMalayOdds(malayOdds);
        if (MapUtils.isEmpty(oddsMap)) {
            QueryWrapper<RcsOddsConvertMappingMy> queryWrapper = new QueryWrapper<>();
            List<RcsOddsConvertMappingMy> list = rcsOddsConvertMappingMyMapper.selectList(queryWrapper);
            oddsMap = list.stream().collect(Collectors.toMap(e -> Double.parseDouble(e.getMalaysia()), RcsOddsConvertMappingMy::getEurope));
        }
        String fieldOddsValue = oddsMap.get(malayOdds.doubleValue());
        if (StringUtils.isBlank(fieldOddsValue)) {
            fieldOddsValue = NumberUtils.INTEGER_ZERO.toString();
        }
        return new BigDecimal(fieldOddsValue).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue();
    }

    private Map<Integer, Integer> getMarketPlaceStatusFromRedis(Long matchId, Long playId) {
        Map<Integer, Integer> resultMap = Maps.newHashMap();
        String key = String.format(RCS_MARKET_STATUS_CONFIG, matchId, playId);
        Map<String, String> map = redisClient.hGetAll(key, String.class);
        log.info("Redis位置状态：matchId={},playId={},map={}", matchId, playId, map);
        if (CollectionUtils.isEmpty(map)) {
            // 盘口位置，最多10个位置
            for (int placeNum = 1; placeNum <= RcsConstant.DEFAULT_MARKET_PLACE_AMOUNT; placeNum++) {
                resultMap.put(placeNum, TradeStatusEnum.OPEN.getStatus());
            }
            return resultMap;
        }
        map.forEach((k, v) -> resultMap.put(NumberUtils.toInt(k), NumberUtils.toInt(v, TradeStatusEnum.OPEN.getStatus())));
        return resultMap;
    }

    private BigDecimal getNewMainMv(Long playId, BigDecimal mainMv, BigDecimal marketHeadGap, BigDecimal marketAdjustRange) {
        if (marketHeadGap.compareTo(BigDecimal.ZERO) == 0) {
            // 盘口差为0
            return mainMv;
        }
        Integer changeTime = marketHeadGap.abs().divide(marketAdjustRange).intValue();
        // 主盘口值 + 盘口差
        BigDecimal newMainMv = mainMv.add(marketHeadGap).stripTrailingZeros();
    
        marketAdjustRange = marketHeadGap.compareTo(BigDecimal.ZERO) > 0 ? marketAdjustRange : marketAdjustRange.negate();
        
        if(Basketball.isHandicap(playId)) {
            BigDecimal stepMv = mainMv;
            for (Integer i = 0; i < changeTime; i++) {
                newMainMv = stepMv.add(marketAdjustRange).stripTrailingZeros();
                if (Basketball.Main.FULL_TIME.getHandicap().equals(playId)) {
//              39号玩法不能出现正负0.5和0球头
                    if (newMainMv.abs().compareTo(BigDecimal.ONE) < 0) {
                        while (true){
                            newMainMv = newMainMv.add(marketAdjustRange).stripTrailingZeros();
                            if (newMainMv.abs().compareTo(BigDecimal.ONE) >= 0) {
                                break;
                            }
                        }
                    }
                    
                } else {
//              39 全场让分，19 上半场让分，46 第1节让分，52 第2节让分，58 第3节让分，64 第4节让分，143 下半场让分  不能出现0球头
                    if (newMainMv.abs().compareTo(BigDecimal.ZERO) == 0) {
                        while (true){
                            newMainMv = newMainMv.add(marketAdjustRange).stripTrailingZeros();
                            if (newMainMv.abs().compareTo(BigDecimal.ZERO) != 0) {
                                break;
                            }
                        }
                    }
                }
                stepMv = newMainMv;
            }
        }
        log.info("::{}::,getNewMainMv 生成主盘口值 = {}",playId,newMainMv.toPlainString());
        return newMainMv;
    }

    private boolean isInteger(BigDecimal value) {
        try {
            // 1 2 3 4 5 6 ...
            value.intValueExact();
            return true;
        } catch (Exception e) {
            // 0.5 1.5 2.5 3.5 4.5 5.5 6.5 ...
            return false;
        }
    }

    @Override
    public List<StandardMarketDTO> buildMarketList(Long matchId, StandardMarketMessage market, BuildMarketPlayConfig config, boolean isFirst) {
        Long playId = market.getMarketCategoryId();
        List<StandardMarketOddsMessage> marketOddsList = market.getMarketOddsList();
        market.setMarketOddsList(null);
        Map<Integer, BigDecimal> placeWaterDiffMap = config.getPlaceWaterDiffMap();
        Map<Integer, BigDecimal> placeSpreadMap = config.getPlaceSpreadMap();
        // 数据源主盘口值
        BigDecimal mainMv = new BigDecimal(market.getAddition1()).stripTrailingZeros();
        Integer marketCount = config.getMarketCount();
        // 相邻盘口差值
        BigDecimal marketNearDiff = config.getMarketNearDiff();
        // 主盘口值 + 盘口差
        BigDecimal newMainMv = getNewMainMv(playId, mainMv, config.getMarketHeadGap().stripTrailingZeros(), config.getMarketAdjustRange().stripTrailingZeros());
        List<BigDecimal> marketValueList = MarketUtils.generateMarketValues(playId, marketCount, newMainMv, marketNearDiff);
        List<Map<String, BigDecimal>> malayOddsList = MarketUtils.generateMalayOddsList(playId, marketCount, placeSpreadMap, config.getMarketNearOddsDiff(),marketValueList);
        for (int i = 0; i < malayOddsList.size(); i++) {
            int placeNum = i + 1;
            Map<String, BigDecimal> malayOddsMap = malayOddsList.get(i);
            // 水差 = 位置水差，上盘赔率减水差，下盘赔率加水差
            BigDecimal waterDiff = placeWaterDiffMap.getOrDefault(placeNum, BigDecimal.ZERO);
            BigDecimal homeOdds = malayOddsMap.get(RcsConstant.HOME_POSITION).subtract(waterDiff);
            BigDecimal awayOdds = malayOddsMap.get(RcsConstant.AWAY_POSITION).add(waterDiff);
            malayOddsMap.put(RcsConstant.HOME_POSITION, MarketUtils.checkMalayOdds(homeOdds));
            malayOddsMap.put(RcsConstant.AWAY_POSITION, MarketUtils.checkMalayOdds(awayOdds));
            malayOddsMap.put("home_market_diff_value", waterDiff.negate());
            malayOddsMap.put("away_market_diff_value", waterDiff);
        }
        log.info("赔率计算水差后：{}", malayOddsList);
        Map<Integer, Integer> placeStatusMap = getMarketPlaceStatusFromRedis(matchId, playId);
        List<StandardMarketDTO> marketList = new ArrayList<>(marketCount);
        for (int i = 0; i < marketCount; i++) {
            int placeNum = i + 1;
            Integer placeStatus = placeStatusMap.getOrDefault(placeNum, TradeStatusEnum.OPEN.getStatus());
            BigDecimal marketValue = marketValueList.get(i).stripTrailingZeros();
            Map<String, BigDecimal> malayOddsMap = malayOddsList.get(i);
            StandardMarketDTO newMarket = JSON.parseObject(JSON.toJSONString(market), StandardMarketDTO.class);
            newMarket.setOddsValue(marketValue.toPlainString());
            newMarket.setAddition1(marketValue.toPlainString());
            if (RcsConstant.BENCHMARK_SCORE.contains(playId)) {
                // 基准分
                newMarket.setAddition2(marketValue.toPlainString());
            }
            if (Basketball.Main.getHandicapPlayIds().contains(playId)) {
                newMarket.setAddition5(market.getAddition1());
            }
            newMarket.setPlaceNum(placeNum);
            newMarket.setMarketHeadGap(config.getMarketHeadGap().doubleValue());
            Integer status = market.getStatus();
            if (TradeStatusEnum.isOpen(market.getThirdMarketSourceStatus())) {
                status = placeStatusMap.getOrDefault(placeNum, status);
            }
            newMarket.setStatus(status);
            newMarket.setPlaceNumStatus(placeStatus);
            newMarket.setThirdMarketSourceStatus(market.getThirdMarketSourceStatus());
            newMarket.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
            log.info("::{}::A+模式构建盘口(让分-大小),盘口Id:={},玩法Id:={},placeNum:={},盘口状态:={}","数据商盘口源状态:={}",
                    matchId, market.getId(), market.getMarketCategoryId(),placeNum, status,newMarket.getThirdMarketSourceStatus());

            if (CollectionUtils.isNotEmpty(marketOddsList)) {
                List<StandardMarketOddsDTO> newMarketOddsList = new ArrayList<>(marketOddsList.size());
                for (StandardMarketOddsMessage marketOdds : marketOddsList) {
                    String oddsType = marketOdds.getOddsType();
                    StandardMarketOddsDTO marketOddsDTO = JSON.parseObject(JSON.toJSONString(marketOdds), StandardMarketOddsDTO.class);
                    marketOddsDTO.setMarketDiffValue(null);
                    marketOddsDTO.setNameExpressionValue(MarketUtils.getNameExpressionValue(oddsType, marketValue));
                    if (OddsTypeEnum.isHomeOddsType(oddsType)) {
                        marketOddsDTO.setOddsValue(getConvertedOdds(malayOddsMap.get(RcsConstant.HOME_POSITION)));
                        BigDecimal marketDiffValue = malayOddsMap.get("home_market_diff_value");
                        if (marketDiffValue != null) {
                            marketOddsDTO.setMarketDiffValue(marketDiffValue.doubleValue());
                        }
                    } else if (OddsTypeEnum.isAwayOddsType(oddsType)) {
                        marketOddsDTO.setOddsValue(getConvertedOdds(malayOddsMap.get(RcsConstant.AWAY_POSITION)));
                        BigDecimal marketDiffValue = malayOddsMap.get("away_market_diff_value");
                        if (marketDiffValue != null) {
                            marketOddsDTO.setMarketDiffValue(marketDiffValue.doubleValue());
                        }
                    }
                    marketOddsDTO.setDataSourceCode(BaseConstants.DATA_SOURCE_CODE);
                    newMarketOddsList.add(marketOddsDTO);
                }
                newMarket.setMarketOddsList(newMarketOddsList);
            } else {
                newMarket.setThirdMarketSourceStatus(TradeStatusEnum.CLOSE.getStatus());
            }
            marketList.add(newMarket);
        }
        if (isFirst) {
            Integer tradeMode = getTradeMode(matchId, playId);
            if (!TradeModeEnum.isAutoPlus(tradeMode)) {
                log.warn("不是A+模式不推赔率：matchId={},playId={},tradeMode={}", matchId, playId, tradeMode);
                return Lists.newArrayList();
            }
        }
        return marketList;
    }

    @Override
    public List<StandardMarketDTO> buildMarketSingleOperList(Long matchId, StandardMarketMessage market, BuildMarketPlayConfig config, boolean isFirst) {
        //玩法ID
        Long playId = market.getMarketCategoryId();
        //盘口投注项
        List<StandardMarketOddsMessage> marketOddsList = market.getMarketOddsList();
        //单双玩法固定一个盘口
        int placeNum = 1;
        // 构建盘口基本信息
        StandardMarketDTO standardMarketDTO = MarketUtils.buildStandardMarket(playId, playId, config.getMatchType(), placeNum, null, null);
        // 位置状态，默认开
        Map<Integer, Integer> placeStatusMap = getMarketPlaceStatusFromRedis(matchId, playId);
        //操盘状态
        Integer placeStatus = placeStatusMap.getOrDefault(placeNum, TradeStatusEnum.OPEN.getStatus());
        standardMarketDTO.setPlaceNumStatus(placeStatus);
        //三方盘口源状态
        Integer sourceStatus = market.getThirdMarketSourceStatus();
        standardMarketDTO.setThirdMarketSourceStatus(sourceStatus);
        // 水差 = 位置水差，上盘赔率减水差，下盘赔率加水差
        BigDecimal waterDiff = config.getPlaceWaterDiffMap().getOrDefault(NumberUtils.INTEGER_ONE, BigDecimal.ZERO);
        // 主盘 spread
        BigDecimal spread = config.getPlaceSpreadMap().get(NumberUtils.INTEGER_ONE);
        //盘口状态状态判断
        if(!TradeStatusEnum.isOpen(sourceStatus)){
            standardMarketDTO.setStatus(sourceStatus);
        }else {
            standardMarketDTO.setStatus(placeStatus);
        }
        log.info("::{}::A+模式构建盘口(让分-大小),盘口Id:={},玩法Id:={},placeNum:={},盘口状态:={}","数据商盘口源状态:={}",
                matchId, market.getId(), market.getMarketCategoryId(),placeNum, standardMarketDTO.getStatus(),standardMarketDTO.getThirdMarketSourceStatus());

        List<StandardMarketDTO> marketList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(marketOddsList)) {
            // 标准投注项模板ID，重要，否则前端不能投注
            Map<String, Long> oddsFieldsTemplateIdMap = marketOddsList.stream().collect(Collectors.toMap(StandardMarketOddsMessage::getOddsType, StandardMarketOddsMessage::getOddsFieldsTemplateId));
            // 计算初始赔率
            // 1 - ( spread / 2 )
            BigDecimal myOdds = BigDecimalUtils.calMyOddsBySpread(spread);

            // 位置水差计算，篮球水差固定在下盘，上盘赔率减水差，下盘赔率加水差
            BigDecimal homeOdds = MarketUtils.checkMalayOdds(BigDecimalUtils.ROUND_DOWN_2.subtract(myOdds, waterDiff));
            BigDecimal awayOdds = MarketUtils.checkMalayOdds(BigDecimalUtils.ROUND_DOWN_2.add(myOdds, waterDiff));

            // 构建上盘赔率信息
            StandardMarketOddsDTO homeMarketOdds = MarketUtils.buildStandardMarketOdds(OddsTypeEnum.ODD, waterDiff, null);
            homeMarketOdds.setOddsValue(getConvertedOdds(homeOdds));
            homeMarketOdds.setOriginalOddsValue(getConvertedOdds(myOdds));
            homeMarketOdds.setOddsFieldsTemplateId(oddsFieldsTemplateIdMap.get(OddsTypeEnum.ODD));

            // 构建下盘赔率信息
            StandardMarketOddsDTO awayMarketOdds = MarketUtils.buildStandardMarketOdds(OddsTypeEnum.EVEN, waterDiff, null);
            awayMarketOdds.setOddsValue(getConvertedOdds(awayOdds));
            awayMarketOdds.setOriginalOddsValue(getConvertedOdds(myOdds));
            awayMarketOdds.setOddsFieldsTemplateId(oddsFieldsTemplateIdMap.get(OddsTypeEnum.EVEN));
            standardMarketDTO.setMarketOddsList(Lists.newArrayList(homeMarketOdds, awayMarketOdds));
            log.info("计算后的盘口信息marketOddsList：{}", standardMarketDTO.getMarketOddsList());
        }else {
            standardMarketDTO.setThirdMarketSourceStatus(TradeStatusEnum.CLOSE.getStatus());
        }
        marketList.add(standardMarketDTO);

        if (isFirst) {
            Integer tradeMode = getTradeMode(matchId, playId);
            if (!TradeModeEnum.isAutoPlus(tradeMode)) {
                log.warn("不是A+模式不推赔率：matchId={},playId={},tradeMode={}", matchId, playId, tradeMode);
                return Lists.newArrayList();
            }
        }
        return marketList;
    }

    private int malayOddsToOddsValue(BigDecimal malayOdds) {
        BigDecimal euOdds = new BigDecimal(rcsOddsConvertMappingService.getEUOdds(malayOdds.toPlainString()));
        return euOdds.multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).intValue();
    }

    /**
     * 计算100000倍欧赔
     *
     * @param spread             位置spread
     * @param waterDiff          位置水差
     * @param oddsChange         赔率修正值
     * @return
     */
    private Map<String, Integer> calOddsValue(final BigDecimal spread, final BigDecimal waterDiff, final BigDecimal oddsChange) {
        // 1 - ( spread / 2 )
        BigDecimal myOdds = BigDecimalUtils.calMyOddsBySpread(spread);
        BigDecimal homeOdds = OddsConvertUtils.checkMalayOdds(BigDecimalUtils.ROUND_DOWN_2.add(myOdds, oddsChange));
        BigDecimal awayOdds = OddsConvertUtils.checkMalayOdds(BigDecimalUtils.ROUND_DOWN_2.subtract(myOdds, oddsChange));
        Map<String, Integer> map = Maps.newHashMap();
        map.put(HOME_ORIGINAL_ODDS, rcsOddsConvertMappingService.myOddsToOddsValue(homeOdds));
        map.put(AWAY_ORIGINAL_ODDS, rcsOddsConvertMappingService.myOddsToOddsValue(awayOdds));
        // 位置水差计算，篮球水差固定在下盘，上盘赔率减水差，下盘赔率加水差
        homeOdds = MarketUtils.checkMalayOdds(BigDecimalUtils.ROUND_DOWN_2.subtract(homeOdds, waterDiff));
        awayOdds = OddsConvertUtils.checkMalayOdds(BigDecimalUtils.ROUND_DOWN_2.add(awayOdds, waterDiff));
        map.put(HOME_ODDS, rcsOddsConvertMappingService.myOddsToOddsValue(homeOdds));
        map.put(AWAY_ODDS, rcsOddsConvertMappingService.myOddsToOddsValue(awayOdds));
        return map;
    }

    private Integer getTradeMode(Long matchId, Long playId) {
        String key = RedisKey.getTradeModeKey(matchId);
        String hashKey = String.valueOf(playId);
        String value = redisClient.hGet(key, hashKey);
        log.info("获取操盘模式：key={},hashKey={},value={}", key, hashKey, value);
        return NumberUtils.toInt(value, TradeModeEnum.AUTO.getMode());
    }
}
