package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskLimit;
import com.panda.rcs.stray.limit.mapper.RcsMerchantHighRiskLimitMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantHighRiskLimitService;
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
 * 高风险单注赔付限额 服务实现类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Service
@Slf4j
public class RcsMerchantHighRiskLimitServiceImpl extends ServiceImpl<RcsMerchantHighRiskLimitMapper, RcsMerchantHighRiskLimit> implements IRcsMerchantHighRiskLimitService {

    @Autowired
    private RcsMerchantHighRiskLimitMapper rcsMerchantHighRiskLimitMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CommonService commonService;

    @Override
    public boolean updateById(RcsMerchantHighRiskLimit rcsMerchantHighRiskLimit) {
        int i = baseMapper.updateById(rcsMerchantHighRiskLimit);
        String formatKey =String.format( SeriesRedisKeyEnums.HIGH_RISK_KEY,rcsMerchantHighRiskLimit.getSeriesType(),rcsMerchantHighRiskLimit.getSportId(), rcsMerchantHighRiskLimit.getTournamentLevel());
        if (i > 0){
            redisUtils.setex(formatKey, JSON.toJSONString(rcsMerchantHighRiskLimit),NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            commonService.initJsonObject(formatKey,JSON.toJSONString(rcsMerchantHighRiskLimit));
        }
        return i > 0;
    }

    @Override
    public List<RcsMerchantHighRiskLimit> queryHighRiskLimit(Integer sportId) {
        LambdaQueryWrapper<RcsMerchantHighRiskLimit> qw = new LambdaQueryWrapper<>();
        qw.eq(RcsMerchantHighRiskLimit::getSportId, sportId);
        qw.orderByAsc(RcsMerchantHighRiskLimit::getSportId);
        return rcsMerchantHighRiskLimitMapper.selectList(qw);
    }


    @Override
    public RcsMerchantHighRiskLimit queryFilterData(Integer sportId, Integer tournamentLevel, Integer seriesType) {
        return queryRedisData(sportId, tournamentLevel, seriesType);
    }


    private RcsMerchantHighRiskLimit queryRedisData(Integer sportId, Integer tournamentLevel, Integer seriesType) {
        long s = System.currentTimeMillis();
        String formatKey =String.format( SeriesRedisKeyEnums.HIGH_RISK_KEY,seriesType,sportId,tournamentLevel);
        String json = RcsLocalCacheUtils.getValue(formatKey, redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        if (StringUtils.isEmpty(json)) {
            //查询数据库
            LambdaQueryWrapper<RcsMerchantHighRiskLimit> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RcsMerchantHighRiskLimit::getSportId, sportId).eq(RcsMerchantHighRiskLimit::getTournamentLevel, tournamentLevel).eq(RcsMerchantHighRiskLimit::getSeriesType, seriesType);
            RcsMerchantHighRiskLimit rcsMerchantHighRiskLimit = rcsMerchantHighRiskLimitMapper.selectOne(wrapper);
            redisUtils.setex(formatKey, JSONObject.toJSONString(rcsMerchantHighRiskLimit),NumberConstant.NUM_SEVEN, TimeUnit.DAYS);
            log.info("query DB RcsMerchantHighRiskLimit total time: {} ms", System.currentTimeMillis() - s);
            return rcsMerchantHighRiskLimit;
        }
        RcsMerchantHighRiskLimit rcsMerchantHighRiskLimit = JSONObject.parseObject(json, RcsMerchantHighRiskLimit.class);
        log.info("query Cache RcsMerchantHighRiskLimit total time: {} ms", System.currentTimeMillis() - s);
        return rcsMerchantHighRiskLimit;
    }
}
