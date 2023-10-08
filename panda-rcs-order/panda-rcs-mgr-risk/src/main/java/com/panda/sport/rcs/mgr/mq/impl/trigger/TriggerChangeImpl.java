package com.panda.sport.rcs.mgr.mq.impl.trigger;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.constants.PlayIdEnum;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsOperateMerchantsSetMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.MatchTypeEnum;
import com.panda.sport.rcs.mgr.wrapper.AmountLimitService;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 下单触发水位调整
 * @author Administrator
 */
@Component
@Slf4j
public class TriggerChangeImpl {

    @Resource(name = "threeWayAmountLimitServiceImpl")
    AmountLimitService threeWayAmountLimitService;
    @Resource(name = "twoWayDoubleAmountLimitServiceImpl")
    AmountLimitService twoWayDoubleAmountLimitService;

    @Autowired
    RcsOperateMerchantsSetMapper rcsOperateMerchantsSetMapper;

    /**
     * 调赔逻辑
     * 订单进入风控，并且已经验证成功就开始计算
     * 不需要等待确认成功才计算
     */
    public void orderHandle(OrderBean orderBean) {
        //只取单条数据,不要串关的
        if (orderBean.getSeriesType() != 1) {
            log.warn("::{}::,下单触发水位调整：串关数据不处理" , orderBean.getOrderNo());
            return;
        }
        Integer matchType = orderBean.getItems().get(0).getMatchType();
        Integer sportId = orderBean.getItems().get(0).getSportId();
        boolean earlyMarketCheck = SportIdEnum.isFootball(sportId) && MatchTypeEnum.isEarlyMarket(matchType);
        if(earlyMarketCheck){
            log.info("::{}::早盘秒接特殊处理:{}" , orderBean.getOrderNo(), earlyMarketCheck);
        }
        //拒单
        if(orderBean.getValidateResult() != NumberUtils.INTEGER_ONE.intValue() && !earlyMarketCheck){
            log.warn("::{}::,非早盘期望值接收拒单mq，不在计算：{}",orderBean.getOrderNo(),JSONObject.toJSONString(orderBean));
            return;
        }

        RcsOperateMerchantsSet merchantsSet = RcsLocalCacheUtils.getValue("rcsOperateMerchantsSet:"+orderBean.getTenantId(),(k)->{
            LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsOperateMerchantsSet::getMerchantsId, String.valueOf(orderBean.getTenantId()));
            return rcsOperateMerchantsSetMapper.selectOne(wrapper);
        }, 12 * 60 * 60 * 1000L);

        if (merchantsSet == null || merchantsSet.getStatus() == 0 || merchantsSet.getValidStatus() == 0){
            log.info("::{}::不是有效商户不计算" , orderBean.getOrderNo());
            return;
        }
        log.info("::{}::,下单触发水位调整,实体bean{}", orderBean.getOrderNo(),JsonFormatUtils.toJson(orderBean));
        for (OrderItem item : orderBean.getItems()) {
            try {
                //拒单
                if(item.getValidateResult() != NumberUtils.INTEGER_ONE.intValue() && !earlyMarketCheck){
                    log.warn("::{}::,非早盘期望值接收拒单mq，不计算：{}",orderBean.getOrderNo(),JSONObject.toJSONString(orderBean));
                    continue;
                }

                //判断是否触发水位调整
                boolean earlyMarketCheckItem = SportIdEnum.isFootball(item.getMatchType()) && MatchTypeEnum.isEarlyMarket(item.getMatchType());
                if("1".equals(String.valueOf(item.getRiskChannel())) || earlyMarketCheckItem) {
                    if (item.getMatchType() == 3){
                        threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                    }else if (SportIdEnum.isFootball(item.getSportId())){
                        if (RcsConstant.FOOTBALL_EU_PLAYS.contains(item.getPlayId()) ||
                                RcsConstant.FOOTBALL_MOST_PLAYS.contains(item.getPlayId()) ||
                                RcsConstant.FOOTBALL_X_MOST_PLAYS.contains(item.getPlayId()) ||
                                RcsConstant.FOOTBALL_X_EU_PLAYS.contains(item.getPlayId())) {
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        } else if (RcsConstant.FOOTBALL_MY_PLAYS.contains(item.getPlayId()) || RcsConstant.FOOTBALL_X_MY_PLAYS.contains(item.getPlayId())) {
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isBasketball(item.getSportId())){
                        if (RcsConstant.BASKETBALL_EU_PLAYS.contains(item.getPlayId())||RcsConstant.BASKETBALL_AO.contains(item.getPlayId())||RcsConstant.BASKETBALL_TWO_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        } else if (RcsConstant.BASKETBALL_MY_PLAYS.contains(item.getPlayId()) || RcsConstant.BASKETBALL_X_MY_PLAYS.contains(item.getPlayId())) {
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isTennis(item.getSportId())){
                        if (RcsConstant.TINNIS_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.TINNIS_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.TINNIS_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isSnooker(item.getSportId())){
                        if (RcsConstant.SNOOKER_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.SNOOKER_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.SNOOKER_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isPingPong(item.getSportId())){
                        if (RcsConstant.PING_PONG_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.PING_PONG_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.PING_PONG_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isVolleyBall(item.getSportId())){
                        if (RcsConstant.VOLLEYBALL_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.VOLLEYBALL_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.VOLLEYBALL_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isBadminton(item.getSportId())){
                        if (RcsConstant.BADMINTON_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.BADMINTON_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.BADMINTON_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isBaseBall(item.getSportId())){
                        if (RcsConstant.BASEBALL_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.BASEBALL_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.BASEBALL_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isIceHockey(item.getSportId())){
                        if (RcsConstant.ICE_HOCKEY_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.ICE_HOCKEY_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.ICE_HOCKEY_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isAmericanFootball(item.getSportId())){
                        if (RcsConstant.AMERICAN_FOOTBALL_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.AMERICAN_FOOTBALL_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.AMERICAN_FOOTBALL_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isHandball(item.getSportId())){
                        if (RcsConstant.HANDBALL_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.HANDBALL_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.HANDBALL_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isBeachVolleyball(item.getSportId())){
                        if (RcsConstant.BEACH_VOLLEYBALL_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.BEACH_VOLLEYBALL_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.BEACH_VOLLEYBALL_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isRugbyUnion(item.getSportId())){
                        if (RcsConstant.RUGBY_UNION_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.RUGBY_UNION_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.RUGBY_UNION_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isHockey(item.getSportId())){
                        if (RcsConstant.HOCKEY_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.HOCKEY_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.HOCKEY_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isWaterOolo(item.getSportId())){
                        if (RcsConstant.WATER_POLO_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.WATER_POLO_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.WATER_POLO_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }else if (SportIdEnum.isBoxing(item.getSportId())){
                        if (RcsConstant.BOXING_EU_PLAYS.contains(item.getPlayId()) || RcsConstant.BOXING_EU_MOST_PLAYS.contains(item.getPlayId())){
                            threeWayAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }else if (RcsConstant.BOXING_MY_PLAYS.contains(item.getPlayId())){
                            twoWayDoubleAmountLimitService.sumCurrentLoadValue(item, orderBean);
                        }
                    }
                }
            }catch (Exception e) {
                log.error("::{}::调赔逻辑ERROR{}",orderBean.getOrderNo(),e.getMessage(),e);
            }
        }
    }
}
