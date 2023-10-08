package com.panda.rcs.stray.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.rcs.stray.limit.entity.constant.NumberConstant;
import com.panda.rcs.stray.limit.entity.enums.SeriesRedisKeyEnums;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSeriesConfig;
import com.panda.rcs.stray.limit.mapper.RcsMerchantSeriesConfigMapper;
import com.panda.rcs.stray.limit.service.IRcsMerchantSeriesConfigService;
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
 * 单日串关赔付总限额及单日派彩总限额 服务实现类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@Service
@Slf4j
public class RcsMerchantSeriesConfigServiceImpl extends ServiceImpl<RcsMerchantSeriesConfigMapper, RcsMerchantSeriesConfig> implements IRcsMerchantSeriesConfigService {

    @Autowired
    private RcsMerchantSeriesConfigMapper rcsMerchantSeriesConfigMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    CommonService commonService;

    @Override
    public boolean updateById(RcsMerchantSeriesConfig rcsMerchantSeriesConfig) {
        int i = baseMapper.updateById(rcsMerchantSeriesConfig);
        if (i > 0) {
            String key = SeriesRedisKeyEnums.SERIES_PAYOUT_AND_PAICAI_TOTAL_AMOUNT;
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantSeriesConfig), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            commonService.initJsonObject(key,JSON.toJSONString(rcsMerchantSeriesConfig));
        }
        return i > 0;
    }

    @Override
    public List<RcsMerchantSeriesConfig> queryAll() {
        return rcsMerchantSeriesConfigMapper.selectList(new LambdaQueryWrapper<RcsMerchantSeriesConfig>().eq(RcsMerchantSeriesConfig::getStatus, YesNoEnum.N).
                orderByDesc(RcsMerchantSeriesConfig::getCreateTime));
    }


    @Override
    public RcsMerchantSeriesConfig queryRedisCache() {
        long s = System.currentTimeMillis();
        String key = SeriesRedisKeyEnums.SERIES_PAYOUT_AND_PAICAI_TOTAL_AMOUNT;
        String jsonData = RcsLocalCacheUtils.getValue(key, redisUtils::get, NumberConstant.REDIS_TIM_OUT);
        if (StringUtils.isEmpty(jsonData)) {
            RcsMerchantSeriesConfig rcsMerchantSeriesConfig = rcsMerchantSeriesConfigMapper.selectOne(new LambdaQueryWrapper<RcsMerchantSeriesConfig>());
            redisUtils.setex(key, JSON.toJSONString(rcsMerchantSeriesConfig), NumberConstant.NUM_FIVES, TimeUnit.DAYS);
            return rcsMerchantSeriesConfig;
        }
        RcsMerchantSeriesConfig rcsMerchantSeriesConfig = JSONObject.parseObject(jsonData, RcsMerchantSeriesConfig.class);
        log.info("query  RcsMerchantSeriesConfig total time: {} ms", System.currentTimeMillis() - s);
        return rcsMerchantSeriesConfig;
    }
}
