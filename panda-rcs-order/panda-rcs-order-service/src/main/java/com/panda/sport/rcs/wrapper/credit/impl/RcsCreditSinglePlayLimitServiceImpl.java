package com.panda.sport.rcs.wrapper.credit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;
import com.panda.sport.rcs.mapper.credit.RcsCreditSinglePlayLimitMapper;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayLimitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法限额
 * @Author : Paca
 * @Date : 2021-04-30 19:25
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsCreditSinglePlayLimitServiceImpl extends ServiceImpl<RcsCreditSinglePlayLimitMapper, RcsCreditSinglePlayLimit> implements RcsCreditSinglePlayLimitService {

    @Override
    public int batchInsertOrUpdate(List<RcsCreditSinglePlayLimit> list) {
        return this.baseMapper.batchInsertOrUpdate(list);
    }

    @Override
    public List<RcsCreditSinglePlayLimit> querySinglePlayLimit(Long merchantId, String creditId, Long userId) {
        LambdaQueryWrapper<RcsCreditSinglePlayLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSinglePlayLimit::getMerchantId, merchantId)
                .eq(RcsCreditSinglePlayLimit::getCreditId, creditId)
                .eq(RcsCreditSinglePlayLimit::getUserId, userId);
        return this.list(wrapper);
    }

    @Override
    public List<RcsCreditSinglePlayLimit> querySinglePlayLimit(Long merchantId, String creditId, Long userId, Integer sportId, Integer playClassify, String betStage) {
        LambdaQueryWrapper<RcsCreditSinglePlayLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSinglePlayLimit::getMerchantId, merchantId)
                .eq(RcsCreditSinglePlayLimit::getCreditId, creditId)
                .eq(RcsCreditSinglePlayLimit::getUserId, userId)
                .eq(RcsCreditSinglePlayLimit::getSportId, sportId)
                .eq(RcsCreditSinglePlayLimit::getPlayClassify, playClassify)
                .eq(RcsCreditSinglePlayLimit::getBetStage, betStage);
        return this.list(wrapper);
    }
}
