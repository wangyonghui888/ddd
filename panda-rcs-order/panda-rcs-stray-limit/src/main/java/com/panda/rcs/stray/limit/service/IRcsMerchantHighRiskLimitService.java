package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.enums.SeriesTypeEnum;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskLimit;

import java.util.List;

/**
 * <p>
 * 高风险单注赔付限额 服务类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
public interface IRcsMerchantHighRiskLimitService extends IService<RcsMerchantHighRiskLimit>{

    boolean updateById(RcsMerchantHighRiskLimit rcsMerchantHighRiskLimit);


    List<RcsMerchantHighRiskLimit> queryHighRiskLimit(Integer sportId);


    /**
     * 过滤高风险单注赔付限额配置
     *
     * @param sportId         赛种ID {@link com.panda.sport.rcs.enums.SportIdEnum}
     * @param tournamentLevel 联赛级别
     * @param seriesType      串关赔付类型  {@link SeriesTypeEnum}
     */
    RcsMerchantHighRiskLimit queryFilterData(Integer sportId, Integer tournamentLevel, Integer seriesType);


}
