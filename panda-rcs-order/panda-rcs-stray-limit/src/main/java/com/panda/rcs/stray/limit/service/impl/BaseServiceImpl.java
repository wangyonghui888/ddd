package com.panda.rcs.stray.limit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.constant.RedisKeyConstant;
import com.panda.rcs.stray.limit.service.BaseService;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BaseServiceImpl implements BaseService {

    private final RedisUtils redisUtils;
    private final RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    public BaseServiceImpl(RedisUtils redisUtils, RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper) {
        this.redisUtils = redisUtils;
        this.rcsQuotaBusinessLimitMapper = rcsQuotaBusinessLimitMapper;
    }

    @Override
    public String queryBusinessSwitch(String businessId) {
        String key = String.format(RedisKeyConstant.getBusinessSwitchKey(), businessId);
        String val = RcsLocalCacheUtils.getValue(key, redisUtils::get);
        if (StringUtils.isNotBlank(val)) {
            return val;
        }
        LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, Long.valueOf(businessId));
        //查询数据库
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = rcsQuotaBusinessLimitMapper.selectOne(wrapper);
        if (Objects.nonNull(rcsQuotaBusinessLimit)) {
            redisUtils.setex(key, String.valueOf(rcsQuotaBusinessLimit.getStraySwitchVal()), NumberConstant.NUM_ONE, TimeUnit.DAYS);
        }
        return String.valueOf(rcsQuotaBusinessLimit.getStraySwitchVal());
    }
}