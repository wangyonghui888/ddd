package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsMatchConfigMapper;
import com.panda.sport.rcs.pojo.RcsMatchConfig;
import com.panda.sport.rcs.mgr.wrapper.RcsMatchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2019-11-08 20:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
public class RcsMatchConfigServiceImpl extends ServiceImpl<RcsMatchConfigMapper, RcsMatchConfig> implements RcsMatchConfigService {
    @Autowired
    RcsMatchConfigMapper rcsMatchConfigMapper;
    @Override
    public RcsMatchConfig selectMatchConfig(Long matchId) {
        QueryWrapper<RcsMatchConfig> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsMatchConfig::getMatchId, matchId);
        return getOne(wrapper);
    }
}
