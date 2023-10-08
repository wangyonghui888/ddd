package com.panda.sport.rcs.mgr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.statistics.RcsMatchPlayConfigMapper;
import com.panda.sport.rcs.mgr.predict.service.RcsMatchPlayConfigService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName MatchPeriodServiceImpl
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/11/19 
**/
@Service
public class RcsMatchPlayConfigServiceImpl extends ServiceImpl<RcsMatchPlayConfigMapper, RcsMatchPlayConfig> implements RcsMatchPlayConfigService {

    @Resource
    private RcsMatchPlayConfigMapper rcsMatchPlayConfigMapper;

    @Override
    public RcsMatchPlayConfig selectRcsMatchPlayConfig(RcsMatchMarketConfig config) {
        QueryWrapper<RcsMatchPlayConfig> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RcsMatchPlayConfig::getMatchId,config.getMatchId());
        wrapper.lambda().eq(RcsMatchPlayConfig::getPlayId,config.getPlayId());
        return rcsMatchPlayConfigMapper.selectOne(wrapper);
    }

    @Override
    public void insertOrUpdateMarketHeadGap(RcsMatchMarketConfig config) {
        rcsMatchPlayConfigMapper.insertOrUpdateMarketHeadGap(config);
    }
//
//    @Override
//    public void inserOrUpdateList(Long matchId, List<Integer> playId, Integer status, Integer dataSource) {
//        rcsMatchPlayConfigMapper.inserOrUpdateList(matchId, playId, status, dataSource);
//
//    }
}
