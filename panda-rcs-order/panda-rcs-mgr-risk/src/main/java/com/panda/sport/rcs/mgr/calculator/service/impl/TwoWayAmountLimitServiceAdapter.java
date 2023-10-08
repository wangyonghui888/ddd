package com.panda.sport.rcs.mgr.calculator.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.Basketball;
import com.panda.sport.rcs.enums.MarketKindEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.mgr.calculator.service.AmountLimitServiceAdapter;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.odds.JumpOddsLuaDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

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
@Slf4j
public abstract  class TwoWayAmountLimitServiceAdapter extends AmountLimitServiceAdapter {



    public TwoWayAmountLimitServiceAdapter(RedisClient redisClient) {
        super(redisClient);
    }

    @Override
    public void sumCurrentLoadValue(OrderItem item, OrderBean orderBean) {
        String userType = getUserType(item.getUid());
        if ("4".equals(userType)) {
            log.warn("::{}::,特殊VIP用户，不统计平衡值，不触发跳水跳盘",orderBean.getOrderNo());
            return;
        }
        BigDecimal percentageOfTagVolume = getPercentageOfTagVolume(item.getUid(), orderBean);
        if (percentageOfTagVolume.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("::{}::,货量百分比为0，不需要统计平衡值，不会跳水跳盘",orderBean.getOrderNo());
            return;
        }
        RcsMatchMarketConfig result = getConfiguredParams(item);
        if (null == result) {
            log.warn("::{}::,{} 下单平衡值计算，没有配置赛事/联赛参数，或者没有配置盘口位置item:{}",orderBean.getOrderNo(), this.getClass(), JSONObject.toJSONString(item));
            return;
        }
        if (null ==result.getBalanceOption() || null == result.getOddChangeRule()) {
            log.warn("::{}::,{} 下单平衡值计算，没有配置自动跳分机制，item:{}", orderBean.getOrderNo(), this.getClass(), JSONObject.toJSONString(item));
            return;
        }
        log.info("::{}::,{} 下单平衡值计算，赛事或者联赛参数，item:{},result:{}",orderBean.getOrderNo(), this.getClass(), JSONObject.toJSONString(item), JSONObject.toJSONString(result));
        long betAmount = new BigDecimal(item.getBetAmount()).divide(new BigDecimal(OrderItem.PlUSTIMES), 2, RoundingMode.HALF_UP).multiply(percentageOfTagVolume).longValue();
        Boolean isHome = isHome(item.getPlayOptions(), item.getMarketValue());
        JumpOddsLuaDto luaDto = new JumpOddsLuaDto();
        luaDto.setJumpType(1);
        luaDto.setBetAmount(betAmount);
        luaDto.setOdds(item.getHandleAfterOddsValue());
        luaDto.setJumpOddsOneLimit(result.getHomeLevelFirstMaxAmount());
        luaDto.setJumpOddsSecondLimit(result.getHomeLevelSecondMaxAmount());
        luaDto.setOddsType(item.getPlayOptions());
        luaDto.setIsHome(isHome ? 1 : 3);
        luaDto.setBalanceOption(result.getBalanceOption());
        luaDto.setOddChangeRule(result.getOddChangeRule());

        luaDto.setIsOpenJumpOdds(YesNoEnum.Y.getValue());
        luaDto.setIsMultipleJumpOdds(YesNoEnum.N.getValue());
        luaDto.setIsOpenJumpMarket(YesNoEnum.N.getValue());
        luaDto.setIsMultipleJumpMarket(YesNoEnum.N.getValue());
        if (!SportIdEnum.isFootball(item.getSportId())) {
            // 篮球-位置ID
            luaDto.setKeySuffix(item.getMatchId() + "_" + item.getPlayId() + "_" + item.getPlaceNum());
            if (RcsConstant.BASKETBALL_X_MY_PLAYS.contains(item.getPlayId()) ||
                    RcsConstant.BASKETBALL_X_EU_PLAYS.contains(item.getPlayId()) ||
                    RcsConstant.OTHER_BALL.contains(item.getSportId()) ||
                    RcsConstant.OTHER_CAN_TRADE_SPORT.contains(item.getSportId())){
                luaDto.setKeySuffix(item.getMatchId() + "_" + item.getPlayId() + "_" + item.getSubPlayId() + "_" + item.getPlaceNum());
            }
            // 跳水开关所有玩法调价窗口均增加
            luaDto.setIsOpenJumpOdds(result.getIsOpenJumpOdds());
            if (MarketKindEnum.Malaysia.getValue().equalsIgnoreCase(result.getMarketType())
                    && result.getOddChangeRule().intValue() == NumberUtils.INTEGER_ONE){
                // 所有玩法均要增加倍数跳水
                luaDto.setIsMultipleJumpOdds(result.getIsMultipleJumpOdds());
            }
            if (Basketball.Main.isHandicapOrTotal(item.getPlayId().longValue())) {
                luaDto.setIsOpenJumpMarket(result.getIsOpenJumpMarket());
                luaDto.setIsMultipleJumpMarket(result.getIsMultipleJumpMarket());
            }
        } else {
            // 足球-盘口ID
            luaDto.setKeySuffix(item.getMarketId().toString());
            result.setIsMultipleJumpOdds(YesNoEnum.N.getValue());
        }
        luaDto.setDateExpect(item.getDateExpect());
        luaDto.setSportId(item.getSportId());
        luaDto.setJumpMarketOneLimit(result.getJumpMarketOneLimit());
        luaDto.setJumpMarketSecondLimit(result.getJumpMarketSecondLimit());
        executeCalcLua(item, result, luaDto);
    }

    public void composeMarketParams(RcsMatchMarketConfig result,ThreewayOverLoadTriggerItem trigger){
        trigger.setDataSource(result.getDataSource().intValue());
        //1.5需求增加抄盘类型马来盘和是否关盘
        trigger.setMarketType(result.getMarketType());
        trigger.setAutoBetStop(result.getAutoBetStop());
        trigger.setMaxOdds(result.getMaxOdds().toString());
        trigger.setMinOdds(result.getMinOdds().toString());
        trigger.setAwayAutoChangeRate(Double.parseDouble(Optional.ofNullable(result.getAwayAutoChangeRate()).orElse("0")));
        trigger.setMargin(result.getMargin());
        log.info("composeMarketParams:{}",JSONObject.toJSONString(trigger));
    }
    /**
     * 计算和调整赔率
     * @param item
     * @param balance
     */
//    public abstract void fireCountAndFix(OrderItem item,long balance,RcsMatchMarketConfig result);


}
