package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLimitCompensation;

import java.util.List;

/**
 * <p>
 * 单日串关类型赔付总限额 服务类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
public interface IRcsMerchantLimitCompensationService extends IService<RcsMerchantLimitCompensation> {


    boolean updateById(RcsMerchantLimitCompensation rcsMerchantLimitCompensation);


    List<RcsMerchantLimitCompensation> queryAll();


    /**
     * 根据串关赔付类型查询 串关赔付总限额
     *
     * @param seriesType
     * @return
     */
    RcsMerchantLimitCompensation queryBySeriesType(Integer seriesType);
}
