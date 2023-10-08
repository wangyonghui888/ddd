package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.rcs.pojo.MerchantsSinglePercentage;

/**
 * <p>
 * 商户单场限额监控表 服务类
 * </p>
 *
 * @author lithan
 * @since 2021-11-24
 */
public interface IMerchantsSinglePercentageService extends IService<MerchantsSinglePercentage> {

    //计算
    void cacle(OrderBean orderBean);


    //存入
    void add(String key,MerchantsSinglePercentage value);
}
