package com.panda.sport.rcs.mgr.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mgr.predict.service.RcsMatchPlayConfigService;
import com.panda.sport.rcs.mgr.service.MarketStatusService;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsCalcApi;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsPublicMethodApi;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.mgr.wrapper.MarketOddsChangeCalculationService;
import com.panda.sport.rcs.mgr.wrapper.MatchStatisticsInfoService;
import com.panda.sport.rcs.mgr.wrapper.impl.MarketViewServiceImpl;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;


/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.service.impl
 * @Description :  赔率计算服务实现类
 * @Date: 2019-10-28 15:26
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class MarketOddsChangeCalculationServiceImpl implements MarketOddsChangeCalculationService {
    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
    @Autowired
    IRcsMatchMarketConfigService rcsMatchMarketConfigService;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchStatisticsInfoService matchStatisticsInfoService;
    @Autowired
    MarketViewServiceImpl marketViewService;
    @Autowired
    MarketStatusService marketStatusService;
    @Autowired
    RcsMatchPlayConfigService rcsMatchPlayConfigService;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Autowired
    private OddsPublicMethodApi oddsPublicMethodApi;

    @Override
    public Boolean calculationOddsByOverLoadTrigger(RcsMatchMarketConfig config,ThreewayOverLoadTriggerItem overLoadTriggerItem) {
        try {
            config.setMarketId(overLoadTriggerItem.getMarketId());
        	log.info("::{}::,calculationOddsByOverLoadTrigger赔率变更计算入参：{},config={}", config.getMarketId(),JSONObject.toJSONString(overLoadTriggerItem),JSONObject.toJSONString(config));
        	//判断必要参数是否齐全
            Boolean object = checkForNull(overLoadTriggerItem);
            if (!object) {
                log.info("::{}::,缺少必要参数：{}",config.getMarketId(), JSONObject.toJSONString(overLoadTriggerItem));
                return Boolean.FALSE;
            }

            if (RcsConstant.OTHER_BALL.contains(config.getSportId()) || SportIdEnum.isFootball(config.getSportId())){
                setMarketWaterOrMarginConfig(overLoadTriggerItem);
            }

        	OddsCalcApi api = OddsCalcApi.getInstall(overLoadTriggerItem.getDataSource().intValue());
        	if(overLoadTriggerItem.getClass().equals(TwowayDoubleOverLoadTriggerItem.class)) {
                TwowayDoubleOverLoadTriggerItem twoWayDouble = (TwowayDoubleOverLoadTriggerItem) overLoadTriggerItem;
                twoWayDouble.setHomeLevelFirstOddsRate(config.getHomeLevelFirstOddsRate());
                twoWayDouble.setHomeLevelSecondOddsRate(config.getHomeLevelSecondOddsRate());
                twoWayDouble.setAwayLevelFirstOddsRate(config.getAwayLevelFirstOddsRate());
                twoWayDouble.setAwayLevelSecondOddsRate(config.getAwayLevelSecondOddsRate());
                //计算水差(更新水差发送MatchOddsConfig trade RCS_TRADE_MATCH_ODDS_CONFIG)
        		api.waterCalc(config, twoWayDouble);
        	}
        	if(overLoadTriggerItem.getClass().equals(ThreewayOverLoadTriggerItem.class)) {
        		api.maginCalc(config, overLoadTriggerItem);
        	}
        	//足球下一个账务日以后的早盘触发跳分需要封盘
        	if(config.getSportId() == 1 && 3 != config.getMatchType()) {
                log.info("足球下一个账务日以后的早盘触发跳分需要封盘:{}",config.getMatchType());
        		oddsPublicMethodApi.sendMsgAndCloseStatus(overLoadTriggerItem,config);
        	}
        }catch (Exception e){
            log.error("::{}::赔率变更计算异常：{}",overLoadTriggerItem.getMarketId(),e.getMessage(),e);
        }
        log.info("赔率计算完成::{}::,",overLoadTriggerItem.getMarketId());
        return Boolean.TRUE;
    }
    /**
     * @Description   //获取足球盘口配置
     * @Param [overLoadTriggerItem]
     * @Author  sean
     * @Date   2021/1/22
     * @return com.panda.sport.rcs.pojo.RcsMatchMarketConfig
     **/
    private void setMarketWaterOrMarginConfig(ThreewayOverLoadTriggerItem overLoadTriggerItem) {
        RcsMatchMarketConfig marketConfig = rcsMatchMarketConfigMapper.getMarketConfig(overLoadTriggerItem);
        if (!ObjectUtils.isEmpty(marketConfig)){
            if (StringUtils.isNotBlank(marketConfig.getAwayAutoChangeRate())){
                overLoadTriggerItem.setAwayAutoChangeRate(new BigDecimal(marketConfig.getAwayAutoChangeRate()).doubleValue());
            }
            if (!ObjectUtils.isEmpty(marketConfig.getAwayMargin())){
                overLoadTriggerItem.setAwayMargin(marketConfig.getAwayMargin());
            }
            if (!ObjectUtils.isEmpty(marketConfig.getHomeMargin())){
                overLoadTriggerItem.setHomeMargin(marketConfig.getHomeMargin());
            }
            if (!ObjectUtils.isEmpty(marketConfig.getTieMargin())){
                overLoadTriggerItem.setTieMargin(marketConfig.getTieMargin());
            }
        }
        log.info("::{}::,盘口配置={}",overLoadTriggerItem.getMarketId(),JSONObject.toJSONString(overLoadTriggerItem));
    }

    private Boolean checkForNull(ThreewayOverLoadTriggerItem overLoadTriggerItem) {
        if (ObjectUtils.isEmpty(overLoadTriggerItem)) {
            return Boolean.FALSE;
        }
        if (ObjectUtils.isEmpty(overLoadTriggerItem.getMatchId())) {
            return Boolean.FALSE;
        }
        if (ObjectUtils.isEmpty(overLoadTriggerItem.getMarketId())) {
            return Boolean.FALSE;
        }
        if (ObjectUtils.isEmpty(overLoadTriggerItem.getPlayOptionsId())) {
            return Boolean.FALSE;
        }
        if (ObjectUtils.isEmpty(overLoadTriggerItem.getDataSource())) {
            return Boolean.FALSE;
        }
        if (ObjectUtils.isEmpty(overLoadTriggerItem.getMaxOdds())) {
            return Boolean.FALSE;
        }
        if (ObjectUtils.isEmpty(overLoadTriggerItem.getMinOdds())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


}
