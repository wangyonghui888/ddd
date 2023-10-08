package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSeriesConfig;

import java.util.List;

/**
 * <p>
 * 单日串关赔付总限额及单日派彩总限额 服务类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
public interface IRcsMerchantSeriesConfigService extends IService<RcsMerchantSeriesConfig> {


    boolean updateById(RcsMerchantSeriesConfig rcsMerchantSeriesConfig);

    /**
     * 查询 单日串关赔付总限额 以及 单日派彩总限额
     *
     * @return
     */
    RcsMerchantSeriesConfig queryRedisCache();

    List<RcsMerchantSeriesConfig> queryAll();
}
