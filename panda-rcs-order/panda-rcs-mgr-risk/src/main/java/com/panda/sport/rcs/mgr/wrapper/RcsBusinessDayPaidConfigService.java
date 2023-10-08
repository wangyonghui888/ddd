package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.RcsBusinessDayPaidConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商户单日最大赔付 服务类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public interface RcsBusinessDayPaidConfigService extends IService<RcsBusinessDayPaidConfig> {

    List<RcsBusinessDayPaidConfig> queryBusDayConifgs();

    void  updateRcsBusinessDayPaidConfig(RcsBusinessDayPaidConfig rcsBusinessDayPaidConfig);

    RcsBusinessDayPaidConfig getDayPaid(Long businessId);
}


