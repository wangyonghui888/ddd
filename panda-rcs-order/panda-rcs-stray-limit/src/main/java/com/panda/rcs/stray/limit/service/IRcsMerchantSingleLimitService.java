package com.panda.rcs.stray.limit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.rcs.stray.limit.entity.vo.HighRiskObjConfig;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSingleLimit;

import java.util.List;

/**
 * <p>
 * 高风险单注赛种投注限制 服务类
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
public interface IRcsMerchantSingleLimitService extends IService<RcsMerchantSingleLimit>{


    boolean updateById(RcsMerchantSingleLimit rcsMerchantSingleLimit);


    /**
     * 高风险单注赛种投注限制中 根据 单注赔付限额类型 过滤
     *
     * @param strayType 单注赔付限额类型
     */
    List<HighRiskObjConfig> querySingleLimit(Integer strayType,Integer sportId);

    List<RcsMerchantSingleLimit> querySingleLimitByList(Integer sportId);


    /**
     * 从数据查询数据
     */
    List<RcsMerchantSingleLimit> queryAll();
}
