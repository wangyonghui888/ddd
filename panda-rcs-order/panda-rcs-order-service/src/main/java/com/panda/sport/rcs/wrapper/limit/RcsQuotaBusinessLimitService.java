package com.panda.sport.rcs.wrapper.limit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额
 * @Author : Paca
 * @Date : 2021-05-06 15:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaBusinessLimitService extends IService<RcsQuotaBusinessLimit> {

    /**
     * 插入信用代理信息，如果该信用代理不存在
     *
     * @param merchantId
     * @param creditAgentId
     * @param creditName
     * @param parentCreditId
     */
    void insertCreditAgentIfAbsent(Long merchantId, String creditAgentId, String creditName, String parentCreditId);

    List<RcsQuotaBusinessLimit> listByBusinessIds(List<String> businessIds);

    RcsQuotaBusinessLimit getByBusinessId(String businessId);
    /**
     * 从Redis获取商户配置
     *
     * @param merchantId
     * @return
     */
    RcsQuotaBusinessLimitResVo getByMerchantIdFromRedis(Long merchantId);
}
