package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;

/**
 * <p>
 * 操盘商户设置 服务类
 * </p>
 *
 * @author lithan
 * @since 2020-12-03
 */
public interface IRcsOperateMerchantsSetService extends IService<RcsOperateMerchantsSet> {

    /**
     * 插入信用代理信息，如果该信用代理不存在
     *
     * @param merchantId
     * @param creditAgentId
     * @param creditName
     * @param parentCreditId
     * @return 是否新的代理信息
     */
    boolean insertCreditAgentIfAbsent(Long merchantId, String creditAgentId, String creditName, String parentCreditId);

    RcsOperateMerchantsSet getByMerchantCode(String merchantCode);
}
