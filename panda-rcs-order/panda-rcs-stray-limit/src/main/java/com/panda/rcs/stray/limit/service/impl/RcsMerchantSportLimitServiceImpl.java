package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSportLimit;
import com.panda.rcs.stray.limit.mapper.RcsMerchantSportLimitMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantSportLimitService;
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
 * 单日串关赛种赔付限额及派彩限额 服务实现类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Service
@Slf4j
public class RcsMerchantSportLimitServiceImpl extends ServiceImpl<RcsMerchantSportLimitMapper, RcsMerchantSportLimit> implements IRcsMerchantSportLimitService {

    @Autowired
    private RcsMerchantSportLimitMapper rcsMerchantSportLimitMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CommonService commonService;

    @Override
    public boolean updateById(RcsMerchantSportLimit rcsMerchantSportLimit) {
        int i = baseMapper.updateById(rcsMerchantSportLimit);
        if (i > 0) {
            String key = String.format(SeriesRedisKeyEnums.MERCHANT_SPORT_LIMIT_TOTAL, rcsMerchantSportLimit.getSportId());
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantSportLimit), NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
            commonService.initJsonObject(key,JSON.toJSONString(rcsMerchantSportLimit));
        }
        return i > 0;
    }


    @Override
    public List<RcsMerchantSportLimit> queryAll() {
        List<RcsMerchantSportLimit> rcsMerchantSportLimitList = rcsMerchantSportLimitMapper.
                selectList(new LambdaQueryWrapper<RcsMerchantSportLimit>().orderByAsc(RcsMerchantSportLimit::getSportId));
        RcsMerchantSportLimit rcsMerchantSportLimit = rcsMerchantSportLimitList.remove(0);
        rcsMerchantSportLimitList.add(rcsMerchantSportLimit);
        return rcsMerchantSportLimitList;
    }

    @Override
    public RcsMerchantSportLimit queryBySportId(Integer sportId) {
        long s = System.currentTimeMillis();
        String key = String.format(SeriesRedisKeyEnums.MERCHANT_SPORT_LIMIT_TOTAL, sportId);
        String jsonData = RcsLocalCacheUtils.getValue(key, redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        if (StringUtils.isEmpty(jsonData)) {
            RcsMerchantSportLimit rcsMerchantSportLimitList = rcsMerchantSportLimitMapper.
                    selectOne(new LambdaQueryWrapper<RcsMerchantSportLimit>().eq(RcsMerchantSportLimit::getSportId, sportId));
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantSportLimitList), NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
            return rcsMerchantSportLimitList;
        }
        RcsMerchantSportLimit rcsMerchantSportLimit = JSON.parseObject(jsonData, RcsMerchantSportLimit.class);
        log.info("query  RcsMerchantSportLimit total time: {} ms", System.currentTimeMillis() - s);
        return rcsMerchantSportLimit;

    }
}
