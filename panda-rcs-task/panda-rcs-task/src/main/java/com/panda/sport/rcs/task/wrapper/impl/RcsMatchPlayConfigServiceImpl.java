package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.statistics.RcsMatchPlayConfigMapper;
import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;
import com.panda.sport.rcs.task.wrapper.RcsMatchPlayConfigService;
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
    public RcsMatchPlayConfig selectRcsMatchPlayConfig(Long matchId, Integer playPhase) {
        QueryWrapper<RcsMatchPlayConfig> wrapper = new QueryWrapper();
        wrapper.lambda().eq(RcsMatchPlayConfig::getMatchId,matchId);
        wrapper.lambda().eq(RcsMatchPlayConfig::getPlayId,playPhase);
        return rcsMatchPlayConfigMapper.selectOne(wrapper);
    }
}
