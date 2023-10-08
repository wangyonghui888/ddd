package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.HighRiskObjConfig;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSingleLimit;
import com.panda.rcs.stray.limit.mapper.RcsMerchantSingleLimitMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantSingleLimitService;
import com.panda.rcs.stray.limit.utils.RedisUtils;
import com.panda.rcs.stray.limit.wrapper.CommonService;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 高风险单注赛种投注限制 服务实现类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Service
@Slf4j
public class RcsMerchantSingleLimitServiceImpl extends ServiceImpl<RcsMerchantSingleLimitMapper, RcsMerchantSingleLimit> implements IRcsMerchantSingleLimitService {


    @Autowired
    private RcsMerchantSingleLimitMapper rcsMerchantSingleLimitMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CommonService commonService;
    @Override
    public boolean updateById(RcsMerchantSingleLimit rcsMerchantSingleLimit) {
        boolean b = saveOrUpdate(rcsMerchantSingleLimit);
        if (b){
            String key=String.format(SeriesRedisKeyEnums.MERCHANT_HIGH_SINGLE_LIMIT,rcsMerchantSingleLimit.getStrayType(),rcsMerchantSingleLimit.getSportId());
            redisUtils.setex(key,rcsMerchantSingleLimit.getHighRiskConfig(), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            commonService.initJsonObject(key,rcsMerchantSingleLimit.getHighRiskConfig());
        }
        return b;
    }


    @Override
    public List<RcsMerchantSingleLimit> queryAll() {
        return rcsMerchantSingleLimitMapper.selectList(new QueryWrapper<>());
    }

    private List<HighRiskObjConfig> queryRedis(Integer strayType,Integer sportId) {
        long s = System.currentTimeMillis();
        String key=String.format(SeriesRedisKeyEnums.MERCHANT_HIGH_SINGLE_LIMIT,strayType,sportId);
        String jsonData = RcsLocalCacheUtils.getValue(key,redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        List<HighRiskObjConfig> highRiskObjConfigList;
        if (StringUtils.isEmpty(jsonData)) {
            RcsMerchantSingleLimit rcsMerchantSingleLimit = baseMapper.selectOne(new LambdaQueryWrapper<RcsMerchantSingleLimit>()
                            .eq(RcsMerchantSingleLimit::getSportId,sportId)
                    .eq(RcsMerchantSingleLimit::getStrayType, strayType).eq(RcsMerchantSingleLimit::getStatus, YesNoEnum.N.getValue()));
            if(null == rcsMerchantSingleLimit){//如果以球种没有取到值则取其他
                rcsMerchantSingleLimit = baseMapper.selectOne(new LambdaQueryWrapper<RcsMerchantSingleLimit>()
                        .eq(RcsMerchantSingleLimit::getSportId,-1)
                        .eq(RcsMerchantSingleLimit::getStrayType, strayType).eq(RcsMerchantSingleLimit::getStatus, YesNoEnum.N.getValue()));
            }
            redisUtils.setex(key,rcsMerchantSingleLimit.getHighRiskConfig(), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            highRiskObjConfigList = jsonToObj(rcsMerchantSingleLimit.getHighRiskConfig());
        } else {
            highRiskObjConfigList = jsonToObj(jsonData);
        }
        log.info("query  RcsMerchantSingleLimit total time: {} ms", System.currentTimeMillis() - s);
        return highRiskObjConfigList;
    }

    private List<HighRiskObjConfig> jsonToObj(String highRiskConfig) {
        return JSON.parseArray(highRiskConfig, HighRiskObjConfig.class);
    }

    @Override
    public List<RcsMerchantSingleLimit> querySingleLimitByList(Integer sportId) {
        List<RcsMerchantSingleLimit> rcsMerchantSingleLimits = rcsMerchantSingleLimitMapper.selectList(new LambdaQueryWrapper<RcsMerchantSingleLimit>().eq(null != sportId, RcsMerchantSingleLimit::getSportId, sportId)
                .orderByAsc(RcsMerchantSingleLimit::getId));
        if(null != sportId && CollectionUtils.isEmpty(rcsMerchantSingleLimits)){
            rcsMerchantSingleLimits = rcsMerchantSingleLimitMapper.selectList(new LambdaQueryWrapper<RcsMerchantSingleLimit>().isNull(RcsMerchantSingleLimit::getSportId)
                    .orderByAsc(RcsMerchantSingleLimit::getId));
            rcsMerchantSingleLimits.forEach(rcsMerchantSingleLimit -> {rcsMerchantSingleLimit.setId(null);rcsMerchantSingleLimit.setSportId(sportId);});
            saveBatch(rcsMerchantSingleLimits);
        }
        return rcsMerchantSingleLimits;
    }

    @Override
    public List<HighRiskObjConfig> querySingleLimit(Integer strayType,Integer sportId) {
        return queryRedis(strayType,sportId);
    }

}
