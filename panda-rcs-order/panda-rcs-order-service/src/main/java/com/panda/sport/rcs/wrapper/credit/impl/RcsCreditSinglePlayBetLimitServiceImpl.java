package com.panda.sport.rcs.wrapper.credit.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.credit.RcsCreditSinglePlayBetLimitMapper;
import com.panda.sport.rcs.pojo.credit.RcsCreditSinglePlayBetLimit;
import com.panda.sport.rcs.wrapper.credit.RcsCreditSinglePlayBetLimitService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法单注限额
 * @Author : Paca
 * @Date : 2021-07-17 18:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsCreditSinglePlayBetLimitServiceImpl extends ServiceImpl<RcsCreditSinglePlayBetLimitMapper, RcsCreditSinglePlayBetLimit> implements RcsCreditSinglePlayBetLimitService {

    @Override
    public int batchInsertOrUpdate(List<RcsCreditSinglePlayBetLimit> list) {
        return this.baseMapper.batchInsertOrUpdate(list);
    }

    @Override
    public List<RcsCreditSinglePlayBetLimit> querySinglePlayBetLimit(Long merchantId, String creditId, Long userId) {
        LambdaQueryWrapper<RcsCreditSinglePlayBetLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSinglePlayBetLimit::getMerchantId, merchantId)
                .eq(RcsCreditSinglePlayBetLimit::getCreditId, creditId)
                .eq(RcsCreditSinglePlayBetLimit::getUserId, userId);
        return this.list(wrapper);
    }

    @Override
    public List<RcsCreditSinglePlayBetLimit> querySinglePlayBetLimit(Long merchantId, String creditId, Long userId, Integer sportId, Integer playClassify, String betStage) {
        LambdaQueryWrapper<RcsCreditSinglePlayBetLimit> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsCreditSinglePlayBetLimit::getMerchantId, merchantId)
                .eq(RcsCreditSinglePlayBetLimit::getCreditId, creditId)
                .eq(RcsCreditSinglePlayBetLimit::getUserId, userId)
                .eq(RcsCreditSinglePlayBetLimit::getSportId, sportId)
                .eq(RcsCreditSinglePlayBetLimit::getPlayClassify, playClassify)
                .eq(RcsCreditSinglePlayBetLimit::getBetStage, betStage);
        return this.list(wrapper);
    }
}
