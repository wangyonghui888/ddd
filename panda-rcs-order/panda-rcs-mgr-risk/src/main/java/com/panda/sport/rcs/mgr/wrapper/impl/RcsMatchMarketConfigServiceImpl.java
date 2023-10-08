package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigSubMapper;
import com.panda.sport.rcs.mapper.RcsMatchMarketProbabilityConfigMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.champion.RcsChampionRiskConfigMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.mgr.mq.bean.HideOrderRatioVo;
import com.panda.sport.rcs.mgr.mq.impl.trigger.TriggerChangeImpl;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsTournamentOperateMarketService;
import com.panda.sport.rcs.mgr.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.pojo.RcsChampionRiskConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketProbabilityConfig;
import com.panda.sport.rcs.vo.odds.MatchMarketPlaceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 赛事设置表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-24
 */
@Service
@Slf4j
public class RcsMatchMarketConfigServiceImpl extends ServiceImpl<RcsMatchMarketConfigMapper, RcsMatchMarketConfig> implements IRcsMatchMarketConfigService {
    @Autowired
    RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
//    @Autowired
//    RcsTournamentOperateMarketService rcsTournamentOperateMarketService;
    @Autowired
    StandardSportMarketService standardSportMarketService;
    @Autowired
    IRcsMatchMarketConfigService matchMarketConfigService;
    @Autowired
    private RcsTournamentTemplatePlayMargainMapper rcsTournamentTemplatePlayMargainMapper;
    @Autowired
    private RcsChampionRiskConfigMapper rcsChampionRiskConfigMapper;
    @Autowired
    RedisClient redisClient;
    private String RCS_CODE_KEY = "magin";
    private static String TIMEOUT = "timeout";

    @Override
    public RcsMatchMarketConfig queryMaxBetAmount(RcsMatchMarketConfig config) {
        log.info("queryMaxBetAmount_config:{}",JSONObject.toJSONString(config));
        if(null==config){
            return  null;
        }
        if(config.getMatchId()!=null){
            if (config.getMarketId() == null || config.getPlayId() == null) {
                log.error("::{}::,单个赛事操盘，赛事盘口ID和玩法ID均不能为空！config:{}",config.getMarketId(),config);
                return null;
            }
        } else {
            if (config.getTournamentId() == null || config.getPlayId() == null) {
                log.error("::{}::,联赛操盘设置，联赛ID和玩法ID均不能为空！config:{}",config.getMarketId(),config);
                return null;
            }
        }
        // 多项盘
        if (3 == config.getMatchType()){
            QueryWrapper<RcsChampionRiskConfig> championRiskConfigQueryWrapper = new QueryWrapper<>();
            championRiskConfigQueryWrapper.lambda().eq(RcsChampionRiskConfig ::getMarketId,config.getMarketId());
            RcsChampionRiskConfig marketConfig = rcsChampionRiskConfigMapper.selectOne(championRiskConfigQueryWrapper);
            if (ObjectUtils.isEmpty(marketConfig)){
                marketConfig = new RcsChampionRiskConfig(BigDecimal.valueOf(100000),
                        BigDecimal.valueOf(500000),
                        new BigDecimal("0.5"),
                        new BigDecimal("1"),
                        new BigDecimal("1.5"));
            }
            RcsMatchMarketProbabilityConfig probabilityConfig = rcsMatchMarketConfigMapper.getOddsTypeProbability(config);
            if (ObjectUtils.isEmpty(probabilityConfig) || probabilityConfig.getOddsChangeTimes() == 0 || probabilityConfig.getOddsChangeTimes() >= 3){
                config.setHomeLevelFirstOddsRate(marketConfig.getOneProbability());
            }else if (probabilityConfig.getOddsChangeTimes() == 1){
                config.setHomeLevelFirstOddsRate(marketConfig.getTwoProbability());
            }else if (probabilityConfig.getOddsChangeTimes() >= 2){
                config.setHomeLevelFirstOddsRate(marketConfig.getThreeProbability());
            }
            config.setHomeLevelFirstMaxAmount(marketConfig.getOneOddsAmount().longValue());
            config.setHomeLevelSecondMaxAmount(marketConfig.getOneTotalOddsAmount().longValue());
            config.setMarketType(MarketKindEnum.Europe.getValue());
            config.setMarketIndex(config.getMarketIndex());
            return config;
        }
        // 其他临时球种
        if (RcsConstant.OTHER_BALL.contains(config.getSportId().intValue())){

            if (SportIdEnum.getMyPlaysBySportId(config.getSportId().longValue()).contains(config.getPlayId().intValue())){
                config.setMarketType(MarketKindEnum.Malaysia.getValue());
            }else {
                config.setMarketType(MarketKindEnum.Europe.getValue());
            }
            RcsMatchMarketConfig marketConfig = rcsTournamentTemplatePlayMargainMapper.rcsTournamentConfig(config);
            if (!ObjectUtils.isEmpty(marketConfig)){
                marketConfig.setMarketType(config.getMarketType());
            }
            return marketConfig;
        }
        // 多项盘
        if (SportIdEnum.isFootball(config.getSportId().intValue()) &&
                (RcsConstant.FOOTBALL_MOST_PLAYS.contains(config.getPlayId().intValue()) ||
                RcsConstant.FOOTBALL_X_MOST_PLAYS.contains(config.getPlayId().intValue()))){
            RcsMatchMarketConfig marketConfig = rcsMatchMarketConfigMapper.queryMostOddsTypeMarketConfig(config);
            if (ObjectUtils.isEmpty(marketConfig)){
                return marketConfig;
            }
            config.setHomeLevelFirstMaxAmount(marketConfig.getHomeLevelFirstMaxAmount());
            config.setHomeLevelSecondMaxAmount(marketConfig.getHomeLevelSecondMaxAmount());
            config.setHomeLevelFirstOddsRate(marketConfig.getHomeLevelFirstOddsRate());
            config.setMarketType(MarketKindEnum.Europe.getValue());
            config.setMarketIndex(config.getMarketIndex());
            return config;
        }

        if (RcsConstant.FOOTBALL_X_EU_PLAYS.contains(config.getPlayId().intValue()) ||
                RcsConstant.FOOTBALL_X_MY_PLAYS.contains(config.getPlayId().intValue()) ||
                RcsConstant.BASKETBALL_X_MY_PLAYS.contains(config.getPlayId().intValue()) ||
                RcsConstant.BASKETBALL_X_EU_PLAYS.contains(config.getPlayId().intValue()) ||
                RcsConstant.OTHER_CAN_TRADE_SPORT.contains(config.getSportId().intValue())){
            RcsMatchMarketConfig marketConfig = rcsMatchMarketConfigMapper.queryMatchMarketConfigSub(config);
            marketConfig.setSubPlayId(config.getSubPlayId());
            // 暂停设置暂停margin
            setTimeOutMargin(config, marketConfig);
            return marketConfig;
        }
        List<RcsMatchMarketConfig> list = rcsMatchMarketConfigMapper.queryMaxBetAmount(config);
        if(CollectionUtils.isEmpty(list)){
            log.error("::{}::,联赛操盘设置，数据库不存在该联赛或者该赛事配置！config:{}",config.getMarketId(),config);
            return null;
        }
        if(list.size()==1){
            RcsMatchMarketConfig marketConfig = list.get(0);
            // 暂停设置暂停margin
           setTimeOutMargin(config, marketConfig);
           return marketConfig;

        }else {
            log.warn("::{}::,配置记录不是一条{},list={}", config.getMarketId(),JSONObject.toJSONString(config),JSONObject.toJSONString(list));
        }
        return null;
    }

    private void setTimeOutMargin(RcsMatchMarketConfig config, RcsMatchMarketConfig marketConfig) {
        String redisKey = String.format("rcs:task:match:event:%s", config.getMatchId());
        String eventCode = redisClient.get(redisKey);
        log.info("::{}::,赛事={}，事件={}",config.getMarketId(),config.getMatchId(),eventCode);
        if (StringUtils.isNotBlank(eventCode) &&
                TIMEOUT.equalsIgnoreCase(eventCode) &&
                (!ObjectUtils.isEmpty(marketConfig.getTimeOutMargin()))){
            marketConfig.setMargin(marketConfig.getTimeOutMargin());
        }
    }

//    @Override
//    public List<RcsMatchMarketConfig> getRcsMatchMarketConfigList(Long matchId, Long marketId) {
//        Map<String, Object> columnMap = new HashMap<>(1);
//        columnMap.put("market_Id", marketId);
//        List<RcsMatchMarketConfig> rcsMatchMarketConfigs = rcsMatchMarketConfigMapper.selectByMap(columnMap);
//        return rcsMatchMarketConfigs;
//    }

    @Override
    public List<RcsMatchMarketConfig> getRcsMatchMarketConfigs(RcsMatchMarketConfig config) {
        String matchMarketConfigsKey = String.format("rcs:match:market:configs:%s:%s", config.getMatchId(),config.getPlayId());
        String matchMarketConfigsValue = redisClient.get(matchMarketConfigsKey);
        List<RcsMatchMarketConfig> rcsMatchMarketConfigList=null;
        if(!StringUtils.isBlank(matchMarketConfigsValue)){
            rcsMatchMarketConfigList= JSONArray.parseArray(matchMarketConfigsValue, RcsMatchMarketConfig.class);
        }
        QueryWrapper<RcsMatchMarketConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RcsMatchMarketConfig::getMatchId, config.getMatchId()).eq(RcsMatchMarketConfig::getPlayId, config.getPlayId());
        queryWrapper.lambda().orderByAsc(RcsMatchMarketConfig ::getMarketIndex);
        rcsMatchMarketConfigList= rcsMatchMarketConfigMapper.selectList(queryWrapper);

        if(!CollectionUtils.isEmpty(rcsMatchMarketConfigList)){
            redisClient.setExpiry(matchMarketConfigsKey, JSONArray.toJSONString(rcsMatchMarketConfigList), 15 * 60L);
        }
        return rcsMatchMarketConfigList;
    }
    /**
     * @Description   //获取水差和
     * @Param [config, list]
     * @Author  sean
     * @Date   2021/1/24
     * @return java.util.List<com.panda.sport.rcs.pojo.dto.odds.MatchMarketPlaceConfig>
     **/
    @Override
    public List<MatchMarketPlaceConfig> getPlaceConfigs(BigDecimal waterValue, List<RcsMatchMarketConfig> list) {
        List<MatchMarketPlaceConfig> placeConfigs = Lists.newArrayList();
        if (CollectionUtils.isEmpty(list)) return placeConfigs;
        waterValue = ObjectUtils.isEmpty(waterValue) ? BigDecimal.ZERO : waterValue;
        for (RcsMatchMarketConfig config : list){
            MatchMarketPlaceConfig placeConfig = new MatchMarketPlaceConfig();
            config.setAwayAutoChangeRate(StringUtils.isEmpty(config.getAwayAutoChangeRate()) ? NumberUtils.DOUBLE_ZERO.toString(): config.getAwayAutoChangeRate());
            placeConfig.setPlaceMarketDiff(waterValue.add(new BigDecimal(config.getAwayAutoChangeRate())));
            placeConfig.setPlaceNum(config.getMarketIndex());
            placeConfigs.add(placeConfig);
        }
        return placeConfigs;
    }
    /**
     * @Description   //修订位置水差
     * @Param [placeConfigs, list]
     * @Author  sean
     * @Date   2021/1/24
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsMatchMarketConfig>
     **/
    @Override
    public List<RcsMatchMarketConfig> limitPlaceWater(RcsMatchMarketConfig matchMarketConfig,BigDecimal waterValue) {
        List<RcsMatchMarketConfig> list = getRcsMatchMarketConfigs(matchMarketConfig);
        List<MatchMarketPlaceConfig> placeConfigs = getPlaceConfigs(waterValue, list);
        List<RcsMatchMarketConfig> configs = Lists.newArrayList();
        Map<Integer,RcsMatchMarketConfig> map = list.stream().collect(Collectors.toMap(e -> e.getMarketIndex(), e -> e));
        for (MatchMarketPlaceConfig placeConfig : placeConfigs){
            if (placeConfig.getPlaceMarketDiff().compareTo(new BigDecimal("0.3")) == NumberUtils.INTEGER_ONE ||
                    placeConfig.getPlaceMarketDiff().compareTo(new BigDecimal("-0.3")) == NumberUtils.INTEGER_MINUS_ONE){
                RcsMatchMarketConfig config = map.get(placeConfig.getPlaceNum());
                if (placeConfig.getPlaceMarketDiff().doubleValue() > NumberUtils.DOUBLE_ZERO){
                    config.setAwayAutoChangeRate(new BigDecimal(config.getAwayAutoChangeRate()).add(new BigDecimal("0.3")).subtract(placeConfig.getPlaceMarketDiff()).toString());
                    placeConfig.setPlaceMarketDiff(new BigDecimal("0.3"));
                }else {
                    config.setAwayAutoChangeRate(new BigDecimal(config.getAwayAutoChangeRate()).add(new BigDecimal("-0.3")).subtract(placeConfig.getPlaceMarketDiff()).toString());
                    placeConfig.setPlaceMarketDiff(new BigDecimal("-0.3"));
                }
                configs.add(config);
            }
        }
        if (!CollectionUtils.isEmpty(configs)){
            rcsMatchMarketConfigMapper.updateMatchMarketWaters(list);
        }
        log.info("玩法水差引起修订位置水差={}",JSONObject.toJSONString(configs));
        return configs;
    }
}
