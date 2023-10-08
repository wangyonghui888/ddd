package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.panda.sport.rcs.mapper.RcsMarketOddsConfigMapper;
import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.panda.sport.rcs.task.wrapper.RcsMarketOddsConfigService;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-11-01 16:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
public class RcsMarketOddsConfigServiceImpl extends ServiceImpl<RcsMarketOddsConfigMapper, RcsMarketOddsConfig> implements RcsMarketOddsConfigService {
    @Autowired
    RcsMarketOddsConfigMapper marketOddsConfigMapper;

    @Override
    public RcsMarketOddsConfig getMarketOdds(RcsMarketOddsConfig rcsMarketOddsConfig) {
        QueryWrapper<RcsMarketOddsConfig> queryWrapper = new QueryWrapper();
        if (rcsMarketOddsConfig.getMatchId() != null) {
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchId, rcsMarketOddsConfig.getMatchId());
        }
        if (rcsMarketOddsConfig.getMarketCategoryId() != null) {
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getMarketCategoryId, rcsMarketOddsConfig.getMarketCategoryId());
        }
        if (rcsMarketOddsConfig.getMatchMarketId() != null) {
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchMarketId, rcsMarketOddsConfig.getMatchMarketId());
        }
        if (rcsMarketOddsConfig.getMarketOddsId() != null) {
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getMarketOddsId, rcsMarketOddsConfig.getMarketOddsId());
        }
        if (rcsMarketOddsConfig.getStandardTournamentId() != null) {
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getStandardTournamentId, rcsMarketOddsConfig.getStandardTournamentId());
        }
        if (rcsMarketOddsConfig.getMatchType() != null) {
            queryWrapper.lambda().eq(RcsMarketOddsConfig::getMatchType, rcsMarketOddsConfig.getMatchType());
        }
        return marketOddsConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public List<OrderDetailStatReportVo> queryMarketStatByMarketId(Long marketId) {

        return marketOddsConfigMapper.queryMarketStatByMarketId(marketId);
    }

}
