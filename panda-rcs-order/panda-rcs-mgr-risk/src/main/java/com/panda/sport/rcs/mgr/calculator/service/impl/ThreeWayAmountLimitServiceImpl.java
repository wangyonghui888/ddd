package com.panda.sport.rcs.mgr.calculator.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.data.rcs.dto.TwowayDoubleOverLoadTriggerItem;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.mgr.calculator.service.AmountLimitServiceAdapter;
import com.panda.sport.rcs.mgr.wrapper.IRcsMatchMarketConfigService;
import com.panda.sport.rcs.mgr.wrapper.MarketOddsChangeCalculationService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.odds.JumpOddsLuaDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.rpc.calculator.service.impl
 * @Description :操盘限额计算
 * @Date: 2019-10-22 21:36
 */
@Service("threeWayAmountLimitServiceImpl")
@Slf4j
public class ThreeWayAmountLimitServiceImpl extends AmountLimitServiceAdapter {
    @Autowired
    MarketOddsChangeCalculationService marketOddsChangeCalculationService;

    @Autowired
    IRcsMatchMarketConfigService rcsMatchMarketConfigService;

    @Autowired
    public ThreeWayAmountLimitServiceImpl(RedisClient redisClient) {
        super(redisClient);
    }

    
    @Override
    public void triggerChange(RcsMatchMarketConfig result,OrderItem item, JSONArray exeResultArray) {
        ThreewayOverLoadTriggerItem trigger = getTriggerItem(item, ThreewayOverLoadTriggerItem.class);
         trigger.setDataSource(result.getDataSource().intValue());
//        Integer level = exeResultArray.getInteger(2);
//        if (level == 1){
        trigger.setHomeLevelFirstOddsRate(result.getHomeLevelFirstOddsRate());
//        }else {
//            result.setHomeLevelFirstOddsRate(result.getHomeLevelSecondOddsRate());
//        }
        trigger.setFixDirectionEnum(ThreewayOverLoadTriggerItem.FixDirectionEnum.DESC);
        //查询指定投注项ID对应oddsType
        //StandardSportMarketOdds odds = getMarketOdds(item);
        //1.5需求增加抄盘类型马来盘和是否关盘
        trigger.setMatchType(item.getMatchType());
        trigger.setMarketType(result.getMarketType());
        trigger.setAutoBetStop(result.getAutoBetStop());
        trigger.setAwayAutoChangeRate(StringUtils.isNoneBlank(result.getAwayAutoChangeRate()) ? Double.parseDouble(result.getAwayAutoChangeRate()) : 0);
        trigger.setMaxOdds(ObjectUtils.isEmpty(result.getMaxOdds()) ? BaseConstants.MAX_ODDS_VALUE.toString() : result.getMaxOdds().toString());
        trigger.setMinOdds(ObjectUtils.isEmpty(result.getMinOdds()) ? BaseConstants.MIN_ODDS_VALUE.toString() : result.getMinOdds().toString());
        trigger.setHomeMargin(result.getHomeMargin());
        trigger.setAwayMargin(result.getAwayMargin());
        trigger.setTieMargin(result.getTieMargin());
        // 需要传递盘口位置，这里临时处理一下
        trigger.setPlaceNum(item.getPlaceNum());
        trigger.replaceMsg("match_info", item.getMatchInfo()).replaceMsg("play_option_name", item.getPlayName());

        //trigger.setOddsType(odds.getOddsType());
        trigger.setMargin(result.getMargin());
        //触发调价和平衡值清零-延申到trade RCS_TRADE_MATCH_ODDS_CONFIG M/A+模式业务处理
        log.info("triggerForOverLoad入参：{},trigger:{}", JSONObject.toJSONString(result), JSONObject.toJSONString(trigger));
        triggerForOverLoad(result, trigger);

    }
    @Override
    public void sumCurrentLoadValue(OrderItem item, OrderBean orderBean) {
        String userType = getUserType(item.getUid());
        if ("4".equals(userType)) {
            log.warn("::{}::,特殊VIP用户，不统计平衡值，不触发跳水跳盘",item.getOrderNo());
            return;
        }
        BigDecimal percentageOfTagVolume = getPercentageOfTagVolume(item.getUid(), orderBean);
        if (0 == percentageOfTagVolume.compareTo(BigDecimal.ZERO)) {
            log.warn("::{}::,货量百分比为0，不需要统计平衡值，不会跳水跳盘",item.getOrderNo());
            return;
        }
        //获取单项投注额
        RcsMatchMarketConfig result = getConfiguredParams(item);
        if (null == result) {
            log.info("::{}::,{} 下单平衡值计算，没有配置赛事或者联赛参数，item:{}", item.getOrderNo(),this.getClass(), JSONObject.toJSONString(item));
            return;
        }
        long betAmount = new BigDecimal(item.getBetAmount()).divide(new BigDecimal(OrderItem.PlUSTIMES), 2, RoundingMode.HALF_UP).multiply(percentageOfTagVolume).longValue();
        JumpOddsLuaDto luaDto = new JumpOddsLuaDto();
        luaDto.setJumpType(2);
        luaDto.setBetAmount(betAmount);
        luaDto.setOdds(item.getHandleAfterOddsValue());
        luaDto.setJumpOddsOneLimit(result.getHomeLevelFirstMaxAmount());
        luaDto.setJumpOddsSecondLimit(result.getHomeLevelSecondMaxAmount());
        luaDto.setOddsType(item.getPlayOptions());
//        luaDto.setIsHome("");
        luaDto.setBalanceOption(result.getBalanceOption());
        luaDto.setOddChangeRule(result.getOddChangeRule());

        luaDto.setIsOpenJumpOdds(YesNoEnum.Y.getValue());
        luaDto.setIsMultipleJumpOdds(YesNoEnum.N.getValue());
        luaDto.setIsOpenJumpMarket(YesNoEnum.N.getValue());
        luaDto.setIsMultipleJumpMarket(YesNoEnum.N.getValue());
        if (!SportIdEnum.isFootball(item.getSportId())) {
            // 篮球-位置ID RcsConstant.BASKETBALL_X_EU_PLAYS.contains(item.getPlayId())||
            luaDto.setKeySuffix(item.getMatchId() + "_" + item.getPlayId() + "_" + item.getPlaceNum());
            if (RcsConstant.BASKETBALL_X_MY_PLAYS.contains(item.getPlayId()) ||
                    RcsConstant.OTHER_BALL.contains(item.getSportId()) ||
                    RcsConstant.OTHER_CAN_TRADE_SPORT.contains(item.getSportId())){
                luaDto.setKeySuffix(item.getMatchId() + "_" + item.getPlayId() + "_" + item.getSubPlayId() + "_" + item.getPlaceNum());
            }
            // 跳水开关所有玩法调价窗口均增加
            luaDto.setIsOpenJumpOdds(result.getIsOpenJumpOdds());
        } else {
            // 足球-盘口ID
            luaDto.setKeySuffix(item.getMarketId().toString());
        }
        luaDto.setDateExpect(item.getDateExpect());
        luaDto.setSportId(item.getSportId());
        luaDto.setJumpMarketOneLimit(result.getJumpMarketOneLimit());
        luaDto.setJumpMarketSecondLimit(result.getJumpMarketSecondLimit());
        executeCalcLua(item, result, luaDto);
    }

}
