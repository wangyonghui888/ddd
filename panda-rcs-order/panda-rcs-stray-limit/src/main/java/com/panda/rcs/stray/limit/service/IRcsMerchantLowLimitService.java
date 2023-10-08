package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLowLimit;

import java.util.List;

/**
 * <p>
 * 单日额度用完最低可投注金额配置 服务类
 * </p>
 *
 * @author joey
 * @since 2022-04-02
 */
public interface IRcsMerchantLowLimitService extends IService<RcsMerchantLowLimit> {

    boolean updateById(RcsMerchantLowLimit rcsMerchantLowLimit);

    List<RcsMerchantLowLimit> queryAll();

    RcsMerchantLowLimit queryByStrayType(Integer strayType);
}
