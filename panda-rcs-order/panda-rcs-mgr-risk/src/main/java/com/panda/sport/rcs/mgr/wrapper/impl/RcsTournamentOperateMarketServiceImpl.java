package com.panda.sport.rcs.mgr.wrapper.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsTournamentOperateMarketMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsTournamentOperateMarketService;
import com.panda.sport.rcs.pojo.RcsTournamentMarketConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  联赛操盘赔付服务实现类
 * @Date: 2019-10-23 16:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsTournamentOperateMarketServiceImpl extends ServiceImpl<RcsTournamentOperateMarketMapper, RcsTournamentMarketConfig> implements RcsTournamentOperateMarketService {

    @Autowired
    RcsTournamentOperateMarketMapper rcsTournamentOperateMarketMapper;
    @Autowired
    RedisClient redisClient;

    /**
     * @Description 找联赛和赛事的投注限额
     * @Param [order]
     * @Author  max
     * @Date  12:00 2019/12/16
     * @return com.panda.sport.rcs.pojo.RcsTournamentMarketConfig
     **/
    @Override
    public Long queryMatchAndTournamentMaxBetAmount(ExtendBean order) {
        String marketKey = String.format(RedisKeys.RCS_MARKET_MAX_AMOUNT,order.getMatchId(),order.getPlayId(),order.getItemBean().getMarketId());
        String tournamentKey = String.format(RedisKeys.RCS_TOURNAMENT_MAX_AMOUNT,order.getTournamentId(),order.getPlayId());
        String marketAmount = redisClient.get(marketKey);
        String tournamentAmount = redisClient.get(tournamentKey);
        RcsTournamentMarketConfig rcsTournamentMarketConfig;
        Long money = getMaxAmountFromRedis(marketAmount,tournamentAmount);
        if (money.longValue() == 0){

            rcsTournamentMarketConfig = rcsTournamentOperateMarketMapper.queryMatchAndTournamentMaxBetAmount(order);
            log.info("数据库中联赛和赛事最大投注额配置{}", JSONObject.toJSONString(rcsTournamentMarketConfig));
            if(ObjectUtils.isEmpty(rcsTournamentMarketConfig)){
                money = Long.MAX_VALUE / 100 ;
                redisClient.set(marketKey,money);
            }
            else {
                money = ObjectUtils.isEmpty(rcsTournamentMarketConfig.getMaxSingleBetAmount()) ? Long.MAX_VALUE / 100 : rcsTournamentMarketConfig.getMaxSingleBetAmount();
                if(money.intValue() == 0){
                    money = Long.MAX_VALUE / 100 ;
                }
                redisClient.set(marketKey, money);
            }
        }
        //为空转换成最大值
        Double odds = order.getItemBean().getHandleAfterOddsValue();
        if(odds > NumberUtils.INTEGER_TWO){
            money = new BigDecimal(money).divide(new BigDecimal(odds.toString()).subtract(new BigDecimal(NumberUtils.LONG_ONE)),2,BigDecimal.ROUND_DOWN).longValue();
        }

        log.info("联赛和赛事投注最小值:{}",money);
        return money;
    }
    public static Long getMaxAmountFromRedis(String marketAmount,String tournamentAmount){
        if (StringUtils.isEmpty(marketAmount) && StringUtils.isEmpty(tournamentAmount)){
            return 0L;
        }
        if (StringUtils.isNotEmpty(marketAmount)){
            return Long.parseLong(marketAmount);
        }
        return Long.parseLong(tournamentAmount);

    }
    @Override
    public List<RcsTournamentMarketConfig> getRcsTournamentMarketConfig(Map<String, Object> columnMap) {
        return rcsTournamentOperateMarketMapper.selectByMap(columnMap);
    }
}
