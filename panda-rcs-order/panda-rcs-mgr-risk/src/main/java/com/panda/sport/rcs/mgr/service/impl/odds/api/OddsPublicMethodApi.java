package com.panda.sport.rcs.mgr.service.impl.odds.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.panda.merge.api.IOutrightTradeConfigApi;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.sport.data.rcs.dto.RedisCacheSyncBean;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.*;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.RcsOddsConvertMappingMapper;
import com.panda.sport.rcs.mapper.RcsStandardOutrightMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.statistics.RcsMatchPlayConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mgr.constant.RcsCacheContant;
import com.panda.sport.rcs.mgr.enums.LanguageTypeDataEnum;
import com.panda.sport.rcs.mgr.service.impl.odds.OddsCalcuCommonService;
import com.panda.sport.rcs.mgr.utils.LanguageUtils;
import com.panda.sport.rcs.mgr.utils.MarketUtils;
import com.panda.sport.rcs.mongo.MarketCategory;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.dto.RcsBroadCast;
import com.panda.sport.rcs.pojo.dto.RcsBroadCastDTO;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.utils.DataRealtimeApiUtils;
import com.panda.sport.rcs.utils.NameExpressionValueUtils;
import com.panda.sport.rcs.utils.StringUtils;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig;
import com.panda.sport.rcs.vo.odds.MatchOddsConfig;
import com.panda.sport.rcs.vo.odds.MatchPlayConfig;
import com.panda.sport.rcs.vo.odds.RcsStandardMarketDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OddsPublicMethodApi {

    @Reference(check = false, lazy = true, retries = 0, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Autowired
    private RcsOddsConvertMappingMapper rcsOddsConvertMappingMapper;

    @Autowired
    private RcsMatchPlayConfigMapper rcsMatchPlayConfigMapper;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;

    @Autowired
    private LanguageUtils languageUtils;
    public static final String TRADE_ODDS_TOPIC = "RCS_TRADE_MATCH_ODDS_CONFIG";
    @Autowired
    private RcsTournamentTemplatePlayMargainMapper rcsTournamentTemplatePlayMargainMapper;

    @Autowired
    private RcsStandardOutrightMatchInfoMapper rcsStandardOutrightMatchInfoMapper;
    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private IOutrightTradeConfigApi iOutrightTradeConfigApi;

    @Autowired
    RedisClient redisClient;
    @Autowired
    OddsCalcuCommonService oddsCalcuCommonService;
    @Autowired
    private StandardSportMarketOddsMapper sportMarketOddsMapper;

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * @Description
     *  1、大、单、是为上盘
     *  2、小、双、否为下盘
     *  4、让球盘
     *      a、让球方为上盘，受让方为下盘
     *      b、让0球时赔率小的为上盘，赔率大的为下盘
     *      c、赔率相同的情况下，主队上盘、客队下盘
     * @Param 玩法id  盘口值
     **/
    public String getOddsType(List<StandardSportMarketOdds> odds,RcsMatchMarketConfig config){
        String oddsType = BaseConstants.ODD_TYPE_2;
        if (!ObjectUtils.isEmpty(odds)){
            for (StandardSportMarketOdds odd : odds) {
                if (BaseConstants.ODD_TYPE_UNDER.equalsIgnoreCase(odd.getOddsType()) ||
                        BaseConstants.ODD_TYPE_OVER.equalsIgnoreCase(odd.getOddsType())) {
                    oddsType = BaseConstants.ODD_TYPE_UNDER;
                    break;
                } else if (BaseConstants.ODD_TYPE_ODD.equalsIgnoreCase(odd.getOddsType()) ||
                        BaseConstants.ODD_TYPE_EVEN.equalsIgnoreCase(odd.getOddsType())) {
                    oddsType = BaseConstants.ODD_TYPE_EVEN;
                    break;
                } else if (BaseConstants.ODD_TYPE_YES.equalsIgnoreCase(odd.getOddsType()) ||
                        BaseConstants.ODD_TYPE_NO.equalsIgnoreCase(odd.getOddsType())) {
                    oddsType = BaseConstants.ODD_TYPE_NO;
                    break;
                } else if (BaseConstants.ODD_TYPE_X.equalsIgnoreCase(odd.getOddsType())) {
                    oddsType = BaseConstants.ODD_TYPE_X;
                    break;
                } else if (!SportIdEnum.isFootball(config.getSportId())) {
                    if (BaseConstants.ODD_TYPE_1.equalsIgnoreCase(odd.getOddsType()) ||
                            BaseConstants.ODD_TYPE_2.equalsIgnoreCase(odd.getOddsType())) {
                        oddsType = BaseConstants.ODD_TYPE_2;
                        break;
                    }
                }else if ((StringUtils.isNotBlank(odd.getAddition1()))) {
                    BigDecimal value = new BigDecimal(odd.getAddition1());
                    if (value.compareTo(BigDecimal.ZERO) <= 0) {
                        oddsType = BaseConstants.ODD_TYPE_2;
                    } else {
                        oddsType = BaseConstants.ODD_TYPE_1;
                    }
                    break;
                }
            }
        }
        return oddsType;
    }

    /**
     * 获取篮球下盘的oddsType  固定写死 客队
     * @Title: getBasketBallDownOddsType
     * @Description: TODO
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
    public String getBasketBallDownOddsType(List<StandardSportMarketOdds> odds) {
        for(StandardSportMarketOdds odd : odds ) {
            if (BaseConstants.ODD_TYPE_UNDER.equalsIgnoreCase(odd.getOddsType()) ||
                    BaseConstants.ODD_TYPE_OVER.equalsIgnoreCase(odd.getOddsType())){
                return BaseConstants.ODD_TYPE_UNDER;
            } else if (BaseConstants.ODD_TYPE_ODD.equalsIgnoreCase(odd.getOddsType()) ||
                    BaseConstants.ODD_TYPE_EVEN.equalsIgnoreCase(odd.getOddsType())){
                return BaseConstants.ODD_TYPE_EVEN;
            }else if (BaseConstants.ODD_TYPE_YES.equalsIgnoreCase(odd.getOddsType()) ||
                    BaseConstants.ODD_TYPE_NO.equalsIgnoreCase(odd.getOddsType())){
                return BaseConstants.ODD_TYPE_NO;
            }else if (StringUtils.isNotBlank(odd.getAddition1())){
                return BaseConstants.ODD_TYPE_2;
            }
        }

        return BaseConstants.ODD_TYPE_2;
    }

    /**
     * @Description
     *  1、大、单、是为上盘
     *  2、小、双、否为下盘
     *  4、让球盘
     *      a、让球方为上盘，受让方为下盘
     *      b、让0球时赔率小的为上盘，赔率大的为下盘
     *      c、赔率相同的情况下，主队上盘、客队下盘
     * @Param [oddsVoList, playOptionsId]
     * @Author  Sean
     * @Date  10:34 2020/7/5
     * @return java.lang.String
     **/
    public String getTwoPositon(List<StandardSportMarketOdds> oddsVoList,Long playOptionsId,RcsMatchMarketConfig config){
        String positon = "";
        String oddsType = getOddsType(oddsVoList,config);
        for (StandardSportMarketOdds odds : oddsVoList){
            if (odds.getId().longValue() == playOptionsId.longValue()){
                if (odds.getOddsType().equalsIgnoreCase(oddsType)) {
                    positon = BaseConstants.ODD_TYPE_AWAY;
                }else {
                    positon = BaseConstants.ODD_TYPE_HOME;
                }
            }
        }
        return positon;
    }

    public Boolean checkTwoWayForNull(TwowayDoubleOverLoadTriggerItem overLoadTriggerItem) {

        if (ObjectUtils.isEmpty(overLoadTriggerItem.getLimitLevel())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    public BigDecimal getHomeOddsRate(TwowayDoubleOverLoadTriggerItem overLoadTriggerItem,RcsMatchMarketConfig config){

        if (overLoadTriggerItem.getLimitLevel().intValue() == NumberUtils.INTEGER_ONE){
            return overLoadTriggerItem.getHomeLevelFirstOddsRate();
        }
        if (overLoadTriggerItem.getLimitLevel().intValue() == NumberUtils.INTEGER_TWO){
            return overLoadTriggerItem.getHomeLevelSecondOddsRate();
        }
        if (ObjectUtils.isNotEmpty(config.getIsMultipleJumpOdds()) && config.getIsMultipleJumpOdds().intValue() == 1 && config.getOddChangeRule() ==1){
            Integer level2 = overLoadTriggerItem.getLimitLevel() / 10000;
            Integer level1 = (overLoadTriggerItem.getLimitLevel() % 10000) / 100;
            return new BigDecimal(level2).multiply(overLoadTriggerItem.getHomeLevelSecondOddsRate())
                    .add(new BigDecimal(level1).multiply(overLoadTriggerItem.getHomeLevelFirstOddsRate()));
        }
        return BigDecimal.valueOf(0);
    }

    public BigDecimal getAwayOddsRate(TwowayDoubleOverLoadTriggerItem overLoadTriggerItem,RcsMatchMarketConfig config){

        if (overLoadTriggerItem.getLimitLevel().intValue() == NumberUtils.INTEGER_ONE){
            return overLoadTriggerItem.getAwayLevelFirstOddsRate();
        }
        if (overLoadTriggerItem.getLimitLevel().intValue() == NumberUtils.INTEGER_TWO){
            return overLoadTriggerItem.getAwayLevelSecondOddsRate();
        }
        if (ObjectUtils.isNotEmpty(config.getIsMultipleJumpOdds()) && config.getIsMultipleJumpOdds().intValue() == 1 && config.getOddChangeRule() ==1){
            Integer level2 = overLoadTriggerItem.getLimitLevel() / 10000;
            Integer level1 = (overLoadTriggerItem.getLimitLevel() % 10000) / 100;
            return new BigDecimal(level2).multiply(overLoadTriggerItem.getAwayLevelSecondOddsRate())
                    .add(new BigDecimal(level1).multiply(overLoadTriggerItem.getAwayLevelFirstOddsRate()));
        }
        return BigDecimal.valueOf(0);
    }

    /**
     * @Description   //自动水差范围应在-0.30<=OddsGap<=0.30
     * @Param [waterValue]
     * @Author  Sean
     * @Date  17:48 2020/7/1
     * @return java.math.BigDecimal
     **/
    public BigDecimal checkWaterValue(BigDecimal waterValue){
        if (waterValue.compareTo(new BigDecimal("0.3")) == NumberUtils.INTEGER_ONE){
            return new BigDecimal("0.3");
        }
        if (waterValue.compareTo(new BigDecimal("-0.3")) == NumberUtils.INTEGER_MINUS_ONE){
            return new BigDecimal("-0.3");
        }
        return waterValue;
    }
    /**
     * @Description   //概率差校验
     * @Param [probability]
     * @Author  sean
     * @Date   2021/5/21
     * @return java.math.BigDecimal
     **/
    public BigDecimal checkProbability(BigDecimal probability){
        if (probability.compareTo(new BigDecimal("30")) == NumberUtils.INTEGER_ONE){
            return new BigDecimal("30");
        }
        if (probability.compareTo(new BigDecimal("-30")) == NumberUtils.INTEGER_MINUS_ONE){
            return new BigDecimal("-30");
        }
        return probability;
    }

    /**
     * 构建marginlist
     * @Title: buildMargainList
     * @Description: TODO
     * @param @return    设定文件
     * @return List<MarketMarginDtlDTO>    返回类型
     * @throws
     */
    public TradeMarketAutoDiffConfigItemDTO buildWarterList(Integer playId, Long marketId , String oddsType , BigDecimal warter,String subPlayId){
        TradeMarketAutoDiffConfigItemDTO warterBean = new TradeMarketAutoDiffConfigItemDTO();
        warterBean.setOddType(oddsType);
        warterBean.setDiffValue(warter.doubleValue());
        warterBean.setMarketCategoryId(playId.longValue());
        warterBean.setMarketId(marketId);
        if (RcsConstant.FOOTBALL_X_MY_PLAYS.contains(playId)){
            warterBean.setChildStandardCategoryId(Long.parseLong(subPlayId));
        }
        return warterBean;
    }

    public MarketMarginDtlDTO buildMargainList(String oddsType , BigDecimal warter){
        MarketMarginDtlDTO warterBean = new MarketMarginDtlDTO();
        warterBean.setOddsType(oddsType);
        warterBean.setMargin(warter.doubleValue());

        return warterBean;
    }

    /**
     *更新玩法水差
     * @Title: updateWater
     * @Description: TODO
     * @param @param oddsVoList
     * @param @param waterValue
     * @param @param overLoadTriggerItem    设定文件
     * @return void    返回类型
     * @throws
     */
    public void sendPlayWaterConfigApi(ThreewayOverLoadTriggerItem overLoadTriggerItem,RcsMatchMarketConfig config,
                                       String oddsType,List<MatchMarketPlaceConfig> list) {
        log.info("::{}::,sendPlayWaterConfigApi start overLoadTriggerItem={},config={},oddsType={},list={}",config.getMarketId(),
                JSONObject.toJSONString(overLoadTriggerItem),
                JSONObject.toJSONString(config),
                oddsType,
                JSONObject.toJSONString(list));

        TradeMarketUiConfigDTO apiConfig = new TradeMarketUiConfigDTO();
        apiConfig.setStandardMatchInfoId(overLoadTriggerItem.getMatchId());
        apiConfig.setStandardCategoryId(overLoadTriggerItem.getPlayId().longValue());
        apiConfig.setPlaceNum(overLoadTriggerItem.getPlaceNum());//用位置替换的数据
        apiConfig.setMarketType(config.getMatchType());
        if (ObjectUtils.isNotEmpty(config.getSubPlayId())){
            apiConfig.setChildStandardCategoryId(Long.parseLong(config.getSubPlayId()));
        }
        List<TradePlaceNumAutoDiffConfigItemDTO> itemDTOS = Lists.newArrayList();
        for (MatchMarketPlaceConfig matchMarketConfig : list){
            TradePlaceNumAutoDiffConfigItemDTO itemDTO = new TradePlaceNumAutoDiffConfigItemDTO();
            itemDTO.setOddType(oddsType);
            itemDTO.setPlaceNum(matchMarketConfig.getPlaceNum());
            itemDTO.setMarketCategoryId(overLoadTriggerItem.getPlayId().longValue());
            itemDTO.setDiffValue(matchMarketConfig.getPlaceMarketDiff().doubleValue());
            itemDTOS.add(itemDTO);
        }
        apiConfig.setPlaceNumDiffConfigs(itemDTOS);
        log.info("给融合API 参数:{},",JSONObject.toJSONString(apiConfig));
        DataRealtimeApiUtils.handleApi(apiConfig, new DataRealtimeApiUtils.ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketUiConfig(request);
            }
        });
        log.info("融合API已经处理:{},",JSONObject.toJSONString(apiConfig));
        // 其他球种推送水差就好了
        if (RcsConstant.OTHER_BALL.contains(config.getSportId().intValue())){
            return;
        }
        // 是否封盘处理
        Boolean isCloseMarket = Boolean.FALSE;
        Boolean isAccumulatePf = Boolean.FALSE;
        Integer linkedType = 13;
        // 1.查询盘口变化幅度实体
        RcsTournamentTemplatePlayMargain templatePlayMargin = rcsTournamentTemplatePlayMargainMapper.queryTournamentAdjustRangeByPlayId(config);
        // 推送盘口差
        if (ObjectUtils.isNotEmpty(config.getMarketHeadGap())
                && DataSourceTypeEnum.MANUAL.getValue() != config.getDataSource().intValue()){
            // 自动模式 A+ 模式，直接发送盘口差
            //  调融合api
            TradeMarketHeadGapConfigDTO dto = new TradeMarketHeadGapConfigDTO();
            dto.setMarketType(config.getMatchType());
            dto.setMarketHeadGap(config.getMarketHeadGap().doubleValue());
            dto.setStandardCategoryId(config.getPlayId());
            dto.setStandardMatchInfoId(config.getMatchId());
            dto.setMarketHeadGapInitial(templatePlayMargin.getMarketAdjustRange().doubleValue());
            if (StringUtils.isNotBlank(config.getSubPlayId())){
                dto.setChildStandardCategoryId(Long.parseLong(config.getSubPlayId()));
            }
            Response<String> response = DataRealtimeApiUtils.handleApi(dto, new DataRealtimeApiUtils.ApiCall() {
                @Override
                @Trace
                public <R> Response<R> callApi(Request request) {
                    Response rs = tradeMarketConfigApi.putTradeMarketHeadGapConfig(request);
                    return rs;
                }
            });
            rcsMatchPlayConfigMapper.insertOrUpdateMarketHeadGap(config);
            //redisClient.delete(String.format(RcsConstant.REDIS_MATCH_MARKET_HEAD_CONFIG,config.getMatchId(),config.getPlayId(),config.getSubPlayId()));
        }

        if (!ObjectUtils.isEmpty(templatePlayMargin)) {
            templatePlayMargin.setSubPlayId(config.getSubPlayId());
            // 跳赔超过限制封盘
            for (MatchMarketPlaceConfig placeConfig : list){
                // 跳赔超过限制封盘
                if (placeConfig.getPlaceMarketDiff().abs().compareTo(templatePlayMargin.getOddsMaxValue()) == NumberUtils.INTEGER_ONE){
                    isCloseMarket = Boolean.TRUE;
                    isAccumulatePf = Boolean.TRUE;
                    break;
                }
            }
            // 盘口调整值大于跳盘最大值触发防封
            if (ObjectUtils.isNotEmpty(config.getMarketAdjustRange()) &&
                    config.getMarketAdjustRange().abs().compareTo(templatePlayMargin.getMarketMaxValue()) == NumberUtils.INTEGER_ONE){
                linkedType = 25;
                isCloseMarket = Boolean.TRUE;
            }

        }
        MatchOddsConfig matchConfig = new MatchOddsConfig(overLoadTriggerItem.getMatchId().toString(), config.getMatchType());
        if (ObjectUtils.isNotEmpty(config.getMarketHeadGap())){
            matchConfig.getPlayConfigList().add(new MatchPlayConfig(config.getPlayId().toString(), config.getMarketType(), list,config.getMarketHeadGap().toString(),templatePlayMargin));
        }else {
            matchConfig.getPlayConfigList().add(new MatchPlayConfig(config.getPlayId().toString(), config.getMarketType(), list));
        }
        //需求2129  网球、乒乓球 M模式 投注水差超过限制不封盘
        if(isCloseMarket && this.tennisAndPingPongNewPlayNoRadioLimit(config.getSportId().longValue(),config.getPlayId()) && !MarketUtils.isAuto(config.getDataSource().intValue())){
            isCloseMarket = Boolean.FALSE;
        }

        // 超过限制封盘
        if (isCloseMarket){
            matchConfig.getPlayConfigList().get(NumberUtils.INTEGER_ZERO).setStatus(13);
        }
        String key = UuidUtils.generateUuid()+ "_risk";
        log.info("M::{}::,ManualOddsCalcService waterCalc key = {}",config.getMarketId(),key);
        matchConfig.setLinkId(key);
        producerSendMessageUtils.sendMessage("RCS_TRADE_MATCH_ODDS_CONFIG", config.getMatchId() + config.getPlayId().toString(),
                key , matchConfig);
        log.info("发送RCS_TRADE_MATCH_ODDS_CONFIG成功：{}",key);
        if (isCloseMarket && DataSourceTypeEnum.AUTOMATIC.getValue().intValue() == config.getDataSource()){
            betOverLimitClosePlay(config,linkedType,isAccumulatePf);
        }
    }
    /**
     * @param sportId 赛种id 网球5 乒乓球8
     * @param playId 玩法ID
     * @return 是否是网球和乒乓球下的玩法类型
     */
    public Boolean tennisAndPingPongNewPlayNoRadioLimit(Long sportId, Long playId){
        Boolean flag = Boolean.FALSE;
        if(SportIdEnum.isTennis(sportId) || SportIdEnum.isPingPong(sportId)){
            if (Tennis.isExistPlay(playId) || PingPong.isExistPlay(playId)){
                flag = Boolean.TRUE;
            }
        }
        return flag;
    }
    public void betOverLimitClosePlay(RcsMatchMarketConfig config,Integer linkedType,Boolean isAccumulatePf) {
        JSONObject obj = new JSONObject().fluentPut("tradeLevel", NumberUtils.INTEGER_TWO)
                .fluentPut("sportId", config.getSportId())
                .fluentPut("matchId", config.getMatchId())
                .fluentPut("playId", config.getPlayId())
                .fluentPut("placeNum", config.getMarketIndex())
                .fluentPut("status", NumberUtils.INTEGER_ONE.toString())
                .fluentPut("linkedType", linkedType)
                .fluentPut("isAccumulatePf", isAccumulatePf)
                .fluentPut("remark", "跳水跳盘超过限制封盘");
//        if (RcsConstant.BASKETBALL_X_MY_PLAYS.contains(config.getPlayId().intValue()) || SportIdEnum.isTennis(config.getSportId())){
        obj.put("subPlayId",config.getSubPlayId());
//        }
        String linkId = config.getMarketId() + "-" + System.currentTimeMillis();
        Request<JSONObject> request = new Request<>();
        request.setData(obj);
        request.setLinkId(linkId);
        request.setDataSourceTime(System.currentTimeMillis());
        log.info("::{}::,发送玩法封盘MQ消息linkId={}",config.getMarketId(),linkId);
        producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", config.getMatchId().toString(),
                linkId, request);
    }
    /**
     *更新水差
     * @Title: updateWater
     * @Description: TODO
     * @param @param oddsVoList
     * @param @param waterValue
     * @param @param overLoadTriggerItem    设定文件
     * @return void    返回类型
     * @throws
     */
    public void sendWaterConfigApi(List<TradeMarketAutoDiffConfigItemDTO> waterList,ThreewayOverLoadTriggerItem overLoadTriggerItem,RcsMatchMarketConfig config) {
        TradeMarketUiConfigDTO apiConfig = new TradeMarketUiConfigDTO();
        apiConfig.setStandardMatchInfoId(overLoadTriggerItem.getMatchId());
        apiConfig.setStandardCategoryId(overLoadTriggerItem.getPlayId().longValue());
        apiConfig.setPlaceNum(overLoadTriggerItem.getPlaceNum());//用位置替换的数据
        apiConfig.setMarketType(overLoadTriggerItem.getMatchType() == 2 ? 0 : 1);
        apiConfig.setDiffConfigs(waterList);
        if (StringUtils.isNotBlank(config.getSubPlayId())){
            apiConfig.setChildStandardCategoryId(Long.parseLong(config.getSubPlayId()));
        }
        DataRealtimeApiUtils.handleApi(apiConfig, new DataRealtimeApiUtils.ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketUiConfig(request);
            }
        });
//        sendWaterMq(waterValue,overLoadTriggerItem);
    }


    public void sendMargainConfigApi(List<MarketMarginDtlDTO> margainList,ThreewayOverLoadTriggerItem overLoadTriggerItem) {
        TradeMarketUiConfigDTO apiConfig = new TradeMarketUiConfigDTO();
        apiConfig.setStandardMatchInfoId(overLoadTriggerItem.getMatchId());
        apiConfig.setStandardCategoryId(overLoadTriggerItem.getPlayId().longValue());
        apiConfig.setPlaceNum(overLoadTriggerItem.getPlaceNum());//用位置替换的数据
        apiConfig.setMarketType(overLoadTriggerItem.getMatchType() == 2 ? 0 : 1);
        apiConfig.setMarketMarginDtlDTOList(margainList);

        DataRealtimeApiUtils.handleApi(apiConfig, new DataRealtimeApiUtils.ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketUiConfig(request);
            }
        });
//        sendWaterMq(waterValue,overLoadTriggerItem);
    }

    /**
     * 发送水差
     * @Title: sendWaterMq
     * @Description: TODO
     * @param @param waterValue
     * @param @param overLoadTriggerItem    设定文件
     * @return void    返回类型
     * @throws    *  水差跟着赔率走，顾不需要在发送接口
     */
//    @Deprecated
//    private void sendWaterMq(BigDecimal waterValue,ThreewayOverLoadTriggerItem overLoadTriggerItem) {
//        List<RcsMatchMarketConfig> rcsMatchMarketConfigs = new ArrayList<>();
//        RcsMatchMarketConfig matchMarketConfig = new RcsMatchMarketConfig();
//        matchMarketConfig.setMatchId(overLoadTriggerItem.getMatchId());
//        matchMarketConfig.setPlayId(overLoadTriggerItem.getPlayId().longValue());
//        matchMarketConfig.setMarketId(overLoadTriggerItem.getMarketId());
//        matchMarketConfig.setAwayAutoChangeRate(waterValue.toPlainString());
//        rcsMatchMarketConfigs.add(matchMarketConfig);
//        //发送MQ更新mongo数据
//        producerSendMessageUtils.sendMessage(MARKET_WATER_CONFIG_TOPIC, rcsMatchMarketConfigs);
//        log.info("saveAndUpdateMarketWaterHeadConfig盘口设置水差:{}", JsonFormatUtils.toJson(rcsMatchMarketConfigs));
//    }
//
//    private TradeMarketAutoDiffConfigItemDTO buildMarketAutoDiffConfigBean(Long playId,Long marketId,String oddType,Double diffVal ) {
//        TradeMarketAutoDiffConfigItemDTO tradeMarketAutoDiffConfigItemDTO = new TradeMarketAutoDiffConfigItemDTO();
//        tradeMarketAutoDiffConfigItemDTO.setMarketCategoryId(playId);
//        tradeMarketAutoDiffConfigItemDTO.setMarketId(marketId);
//        tradeMarketAutoDiffConfigItemDTO.setOddType(oddType);
//        tradeMarketAutoDiffConfigItemDTO.setDiffValue(diffVal);
//        return tradeMarketAutoDiffConfigItemDTO;
//    }

    public void updateMarginAndAutoChangeOdds(ThreewayOverLoadTriggerItem overLoadTriggerItem,RcsMatchMarketConfig config){
        rcsMatchMarketConfigMapper.insertOrUpdateMarketMarginConfig(overLoadTriggerItem);
        //redisClient.hashRemove(String.format(RcsConstant.REDIS_MATCH_MARKET_WATER,config.getMatchId()),config.getMarketId().toString());
        //------------开始------------
        //waldkir-redis集群-发送至trade进行delete
        String tag = config.getMatchId()+"_"+config.getMarketId().toString();
        String linkId = tag + "_" + System.currentTimeMillis();
        String key = String.format(RcsConstant.REDIS_MATCH_MARKET_WATER,config.getMatchId());
        RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(key, key, config.getMarketId().toString());
        log.info("::{}::,发送MQ消息linkId={}",config.getMarketId(), syncBean);
        producerSendMessageUtils.sendMessage("RCS_TRADE_REDIS_CACHE_SYNC", tag, linkId, syncBean);
        //------------结束------------
    }

    public void updatePlayWater(Long matchId , Integer playId , BigDecimal waterVal){
        RcsMatchPlayConfig config = new RcsMatchPlayConfig();
        config.setMatchId(matchId);
        config.setPlayId(playId);
        config.setAwayAutoChangeRate(waterVal);
        rcsMatchMarketConfigMapper.insertOrUpdatePlayMarginConfig(config);
    }


    /**
     * @Description
     *  1、1 主胜、2客胜、X平局
     * @Param [oddsVoList, playOptionsId]
     * @Author  Sean
     * @Date  10:34 2020/7/5
     * @return java.lang.String
     **/
    public String getThreePositon(List<StandardSportMarketOdds> oddsVoList,Long playOptionsId){
        log.info("根据投注项获取调配盘，赔率list：{},投注项id：{}",JSONObject.toJSONString(oddsVoList),playOptionsId.toString());
        String positon = "";
        for (StandardSportMarketOdds odds : oddsVoList){
            if (odds.getId().longValue() == playOptionsId.longValue()){
                if (BaseConstants.ODD_TYPE_1.equalsIgnoreCase(odds.getOddsType()) ||
                        BaseConstants.ODD_TYPE_YES.equalsIgnoreCase(odds.getOddsType())){
                    positon = BaseConstants.ODD_TYPE_HOME;
                    break;
                }
                if (BaseConstants.ODD_TYPE_2.equalsIgnoreCase(odds.getOddsType()) ||
                        BaseConstants.ODD_TYPE_NO.equalsIgnoreCase(odds.getOddsType())){
                    positon = BaseConstants.ODD_TYPE_AWAY;
                    break;
                }
                if (BaseConstants.ODD_TYPE_X.equalsIgnoreCase(odds.getOddsType()) ||
                        "None".equalsIgnoreCase(odds.getOddsType())){
                    positon = BaseConstants.ODD_TYPE_TIE;
                    break;
                }

            }
        }
        return positon;
    }


    public String getMYOddsValue(String odds){
        String oddsValue = new BigDecimal(odds).divide(BigDecimal.valueOf(BaseConstants.MULTIPLE_VALUE),2,BigDecimal.ROUND_DOWN).toPlainString();
        QueryWrapper<RcsOddsConvertMapping> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsOddsConvertMapping::getEurope, oddsValue);
        wrapper.lambda().select(RcsOddsConvertMapping::getMalaysia);
        RcsOddsConvertMapping mapping = rcsOddsConvertMappingMapper.selectOne(wrapper);
        if (!ObjectUtils.isEmpty(mapping)){
            oddsValue = mapping.getMalaysia();
        }else {
            log.warn("欧洲赔{}没有找到对应的马来赔率",oddsValue);
            oddsValue = "0";
        }
        return oddsValue;
    }

    /**
     * @Description
     *       if (原马来赔>0 && 原马来赔+自动水差>=1)：根据亚洲赔对照表中的“原马来赔+自动水差”查对应的新马来赔率；
     *        if (原马来赔>0 && 原马来赔+自动水差<=0.01)：新马来赔固定取0.01。
     *
     *        if (0>原马来赔>-1 && 原马来赔+自动水差<=-1)：根据马来赔对照表中的“原马来赔“查出香港赔，之后再用该香港赔+自动水差，再用该数值查对应的马来赔数值，即为新马来赔；
     *        if (0>原马来赔>-1 && 原马来赔+自动水差>=-0.01)：新马来赔固定取-0.01；
     * @Param [sourceOdds, changeOdds]
     * @Author  Sean
     * @Date  20:16 2020/7/10
     * @return java.math.BigDecimal
     **/
    public BigDecimal calculationOdds(BigDecimal sourceOdds,BigDecimal changeOdds){
        BigDecimal result = sourceOdds.add(changeOdds);
        if (sourceOdds.compareTo(BigDecimal.valueOf(0)) == 1){
            if (result.compareTo(new BigDecimal("1")) == 1){
                String oddsStr = result.add(new BigDecimal("1")).multiply(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).toPlainString();
                result = new BigDecimal(getMYOddsValue(oddsStr));
            }else if (result.compareTo(new BigDecimal("0.01")) == -1){
                result = new BigDecimal("0.01");
            }
        }else {
            if (result.compareTo(new BigDecimal("-1")) <= 0){
                RcsOddsConvertMapping mapping = rcsOddsConvertMappingMapper.queryHongKongOddsMappingByMY(sourceOdds.toPlainString());
                if ((!ObjectUtils.isEmpty(mapping)) && StringUtils.isNotBlank(mapping.getHongkong())){
                    result = new BigDecimal(mapping.getHongkong()).add(changeOdds);
                    QueryWrapper<RcsOddsConvertMapping> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(RcsOddsConvertMapping::getHongkong, result.toPlainString());
                    wrapper.lambda().select(RcsOddsConvertMapping::getMalaysia);
                    mapping = rcsOddsConvertMappingMapper.selectOne(wrapper);
                    if ((!ObjectUtils.isEmpty(mapping)) && StringUtils.isNotBlank(mapping.getMalaysia())){
                        result = new BigDecimal(mapping.getMalaysia());
                    }else {
                        log.warn("香港赔{}没有找到对应的马来赔",sourceOdds.toPlainString());
                    }
                }else {
                    log.warn("马来赔{}没有找到对应的香港赔",sourceOdds.toPlainString());
                }
            }else if(result.compareTo(new BigDecimal("-0.01")) == 1){
                result = new BigDecimal("-0.01");
            }
        }
        return result;
    }

    public BigDecimal getUpperOdds(TwowayDoubleOverLoadTriggerItem overLoadTriggerItem,BigDecimal downOdds){
        BigDecimal uppderOdds = downOdds.add(overLoadTriggerItem.getMargin());
        if (uppderOdds.doubleValue() >= 1){
            uppderOdds = BigDecimal.valueOf(NumberUtils.INTEGER_TWO).subtract(uppderOdds);
        }else {
            uppderOdds = uppderOdds.multiply(BigDecimal.valueOf(NumberUtils.LONG_MINUS_ONE));
        }
        return uppderOdds;
    }

    /**
     * 发送赔率到trade赔率队列
     * @Title: sendOddsConfigApi
     * @Description: TODO
     * @param @param oddsVoList    设定文件
     * @return void    返回类型
     * @throws
     */
    public void sendOddsConfigApi(ThreewayOverLoadTriggerItem item,List<StandardSportMarketOdds> oddsVoList , RcsMatchMarketConfig config) {
        log.info("发送赔率到trade赔率队列_sendOddsConfigApi:{}",JSONObject.toJSONString(item));
        RcsStandardMarketDTO dto = new RcsStandardMarketDTO();
        List<StandardMarketOddsDTO> oddsDTOList = Lists.newArrayList();
        for (StandardSportMarketOdds odd : oddsVoList){
            StandardMarketOddsDTO oddsDTO = new StandardMarketOddsDTO();
            oddsDTO.setOddsValue(odd.getOddsValue());
            if (ObjectUtils.isNotEmpty(config.getMargin())){
                oddsDTO.setMargin(config.getMargin().doubleValue());
            }
            oddsDTO.setOddsType(odd.getOddsType());
            oddsDTO.setOriginalOddsValue(odd.getOddsValue());
            oddsDTO.setActive(NumberUtils.INTEGER_ONE);
            oddsDTO.setAnchor(odd.getAnchor());
            oddsDTOList.add(oddsDTO);
            dto.setId(odd.getMarketId().toString());
            dto.setAddition1(odd.getAddition1());
            dto.setAddition2(odd.getAddition2());
            dto.setAddition3(odd.getAddition3());
        }
        dto.setOddsReplaceOrder(NumberUtils.INTEGER_ZERO);
        dto.setPlaceNum(config.getMarketIndex());
        dto.setMarketOddsList(oddsDTOList);
        dto.setMarketCategoryId(config.getPlayId());
        MatchOddsConfig matchConfig = new MatchOddsConfig(config.getMatchId().toString(), config.getMatchType());
        MatchPlayConfig playConfig = new MatchPlayConfig(config.getPlayId().toString(), config.getMarketType());
        playConfig.setMarketList(Lists.newArrayList(dto));
        // 是否封盘
        closeMarket(config,item,dto,playConfig);

        matchConfig.getPlayConfigList().add(playConfig);
        String key = UuidUtils.generateUuid()+ "_risk";
        log.info("::{}::,OddsPublicMethodApi sendOddsConfigApi key = {}",config.getMarketId(),key);
        producerSendMessageUtils.sendMessage(TRADE_ODDS_TOPIC,null,key,matchConfig);
        log.info("::{}::,多项玩法 SendOddsThroughApi key = {}",config.getMarketId(),key);
    }
    /**
     * @Description   //下一个账务日期封盘
     * @Param [matchId, dto]
     * @Author  sean
     * @Date   2021/1/22
     * @return void
     **/
    private void closeMarket(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item,RcsStandardMarketDTO dto,MatchPlayConfig playConfig) {
        String linkId = config.getMarketId() + "-" + System.currentTimeMillis();
        StandardMatchInfo bean = RcsCacheContant.MATCH_CACHE.get(config.getMatchId(), key -> {
            QueryWrapper<StandardMatchInfo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(StandardMatchInfo::getId,config.getMatchId());
            return standardMatchInfoMapper.selectOne(wrapper);
        });
        if(bean == null ) {
            return;
        }

        boolean isMatchNextAcctDay = isNextAcctDay(bean.getBeginTime());
        log.info("::{}::,linkId, isMatchNextAcctDay: {}", linkId, isMatchNextAcctDay);
        if (!isMatchNextAcctDay) {
            return;
        }

        // 超过次数封盘
        // 统计次数 1,17,2,4,18,19 玩法
        if (RcsConstant.RCS_COUNT_TIMES_PLAY.contains(config.getPlayId().intValue()) && SportIdEnum.isFootball(config.getSportId())){
            // 让分大小玩法,1274新增独赢玩法
            if (!oddsCalcuCommonService.isChangeTimesOver(item, config)) {
                log.info("::{}::,!isChangeTimesOver ", linkId);
                return;
            }else {
                config.setAutoBetStop("1");
            }
        }
        dto.setStatus(NumberUtils.INTEGER_ONE);
        dto.setPlaceNumStatus(NumberUtils.INTEGER_ONE);
        playConfig.setStatus(7);
    }

    /**
     * 获取玩法水差
     * @Title: getPlayWater
     * @Description: TODO
     * @param @param matchId
     * @param @param playId
     * @param @return    设定文件
     * @return BigDecimal    返回类型
     * @throws
     */
    public BigDecimal getPlayWater(Long matchId , Integer playId) {
        QueryWrapper<RcsMatchPlayConfig> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsMatchPlayConfig::getMatchId, matchId);
        wrapper.lambda().eq(RcsMatchPlayConfig::getPlayId, playId);
        wrapper.lambda().select(RcsMatchPlayConfig::getAwayAutoChangeRate);

        RcsMatchPlayConfig bean = rcsMatchPlayConfigMapper.selectOne(wrapper);
        if(bean == null || bean.getAwayAutoChangeRate() == null) {
            bean = new RcsMatchPlayConfig();
            bean.setAwayAutoChangeRate(BigDecimal.ZERO);
        }

        return bean.getAwayAutoChangeRate();
    }


    public List<MatchMarketPlaceConfig> queryPlaceWaterConfigList(RcsMatchMarketConfig config, BigDecimal changeOdds) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("matchId", config.getMatchId());
        map.put("playId", config.getPlayId());
        map.put("changeOdds", changeOdds.toString());
        List<MatchMarketPlaceConfig> list = Lists.newArrayList();
        RcsTournamentTemplatePlayMargain template = rcsTournamentTemplatePlayMargainMapper.queryTournamentAdjustRangeByPlayId(config);
        if (ObjectUtils.isNotEmpty(template) && ObjectUtils.isNotEmpty(template.getMarketCount())){
            map.put("marketIndex", template.getMarketCount());
        }

        if (RcsConstant.BASKETBALL_X_MY_PLAYS.contains(config.getPlayId().intValue()) ||
                RcsConstant.BASKETBALL_X_EU_PLAYS.contains(config.getPlayId().intValue()) ||
                !(SportIdEnum.isFootball(config.getSportId()) || SportIdEnum.isBasketball(config.getSportId()))){
            map.put("subPlayId", config.getSubPlayId());
//            rcsMatchPlayConfigMapper.updateMatchPlayPlaceSubList(map);
            list = rcsMatchPlayConfigMapper.queryPlaceWaterConfigSubList(map);
            RcsMatchMarketConfig conf = JSONObject.parseObject(JSONObject.toJSONString(config),RcsMatchMarketConfig.class);
            if (CollectionUtils.isEmpty(list) || list.size() < template.getMarketCount()){
                List<Integer> indexs =  Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(list)){
                    indexs = list.stream().map(e -> e.getPlaceNum()).collect(Collectors.toList());
                }
                for (int i=1;i<= template.getMarketCount();i++){
                    if (!indexs.contains(i)){
                        conf.setMarketIndex(i);
                        rcsMatchMarketConfigMapper.initMarketConfig(conf);
                    }
                }
            }
            rcsMatchPlayConfigMapper.updateMatchPlayPlaceSubList(map);
            //redisClient.delete(String.format(RcsConstant.REDIS_MATCH_MARKET_SUB_CONFIG,config.getMatchId(),config.getPlayId(),config.getSubPlayId()));
            //------------开始------------
            //waldkir-redis集群-发送至trade进行delete
            String tag = config.getMatchId()+"_"+config.getPlayId()+"_"+config.getSubPlayId();
            String linkId = tag + "_" + System.currentTimeMillis();
            String key = String.format(RcsConstant.REDIS_MATCH_MARKET_SUB_CONFIG,config.getMatchId(),config.getPlayId(),config.getSubPlayId());
            RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(RcsConstant.REDIS_MATCH_MARKET_SUB_CONFIG, key);
            log.info("::{}::,发送MQ消息linkId={}",config.getMarketId(),syncBean);
            producerSendMessageUtils.sendMessage("RCS_TRADE_REDIS_CACHE_SYNC", tag, linkId, syncBean);
            //------------结束------------
            list = rcsMatchPlayConfigMapper.queryPlaceWaterConfigSubList(map);
        }
        else {
            rcsMatchPlayConfigMapper.updateMatchPlayPlaceList(map);
            list = rcsMatchPlayConfigMapper.queryPlaceWaterConfigList(map);
            //redisClient.delete(String.format(RcsConstant.REDIS_MATCH_MARKET_CONFIG,config.getMatchId(),config.getPlayId()));
            //------------开始------------
            //waldkir-redis集群-发送至trade进行delete
            String tag = config.getMatchId()+"_"+config.getPlayId();
            String linkId = tag + "_" + System.currentTimeMillis();
            String key = String.format(RcsConstant.REDIS_MATCH_MARKET_CONFIG,config.getMatchId(),config.getPlayId());
            RedisCacheSyncBean syncBean = RedisCacheSyncBean.build(RcsConstant.REDIS_MATCH_MARKET_CONFIG, key);
            log.info("::{}::,发送MQ消息linkId={}",config.getMarketId(),syncBean);
            producerSendMessageUtils.sendMessage("RCS_TRADE_REDIS_CACHE_SYNC", tag, linkId, syncBean);
            //------------结束------------
        }
        log.info("::{}::,list={},changeOdds={}",config.getMarketId(),JSONObject.toJSONString(list),changeOdds);
        return list;
    }

    private Boolean isNextAcctDay(Long beginTime) {
        try {

            if (DateUtils.getHourByDate(new Date(beginTime)) >= 12){
                beginTime += 24 * 60 * 60 * 1000L;
            }
            Integer beginDate = Integer.parseInt(DateUtils.parseDate(beginTime, DateUtils.YYYYMMDD));

            Long now = System.currentTimeMillis();
            if (DateUtils.getHourByDate(new Date(now)) >= 12 ){
                now += 24 * 60 * 60 * 1000L;
            }
            Integer nextDate = Integer.parseInt(DateUtils.parseDate(now, DateUtils.YYYYMMDD));
            if(beginDate > nextDate) {
                return true;
            }
        }catch (Exception e) {
            log.error("处理时间对比判断错误：{}",e.getMessage(),e);
        }

        return false;
    }

    public void sendMsgAndCloseStatus(ThreewayOverLoadTriggerItem twoWayDouble,RcsMatchMarketConfig config) {
        String linkId = config.getMarketId() + "-" + System.currentTimeMillis();
        log.info("::{}::,linkId", linkId);
        log.info("::{}::,twoWayDouble: {}", linkId,JSONObject.toJSONString(twoWayDouble));
        log.info("::{}::,config: {}", linkId, JSONObject.toJSONString(config));
        try {
            StandardMatchInfo bean = RcsCacheContant.MATCH_CACHE.get(twoWayDouble.getMatchId(), key -> {
                QueryWrapper<StandardMatchInfo> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(StandardMatchInfo::getId, twoWayDouble.getMatchId());
                return standardMatchInfoMapper.selectOne(wrapper);
            });
            log.info("::{}::, StandardMatchInfo: {}", linkId, JSONObject.toJSONString(bean));
            if (bean == null) {
                return;
            }
            boolean isMatchNextAcctDay = isNextAcctDay(bean.getBeginTime());
            log.info("::{}::,linkId, isMatchNextAcctDay: {}", linkId, isMatchNextAcctDay);
            if (!isMatchNextAcctDay) {
                return;
            }
            log.info("::{}::,StandardMatchInfo: {}", linkId, JSONObject.toJSONString(bean));
            // 统计次数 2,4,18,19 玩法 ,M模式已经统计过了，这里只统计A模式
            if (RcsConstant.RCS_COUNT_TIMES_PLAY.contains(config.getPlayId().intValue())) {
                // M模式之前已统计，这里直接判断
                if (DataSourceTypeEnum.AUTOMATIC.getValue().intValue() != config.getDataSource()) {
                    if (StringUtils.isBlank(config.getAutoBetStop())
                            || !"1".equalsIgnoreCase(config.getAutoBetStop())) {
                        log.info("::{}::,getAutoBetStop: {}", linkId, config.getAutoBetStop());
                        return;
                    }
                } else if (DataSourceTypeEnum.AUTOMATIC.getValue().intValue() == config.getDataSource()) {
                    // 让分大小玩法,1274新增独赢玩法
                    if (!oddsCalcuCommonService.isChangeTimesOver(twoWayDouble, config)) {
                        log.info("::{}::,getAutoBetStop: {}", linkId, "isChangeTimesOver");
                        return;
                    }
                }
            }
            JSONObject obj = oddsCalcuCommonService.getSealMQJson(twoWayDouble, config);

            log.info("::{}::,发送MQ消息MarketId = {}", linkId, config.getMarketId());
            Request<JSONObject> request = new Request<>();
            request.setData(obj);
            request.setLinkId(linkId);
            request.setDataSourceTime(System.currentTimeMillis());
            log.info("::{}::,sendMessage request = {}", linkId, JSONObject.toJSONString(request));
            if (DataSourceTypeEnum.AUTOMATIC.getValue() == config.getDataSource().longValue() || ObjectUtils.isNotEmpty(obj.get("isClose"))) {
                producerSendMessageUtils.sendMessage("RCS_TRADE_UPDATE_MARKET_STATUS", twoWayDouble.getMatchId().toString(),
                        linkId, request);
            }

            String msg = getMsg(bean.getId(), Long.parseLong(bean.getMatchManageId()), twoWayDouble.getPlayId().longValue(), config.getSportId());
            log.info("::{}::,sendCloseMarketMessage msg = {}", linkId, JSONObject.toJSONString(request));
            sendCloseMarketMessage(config, msg, linkId);
        }catch (Exception e){
            log.error("::{}::,sendMsgAndCloseStatus linkId={}",config.getMarketId(),linkId);
        }
    }

    /**
     * 组合消息
     * @param matchId 赛事id
     * @param matchManageId 赛事管理id
     * @param playId 玩法id
     * @param sportId 赛种id
     * @return 组合消息
     */
    public String getMsg(Long matchId, Long matchManageId, Long playId, Integer sportId) {
        HashMap<String, String> hashMap = new HashMap();
        for (LanguageTypeDataEnum languageTypeDataEnum : LanguageTypeDataEnum.values()) {
            StringBuilder stringBuilder = new StringBuilder();
            String type = languageTypeDataEnum.getType();
            stringBuilder.append(languageUtils.getHomeNameAndAwayName(matchId, type)).append("(").append(matchManageId).append("):");
            stringBuilder.append(languageUtils.getPlayName(playId, type, sportId));
            stringBuilder.append(languageUtils.getSealing(type));
            hashMap.put(type, stringBuilder.toString());
        }
        return JSONObject.toJSONString(hashMap);
    }

    /**
     * @Description   //发送封盘消息
     * @Param []
     * @Author  sean
     * @Date   2021/1/16
     * @return void
     **/
    public void sendCloseMarketMessage(RcsMatchMarketConfig config,String msg,String linkId) {
        log.info("sendCloseMarketMessage:{}",JSONObject.toJSONString(config));
        RcsBroadCastDTO cast = new RcsBroadCastDTO();
        cast.setSportId(config.getSportId().longValue());
        if (config.getMatchType()==1){
            cast.setMatchType(0);
        }else if (config.getMatchType()==0){
            cast.setMatchType(1);
        }else {
            cast.setMatchType(config.getMatchType());
        }
        RcsBroadCast broad = new RcsBroadCast();
        broad.setMsgType(3);
        broad.setContent(msg);
        broad.setExtendsField(config.getMatchId().toString());
        broad.setExtendsField1(NumberUtils.INTEGER_ZERO.toString());
        broad.setStatus(NumberUtils.INTEGER_ONE);
        broad.setMsgId(linkId);
        cast.setRcsBroadCast(broad);
        log.info("::{}::,发送MQ封盘消息risk_msg_alarm, cast={}",linkId, JSONObject.toJSONString(cast));
        producerSendMessageUtils.sendMessage("risk_msg_alarm",null, linkId, cast);
        log.info("{}::linkId_发送到risk_msg_alarm",linkId);
    }
    /**
     * @Description   //位置水差
     * @Param [waterList, item]
     * @Author  sean
     * @Date   2021/1/29
     * @return void
     **/
    public void sendPlaceWaterConfigApi(List<TradeMarketAutoDiffConfigItemDTO> waterList, ThreewayOverLoadTriggerItem item) {
        TradeMarketUiConfigDTO apiConfig = new TradeMarketUiConfigDTO();
        apiConfig.setStandardMatchInfoId(item.getMatchId());
        apiConfig.setStandardCategoryId(item.getPlayId().longValue());
        apiConfig.setPlaceNum(item.getPlaceNum());//用位置替换的数据
        apiConfig.setMarketType(item.getMatchType() == 2 ? 0 : 1);
        List<TradePlaceNumAutoDiffConfigItemDTO> placeNumDiffConfigs = Lists.newArrayList();
        for (TradeMarketAutoDiffConfigItemDTO itemDTO : waterList){
            TradePlaceNumAutoDiffConfigItemDTO place = new TradePlaceNumAutoDiffConfigItemDTO();
            place.setOddType(itemDTO.getOddType());
            place.setMarketCategoryId(itemDTO.getMarketCategoryId());
            place.setPlaceNum(item.getPlaceNum());
            place.setDiffValue(itemDTO.getDiffValue());
            placeNumDiffConfigs.add(place);
        }
        apiConfig.setPlaceNumDiffConfigs(placeNumDiffConfigs);

        DataRealtimeApiUtils.handleApi(apiConfig, new DataRealtimeApiUtils.ApiCall() {
            @Override
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketUiConfig(request);
            }
        });
    }
    /**
     * @Description   //获取没有描点的投注项
     * @Param [marketOddsList]
     * @Author  sean
     * @Date   2021/5/4
     * @return com.panda.merge.dto.StandardMarketOddsDTO
     **/
    public StandardSportMarketOdds getNotAnchor(List<StandardSportMarketOdds> odds,RcsMatchMarketConfig config) {
        log.info("::{}::,获取没有描点的投注项 -----> getNotAnchor = {}", config.getMarketId(),JSONObject.toJSONString(odds));
        StandardSportMarketOdds dto =  odds.get(NumberUtils.INTEGER_ZERO);
        // 两项盘 赔率小的是描点，相同赔率主队是描点
        if (odds.size() == 2){
            StandardSportMarketOdds dto1 = odds.get(NumberUtils.INTEGER_ZERO);
            StandardSportMarketOdds dto2 = odds.get(NumberUtils.INTEGER_ONE);
            if (dto1.getOddsValue() > dto2.getOddsValue()){
                dto = dto1;
            }
            if (dto1.getOddsValue() < dto2.getOddsValue()){
                dto = dto2;
            }
            if (dto1.getOddsValue().intValue() == dto2.getOddsValue()){
                if (dto1.getOddsType().equalsIgnoreCase(getOddsType(odds,config))){
                    dto =  dto2;
                }else {
                    dto = dto1;
                }
            }
        }else {
            // 三项盘通过字段区分
            for (StandardSportMarketOdds oddsDTO : odds){
                if (ObjectUtils.isEmpty(oddsDTO.getAnchor()) || oddsDTO.getAnchor().intValue() == 0){
                    dto = oddsDTO;
                }
            }
        }
        log.info("::{}::,获取没有描点的投注项 -----> end getNotAnchor = {}",config.getMarketId(),JSONObject.toJSONString(dto));
        return dto;
    }
    /**
     * @Description   //margin 水差等811需求参数
     * @Param [gapConfigDTO]
     * @Author  sean
     * @Date   2021/5/2
     * @return void
     **/
    public void putTradeMarginGap(TradeMarketMarginGapConfigDTO gapConfigDTO) {
        DataRealtimeApiUtils.handleApi(gapConfigDTO, new DataRealtimeApiUtils.ApiCall() {
            @Override
            @Trace
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketMarginGapConfig(request);
            }
        });
    }

    public void putTradeMarginProbabilityGap(TradeMarketMarginGapConfigDTO gapConfigDTO) {
        DataRealtimeApiUtils.handleApi(gapConfigDTO, new DataRealtimeApiUtils.ApiCall() {
            @Override
            @Trace
            public <R> Response<R> callApi(Request request) {
                return tradeMarketConfigApi.putTradeMarketMarginGapConfig(request);
            }
        });
    }
    /**
     * @Description   //是否均分
     * @Param [config]
     * @Author  sean
     * @Date   2021/5/11
     * @return java.lang.Integer
     **/
    public Integer getLinkeAgeMode(RcsMatchMarketConfig config,List<StandardSportMarketOdds> oddsVoList) {
        // 默认M模式不联动，A模式联动
        Integer linkageMode = config.getLinkageMode();
        if (ObjectUtils.isEmpty(config.getLinkageMode())){
            if (MarketUtils.isAuto(config.getDataSource().intValue())){
                linkageMode = NumberUtils.INTEGER_ZERO;
            }else {
                linkageMode = NumberUtils.INTEGER_ONE;
            }
        }
        if (2 == oddsVoList.size()){
            linkageMode = NumberUtils.INTEGER_ONE;
        }
        if (oddsVoList.size() > 3 ){
            linkageMode = NumberUtils.INTEGER_ZERO;
        }
        return linkageMode;
    }
    /**
     * @Description   //获取投注项概率差
     * @Param [oddsType, probabilitys]
     * @Author  sean
     * @Date   2021/5/15
     * @return java.math.BigDecimal
     **/
    public BigDecimal getOddsTypeProbabilitys(String oddsType, List<RcsMatchMarketProbabilityConfig> probabilitys) {
        BigDecimal probability = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(probabilitys)){
            for (RcsMatchMarketProbabilityConfig probabilityConfig : probabilitys){
                if (oddsType.equalsIgnoreCase(probabilityConfig.getOddsType())){
                    probability = probabilityConfig.getProbability();
                }
            }
        }
        return probability;
    }
    /**
     * @Description   //获取投注项概率差
     * @Param [oddsType, probabilitys]
     * @Author  sean
     * @Date   2021/5/15
     * @return java.math.BigDecimal
     **/
    public RcsMatchMarketProbabilityConfig getChampionProbabilitys(String oddsType, List<RcsMatchMarketProbabilityConfig> probabilitys) {
        RcsMatchMarketProbabilityConfig probability = null;
        if (CollectionUtils.isNotEmpty(probabilitys)){
            for (RcsMatchMarketProbabilityConfig probabilityConfig : probabilitys){
                if (oddsType.equalsIgnoreCase(probabilityConfig.getOddsType())){
                    probability = probabilityConfig;
                    probability.setOddsChangeTimes(probability.getOddsChangeTimes() +1);
                }
            }
        }
        if (ObjectUtils.isEmpty(probability)){
            probability = new RcsMatchMarketProbabilityConfig(BigDecimal.ZERO,NumberUtils.INTEGER_ONE);
        }
        return probability;
    }
    /**
     * @Description   //需要保存的概率数据
     * @Param [config, ps, dtlDTO, probability]
     * @Author  sean
     * @Date   2021/5/15
     * @return void
     **/
    public RcsMatchMarketProbabilityConfig buildProbability(RcsMatchMarketConfig config, String oddsType, BigDecimal probability) {
        RcsMatchMarketProbabilityConfig probabilityConfig = new RcsMatchMarketProbabilityConfig(config.getMatchId(),config.getPlayId(),config.getMarketId());
        probabilityConfig.setOddsType(oddsType);
        probabilityConfig.setProbability(probability);
        return probabilityConfig;
    }
    public void setDefaultAnchor(List<StandardSportMarketOdds> oddsVoList) {
        if (CollectionUtils.isNotEmpty(oddsVoList)){
            if (oddsVoList.size() !=3){
                return;
            }
            oddsVoList = oddsVoList.stream().sorted(Comparator.comparing(StandardSportMarketOdds::getOddsValue)).collect(Collectors.toList());
            Integer count = 0;
            for (StandardSportMarketOdds odds : oddsVoList){
                count += ObjectUtils.isEmpty(odds.getAnchor()) ? 0 :odds.getAnchor();
            }
            if (count != 2){
                for (int i=0;i<oddsVoList.size();i++){
                    oddsVoList.get(i).setAnchor(1);
                    if (i==2 ){
                        if (!!(oddsVoList.get(i).getOddsType().equalsIgnoreCase(BaseConstants.ODD_TYPE_X))){
                            oddsVoList.get(i).setAnchor(0);
                        }else {
                            oddsVoList.get(i-1).setAnchor(0);
                        }
                    }
                }
            }
        }
        log.info("::{}::,设置自动描点={}",oddsVoList.get(0).getMarketId(),JSONObject.toJSONString(oddsVoList));
    }

    public void sendChampionCloseMarketMessage(RcsMatchMarketConfig config, ThreewayOverLoadTriggerItem item) {
        RcsStandardOutrightMatchInfo bean = RcsCacheContant.RCS_MATCH_CHAMPION.get(item.getMatchId(), key -> {
            QueryWrapper<RcsStandardOutrightMatchInfo> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsStandardOutrightMatchInfo::getId,item.getMatchId());
            return rcsStandardOutrightMatchInfoMapper.selectOne(wrapper);
        });
        config.setCloseMsg(config.getCloseMsg().replace("matchManageId",bean.getStandardOutrightManagerId()));
        sendCloseMarketMessage(config,config.getCloseMsg(), UuidUtils.generateUuid());
    }

    /**
     * @Description   //封装国际化
     * @Param [market]
     * @Author  sean
     * @Date   2021/6/25
     * @return void
     **/
    public void setI18nName(StandardMarketDTO market) {
        List<Map<String,String>> map = standardSportMarketMapper.queryMarketI18nNames(market.getId());
        List<I18nItemDTO> mI18nNames = Lists.newArrayList();
        for (Map<String,String> m :map){
            for (StandardMarketOddsDTO dto : market.getMarketOddsList()){
                if ((!org.springframework.util.ObjectUtils.isEmpty(m.get("i18n_names"))) && (!m.get("i18n_names").equalsIgnoreCase("null"))){
                    List<I18nItemDTO> i18nNames = JSONArray.parseArray(m.get("i18n_names"),I18nItemDTO.class);
                    dto.setI18nNames(i18nNames);
                }
            }
            if (org.apache.commons.collections.CollectionUtils.isEmpty(mI18nNames)){
                mI18nNames = JSONArray.parseArray(m.get("mNames"),I18nItemDTO.class);
            }
        }
        market.setI18nNames(mI18nNames);
    }

    public RcsTournamentTemplatePlayMargain getRcsTournamentTemplateConfig(RcsMatchMarketConfig config) {
        if (NumberUtils.INTEGER_TWO.intValue() == config.getMatchType()) {
            config.setMatchType(NumberUtils.INTEGER_ZERO);
        }
        // 1.查询变化幅度
        RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargin = rcsTournamentTemplatePlayMargainMapper.queryTournamentAdjustRangeByPlayId(config);
        if (org.springframework.util.ObjectUtils.isEmpty(rcsTournamentTemplatePlayMargin) ||
                org.springframework.util.ObjectUtils.isEmpty(rcsTournamentTemplatePlayMargin.getMarketAdjustRange())) {
            log.error("没有找到联赛配置={}", JSONObject.toJSONString(config));
            throw new RcsServiceException("没有找到联赛配置");
        }
        return rcsTournamentTemplatePlayMargin;
    }

    /**
     * @Description   //通知融合封投注项
     * @Param [gapConfigDTO]
     * @Author  sean
     * @Date   2021/7/17
     * @return void
     **/
    public void closeOddType(OutrightTradeProbabilityConfigDTO gapConfigDTO) {
        OutrightTradeOddsConfigDTO oddsConfigDTO = JSONObject.parseObject(JSONObject.toJSONString(gapConfigDTO),OutrightTradeOddsConfigDTO.class);
        oddsConfigDTO.setOddsStatus(NumberUtils.INTEGER_ZERO);
        // todo 投注项封盘
        DataRealtimeApiUtils.handleApi(oddsConfigDTO, new DataRealtimeApiUtils.ApiCall() {
            @Override
            @Trace
            public <R> Response<R> callApi(Request request) {
                return iOutrightTradeConfigApi.putOutrightTradeOddsConfig(request);
            }
        });
    }
    public List<StandardSportMarketOdds> queryOddsVoList(RcsMatchMarketConfig config){
        List<StandardSportMarketOdds> oddsVoList = org.apache.curator.shaded.com.google.common.collect.Lists.newArrayList();
        String result = redisClient.get(String.format(RcsConstant.REDIS_MATCH_MARKET_ODDS_NEW,config.getPlayId().toString(),config.getMatchId()));
        List<RcsStandardMarketDTO> playOddsList = Lists.newArrayList();
        if (StringUtils.isNotBlank(result)) {
            List<StandardMarketMessage> markets = JSONArray.parseArray(result, StandardMarketMessage.class);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(markets)) {
                for (StandardMarketMessage market : markets) {
                    if (market.getThirdMarketSourceStatus() >= 2) {
                        continue;
                    }
                    market.setOddsMetric(market.getPlaceNum());
                    List<StandardMarketOddsMessage> marketOddsList = market.getMarketOddsList();
                    if (!org.springframework.util.CollectionUtils.isEmpty(marketOddsList)) {
                        marketOddsList.forEach(e -> {
                            e.setOddsValue(e.getPaOddsValue());
                            e.setNameExpressionValue(NameExpressionValueUtils.getNameExpressionValue(config.getPlayId().intValue(), e.getOddsType(), market.getAddition1()));
                        });
                    }
                    RcsStandardMarketDTO m = JSONObject.parseObject(JSONObject.toJSONString(market), RcsStandardMarketDTO.class);
                    m.setChildStandardCategoryId(market.getChildMarketCategoryId());
                    playOddsList.add(m);
                }
            }
            for (RcsStandardMarketDTO marketDTO : playOddsList) {
                if (marketDTO.getId().equalsIgnoreCase(config.getMarketId().toString()) && CollectionUtils.isNotEmpty(marketDTO.getMarketOddsList())) {
                    for (StandardMarketOddsDTO oddsDTO : marketDTO.getMarketOddsList()) {
                        StandardSportMarketOdds odds = JSONObject.parseObject(JSONObject.toJSONString(oddsDTO), StandardSportMarketOdds.class);
                        odds.setAddition1(marketDTO.getAddition1());
                        odds.setOrderOdds(marketDTO.getThirdMarketSourceStatus());
                        odds.setOriginalOddsValue(ObjectUtils.isEmpty(oddsDTO.getOriginalOddsValue()) ? oddsDTO.getOddsValue() : oddsDTO.getOriginalOddsValue());
                    }

                }
            }
        }
        if (CollectionUtils.isEmpty(oddsVoList)){
            oddsVoList = sportMarketOddsMapper.queryMarketOddsByMarket(config.getMarketId());
        }
        return oddsVoList;
    }


    /**
     * 累封防封修改玩法盘口信息
     *
     * @param config
     * @param placeNumStatusDisplay
     */
    public void updateMarketCategory(RcsMatchMarketConfig config,Integer placeNumStatusDisplay) {
        log.info("累封防封判断参数:{},placeNumStatusDisplay:{}", JSONObject.toJSONString(config),placeNumStatusDisplay);
        try{
            Long matchId = config.getMatchId();
            Long playId = config.getPlayId();
            Integer placeNum = config.getMarketIndex();
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(String.valueOf(matchId)).and("id").is(playId));
            query.addCriteria(Criteria.where("matchMarketVoList.placeNum").is(placeNum));
            MarketCategory category = mongoTemplate.findOne(query, MarketCategory.class);
            List<MatchMarketLiveOddsVo.MatchMarketVo> matchMarketVoList = category.getMatchMarketVoList();
            log.info("累封防封判断获取matchMarketVoList:{}",JSONObject.toJSONString(matchMarketVoList));
            Integer resultStatus = placeNumStatusDisplay;
            if(!CollectionUtils.isEmpty(matchMarketVoList)){
                for (MatchMarketLiveOddsVo.MatchMarketVo model : matchMarketVoList) {
                    if (model.getPlaceNum() != null && placeNum.intValue() == model.getPlaceNum().intValue()) {
                        if(model.getPlaceNumStatusDisplay() != null) {
                            //取大的值
                            resultStatus = placeNumStatusDisplay > model.getPlaceNumStatusDisplay() ? placeNumStatusDisplay : model.getPlaceNumStatusDisplay();
                        }
                    }
                }
                Update update = new Update();
                update.set("matchMarketVoList.$.placeNumStatusDisplay", resultStatus);
                log.info("累封防封判断更新matchId:{},placeNum:{},resultStatus:{}",matchId,placeNum,resultStatus);
                mongoTemplate.updateFirst(query, update, MarketCategory.class);
            }
        }catch (Exception e){
            log.error("::{}::{}::累封防封触发失败:{}", config.getMatchId(),config.getPlayId(), e);
        }
    }

}
