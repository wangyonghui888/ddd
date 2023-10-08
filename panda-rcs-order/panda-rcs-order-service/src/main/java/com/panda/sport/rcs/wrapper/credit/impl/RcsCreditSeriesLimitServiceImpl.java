package com.panda.sport.rcs.wrapper.credit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;
import com.panda.sport.rcs.mapper.credit.RcsCreditSeriesLimitMapper;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSeriesLimitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网串关限额
 * @Author : Paca
 * @Date : 2021-04-30 19:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsCreditSeriesLimitServiceImpl extends ServiceImpl<RcsCreditSeriesLimitMapper, RcsCreditSeriesLimit> implements RcsCreditSeriesLimitService {

    @Override
    public int batchInsertOrUpdate(List<RcsCreditSeriesLimit> list) {
        return this.baseMapper.batchInsertOrUpdate(list);
    }

    @Override
    public List<RcsCreditSeriesLimit> querySeriesLimit(Long merchantId, String creditId, Long userId) {
        LambdaQueryWrapper<RcsCreditSeriesLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSeriesLimit::getMerchantId, merchantId)
                .eq(RcsCreditSeriesLimit::getCreditId, creditId)
                .eq(RcsCreditSeriesLimit::getUserId, userId)
                .orderByAsc(RcsCreditSeriesLimit::getSeriesType);
        return this.list(wrapper);
    }

    @Override
    public Long getMerchantIdByCreditId(String creidtId) {
        if (StringUtils.isBlank(creidtId)) {
            return null;
        }
        return baseMapper.getMerchantIdByCreditId(creidtId.trim());
    }
}
