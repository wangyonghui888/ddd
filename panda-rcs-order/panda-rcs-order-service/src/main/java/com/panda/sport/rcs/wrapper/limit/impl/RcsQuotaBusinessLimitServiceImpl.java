package com.panda.sport.rcs.wrapper.limit.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额
 * @Author : Paca
 * @Date : 2021-05-06 15:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service("quotaBusinessLimitServiceImpl")
public class RcsQuotaBusinessLimitServiceImpl extends ServiceImpl<RcsQuotaBusinessLimitMapper, RcsQuotaBusinessLimit> implements RcsQuotaBusinessLimitService {

    @Autowired
    private RedisClient redisClient;

    @Override
    public void insertCreditAgentIfAbsent(Long merchantId, String creditAgentId, String creditName, String parentCreditId) {
        LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, creditAgentId);
        RcsQuotaBusinessLimit entity = this.getOne(wrapper);
        if (entity == null) {
            entity = new RcsQuotaBusinessLimit();
            entity.setBusinessId(creditAgentId);
            entity.setBusinessName(creditName);
            entity.setBusinessSingleDayLimitProportion(new BigDecimal("0.1"));
            entity.setBusinessSingleDayLimit(10_000_000L);
            entity.setBusinessSingleDaySeriesLimitProportion(new BigDecimal("0.05"));
            entity.setBusinessSingleDayLimit(5_000_000L);
            entity.setBusinessSingleDayGameProportion(BigDecimal.ONE);
            entity.setUserQuotaRatio(BigDecimal.ONE);
            entity.setCreditBetRatio(BigDecimal.ONE);
            entity.setStatusOfTheDay(1);
            entity.setStatus(1);
            entity.setCreditName(creditName);
            entity.setCreditParentAgentId(parentCreditId);
//            entity.setTagMarketStatus();
//            entity.setTagMarketLevelId();
            entity.setChampionBusinessProportion(BigDecimal.ONE);
            entity.setChampionUserProportion(BigDecimal.ONE);
            this.save(entity);
        } else if (!parentCreditId.equals(entity.getCreditParentAgentId()) || StringUtils.isBlank(entity.getCreditName())) {
            entity.setBusinessId(creditAgentId);
            entity.setBusinessName(creditName);
            entity.setCreditName(creditName);
            entity.setCreditParentAgentId(parentCreditId);
            this.updateById(entity);
        }
    }

    @Override
    public List<RcsQuotaBusinessLimit> listByBusinessIds(List<String> businessIds) {
        return this.baseMapper.listByBusinessIds(businessIds);
    }

    @Override
    public RcsQuotaBusinessLimit getByBusinessId(String businessId) {
        LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, businessId);
        return this.getOne(wrapper);
    }
    @Override
    public RcsQuotaBusinessLimitResVo getByMerchantIdFromRedis(Long merchantId) {
        String key = "rcs:limit:merchants:";
        String value = RcsLocalCacheUtils.getValue(key + merchantId,redisClient::get);
        log.info("查询商户限额：key={},hashKey={},hashValue={}", key, merchantId, value);
        RcsQuotaBusinessLimitResVo resVo;
        if (StringUtils.isNotBlank(value)) {
            resVo = JSON.parseObject(value, RcsQuotaBusinessLimitResVo.class);
        } else {
            RcsQuotaBusinessLimit businessLimit = getByBusinessId(String.valueOf(merchantId));
            if (businessLimit == null) {
                throw new RcsServiceException("未查询到商户限额配置：merchantId=" + merchantId);
            }
            // 金额转成分
            if (businessLimit.getBusinessSingleDayLimit() != null) {
                businessLimit.setBusinessSingleDayLimit(businessLimit.getBusinessSingleDayLimit() * 100);
            }
            if (businessLimit.getBusinessSingleDaySeriesLimit() != null) {
                businessLimit.setBusinessSingleDaySeriesLimit(businessLimit.getBusinessSingleDaySeriesLimit() * 100);
            }
            if (businessLimit.getUserSingleStrayLimit() != null) {
                businessLimit.setUserSingleStrayLimit(businessLimit.getUserSingleStrayLimit() * 100);
            }
            resVo = JSON.parseObject(JSON.toJSONString(businessLimit), RcsQuotaBusinessLimitResVo.class);
        }
        if (resVo.getUserStrayQuotaRatio() == null) {
            resVo.setUserStrayQuotaRatio(resVo.getUserQuotaRatio());
        }
        if (resVo.getBusinessSingleDaySeriesLimit() == null) {
            resVo.setBusinessSingleDaySeriesLimit(resVo.getBusinessSingleDayLimit() / 2);
        }
        if (StringUtils.isBlank(value)) {
            redisClient.set(key + String.valueOf(merchantId), JSON.toJSONString(resVo));
            redisClient.expireKey(key + String.valueOf(merchantId), 30 * 24 * 60 * 60);

        }
        return resVo;
    }
}
