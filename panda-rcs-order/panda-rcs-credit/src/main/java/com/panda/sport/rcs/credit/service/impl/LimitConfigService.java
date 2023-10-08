package com.panda.sport.rcs.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.cache.redis.RedisUtils;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.credit.constants.CreditRedisKey;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 限额配置服务
 * @Author : Paca
 * @Date : 2021-06-09 15:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
public class LimitConfigService {

    @Autowired
    private RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取商户或信用代理限额
     *
     * @param businessId 商户ID或信用代理ID
     * @return
     * @author Paca
     */
    public RcsQuotaBusinessLimitResVo getBusinessLimit(final String businessId) {
        // 先从缓存取
        String key = CreditRedisKey.BUSINESS_LIMIT_KEY;
        String value = RcsLocalCacheUtils.getValue(key + businessId,redisUtils::get);
        log.info("Redis获取商户或信用代理限额：key={},field={},value={}", key, businessId, value);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, RcsQuotaBusinessLimitResVo.class);
        }
        LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, businessId);
        RcsQuotaBusinessLimit businessLimit = rcsQuotaBusinessLimitMapper.selectOne(wrapper);
        if (businessLimit != null) {
            // 商户限额，缓存单位分
            businessLimit.setBusinessSingleDayLimit(businessLimit.getBusinessSingleDayLimit() * 100);
            if (businessLimit.getBusinessSingleDaySeriesLimit() != null) {
                businessLimit.setBusinessSingleDaySeriesLimit(businessLimit.getBusinessSingleDaySeriesLimit() * 100);
            }
            if (businessLimit.getUserSingleStrayLimit() != null) {
                businessLimit.setUserSingleStrayLimit(businessLimit.getUserSingleStrayLimit() * 100);
            }
            String jsonString = JSON.toJSONString(businessLimit);
            log.info("数据库获取商户或信用代理限额：" + jsonString);
            RcsQuotaBusinessLimitResVo result = JSON.parseObject(jsonString, RcsQuotaBusinessLimitResVo.class);
            redisUtils.set(key + businessId, JSON.toJSONString(result));
            redisUtils.expire(key + businessId, 30L, TimeUnit.DAYS);
            return result;
        }
        log.warn("数据库获取不到商户或信用代理限额，取默认值");
        return new RcsQuotaBusinessLimitResVo();
    }

    public long businessLimitIncrBy(Long time, String businessId, Long incrValue) {
        if (time == null) {
            time = System.currentTimeMillis();
        }
        String dateExpect = DateUtils.getDateExpect(time);
        String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE, dateExpect, businessId);
        long value = redisUtils.incrBy(key, incrValue);
        redisUtils.expire(key, 30L, TimeUnit.DAYS);
        return value;
    }
}
