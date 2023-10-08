package com.panda.sport.rcs.wrapper.credit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;
import com.panda.sport.rcs.mapper.credit.RcsCreditSingleMatchLimitMapper;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSingleMatchLimitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网单场赛事限额
 * @Author : Paca
 * @Date : 2021-04-30 19:26
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsCreditSingleMatchLimitServiceImpl extends ServiceImpl<RcsCreditSingleMatchLimitMapper, RcsCreditSingleMatchLimit> implements RcsCreditSingleMatchLimitService {

    @Override
    public int batchInsertOrUpdate(List<RcsCreditSingleMatchLimit> list) {
        return this.baseMapper.batchInsertOrUpdate(list);
    }

    @Override
    public List<RcsCreditSingleMatchLimit> querySingleMatchLimit(Long merchantId, String creditId) {
        LambdaQueryWrapper<RcsCreditSingleMatchLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSingleMatchLimit::getMerchantId, merchantId)
                .eq(RcsCreditSingleMatchLimit::getCreditId, creditId);
        return this.list(wrapper);
    }

    @Override
    public List<RcsCreditSingleMatchLimit> querySingleMatchLimit(Long merchantId, String creditId, Integer sportId) {
        LambdaQueryWrapper<RcsCreditSingleMatchLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSingleMatchLimit::getMerchantId, merchantId)
                .eq(RcsCreditSingleMatchLimit::getCreditId, creditId)
                .eq(RcsCreditSingleMatchLimit::getSportId, sportId);
        return this.list(wrapper);
    }
}
