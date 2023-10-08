package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSportLimit;

import java.util.List;

/**
 * <p>
 * 单日串关赛种赔付限额及派彩限额 服务类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
public interface IRcsMerchantSportLimitService extends IService<RcsMerchantSportLimit> {

    boolean updateById(RcsMerchantSportLimit rcsMerchantSportLimit);

    RcsMerchantSportLimit queryBySportId(Integer sportId);

    List<RcsMerchantSportLimit> queryAll();
}
