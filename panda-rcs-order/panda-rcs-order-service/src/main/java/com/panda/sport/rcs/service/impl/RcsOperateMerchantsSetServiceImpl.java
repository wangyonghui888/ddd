package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.mapper.RcsOperateMerchantsSetMapper;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.service.IRcsOperateMerchantsSetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 操盘商户设置 服务实现类
 * </p>
 *
 * @author lithan
 * @since 2020-12-03
 */
@Service
public class RcsOperateMerchantsSetServiceImpl extends ServiceImpl<RcsOperateMerchantsSetMapper, RcsOperateMerchantsSet> implements IRcsOperateMerchantsSetService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertCreditAgentIfAbsent(Long merchantId, String creditAgentId, String creditName, String parentCreditId) {
        LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsOperateMerchantsSet::getMerchantsId, creditAgentId);
        RcsOperateMerchantsSet entity = this.getOne(wrapper);
        if (entity == null) {
            entity = new RcsOperateMerchantsSet();
            entity.setMerchantsId(creditAgentId);
            entity.setMerchantsCode(creditName);
            entity.setCreditName(creditName);
            entity.setStatus(YesNoEnum.Y.getValue());
            entity.setValidStatus(YesNoEnum.Y.getValue());
            entity.setLimitType(2);
            entity.setCreditParentId(merchantId);
            this.save(entity);
            return true;
        } else if (!merchantId.equals(entity.getCreditParentId()) || !parentCreditId.equals(entity.getCreditParentAgentId()) || StringUtils.isBlank(entity.getCreditName())) {
            entity.setMerchantsCode(creditName);
            entity.setLimitType(2);
            entity.setCreditParentId(merchantId);
            entity.setCreditParentAgentId(parentCreditId);
            entity.setCreditName(creditName);
            this.updateById(entity);
            return true;
        }
        return false;
    }

    @Override
    public RcsOperateMerchantsSet getByMerchantCode(String merchantCode) {
        LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RcsOperateMerchantsSet::getMerchantsCode, merchantCode);
        return this.getOne(wrapper);
    }
}
