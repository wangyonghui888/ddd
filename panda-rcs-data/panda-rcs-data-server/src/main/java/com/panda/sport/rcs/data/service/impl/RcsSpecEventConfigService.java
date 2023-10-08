package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsSpecEventConfigMapper;
import com.panda.sport.rcs.data.service.IRcsSpecEventConfigService;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RcsSpecEventConfigService extends ServiceImpl<RcsSpecEventConfigMapper, RcsSpecEventConfig> implements IRcsSpecEventConfigService {

    @Override
    public RcsSpecEventConfig getByMatchId(Long matchId, String eventCode) {
        LambdaQueryWrapper<RcsSpecEventConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsSpecEventConfig::getType, 3);
        wrapper.eq(RcsSpecEventConfig::getTypeVal, matchId);
        wrapper.eq(RcsSpecEventConfig::getEventCode, eventCode);
        return this.getOne(wrapper, false);
    }

    @Override
    public List<RcsSpecEventConfig> getByMatchId(Long matchId) {
        LambdaQueryWrapper<RcsSpecEventConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsSpecEventConfig::getType, 3);
        wrapper.eq(RcsSpecEventConfig::getTypeVal, matchId);
        return this.list(wrapper);
    }
}
