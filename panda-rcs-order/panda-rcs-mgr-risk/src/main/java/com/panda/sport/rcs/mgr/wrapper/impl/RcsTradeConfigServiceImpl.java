package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.rcs.enums.TradeTypeEnum;
import com.panda.sport.rcs.enums.TraderLevelEnum;
import com.panda.sport.rcs.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.mgr.utils.BeanFactory;
import com.panda.sport.rcs.mgr.wrapper.IRcsTradeConfigService;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-03-07
 */
@Service
public class RcsTradeConfigServiceImpl extends ServiceImpl<RcsTradeConfigMapper, RcsTradeConfig> implements IRcsTradeConfigService {

    public RcsTradeConfig getLatestStatusConfig(Long matchId, TraderLevelEnum tradeLevelEnum, Long targetId) {
        LambdaQueryWrapper<RcsTradeConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsTradeConfig::getMatchId, matchId.toString())
                .eq(RcsTradeConfig::getTraderLevel, tradeLevelEnum.getLevel())
                .eq(RcsTradeConfig::getTargerData, targetId.toString())
                .isNotNull(RcsTradeConfig::getStatus)
                .orderByDesc(RcsTradeConfig::getId)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    public RcsTradeConfig getMatchStatusConfig(Long matchId) {
        RcsTradeConfig config = getLatestStatusConfig(matchId, TraderLevelEnum.MATCH, matchId);
        return config != null ? config : BeanFactory.defaultMatchStatus();
    }

    @Override
    public Integer getMatchStatus(Long matchId) {
        return getMatchStatusConfig(matchId).getStatus();
    }

    @Override
    public RcsTradeConfig getPlayStatusConfig(Long matchId, Long playId) {
        RcsTradeConfig config = getLatestStatusConfig(matchId, TraderLevelEnum.PLAY, playId);
        return config != null ? config : BeanFactory.defaultCategoryStatus();
    }

    @Override
    public RcsTradeConfig getPlaySetStatusByPlayId(Long matchId, Long playId) {
        RcsTradeConfig config = this.baseMapper.getPlaySetStatusByPlayId(String.valueOf(matchId), playId);
        return config != null ? config : BeanFactory.defaultCategorySetStatus();
    }

    @Override
    public Map<Integer, RcsTradeConfig> getMarketPlaceStatus(Long matchId, Long playId) {
        List<RcsTradeConfig> configList = this.baseMapper.getMarketPlaceStatus(String.valueOf(matchId), Lists.newArrayList(String.valueOf(playId)));
        if (CollectionUtils.isEmpty(configList)) {
            return Maps.newHashMap();
        }
        return configList.stream().collect(Collectors.toMap(config -> Integer.valueOf(config.getTargerData()), Function.identity()));
    }

    @Override
    public Integer getDataSource(Long matchId, Long playId) {
        RcsTradeConfig config = this.baseMapper.getDataSource(matchId.toString(), playId.toString());
        return config != null ? config.getDataSource() : TradeTypeEnum.AUTO.getCode();
    }
}
