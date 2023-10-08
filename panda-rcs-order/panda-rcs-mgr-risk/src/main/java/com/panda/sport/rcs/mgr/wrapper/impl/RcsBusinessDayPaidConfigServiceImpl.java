package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsBusinessDayPaidConfigMapper;
import com.panda.sport.rcs.pojo.RcsBusinessDayPaidConfig;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessDayPaidConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商户单日最大赔付 服务实现类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
@Service
public class RcsBusinessDayPaidConfigServiceImpl extends ServiceImpl<RcsBusinessDayPaidConfigMapper, RcsBusinessDayPaidConfig> implements RcsBusinessDayPaidConfigService {

    @Autowired
    RcsBusinessDayPaidConfigMapper rcsBusinessDayPaidConfigMapper;

    @Override
    public List<RcsBusinessDayPaidConfig> queryBusDayConifgs() {
        return rcsBusinessDayPaidConfigMapper.queryBusDayConifgs();
    }

    @Override
    public void updateRcsBusinessDayPaidConfig(RcsBusinessDayPaidConfig rcsBusinessDayPaidConfig) {
        rcsBusinessDayPaidConfigMapper.updateRcsBusinessDayPaidConfig(rcsBusinessDayPaidConfig);
    }

    @Override
    public RcsBusinessDayPaidConfig getDayPaid(Long businessId) {
        return rcsBusinessDayPaidConfigMapper.selectOneDayPaid(businessId);
    }


}
