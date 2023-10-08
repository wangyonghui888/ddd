package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.facory.BeanFactorys;
import com.panda.sport.rcs.data.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.data.service.RcsTradeConfigService;
import com.panda.sport.rcs.enums.TradeLevelEnum;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author :  wealth
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.data.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-03-05 16:56
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsTradeConfigServiceImpl  extends ServiceImpl<RcsTradeConfigMapper, RcsTradeConfig> implements RcsTradeConfigService {

    @Override
    public RcsTradeConfig getMatchStatusConfig(Long matchId) {
        LambdaQueryWrapper<RcsTradeConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsTradeConfig::getMatchId, matchId.toString())
                .eq(RcsTradeConfig::getTraderLevel, TradeLevelEnum.MATCH.getLevel())
                .eq(RcsTradeConfig::getTargerData, matchId.toString())
                .isNotNull(RcsTradeConfig::getStatus)
                .orderByDesc(RcsTradeConfig::getId)
                .last("LIMIT 1");
        RcsTradeConfig config = this.getOne(wrapper);
        return config != null ? config : BeanFactorys.defaultMatchStatus();
    }

    @Override
    public Integer getMatchStatus(Long matchId) {
        return getMatchStatusConfig(matchId).getStatus();
    }
}
