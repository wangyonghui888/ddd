package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLowLimit;
import com.panda.rcs.stray.limit.mapper.RcsMerchantLowLimitMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantLowLimitService;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 单日额度用完最低可投注金额配置 服务实现类
 * </p>
 *
 * @author joey
 * @since 2022-04-02
 */
@Service
@Slf4j
public class RcsMerchantLowLimitServiceImpl extends ServiceImpl<RcsMerchantLowLimitMapper, RcsMerchantLowLimit> implements IRcsMerchantLowLimitService {


    @Autowired
    private RcsMerchantLowLimitMapper merchantLowLimitMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    CommonService commonService;

    @Override
    public boolean updateById(RcsMerchantLowLimit rcsMerchantLowLimit) {
        int i = baseMapper.updateById(rcsMerchantLowLimit);
        if (i > 0) {
            String key = String.format(SeriesRedisKeyEnums.MERCHANT_LOW_LIMIT_AMOUNT, rcsMerchantLowLimit.getStrayType());
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantLowLimit), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            commonService.initJsonObject(key, JSON.toJSONString(rcsMerchantLowLimit));
        }
        return i > 0;
    }

    @Override
    public List<RcsMerchantLowLimit> queryAll() {
        return merchantLowLimitMapper.selectList(new LambdaQueryWrapper<RcsMerchantLowLimit>().orderByAsc(RcsMerchantLowLimit::getStrayType));
    }

    @Override
    public RcsMerchantLowLimit queryByStrayType(Integer strayType) {
        long s = System.currentTimeMillis();
        String key = String.format(SeriesRedisKeyEnums.MERCHANT_LOW_LIMIT_AMOUNT, strayType);
        String json = RcsLocalCacheUtils.getValue(key, redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        if (StringUtils.isEmpty(json)) {
            RcsMerchantLowLimit rcsMerchantLowLimit = merchantLowLimitMapper.selectOne(new LambdaQueryWrapper<RcsMerchantLowLimit>()
                    .eq(RcsMerchantLowLimit::getStrayType, strayType));
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantLowLimit), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            return rcsMerchantLowLimit;
        }
        RcsMerchantLowLimit rcsMerchantLowLimitList = JSONObject.parseObject(json, RcsMerchantLowLimit.class);
        log.info("query  RcsMerchantLowLimit total time: {} ms", System.currentTimeMillis() - s);
        return rcsMerchantLowLimitList;

    }
}
