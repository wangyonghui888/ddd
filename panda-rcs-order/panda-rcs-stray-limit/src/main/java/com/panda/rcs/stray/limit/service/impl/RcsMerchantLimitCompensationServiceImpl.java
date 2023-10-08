package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLimitCompensation;
import com.panda.rcs.stray.limit.mapper.RcsMerchantLimitCompensationMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantLimitCompensationService;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 单日串关类型赔付总限额 服务实现类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Service
@Slf4j
public class RcsMerchantLimitCompensationServiceImpl extends ServiceImpl<RcsMerchantLimitCompensationMapper, RcsMerchantLimitCompensation> implements IRcsMerchantLimitCompensationService {

    @Autowired
    private RcsMerchantLimitCompensationMapper rcsMerchantLimitCompensationMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CommonService commonService;

    @Override
    public boolean updateById(RcsMerchantLimitCompensation rcsMerchantLimitCompensation) {
        int i = baseMapper.updateById(rcsMerchantLimitCompensation);
        if (i > 0) {
            String key = String.format(SeriesRedisKeyEnums.MERCHANT_LIMIT_COMPENSATION_KEY, rcsMerchantLimitCompensation.getSeriesType());
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantLimitCompensation), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            commonService.initJsonObject(key,JSON.toJSONString(rcsMerchantLimitCompensation));

        }
        return i > 0;
    }

    @Override
    public List<RcsMerchantLimitCompensation> queryAll() {
        return rcsMerchantLimitCompensationMapper.selectList(new LambdaQueryWrapper<RcsMerchantLimitCompensation>().
                eq(RcsMerchantLimitCompensation::getStatus, YesNoEnum.N.getValue()).orderByAsc(RcsMerchantLimitCompensation::getSeriesType));
    }

    @Override
    public RcsMerchantLimitCompensation queryBySeriesType(Integer seriesType) {
        long s = System.currentTimeMillis();
        String key = String.format(SeriesRedisKeyEnums.MERCHANT_LIMIT_COMPENSATION_KEY, seriesType);
        String json = RcsLocalCacheUtils.getValue(key, redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        if (StringUtils.isEmpty(json)) {
            RcsMerchantLimitCompensation rcsMerchantLimitCompensation=rcsMerchantLimitCompensationMapper.selectOne(new LambdaQueryWrapper<RcsMerchantLimitCompensation>().eq(RcsMerchantLimitCompensation::getSeriesType, seriesType));
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantLimitCompensation), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            return rcsMerchantLimitCompensation;
        }
        RcsMerchantLimitCompensation rcsMerchantLimitCompensation = JSONObject.parseObject(json, RcsMerchantLimitCompensation.class);
        log.info("query  RcsMerchantLimitCompensation total time: {} ms", System.currentTimeMillis() - s);
        return rcsMerchantLimitCompensation;
    }
}
