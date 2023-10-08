package com.panda.sport.rcs.wrapper.champion.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.champion.RcsChampionTradeConfigMapper;
import com.panda.sport.rcs.pojo.RcsChampionTradeConfig;
import com.panda.sport.rcs.wrapper.champion.RcsChampionTradeConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 冠军玩法限额配置
 * @Author : Paca
 * @Date : 2021-06-15 15:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsChampionTradeConfigServiceImpl extends ServiceImpl<RcsChampionTradeConfigMapper, RcsChampionTradeConfig> implements RcsChampionTradeConfigService {

    @Override
    public List<RcsChampionTradeConfig> getChampionLimit(Long marketId) {
        LambdaQueryWrapper<RcsChampionTradeConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsChampionTradeConfig::getMarketId, marketId);
        return this.list(wrapper);
    }
}
