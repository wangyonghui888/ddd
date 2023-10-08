package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.calc.service.MarketBuildService;
import com.panda.sport.rcs.data.calc.service.MarketValueCalculatorService;
import com.panda.sport.rcs.data.calc.service.impl.MarketValueCalcServiceImpl;
import com.panda.sport.rcs.data.config.RedissonManager;
import com.panda.sport.rcs.data.constant.Constants;
import com.panda.sport.rcs.data.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.data.utils.MarketUtils;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.TradeStatusEnum;
import com.panda.sport.rcs.pojo.config.BuildMarketConfigDto;
import com.panda.sport.rcs.pojo.config.BuildMarketPlayConfig;
import com.panda.sport.rcs.pojo.config.MarketBuildConfig;
import com.panda.sport.rcs.pojo.config.MarketBuildPlayConfig;
import com.panda.sport.rcs.pojo.dto.ClearDTO;
import com.panda.sport.rcs.pojo.dto.ClearSubDTO;
import com.panda.sport.rcs.utils.i18n.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : A+自动加强模式
 * @Author : Paca
 * @Date : 2021-10-30 18:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
@Service
@RocketMQMessageListener(
        topic = "STANDARD_MARKET_ODDS_RISK",
        consumerGroup = "RCS_DATA_STANDARD_MARKET_ODDS_RISK_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class AutoPlusModeConsumer extends RcsConsumer<Request<StandardMatchMarketMessage>> {

    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    private MarketValueCalculatorService marketValueCalculatorService;

    @Autowired
    private MarketBuildService marketBuildService;

    @Autowired
    private RedissonManager redissonManager;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RDSProducerSendMessageUtils producerSendMessageUtils;

    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketOddsApi tradeMarketOddsApi;

    @Override
    protected String getTopic() {
        return "STANDARD_MARKET_ODDS_RISK";
    }

    private boolean msgCheck(Request<StandardMatchMarketMessage> msg) {
        Long dataSourceTime = msg.getDataSourceTime();
        if (dataSourceTime == null) {
            log.warn("::{}::没有带时间戳，当前数据不做更新！","RDSMODSG_"+msg.getLinkId()+"_"+msg.getData().getStandardMatchInfoId());
            return false;
        }
        StandardMatchMarketMessage data = msg.getData();
        Long sportId = data.getSportId();
        Long matchId = data.getStandardMatchInfoId();
        if (!SportIdEnum.isBasketball(sportId)) {
            log.warn("::{}::篮球才有A+模式：matchId=" + matchId,"RDSMODSG_"+msg.getLinkId()+"_"+msg.getData().getStandardMatchInfoId());
            return false;
        }
        if (CollectionUtils.isEmpty(data.getMarketList())) {
            log.warn("::{}::无盘口数据：matchId=" + matchId,"RDSMODSG_"+msg.getLinkId()+"_"+msg.getData().getStandardMatchInfoId());
            return false;
        }
        return true;
    }

    @Override
    public Boolean handleMs(Request<StandardMatchMarketMessage> msg) {
        if (!msgCheck(msg)) {
            return true;
        }
        String linkId = msg.getLinkId();
        Long dataSourceTime = msg.getDataSourceTime();
        StandardMatchMarketMessage data = msg.getData();
        List<StandardMarketMessage> marketList = getMainMarketList(data.getMarketList());
        if (CollectionUtils.isEmpty(marketList)) {
            log.info("::{}::A+模式盘口构建，无主盘口信息","RDSMODSG_"+msg.getLinkId()+"_"+msg.getData().getStandardMatchInfoId());
            return true;
        }
        Map<Long, List<StandardMarketDTO>> map = Maps.newHashMap();
        Long matchId = data.getStandardMatchInfoId();
        for (StandardMarketMessage market : marketList) {
            dataProviderSeal(matchId, market,msg);
            Long playId = market.getMarketCategoryId();
            Long subPlayId = market.getChildMarketCategoryId();
            String newMarketValue = market.getAddition1();
            String newSourceStatus = String.valueOf(market.getThirdMarketSourceStatus());
            String newMarketType = String.valueOf(market.getMarketType());
            String newMarketSource = String.valueOf(market.getMarketSource());
            String lock = "datasource_major_market_change_lock:" + matchId + ":" + playId;
            try {
                redissonManager.lock(lock);
                String newMarketBuildSign = generateMarketBuildSign(dataSourceTime, market);
                String oldMarketBuildSign = getMarketBuildSignFromRedis(matchId, playId, subPlayId,msg);
                log.info("::{}::新旧盘口标志：matchId={},playId={},newMarketBuildSign={},oldMarketBuildSign={}","RDSMODSG_"+msg.getLinkId()+"_"+matchId, matchId, playId, newMarketBuildSign, oldMarketBuildSign);
                boolean isClearWaterDiff = false;
                boolean isClearMarketHeadGap = false;

                if (StringUtils.isNotBlank(oldMarketBuildSign)) {
                    String[] array = oldMarketBuildSign.split("_");
                    Long oldDataSourceTime = NumberUtils.toLong(array[0]);
                    String oldMarketValue = array[1];
                    String oldSourceStatus = array[2];
                    String oldMarketType = array[3];
                    String oldMarketSource = array[4];
                    if (oldDataSourceTime > dataSourceTime) {
                        log.warn("::{}::缓存时间大于当前数据时间，老数据不处理：matchId={},playId={},oldDataSourceTime={},dataSourceTime={}","RDSMODSG_"+msg.getLinkId()+"_"+matchId, matchId, playId, oldDataSourceTime, dataSourceTime);
                        continue;
                    }
                    // 缓存数据源主盘口信息
                    setMainMarketInfoToRedis(matchId, market, linkId, dataSourceTime,msg);
                    boolean marketTypeEquals = StringUtils.equals(newMarketType, oldMarketType);
                    boolean marketSourceEquals = StringUtils.equals(newMarketSource, oldMarketSource);
                    isClearWaterDiff = !marketTypeEquals || !marketSourceEquals;
                    isClearMarketHeadGap = !marketTypeEquals;
                    if (StringUtils.equals(newMarketValue, oldMarketValue) && StringUtils.equals(newSourceStatus, oldSourceStatus) && marketTypeEquals && marketSourceEquals) {
                        log.warn("::{}::主盘口信息未改变，不需要构建","RDSMODSG_"+msg.getLinkId()+"_"+matchId);
                        continue;
                    }
                } else {
                    // 缓存数据源主盘口信息
                    setMainMarketInfoToRedis(matchId, market, linkId, dataSourceTime, msg);
                }
                //查询篮球构建盘口配置(玩法配置/分时配置)
                BuildMarketPlayConfig buildMarketConfig = marketBuildService.queryBasketballBuildMarketConfig(matchId, playId);
                if (isClearWaterDiff) {
                    buildMarketConfig.setPlaceWaterDiffMap(Maps.newHashMap());
                    clearWaterDiff(matchId, playId, linkId);
                }
                if (isClearMarketHeadGap) {
                    buildMarketConfig.setMarketHeadGap(BigDecimal.ZERO);
                }
                //2022/06/06 add by 篮球单双玩法增加A+操盘模式
                if(Constants.BASKETBALL_SINGLE_DOUBLE_PLAY.contains(playId)){
                    List<StandardMarketDTO> marketDTOList = marketValueCalculatorService.buildMarketSingleOperList(matchId, market, buildMarketConfig, StringUtils.isBlank(oldMarketBuildSign));
                    if (CollectionUtils.isNotEmpty(marketDTOList)) {
                        map.put(playId, marketDTOList);
                    }
                }else{
                    //A+模式构建盘口(让分,大小)
                    List<StandardMarketDTO> marketDTOList = marketValueCalculatorService.buildMarketList(matchId, market, buildMarketConfig, StringUtils.isBlank(oldMarketBuildSign));
                    if (CollectionUtils.isNotEmpty(marketDTOList)) {
                        map.put(playId, marketDTOList);
                    }
                }

            } catch (Exception e) {
                log.error("::{}::A+模式盘口构建异常{}", "RDSMODSG_"+msg.getLinkId()+"_"+matchId,e);
            } finally {
                redissonManager.unlock(lock);
            }
        }
        if (CollectionUtils.isNotEmpty(map)) {
            List<StandardMarketDTO> marketDTOList = Lists.newArrayList();
            map.forEach((playId, list) -> {
                marketDTOList.addAll(list);
                List<BigDecimal> marketValueList = list.stream().map(market -> CommonUtils.toBigDecimal(market.getAddition1())).collect(Collectors.toList());
                if (Basketball.Main.getHandicapPlayIds().contains(playId) && MarketUtils.isSealCheck(marketValueList)) {
                    // 让球玩法只要出现0或±0.5的球头，独赢玩法自动封盘，独赢玩法盘口级封盘
                    marketValueCalculatorService.basketballWinSeal(matchId, Basketball.Main.getWinAloneByHandicap(playId));
                }
            });
            //下发盘口数据到融合
            if (CollectionUtils.isNotEmpty(marketDTOList)) {
                putTradeMarketOdds(matchId, marketDTOList);
            }
        }
        return true;
    }

    /**
     * 下发盘口数据到融合
     * @param matchId
     * @param marketList
     * @return
     */
    private Response putTradeMarketOdds(Long matchId, List<StandardMarketDTO> marketList) {
        StandardMatchMarketDTO standardMatchMarketDTO = new StandardMatchMarketDTO();
        standardMatchMarketDTO.setStandardMatchInfoId(matchId);
        standardMatchMarketDTO.setMarketList(marketList);
        return DataRealtimeApiUtils.handleApi(standardMatchMarketDTO, new DataRealtimeApiUtils.ApiCall() {
            @Override
            @Trace
            public <R> Response<R> callApi(Request request) {
                return tradeMarketOddsApi.putTradeMarketOdds(request);
            }
        });
    }

    private List<StandardMarketMessage> getMainMarketList(List<StandardMarketMessage> marketList) {
        // 获取玩法主盘口信息
        return marketList.stream()
                .filter(market -> NumberUtils.INTEGER_ONE.equals(market.getPlaceNum()))
                .peek(market -> {
                    if (market.getThirdMarketSourceStatus() == null) {
                        market.setThirdMarketSourceStatus(TradeStatusEnum.OPEN.getStatus());
                    }
                    if (market.getPlaceNumStatus() == null) {
                        market.setPlaceNumStatus(TradeStatusEnum.OPEN.getStatus());
                    }
                    if (market.getStatus() == null) {
                        market.setStatus(TradeStatusEnum.OPEN.getStatus());
                    }
                }).collect(Collectors.toList());
    }

    private void clearWaterDiff(Long matchId, Long playId, String linkId) {
        ClearSubDTO clearSubDTO = new ClearSubDTO();
        clearSubDTO.setMatchId(matchId);
        clearSubDTO.setPlayId(playId);

        ClearDTO clearDTO = new ClearDTO();
        clearDTO.setSportId(SportIdEnum.BASKETBALL.getId());
        clearDTO.setType(0);
        clearDTO.setClearType(15);
        clearDTO.setMatchId(matchId);
        clearDTO.setList(Lists.newArrayList(clearSubDTO));
        producerSendMessageUtils.sendMessage("RCS_CLEAR_MATCH_MARKET_TAG", null, linkId + "_clearWaterDiff", clearDTO);
    }

    private BuildMarketConfigDto getBuildMarketConfig(Long matchId, Long playId) {
        BuildMarketConfigDto configDto = new BuildMarketConfigDto();
        configDto.setMatchId(matchId);
        configDto.setPlayId(playId);
        // 最大盘口数
        Integer marketCount = 1;
        // 相邻盘口差值
        BigDecimal marketNearDiff = BigDecimal.ONE;
        // 相邻盘口赔率差值
        BigDecimal marketNearOddsDiff = new BigDecimal("0.15");
        // 盘口调整幅度
        BigDecimal marketAdjustRange = BigDecimal.ONE;
        // 主盘 spread
        final BigDecimal mainSpread;
        MarketBuildPlayConfig playConfig = rcsMatchMarketConfigMapper.queryMarketBuildPlayConfig(matchId, playId.intValue());
        log.info("A+模式构建盘口，玩法配置：matchId={},playId={}", matchId, playId);
        if (playConfig != null) {
            configDto.setMatchType(playConfig.getMatchType());
            configDto.setMarketType(playConfig.getMarketType());
            marketCount = Optional.ofNullable(playConfig.getMarketCount()).orElse(marketCount);
            marketNearDiff = Optional.ofNullable(playConfig.getMarketNearDiff()).orElse(marketNearDiff);
            marketNearOddsDiff = Optional.ofNullable(playConfig.getMarketNearOddsDiff()).orElse(marketNearOddsDiff);
            marketAdjustRange = Optional.ofNullable(playConfig.getMarketAdjustRange()).orElse(marketAdjustRange);
            mainSpread = CommonUtils.toBigDecimal(playConfig.getSpread(), new BigDecimal("0.2"));
        } else {
            mainSpread = new BigDecimal("0.2");
        }
        Map<Integer, BigDecimal> spreadMap = Maps.newHashMap();
        spreadMap.put(NumberUtils.INTEGER_ONE, mainSpread);
        // 盘口差
        BigDecimal marketHeadGap = BigDecimal.ZERO;
        // 位置水差
        Map<Integer, BigDecimal> placeWaterDiffMap = Maps.newHashMap();
        List<MarketBuildConfig> marketBuildConfigList = rcsMatchMarketConfigMapper.listMarketBuildConfig(matchId, playId);
        log.info("A+模式构建盘口，位置配置：matchId={},playId={}", matchId, playId);
        if (CollectionUtils.isNotEmpty(marketBuildConfigList)) {
            MarketBuildConfig mainConfig = marketBuildConfigList.get(0);
            marketHeadGap = mainConfig.getMarketHeadGap();
            String redisKey = String.format("rcs:task:match:event:%s", matchId);
            String eventCode = redisClient.get(redisKey);
            log.info("A+模式构建盘口，事件编码：matchId={},playId={},eventCode={}", matchId, playId, eventCode);
            marketBuildConfigList.forEach(config -> {
                Integer placeNum = config.getPlaceNum();
                placeWaterDiffMap.put(placeNum, config.getPlaceWaterDiff());
                if ("timeout".equals(eventCode)) {
                    // 比赛暂停取暂停spread
                    spreadMap.put(placeNum, Optional.ofNullable(config.getTimeOutMargin()).orElse(mainSpread));
                } else {
                    spreadMap.put(placeNum, Optional.ofNullable(config.getMargin()).orElse(mainSpread));
                }
            });
        }

        configDto.setMarketHeadGap(marketHeadGap);
        configDto.setMarketCount(marketCount);
        configDto.setMarketNearDiff(marketNearDiff);
        configDto.setMarketNearOddsDiff(marketNearOddsDiff);
        configDto.setMarketAdjustRange(marketAdjustRange);
        configDto.setPlaceSpreadMap(spreadMap);
        configDto.setPlaceWaterDiffMap(placeWaterDiffMap);
        log.info("A+模式构建盘口配置：matchId={},playId={}", matchId, playId);
        return configDto;
    }

    private String getMarketBuildSignFromRedis(Long matchId, Long playId, Long subPlayId, Request<StandardMatchMarketMessage> msg) {
        String key = RedisKey.MainMarket.getAutoPlusMainMarketInfoKey(matchId);
        String hashKey = RedisKey.MainMarket.getAutoPlusMainMarketInfoHashKey(playId, subPlayId);
        String value = redisClient.hGet(key, hashKey);
        log.info("::{}::获取旧盘口标志：key={},hashKey={},value={}", "RDSMODSG_"+msg.getLinkId()+"_"+matchId,key, hashKey, value);
        if (StringUtils.isBlank(value)) {
            return "";
        }
        JSONObject jsonObject = JSON.parseObject(value);
        Long dataSourceTime = jsonObject.getLong("dataSourceTime");
        StandardMarketMessage market = JSON.parseObject(value, StandardMarketMessage.class);
        return generateMarketBuildSign(dataSourceTime, market);
    }

    private void setMainMarketInfoToRedis(Long matchId, StandardMarketMessage mainMarket, String linkId, Long dataSourceTime, Request<StandardMatchMarketMessage> msg) {
        Long playId = mainMarket.getMarketCategoryId();
        Long subPlayId = mainMarket.getChildMarketCategoryId();
        String key = RedisKey.MainMarket.getAutoPlusMainMarketInfoKey(matchId);
        String hashKey = RedisKey.MainMarket.getAutoPlusMainMarketInfoHashKey(playId, subPlayId);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(mainMarket);
        jsonObject.put("linkId", linkId);
        jsonObject.put("dataSourceTime", dataSourceTime);
        // 缓存A+模式数据源主盘口信息，早盘缓存30天，滚球缓存3天
        redisClient.hSet(key, hashKey, jsonObject.toJSONString());
        log.info("::{}::缓存A+模式数据源主盘口信息：key={},hashKey={},linkId={},dataSourceTime={}", "RDSMODSG_"+msg.getLinkId()+"_"+matchId,key, hashKey, linkId, dataSourceTime);
        if (NumberUtils.INTEGER_ZERO.equals(mainMarket.getMarketType())) {
            redisClient.expireKey(key, RedisKey.Second.DAYS_3);
        } else {
            redisClient.expireKey(key, RedisKey.Second.DAYS_30);
        }
    }

    /**
     * 生成盘口构建标志
     *
     * @param dataSourceTime
     * @param market
     * @return
     */
    private String generateMarketBuildSign(Long dataSourceTime, StandardMarketMessage market) {
        String newMarketValue = market.getAddition1();
        String newSourceStatus = String.valueOf(market.getThirdMarketSourceStatus());
        String newMarketType = String.valueOf(market.getMarketType());
        String newMarketSource = String.valueOf(market.getMarketSource());
        return String.format("%s_%s_%s_%s_%s", dataSourceTime, newMarketValue, newSourceStatus, newMarketType, newMarketSource);
    }

    /**
     * A+模式数据商挡板封盘
     *
     * @param market
     * @param msg
     */
    private void dataProviderSeal(Long matchId, StandardMarketMessage market, Request<StandardMatchMarketMessage> msg) {
        Integer status = market.getCategorySuspended();
        if (status != null && !TradeStatusEnum.isOpen(status)) {
            Long playId = market.getMarketCategoryId();
            String key = String.format(MarketValueCalcServiceImpl.RCS_MARKET_STATUS_CONFIG, matchId, playId);
            for (int placeNum = 1; placeNum <= RcsConstant.DEFAULT_MARKET_PLACE_AMOUNT; placeNum++) {
                redisClient.hSet(key, String.valueOf(placeNum), String.valueOf(status));
            }
            log.info("::{}::数据商挡板封盘：matchId={},playId={}","RDSMODSG_"+msg.getLinkId()+"_"+matchId, matchId, playId);
        }
    }
}
